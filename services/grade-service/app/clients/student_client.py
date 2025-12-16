"""
REST Client for Student Service
Handles HTTP communication with Student Service
"""
import os
import httpx
import logging
from typing import Optional, Dict, Any
from datetime import datetime

from app.clients.circuit_breaker import CircuitBreaker, CircuitBreakerException
from app.middleware.error import ServiceException

logger = logging.getLogger(__name__)

# Service URLs from environment
STUDENT_SERVICE_URL = os.getenv("STUDENT_SERVICE_URL", "http://localhost:3001")
AUTH_SERVICE_URL = os.getenv("AUTH_SERVICE_URL", "http://localhost:3000")

# Request timeout
REQUEST_TIMEOUT = int(os.getenv("SERVICE_REQUEST_TIMEOUT", "5"))


class StudentServiceClient:
    """
    HTTP client for Student Service with circuit breaker and retry logic
    """
    
    def __init__(self):
        self.base_url = STUDENT_SERVICE_URL
        self.timeout = REQUEST_TIMEOUT
        self.circuit_breaker = CircuitBreaker(
            failure_threshold=5,
            reset_timeout=60.0,
            success_threshold=2,
            name="StudentService"
        )
        self.max_retries = 3
        self.retry_delay = 1.0  # seconds
    
    async def _execute_request(
        self,
        method: str,
        path: str,
        trace_id: Optional[str] = None,
        jwt_token: Optional[str] = None,
        json_data: Optional[Dict] = None,
        params: Optional[Dict] = None
    ) -> Dict[Any, Any]:
        """
        Execute HTTP request with circuit breaker and retry logic
        
        Args:
            method: HTTP method (GET, POST, PUT, DELETE)
            path: API path
            trace_id: Trace ID for distributed tracing
            jwt_token: JWT token for authentication
            json_data: JSON request body
            params: Query parameters
            
        Returns:
            Response data as dictionary
            
        Raises:
            ServiceException: If request fails
        """
        url = f"{self.base_url}{path}"
        headers = {}
        
        if trace_id:
            headers["X-Trace-Id"] = trace_id
        
        if jwt_token:
            headers["Authorization"] = f"Bearer {jwt_token}"
        
        async def make_request():
            async with httpx.AsyncClient(timeout=self.timeout) as client:
                response = await client.request(
                    method=method,
                    url=url,
                    headers=headers,
                    json=json_data,
                    params=params
                )
                
                # Check for error responses
                if response.status_code >= 400:
                    try:
                        error_data = response.json()
                        raise ServiceException(
                            code=error_data.get("code", "SVC_COMMUNICATION_ERROR"),
                            message=error_data.get("message", "Student service request failed"),
                            details=error_data.get("details"),
                            status_code=response.status_code
                        )
                    except ValueError:
                        raise ServiceException(
                            code="SVC_COMMUNICATION_ERROR",
                            message=f"Student service returned {response.status_code}",
                            details=response.text,
                            status_code=response.status_code
                        )
                
                return response.json()
        
        # Retry logic with exponential backoff
        last_exception = None
        for attempt in range(self.max_retries):
            try:
                # Execute with circuit breaker
                result = self.circuit_breaker.execute(
                    lambda: self._run_async(make_request())
                )
                return result
                
            except CircuitBreakerException as e:
                logger.error(f"[{trace_id}] Circuit breaker is open for Student Service")
                raise ServiceException(
                    code="SVC_CIRCUIT_OPEN",
                    message="Student service is temporarily unavailable",
                    details=str(e),
                    status_code=503
                )
                
            except ServiceException as e:
                last_exception = e
                
                # Don't retry client errors (4xx except timeouts)
                if 400 <= e.status_code < 500 and e.status_code != 408:
                    raise
                
                # Retry on server errors and timeouts
                if attempt < self.max_retries - 1:
                    delay = self.retry_delay * (2 ** attempt)
                    logger.warning(
                        f"[{trace_id}] Student service request failed (attempt {attempt + 1}), "
                        f"retrying in {delay}s: {e.message}"
                    )
                    import asyncio
                    await asyncio.sleep(delay)
                    
            except Exception as e:
                last_exception = e
                
                if attempt < self.max_retries - 1:
                    delay = self.retry_delay * (2 ** attempt)
                    logger.warning(
                        f"[{trace_id}] Student service request error (attempt {attempt + 1}), "
                        f"retrying in {delay}s: {str(e)}"
                    )
                    import asyncio
                    await asyncio.sleep(delay)
        
        # All retries failed
        if isinstance(last_exception, ServiceException):
            raise last_exception
        else:
            raise ServiceException(
                code="SVC_COMMUNICATION_ERROR",
                message="Failed to communicate with Student service",
                details=str(last_exception),
                status_code=503
            )
    
    def _run_async(self, coroutine):
        """Helper to run async operations in circuit breaker"""
        import asyncio
        loop = asyncio.new_event_loop()
        try:
            return loop.run_until_complete(coroutine)
        finally:
            loop.close()
    
    async def get_student(self, student_id: str, trace_id: Optional[str] = None) -> Dict[Any, Any]:
        """
        Get student information by ID
        
        Args:
            student_id: Student ID
            trace_id: Trace ID for distributed tracing
            
        Returns:
            Student data
            
        Raises:
            ServiceException: If student not found or request fails
        """
        logger.info(f"[{trace_id}] Fetching student {student_id} from Student Service")
        
        try:
            return await self._execute_request(
                method="GET",
                path=f"/api/v1/students/{student_id}",
                trace_id=trace_id
            )
        except ServiceException as e:
            if e.code == "RES_NOT_FOUND":
                raise ServiceException(
                    code="VAL_FOREIGN_KEY_VIOLATION",
                    message=f"Student with ID {student_id} does not exist",
                    details="Invalid student reference",
                    status_code=400
                )
            raise
    
    async def validate_student(self, student_id: str, trace_id: Optional[str] = None) -> bool:
        """
        Validate that student exists
        
        Args:
            student_id: Student ID to validate
            trace_id: Trace ID for distributed tracing
            
        Returns:
            True if student exists
            
        Raises:
            ServiceException: If student doesn't exist or validation fails
        """
        await self.get_student(student_id, trace_id)
        return True


# Singleton instance
_student_client = None


def get_student_client() -> StudentServiceClient:
    """Get singleton Student Service client"""
    global _student_client
    if _student_client is None:
        _student_client = StudentServiceClient()
    return _student_client

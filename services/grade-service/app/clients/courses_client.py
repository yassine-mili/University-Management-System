"""
SOAP Client for Courses Service
Handles SOAP communication with Courses Service
"""
import os
import logging
from typing import Optional, Dict, Any
from zeep import Client, Settings
from zeep.transports import Transport
from zeep.exceptions import Fault, TransportError
from requests import Session
from requests.adapters import HTTPAdapter
from urllib3.util.retry import Retry

from app.clients.circuit_breaker import CircuitBreaker, CircuitBreakerException
from app.middleware.error import ServiceException

logger = logging.getLogger(__name__)

# Service URL from environment
COURSES_SERVICE_URL = os.getenv("COURSES_SERVICE_URL", "http://localhost:8080")
COURSES_WSDL_URL = f"{COURSES_SERVICE_URL}/soap/courses?wsdl"

# Request timeout
REQUEST_TIMEOUT = int(os.getenv("SERVICE_REQUEST_TIMEOUT", "10"))


class CoursesServiceClient:
    """
    SOAP client for Courses Service with circuit breaker
    """
    
    def __init__(self):
        self.wsdl_url = COURSES_WSDL_URL
        self.timeout = REQUEST_TIMEOUT
        self.circuit_breaker = CircuitBreaker(
            failure_threshold=5,
            reset_timeout=60.0,
            success_threshold=2,
            name="CoursesService"
        )
        
        # Configure HTTP session with retry
        session = Session()
        retry_strategy = Retry(
            total=3,
            backoff_factor=1,
            status_forcelist=[500, 502, 503, 504],
            allowed_methods=["POST"]  # SOAP uses POST
        )
        adapter = HTTPAdapter(max_retries=retry_strategy)
        session.mount("http://", adapter)
        session.mount("https://", adapter)
        
        # Create SOAP client
        transport = Transport(session=session, timeout=self.timeout)
        settings = Settings(strict=False, xml_huge_tree=True)
        
        try:
            self.client = Client(self.wsdl_url, transport=transport, settings=settings)
            logger.info(f"SOAP client initialized for Courses Service: {self.wsdl_url}")
        except Exception as e:
            logger.error(f"Failed to initialize SOAP client: {e}")
            self.client = None
    
    def _create_client_if_needed(self):
        """Lazy initialization of SOAP client"""
        if self.client is None:
            session = Session()
            retry_strategy = Retry(
                total=3,
                backoff_factor=1,
                status_forcelist=[500, 502, 503, 504],
                allowed_methods=["POST"]
            )
            adapter = HTTPAdapter(max_retries=retry_strategy)
            session.mount("http://", adapter)
            session.mount("https://", adapter)
            
            transport = Transport(session=session, timeout=self.timeout)
            settings = Settings(strict=False, xml_huge_tree=True)
            
            self.client = Client(self.wsdl_url, transport=transport, settings=settings)
    
    def _execute_soap_call(
        self,
        operation: str,
        trace_id: Optional[str] = None,
        jwt_token: Optional[str] = None,
        **kwargs
    ) -> Any:
        """
        Execute SOAP operation with circuit breaker
        
        Args:
            operation: SOAP operation name
            trace_id: Trace ID for distributed tracing
            jwt_token: JWT token for authentication
            **kwargs: Operation parameters
            
        Returns:
            Operation result
            
        Raises:
            ServiceException: If operation fails
        """
        self._create_client_if_needed()
        
        def make_call():
            try:
                # Add authentication header if token provided
                if jwt_token:
                    # Note: Zeep doesn't easily support custom HTTP headers
                    # You may need to use the transport's session to set headers
                    pass
                
                # Get service method
                service = self.client.service
                method = getattr(service, operation)
                
                # Execute SOAP call
                result = method(**kwargs)
                return result
                
            except Fault as e:
                # Parse SOAP fault
                logger.error(f"[{trace_id}] SOAP Fault from Courses Service: {e.message}")
                
                # Try to extract error details from fault
                fault_code = e.code
                fault_message = e.message
                
                # Map SOAP faults to service exceptions
                if "AUTH" in fault_message.upper():
                    raise ServiceException(
                        code="AUTH_REQUIRED",
                        message="Authentication required for Courses Service",
                        details=fault_message,
                        status_code=401
                    )
                elif "NOT_FOUND" in fault_message.upper() or "NOT FOUND" in fault_message:
                    raise ServiceException(
                        code="RES_NOT_FOUND",
                        message="Course not found",
                        details=fault_message,
                        status_code=404
                    )
                else:
                    raise ServiceException(
                        code="SVC_SOAP_FAULT",
                        message=f"SOAP fault from Courses Service: {fault_code}",
                        details=fault_message,
                        status_code=500
                    )
                    
            except TransportError as e:
                logger.error(f"[{trace_id}] Transport error calling Courses Service: {e}")
                raise ServiceException(
                    code="SVC_COMMUNICATION_ERROR",
                    message="Failed to communicate with Courses Service",
                    details=str(e),
                    status_code=503
                )
                
            except Exception as e:
                logger.error(f"[{trace_id}] Unexpected error calling Courses Service: {e}")
                raise ServiceException(
                    code="SVC_UNKNOWN_ERROR",
                    message="Unexpected error calling Courses Service",
                    details=str(e),
                    status_code=500
                )
        
        try:
            return self.circuit_breaker.execute(make_call)
            
        except CircuitBreakerException as e:
            logger.error(f"[{trace_id}] Circuit breaker is open for Courses Service")
            raise ServiceException(
                code="SVC_CIRCUIT_OPEN",
                message="Courses service is temporarily unavailable",
                details=str(e),
                status_code=503
            )
    
    def get_course(self, course_id: int, trace_id: Optional[str] = None) -> Dict[Any, Any]:
        """
        Get course information by ID
        
        Args:
            course_id: Course ID
            trace_id: Trace ID for distributed tracing
            
        Returns:
            Course data as dictionary
            
        Raises:
            ServiceException: If course not found or request fails
        """
        logger.info(f"[{trace_id}] Fetching course {course_id} from Courses Service")
        
        result = self._execute_soap_call(
            operation="getCourse",
            trace_id=trace_id,
            courseId=course_id
        )
        
        # Convert SOAP response to dict
        if result:
            return {
                "courseId": result.courseId if hasattr(result, 'courseId') else None,
                "courseCode": result.courseCode if hasattr(result, 'courseCode') else None,
                "courseName": result.courseName if hasattr(result, 'courseName') else None,
                "credits": result.credits if hasattr(result, 'credits') else None,
                "department": result.department if hasattr(result, 'department') else None,
            }
        
        raise ServiceException(
            code="RES_NOT_FOUND",
            message=f"Course with ID {course_id} not found",
            status_code=404
        )
    
    def validate_course(self, course_id: int, trace_id: Optional[str] = None) -> bool:
        """
        Validate that course exists
        
        Args:
            course_id: Course ID to validate
            trace_id: Trace ID for distributed tracing
            
        Returns:
            True if course exists
            
        Raises:
            ServiceException: If course doesn't exist or validation fails
        """
        self.get_course(course_id, trace_id)
        return True
    
    def check_enrollment(
        self,
        student_id: int,
        course_id: int,
        trace_id: Optional[str] = None
    ) -> bool:
        """
        Check if student is enrolled in course
        
        Args:
            student_id: Student ID
            course_id: Course ID
            trace_id: Trace ID for distributed tracing
            
        Returns:
            True if enrolled, False otherwise
            
        Raises:
            ServiceException: If request fails
        """
        logger.info(
            f"[{trace_id}] Checking enrollment for student {student_id} "
            f"in course {course_id}"
        )
        
        try:
            result = self._execute_soap_call(
                operation="checkEnrollment",
                trace_id=trace_id,
                studentId=student_id,
                courseId=course_id
            )
            return bool(result)
            
        except ServiceException as e:
            if e.code == "RES_NOT_FOUND":
                return False
            raise


# Singleton instance
_courses_client = None


def get_courses_client() -> CoursesServiceClient:
    """Get singleton Courses Service client"""
    global _courses_client
    if _courses_client is None:
        _courses_client = CoursesServiceClient()
    return _courses_client

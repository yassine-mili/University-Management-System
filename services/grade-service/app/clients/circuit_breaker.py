"""
Circuit Breaker Pattern Implementation for Python
Provides resilience for service-to-service calls
"""
import time
import logging
from enum import Enum
from typing import Callable, TypeVar, Generic
from threading import Lock

logger = logging.getLogger(__name__)

T = TypeVar('T')


class CircuitState(Enum):
    """Circuit breaker states"""
    CLOSED = "CLOSED"      # Normal operation
    OPEN = "OPEN"          # Failing, reject requests
    HALF_OPEN = "HALF_OPEN"  # Testing if service recovered


class CircuitBreakerException(Exception):
    """Raised when circuit breaker is open"""
    pass


class CircuitBreaker(Generic[T]):
    """
    Circuit breaker implementation
    
    States:
    - CLOSED: Normal operation, requests pass through
    - OPEN: Too many failures, reject requests immediately
    - HALF_OPEN: Test mode, allow limited requests to check recovery
    
    Args:
        failure_threshold: Number of failures before opening circuit (default: 5)
        reset_timeout: Seconds to wait before trying HALF_OPEN (default: 60)
        success_threshold: Successes in HALF_OPEN to close circuit (default: 2)
    """
    
    def __init__(
        self,
        failure_threshold: int = 5,
        reset_timeout: float = 60.0,
        success_threshold: int = 2,
        name: str = "CircuitBreaker"
    ):
        self.failure_threshold = failure_threshold
        self.reset_timeout = reset_timeout
        self.success_threshold = success_threshold
        self.name = name
        
        self.state = CircuitState.CLOSED
        self.failure_count = 0
        self.success_count = 0
        self.last_failure_time = None
        self.lock = Lock()
    
    def execute(self, operation: Callable[[], T]) -> T:
        """
        Execute operation with circuit breaker protection
        
        Args:
            operation: Callable to execute
            
        Returns:
            Result of operation
            
        Raises:
            CircuitBreakerException: If circuit is open
            Exception: Any exception raised by operation
        """
        with self.lock:
            if self.state == CircuitState.OPEN:
                if self._should_attempt_reset():
                    logger.info(f"[{self.name}] Circuit transitioning to HALF_OPEN")
                    self.state = CircuitState.HALF_OPEN
                    self.success_count = 0
                else:
                    raise CircuitBreakerException(
                        f"Circuit breaker [{self.name}] is OPEN. "
                        f"Service unavailable. Retry after {self.reset_timeout}s"
                    )
        
        # Execute operation
        try:
            result = operation()
            self._record_success()
            return result
        except Exception as e:
            self._record_failure()
            raise
    
    def _should_attempt_reset(self) -> bool:
        """Check if enough time has passed to attempt reset"""
        if self.last_failure_time is None:
            return True
        
        elapsed = time.time() - self.last_failure_time
        return elapsed >= self.reset_timeout
    
    def _record_success(self):
        """Record successful operation"""
        with self.lock:
            if self.state == CircuitState.HALF_OPEN:
                self.success_count += 1
                logger.info(
                    f"[{self.name}] Success in HALF_OPEN state "
                    f"({self.success_count}/{self.success_threshold})"
                )
                
                if self.success_count >= self.success_threshold:
                    logger.info(f"[{self.name}] Circuit closing after successful recovery")
                    self.state = CircuitState.CLOSED
                    self.failure_count = 0
                    self.success_count = 0
            elif self.state == CircuitState.CLOSED:
                # Reset failure count on success
                self.failure_count = 0
    
    def _record_failure(self):
        """Record failed operation"""
        with self.lock:
            self.failure_count += 1
            self.last_failure_time = time.time()
            
            if self.state == CircuitState.HALF_OPEN:
                logger.warning(f"[{self.name}] Failure in HALF_OPEN state, reopening circuit")
                self.state = CircuitState.OPEN
                self.success_count = 0
            elif self.failure_count >= self.failure_threshold:
                logger.error(
                    f"[{self.name}] Failure threshold reached ({self.failure_count}), "
                    f"opening circuit"
                )
                self.state = CircuitState.OPEN
    
    def get_state(self) -> CircuitState:
        """Get current circuit state"""
        with self.lock:
            return self.state
    
    def reset(self):
        """Manually reset circuit to CLOSED state"""
        with self.lock:
            logger.info(f"[{self.name}] Circuit manually reset to CLOSED")
            self.state = CircuitState.CLOSED
            self.failure_count = 0
            self.success_count = 0
            self.last_failure_time = None

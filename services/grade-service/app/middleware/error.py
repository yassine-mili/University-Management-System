"""
Error Handling Middleware for Grades Service
Provides unified error response format and exception handling
"""
from fastapi import Request, status
from fastapi.responses import JSONResponse
from fastapi.exceptions import RequestValidationError
from starlette.exceptions import HTTPException as StarletteHTTPException
from sqlalchemy.exc import IntegrityError, OperationalError
from datetime import datetime
import logging
import uuid

logger = logging.getLogger(__name__)


class ServiceException(Exception):
    """Base exception for service errors"""
    def __init__(self, code: str, message: str, details: str = None, status_code: int = 500):
        self.code = code
        self.message = message
        self.details = details
        self.status_code = status_code
        super().__init__(message)


def get_trace_id(request: Request) -> str:
    """Get or generate trace ID for request"""
    return request.headers.get("X-Trace-Id") or str(uuid.uuid4())


async def service_exception_handler(request: Request, exc: ServiceException) -> JSONResponse:
    """Handle ServiceException"""
    trace_id = get_trace_id(request)
    
    error_response = {
        "code": exc.code,
        "message": exc.message,
        "details": exc.details,
        "timestamp": datetime.utcnow().isoformat(),
        "path": str(request.url.path),
        "service": "grades-service",
        "traceId": trace_id
    }
    
    logger.error(f"[{trace_id}] ServiceException: {exc.code} - {exc.message}")
    
    return JSONResponse(
        status_code=exc.status_code,
        content=error_response
    )


async def http_exception_handler(request: Request, exc: StarletteHTTPException) -> JSONResponse:
    """Handle HTTP exceptions"""
    trace_id = get_trace_id(request)
    
    # Check if detail is already formatted
    if isinstance(exc.detail, dict) and "code" in exc.detail:
        error_response = {
            **exc.detail,
            "path": str(request.url.path),
            "traceId": trace_id
        }
    else:
        # Map status code to error code
        error_code_map = {
            400: "VAL_BAD_REQUEST",
            401: "AUTH_REQUIRED",
            403: "AUTH_INSUFFICIENT_PERMISSIONS",
            404: "RES_NOT_FOUND",
            405: "VAL_METHOD_NOT_ALLOWED",
            409: "BIZ_CONFLICT",
            422: "VAL_UNPROCESSABLE_ENTITY",
            429: "SVC_RATE_LIMIT_EXCEEDED",
            500: "SVC_INTERNAL_ERROR",
            503: "SVC_UNAVAILABLE"
        }
        
        error_response = {
            "code": error_code_map.get(exc.status_code, "SVC_UNKNOWN_ERROR"),
            "message": str(exc.detail),
            "timestamp": datetime.utcnow().isoformat(),
            "path": str(request.url.path),
            "service": "grades-service",
            "traceId": trace_id
        }
    
    logger.error(f"[{trace_id}] HTTP {exc.status_code}: {exc.detail}")
    
    return JSONResponse(
        status_code=exc.status_code,
        content=error_response
    )


async def validation_exception_handler(request: Request, exc: RequestValidationError) -> JSONResponse:
    """Handle request validation errors"""
    trace_id = get_trace_id(request)
    
    # Extract validation errors
    validation_errors = []
    for error in exc.errors():
        field = ".".join(str(loc) for loc in error["loc"])
        validation_errors.append({
            "field": field,
            "message": error["msg"],
            "type": error["type"]
        })
    
    error_response = {
        "code": "VAL_VALIDATION_ERROR",
        "message": "Request validation failed",
        "details": {
            "errors": validation_errors
        },
        "timestamp": datetime.utcnow().isoformat(),
        "path": str(request.url.path),
        "service": "grades-service",
        "traceId": trace_id
    }
    
    logger.warning(f"[{trace_id}] Validation error: {validation_errors}")
    
    return JSONResponse(
        status_code=status.HTTP_422_UNPROCESSABLE_ENTITY,
        content=error_response
    )


async def database_exception_handler(request: Request, exc: Exception) -> JSONResponse:
    """Handle database-specific exceptions"""
    trace_id = get_trace_id(request)
    
    if isinstance(exc, IntegrityError):
        # Check for specific constraint violations
        error_msg = str(exc.orig) if hasattr(exc, 'orig') else str(exc)
        
        if "foreign key" in error_msg.lower():
            error_response = {
                "code": "VAL_FOREIGN_KEY_VIOLATION",
                "message": "Referenced resource does not exist",
                "details": error_msg,
                "timestamp": datetime.utcnow().isoformat(),
                "path": str(request.url.path),
                "service": "grades-service",
                "traceId": trace_id
            }
            status_code = status.HTTP_400_BAD_REQUEST
        elif "unique" in error_msg.lower() or "duplicate" in error_msg.lower():
            error_response = {
                "code": "BIZ_DUPLICATE_ENTRY",
                "message": "Resource already exists",
                "details": error_msg,
                "timestamp": datetime.utcnow().isoformat(),
                "path": str(request.url.path),
                "service": "grades-service",
                "traceId": trace_id
            }
            status_code = status.HTTP_409_CONFLICT
        else:
            error_response = {
                "code": "DB_CONSTRAINT_VIOLATION",
                "message": "Database constraint violation",
                "details": error_msg,
                "timestamp": datetime.utcnow().isoformat(),
                "path": str(request.url.path),
                "service": "grades-service",
                "traceId": trace_id
            }
            status_code = status.HTTP_400_BAD_REQUEST
        
        logger.error(f"[{trace_id}] IntegrityError: {error_msg}")
        
    elif isinstance(exc, OperationalError):
        error_response = {
            "code": "DB_CONNECTION_ERROR",
            "message": "Database connection failed",
            "details": str(exc),
            "timestamp": datetime.utcnow().isoformat(),
            "path": str(request.url.path),
            "service": "grades-service",
            "traceId": trace_id
        }
        status_code = status.HTTP_503_SERVICE_UNAVAILABLE
        
        logger.error(f"[{trace_id}] OperationalError: {exc}")
        
    else:
        error_response = {
            "code": "DB_ERROR",
            "message": "Database operation failed",
            "details": str(exc),
            "timestamp": datetime.utcnow().isoformat(),
            "path": str(request.url.path),
            "service": "grades-service",
            "traceId": trace_id
        }
        status_code = status.HTTP_500_INTERNAL_SERVER_ERROR
        
        logger.error(f"[{trace_id}] Database error: {exc}")
    
    return JSONResponse(
        status_code=status_code,
        content=error_response
    )


async def generic_exception_handler(request: Request, exc: Exception) -> JSONResponse:
    """Handle all unhandled exceptions"""
    trace_id = get_trace_id(request)
    
    error_response = {
        "code": "SVC_INTERNAL_ERROR",
        "message": "An unexpected error occurred",
        "details": str(exc) if request.app.debug else "Internal server error",
        "timestamp": datetime.utcnow().isoformat(),
        "path": str(request.url.path),
        "service": "grades-service",
        "traceId": trace_id
    }
    
    logger.exception(f"[{trace_id}] Unhandled exception: {exc}")
    
    return JSONResponse(
        status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
        content=error_response
    )


def register_exception_handlers(app):
    """Register all exception handlers with the app"""
    from fastapi.exceptions import RequestValidationError
    from starlette.exceptions import HTTPException as StarletteHTTPException
    from sqlalchemy.exc import IntegrityError, OperationalError
    
    app.add_exception_handler(ServiceException, service_exception_handler)
    app.add_exception_handler(StarletteHTTPException, http_exception_handler)
    app.add_exception_handler(RequestValidationError, validation_exception_handler)
    app.add_exception_handler(IntegrityError, database_exception_handler)
    app.add_exception_handler(OperationalError, database_exception_handler)
    app.add_exception_handler(Exception, generic_exception_handler)

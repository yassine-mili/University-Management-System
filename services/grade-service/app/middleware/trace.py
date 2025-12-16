"""
Trace ID Middleware for Grades Service
Generates and propagates trace IDs for distributed tracing
"""
from fastapi import Request
from starlette.middleware.base import BaseHTTPMiddleware
import uuid


class TraceMiddleware(BaseHTTPMiddleware):
    """Middleware to add trace ID to requests"""
    
    async def dispatch(self, request: Request, call_next):
        # Get existing trace ID or generate new one
        trace_id = request.headers.get("X-Trace-Id") or str(uuid.uuid4())
        
        # Store trace ID in request state
        request.state.trace_id = trace_id
        
        # Process request
        response = await call_next(request)
        
        # Add trace ID to response headers
        response.headers["X-Trace-Id"] = trace_id
        
        return response

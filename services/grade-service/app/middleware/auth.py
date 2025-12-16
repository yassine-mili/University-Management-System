"""
JWT Authentication Middleware for Grades Service
Validates JWT tokens and extracts user information
"""
import os
import jwt
from typing import Optional, List
from fastapi import Depends, HTTPException, status, Request
from fastapi.security import HTTPBearer, HTTPAuthorizationCredentials
from datetime import datetime

# JWT Configuration
JWT_SECRET = os.getenv("JWT_SECRET", "your-secret-key-change-this-in-production")
JWT_ALGORITHM = "HS256"

# Security scheme
security = HTTPBearer()


class AuthenticationError(HTTPException):
    """Custom authentication exception"""
    def __init__(self, code: str, message: str, details: str = None):
        super().__init__(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail={
                "code": code,
                "message": message,
                "details": details,
                "timestamp": datetime.utcnow().isoformat(),
                "service": "grades-service"
            },
            headers={"WWW-Authenticate": "Bearer"}
        )


class User:
    """User model extracted from JWT token"""
    def __init__(self, user_id: str, username: str, email: str, roles: List[str]):
        self.user_id = user_id
        self.username = username
        self.email = email
        self.roles = roles
    
    def has_role(self, *required_roles: str) -> bool:
        """Check if user has any of the required roles"""
        return any(role in self.roles for role in required_roles)
    
    def has_all_roles(self, *required_roles: str) -> bool:
        """Check if user has all required roles"""
        return all(role in self.roles for role in required_roles)


def decode_token(token: str) -> dict:
    """
    Decode and validate JWT token
    
    Args:
        token: JWT token string
        
    Returns:
        Decoded token claims
        
    Raises:
        AuthenticationError: If token is invalid, expired, or malformed
    """
    try:
        payload = jwt.decode(token, JWT_SECRET, algorithms=[JWT_ALGORITHM])
        return payload
    except jwt.ExpiredSignatureError:
        raise AuthenticationError(
            code="AUTH_TOKEN_EXPIRED",
            message="Authentication token has expired",
            details="Please login again to obtain a new token"
        )
    except jwt.InvalidTokenError as e:
        raise AuthenticationError(
            code="AUTH_TOKEN_INVALID",
            message="Invalid authentication token",
            details=str(e)
        )
    except Exception as e:
        raise AuthenticationError(
            code="AUTH_TOKEN_MALFORMED",
            message="Malformed authentication token",
            details=str(e)
        )


def get_current_user(credentials: HTTPAuthorizationCredentials = Depends(security)) -> User:
    """
    Dependency to get current authenticated user from JWT token
    
    Args:
        credentials: HTTP Bearer token credentials
        
    Returns:
        User object with user information
        
    Raises:
        AuthenticationError: If authentication fails
    """
    token = credentials.credentials
    payload = decode_token(token)
    
    # Extract user information from token
    try:
        user_id = payload.get("userId") or payload.get("user_id") or payload.get("sub")
        username = payload.get("username")
        email = payload.get("email")
        roles = payload.get("roles", [])
        
        if not user_id:
            raise AuthenticationError(
                code="AUTH_TOKEN_INVALID",
                message="Token missing required user information",
                details="userId claim not found in token"
            )
        
        return User(
            user_id=str(user_id),
            username=username or "",
            email=email or "",
            roles=roles if isinstance(roles, list) else []
        )
    except AuthenticationError:
        raise
    except Exception as e:
        raise AuthenticationError(
            code="AUTH_TOKEN_INVALID",
            message="Failed to extract user information from token",
            details=str(e)
        )


def get_optional_user(request: Request) -> Optional[User]:
    """
    Dependency to get current user if token is provided, None otherwise
    
    Args:
        request: FastAPI request object
        
    Returns:
        User object if authenticated, None otherwise
    """
    auth_header = request.headers.get("Authorization")
    if not auth_header or not auth_header.startswith("Bearer "):
        return None
    
    try:
        token = auth_header.split(" ")[1]
        payload = decode_token(token)
        
        user_id = payload.get("userId") or payload.get("user_id") or payload.get("sub")
        username = payload.get("username")
        email = payload.get("email")
        roles = payload.get("roles", [])
        
        if not user_id:
            return None
        
        return User(
            user_id=str(user_id),
            username=username or "",
            email=email or "",
            roles=roles if isinstance(roles, list) else []
        )
    except Exception:
        return None


class RoleChecker:
    """Dependency class to check user roles"""
    
    def __init__(self, allowed_roles: List[str]):
        self.allowed_roles = allowed_roles
    
    def __call__(self, user: User = Depends(get_current_user)) -> User:
        """
        Check if user has required roles
        
        Args:
            user: Current authenticated user
            
        Returns:
            User object if authorized
            
        Raises:
            HTTPException: If user lacks required roles
        """
        if not user.has_role(*self.allowed_roles):
            raise HTTPException(
                status_code=status.HTTP_403_FORBIDDEN,
                detail={
                    "code": "AUTH_INSUFFICIENT_PERMISSIONS",
                    "message": "User does not have required permissions",
                    "details": f"Required roles: {', '.join(self.allowed_roles)}",
                    "timestamp": datetime.utcnow().isoformat(),
                    "service": "grades-service"
                }
            )
        return user


# Common role checkers
require_admin = RoleChecker(["ADMIN"])
require_staff = RoleChecker(["ADMIN", "STAFF"])
require_any_role = RoleChecker(["ADMIN", "STAFF", "STUDENT"])

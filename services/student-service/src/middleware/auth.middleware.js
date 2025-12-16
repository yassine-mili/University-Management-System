// Fichier : student-service/src/middleware/auth.middleware.js

const jwt = require("jsonwebtoken");

const DEFAULT_JWT_SECRET =
  "UniversityManagementSystemSecretKey2024VeryLongSecretForHS512Algorithm";

/**
 * Resolve the JWT secret so it matches how the auth-service signs tokens.
 * The Spring Boot service base64-decodes the configured secret before signing,
 * so we mirror that behaviour here to avoid signature mismatches.
 */
const getJwtSecret = () => {
  const configuredSecret = process.env.JWT_SECRET || DEFAULT_JWT_SECRET;
  const decodedSecret = Buffer.from(configuredSecret, "base64");
  return decodedSecret.length > 0 ? decodedSecret : configuredSecret;
};

/**
 * JWT Authentication Middleware
 * Validates JWT tokens for protected routes
 */
const authenticateToken = (req, res, next) => {
  try {
    // Get token from Authorization header
    const authHeader = req.headers["authorization"];
    const token = authHeader && authHeader.split(" ")[1]; // Bearer TOKEN

    if (!token) {
      return res.status(401).json({
        error: {
          code: "AUTH_TOKEN_MISSING",
          message: "No authentication token provided",
          details:
            "Please provide a valid JWT token in the Authorization header",
          timestamp: new Date().toISOString(),
          path: req.path,
          service: "student-service",
          traceId: req.headers["x-trace-id"] || generateTraceId(),
        },
      });
    }

    // Get JWT secret from environment or use default
    const jwtSecret = getJwtSecret();

    // Verify token
    jwt.verify(token, jwtSecret, (err, user) => {
      if (err) {
        const errorCode =
          err.name === "TokenExpiredError"
            ? "AUTH_TOKEN_EXPIRED"
            : "AUTH_TOKEN_INVALID";
        const errorMessage =
          err.name === "TokenExpiredError"
            ? "Your session has expired. Please login again."
            : "Invalid authentication token";

        return res.status(401).json({
          error: {
            code: errorCode,
            message: errorMessage,
            details: err.message,
            timestamp: new Date().toISOString(),
            path: req.path,
            service: "student-service",
            traceId: req.headers["x-trace-id"] || generateTraceId(),
          },
        });
      }

      // Attach user info to request
      req.user = user;
      next();
    });
  } catch (error) {
    return res.status(500).json({
      error: {
        code: "SVC_INTERNAL_ERROR",
        message: "Internal server error during authentication",
        details: error.message,
        timestamp: new Date().toISOString(),
        path: req.path,
        service: "student-service",
        traceId: req.headers["x-trace-id"] || generateTraceId(),
      },
    });
  }
};

/**
 * Optional authentication - doesn't fail if no token
 * Sets req.user if valid token is provided
 */
const optionalAuth = (req, res, next) => {
  try {
    const authHeader = req.headers["authorization"];
    const token = authHeader && authHeader.split(" ")[1];

    if (!token) {
      req.user = null;
      return next();
    }

    const jwtSecret = getJwtSecret();

    jwt.verify(token, jwtSecret, (err, user) => {
      if (err) {
        req.user = null;
      } else {
        req.user = user;
      }
      next();
    });
  } catch (error) {
    req.user = null;
    next();
  }
};

/**
 * Role-based authorization middleware
 * @param {Array<string>} allowedRoles - Array of roles that can access the route
 */
const authorize = (...allowedRoles) => {
  return (req, res, next) => {
    if (!req.user) {
      return res.status(401).json({
        error: {
          code: "AUTH_TOKEN_MISSING",
          message: "Authentication required",
          details: "Please login to access this resource",
          timestamp: new Date().toISOString(),
          path: req.path,
          service: "student-service",
          traceId: req.headers["x-trace-id"] || generateTraceId(),
        },
      });
    }

    const userRoles = req.user.roles || [];
    const hasPermission = allowedRoles.some((role) => userRoles.includes(role));

    if (!hasPermission) {
      return res.status(403).json({
        error: {
          code: "AUTH_INSUFFICIENT_PERMISSIONS",
          message: "Insufficient permissions to access this resource",
          details: `Required roles: ${allowedRoles.join(", ")}`,
          timestamp: new Date().toISOString(),
          path: req.path,
          service: "student-service",
          traceId: req.headers["x-trace-id"] || generateTraceId(),
        },
      });
    }

    next();
  };
};

/**
 * Generate unique trace ID
 */
function generateTraceId() {
  return `${Date.now()}-${Math.random().toString(36).substr(2, 9)}`;
}

module.exports = {
  authenticateToken,
  optionalAuth,
  authorize,
};

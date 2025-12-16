// Fichier : student-service/src/middleware/error.middleware.js

/**
 * Unified Error Response Middleware
 * Handles all errors and formats them according to standard error response format
 */

/**
 * Get HTTP status code based on error code
 */
function getHttpStatusCode(errorCode) {
  const statusMap = {
    // Authentication errors
    AUTH_TOKEN_MISSING: 401,
    AUTH_TOKEN_INVALID: 401,
    AUTH_TOKEN_EXPIRED: 401,
    AUTH_INSUFFICIENT_PERMISSIONS: 403,
    AUTH_USER_NOT_FOUND: 404,

    // Validation errors
    VAL_REQUIRED_FIELD: 400,
    VAL_INVALID_FORMAT: 400,
    VAL_OUT_OF_RANGE: 400,
    VAL_DUPLICATE_ENTRY: 409,
    VAL_FOREIGN_KEY_VIOLATION: 400,

    // Resource errors
    RES_NOT_FOUND: 404,
    RES_ALREADY_EXISTS: 409,
    RES_CONFLICT: 409,
    RES_DELETED: 410,

    // Service errors
    SVC_UNAVAILABLE: 503,
    SVC_TIMEOUT: 504,
    SVC_CIRCUIT_OPEN: 503,
    SVC_COMMUNICATION_ERROR: 503,
    SVC_INTERNAL_ERROR: 500,

    // Business logic errors
    BIZ_ENROLLMENT_FULL: 422,
    BIZ_PREREQUISITE_NOT_MET: 422,
    BIZ_SCHEDULE_CONFLICT: 422,
    BIZ_INSUFFICIENT_BALANCE: 422,
    BIZ_PAYMENT_FAILED: 422,
    BIZ_INVALID_OPERATION: 422,

    // Database errors
    DB_CONNECTION_ERROR: 500,
    DB_QUERY_ERROR: 500,
    DB_TRANSACTION_ERROR: 500,
  };

  return statusMap[errorCode] || 500;
}

/**
 * Error response class
 */
class ErrorResponse {
  constructor(code, message, details = null) {
    this.error = {
      code,
      message,
      details,
      timestamp: new Date().toISOString(),
      path: null,
      service: "student-service",
      traceId: null,
    };
  }
}

/**
 * Custom Service Exception class
 */
class ServiceException extends Error {
  constructor(code, message, details = null) {
    super(message);
    this.code = code;
    this.details = details;
    this.name = "ServiceException";
  }
}

/**
 * Global error handling middleware
 */
const errorHandler = (err, req, res, next) => {
  console.error("Error occurred:", err);

  // Generate trace ID if not present
  const traceId = req.headers["x-trace-id"] || generateTraceId();

  // Handle ServiceException
  if (err instanceof ServiceException) {
    const errorResponse = new ErrorResponse(err.code, err.message, err.details);
    errorResponse.error.path = req.path;
    errorResponse.error.traceId = traceId;

    const statusCode = getHttpStatusCode(err.code);
    return res.status(statusCode).json(errorResponse);
  }

  // Handle Prisma errors
  if (err.code && err.code.startsWith("P")) {
    return handlePrismaError(err, req, res, traceId);
  }

  // Handle validation errors
  if (err.name === "ValidationError") {
    const errorResponse = new ErrorResponse(
      "VAL_INVALID_FORMAT",
      "Validation failed",
      err.message
    );
    errorResponse.error.path = req.path;
    errorResponse.error.traceId = traceId;

    return res.status(400).json(errorResponse);
  }

  // Default internal server error
  const errorResponse = new ErrorResponse(
    "SVC_INTERNAL_ERROR",
    "An unexpected error occurred",
    process.env.NODE_ENV === "development" ? err.message : null
  );
  errorResponse.error.path = req.path;
  errorResponse.error.traceId = traceId;

  res.status(500).json(errorResponse);
};

/**
 * Handle Prisma-specific errors
 */
function handlePrismaError(err, req, res, traceId) {
  let errorResponse;

  switch (err.code) {
    case "P2002": // Unique constraint violation
      errorResponse = new ErrorResponse(
        "VAL_DUPLICATE_ENTRY",
        "Duplicate entry detected",
        `A record with this ${err.meta?.target?.[0]} already exists`
      );
      break;

    case "P2025": // Record not found
      errorResponse = new ErrorResponse(
        "RES_NOT_FOUND",
        "Record not found",
        "The requested resource does not exist"
      );
      break;

    case "P2003": // Foreign key constraint violation
      errorResponse = new ErrorResponse(
        "VAL_FOREIGN_KEY_VIOLATION",
        "Invalid reference",
        "Referenced entity does not exist"
      );
      break;

    case "P2014": // Relation violation
      errorResponse = new ErrorResponse(
        "RES_CONFLICT",
        "Cannot perform operation due to related records",
        err.message
      );
      break;

    default:
      errorResponse = new ErrorResponse(
        "DB_QUERY_ERROR",
        "Database operation failed",
        err.message
      );
  }

  errorResponse.error.path = req.path;
  errorResponse.error.traceId = traceId;

  const statusCode = getHttpStatusCode(errorResponse.error.code);
  return res.status(statusCode).json(errorResponse);
}

/**
 * 404 Not Found handler
 */
const notFoundHandler = (req, res) => {
  const errorResponse = new ErrorResponse(
    "RES_NOT_FOUND",
    "Endpoint not found",
    `The requested endpoint ${req.method} ${req.path} does not exist`
  );
  errorResponse.error.path = req.path;
  errorResponse.error.traceId = req.headers["x-trace-id"] || generateTraceId();

  res.status(404).json(errorResponse);
};

/**
 * Generate unique trace ID
 */
function generateTraceId() {
  return `${Date.now()}-${Math.random().toString(36).substr(2, 9)}`;
}

module.exports = {
  errorHandler,
  notFoundHandler,
  ServiceException,
  ErrorResponse,
};

// Fichier : student-service/src/middleware/trace.middleware.js

const { v4: uuidv4 } = require("uuid");

/**
 * Trace ID Middleware
 * Generates or propagates trace IDs for distributed tracing
 */
const traceMiddleware = (req, res, next) => {
  // Get trace ID from header or generate new one
  const traceId = req.headers["x-trace-id"] || uuidv4();

  // Attach to request
  req.traceId = traceId;

  // Set response header
  res.setHeader("X-Trace-Id", traceId);

  // Log request
  console.log(`[${traceId}] ${req.method} ${req.path}`);

  next();
};

module.exports = traceMiddleware;

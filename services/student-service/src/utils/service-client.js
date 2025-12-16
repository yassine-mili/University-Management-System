// Fichier : student-service/src/utils/service-client.js

const axios = require("axios");
const { ServiceException } = require("../middleware/error.middleware");

/**
 * Circuit Breaker implementation for service calls
 */
class CircuitBreaker {
  constructor(serviceName, options = {}) {
    this.serviceName = serviceName;
    this.failureThreshold = options.failureThreshold || 5;
    this.resetTimeout = options.resetTimeout || 60000; // 1 minute
    this.failureCount = 0;
    this.lastFailureTime = null;
    this.state = "CLOSED"; // CLOSED, OPEN, HALF_OPEN
  }

  async execute(fn) {
    if (this.state === "OPEN") {
      const now = Date.now();
      if (now - this.lastFailureTime >= this.resetTimeout) {
        console.log(
          `Circuit breaker for ${this.serviceName} entering HALF_OPEN state`
        );
        this.state = "HALF_OPEN";
      } else {
        throw new ServiceException(
          "SVC_CIRCUIT_OPEN",
          `${this.serviceName} is currently unavailable`,
          "Circuit breaker is open. Please try again later."
        );
      }
    }

    try {
      const result = await fn();

      // Success - reset if we were in HALF_OPEN
      if (this.state === "HALF_OPEN") {
        console.log(`Circuit breaker for ${this.serviceName} reset to CLOSED`);
        this.reset();
      }

      return result;
    } catch (error) {
      this.recordFailure();
      throw error;
    }
  }

  recordFailure() {
    this.failureCount++;
    this.lastFailureTime = Date.now();

    if (this.failureCount >= this.failureThreshold) {
      console.error(
        `Circuit breaker for ${this.serviceName} opened after ${this.failureCount} failures`
      );
      this.state = "OPEN";
    }
  }

  reset() {
    this.failureCount = 0;
    this.lastFailureTime = null;
    this.state = "CLOSED";
  }
}

/**
 * Service Client for REST API calls
 */
class ServiceClient {
  constructor(serviceName, baseUrl, options = {}) {
    this.serviceName = serviceName;
    this.baseUrl = baseUrl;
    this.timeout = options.timeout || 5000;
    this.retries = options.retries || 3;
    this.retryDelay = options.retryDelay || 1000;
    this.circuitBreaker = new CircuitBreaker(
      serviceName,
      options.circuitBreaker
    );

    // Create axios instance
    this.client = axios.create({
      baseURL: baseUrl,
      timeout: this.timeout,
      headers: {
        "Content-Type": "application/json",
      },
    });

    // Add request interceptor for trace ID
    this.client.interceptors.request.use((config) => {
      if (config.traceId) {
        config.headers["X-Trace-Id"] = config.traceId;
      }
      if (config.token) {
        config.headers["Authorization"] = `Bearer ${config.token}`;
      }
      return config;
    });
  }

  /**
   * Make a GET request with circuit breaker and retry logic
   */
  async get(path, config = {}) {
    return this.circuitBreaker.execute(async () => {
      return this.retryRequest(async () => {
        try {
          const response = await this.client.get(path, config);
          return response.data;
        } catch (error) {
          throw this.handleError(error);
        }
      });
    });
  }

  /**
   * Make a POST request with circuit breaker and retry logic
   */
  async post(path, data, config = {}) {
    return this.circuitBreaker.execute(async () => {
      return this.retryRequest(async () => {
        try {
          const response = await this.client.post(path, data, config);
          return response.data;
        } catch (error) {
          throw this.handleError(error);
        }
      });
    });
  }

  /**
   * Make a PUT request with circuit breaker and retry logic
   */
  async put(path, data, config = {}) {
    return this.circuitBreaker.execute(async () => {
      return this.retryRequest(async () => {
        try {
          const response = await this.client.put(path, data, config);
          return response.data;
        } catch (error) {
          throw this.handleError(error);
        }
      });
    });
  }

  /**
   * Make a DELETE request with circuit breaker and retry logic
   */
  async delete(path, config = {}) {
    return this.circuitBreaker.execute(async () => {
      return this.retryRequest(async () => {
        try {
          const response = await this.client.delete(path, config);
          return response.data;
        } catch (error) {
          throw this.handleError(error);
        }
      });
    });
  }

  /**
   * Retry logic for failed requests
   */
  async retryRequest(fn, attempt = 1) {
    try {
      return await fn();
    } catch (error) {
      if (attempt < this.retries && this.isRetryable(error)) {
        console.log(
          `Retrying ${this.serviceName} request (attempt ${attempt + 1}/${
            this.retries
          })`
        );
        await this.delay(this.retryDelay * attempt);
        return this.retryRequest(fn, attempt + 1);
      }
      throw error;
    }
  }

  /**
   * Check if error is retryable
   */
  isRetryable(error) {
    if (error.code === "ECONNREFUSED" || error.code === "ETIMEDOUT") {
      return true;
    }
    if (error.response) {
      const status = error.response.status;
      return status === 503 || status === 504 || status === 429;
    }
    return false;
  }

  /**
   * Delay utility
   */
  delay(ms) {
    return new Promise((resolve) => setTimeout(resolve, ms));
  }

  /**
   * Handle and transform errors
   */
  handleError(error) {
    console.error(`Error calling ${this.serviceName}:`, error.message);

    if (error.code === "ECONNREFUSED") {
      return new ServiceException(
        "SVC_UNAVAILABLE",
        `${this.serviceName} is currently unavailable`,
        `Failed to connect to ${this.serviceName}`
      );
    }

    if (error.code === "ETIMEDOUT" || error.code === "ECONNABORTED") {
      return new ServiceException(
        "SVC_TIMEOUT",
        `Request to ${this.serviceName} timed out`,
        `Timeout after ${this.timeout}ms`
      );
    }

    if (error.response) {
      // Service returned an error response
      const errorData = error.response.data?.error || error.response.data;
      return new ServiceException(
        errorData.code || "SVC_COMMUNICATION_ERROR",
        errorData.message || `Error from ${this.serviceName}`,
        errorData.details || error.message
      );
    }

    return new ServiceException(
      "SVC_COMMUNICATION_ERROR",
      `Failed to communicate with ${this.serviceName}`,
      error.message
    );
  }
}

/**
 * Service URLs configuration
 */
const SERVICE_URLS = {
  AUTH_SERVICE: process.env.AUTH_SERVICE_URL || "http://auth_service:8081",
  STUDENT_SERVICE:
    process.env.STUDENT_SERVICE_URL || "http://student_service:8082",
  COURSES_SERVICE:
    process.env.COURSES_SERVICE_URL || "http://courses_service:8083",
  GRADES_SERVICE:
    process.env.GRADES_SERVICE_URL || "http://grades_service:8084",
  BILLING_SERVICE:
    process.env.BILLING_SERVICE_URL || "http://billing_service:5000",
};

/**
 * Pre-configured service clients
 */
const authServiceClient = new ServiceClient(
  "auth-service",
  SERVICE_URLS.AUTH_SERVICE,
  {
    timeout: 5000,
    retries: 3,
    circuitBreaker: { failureThreshold: 5, resetTimeout: 60000 },
  }
);

module.exports = {
  ServiceClient,
  CircuitBreaker,
  SERVICE_URLS,
  authServiceClient,
};

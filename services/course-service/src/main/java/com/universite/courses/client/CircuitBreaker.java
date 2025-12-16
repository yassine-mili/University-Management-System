package com.universite.courses.client;

import com.universite.courses.exception.CourseServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.Instant;

/**
 * Circuit Breaker implementation for preventing cascading failures
 */
public class CircuitBreaker {
    private static final Logger logger = LoggerFactory.getLogger(CircuitBreaker.class);
    
    private enum State {
        CLOSED,     // Normal operation
        OPEN,       // Failing, reject requests
        HALF_OPEN   // Testing if service recovered
    }
    
    private final String serviceName;
    private final int failureThreshold;
    private final long resetTimeoutMs;
    
    private State state = State.CLOSED;
    private int failureCount = 0;
    private Instant lastFailureTime = null;
    
    /**
     * Constructor
     * 
     * @param serviceName Name of the service
     * @param failureThreshold Number of failures before opening circuit
     * @param resetTimeoutMs Time in ms before attempting to close circuit
     */
    public CircuitBreaker(String serviceName, int failureThreshold, long resetTimeoutMs) {
        this.serviceName = serviceName;
        this.failureThreshold = failureThreshold;
        this.resetTimeoutMs = resetTimeoutMs;
    }
    
    /**
     * Execute operation with circuit breaker protection
     */
    public <T> T execute(CircuitBreakerExecutor<T> executor) throws CourseServiceException {
        // Check if circuit should transition from OPEN to HALF_OPEN
        if (state == State.OPEN) {
            if (shouldAttemptReset()) {
                logger.info("Circuit breaker for {} entering HALF_OPEN state", serviceName);
                state = State.HALF_OPEN;
            } else {
                throw new CourseServiceException("SVC_CIRCUIT_OPEN", 
                    serviceName + " is currently unavailable",
                    "Circuit breaker is open. Please try again later.");
            }
        }
        
        try {
            // Execute the operation
            T result = executor.execute();
            
            // Success - reset circuit if in HALF_OPEN
            if (state == State.HALF_OPEN) {
                logger.info("Circuit breaker for {} reset to CLOSED", serviceName);
                reset();
            }
            
            return result;
            
        } catch (CourseServiceException e) {
            // Record failure
            recordFailure();
            throw e;
        }
    }
    
    /**
     * Record a failure
     */
    private synchronized void recordFailure() {
        failureCount++;
        lastFailureTime = Instant.now();
        
        if (failureCount >= failureThreshold && state != State.OPEN) {
            logger.error("Circuit breaker for {} OPENED after {} failures", 
                serviceName, failureCount);
            state = State.OPEN;
        }
    }
    
    /**
     * Reset circuit breaker
     */
    private synchronized void reset() {
        failureCount = 0;
        lastFailureTime = null;
        state = State.CLOSED;
    }
    
    /**
     * Check if enough time has passed to attempt reset
     */
    private boolean shouldAttemptReset() {
        if (lastFailureTime == null) {
            return true;
        }
        Duration elapsed = Duration.between(lastFailureTime, Instant.now());
        return elapsed.toMillis() >= resetTimeoutMs;
    }
    
    /**
     * Get current state
     */
    public State getState() {
        return state;
    }
    
    /**
     * Get failure count
     */
    public int getFailureCount() {
        return failureCount;
    }
    
    /**
     * Functional interface for circuit breaker execution
     */
    @FunctionalInterface
    public interface CircuitBreakerExecutor<T> {
        T execute() throws CourseServiceException;
    }
}

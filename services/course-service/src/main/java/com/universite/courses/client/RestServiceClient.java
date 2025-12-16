package com.universite.courses.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.universite.courses.exception.CourseServiceException;
import org.apache.hc.client5.http.classic.methods.*;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.util.Timeout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * REST Service Client with Circuit Breaker pattern
 * Handles HTTP communication with other microservices
 */
public class RestServiceClient {
    private static final Logger logger = LoggerFactory.getLogger(RestServiceClient.class);
    
    private final String serviceName;
    private final String baseUrl;
    private final CloseableHttpClient httpClient;
    private final CircuitBreaker circuitBreaker;
    private final ObjectMapper objectMapper;
    private final int maxRetries;
    private final int retryDelayMs;
    
    /**
     * Constructor with default configuration
     */
    public RestServiceClient(String serviceName, String baseUrl) {
        this(serviceName, baseUrl, 5000, 3, 1000);
    }
    
    /**
     * Constructor with custom configuration
     * 
     * @param serviceName Name of the service for logging
     * @param baseUrl Base URL of the service
     * @param timeoutMs Request timeout in milliseconds
     * @param maxRetries Maximum number of retry attempts
     * @param retryDelayMs Delay between retries in milliseconds
     */
    public RestServiceClient(String serviceName, String baseUrl, 
                            int timeoutMs, int maxRetries, int retryDelayMs) {
        this.serviceName = serviceName;
        this.baseUrl = baseUrl;
        this.maxRetries = maxRetries;
        this.retryDelayMs = retryDelayMs;
        this.objectMapper = new ObjectMapper();
        
        // Configure connection pool
        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
        connectionManager.setMaxTotal(100);
        connectionManager.setDefaultMaxPerRoute(20);
        
        // Configure request timeouts
        RequestConfig requestConfig = RequestConfig.custom()
            .setConnectionRequestTimeout(Timeout.ofMilliseconds(timeoutMs))
            .setResponseTimeout(Timeout.ofMilliseconds(timeoutMs))
            .build();
        
        // Create HTTP client
        this.httpClient = HttpClients.custom()
            .setConnectionManager(connectionManager)
            .setDefaultRequestConfig(requestConfig)
            .build();
        
        // Create circuit breaker
        this.circuitBreaker = new CircuitBreaker(serviceName, 5, 60000);
    }
    
    /**
     * Execute GET request
     */
    public <T> T get(String path, Class<T> responseType, Map<String, String> headers) 
            throws CourseServiceException {
        return circuitBreaker.execute(() -> {
            return retryRequest(() -> executeGet(path, responseType, headers), 1);
        });
    }
    
    /**
     * Execute POST request
     */
    public <T> T post(String path, Object body, Class<T> responseType, Map<String, String> headers) 
            throws CourseServiceException {
        return circuitBreaker.execute(() -> {
            return retryRequest(() -> executePost(path, body, responseType, headers), 1);
        });
    }
    
    /**
     * Execute PUT request
     */
    public <T> T put(String path, Object body, Class<T> responseType, Map<String, String> headers) 
            throws CourseServiceException {
        return circuitBreaker.execute(() -> {
            return retryRequest(() -> executePut(path, body, responseType, headers), 1);
        });
    }
    
    /**
     * Execute DELETE request
     */
    public <T> T delete(String path, Class<T> responseType, Map<String, String> headers) 
            throws CourseServiceException {
        return circuitBreaker.execute(() -> {
            return retryRequest(() -> executeDelete(path, responseType, headers), 1);
        });
    }
    
    /**
     * Execute GET request (internal)
     */
    private <T> T executeGet(String path, Class<T> responseType, Map<String, String> headers) 
            throws CourseServiceException {
        String url = baseUrl + path;
        HttpGet request = new HttpGet(url);
        addHeaders(request, headers);
        
        logger.debug("GET {}", url);
        return executeRequest(request, responseType);
    }
    
    /**
     * Execute POST request (internal)
     */
    private <T> T executePost(String path, Object body, Class<T> responseType, Map<String, String> headers) 
            throws CourseServiceException {
        String url = baseUrl + path;
        HttpPost request = new HttpPost(url);
        addHeaders(request, headers);
        
        try {
            String jsonBody = objectMapper.writeValueAsString(body);
            request.setEntity(new StringEntity(jsonBody, ContentType.APPLICATION_JSON));
            logger.debug("POST {} - Body: {}", url, jsonBody);
        } catch (Exception e) {
            throw new CourseServiceException("SVC_INTERNAL_ERROR", 
                "Failed to serialize request body", e.getMessage());
        }
        
        return executeRequest(request, responseType);
    }
    
    /**
     * Execute PUT request (internal)
     */
    private <T> T executePut(String path, Object body, Class<T> responseType, Map<String, String> headers) 
            throws CourseServiceException {
        String url = baseUrl + path;
        HttpPut request = new HttpPut(url);
        addHeaders(request, headers);
        
        try {
            String jsonBody = objectMapper.writeValueAsString(body);
            request.setEntity(new StringEntity(jsonBody, ContentType.APPLICATION_JSON));
            logger.debug("PUT {} - Body: {}", url, jsonBody);
        } catch (Exception e) {
            throw new CourseServiceException("SVC_INTERNAL_ERROR", 
                "Failed to serialize request body", e.getMessage());
        }
        
        return executeRequest(request, responseType);
    }
    
    /**
     * Execute DELETE request (internal)
     */
    private <T> T executeDelete(String path, Class<T> responseType, Map<String, String> headers) 
            throws CourseServiceException {
        String url = baseUrl + path;
        HttpDelete request = new HttpDelete(url);
        addHeaders(request, headers);
        
        logger.debug("DELETE {}", url);
        return executeRequest(request, responseType);
    }
    
    /**
     * Execute HTTP request
     */
    private <T> T executeRequest(HttpUriRequestBase request, Class<T> responseType) 
            throws CourseServiceException {
        try (CloseableHttpResponse response = httpClient.execute(request)) {
            int statusCode = response.getCode();
            String responseBody = EntityUtils.toString(response.getEntity());
            
            logger.debug("Response: {} - {}", statusCode, responseBody);
            
            // Check for errors
            if (statusCode >= 400) {
                handleErrorResponse(statusCode, responseBody);
            }
            
            // Parse successful response
            if (responseType == String.class) {
                return responseType.cast(responseBody);
            } else if (responseType == Void.class) {
                return null;
            } else {
                return objectMapper.readValue(responseBody, responseType);
            }
            
        } catch (IOException e) {
            logger.error("Network error calling {}: {}", serviceName, e.getMessage());
            throw new CourseServiceException("SVC_UNAVAILABLE", 
                serviceName + " is currently unavailable",
                "Failed to connect to " + serviceName);
        } catch (ParseException e) {
            logger.error("Error parsing response from {}: {}", serviceName, e.getMessage());
            throw new CourseServiceException("SVC_COMMUNICATION_ERROR", 
                "Failed to parse response from " + serviceName,
                e.getMessage());
        }
    }
    
    /**
     * Handle error response from service
     */
    private void handleErrorResponse(int statusCode, String responseBody) 
            throws CourseServiceException {
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> errorResponse = objectMapper.readValue(responseBody, Map.class);
            
            if (errorResponse.containsKey("error")) {
                @SuppressWarnings("unchecked")
                Map<String, String> error = (Map<String, String>) errorResponse.get("error");
                String code = error.getOrDefault("code", "SVC_COMMUNICATION_ERROR");
                String message = error.getOrDefault("message", "Error from " + serviceName);
                String details = error.get("details");
                
                throw new CourseServiceException(code, message, details);
            }
        } catch (Exception e) {
            // If we can't parse error, throw generic error
            logger.error("Error parsing error response: {}", e.getMessage());
        }
        
        // Fallback error
        throw new CourseServiceException("SVC_COMMUNICATION_ERROR", 
            "Error from " + serviceName + " (HTTP " + statusCode + ")",
            responseBody);
    }
    
    /**
     * Retry request with exponential backoff
     */
    private <T> T retryRequest(RequestExecutor<T> executor, int attempt) 
            throws CourseServiceException {
        try {
            return executor.execute();
        } catch (CourseServiceException e) {
            if (attempt < maxRetries && isRetryable(e)) {
                logger.warn("Retrying {} request (attempt {}/{}): {}", 
                    serviceName, attempt + 1, maxRetries, e.getMessage());
                
                try {
                    TimeUnit.MILLISECONDS.sleep(retryDelayMs * attempt);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw e;
                }
                
                return retryRequest(executor, attempt + 1);
            }
            throw e;
        }
    }
    
    /**
     * Check if error is retryable
     */
    private boolean isRetryable(CourseServiceException e) {
        String code = e.getCode();
        return code.equals("SVC_UNAVAILABLE") || 
               code.equals("SVC_TIMEOUT") || 
               code.equals("SVC_COMMUNICATION_ERROR");
    }
    
    /**
     * Add headers to request
     */
    private void addHeaders(HttpUriRequestBase request, Map<String, String> headers) {
        if (headers != null) {
            headers.forEach(request::addHeader);
        }
        request.addHeader("Content-Type", "application/json");
    }
    
    /**
     * Close HTTP client
     */
    public void close() {
        try {
            httpClient.close();
        } catch (IOException e) {
            logger.error("Error closing HTTP client: {}", e.getMessage());
        }
    }
    
    /**
     * Functional interface for request execution
     */
    @FunctionalInterface
    private interface RequestExecutor<T> {
        T execute() throws CourseServiceException;
    }
}

package com.universite.courses.filter;

import com.universite.courses.util.JwtUtil;
import io.jsonwebtoken.Claims;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.xml.soap.Detail;
import jakarta.xml.soap.SOAPElement;
import jakarta.xml.soap.SOAPException;
import jakarta.xml.soap.SOAPFault;
import jakarta.xml.soap.SOAPMessage;
import jakarta.xml.ws.handler.MessageContext;
import jakarta.xml.ws.handler.soap.SOAPHandler;
import jakarta.xml.ws.handler.soap.SOAPMessageContext;
import javax.xml.namespace.QName;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * SOAP Handler for JWT Authentication
 * Validates JWT tokens in SOAP requests
 */
public class JwtAuthenticationHandler implements SOAPHandler<SOAPMessageContext> {
    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationHandler.class);
    
    // Public endpoints that don't require authentication
    private static final Set<String> PUBLIC_ENDPOINTS = Set.of(
        "GetServiceInfo"
    );
    
    @Override
    public boolean handleMessage(SOAPMessageContext context) {
        Boolean outbound = (Boolean) context.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
        
        // Only process inbound requests
        if (outbound != null && outbound) {
            return true;
        }
        
        try {
            // Get trace ID
            String traceId = getOrGenerateTraceId(context);
            context.put("TRACE_ID", traceId);
            context.setScope("TRACE_ID", MessageContext.Scope.APPLICATION);
            
            // Get operation name
            String operation = getOperationName(context);
            logger.info("[{}] Processing SOAP request: {}", traceId, operation);
            
            // Skip authentication for public endpoints
            if (PUBLIC_ENDPOINTS.contains(operation)) {
                logger.debug("[{}] Public endpoint, skipping authentication", traceId);
                return true;
            }
            
            // Extract Authorization header
            @SuppressWarnings("unchecked")
            Map<String, java.util.List<String>> headers = 
                (Map<String, java.util.List<String>>) context.get(MessageContext.HTTP_REQUEST_HEADERS);
            
            String authHeader = null;
            if (headers != null) {
                // Try both lowercase and capitalized versions
                java.util.List<String> authHeaders = headers.get("authorization");
                if (authHeaders == null) {
                    authHeaders = headers.get("Authorization");
                }
                if (authHeaders != null && !authHeaders.isEmpty()) {
                    authHeader = authHeaders.get(0);
                }
            }
            
            // Check if token exists
            if (authHeader == null) {
                logger.warn("[{}] No authorization header found", traceId);
                return handleAuthError(context, "AUTH_TOKEN_MISSING", 
                    "No authentication token provided",
                    "Please provide a valid JWT token in the Authorization header",
                    traceId);
            }
            
            // Extract and validate token
            String token = JwtUtil.extractToken(authHeader);
            if (token == null) {
                logger.warn("[{}] Invalid authorization header format", traceId);
                return handleAuthError(context, "AUTH_TOKEN_INVALID", 
                    "Invalid authorization header format",
                    "Authorization header must be in format: Bearer <token>",
                    traceId);
            }
            
            // Validate JWT
            Claims claims = JwtUtil.validateToken(token);
            
            // Store user info in context for use by service
            context.put("USER_ID", JwtUtil.getUserId(claims));
            context.put("USERNAME", JwtUtil.getUsername(claims));
            context.put("USER_ROLES", JwtUtil.getRoles(claims));
            context.setScope("USER_ID", MessageContext.Scope.APPLICATION);
            context.setScope("USERNAME", MessageContext.Scope.APPLICATION);
            context.setScope("USER_ROLES", MessageContext.Scope.APPLICATION);
            
            logger.info("[{}] JWT validated successfully for user: {}", 
                traceId, JwtUtil.getUsername(claims));
            
            return true;
            
        } catch (JwtUtil.JwtAuthenticationException e) {
            String traceId = (String) context.get("TRACE_ID");
            logger.error("[{}] JWT validation failed: {}", traceId, e.getMessage());
            return handleAuthError(context, e.getCode(), e.getMessage(), e.getDetails(), traceId);
        } catch (Exception e) {
            String traceId = (String) context.get("TRACE_ID");
            logger.error("[{}] Unexpected error during authentication: {}", traceId, e.getMessage(), e);
            return handleAuthError(context, "SVC_INTERNAL_ERROR", 
                "Internal server error during authentication",
                e.getMessage(), traceId);
        }
    }
    
    @Override
    public boolean handleFault(SOAPMessageContext context) {
        // Let faults pass through
        return true;
    }
    
    @Override
    public void close(MessageContext context) {
        // Cleanup if needed
    }
    
    @Override
    public Set<QName> getHeaders() {
        return null;
    }
    
    /**
     * Handle authentication errors by creating SOAP fault
     */
    private boolean handleAuthError(SOAPMessageContext context, String code, 
                                    String message, String details, String traceId) {
        try {
            SOAPMessage soapMessage = context.getMessage();
            SOAPFault fault = soapMessage.getSOAPBody().addFault();
            
            fault.setFaultCode("soap:Server");
            fault.setFaultString(message);
            
            // Add error details
            Detail detail = fault.addDetail();
            SOAPElement errorDetail = detail.addChildElement("ErrorDetail", "err", "http://university.com/errors");
            
            errorDetail.addChildElement("Code").addTextNode(code);
            errorDetail.addChildElement("Message").addTextNode(message);
            errorDetail.addChildElement("Details").addTextNode(details != null ? details : "");
            errorDetail.addChildElement("Timestamp").addTextNode(java.time.Instant.now().toString());
            errorDetail.addChildElement("Service").addTextNode("courses-service");
            errorDetail.addChildElement("TraceId").addTextNode(traceId);
            
            // Set HTTP status code
            int statusCode = getHttpStatusCode(code);
            context.put(MessageContext.HTTP_RESPONSE_CODE, statusCode);
            
            return false;
            
        } catch (SOAPException e) {
            logger.error("[{}] Error creating SOAP fault: {}", traceId, e.getMessage(), e);
            return false;
        }
    }
    
    /**
     * Get operation name from SOAP context
     */
    private String getOperationName(SOAPMessageContext context) {
        try {
            QName operation = (QName) context.get(MessageContext.WSDL_OPERATION);
            if (operation != null) {
                return operation.getLocalPart();
            }
        } catch (Exception e) {
            logger.debug("Could not extract operation name: {}", e.getMessage());
        }
        return "Unknown";
    }
    
    /**
     * Get or generate trace ID
     */
    private String getOrGenerateTraceId(SOAPMessageContext context) {
        @SuppressWarnings("unchecked")
        Map<String, java.util.List<String>> headers = 
            (Map<String, java.util.List<String>>) context.get(MessageContext.HTTP_REQUEST_HEADERS);
        
        if (headers != null) {
            java.util.List<String> traceHeaders = headers.get("x-trace-id");
            if (traceHeaders == null) {
                traceHeaders = headers.get("X-Trace-Id");
            }
            if (traceHeaders != null && !traceHeaders.isEmpty()) {
                return traceHeaders.get(0);
            }
        }
        
        return UUID.randomUUID().toString();
    }
    
    /**
     * Map error codes to HTTP status codes
     */
    private int getHttpStatusCode(String errorCode) {
        return switch (errorCode) {
            case "AUTH_TOKEN_MISSING", "AUTH_TOKEN_INVALID", "AUTH_TOKEN_EXPIRED" -> 401;
            case "AUTH_INSUFFICIENT_PERMISSIONS" -> 403;
            case "VAL_REQUIRED_FIELD", "VAL_INVALID_FORMAT", "VAL_OUT_OF_RANGE", 
                 "VAL_FOREIGN_KEY_VIOLATION" -> 400;
            case "RES_NOT_FOUND" -> 404;
            case "RES_ALREADY_EXISTS", "RES_CONFLICT", "VAL_DUPLICATE_ENTRY" -> 409;
            case "SVC_UNAVAILABLE", "SVC_CIRCUIT_OPEN" -> 503;
            case "SVC_TIMEOUT" -> 504;
            case "BIZ_ENROLLMENT_FULL", "BIZ_PREREQUISITE_NOT_MET", 
                 "BIZ_SCHEDULE_CONFLICT", "BIZ_INVALID_OPERATION" -> 422;
            default -> 500;
        };
    }
}

package com.universite.courses.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;

/**
 * JWT Utility class for parsing and validating JWT tokens
 */
public class JwtUtil {
    private static final Logger logger = LoggerFactory.getLogger(JwtUtil.class);
    
    // JWT Secret key - should match across all services
    private static final String JWT_SECRET = System.getenv("JWT_SECRET") != null 
        ? System.getenv("JWT_SECRET")
        : "UniversityManagementSystemSecretKey2024VeryLongSecretForHS512Algorithm";
    
    private static final SecretKey SECRET_KEY = Keys.hmacShaKeyFor(
        JWT_SECRET.getBytes(StandardCharsets.UTF_8)
    );
    
    /**
     * Parse and validate JWT token
     * 
     * @param token JWT token string
     * @return Claims object containing token data
     * @throws JwtAuthenticationException if token is invalid
     */
    public static Claims validateToken(String token) throws JwtAuthenticationException {
        try {
            return Jwts.parser()
                .verifyWith(SECRET_KEY)
                .build()
                .parseSignedClaims(token)
                .getPayload();
        } catch (ExpiredJwtException e) {
            logger.warn("JWT token expired: {}", e.getMessage());
            throw new JwtAuthenticationException("AUTH_TOKEN_EXPIRED", 
                "Your session has expired. Please login again.", 
                "Token expired at " + e.getClaims().getExpiration());
        } catch (MalformedJwtException e) {
            logger.warn("Malformed JWT token: {}", e.getMessage());
            throw new JwtAuthenticationException("AUTH_TOKEN_INVALID", 
                "Invalid authentication token", 
                "Token format is invalid");
        } catch (SignatureException e) {
            logger.warn("Invalid JWT signature: {}", e.getMessage());
            throw new JwtAuthenticationException("AUTH_TOKEN_INVALID", 
                "Invalid authentication token", 
                "Token signature verification failed");
        } catch (Exception e) {
            logger.error("Error validating JWT token: {}", e.getMessage());
            throw new JwtAuthenticationException("AUTH_TOKEN_INVALID", 
                "Invalid authentication token", 
                e.getMessage());
        }
    }
    
    /**
     * Extract username from token
     */
    public static String getUsername(Claims claims) {
        return claims.get("username", String.class);
    }
    
    /**
     * Extract user ID from token
     */
    public static String getUserId(Claims claims) {
        return claims.getSubject();
    }
    
    /**
     * Extract roles from token
     */
    @SuppressWarnings("unchecked")
    public static List<String> getRoles(Claims claims) {
        Object roles = claims.get("roles");
        if (roles instanceof List) {
            return (List<String>) roles;
        }
        return List.of();
    }
    
    /**
     * Extract email from token
     */
    public static String getEmail(Claims claims) {
        return claims.get("email", String.class);
    }
    
    /**
     * Check if token is expired
     */
    public static boolean isTokenExpired(Claims claims) {
        Date expiration = claims.getExpiration();
        return expiration != null && expiration.before(new Date());
    }
    
    /**
     * Check if user has required role
     */
    public static boolean hasRole(Claims claims, String... requiredRoles) {
        List<String> userRoles = getRoles(claims);
        for (String role : requiredRoles) {
            if (userRoles.contains(role)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Extract token from Authorization header
     * 
     * @param authHeader Authorization header value (Bearer token)
     * @return JWT token string or null
     */
    public static String extractToken(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return null;
        }
        return authHeader.substring(7);
    }
    
    /**
     * Custom exception for JWT authentication errors
     */
    public static class JwtAuthenticationException extends Exception {
        private final String code;
        private final String details;
        
        public JwtAuthenticationException(String code, String message, String details) {
            super(message);
            this.code = code;
            this.details = details;
        }
        
        public String getCode() {
            return code;
        }
        
        public String getDetails() {
            return details;
        }
    }
}

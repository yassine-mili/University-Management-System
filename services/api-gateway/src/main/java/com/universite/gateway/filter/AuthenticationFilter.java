package com.universite.gateway.filter;

import com.universite.gateway.util.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {

    @Autowired
    private JwtUtil jwtUtil;

    public AuthenticationFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();

            log.debug("Processing request: {} {}", request.getMethod(), request.getURI());

            if (request.getMethod() == HttpMethod.OPTIONS) {
                log.trace("Allowing CORS preflight without auth for: {}", request.getURI());
                return chain.filter(exchange);
            }

            // Extract Authorization header
            if (!request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                log.warn("Missing Authorization header for: {}", request.getURI());
                return onError(exchange, "Missing Authorization header", HttpStatus.UNAUTHORIZED);
            }

            String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
            
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                log.warn("Invalid Authorization header format for: {}", request.getURI());
                return onError(exchange, "Invalid Authorization header format", HttpStatus.UNAUTHORIZED);
            }

            String token = authHeader.substring(7);

            try {
                // Validate JWT token
                if (!jwtUtil.validateToken(token)) {
                    log.warn("Invalid or expired token for: {}", request.getURI());
                    return onError(exchange, "Invalid or expired token", HttpStatus.UNAUTHORIZED);
                }

                // Extract user information from token
                String username = jwtUtil.extractUsername(token);
                String role = jwtUtil.extractRole(token);

                log.debug("Authenticated user: {} with role: {}", username, role);

                // Add user information to request headers for downstream services
                ServerHttpRequest modifiedRequest = exchange.getRequest().mutate()
                        .header("X-User-Username", username)
                        .header("X-User-Role", role)
                        .build();

                return chain.filter(exchange.mutate().request(modifiedRequest).build());

            } catch (Exception e) {
                log.error("JWT validation error: {}", e.getMessage());
                return onError(exchange, "JWT validation failed: " + e.getMessage(), HttpStatus.UNAUTHORIZED);
            }
        };
    }

    private Mono<Void> onError(ServerWebExchange exchange, String err, HttpStatus httpStatus) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(httpStatus);
        response.getHeaders().add("Content-Type", "application/json");
        String origin = exchange.getRequest().getHeaders().getOrigin();
        if (origin != null) {
            response.getHeaders().set("Access-Control-Allow-Origin", origin);
            response.getHeaders().set("Vary", HttpHeaders.ORIGIN);
        }
        response.getHeaders().set("Access-Control-Allow-Credentials", "true");
        response.getHeaders().set("Access-Control-Allow-Headers", "*");
        response.getHeaders().set("Access-Control-Allow-Methods", "GET,POST,PUT,DELETE,OPTIONS,PATCH");
        
        String errorBody = String.format(
            "{\"error\":\"%s\",\"message\":\"%s\",\"status\":%d}",
            httpStatus.getReasonPhrase(),
            err,
            httpStatus.value()
        );
        
        return response.writeWith(Mono.just(response.bufferFactory().wrap(errorBody.getBytes())));
    }

    public static class Config {
        // Configuration properties can be added here if needed
    }
}

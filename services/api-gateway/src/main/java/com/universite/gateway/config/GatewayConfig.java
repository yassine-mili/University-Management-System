package com.universite.gateway.config;

import com.universite.gateway.filter.AuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class GatewayConfig {

    private final AuthenticationFilter authenticationFilter;

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                // Public routes (no authentication required)
                .route("auth-public", r -> r
                        .path("/api/auth/login", "/api/auth/register", "/api/auth/health")
                        .uri("lb://auth-service"))
                
                // Protected routes (authentication required)
                .route("auth-protected", r -> r
                        .path("/api/auth/**")
                        .filters(f -> f.filter(authenticationFilter.apply(new AuthenticationFilter.Config())))
                        .uri("lb://auth-service"))
                
                .route("students-protected", r -> r
                        .path("/api/students/**")
                        .filters(f -> f.filter(authenticationFilter.apply(new AuthenticationFilter.Config())))
                        .uri("lb://student-service"))
                
                .route("courses-protected", r -> r
                        .path("/api/courses/**")
                        .filters(f -> f.filter(authenticationFilter.apply(new AuthenticationFilter.Config())))
                        .uri("lb://courses-service"))
                
                .route("grades-protected", r -> r
                        .path("/api/grades/**")
                        .filters(f -> f.filter(authenticationFilter.apply(new AuthenticationFilter.Config())))
                        .uri("lb://grades-service"))
                
                .route("billing-protected", r -> r
                        .path("/api/billing/**")
                        .filters(f -> f.filter(authenticationFilter.apply(new AuthenticationFilter.Config())))
                        .uri("lb://billing-service"))
                
                .build();
    }
}

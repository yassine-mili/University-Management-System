package com.universite.gateway.config;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import reactor.core.publisher.Mono;

@Configuration
public class RateLimiterConfiguration {

    /**
     * Rate limiting based on user IP address
     */
    @Bean
    @Primary
    public KeyResolver userKeyResolver() {
        return exchange -> {
            String userIp = exchange.getRequest().getRemoteAddress() != null
                    ? exchange.getRequest().getRemoteAddress().getAddress().getHostAddress()
                    : "unknown";
            return Mono.just(userIp);
        };
    }

    /**
     * Alternative: Rate limiting based on authenticated user
     */
    @Bean
    public KeyResolver authenticatedUserKeyResolver() {
        return exchange -> {
            String username = exchange.getRequest().getHeaders().getFirst("X-User-Username");
            return Mono.just(username != null ? username : "anonymous");
        };
    }
}

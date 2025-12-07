package com.universite.gateway.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/gateway")
public class GatewayController {

    @GetMapping("/health")
    public Mono<ResponseEntity<Map<String, Object>>> health() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("service", "API Gateway");
        response.put("timestamp", LocalDateTime.now());
        response.put("message", "Gateway is running");
        
        return Mono.just(ResponseEntity.ok(response));
    }

    @GetMapping("/info")
    public Mono<ResponseEntity<Map<String, Object>>> info() {
        Map<String, Object> response = new HashMap<>();
        response.put("name", "University Management System - API Gateway");
        response.put("version", "1.0.0");
        response.put("description", "Centralized API Gateway for all microservices");
        response.put("port", 8080);
        
        Map<String, String> routes = new HashMap<>();
        routes.put("/api/auth/**", "Authentication Service (8081)");
        routes.put("/api/students/**", "Student Service (8082)");
        routes.put("/api/courses/**", "Courses Service (8083)");
        routes.put("/api/grades/**", "Grades Service (8084)");
        routes.put("/api/billing/**", "Billing Service (5000)");
        
        response.put("routes", routes);
        response.put("timestamp", LocalDateTime.now());
        
        return Mono.just(ResponseEntity.ok(response));
    }
}

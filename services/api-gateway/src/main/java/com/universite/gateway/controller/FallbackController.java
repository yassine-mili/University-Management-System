package com.universite.gateway.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/fallback")
public class FallbackController {

    @GetMapping("/auth")
    public Mono<ResponseEntity<Map<String, Object>>> authFallback() {
        return Mono.just(createFallbackResponse("Auth Service", 
                "The authentication service is currently unavailable. Please try again later."));
    }

    @GetMapping("/student")
    public Mono<ResponseEntity<Map<String, Object>>> studentFallback() {
        return Mono.just(createFallbackResponse("Student Service", 
                "The student service is currently unavailable. Please try again later."));
    }

    @GetMapping("/courses")
    public Mono<ResponseEntity<Map<String, Object>>> coursesFallback() {
        return Mono.just(createFallbackResponse("Courses Service", 
                "The courses service is currently unavailable. Please try again later."));
    }

    @GetMapping("/grades")
    public Mono<ResponseEntity<Map<String, Object>>> gradesFallback() {
        return Mono.just(createFallbackResponse("Grades Service", 
                "The grades service is currently unavailable. Please try again later."));
    }

    @GetMapping("/billing")
    public Mono<ResponseEntity<Map<String, Object>>> billingFallback() {
        return Mono.just(createFallbackResponse("Billing Service", 
                "The billing service is currently unavailable. Please try again later."));
    }

    private ResponseEntity<Map<String, Object>> createFallbackResponse(String service, String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("service", service);
        response.put("message", message);
        response.put("timestamp", LocalDateTime.now());
        response.put("status", "SERVICE_UNAVAILABLE");
        
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
    }
}

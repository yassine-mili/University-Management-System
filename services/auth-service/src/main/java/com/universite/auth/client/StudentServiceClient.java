package com.universite.auth.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class StudentServiceClient {

    private final RestTemplate restTemplate;

    @Value("${student.service.url:http://localhost:8082}")
    private String studentServiceUrl;

    public String getOrCreateStudentProfile(Long userId, String email, String username) {
        String studentId = findStudentNumberByEmail(email);
        if (studentId != null) {
            return studentId;
        }

        // Attempt create, then re-check in case of conflict race
        studentId = createStudentProfile(userId, email, username);
        if (studentId != null) {
            return studentId;
        }

        return findStudentNumberByEmail(email);
    }

    public String createStudentProfile(Long userId, String email, String username) {
        try {
            log.info("Attempting to create student profile for user ID: {}, email: {}", userId, email);
            
            String url = studentServiceUrl + "/api/v1/students/internal";
            
            // Generate student number based on user ID
            String studentNumber = "STU" + String.format("%06d", userId);
            
            log.info("Generated student number: {}, calling URL: {}", studentNumber, url);
            
            // Prepare request body
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("numero_etudiant", studentNumber);
            requestBody.put("email", email);
            requestBody.put("nom", username); // Using username as last name for now
            requestBody.put("prenom", "Student"); // Default first name
            requestBody.put("niveau", "L1"); // Default level
            requestBody.put("est_actif", true);
            
            // Set headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
            
            // Make POST request
            restTemplate.postForObject(url, request, Map.class);
            
            log.info("Student profile created successfully for user ID: {}", userId);
            return studentNumber;
        } catch (HttpClientErrorException.Conflict e) {
            log.warn("Student profile already exists for email {} (conflict). Will attempt to fetch.", email);
            return findStudentNumberByEmail(email);
        } catch (Exception e) {
            log.error("Failed to create student profile for user ID: {}. Error: {}", userId, e.getMessage(), e);
            // Don't throw exception - registration should succeed even if student creation fails
            return null;
        }
    }

    public String findStudentNumberByEmail(String email) {
        try {
                URI uri = UriComponentsBuilder.fromHttpUrl(studentServiceUrl)
                    .path("/api/v1/students/internal/by-email/{email}")
                    .buildAndExpand(email)
                    .encode()
                    .toUri();

            log.info("Looking up student profile by email: {}", email);
            ResponseEntity<Map> response = restTemplate.getForEntity(uri, Map.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                Object data = response.getBody().get("data");
                if (data instanceof Map<?, ?> dataMap) {
                    Object numero = dataMap.get("numero_etudiant");
                    if (numero != null) {
                        return numero.toString();
                    }
                }
            }

            return null;
        } catch (HttpClientErrorException.NotFound e) {
            log.warn("Student profile not found for email: {}", email);
            return null;
        } catch (Exception e) {
            log.error("Error while looking up student profile for email {}: {}", email, e.getMessage(), e);
            return null;
        }
    }
}

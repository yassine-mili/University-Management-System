package com.universite.courses.client;

import com.universite.courses.exception.CourseServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Pre-configured service clients for inter-service communication
 */
public class ServiceClients {
    private static final Logger logger = LoggerFactory.getLogger(ServiceClients.class);
    
    // Service URLs from environment variables
    private static final String STUDENT_SERVICE_URL = 
        System.getenv("STUDENT_SERVICE_URL") != null 
            ? System.getenv("STUDENT_SERVICE_URL")
            : "http://student_service:8082";
    
    private static final String AUTH_SERVICE_URL = 
        System.getenv("AUTH_SERVICE_URL") != null 
            ? System.getenv("AUTH_SERVICE_URL")
            : "http://auth_service:8081";
    
    // Singleton instances
    private static RestServiceClient studentServiceClient;
    private static RestServiceClient authServiceClient;
    
    /**
     * Get Student Service client
     */
    public static synchronized RestServiceClient getStudentServiceClient() {
        if (studentServiceClient == null) {
            studentServiceClient = new RestServiceClient("student-service", STUDENT_SERVICE_URL);
            logger.info("Initialized Student Service client: {}", STUDENT_SERVICE_URL);
        }
        return studentServiceClient;
    }
    
    /**
     * Get Auth Service client
     */
    public static synchronized RestServiceClient getAuthServiceClient() {
        if (authServiceClient == null) {
            authServiceClient = new RestServiceClient("auth-service", AUTH_SERVICE_URL);
            logger.info("Initialized Auth Service client: {}", AUTH_SERVICE_URL);
        }
        return authServiceClient;
    }
    
    /**
     * Student DTO for inter-service communication
     */
    public static class StudentDTO {
        private Long id;
        private String numeroEtudiant;
        private String nom;
        private String prenom;
        private String email;
        private String dateNaissance;
        private String adresse;
        private String telephone;
        private String programme;
        
        // Getters and setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        
        public String getNumeroEtudiant() { return numeroEtudiant; }
        public void setNumeroEtudiant(String numeroEtudiant) { this.numeroEtudiant = numeroEtudiant; }
        
        public String getNom() { return nom; }
        public void setNom(String nom) { this.nom = nom; }
        
        public String getPrenom() { return prenom; }
        public void setPrenom(String prenom) { this.prenom = prenom; }
        
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        
        public String getDateNaissance() { return dateNaissance; }
        public void setDateNaissance(String dateNaissance) { this.dateNaissance = dateNaissance; }
        
        public String getAdresse() { return adresse; }
        public void setAdresse(String adresse) { this.adresse = adresse; }
        
        public String getTelephone() { return telephone; }
        public void setTelephone(String telephone) { this.telephone = telephone; }
        
        public String getProgramme() { return programme; }
        public void setProgramme(String programme) { this.programme = programme; }
    }
    
    /**
     * Validate student exists by calling Student Service
     * 
     * @param studentId Student identifier
     * @param traceId Trace ID for distributed tracing
     * @return StudentDTO if found
     * @throws CourseServiceException if student not found or service unavailable
     */
    public static StudentDTO validateStudent(String studentId, String traceId) 
            throws CourseServiceException {
        try {
            Map<String, String> headers = new HashMap<>();
            headers.put("X-Trace-Id", traceId);
            
            RestServiceClient client = getStudentServiceClient();
            StudentDTO student = client.get(
                "/api/v1/students/" + studentId, 
                StudentDTO.class, 
                headers
            );
            
            logger.info("[{}] Student validated: {}", traceId, studentId);
            return student;
            
        } catch (CourseServiceException e) {
            if (e.getCode().equals("RES_NOT_FOUND")) {
                throw new CourseServiceException("VAL_FOREIGN_KEY_VIOLATION",
                    "Student not found",
                    "Student with ID " + studentId + " does not exist");
            }
            throw e;
        }
    }
    
    /**
     * Validate multiple students exist
     * 
     * @param studentIds List of student identifiers
     * @param traceId Trace ID for distributed tracing
     * @throws CourseServiceException if any student not found
     */
    public static void validateStudents(List<String> studentIds, String traceId) 
            throws CourseServiceException {
        for (String studentId : studentIds) {
            validateStudent(studentId, traceId);
        }
    }
    
    /**
     * Close all service clients
     */
    public static void closeAll() {
        if (studentServiceClient != null) {
            studentServiceClient.close();
        }
        if (authServiceClient != null) {
            authServiceClient.close();
        }
    }
}

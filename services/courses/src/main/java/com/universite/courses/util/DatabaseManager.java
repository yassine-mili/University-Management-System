package main.java.com.universite.courses.util;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class DatabaseManager {
    
    private static EntityManagerFactory entityManagerFactory;
    
    public static void initialize() {
        try {
            log.info("Initializing database connection...");
            
            Map<String, String> properties = new HashMap<>();
            
            // Override database connection from environment variables
            String dbUrl = System.getenv("DB_URL");
            String dbUser = System.getenv("DB_USER");
            String dbPassword = System.getenv("DB_PASSWORD");
            
            if (dbUrl != null) {
                properties.put("jakarta.persistence.jdbc.url", dbUrl);
            }
            if (dbUser != null) {
                properties.put("jakarta.persistence.jdbc.user", dbUser);
            }
            if (dbPassword != null) {
                properties.put("jakarta.persistence.jdbc.password", dbPassword);
            }
            
            if (properties.isEmpty()) {
                entityManagerFactory = Persistence.createEntityManagerFactory("coursesPU");
            } else {
                entityManagerFactory = Persistence.createEntityManagerFactory("coursesPU", properties);
            }
            
            log.info("Database connection initialized successfully");
            
        } catch (Exception e) {
            log.error("Failed to initialize database connection: {}", e.getMessage());
            throw new RuntimeException("Database initialization failed", e);
        }
    }
    
    public static EntityManager getEntityManager() {
        if (entityManagerFactory == null) {
            initialize();
        }
        return entityManagerFactory.createEntityManager();
    }
    
    public static void close() {
        if (entityManagerFactory != null && entityManagerFactory.isOpen()) {
            log.info("Closing database connection...");
            entityManagerFactory.close();
            log.info("Database connection closed");
        }
    }
}

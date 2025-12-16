package com.universite.courses;

import com.universite.courses.config.DatabaseConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CoursesServiceApplication {
    private static final Logger logger = LoggerFactory.getLogger(CoursesServiceApplication.class);

    public static void main(String[] args) {
        logger.info("Starting University Courses Service (REST API)...");

        try {
            // Initialize database connection
            DatabaseConfig.getDataSource();
            logger.info("Database connection initialized successfully");

            // Start Spring Boot for REST API
            SpringApplication.run(CoursesServiceApplication.class, args);
            
            logger.info("=".repeat(80));
            logger.info("University Courses Service is running!");
            logger.info("REST API URL: http://0.0.0.0:8083/");
            logger.info("Health Check: http://0.0.0.0:8083/health");
            logger.info("=".repeat(80));

        } catch (Exception e) {
            logger.error("Failed to start Courses Service", e);
            System.exit(1);
        }
    }
}

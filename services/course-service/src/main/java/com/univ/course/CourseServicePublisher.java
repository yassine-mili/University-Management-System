// Fichier : CourseServicePublisher.java

package com.univ.course;

import jakarta.xml.ws.Endpoint;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import java.util.HashMap;
import java.util.Map;

public class CourseServicePublisher {

    public static void main(String[] args) {
        // --- 1. Load Configuration from Environment ---
        String dbUrl = System.getenv("DB_URL");
        String dbUser = System.getenv("DB_USER");
        String dbPassword = System.getenv("DB_PASSWORD");
        
        // Default port if not set in environment
        String port = System.getenv("PORT") != null ? System.getenv("PORT") : "8083"; 
        
        final String SERVICE_URL = "http://0.0.0.0:" + port + "/CourseService";
        
        // --- 2. Create Map for JPA Property Overrides ---
        Map<String, String> jpaProperties = new HashMap<>();
        
        // Use default values from persistence.xml if env vars are null
        if (dbUrl != null) jpaProperties.put("jakarta.persistence.jdbc.url", dbUrl);
        if (dbUser != null) jpaProperties.put("jakarta.persistence.jdbc.user", dbUser);
        if (dbPassword != null) jpaProperties.put("jakarta.persistence.jdbc.password", dbPassword);
        
        try {
            // --- 3. Initialize JPA with Dynamic Properties ---
            System.out.println("Tentative de création/mise à jour du schéma de base de données (hbm2ddl.auto=update)...");
            
            // Pass the map of properties to override the persistence.xml settings
            EntityManagerFactory emf = Persistence.createEntityManagerFactory("CourseUnit", jpaProperties);
            emf.close(); // Forces schema update, then closes the factory

            // --- 4. Publish Service ---
            // Pass the JPA properties to the CourseServiceImpl constructor for its EMF initialization (See Step 3)
            Endpoint.publish(SERVICE_URL, new CourseServiceImpl(jpaProperties));
            
            System.out.println("✅ Service Cours SOAP démarré sur: " + SERVICE_URL);
            System.out.println("WSDL accessible à: " + SERVICE_URL + "?wsdl");

        } catch (Exception e) {
            System.err.println("❌ Erreur au démarrage du service Cours: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
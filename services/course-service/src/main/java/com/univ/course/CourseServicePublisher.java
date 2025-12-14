// Fichier : CourseServicePublisher.java

package com.univ.course;

import jakarta.xml.ws.Endpoint;
import jakarta.persistence.Persistence;

public class CourseServicePublisher {

    // URL requise : Port 8083
    private static final String URL = "http://0.0.0.0:8083/CourseService";

    public static void main(String[] args) {
        try {
            // Initialisation de JPA pour s'assurer que le schéma est créé
            System.out.println("Tentative de création/mise à jour du schéma de base de données (hbm2ddl.auto=update)...");
            Persistence.createEntityManagerFactory("CourseUnit").close();

            // Publication du service (sur le port 8083)
            Endpoint.publish(URL, new CourseServiceImpl());
            System.out.println("✅ Service Cours SOAP démarré sur: " + URL);
            System.out.println("WSDL accessible à: " + URL + "?wsdl");

        } catch (Exception e) {
            System.err.println("❌ Erreur au démarrage du service Cours: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
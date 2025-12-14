# Fichier : course-service/Dockerfile

# Étape de construction
FROM eclipse-temurin:17-jdk-alpine AS builder

WORKDIR /app

# Installer Maven pour la construction
RUN apk add --no-cache maven

# Copier et compiler le projet
COPY pom.xml .
COPY src ./src
RUN mvn clean install -DskipTests

# Étape d'exécution (utilisation d'une image JRE plus légère)
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Copier le JAR exécutable
COPY --from=builder /app/target/course-service-1.0.0-SNAPSHOT.jar course-service.jar

# Port d'écoute du service SOAP (8083)
EXPOSE 8083

# Commande de démarrage
ENTRYPOINT ["java", "-jar", "course-service.jar"]
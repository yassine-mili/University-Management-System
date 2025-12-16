FROM maven:3.9.5-eclipse-temurin-17 AS build

WORKDIR /app

# Copy pom.xml and download dependencies
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code
COPY src ./src

# Build the application
RUN mvn clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:17-jre-jammy

WORKDIR /app

# Copy the JAR from build stage
COPY --from=build /app/target/courses-service-jar-with-dependencies.jar /app/courses-service.jar

# Create logs directory
RUN mkdir -p /app/logs

# Expose port 8083
EXPOSE 8083

# Set environment variables
ENV ENVIRONMENT=docker
ENV JAVA_OPTS="-Xms256m -Xmx512m"

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=40s --retries=3 \
    CMD curl -f http://localhost:8083/CourseService?wsdl || exit 1

# Run the application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/courses-service.jar"]

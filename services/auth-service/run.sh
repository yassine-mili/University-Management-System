#!/bin/bash
# Build and run auth-service locally

echo "Building Authentication Service..."
mvn clean package -DskipTests

if [ $? -ne 0 ]; then
    echo "Build failed!"
    exit 1
fi

echo "Starting service on port 8081..."
mvn spring-boot:run

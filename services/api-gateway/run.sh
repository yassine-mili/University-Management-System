#!/bin/bash

echo "===================================="
echo "Building API Gateway"
echo "===================================="

# Check if Maven is installed
if ! command -v mvn &> /dev/null; then
    echo "ERROR: Maven is not installed"
    echo "Please install Maven from https://maven.apache.org/download.cgi"
    exit 1
fi

echo "Building with Maven..."
mvn clean package -DskipTests

if [ $? -eq 0 ]; then
    echo ""
    echo "===================================="
    echo "Build Successful!"
    echo "===================================="
    echo ""
    echo "Starting API Gateway on port 8080..."
    echo ""
    
    # Run the JAR file
    java -jar target/api-gateway-1.0.0.jar
else
    echo ""
    echo "===================================="
    echo "Build Failed!"
    echo "===================================="
    exit 1
fi

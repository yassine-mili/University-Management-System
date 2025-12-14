#!/bin/bash

echo "===================================="
echo "Building Courses Service (SOAP)"
echo "===================================="

# Check if Maven is installed
if ! command -v mvn &> /dev/null; then
    echo "ERROR: Maven is not installed"
    echo "Please install Maven from https://maven.apache.org/download.cgi"
    exit 1
fi

echo "Building with Maven..."
mvn clean package

if [ $? -eq 0 ]; then
    echo ""
    echo "===================================="
    echo "Build Successful!"
    echo "===================================="
    echo ""
    echo "Starting Courses Service on port 8083..."
    echo ""
    
    # Run the JAR file
    java -jar target/courses-service-jar-with-dependencies.jar
else
    echo ""
    echo "===================================="
    echo "Build Failed!"
    echo "===================================="
    exit 1
fi

#!/bin/bash

# Courses Service Build and Run Script

echo "======================================"
echo "University Courses Service Builder"
echo "======================================"

# Function to build the project
build() {
    echo ""
    echo "Building Maven project..."
    mvn clean package -DskipTests
    
    if [ $? -eq 0 ]; then
        echo "✓ Build successful!"
    else
        echo "✗ Build failed!"
        exit 1
    fi
}

# Function to run locally
run_local() {
    echo ""
    echo "Starting service locally..."
    java -jar target/courses-service-jar-with-dependencies.jar
}

# Function to build Docker image
docker_build() {
    echo ""
    echo "Building Docker image..."
    docker build -t courses-service:latest .
    
    if [ $? -eq 0 ]; then
        echo "✓ Docker image built successfully!"
    else
        echo "✗ Docker build failed!"
        exit 1
    fi
}

# Function to run with Docker
docker_run() {
    echo ""
    echo "Running service in Docker..."
    docker run -p 8083:8083 \
        -e ENVIRONMENT=docker \
        -e JAVA_OPTS="-Xms256m -Xmx512m" \
        --name courses-service \
        --rm \
        courses-service:latest
}

# Function to run with Docker Compose
docker_compose_up() {
    echo ""
    echo "Starting service with Docker Compose..."
    cd ../../../docker
    docker-compose up courses_service courses_db
}

# Function to test WSDL
test_wsdl() {
    echo ""
    echo "Testing WSDL accessibility..."
    curl -s http://localhost:8083/CourseService?wsdl | head -n 20
    
    if [ $? -eq 0 ]; then
        echo ""
        echo "✓ WSDL is accessible!"
        echo "Full WSDL at: http://localhost:8083/CourseService?wsdl"
    else
        echo "✗ WSDL not accessible. Is the service running?"
    fi
}

# Function to run all tests
test_all() {
    echo ""
    echo "Running all SOAP test requests..."
    
    for file in test-soap-requests/*.xml; do
        filename=$(basename "$file")
        echo ""
        echo "Testing: $filename"
        echo "--------------------------------------"
        
        curl -X POST http://localhost:8083/CourseService \
            -H "Content-Type: text/xml" \
            -d @"$file" \
            2>/dev/null | head -n 10
        
        echo ""
    done
}

# Main menu
case "$1" in
    build)
        build
        ;;
    run)
        run_local
        ;;
    docker-build)
        docker_build
        ;;
    docker-run)
        docker_run
        ;;
    compose)
        docker_compose_up
        ;;
    test-wsdl)
        test_wsdl
        ;;
    test-all)
        test_all
        ;;
    full)
        build
        docker_build
        docker_compose_up
        ;;
    *)
        echo "Usage: $0 {build|run|docker-build|docker-run|compose|test-wsdl|test-all|full}"
        echo ""
        echo "Commands:"
        echo "  build         - Build Maven project"
        echo "  run           - Run service locally"
        echo "  docker-build  - Build Docker image"
        echo "  docker-run    - Run Docker container"
        echo "  compose       - Start with Docker Compose"
        echo "  test-wsdl     - Test WSDL accessibility"
        echo "  test-all      - Run all SOAP test requests"
        echo "  full          - Build, create Docker image, and start with compose"
        exit 1
        ;;
esac

exit 0

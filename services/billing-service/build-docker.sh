#!/bin/bash
# Billing Service - Docker Build Script

SERVICE_NAME="billing-service"
DB_NAME="billing_db"

if [ "$1" == "down" ]; then
    echo "Stopping services..."
    cd ../..
    docker-compose -f docker/docker-compose.yml down $SERVICE_NAME $DB_NAME
    echo "Services stopped!"
    exit 0
fi

echo "Building Billing Service Docker Image..."

# Build Docker image
docker build -t $SERVICE_NAME .

if [ $? -ne 0 ]; then
    echo "✗ Docker build failed!"
    exit 1
fi

echo "✓ Docker image built successfully!"

if [ "$1" != "--no-run" ]; then
    echo ""
    echo "Starting services with docker-compose..."
    
    cd ../..
    docker-compose -f docker/docker-compose.yml up -d $DB_NAME
    
    echo "Waiting for database to be ready..."
    sleep 10
    
    docker-compose -f docker/docker-compose.yml up -d $SERVICE_NAME
    
    echo ""
    echo "✓ Services started!"
    echo ""
    echo "Service URL: http://localhost:5000/BillingService.asmx"
    echo "WSDL: http://localhost:5000/BillingService.asmx?wsdl"
    echo "Database: localhost:5436"
    echo ""
    echo "View logs with: docker-compose -f docker/docker-compose.yml logs -f $SERVICE_NAME"
fi

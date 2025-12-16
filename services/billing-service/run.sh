#!/bin/bash
# Billing Service - Build and Run Script

echo "Building Billing Service..."
dotnet build

if [ $? -eq 0 ]; then
    echo "✓ Build successful!"
else
    echo "✗ Build failed!"
    exit 1
fi

echo ""
echo "Starting Billing Service..."
echo "Service will be available at: http://localhost:5000"
echo "WSDL: http://localhost:5000/BillingService.asmx?wsdl"
echo ""
echo "Press Ctrl+C to stop the service"
echo ""

dotnet run

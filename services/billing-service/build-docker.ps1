# Billing Service - Docker Build Script

param(
    [switch]$NoBuild,
    [switch]$Down
)

$serviceName = "billing-service"
$dbName = "billing_db"

if ($Down) {
    Write-Host "Stopping services..." -ForegroundColor Yellow
    Set-Location ../..
    docker-compose -f docker/docker-compose.yml down $serviceName $dbName
    Write-Host "Services stopped!" -ForegroundColor Green
    exit 0
}

Write-Host "Building Billing Service Docker Image..." -ForegroundColor Cyan

# Build Docker image
docker build -t $serviceName .

if ($LASTEXITCODE -ne 0) {
    Write-Host "✗ Docker build failed!" -ForegroundColor Red
    exit 1
}

Write-Host "✓ Docker image built successfully!" -ForegroundColor Green

if (-not $NoBuild) {
    Write-Host "`nStarting services with docker-compose..." -ForegroundColor Cyan
    
    Set-Location ../..
    docker-compose -f docker/docker-compose.yml up -d $dbName
    
    Write-Host "Waiting for database to be ready..." -ForegroundColor Yellow
    Start-Sleep -Seconds 10
    
    docker-compose -f docker/docker-compose.yml up -d $serviceName
    
    Write-Host "`n✓ Services started!" -ForegroundColor Green
    Write-Host "`nService URL: http://localhost:5000/BillingService.asmx" -ForegroundColor Yellow
    Write-Host "WSDL: http://localhost:5000/BillingService.asmx?wsdl" -ForegroundColor Yellow
    Write-Host "Database: localhost:5436" -ForegroundColor Yellow
    Write-Host "`nView logs with: docker-compose -f docker/docker-compose.yml logs -f $serviceName" -ForegroundColor Gray
}

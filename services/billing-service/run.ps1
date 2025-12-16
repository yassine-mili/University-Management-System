# Billing Service - Build and Run Scripts

# Build the project
Write-Host "Building Billing Service..." -ForegroundColor Cyan
dotnet build

if ($LASTEXITCODE -eq 0) {
    Write-Host "✓ Build successful!" -ForegroundColor Green
} else {
    Write-Host "✗ Build failed!" -ForegroundColor Red
    exit 1
}

# Run the service
Write-Host "`nStarting Billing Service..." -ForegroundColor Cyan
Write-Host "Service will be available at: http://localhost:5000" -ForegroundColor Yellow
Write-Host "WSDL: http://localhost:5000/BillingService.asmx?wsdl" -ForegroundColor Yellow
Write-Host "`nPress Ctrl+C to stop the service`n" -ForegroundColor Gray

dotnet run

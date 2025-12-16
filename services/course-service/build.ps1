# Courses Service Build and Run Script (PowerShell)

param(
    [Parameter(Mandatory=$false)]
    [ValidateSet('build', 'run', 'docker-build', 'docker-run', 'compose', 'test-wsdl', 'test-all', 'full')]
    [string]$Command
)

Write-Host "======================================" -ForegroundColor Cyan
Write-Host "University Courses Service Builder" -ForegroundColor Cyan
Write-Host "======================================" -ForegroundColor Cyan

function Build-Project {
    Write-Host "`nBuilding Maven project..." -ForegroundColor Yellow
    mvn clean package -DskipTests
    
    if ($LASTEXITCODE -eq 0) {
        Write-Host "✓ Build successful!" -ForegroundColor Green
    } else {
        Write-Host "✗ Build failed!" -ForegroundColor Red
        exit 1
    }
}

function Run-Local {
    Write-Host "`nStarting service locally..." -ForegroundColor Yellow
    java -jar target/courses-service-jar-with-dependencies.jar
}

function Build-DockerImage {
    Write-Host "`nBuilding Docker image..." -ForegroundColor Yellow
    docker build -t courses-service:latest .
    
    if ($LASTEXITCODE -eq 0) {
        Write-Host "✓ Docker image built successfully!" -ForegroundColor Green
    } else {
        Write-Host "✗ Docker build failed!" -ForegroundColor Red
        exit 1
    }
}

function Run-Docker {
    Write-Host "`nRunning service in Docker..." -ForegroundColor Yellow
    docker run -p 8083:8083 `
        -e ENVIRONMENT=docker `
        -e JAVA_OPTS="-Xms256m -Xmx512m" `
        --name courses-service `
        --rm `
        courses-service:latest
}

function Start-Compose {
    Write-Host "`nStarting service with Docker Compose..." -ForegroundColor Yellow
    Push-Location ../../../docker
    docker-compose up courses_service courses_db
    Pop-Location
}

function Test-WSDL {
    Write-Host "`nTesting WSDL accessibility..." -ForegroundColor Yellow
    
    try {
        $response = Invoke-WebRequest -Uri "http://localhost:8083/CourseService?wsdl" -Method Get -TimeoutSec 5
        
        Write-Host "`n✓ WSDL is accessible!" -ForegroundColor Green
        Write-Host "Full WSDL at: http://localhost:8083/CourseService?wsdl" -ForegroundColor Cyan
        Write-Host "`nFirst 20 lines:" -ForegroundColor Yellow
        ($response.Content -split "`n")[0..19] | ForEach-Object { Write-Host $_ }
    }
    catch {
        Write-Host "`n✗ WSDL not accessible. Is the service running?" -ForegroundColor Red
        Write-Host "Error: $_" -ForegroundColor Red
    }
}

function Test-All {
    Write-Host "`nRunning all SOAP test requests..." -ForegroundColor Yellow
    
    $testFiles = Get-ChildItem -Path "test-soap-requests" -Filter "*.xml"
    
    foreach ($file in $testFiles) {
        Write-Host "`nTesting: $($file.Name)" -ForegroundColor Cyan
        Write-Host "--------------------------------------" -ForegroundColor Gray
        
        try {
            $body = Get-Content -Path $file.FullName -Raw
            
            $response = Invoke-WebRequest -Uri "http://localhost:8083/CourseService" `
                -Method POST `
                -ContentType "text/xml" `
                -Body $body `
                -TimeoutSec 10
            
            ($response.Content -split "`n")[0..9] | ForEach-Object { Write-Host $_ }
        }
        catch {
            Write-Host "Error: $_" -ForegroundColor Red
        }
        
        Write-Host ""
    }
}

function Show-Usage {
    Write-Host "`nUsage: .\build.ps1 -Command <command>" -ForegroundColor Yellow
    Write-Host "`nCommands:" -ForegroundColor Cyan
    Write-Host "  build         - Build Maven project" -ForegroundColor White
    Write-Host "  run           - Run service locally" -ForegroundColor White
    Write-Host "  docker-build  - Build Docker image" -ForegroundColor White
    Write-Host "  docker-run    - Run Docker container" -ForegroundColor White
    Write-Host "  compose       - Start with Docker Compose" -ForegroundColor White
    Write-Host "  test-wsdl     - Test WSDL accessibility" -ForegroundColor White
    Write-Host "  test-all      - Run all SOAP test requests" -ForegroundColor White
    Write-Host "  full          - Build, create Docker image, and start with compose" -ForegroundColor White
    Write-Host "`nExample: .\build.ps1 -Command build" -ForegroundColor Yellow
}

# Main execution
switch ($Command) {
    'build' { Build-Project }
    'run' { Run-Local }
    'docker-build' { Build-DockerImage }
    'docker-run' { Run-Docker }
    'compose' { Start-Compose }
    'test-wsdl' { Test-WSDL }
    'test-all' { Test-All }
    'full' {
        Build-Project
        Build-DockerImage
        Start-Compose
    }
    default { Show-Usage }
}

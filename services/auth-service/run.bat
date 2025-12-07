@echo off
REM Build and run auth-service locally

echo Building Authentication Service...
call mvn clean package -DskipTests

if %ERRORLEVEL% NEQ 0 (
    echo Build failed!
    exit /b 1
)

echo Starting service on port 8081...
call mvn spring-boot:run

pause

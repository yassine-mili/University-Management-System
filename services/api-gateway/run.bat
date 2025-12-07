@echo off
echo ====================================
echo Building API Gateway
echo ====================================

REM Check if Maven is installed
where mvn >nul 2>nul
if %ERRORLEVEL% NEQ 0 (
    echo ERROR: Maven is not installed or not in PATH
    echo Please install Maven from https://maven.apache.org/download.cgi
    pause
    exit /b 1
)

echo Building with Maven...
call mvn clean package -DskipTests

if %ERRORLEVEL% EQU 0 (
    echo.
    echo ====================================
    echo Build Successful!
    echo ====================================
    echo.
    echo Starting API Gateway on port 8080...
    echo.
    
    REM Run the JAR file
    java -jar target\api-gateway-1.0.0.jar
) else (
    echo.
    echo ====================================
    echo Build Failed!
    echo ====================================
    pause
    exit /b 1
)

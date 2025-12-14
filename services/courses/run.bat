@echo off
echo ====================================
echo Building Courses Service (SOAP)
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
call mvn clean package

if %ERRORLEVEL% EQU 0 (
    echo.
    echo ====================================
    echo Build Successful!
    echo ====================================
    echo.
    echo Starting Courses Service on port 8083...
    echo.
    
    REM Run the JAR file
    java -jar target\courses-service-jar-with-dependencies.jar
) else (
    echo.
    echo ====================================
    echo Build Failed!
    echo ====================================
    pause
    exit /b 1
)

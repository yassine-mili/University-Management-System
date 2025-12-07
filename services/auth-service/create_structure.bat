@echo off
cd /d "c:\Users\R I B\Desktop\cv projects\University-Management-System\services\auth-service"

REM Create directory structure
mkdir "src\main\java\com\university\authservice\controller" 2>nul
mkdir "src\main\java\com\university\authservice\service" 2>nul
mkdir "src\main\java\com\university\authservice\model" 2>nul
mkdir "src\main\java\com\university\authservice\repository" 2>nul
mkdir "src\main\java\com\university\authservice\config" 2>nul
mkdir "src\main\java\com\university\authservice\dto" 2>nul
mkdir "src\main\java\com\university\authservice\security" 2>nul
mkdir "src\main\java\com\university\authservice\exception" 2>nul
mkdir "src\main\resources" 2>nul
mkdir "src\test\java\com\university\authservice" 2>nul

REM Delete placeholder file
del class.java 2>nul

echo Directory structure created successfully!

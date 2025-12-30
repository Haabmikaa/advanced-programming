@echo off
echo ========================================
echo   DEPLOYING QUIZ WEB APPLICATION
echo ========================================
echo.

REM Set color for output
color 0A
title Quiz Web App Deployment

REM Set variables
set "PROJECT_ROOT=%~dp0.."
set "TOMCAT_HOME=C:\apache-tomcat-9.0.113"
set "MYSQL_HOME=C:\Program Files\MySQL\MySQL Server 8.0"
set "JAVA_HOME=C:\Program Files\Java\jdk-17"
set "WAR_FILE=target\QuizWebApp.war"

echo Step 1: Checking prerequisites...
echo.

REM Check Java
if not exist "%JAVA_HOME%" (
    echo ❌ Java not found at %JAVA_HOME%
    echo Please install Java JDK 17 or update the path in deploy.bat
    pause
    exit /b 1
)
echo ✅ Java found: %JAVA_HOME%

REM Check Maven
where mvn >nul 2>nul
if %errorlevel% neq 0 (
    echo ❌ Maven not found in PATH
    echo Please install Apache Maven and add it to PATH
    pause
    exit /b 1
)
echo ✅ Maven found

REM Check Tomcat
if not exist "%TOMCAT_HOME%" (
    echo ❌ Tomcat not found at %TOMCAT_HOME%
    echo Please install Apache Tomcat 9.x or update the path in deploy.bat
    pause
    exit /b 1
)
echo ✅ Tomcat found: %TOMCAT_HOME%

echo.
echo Step 2: Building project...
cd /d "%PROJECT_ROOT%"

REM Clean and build
echo Cleaning project...
call mvn clean

echo Compiling project...
call mvn compile

echo Packaging WAR file...
call mvn package

if %errorlevel% neq 0 (
    echo ❌ Build failed!
    echo Check the Maven output for errors.
    pause
    exit /b 1
)
echo ✅ Build successful!

echo.
echo Step 3: Deploying to Tomcat...
echo Stopping Tomcat if running...
call "%TOMCAT_HOME%\bin\shutdown.bat" >nul 2>nul

REM Wait for Tomcat to stop
timeout /t 5 /nobreak >nul

REM Remove old deployment
if exist "%TOMCAT_HOME%\webapps\QuizWebApp.war" (
    echo Removing old WAR file...
    del "%TOMCAT_HOME%\webapps\QuizWebApp.war"
)

if exist "%TOMCAT_HOME%\webapps\QuizWebApp" (
    echo Removing old exploded directory...
    rmdir /s /q "%TOMCAT_HOME%\webapps\QuizWebApp"
)

REM Copy new WAR
echo Copying new WAR file...
copy "%PROJECT_ROOT%\%WAR_FILE%" "%TOMCAT_HOME%\webapps\" >nul

if %errorlevel% neq 0 (
    echo ❌ Failed to copy WAR file!
    pause
    exit /b 1
)
echo ✅ WAR file deployed successfully!

echo.
echo Step 4: Starting Tomcat...
echo Starting Tomcat server...
start "Tomcat Server" "%TOMCAT_HOME%\bin\startup.bat"

echo Waiting for Tomcat to start...
timeout /t 10 /nobreak >nul

echo.
echo Step 5: Starting RMI Server...
echo Starting RMI Registry and Server...
start "RMI Server" cmd /c "cd /d "%PROJECT_ROOT%" && java -cp "target\classes;lib\*" com.quizapp.rmi.QuizRMIServer"

echo Waiting for RMI Server to start...
timeout /t 5 /nobreak >nul

echo.
echo Step 6: Initializing Database...
echo Checking database connection...

REM Create database schema if needed
if exist "%PROJECT_ROOT%\database\schema.sql" (
    echo Initializing database schema...
    "%MYSQL_HOME%\bin\mysql" -u root -p < "%PROJECT_ROOT%\database\schema.sql"
)

echo.
echo ========================================
echo   DEPLOYMENT COMPLETE!
echo ========================================
echo.
echo ✅ Application successfully deployed!
echo.
echo 📍 Access URLs:
echo    Web Application: http://localhost:8080/QuizWebApp/
echo    RMI Server:      localhost:1099
echo    Database:        localhost:3306/quizdb
echo.
echo 👥 Test Credentials:
echo    Student:  alice_student / student123
echo    Teacher:  john_teacher / teacher123
echo    Admin:    admin / admin123
echo.
echo 📋 Next steps:
echo    1. Open http://localhost:8080/QuizWebApp/ in your browser
echo    2. Log in with test credentials
echo    3. Explore different user roles
echo    4. Test quiz functionality
echo.
echo Press any key to open the application in your browser...
pause >nul

start http://localhost:8080/QuizWebApp/
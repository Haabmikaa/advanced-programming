@echo off
echo ========================================
echo   FINAL DEPLOYMENT - QUIZ WEB APP
echo ========================================
echo.

cd /d "%~dp0\.."

echo Step 1: Stopping any running services...
taskkill /F /FI "WINDOWTITLE eq RMI Server" >nul 2>&1
taskkill /F /FI "WINDOWTITLE eq RMI Registry" >nul 2>&1
taskkill /F /FI "WINDOWTITLE eq Socket Server" >nul 2>&1
taskkill /F /FI "WINDOWTITLE eq Tomcat Server" >nul 2>&1

echo Step 2: Cleaning and building project...
call mvn clean package

if %errorlevel% neq 0 (
    echo ❌ Build failed!
    pause
    exit /b 1
)

echo.
echo Step 3: Deploying to Tomcat...
set TOMCAT_HOME=C:\apache-tomcat-9.0.85
set WAR_FILE=target\QuizWebApp.war

if exist "%TOMCAT_HOME%\webapps\QuizWebApp.war" (
    echo Removing old deployment...
    del "%TOMCAT_HOME%\webapps\QuizWebApp.war"
    rmdir /s /q "%TOMCAT_HOME%\webapps\QuizWebApp" 2>nul
)

copy "%WAR_FILE%" "%TOMCAT_HOME%\webapps\" >nul
echo ✅ WAR file deployed to Tomcat

echo.
echo Step 4: Starting RMI Registry...
start "RMI Registry" cmd /c "echo Starting RMI Registry... && rmiregistry"

timeout /t 2 /nobreak >nul

echo.
echo Step 5: Starting RMI Server...
start "RMI Server" cmd /k "echo [RMI Server] Starting... && java -cp ""target/classes;C:\Users\%USERNAME%\.m2\repository\mysql\mysql-connector-java\8.0.28\mysql-connector-java-8.0.28.jar;C:\Users\%USERNAME%\.m2\repository\com\google\code\gson\gson\2.9.0\gson-2.9.0.jar"" -Djava.rmi.server.hostname=localhost -Djava.rmi.server.codebase=file:///%CD%/target/classes/ -Djava.security.policy=rmi.policy com.quizapp.rmi.server.RMIServer"

timeout /t 3 /nobreak >nul

echo.
echo Step 6: Starting Socket Server...
start "Socket Server" cmd /k "echo [Socket Server] Starting... && java -cp ""target/classes;C:\Users\%USERNAME%\.m2\repository\mysql\mysql-connector-java\8.0.28\mysql-connector-java-8.0.28.jar"" com.quizapp.networking.QuizSocketServer"

timeout /t 2 /nobreak >nul

echo.
echo Step 7: Starting Tomcat Server...
start "Tomcat Server" "%TOMCAT_HOME%\bin\startup.bat"

timeout /t 5 /nobreak >nul

echo.
echo Step 8: Running Integration Tests...
java -cp "target/classes;C:\Users\%USERNAME%\.m2\repository\mysql\mysql-connector-java\8.0.28\mysql-connector-java-8.0.28.jar;C:\Users\%USERNAME%\.m2\repository\com\google\code\gson\gson\2.9.0\gson-2.9.0.jar;C:\Users\%USERNAME%\.m2\repository\org\apache\commons\commons-dbcp2\2.9.0\commons-dbcp2-2.9.0.jar" com.quizapp.test.IntegrationTest

echo.
echo ========================================
echo   DEPLOYMENT COMPLETE!
echo ========================================
echo.
echo 🌐 APPLICATION URLs:
echo   Web Interface: http://localhost:8080/QuizWebApp/
echo   RMI Server: localhost:1099
echo   Socket Server: localhost:5555
echo.
echo 👥 TEST CREDENTIALS:
echo   Student: alice_student / student123
echo   Teacher: john_teacher / teacher123
echo   Admin: admin / admin123
echo.
echo 🛠️ SERVICES RUNNING:
echo   - Apache Tomcat (Web Server)
echo   - RMI Registry & Server
echo   - Socket Server
echo   - MySQL Database
echo.
echo 📊 TO STOP ALL SERVICES:
echo   Run: scripts\stop_all.bat
echo.
pause
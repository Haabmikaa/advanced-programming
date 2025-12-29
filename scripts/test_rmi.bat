@echo off
echo ========================================
echo   RMI SYSTEM TEST
echo ========================================
echo.

cd /d "%~dp0\.."

echo Step 1: Clean and compile...
call mvn clean compile

if %errorlevel% neq 0 (
    echo ❌ Compilation failed!
    pause
    exit /b 1
)

echo.
echo Step 2: Starting RMI Registry in background...
start "RMI Registry" cmd /k "echo Starting RMI Registry... && rmiregistry"

timeout /t 3 /nobreak > nul

echo.
echo Step 3: Starting RMI Server in background...
start "RMI Server" cmd /k "java -cp target/classes -Djava.rmi.server.hostname=localhost -Djava.rmi.server.codebase=file:///%CD%/target/classes/ -Djava.security.policy=rmi.policy com.quizapp.rmi.server.RMIServer"

echo Waiting for server to start...
timeout /t 5 /nobreak > nul

echo.
echo Step 4: Starting RMI Client Test...
java -cp "target/classes;%HOME%\.m2\repository\mysql\mysql-connector-java\8.0.28\mysql-connector-java-8.0.28.jar;%HOME%\.m2\repository\com\google\code\gson\gson\2.9.0\gson-2.9.0.jar" ^
     -Djava.rmi.server.codebase=file:///%CD%/target/classes/ ^
     -Djava.security.policy=rmi.policy ^
     com.quizapp.rmi.client.RMIClient

echo.
echo Step 5: Cleaning up...
taskkill /F /FI "WINDOWTITLE eq RMI Server" > nul 2>&1
taskkill /F /FI "WINDOWTITLE eq RMI Registry" > nul 2>&1

echo.
echo ✅ RMI Test Completed!
pause
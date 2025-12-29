@echo off
echo ========================================
echo   STARTING RMI SERVER
echo ========================================
echo.

cd /d "%~dp0\.."

echo Step 1: Compiling RMI classes...
call mvn clean compile

echo.
echo Step 2: Starting RMI Registry...
start "RMI Registry" cmd /c "rmiregistry -J-Djava.rmi.server.codebase=file:///%CD%/target/classes/"

timeout /t 2 /nobreak > nul

echo.
echo Step 3: Starting RMI Server...
echo [INFO] RMI Server starting on port 1099...
echo [INFO] Press Ctrl+C in this window to stop the server

java -cp "target/classes;%HOME%\.m2\repository\mysql\mysql-connector-java\8.0.28\mysql-connector-java-8.0.28.jar;%HOME%\.m2\repository\com\google\code\gson\gson\2.9.0\gson-2.9.0.jar" ^
     -Djava.rmi.server.hostname=localhost ^
     -Djava.rmi.server.codebase=file:///%CD%/target/classes/ ^
     -Djava.security.policy=rmi.policy ^
     com.quizapp.rmi.server.RMIServer

pause
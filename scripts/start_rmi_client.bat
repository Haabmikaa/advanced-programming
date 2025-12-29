@echo off
echo ========================================
echo   STARTING RMI CLIENT
echo ========================================
echo.

cd /d "%~dp0\.."

echo Step 1: Compiling if needed...
call mvn compile

echo.
echo Step 2: Starting RMI Client...
echo [INFO] Connecting to RMI Server at rmi://localhost:1099/QuizRemoteService

java -cp "target/classes;%HOME%\.m2\repository\mysql\mysql-connector-java\8.0.28\mysql-connector-java-8.0.28.jar;%HOME%\.m2\repository\com\google\code\gson\gson\2.9.0\gson-2.9.0.jar" ^
     -Djava.rmi.server.codebase=file:///%CD%/target/classes/ ^
     -Djava.security.policy=rmi.policy ^
     com.quizapp.rmi.client.RMIClient

pause
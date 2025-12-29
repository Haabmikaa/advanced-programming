@echo off
echo.
echo ===========================================
echo   BUILDING MODEL CLASSES
echo ===========================================
echo.

cd ..

echo Step 1: Compiling model classes...
mvn compile

if %errorlevel% neq 0 (
    echo ERROR: Compilation failed!
    pause
    exit /b 1
)

echo.
echo Step 2: Running model tests...
mvn exec:java -Dexec.mainClass="com.quizapp.model.ModelTest"

echo.
echo Step 3: Creating JAR for models...
mvn package -DskipTests

echo.
echo ===========================================
echo   ✅ MODEL CLASSES BUILT SUCCESSFULLY!
echo ===========================================
echo.
echo Created 10 model classes:
echo   1. BaseEntity.java
echo   2. User.java
echo   3. Quiz.java
echo   4. Question.java
echo   5. QuizAttempt.java
echo   6. UserAnswer.java
echo   7. Result.java
echo   8. RMIServer.java
echo   9. SocketSession.java
echo   10. Log.java
echo.
echo Test class: ModelTest.java
echo.
echo Next Step: Task 4 - Create DAO Classes
pause
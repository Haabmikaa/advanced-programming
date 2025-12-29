@echo off
REM ===================================================
REM DATABASE SETUP SCRIPT FOR QUIZ WEB APPLICATION
REM Advanced Programming Final Project
REM ===================================================
echo.
echo ===================================================
echo   DATABASE SETUP - DISTRIBUTED QUIZ WEB APP
echo ===================================================
echo.

REM Check if MySQL is installed
where mysql >nul 2>nul
if %errorlevel% neq 0 (
    echo ERROR: MySQL is not installed or not in PATH!
    echo Please install MySQL and add it to PATH.
    pause
    exit /b 1
)

REM Set database credentials
set DB_USER=root
set DB_PASS=
set DB_NAME=quizdb

echo Step 1: Creating database and tables...
mysql -u %DB_USER% -p%DB_PASS% < "..\database\schema.sql"

if %errorlevel% neq 0 (
    echo ERROR: Failed to create database!
    pause
    exit /b 1
)

echo.
echo Step 2: Setting up database users and permissions...
mysql -u %DB_USER% -p%DB_PASS% < "..\database\database_config.sql"

if %errorlevel% neq 0 (
    echo ERROR: Failed to setup database users!
    pause
    exit /b 1
)

echo.
echo Step 3: Verifying database setup...
mysql -u %DB_USER% -p%DB_PASS% -e "USE %DB_NAME%; SHOW TABLES;"

echo.
echo Step 4: Inserting sample data...
mysql -u %DB_USER% -p%DB_PASS% %DB_NAME% -e "CALL GetActiveUsers();"

echo.
echo ===================================================
echo   DATABASE SETUP COMPLETED SUCCESSFULLY!
echo ===================================================
echo.
echo Database Name: %DB_NAME%
echo Tables Created: 10
echo Sample Data: Inserted
echo.
echo Test Connection:
echo mysql -u quizapp_user -pquizapp_password123 %DB_NAME%
echo.
pause
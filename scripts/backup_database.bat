@echo off
REM ===================================================
REM DATABASE BACKUP SCRIPT
REM ===================================================
echo.
echo Creating database backup...

set BACKUP_DIR=backups
set DB_NAME=quizdb
set DB_USER=quizapp_backup
set DB_PASS=backup_password123
set TIMESTAMP=%DATE:~10,4%%DATE:~4,2%%DATE:~7,2%_%TIME:~0,2%%TIME:~3,2%

REM Create backup directory if it doesn't exist
if not exist "%BACKUP_DIR%" mkdir "%BACKUP_DIR%"

REM Create backup filename
set BACKUP_FILE=%BACKUP_DIR%\quizdb_backup_%TIMESTAMP%.sql

echo Backup file: %BACKUP_FILE%

REM Perform backup
mysqldump -u %DB_USER% -p%DB_PASS% --routines --triggers --events %DB_NAME% > "%BACKUP_FILE%"

if %errorlevel% equ 0 (
    echo Backup completed successfully!
    echo File: %BACKUP_FILE%
) else (
    echo Backup failed!
    pause
    exit /b 1
)

REM Compress backup (optional)
REM "C:\Program Files\7-Zip\7z.exe" a "%BACKUP_FILE%.zip" "%BACKUP_FILE%"

echo.
pause
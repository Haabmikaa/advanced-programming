@echo off
echo ========================================
echo   STOPPING ALL QUIZ APP SERVICES
echo ========================================
echo.

echo Stopping Tomcat...
call "C:\apache-tomcat-9.0.85\bin\shutdown.bat" >nul 2>&1

echo Stopping RMI and Socket servers...
taskkill /F /FI "WINDOWTITLE eq RMI Server" >nul 2>&1
taskkill /F /FI "WINDOWTITLE eq RMI Registry" >nul 2>&1
taskkill /F /FI "WINDOWTITLE eq Socket Server" >nul 2>&1
taskkill /F /FI "WINDOWTITLE eq Quiz*" >nul 2>&1

echo.
echo ✅ All services stopped!
timeout /t 2 >nul
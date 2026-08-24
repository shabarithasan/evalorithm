@echo off
title EVALORITHM - Full Stack Launcher
echo =====================================
echo   Starting EVALORITHM
echo =====================================
echo.

:: Start Backend (Spring Boot)
echo [1/2] Starting Backend (port 8080)...
start "EVALORITHM-Backend" cmd /c "java -jar "%~dp0backend\target\evalorithm-backend-1.0.0.jar""

:: Wait for backend to initialize
echo        Waiting 40 seconds for backend to start...
timeout /t 40 /nobreak >nul

:: Start Frontend (React)
echo [2/2] Starting Frontend (port 3000)...
start "EVALORITHM-Frontend" cmd /c "cd /d "%~dp0frontend" && npm start"

echo.
echo =====================================
echo   Frontend : http://localhost:3000
echo   Backend  : http://localhost:8080/api
echo =====================================
echo.
echo Close this window to stop both servers.
pause

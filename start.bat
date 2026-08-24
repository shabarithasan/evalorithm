@echo off
echo =============================
echo  Starting EVALORITHM Project
echo =============================
echo.

:: Backend start
echo [1/2] Starting Backend (Spring Boot on port 8080)...
start "EVALORITHM-Backend" cmd /c "java -jar backend\target\evalorithm-backend-1.0.0.jar & pause"

:: Wait for backend to initialize
timeout /t 30 /nobreak >nul

:: Frontend start
echo [2/2] Starting Frontend (React on port 3000)...
cd frontend
start "EVALORITHM-Frontend" cmd /c "npm start & pause"
cd ..

echo.
echo =============================
echo  EVALORITHM is starting up!
echo  Frontend: http://localhost:3000
echo  Backend:  http://localhost:8080/api
echo =============================
pause

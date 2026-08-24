#!/bin/bash
# EVALORITHM - Full Stack Launcher (for Git Bash / WSL)
echo "====================================="
echo "  Starting EVALORITHM"
echo "====================================="

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"

echo "[1/2] Starting Backend (port 8080)..."
java -jar "$SCRIPT_DIR/backend/target/evalorithm-backend-1.0.0.jar" &
BACKEND_PID=$!

echo "      Waiting 40 seconds for backend to start..."
sleep 40

echo "[2/2] Starting Frontend (port 3000)..."
cd "$SCRIPT_DIR/frontend" && npm start &
FRONTEND_PID=$!

echo ""
echo "====================================="
echo "  Frontend : http://localhost:3000"
echo "  Backend  : http://localhost:8080/api"
echo "====================================="
echo ""
echo "Press Ctrl+C to stop both servers"

trap "kill $BACKEND_PID $FRONTEND_PID 2>/dev/null; exit" SIGINT SIGTERM
wait

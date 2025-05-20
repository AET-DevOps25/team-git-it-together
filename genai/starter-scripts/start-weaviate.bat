@echo off
SETLOCAL ENABLEDELAYEDEXPANSION

REM Change directory to where this script—and docker-compose.yml—live
cd /d %~dp0

echo 🛠️  Starting Weaviate (weaviate-genai-dev)...
docker compose up -d

echo.
echo ✅ Weaviate container 'weaviate-genai-dev' is up!
echo    • HTTP: http://localhost:1234
echo    • gRPC: localhost:50051
echo.
echo 🔗 Check readiness:
echo    curl -i http://localhost:1234/v1/.well-known/ready
echo.
echo 🛠️  To stop & remove:
echo    docker stop weaviate-genai-dev
echo    docker rm weaviate-genai-dev
echo.

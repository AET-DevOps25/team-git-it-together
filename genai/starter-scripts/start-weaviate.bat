@echo off
setlocal enabledelayedexpansion

REM Move into the directory where the script lives
cd /d "%~dp0"

set "CONTAINER_NAME=weaviate-genai-dev"
set "IMAGE_NAME=semitechnologies/weaviate:1.24.11"
set "HTTP_PORT=1234"
set "GRPC_PORT=50051"

echo 🛠️  Starting Weaviate (%CONTAINER_NAME%)...

REM Check if the container exists and is running
for /f %%i in ('docker ps -q -f "name=^%CONTAINER_NAME%^"') do set CONTAINER_ID=%%i

if defined CONTAINER_ID (
    echo ↻ Container '%CONTAINER_NAME%' is already running.
) else (
    REM Check if container exists but stopped
    set CONTAINER_ID=
    for /f %%i in ('docker ps -aq -f "name=^%CONTAINER_NAME%^"') do set CONTAINER_ID=%%i
    if defined CONTAINER_ID (
        echo ↻ Starting existing container '%CONTAINER_NAME%'...
        docker start "%CONTAINER_NAME%"
    ) else (
        echo 🚀 Creating & starting container '%CONTAINER_NAME%'...
        docker run -d ^
            --name "%CONTAINER_NAME%" ^
            -p "%HTTP_PORT%:%SERVICE_PORT%" ^
            -p "%GRPC_PORT%:50051" ^
            -e QUERY_DEFAULTS_LIMIT=25 ^
            -e AUTHENTICATION_ANONYMOUS_ACCESS_ENABLED=true ^
            -e PERSISTENCE_DATA_PATH=/var/lib/weaviate ^
            -e DEFAULT_VECTORIZER_MODULE=none ^
            "%IMAGE_NAME%"
    )
)

echo.
echo ✅ Weaviate container '%CONTAINER_NAME%' is up!
echo    • HTTP: http://localhost:%HTTP_PORT%
echo    • gRPC: localhost:%GRPC_PORT%
echo.
echo 🔗 Check readiness:
echo    curl -i http://localhost:%HTTP_PORT%/v1/.well-known/ready
echo.
echo 🛠️  To stop & remove:
echo    docker stop %CONTAINER_NAME%
echo    docker rm %CONTAINER_NAME%
echo.

endlocal
pause
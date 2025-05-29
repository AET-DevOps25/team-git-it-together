@echo off
setlocal

REM ── Configuration ────────────────────────────────
set "CONTAINER_NAME=mongo-dev"
set "VOLUME_NAME=mongo-dev-data"
set "IMAGE_NAME=mongo:7.0"
set "DB_NAME=skillforge_dev"
set "DB_PORT=27017"
REM ─────────────────────────────────────────────────

REM Check if container exists
for /f "delims=" %%i in ('docker ps -aq -f "name=%CONTAINER_NAME%"') do set "CID=%%i"

if defined CID (
  echo ↻ Starting existing container %CONTAINER_NAME%...
  docker start %CONTAINER_NAME%
) else (
  echo 🚀 Creating & starting container %CONTAINER_NAME%...
  docker run -d --name %CONTAINER_NAME% ^
    -p %DB_PORT%:27017 ^
    -e MONGO_INITDB_DATABASE=%DB_NAME% ^
    -e MONGO_INITDB_ROOT_USERNAME=root ^
    -e MONGO_INITDB_ROOT_PASSWORD=root ^
    -v %VOLUME_NAME%:/data/db ^
    %IMAGE_NAME%
)

echo.
echo ✅ MongoDB is running at mongodb://localhost:%DB_PORT%/%DB_NAME%
endlocal

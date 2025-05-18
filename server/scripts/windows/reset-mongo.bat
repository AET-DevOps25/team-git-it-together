@echo off
setlocal

set "CONTAINER_NAME=mongo-dev"
set "VOLUME_NAME=mongo-dev-data"

echo 🔄 Resetting MongoDB development environment...

REM Stop & remove container if exists
for /f "delims=" %%i in ('docker ps -aq -f "name=%CONTAINER_NAME%"') do set "CID=%%i"
if defined CID (
  docker stop %CONTAINER_NAME% >nul 2>&1
  docker rm %CONTAINER_NAME%
  echo ✔ Removed container %CONTAINER_NAME%
) else (
  echo ℹ️  No container %CONTAINER_NAME% to remove.
)

REM Remove volume if exists
for /f "delims=" %%v in ('docker volume ls -q -f "name=%VOLUME_NAME%"') do set "VID=%%v"
if defined VID (
  docker volume rm %VOLUME_NAME%
  echo ✔ Removed volume %VOLUME_NAME%
) else (
  echo ℹ️  No volume %VOLUME_NAME% to remove.
)

echo.
echo ✅ Reset complete.
endlocal

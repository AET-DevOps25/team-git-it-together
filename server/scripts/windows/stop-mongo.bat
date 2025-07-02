@echo off
setlocal

set "CONTAINER_NAME=mongo-dev"

for /f "delims=" %%i in ('docker ps -q -f "name=%CONTAINER_NAME%"') do set "CID=%%i"

if defined CID (
  echo 🛑 Stopping container %CONTAINER_NAME%...
  docker stop %CONTAINER_NAME%
) else (
  echo ℹ️  Container %CONTAINER_NAME% is not running.
)

endlocal

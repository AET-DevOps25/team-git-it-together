@echo off
setlocal enabledelayedexpansion

REM ── Load environment ───────────────────────────────
if exist .env.dev (
  echo 📦 Loading environment from .env.dev...
  for /f "usebackq tokens=1,* delims==" %%A in (".env.dev") do (
    if not "%%A"=="" if not "%%A"=="#" (
      set "%%A=%%B"
    )
  )
) else (
  echo ⚠️  .env.dev not found — using defaults.
)

REM ── Defaults if not set ────────────────────────────
if not defined SPRING_PROFILES_ACTIVE set "SPRING_PROFILES_ACTIVE=dev"
if not defined APP_NAME               set "APP_NAME=skill-forge-server"

REM ── Run Spring Boot ───────────────────────────────
echo ▶️  Running %APP_NAME% with Spring profile '%SPRING_PROFILES_ACTIVE%'
gradlew.bat bootRun -Dspring.profiles.active=%SPRING_PROFILES_ACTIVE%

endlocal

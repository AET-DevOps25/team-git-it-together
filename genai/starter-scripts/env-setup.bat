@echo off
SETLOCAL EnableDelayedExpansion

REM ─── Config ─────────────────────────────────────────────────────────
set "ENV_NAME=genai-devops25"
set "ENV_FILE=environment.yml"
set "REQ_FILE=requirements.txt"

REM ─── Usage check ─────────────────────────────────────────────────────
if "%~1"=="" goto usage
set "MODE=%~1"

REM ─── Conda branch ────────────────────────────────────────────────────
if /I "%MODE%"=="conda" (
    REM Check for conda
    where conda >nul 2>nul
    if ERRORLEVEL 1 (
        echo ❌  Conda not found. Please install Miniconda or Anaconda.
        exit /b 1
    )

    REM Verify environment.yml exists
    if not exist "%ENV_FILE%" (
        echo ❌  "%ENV_FILE%" not found in project root.
        exit /b 1
    )

    REM Create or update env
    conda env list | findstr /R /C:"^%ENV_NAME% " >nul
    if NOT ERRORLEVEL 1 (
        echo ℹ️   Conda env '%ENV_NAME%' already exists. Updating…
        conda env update -n "%ENV_NAME%" -f "%ENV_FILE%"
    ) else (
        echo 📦  Creating Conda env '%ENV_NAME%' from '%ENV_FILE%'…
        conda env create -n "%ENV_NAME%" -f "%ENV_FILE%"
    )

    echo ⚠️   If 'conda activate' fails, run: conda init && restart your shell
    echo ✅  Then: conda activate %ENV_NAME%
    exit /b 0
)

REM ─── venv branch ─────────────────────────────────────────────────────
if /I "%MODE%"=="venv" (
    REM Verify requirements.txt exists
    if not exist "%REQ_FILE%" (
        echo ❌  "%REQ_FILE%" not found in project root.
        exit /b 1
    )

    REM Create or reuse .venv
    if exist ".venv\" (
        echo ℹ️   Virtualenv '.venv' already exists. Skipping creation.
    ) else (
        echo 📦  Creating Python venv at '.venv'…
        python -m venv .venv
    )

    echo 📦  Activating venv and installing packages…
    call ".venv\Scripts\activate.bat"
    python -m pip install --upgrade pip
    pip install -r "%REQ_FILE%"

    echo ✅  Virtual environment ready.
    echo 📝  To activate later: call .venv\Scripts\activate.bat
    exit /b 0
)

REM ─── Usage message ──────────────────────────────────────────────────
:usage
echo Usage: %~nx0 [conda^|venv]
echo.
echo   conda   Create or update Conda env named '%ENV_NAME%' using '%ENV_FILE%'
echo   venv    Create or update Python venv at '.venv' using '%REQ_FILE%'
exit /b 1

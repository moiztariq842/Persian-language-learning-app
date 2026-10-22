@echo off
setlocal
cd /d "%~dp0"

REM ========================================================
REM Run Script for Persian Language Learning App
REM Launches the unified Swing Desktop Application
REM Auto-detects MySQL or falls back to In-Memory mode
REM Pass --no-db to force In-Memory mode without database
REM ========================================================

if not exist "bin\com\persianapp\presentationlayer\MainApp.class" (
    echo [INFO] Building project...
    call build.bat
    if errorlevel 1 (
        echo [ERROR] Build failed!
        pause
        exit /b 1
    )
)

java "-Dfile.encoding=UTF-8" -cp "bin;lib\*" com.persianapp.presentationlayer.MainApp %*
if errorlevel 1 (
    echo.
    echo [ERROR] Application exited with an error.
    pause
)

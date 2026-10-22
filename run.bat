@echo off
REM ========================================================
REM Run Script for Persian Language Learning App
REM Launches the unified Swing Desktop Application
REM Auto-detects MySQL or falls back to In-Memory mode
REM Pass --no-db to force In-Memory mode without database
REM ========================================================

if not exist bin\com (
    echo Building project...
    call build.bat
    if %ERRORLEVEL% NEQ 0 exit /b %ERRORLEVEL%
)

java -Dfile.encoding=UTF-8 -cp "bin;lib\*" com.persianapp.presentationlayer.MainApp %*

@echo off
REM ========================================================
REM Run Script for Persian Language Learning App
REM Launches the application in In-Memory Demo Mode
REM (Runs 100% standalone without requiring MySQL/MariaDB)
REM ========================================================

if not exist bin\com (
    echo Building project...
    call build.bat
    if %ERRORLEVEL% NEQ 0 exit /b %ERRORLEVEL%
)

echo Starting Persian Language Learning App in In-Memory Mode (No DB)...
java -Dfile.encoding=UTF-8 -cp "bin;lib\*" com.persianapp.presentationlayer.MainApp --no-db

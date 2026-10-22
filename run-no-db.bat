@echo off
setlocal
cd /d "%~dp0"

echo ========================================================
echo  Persian Language Learning App (In-Memory / No DB)
echo ========================================================

if not exist "bin\com\persianapp\presentationlayer\MainApp.class" (
    echo [INFO] Compiling project...
    call build.bat
    if errorlevel 1 (
        echo [ERROR] Compilation failed!
        pause
        exit /b 1
    )
)

echo [INFO] Launching Persian Language Learning App in In-Memory Mode...
java "-Dfile.encoding=UTF-8" -cp "bin;lib\*" com.persianapp.presentationlayer.MainApp --no-db
if errorlevel 1 (
    echo.
    echo [ERROR] Application exited with error code %ERRORLEVEL%
    pause
)

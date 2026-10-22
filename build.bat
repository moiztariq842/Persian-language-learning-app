@echo off
REM ========================================================
REM Build Script for Persian Language Learning App
REM Compiles all four use cases (UC-01 to UC-04) to bin\
REM ========================================================

echo Cleaning bin directory...
if exist bin rd /s /q bin
mkdir bin

echo Finding source files...
if exist sources.txt del sources.txt
powershell -NoProfile -Command "if (Test-Path src) { (Get-ChildItem -Recurse -Filter *.java src | Resolve-Path -Relative) -replace '\\', '/' | Out-File -Encoding ASCII sources.txt }"

if not exist sources.txt (
    echo [INFO] No Java source files to compile.
    exit /b 0
)

echo Compiling Java source files...
javac -encoding UTF-8 -cp "lib\*" -d bin @sources.txt
set COMPILE_STATUS=%ERRORLEVEL%
if exist sources.txt del sources.txt

if %COMPILE_STATUS% NEQ 0 (
    echo [ERROR] Compilation failed!
    exit /b %COMPILE_STATUS%
)

REM Copy resources to bin for runtime resource loading
if exist resources (
    xcopy /y /e /i resources bin\resources >nul 2>&1
    copy /y resources\* bin\ >nul 2>&1
)

echo [SUCCESS] Build completed successfully.

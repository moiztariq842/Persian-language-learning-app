@echo off
REM ========================================================
REM Build Script for Persian Language Learning App
REM Compiles pure Java source files to bin\
REM ========================================================

echo Cleaning bin directory...
if exist bin rd /s /q bin
mkdir bin

echo Compiling Java source files...
javac -encoding UTF-8 -cp "lib\*" -d bin src\com\persianapp\domain\*.java src\com\persianapp\application\*.java src\com\persianapp\infrastructure\*.java src\com\persianapp\presentation\*.java

if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] Compilation failed!
    exit /b %ERRORLEVEL%
)

echo [SUCCESS] Build completed successfully.

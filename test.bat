@echo off
REM ========================================================
REM Test Script for Persian Language Learning App
REM Compiles tests and runs JUnit 5 Console Standalone Runner
REM ========================================================

echo Compiling test files...
javac -encoding UTF-8 -cp "bin;lib\*" -d bin test\com\persianapp\domain\*.java test\com\persianapp\application\stubs\*.java test\com\persianapp\application\*.java test\com\persianapp\infrastructure\*.java

if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] Test compilation failed!
    exit /b %ERRORLEVEL%
)

echo Running JUnit 5 tests...
java -jar lib\junit-platform-console-standalone-1.10.2.jar --class-path bin --scan-class-path

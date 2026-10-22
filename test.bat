@echo off
REM ========================================================
REM Test Script for Persian Language Learning App
REM Compiles tests and runs JUnit 5 Console Standalone Runner
REM ========================================================

echo Finding test source files...
if exist test_sources.txt del test_sources.txt
powershell -NoProfile -Command "if (Test-Path test) { (Get-ChildItem -Recurse -Filter *.java test | Resolve-Path -Relative) -replace '\\', '/' | Out-File -Encoding ASCII test_sources.txt }"

if not exist test_sources.txt (
    echo [INFO] No test files to compile.
    exit /b 0
)

echo Compiling test files...
javac -encoding UTF-8 -cp "bin;lib\*" -d bin @test_sources.txt
set TEST_COMPILE_STATUS=%ERRORLEVEL%
if exist test_sources.txt del test_sources.txt

if %TEST_COMPILE_STATUS% NEQ 0 (
    echo [ERROR] Test compilation failed!
    exit /b %TEST_COMPILE_STATUS%
)

echo Running JUnit 5 tests...
java -Dfile.encoding=UTF-8 -jar lib\junit-platform-console-standalone-1.10.2.jar --class-path "bin;lib\mariadb-java-client-3.3.3.jar" --scan-class-path

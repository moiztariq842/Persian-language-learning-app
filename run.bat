@echo off
REM ========================================================
REM Run Script for Persian Language Learning App
REM Launches the unified Swing Desktop Application
REM Requires MySQL/MariaDB running on localhost:3306
REM ========================================================

java -Dfile.encoding=UTF-8 -cp "bin;lib\*" com.persianapp.presentationlayer.MainApp

@echo off
echo 🚀 Starting LIMS Application...
echo ===============================

REM Check if bin directory exists
if not exist bin (
    echo ❌ Application not built! Please run build.bat first.
    pause
    exit /b 1
)

REM Run the application
echo 🔐 Starting LIMS Login...
java -cp "bin;lib/*" LIMSLoginUI

if %ERRORLEVEL% NEQ 0 (
    echo ❌ Application failed to start!
    pause
)

@echo off
echo ========================================
echo    LIMS - Simple Login System
echo ========================================
echo.
echo Starting LIMS...
echo.

cd /d "%~dp0"

java -cp "bin;lib/*" LIMSLoginUI

if %ERRORLEVEL% NEQ 0 (
    echo.
    echo Error starting LIMS. Trying alternative...
    java -cp "bin;src;lib/*" LIMSLoginUI
)

echo.
echo LIMS session ended.
pause

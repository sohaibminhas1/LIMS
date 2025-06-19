@echo off
echo ========================================
echo    LIMS - Simple Run (No Compilation)
echo ========================================
echo.
echo Running LIMS using existing compiled classes...
echo.

cd /d "%~dp0"

echo Starting LIMS Login...
java -cp "bin;src" LIMSLoginUI

if %ERRORLEVEL% NEQ 0 (
    echo.
    echo ERROR: Failed to start LIMS!
    echo Trying alternative classpath...
    java -cp "bin" LIMSLoginUI
)

echo.
echo LIMS session ended.
pause

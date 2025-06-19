@echo off
echo ========================================
echo    LIMS - Simple Login System
echo ========================================
echo.
echo Starting bulletproof authentication...
echo No complex systems - Direct database access
echo.

cd /d "%~dp0"

echo Compiling Java files...
javac -cp ".;lib/*" src/*.java src/model/*.java src/service/*.java src/controller/*.java src/dao/*.java src/utils/*.java src/ui/*.java

if %ERRORLEVEL% NEQ 0 (
    echo.
    echo ERROR: Compilation failed!
    pause
    exit /b 1
)

echo.
echo Starting LIMS with Simple Authentication...
java -cp ".;lib/*" SimpleLIMSLauncher

if %ERRORLEVEL% NEQ 0 (
    echo.
    echo ERROR: Failed to start LIMS!
    pause
    exit /b 1
)

echo.
echo LIMS session ended.
pause

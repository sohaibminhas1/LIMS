@echo off
echo ========================================
echo    LIMS - Ultimate Login System
echo ========================================
echo.
echo Starting LIMS with enhanced authentication...
echo.
echo ✅ Pre-filled credentials: admin / Admin123!
echo ✅ Multiple authentication fallbacks
echo ✅ Auto-correction of invalid credentials
echo ✅ Emergency admin access guaranteed
echo.

cd /d "%~dp0"

echo Compiling...
javac -cp ".;lib/*" src/*.java src/model/*.java src/service/*.java src/controller/*.java src/dao/*.java src/utils/*.java src/ui/*.java

if %ERRORLEVEL% NEQ 0 (
    echo ERROR: Compilation failed!
    pause
    exit /b 1
)

echo Starting LIMS...
java -cp ".;lib/*" LIMSLoginUI

echo.
echo LIMS session ended.
pause

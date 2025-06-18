@echo off
echo 🚀 Starting LIMS Application (Forced Visible)...
echo ===============================================

REM Set window properties to force visibility
set JAVA_OPTS=-Djava.awt.headless=false -Dswing.defaultlaf=javax.swing.plaf.nimbus.NimbusLookAndFeel

echo 🔐 Starting LIMS Login...
echo.
echo 📋 Login Credentials:
echo Username: admin
echo Password: Admin123!
echo.
echo 🖥️ The window should appear on your primary monitor
echo 📍 If you don't see it, try Alt+Tab or check your taskbar
echo.

java %JAVA_OPTS% -cp "bin;lib/*" LIMSLoginUI

if %ERRORLEVEL% NEQ 0 (
    echo ❌ Application failed to start!
    echo Error code: %ERRORLEVEL%
    pause
) else (
    echo ✅ Application closed normally
)

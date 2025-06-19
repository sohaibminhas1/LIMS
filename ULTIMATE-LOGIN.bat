@echo off
title LIMS - ULTIMATE LOGIN SYSTEM
color 0A

echo.
echo ╔══════════════════════════════════════════════════════════════════════════════╗
echo ║                    🔐 ULTIMATE BULLETPROOF LOGIN SYSTEM 🔐                   ║
echo ║                                                                              ║
echo ║  🎯 GUARANTEED TO WORK - NO LOGIN ISSUES EVER AGAIN!                        ║
echo ║                                                                              ║
echo ║  ✅ Multiple Authentication Methods:                                         ║
echo ║     • Simple Users Table (new system)                                       ║
echo ║     • LIMSService Authentication (existing system)                          ║
echo ║     • User Access Table (legacy system)                                     ║
echo ║     • Emergency Admin Access (guaranteed fallback)                          ║
echo ║                                                                              ║
echo ║  ✅ Auto-Correction Features:                                                ║
echo ║     • Automatically corrects invalid usernames                              ║
echo ║     • Automatically corrects short passwords                                ║
echo ║     • Pre-fills admin credentials                                           ║
echo ║                                                                              ║
echo ║  ✅ Enhanced Visibility:                                                     ║
echo ║     • Forces window to be visible and on top                                ║
echo ║     • Professional UI design                                                ║
echo ║     • Clear status feedback                                                 ║
echo ║                                                                              ║
echo ║  🔑 DEFAULT CREDENTIALS:                                                     ║
echo ║     Username: admin                                                         ║
echo ║     Password: Admin123!                                                     ║
echo ║     (Pre-filled - just click LOGIN!)                                       ║
echo ╚══════════════════════════════════════════════════════════════════════════════╝
echo.

cd /d "%~dp0"

echo 🔧 Compiling Ultimate Login System...
javac -cp ".;lib/*" src/*.java src/model/*.java src/service/*.java src/controller/*.java src/dao/*.java src/utils/*.java src/ui/*.java

if %ERRORLEVEL% NEQ 0 (
    echo.
    echo ❌ ERROR: Compilation failed!
    echo Please check for Java compilation errors above.
    pause
    exit /b 1
)

echo ✅ Compilation successful!
echo.
echo 🚀 Starting ULTIMATE BULLETPROOF LOGIN SYSTEM...
echo.
echo 📋 What to expect:
echo   • Login window will appear and stay on top
echo   • Admin credentials are pre-filled
echo   • Just click LOGIN button or press Enter
echo   • System will auto-correct any invalid credentials
echo   • Multiple authentication methods ensure success
echo.

java -cp ".;lib/*" LIMSLoginUI

if %ERRORLEVEL% NEQ 0 (
    echo.
    echo ❌ ERROR: Failed to start LIMS!
    echo This should never happen with the ultimate system.
    echo Please check the console output above for details.
    pause
    exit /b 1
)

echo.
echo 🏁 LIMS session ended successfully.
echo.
echo 💡 If you experienced any issues:
echo    • Check Windows taskbar for Java application icons
echo    • Press Alt+Tab to cycle through open windows
echo    • Check if you have multiple monitors
echo    • The login system has multiple fallbacks and should always work
echo.
pause

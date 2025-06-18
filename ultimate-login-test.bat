@echo off
echo 🛡️ ULTIMATE LIMS LOGIN TEST
echo ===========================
echo.
echo This script will test authentication in multiple ways:
echo.

:MENU
echo 📋 Choose your test method:
echo.
echo 1. 🔧 Run Authentication Diagnostic
echo 2. 🚀 Standard LIMS Login (Enhanced)
echo 3. 🛡️ Bulletproof Login UI
echo 4. 🧪 All Tests (Comprehensive)
echo 5. ❌ Exit
echo.
set /p choice="Enter your choice (1-5): "

if "%choice%"=="1" goto DIAGNOSTIC
if "%choice%"=="2" goto STANDARD
if "%choice%"=="3" goto BULLETPROOF
if "%choice%"=="4" goto ALL_TESTS
if "%choice%"=="5" goto EXIT
goto MENU

:DIAGNOSTIC
echo.
echo 🔧 Running Authentication Diagnostic...
echo ========================================
java -cp "bin;lib/*;src" AuthenticationDiagnostic
echo.
echo 📊 Diagnostic completed! Check the results above.
echo.
pause
goto MENU

:STANDARD
echo.
echo 🚀 Starting Standard LIMS Login (Enhanced)...
echo ===============================================
echo.
echo 📋 Login Credentials:
echo Username: admin
echo Password: Admin123!
echo.
echo 🖥️ The login window should appear and be visible
echo 📍 Window will be on top for 3 seconds, then normal
echo.
java -cp "bin;lib/*" LIMSLoginUI
echo.
echo ✅ Standard login session ended.
echo.
pause
goto MENU

:BULLETPROOF
echo.
echo 🛡️ Starting Bulletproof Login UI...
echo ====================================
echo.
echo 🔧 This version guarantees:
echo ✅ Window visibility and focus
echo ✅ Pre-filled admin credentials
echo ✅ Detailed status feedback
echo ✅ Built-in diagnostic testing
echo.
java -cp "bin;lib/*;src" BulletproofLoginUI
echo.
echo ✅ Bulletproof login session ended.
echo.
pause
goto MENU

:ALL_TESTS
echo.
echo 🧪 Running ALL Authentication Tests...
echo ======================================
echo.
echo Step 1: Authentication Diagnostic
echo ----------------------------------
java -cp "bin;lib/*;src" AuthenticationDiagnostic
echo.
echo Step 2: Testing Standard Login
echo -------------------------------
echo Starting standard login in 3 seconds...
timeout /t 3 /nobreak >nul
start "Standard Login" java -cp "bin;lib/*" LIMSLoginUI
echo.
echo Step 3: Testing Bulletproof Login
echo ----------------------------------
echo Starting bulletproof login in 5 seconds...
timeout /t 5 /nobreak >nul
start "Bulletproof Login" java -cp "bin;lib/*;src" BulletproofLoginUI
echo.
echo ✅ All tests launched! Check the windows that opened.
echo.
pause
goto MENU

:EXIT
echo.
echo 👋 Thank you for testing LIMS authentication!
echo.
echo 📋 Summary of available credentials:
echo Username: admin     Password: Admin123!     Role: Admin
echo Username: teacher1  Password: Teacher123!   Role: Teacher  
echo Username: student1  Password: Student123!   Role: Student
echo Username: tech1     Password: Tech123!      Role: Lab Technician
echo.
echo 🎯 If you're still having issues:
echo 1. Check your taskbar for Java applications
echo 2. Try Alt+Tab to find the window
echo 3. Run the diagnostic test first
echo 4. Use the bulletproof login for guaranteed visibility
echo.
pause
exit /b 0

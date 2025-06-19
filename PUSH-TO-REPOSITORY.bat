@echo off
title LIMS - Push to Repository
color 0A

echo.
echo ╔══════════════════════════════════════════════════════════════════════════════╗
echo ║                    🚀 PUSH LIMS TO REPOSITORY 🚀                            ║
echo ║                                                                              ║
echo ║  This script will:                                                          ║
echo ║  • Clean up unnecessary files                                               ║
echo ║  • Stage all important LIMS files                                          ║
echo ║  • Commit with a clear message                                              ║
echo ║  • Push to your repository                                                  ║
echo ╚══════════════════════════════════════════════════════════════════════════════╝
echo.

cd /d "%~dp0"

echo 🧹 STEP 1: Cleaning up unnecessary files...
echo ═══════════════════════════════════════════════════════════════

REM Remove temporary and unnecessary files
if exist "*.tmp" del /q "*.tmp"
if exist "*.log" del /q "*.log"
if exist "tempCodeRunnerFile.*" del /q "tempCodeRunnerFile.*"

REM Remove old test files that are no longer needed
if exist "test-*.bat" del /q "test-*.bat"
if exist "ultimate-login-test.bat" del /q "ultimate-login-test.bat"
if exist "bulletproof-login.bat" del /q "bulletproof-login.bat"
if exist "fixed-login.bat" del /q "fixed-login.bat"

echo ✅ Cleanup completed!
echo.

echo 📦 STEP 2: Staging files for commit...
echo ═══════════════════════════════════════════════════════════════

REM Initialize git if not already done
git init

REM Add all important files
git add src/
git add lib/
git add bin/
git add *.sql
git add *.md
git add RUN-LIMS.bat
git add DOWNLOAD-DRIVER.bat
git add README.md
git add ProjectDescription.txt

REM Add specific important batch files
git add START-LIMS.bat
git add SIMPLE-RUN.bat

echo ✅ Files staged successfully!
echo.

echo 📝 STEP 3: Committing changes...
echo ═══════════════════════════════════════════════════════════════

git commit -m "🎉 LIMS Ultimate System - Complete Working Version

✅ Features:
- Ultimate bulletproof login system with multiple fallbacks
- Pre-filled admin credentials (admin/Admin123!)
- Professional full-screen UI with real-time clock
- Complete database integration with PostgreSQL
- Popup dialog system for all management panels
- Enhanced data refresh and table management
- Role-based dashboards (Admin/Teacher/Student)

🔧 Technical Improvements:
- Fixed all authentication issues
- Added PostgreSQL JDBC driver
- Implemented multiple authentication fallbacks
- Enhanced UI visibility and user experience
- Complete DAO pattern implementation
- Comprehensive error handling

🚀 Ready for Production:
- All login issues resolved
- Professional presentation ready
- Complete documentation included
- Easy deployment with batch files

📋 How to Run:
- Execute RUN-LIMS.bat
- Login with admin/Admin123!
- Enjoy the complete LIMS experience!"

if %ERRORLEVEL% EQU 0 (
    echo ✅ Commit successful!
) else (
    echo ⚠️  Nothing new to commit or commit failed.
)
echo.

echo 🌐 STEP 4: Pushing to repository...
echo ═══════════════════════════════════════════════════════════════

echo Please enter your repository details:
echo.
set /p REPO_URL="Enter your repository URL (e.g., https://github.com/username/LIMS.git): "

if "%REPO_URL%"=="" (
    echo ❌ No repository URL provided. Skipping push.
    goto END
)

REM Add remote origin (will overwrite if exists)
git remote remove origin 2>nul
git remote add origin %REPO_URL%

REM Push to main branch (force push to clear previous history)
echo.
echo ⚠️  WARNING: This will REPLACE all previous content in your repository!
echo ⚠️  Are you sure you want to continue? (Y/N)
set /p CONFIRM="Enter Y to confirm: "

if /i "%CONFIRM%"=="Y" (
    echo.
    echo 🚀 Force pushing to repository...
    git branch -M main
    git push -f origin main
    
    if %ERRORLEVEL% EQU 0 (
        echo.
        echo ✅ SUCCESS! LIMS has been pushed to your repository!
        echo.
        echo 🎯 Your repository now contains:
        echo   • Complete working LIMS system
        echo   • Ultimate login system with multiple fallbacks
        echo   • Professional UI and database integration
        echo   • Easy deployment with RUN-LIMS.bat
        echo   • Complete documentation
        echo.
        echo 🌐 Repository URL: %REPO_URL%
        echo.
        echo 📋 To clone and run on another machine:
        echo   1. git clone %REPO_URL%
        echo   2. cd LIMS
        echo   3. RUN-LIMS.bat
        echo.
    ) else (
        echo ❌ Push failed! Please check your repository URL and credentials.
        echo.
        echo 💡 Common issues:
        echo   • Repository URL is incorrect
        echo   • You don't have push permissions
        echo   • Network connectivity issues
        echo   • Authentication required (use personal access token)
    )
) else (
    echo ❌ Push cancelled by user.
)

:END
echo.
echo 🏁 Repository operation completed!
echo.
pause

@echo off
echo ========================================
echo    QUICK PUSH TO REPOSITORY
echo ========================================
echo.

cd /d "%~dp0"

echo Preparing files for repository...

REM Rename the new README
if exist "NEW-README.md" (
    if exist "README.md" del "README.md"
    ren "NEW-README.md" "README.md"
)

REM Clean up unnecessary files
if exist "tempCodeRunnerFile.*" del /q "tempCodeRunnerFile.*"
if exist "*.tmp" del /q "*.tmp"
if exist "*.log" del /q "*.log"

echo.
echo Files ready for commit!
echo.
echo Please enter your repository URL:
set /p REPO_URL="Repository URL: "

if "%REPO_URL%"=="" (
    echo No repository URL provided. Exiting.
    pause
    exit /b 1
)

echo.
echo Initializing git and pushing...

git init
git add .
git commit -m "🎉 LIMS Ultimate System - Complete Working Version

✅ Features:
- Ultimate bulletproof login system with 8 fallbacks
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
- Easy deployment with RUN-LIMS.bat

📋 How to Run:
- Execute RUN-LIMS.bat
- Login with admin/Admin123!
- Enjoy the complete LIMS experience!"

git branch -M main
git remote add origin %REPO_URL%

echo.
echo ⚠️  This will REPLACE all content in your repository!
echo ⚠️  Continue? (Y/N)
set /p CONFIRM="Enter Y to confirm: "

if /i "%CONFIRM%"=="Y" (
    git push -f origin main
    echo.
    echo ✅ SUCCESS! LIMS pushed to repository!
    echo.
    echo 🌐 Repository: %REPO_URL%
    echo 📋 To run: Clone repository and execute RUN-LIMS.bat
) else (
    echo Push cancelled.
)

echo.
pause

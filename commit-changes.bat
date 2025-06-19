@echo off
title LIMS - Git Commit Helper
color 0A

echo.
echo ╔══════════════════════════════════════════════════════════════╗
echo ║                    🔄 GIT COMMIT HELPER 🔄                   ║
echo ║                                                              ║
echo ║  Easy script to commit and push changes to your repository  ║
echo ╚══════════════════════════════════════════════════════════════╝
echo.

echo 📋 Current Git Status:
echo ═══════════════════════════════════════════════════════════════
git status --short
echo.

echo 📝 What changes do you want to commit?
echo.
echo 1. 🔧 Bug fixes and improvements
echo 2. ✨ New features added
echo 3. 📚 Documentation updates
echo 4. 🎨 UI/UX improvements
echo 5. 🔐 Security enhancements
echo 6. 📊 Database changes
echo 7. 🧪 Testing improvements
echo 8. 💾 Custom commit message
echo.
set /p choice="Enter your choice (1-8): "

if "%choice%"=="1" set "commit_msg=🔧 Bug fixes and system improvements"
if "%choice%"=="2" set "commit_msg=✨ Added new features and functionality"
if "%choice%"=="3" set "commit_msg=📚 Updated documentation and guides"
if "%choice%"=="4" set "commit_msg=🎨 UI/UX improvements and styling updates"
if "%choice%"=="5" set "commit_msg=🔐 Security enhancements and authentication fixes"
if "%choice%"=="6" set "commit_msg=📊 Database schema and data handling improvements"
if "%choice%"=="7" set "commit_msg=🧪 Testing improvements and bug fixes"
if "%choice%"=="8" goto CUSTOM_MESSAGE

goto COMMIT_CHANGES

:CUSTOM_MESSAGE
echo.
set /p "commit_msg=Enter your custom commit message: "

:COMMIT_CHANGES
echo.
echo 🔄 Committing changes...
echo ═══════════════════════════════════════════════════════════════
echo.
echo Commit message: %commit_msg%
echo.

echo Adding all changes...
git add .

echo Committing changes...
git commit -m "%commit_msg%"

echo.
echo 🚀 Do you want to push to remote repository? (y/n)
set /p push_choice="Push changes: "

if /i "%push_choice%"=="y" (
    echo.
    echo Pushing to remote repository...
    git push
    echo.
    echo ✅ Changes successfully pushed to remote repository!
) else (
    echo.
    echo ✅ Changes committed locally. Use 'git push' later to push to remote.
)

echo.
echo 📊 Current repository status:
git log --oneline -5
echo.
echo 🎉 Git operations completed!
echo.
pause

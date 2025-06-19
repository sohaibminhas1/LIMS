@echo off
title LIMS - Repository Setup
color 0A

echo.
echo ╔══════════════════════════════════════════════════════════════╗
echo ║                  🔗 REPOSITORY SETUP HELPER 🔗               ║
echo ║                                                              ║
echo ║  Connect your local LIMS project to an online repository    ║
echo ╚══════════════════════════════════════════════════════════════╝
echo.

echo 📋 Choose your repository platform:
echo.
echo 1. 🐙 GitHub
echo 2. 🦊 GitLab
echo 3. 🪣 Bitbucket
echo 4. 🔧 Custom Git URL
echo 5. ❌ Exit
echo.
set /p platform="Enter your choice (1-5): "

if "%platform%"=="1" goto GITHUB_SETUP
if "%platform%"=="2" goto GITLAB_SETUP
if "%platform%"=="3" goto BITBUCKET_SETUP
if "%platform%"=="4" goto CUSTOM_SETUP
if "%platform%"=="5" goto EXIT
goto MENU

:GITHUB_SETUP
echo.
echo 🐙 GITHUB REPOSITORY SETUP
echo ═══════════════════════════════════════════════════════════════
echo.
echo Please provide your GitHub repository details:
echo.
set /p username="GitHub Username: "
set /p reponame="Repository Name (e.g., LIMS-System): "
echo.
set "repo_url=https://github.com/%username%/%reponame%.git"
goto SETUP_REMOTE

:GITLAB_SETUP
echo.
echo 🦊 GITLAB REPOSITORY SETUP
echo ═══════════════════════════════════════════════════════════════
echo.
echo Please provide your GitLab repository details:
echo.
set /p username="GitLab Username: "
set /p reponame="Repository Name (e.g., LIMS-System): "
echo.
set "repo_url=https://gitlab.com/%username%/%reponame%.git"
goto SETUP_REMOTE

:BITBUCKET_SETUP
echo.
echo 🪣 BITBUCKET REPOSITORY SETUP
echo ═══════════════════════════════════════════════════════════════
echo.
echo Please provide your Bitbucket repository details:
echo.
set /p username="Bitbucket Username: "
set /p reponame="Repository Name (e.g., LIMS-System): "
echo.
set "repo_url=https://bitbucket.org/%username%/%reponame%.git"
goto SETUP_REMOTE

:CUSTOM_SETUP
echo.
echo 🔧 CUSTOM REPOSITORY SETUP
echo ═══════════════════════════════════════════════════════════════
echo.
echo Please provide your repository URL:
echo.
set /p repo_url="Repository URL (e.g., https://github.com/user/repo.git): "
goto SETUP_REMOTE

:SETUP_REMOTE
echo.
echo 🔗 Setting up remote repository...
echo ═══════════════════════════════════════════════════════════════
echo.
echo Repository URL: %repo_url%
echo.

echo Checking if remote already exists...
git remote get-url origin >nul 2>&1
if %errorlevel%==0 (
    echo Remote 'origin' already exists. Removing it...
    git remote remove origin
)

echo Adding remote repository...
git remote add origin %repo_url%

echo Verifying remote setup...
git remote -v

echo.
echo 🚀 Do you want to push your code to the repository now? (y/n)
set /p push_now="Push now: "

if /i "%push_now%"=="y" (
    echo.
    echo 📤 Pushing to remote repository...
    echo ═══════════════════════════════════════════════════════════════
    echo.
    echo This will upload all your LIMS code to the repository...
    git push -u origin main
    
    if %errorlevel%==0 (
        echo.
        echo ✅ SUCCESS! Your LIMS project has been uploaded to:
        echo 🔗 %repo_url%
        echo.
        echo 🎉 Your repository is now set up and ready!
        echo.
        echo 📋 Next steps:
        echo 1. Visit your repository online to verify the upload
        echo 2. Use commit-changes.bat for future updates
        echo 3. Share the repository URL with your team/professor
    ) else (
        echo.
        echo ❌ Push failed. This might be because:
        echo 1. Repository doesn't exist - create it first on the platform
        echo 2. Authentication required - you may need to login
        echo 3. Network issues
        echo.
        echo 🔧 To fix:
        echo 1. Create the repository on %platform% first
        echo 2. Make sure you have push permissions
        echo 3. Try running this script again
    )
) else (
    echo.
    echo ✅ Remote repository configured successfully!
    echo.
    echo 📋 To push your code later, run:
    echo git push -u origin main
    echo.
    echo Or use the commit-changes.bat script for easy commits and pushes.
)

echo.
echo 📊 Current repository status:
git status --short
echo.
echo 🔗 Remote repositories:
git remote -v
echo.
pause
goto EXIT

:EXIT
echo.
echo 🎉 Repository setup completed!
echo.
echo 📋 Available scripts:
echo • setup-repository.bat - Connect to online repository
echo • commit-changes.bat - Easy commit and push changes
echo • ONE-CLICK-LOGIN.bat - Start LIMS application
echo.
exit /b 0

@echo off
echo 🚀 INSTANT LIMS LOGIN
echo =====================
echo.
echo This will automatically:
echo ✅ Fix admin user in database
echo ✅ Start bulletproof login UI
echo ✅ Pre-fill admin credentials
echo.
echo Starting in 3 seconds...
timeout /t 3 /nobreak >nul

echo 🔧 Fixing admin user...
java -cp "bin;lib/*;src" AdminUserEnsurer >nul 2>&1

echo 🔐 Starting login...
java -cp "bin;lib/*;src" FixedLoginUI

echo.
echo ✅ Login session completed!
pause

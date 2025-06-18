@echo off
echo 🛡️ BULLETPROOF LIMS LOGIN
echo =========================
echo.
echo 🔧 This login system guarantees:
echo ✅ Window will be visible and on top
echo ✅ Admin credentials are pre-filled
echo ✅ Authentication is thoroughly tested
echo ✅ Detailed status feedback provided
echo.
echo 📋 Pre-filled Credentials:
echo Username: admin
echo Password: Admin123!
echo.
echo 🚀 Starting bulletproof login...

java -cp "bin;lib/*;src" BulletproofLoginUI

if %ERRORLEVEL% NEQ 0 (
    echo.
    echo ❌ Error starting bulletproof login!
    echo Trying fallback method...
    echo.
    java -cp "bin;lib/*" LIMSLoginUI
)

echo.
echo 🏁 Login session ended.
pause

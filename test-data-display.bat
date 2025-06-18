@echo off
echo 🔍 TESTING DATA DISPLAY
echo =======================
echo.
echo This will test if data is properly displayed in the UI panels.
echo.

echo 🧪 Running data display test...
java -cp "bin;lib/*;src" DataDisplayFixer

echo.
echo ✅ Test completed! 
echo.
echo 📋 What to check in the test window:
echo 1. Each tab should show data in the table
echo 2. Row counts should match what was logged
echo 3. Refresh buttons should work
echo.
pause

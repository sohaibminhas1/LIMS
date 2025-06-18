@echo off
echo 🔧 Testing Authentication from Main Directory...
echo ===============================================

REM Test authentication using the proper classpath
java -cp "bin;lib/*" -classpath "bin;lib/*" src.QuickAuthTest

echo ===============================================
pause

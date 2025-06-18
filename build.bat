@echo off
echo 🔧 Building LIMS Application...
echo ================================

REM Create bin directory if it doesn't exist
if not exist bin mkdir bin

REM Copy resources
echo 📁 Copying resources...
copy src\background.jpg bin\ >nul 2>&1
copy src\logo.jpg bin\ >nul 2>&1

REM Compile all Java files
echo ⚙️ Compiling Java files...
javac -cp "lib/*" -d bin src\*.java src\dao\*.java src\service\*.java src\model\*.java src\ui\*.java src\utils\*.java src\controller\*.java

if %ERRORLEVEL% EQU 0 (
    echo ✅ Compilation successful!
    echo 🚀 You can now run: java -cp "bin;lib/*" LIMSLoginUI
) else (
    echo ❌ Compilation failed!
    pause
    exit /b 1
)

echo ================================
echo 🎯 Build completed successfully!

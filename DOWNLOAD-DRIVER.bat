@echo off
echo ========================================
echo    Downloading PostgreSQL JDBC Driver
echo ========================================
echo.

cd /d "%~dp0"

if not exist "lib" mkdir lib

echo Downloading PostgreSQL JDBC Driver 42.7.5...
echo.

powershell -Command "Invoke-WebRequest -Uri 'https://repo1.maven.org/maven2/org/postgresql/postgresql/42.7.5/postgresql-42.7.5.jar' -OutFile 'lib\postgresql-42.7.5.jar'"

if exist "lib\postgresql-42.7.5.jar" (
    echo ✅ PostgreSQL JDBC Driver downloaded successfully!
    echo.
    echo File location: lib\postgresql-42.7.5.jar
    echo.
    echo Now you can run LIMS without build path errors.
) else (
    echo ❌ Failed to download PostgreSQL JDBC Driver.
    echo.
    echo Manual download instructions:
    echo 1. Go to: https://repo1.maven.org/maven2/org/postgresql/postgresql/42.7.5/
    echo 2. Download: postgresql-42.7.5.jar
    echo 3. Place it in: lib\postgresql-42.7.5.jar
)

echo.
pause

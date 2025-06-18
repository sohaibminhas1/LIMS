@echo off
echo 🧪 Testing Data Entry Operations...
echo ===================================

echo 🚀 Starting LIMS Application for data entry testing...
echo.
echo 📋 Test Instructions:
echo 1. Login with admin credentials: admin / Admin123!
echo 2. Test Computer Inventory → Add New Computer
echo 3. Test Software Management → Add Software
echo 4. Test Lab Schedule → Reserve Lab
echo 5. Test Report Management → Register Complaint
echo 6. Test Report Management → Submit Feedback
echo.
echo ✅ All data should be saved to PostgreSQL database
echo 🔄 Tables should refresh automatically after each entry
echo.

java -cp "bin;lib/*" LIMSLoginUI

echo.
echo 🏁 Testing completed!
pause

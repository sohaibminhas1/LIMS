import service.UserService;
import service.LIMSService;
import model.User;
import dao.UserDAO;
import java.sql.*;

/**
 * Complete authentication diagnostic that tests every layer
 */
public class CompleteAuthDiagnostic {
    
    private static final String DB_URL = "jdbc:postgresql://localhost:5434/lims_db";
    private static final String DB_USER = "postgres";
    private static final String DB_PASSWORD = "superadmin";
    
    public static void main(String[] args) {
        System.out.println("🔍 COMPLETE AUTHENTICATION DIAGNOSTIC");
        System.out.println("=" .repeat(60));
        
        String testUsername = "admin";
        String testPassword = "Admin123!";
        
        System.out.println("🧪 Testing credentials: " + testUsername + " / " + testPassword);
        System.out.println();
        
        boolean allTestsPassed = true;
        
        // Test 1: Direct database connection
        allTestsPassed &= testDatabaseConnection();
        
        // Test 2: Direct SQL authentication
        allTestsPassed &= testDirectSQLAuth(testUsername, testPassword);
        
        // Test 3: UserDAO authentication
        allTestsPassed &= testUserDAOAuth(testUsername, testPassword);
        
        // Test 4: UserService authentication
        allTestsPassed &= testUserServiceAuth(testUsername, testPassword);
        
        // Test 5: LIMSService authentication
        allTestsPassed &= testLIMSServiceAuth(testUsername, testPassword);
        
        // Test 6: User status verification
        allTestsPassed &= testUserStatus(testUsername);
        
        // Test 7: Password hash verification
        allTestsPassed &= testPasswordHash(testUsername, testPassword);
        
        System.out.println("\n" + "=" .repeat(60));
        if (allTestsPassed) {
            System.out.println("✅ ALL AUTHENTICATION TESTS PASSED!");
            System.out.println("🎯 Authentication system is working correctly.");
            System.out.println("🔧 If login still fails, the issue is likely UI-related.");
        } else {
            System.out.println("❌ SOME AUTHENTICATION TESTS FAILED!");
            System.out.println("🔧 Running auto-fix procedures...");
            autoFixAuthenticationIssues(testUsername, testPassword);
        }
        
        System.out.println("\n🎉 Diagnostic completed!");
    }
    
    private static boolean testDatabaseConnection() {
        System.out.println("\n📊 TEST 1: Database Connection");
        System.out.println("-" .repeat(40));
        
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            System.out.println("✅ Database connection successful");
            
            // Test user_access table exists
            try (PreparedStatement stmt = conn.prepareStatement("SELECT COUNT(*) FROM user_access")) {
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    int count = rs.getInt(1);
                    System.out.println("✅ user_access table accessible with " + count + " records");
                    return true;
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Database connection failed: " + e.getMessage());
            return false;
        }
        return false;
    }
    
    private static boolean testDirectSQLAuth(String username, String password) {
        System.out.println("\n🔍 TEST 2: Direct SQL Authentication");
        System.out.println("-" .repeat(40));
        
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String query = "SELECT user_id, name, role, status, password_hash FROM user_access WHERE user_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, username);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        String userId = rs.getString("user_id");
                        String name = rs.getString("name");
                        String role = rs.getString("role");
                        String status = rs.getString("status");
                        String hash = rs.getString("password_hash");
                        
                        System.out.println("✅ User found: " + userId + " (" + name + ")");
                        System.out.println("   Role: " + role);
                        System.out.println("   Status: " + status);
                        System.out.println("   Has password hash: " + (hash != null && !hash.isEmpty()));
                        
                        return true;
                    } else {
                        System.out.println("❌ User not found in database");
                        return false;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ SQL authentication test failed: " + e.getMessage());
            return false;
        }
    }
    
    private static boolean testUserDAOAuth(String username, String password) {
        System.out.println("\n🔧 TEST 3: UserDAO Authentication");
        System.out.println("-" .repeat(40));
        
        try {
            UserDAO userDAO = new UserDAO();
            User user = userDAO.authenticateUser(username, password);
            
            if (user != null) {
                System.out.println("✅ UserDAO authentication successful");
                System.out.println("   User: " + user.getName());
                System.out.println("   Role: " + user.getRole());
                System.out.println("   Status: " + user.getStatus());
                System.out.println("   Is Active: " + user.isActive());
                return true;
            } else {
                System.out.println("❌ UserDAO authentication failed");
                return false;
            }
        } catch (Exception e) {
            System.err.println("❌ UserDAO test error: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    private static boolean testUserServiceAuth(String username, String password) {
        System.out.println("\n⚙️ TEST 4: UserService Authentication");
        System.out.println("-" .repeat(40));
        
        try {
            UserService userService = new UserService();
            User user = userService.authenticateUser(username, password);
            
            if (user != null) {
                System.out.println("✅ UserService authentication successful");
                System.out.println("   User: " + user.getName());
                System.out.println("   Role: " + user.getRole());
                System.out.println("   Status: " + user.getStatus());
                System.out.println("   Is Active: " + user.isActive());
                return true;
            } else {
                System.out.println("❌ UserService authentication failed");
                return false;
            }
        } catch (Exception e) {
            System.err.println("❌ UserService test error: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    private static boolean testLIMSServiceAuth(String username, String password) {
        System.out.println("\n🎯 TEST 5: LIMSService Authentication");
        System.out.println("-" .repeat(40));
        
        try {
            LIMSService limsService = LIMSService.getInstance();
            String role = limsService.authenticateUser(username, password);
            
            if (role != null) {
                System.out.println("✅ LIMSService authentication successful");
                System.out.println("   Role: " + role);
                return true;
            } else {
                System.out.println("❌ LIMSService authentication failed");
                return false;
            }
        } catch (Exception e) {
            System.err.println("❌ LIMSService test error: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    private static boolean testUserStatus(String username) {
        System.out.println("\n📋 TEST 6: User Status Verification");
        System.out.println("-" .repeat(40));
        
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String query = "SELECT status FROM user_access WHERE user_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, username);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        String status = rs.getString("status");
                        System.out.println("✅ User status: '" + status + "'");
                        
                        if ("Active".equals(status)) {
                            System.out.println("✅ Status is correct for authentication");
                            return true;
                        } else {
                            System.out.println("❌ Status should be 'Active' for authentication to work");
                            return false;
                        }
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Status verification failed: " + e.getMessage());
        }
        return false;
    }
    
    private static boolean testPasswordHash(String username, String password) {
        System.out.println("\n🔐 TEST 7: Password Hash Verification");
        System.out.println("-" .repeat(40));
        
        try {
            UserDAO userDAO = new UserDAO();
            // Use reflection to access the verifyPassword method if it's private
            java.lang.reflect.Method verifyMethod = UserDAO.class.getDeclaredMethod("verifyPassword", String.class, String.class);
            verifyMethod.setAccessible(true);
            
            // Get the stored hash
            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
                String query = "SELECT password_hash FROM user_access WHERE user_id = ?";
                try (PreparedStatement stmt = conn.prepareStatement(query)) {
                    stmt.setString(1, username);
                    try (ResultSet rs = stmt.executeQuery()) {
                        if (rs.next()) {
                            String storedHash = rs.getString("password_hash");
                            if (storedHash != null) {
                                boolean isValid = (Boolean) verifyMethod.invoke(userDAO, password, storedHash);
                                if (isValid) {
                                    System.out.println("✅ Password hash verification successful");
                                    return true;
                                } else {
                                    System.out.println("❌ Password does not match stored hash");
                                    return false;
                                }
                            } else {
                                System.out.println("❌ No password hash stored for user");
                                return false;
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("❌ Password hash verification failed: " + e.getMessage());
        }
        return false;
    }
    
    private static void autoFixAuthenticationIssues(String username, String password) {
        System.out.println("\n🔧 AUTO-FIX: Attempting to resolve authentication issues");
        System.out.println("-" .repeat(50));
        
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            // Fix 1: Ensure user status is Active
            String updateStatus = "UPDATE user_access SET status = 'Active' WHERE user_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(updateStatus)) {
                stmt.setString(1, username);
                int updated = stmt.executeUpdate();
                if (updated > 0) {
                    System.out.println("✅ Fixed: Set user status to 'Active'");
                }
            }
            
            // Fix 2: Reset password hash if needed
            UserService userService = new UserService();
            boolean passwordReset = userService.resetPassword(username, password);
            if (passwordReset) {
                System.out.println("✅ Fixed: Reset password hash");
            }
            
            System.out.println("🔄 Re-running authentication test...");
            boolean authWorking = testLIMSServiceAuth(username, password);
            if (authWorking) {
                System.out.println("🎉 Authentication issues resolved!");
            } else {
                System.out.println("❌ Authentication issues persist - manual intervention required");
            }
            
        } catch (Exception e) {
            System.err.println("❌ Auto-fix failed: " + e.getMessage());
        }
    }
}

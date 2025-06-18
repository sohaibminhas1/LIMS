import service.UserService;
import service.LIMSService;
import model.User;
import dao.UserDAO;
import java.sql.*;

/**
 * Comprehensive authentication diagnostic tool
 */
public class AuthenticationDiagnostic {
    
    public static void main(String[] args) {
        System.out.println("🔍 COMPREHENSIVE AUTHENTICATION DIAGNOSTIC");
        System.out.println("=" .repeat(60));
        
        // Test credentials
        String testUsername = "admin";
        String testPassword = "Admin123!";
        
        System.out.println("🧪 Testing credentials: " + testUsername + " / " + testPassword);
        System.out.println();
        
        // Step 1: Direct database check
        System.out.println("📊 STEP 1: Direct Database Analysis");
        System.out.println("-" .repeat(40));
        checkDatabaseDirectly(testUsername);
        
        // Step 2: UserDAO test
        System.out.println("\n🔧 STEP 2: UserDAO Authentication Test");
        System.out.println("-" .repeat(40));
        testUserDAO(testUsername, testPassword);
        
        // Step 3: UserService test
        System.out.println("\n⚙️ STEP 3: UserService Authentication Test");
        System.out.println("-" .repeat(40));
        testUserService(testUsername, testPassword);
        
        // Step 4: LIMSService test
        System.out.println("\n🎯 STEP 4: LIMSService Authentication Test");
        System.out.println("-" .repeat(40));
        testLIMSService(testUsername, testPassword);
        
        // Step 5: Fix any issues found
        System.out.println("\n🔧 STEP 5: Auto-Fix Issues");
        System.out.println("-" .repeat(40));
        autoFixIssues(testUsername);
        
        System.out.println("\n✅ DIAGNOSTIC COMPLETED!");
        System.out.println("🎯 Try logging in now with: " + testUsername + " / " + testPassword);
    }
    
    private static void checkDatabaseDirectly(String username) {
        String url = "jdbc:postgresql://localhost:5434/lims_db";
        String dbUser = "postgres";
        String dbPassword = "superadmin";
        
        try (Connection conn = DriverManager.getConnection(url, dbUser, dbPassword)) {
            String query = "SELECT user_id, name, role, department, access_level, status, password_hash FROM user_access WHERE user_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, username);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        System.out.println("✅ User found in database:");
                        System.out.println("   User ID: " + rs.getString("user_id"));
                        System.out.println("   Name: " + rs.getString("name"));
                        System.out.println("   Role: " + rs.getString("role"));
                        System.out.println("   Department: " + rs.getString("department"));
                        System.out.println("   Access Level: " + rs.getString("access_level"));
                        System.out.println("   Status: '" + rs.getString("status") + "'");
                        String hash = rs.getString("password_hash");
                        System.out.println("   Password Hash: " + (hash != null ? hash.substring(0, Math.min(20, hash.length())) + "..." : "NULL"));
                        System.out.println("   Hash Length: " + (hash != null ? hash.length() : 0));
                    } else {
                        System.out.println("❌ User NOT found in database!");
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Database error: " + e.getMessage());
        }
    }
    
    private static void testUserDAO(String username, String password) {
        try {
            UserDAO userDAO = new UserDAO();
            User user = userDAO.authenticateUser(username, password);
            
            if (user != null) {
                System.out.println("✅ UserDAO authentication successful:");
                System.out.println("   User: " + user.getName());
                System.out.println("   Role: " + user.getRole());
                System.out.println("   Status: '" + user.getStatus() + "'");
                System.out.println("   Is Active: " + user.isActive());
            } else {
                System.out.println("❌ UserDAO authentication failed");
            }
        } catch (Exception e) {
            System.err.println("❌ UserDAO error: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static void testUserService(String username, String password) {
        try {
            UserService userService = new UserService();
            User user = userService.authenticateUser(username, password);
            
            if (user != null) {
                System.out.println("✅ UserService authentication successful:");
                System.out.println("   User: " + user.getName());
                System.out.println("   Role: " + user.getRole());
                System.out.println("   Status: '" + user.getStatus() + "'");
                System.out.println("   Is Active: " + user.isActive());
            } else {
                System.out.println("❌ UserService authentication failed");
            }
        } catch (Exception e) {
            System.err.println("❌ UserService error: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static void testLIMSService(String username, String password) {
        try {
            LIMSService limsService = LIMSService.getInstance();
            String role = limsService.authenticateUser(username, password);
            
            if (role != null) {
                System.out.println("✅ LIMSService authentication successful:");
                System.out.println("   Role: " + role);
            } else {
                System.out.println("❌ LIMSService authentication failed");
            }
        } catch (Exception e) {
            System.err.println("❌ LIMSService error: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static void autoFixIssues(String username) {
        String url = "jdbc:postgresql://localhost:5434/lims_db";
        String dbUser = "postgres";
        String dbPassword = "superadmin";
        
        try (Connection conn = DriverManager.getConnection(url, dbUser, dbPassword)) {
            // Check and fix status
            String checkQuery = "SELECT status FROM user_access WHERE user_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(checkQuery)) {
                stmt.setString(1, username);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        String currentStatus = rs.getString("status");
                        System.out.println("Current status: '" + currentStatus + "'");
                        
                        if (!"Active".equals(currentStatus)) {
                            System.out.println("🔧 Fixing status to 'Active'...");
                            String updateQuery = "UPDATE user_access SET status = 'Active' WHERE user_id = ?";
                            try (PreparedStatement updateStmt = conn.prepareStatement(updateQuery)) {
                                updateStmt.setString(1, username);
                                int updated = updateStmt.executeUpdate();
                                if (updated > 0) {
                                    System.out.println("✅ Status updated to 'Active'");
                                } else {
                                    System.out.println("❌ Failed to update status");
                                }
                            }
                        } else {
                            System.out.println("✅ Status is already 'Active'");
                        }
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Auto-fix error: " + e.getMessage());
        }
    }
}

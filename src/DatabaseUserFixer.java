import service.UserService;
import model.User;
import java.sql.*;

/**
 * Comprehensive database user configuration fixer
 */
public class DatabaseUserFixer {
    
    private static final String DB_URL = "jdbc:postgresql://localhost:5434/lims_db";
    private static final String DB_USER = "postgres";
    private static final String DB_PASSWORD = "superadmin";
    
    public static void main(String[] args) {
        System.out.println("🔧 DATABASE USER CONFIGURATION FIXER");
        System.out.println("=" .repeat(50));
        
        // Fix admin user
        fixUser("admin", "System Administrator", "Admin123!", "Admin", "IT", "Full");
        
        // Fix other essential users
        fixUser("teacher1", "John Teacher", "Teacher123!", "Teacher", "Computer Science", "Limited");
        fixUser("student1", "Jane Student", "Student123!", "Student", "Computer Science", "Basic");
        fixUser("tech1", "Tech Support", "Tech123!", "Lab Technician", "IT", "Maintenance");
        
        System.out.println("\n✅ Database user configuration completed!");
        System.out.println("🎯 All users are now properly configured for authentication!");
    }
    
    private static void fixUser(String userId, String name, String password, String role, String department, String accessLevel) {
        System.out.println("\n🔧 Fixing user: " + userId);
        System.out.println("-" .repeat(30));
        
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            
            // Check if user exists
            boolean userExists = checkUserExists(conn, userId);
            
            if (!userExists) {
                System.out.println("👤 User does not exist, creating...");
                createUser(conn, userId, name, password, role, department, accessLevel);
            } else {
                System.out.println("👤 User exists, updating...");
                updateUser(conn, userId, name, password, role, department, accessLevel);
            }
            
            // Verify the user is properly configured
            verifyUser(conn, userId, password);
            
        } catch (SQLException e) {
            System.err.println("❌ Error fixing user " + userId + ": " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static boolean checkUserExists(Connection conn, String userId) throws SQLException {
        String query = "SELECT COUNT(*) FROM user_access WHERE user_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }
    
    private static void createUser(Connection conn, String userId, String name, String password, String role, String department, String accessLevel) throws SQLException {
        System.out.println("   Creating new user...");
        
        // Use UserService to create user properly
        UserService userService = new UserService();
        boolean created = userService.createUser(userId, name, password, role, department, accessLevel);
        
        if (created) {
            System.out.println("   ✅ User created successfully");
            
            // Ensure status is Active
            String updateStatus = "UPDATE user_access SET status = 'Active' WHERE user_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(updateStatus)) {
                stmt.setString(1, userId);
                stmt.executeUpdate();
                System.out.println("   ✅ Status set to Active");
            }
        } else {
            System.out.println("   ❌ Failed to create user");
        }
    }
    
    private static void updateUser(Connection conn, String userId, String name, String password, String role, String department, String accessLevel) throws SQLException {
        System.out.println("   Updating existing user...");
        
        // Update basic info
        String updateQuery = "UPDATE user_access SET name = ?, role = ?, department = ?, access_level = ?, status = 'Active' WHERE user_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(updateQuery)) {
            stmt.setString(1, name);
            stmt.setString(2, role);
            stmt.setString(3, department);
            stmt.setString(4, accessLevel);
            stmt.setString(5, userId);
            
            int updated = stmt.executeUpdate();
            if (updated > 0) {
                System.out.println("   ✅ User info updated");
            }
        }
        
        // Update password
        UserService userService = new UserService();
        boolean passwordUpdated = userService.resetPassword(userId, password);
        if (passwordUpdated) {
            System.out.println("   ✅ Password updated");
        } else {
            System.out.println("   ⚠️ Password update may have failed");
        }
    }
    
    private static void verifyUser(Connection conn, String userId, String password) {
        System.out.println("   🔍 Verifying user configuration...");
        
        try {
            // Check database record
            String query = "SELECT user_id, name, role, status, password_hash FROM user_access WHERE user_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, userId);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        String name = rs.getString("name");
                        String role = rs.getString("role");
                        String status = rs.getString("status");
                        String hash = rs.getString("password_hash");
                        
                        System.out.println("   📋 Database record:");
                        System.out.println("      Name: " + name);
                        System.out.println("      Role: " + role);
                        System.out.println("      Status: " + status);
                        System.out.println("      Has password hash: " + (hash != null && !hash.isEmpty()));
                        
                        // Test authentication
                        UserService userService = new UserService();
                        User user = userService.authenticateUser(userId, password);
                        
                        if (user != null && user.isActive()) {
                            System.out.println("   ✅ Authentication test: PASSED");
                        } else {
                            System.out.println("   ❌ Authentication test: FAILED");
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("   ❌ Verification failed: " + e.getMessage());
        }
    }
    
    /**
     * Emergency admin user creation - bypasses normal validation
     */
    public static void emergencyCreateAdmin() {
        System.out.println("\n🚨 EMERGENCY ADMIN USER CREATION");
        System.out.println("=" .repeat(40));
        
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            
            // Delete existing admin if any
            String deleteQuery = "DELETE FROM user_access WHERE user_id = 'admin'";
            try (PreparedStatement stmt = conn.prepareStatement(deleteQuery)) {
                int deleted = stmt.executeUpdate();
                if (deleted > 0) {
                    System.out.println("🗑️ Removed existing admin user");
                }
            }
            
            // Create new admin with direct SQL
            String insertQuery = "INSERT INTO user_access (user_id, name, role, department, access_level, status, password_hash, created_at) VALUES (?, ?, ?, ?, ?, ?, ?, NOW())";
            try (PreparedStatement stmt = conn.prepareStatement(insertQuery)) {
                stmt.setString(1, "admin");
                stmt.setString(2, "System Administrator");
                stmt.setString(3, "Admin");
                stmt.setString(4, "IT");
                stmt.setString(5, "Full");
                stmt.setString(6, "Active");
                
                // Generate password hash for Admin123!
                dao.UserDAO userDAO = new dao.UserDAO();
                String hash = userDAO.hashPassword("Admin123!");
                stmt.setString(7, hash);
                
                int inserted = stmt.executeUpdate();
                if (inserted > 0) {
                    System.out.println("✅ Emergency admin user created successfully");
                    
                    // Test the new admin
                    UserService testUserService = new UserService();
                    User testUser = testUserService.authenticateUser("admin", "Admin123!");
                    if (testUser != null) {
                        System.out.println("✅ Emergency admin authentication test: PASSED");
                    } else {
                        System.out.println("❌ Emergency admin authentication test: FAILED");
                    }
                } else {
                    System.out.println("❌ Failed to create emergency admin user");
                }
            }
            
        } catch (Exception e) {
            System.err.println("❌ Emergency admin creation failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

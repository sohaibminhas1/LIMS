import service.UserService;
import model.User;
import java.sql.*;

/**
 * Ensures admin user exists and is properly configured
 */
public class AdminUserEnsurer {
    
    public static void main(String[] args) {
        System.out.println("🔧 ADMIN USER CONFIGURATION CHECKER");
        System.out.println("=" .repeat(50));
        
        ensureAdminUserExists();
        
        System.out.println("\n✅ Admin user configuration completed!");
        System.out.println("🎯 You can now login with: admin / Admin123!");
    }
    
    public static void ensureAdminUserExists() {
        String url = "jdbc:postgresql://localhost:5434/lims_db";
        String dbUser = "postgres";
        String dbPassword = "superadmin";
        
        try (Connection conn = DriverManager.getConnection(url, dbUser, dbPassword)) {
            
            // Check if admin user exists
            String checkQuery = "SELECT user_id, status, password_hash FROM user_access WHERE user_id = 'admin'";
            try (PreparedStatement stmt = conn.prepareStatement(checkQuery);
                 ResultSet rs = stmt.executeQuery()) {
                
                if (rs.next()) {
                    String status = rs.getString("status");
                    String passwordHash = rs.getString("password_hash");
                    
                    System.out.println("✅ Admin user found in database");
                    System.out.println("   Status: " + status);
                    System.out.println("   Has password hash: " + (passwordHash != null && !passwordHash.isEmpty()));
                    
                    boolean needsUpdate = false;
                    
                    // Check if status needs fixing
                    if (!"Active".equals(status)) {
                        System.out.println("🔧 Fixing admin user status...");
                        String updateStatusQuery = "UPDATE user_access SET status = 'Active' WHERE user_id = 'admin'";
                        try (PreparedStatement updateStmt = conn.prepareStatement(updateStatusQuery)) {
                            updateStmt.executeUpdate();
                            System.out.println("✅ Admin status updated to 'Active'");
                            needsUpdate = true;
                        }
                    }
                    
                    // Check if password needs updating
                    if (passwordHash == null || passwordHash.isEmpty()) {
                        System.out.println("🔧 Setting admin password...");
                        updateAdminPassword(conn);
                        needsUpdate = true;
                    } else {
                        // Test if current password works
                        UserService userService = new UserService();
                        User testUser = userService.authenticateUser("admin", "Admin123!");
                        if (testUser == null) {
                            System.out.println("🔧 Admin password doesn't match, updating...");
                            updateAdminPassword(conn);
                            needsUpdate = true;
                        } else {
                            System.out.println("✅ Admin password is correct");
                        }
                    }
                    
                    if (!needsUpdate) {
                        System.out.println("✅ Admin user is properly configured");
                    }
                    
                } else {
                    System.out.println("❌ Admin user not found, creating...");
                    createAdminUser(conn);
                }
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Database error: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static void createAdminUser(Connection conn) throws SQLException {
        System.out.println("🔧 Creating admin user...");
        
        // Use UserService to create admin user properly
        UserService userService = new UserService();
        boolean success = userService.createUser(
            "admin",
            "System Administrator", 
            "Admin123!",
            "Admin",
            "IT",
            "Full"
        );
        
        if (success) {
            System.out.println("✅ Admin user created successfully");
        } else {
            System.out.println("❌ Failed to create admin user");
        }
    }
    
    private static void updateAdminPassword(Connection conn) throws SQLException {
        // Use UserService to update password properly
        UserService userService = new UserService();
        boolean success = userService.resetPassword("admin", "Admin123!");
        
        if (success) {
            System.out.println("✅ Admin password updated successfully");
        } else {
            System.out.println("❌ Failed to update admin password");
        }
    }
}

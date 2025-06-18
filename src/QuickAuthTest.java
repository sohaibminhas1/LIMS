import service.UserService;
import model.User;

/**
 * Quick test to check if authentication is working
 */
public class QuickAuthTest {
    
    public static void main(String[] args) {
        System.out.println("🔧 Quick Authentication Test");
        System.out.println("=" .repeat(40));
        
        try {
            UserService userService = new UserService();
            
            // Test admin login
            System.out.println("Testing admin login...");
            User user = userService.authenticateUser("admin", "Admin123!");
            
            if (user != null) {
                System.out.println("✅ SUCCESS: Admin login works");
                System.out.println("   User: " + user.getName());
                System.out.println("   Role: " + user.getRole());
                System.out.println("   Status: " + user.getStatus());
            } else {
                System.out.println("❌ FAILED: Admin login failed");
            }
            
            // Test database connection
            System.out.println("\nTesting database connection...");
            dao.UserDAO userDAO = new dao.UserDAO();
            boolean connected = userDAO.testConnection();
            System.out.println("Database: " + (connected ? "✅ Connected" : "❌ Failed"));
            
        } catch (Exception e) {
            System.err.println("❌ Error: " + e.getMessage());
            e.printStackTrace();
        }
        
        System.out.println("\n🏁 Test completed!");
    }
}

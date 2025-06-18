package service;

import dao.UserDAO;
import model.User;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Service class for User operations
 * Handles business logic for user authentication and management
 */
public class UserService {
    private final UserDAO userDAO;
    
    // Password validation patterns
    private static final Pattern PASSWORD_PATTERN = Pattern.compile(
        "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$"
    );
    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[a-zA-Z0-9._-]{3,20}$");
    
    public UserService() {
        this.userDAO = new UserDAO();
    }

    /**
     * Authenticate user with username and password
     * @param username The username
     * @param password The password
     * @return User object if authentication successful, null otherwise
     */
    public User authenticateUser(String username, String password) {
        if (username == null || username.trim().isEmpty()) {
            System.out.println("❌ Authentication failed: Username is empty");
            return null;
        }
        
        if (password == null || password.trim().isEmpty()) {
            System.out.println("❌ Authentication failed: Password is empty");
            return null;
        }
        
        return userDAO.authenticateUser(username.trim(), password);
    }

    /**
     * Create a new user
     * @param userId User ID
     * @param name Full name
     * @param password Plain text password
     * @param role User role
     * @param department Department
     * @param accessLevel Access level
     * @return true if user created successfully, false otherwise
     */
    public boolean createUser(String userId, String name, String password, String role, 
                             String department, String accessLevel) {
        try {
            // Validate input
            if (!validateUserInput(userId, name, password, role)) {
                return false;
            }
            
            // Check if user already exists
            if (userDAO.userExists(userId)) {
                System.out.println("❌ User creation failed: User ID already exists: " + userId);
                return false;
            }
            
            // Hash password
            String hashedPassword = userDAO.hashPassword(password);
            if (hashedPassword == null) {
                System.out.println("❌ User creation failed: Password hashing failed");
                return false;
            }
            
            // Create user object
            User user = new User(userId, name, role, department, accessLevel, "Active", hashedPassword);
            
            // Save to database
            boolean success = userDAO.insert(user);
            
            if (success) {
                System.out.println("✅ User created successfully: " + userId + " (" + name + ")");
            } else {
                System.out.println("❌ User creation failed: Database error for user: " + userId);
            }
            
            return success;
            
        } catch (Exception e) {
            System.err.println("❌ User creation failed: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Update user information (excluding password)
     * @param user User object with updated information
     * @return true if update successful, false otherwise
     */
    public boolean updateUser(User user) {
        if (user == null || user.getUserId() == null) {
            System.out.println("❌ User update failed: Invalid user data");
            return false;
        }
        
        return userDAO.update(user);
    }

    /**
     * Update user password
     * @param userId User ID
     * @param currentPassword Current password for verification
     * @param newPassword New password
     * @return true if update successful, false otherwise
     */
    public boolean updatePassword(String userId, String currentPassword, String newPassword) {
        // Verify current password first
        User user = userDAO.authenticateUser(userId, currentPassword);
        if (user == null) {
            System.out.println("❌ Password update failed: Current password is incorrect");
            return false;
        }
        
        // Validate new password
        if (!validatePassword(newPassword)) {
            return false;
        }
        
        return userDAO.updatePassword(userId, newPassword);
    }

    /**
     * Admin password reset (no current password verification required)
     * @param userId User ID
     * @param newPassword New password
     * @return true if reset successful, false otherwise
     */
    public boolean resetPassword(String userId, String newPassword) {
        if (!validatePassword(newPassword)) {
            return false;
        }
        
        return userDAO.updatePassword(userId, newPassword);
    }

    /**
     * Get user by ID
     * @param userId User ID
     * @return User object or null if not found
     */
    public User getUser(String userId) {
        return userDAO.findById(userId);
    }

    /**
     * Get all users
     * @return List of all users
     */
    public List<User> getAllUsers() {
        return userDAO.findAll();
    }

    /**
     * Deactivate user (soft delete)
     * @param userId User ID
     * @return true if deactivation successful, false otherwise
     */
    public boolean deactivateUser(String userId) {
        return userDAO.delete(userId);
    }

    /**
     * Check if user exists
     * @param userId User ID
     * @return true if user exists, false otherwise
     */
    public boolean userExists(String userId) {
        return userDAO.userExists(userId);
    }

    /**
     * Validate user input for creation
     * @param userId User ID
     * @param name Full name
     * @param password Password
     * @param role Role
     * @return true if valid, false otherwise
     */
    private boolean validateUserInput(String userId, String name, String password, String role) {
        // Validate username
        if (!validateUsername(userId)) {
            return false;
        }
        
        // Validate name
        if (name == null || name.trim().length() < 2 || name.trim().length() > 100) {
            System.out.println("❌ Validation failed: Name must be 2-100 characters long");
            return false;
        }
        
        // Validate password
        if (!validatePassword(password)) {
            return false;
        }
        
        // Validate role
        if (!isValidRole(role)) {
            System.out.println("❌ Validation failed: Invalid role: " + role);
            return false;
        }
        
        return true;
    }

    /**
     * Validate username format
     * @param username Username to validate
     * @return true if valid, false otherwise
     */
    private boolean validateUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            System.out.println("❌ Validation failed: Username cannot be empty");
            return false;
        }
        
        if (!USERNAME_PATTERN.matcher(username.trim()).matches()) {
            System.out.println("❌ Validation failed: Username must be 3-20 characters, alphanumeric with ._- allowed");
            return false;
        }
        
        return true;
    }

    /**
     * Validate password strength
     * @param password Password to validate
     * @return true if valid, false otherwise
     */
    private boolean validatePassword(String password) {
        if (password == null || password.length() < 8) {
            System.out.println("❌ Validation failed: Password must be at least 8 characters long");
            return false;
        }
        
        if (password.length() > 128) {
            System.out.println("❌ Validation failed: Password must be less than 128 characters");
            return false;
        }
        
        // Check for basic complexity (at least one uppercase, lowercase, digit, special char)
        if (!PASSWORD_PATTERN.matcher(password).matches()) {
            System.out.println("❌ Validation failed: Password must contain at least one uppercase letter, " +
                             "one lowercase letter, one digit, and one special character (@$!%*?&)");
            return false;
        }
        
        return true;
    }

    /**
     * Check if role is valid
     * @param role Role to check
     * @return true if valid, false otherwise
     */
    private boolean isValidRole(String role) {
        return role != null && (
            role.equals("Admin") || 
            role.equals("Teacher") || 
            role.equals("Student") || 
            role.equals("Lab Technician")
        );
    }

    /**
     * Generate a secure temporary password
     * @return Generated password
     */
    public String generateTemporaryPassword() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789@$!%*?&";
        StringBuilder password = new StringBuilder();
        java.security.SecureRandom random = new java.security.SecureRandom();
        
        // Ensure at least one of each required character type
        password.append(chars.charAt(random.nextInt(26))); // Uppercase
        password.append(chars.charAt(26 + random.nextInt(26))); // Lowercase
        password.append(chars.charAt(52 + random.nextInt(10))); // Digit
        password.append(chars.charAt(62 + random.nextInt(8))); // Special char
        
        // Fill remaining positions
        for (int i = 4; i < 12; i++) {
            password.append(chars.charAt(random.nextInt(chars.length())));
        }
        
        // Shuffle the password
        for (int i = password.length() - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            char temp = password.charAt(i);
            password.setCharAt(i, password.charAt(j));
            password.setCharAt(j, temp);
        }
        
        return password.toString();
    }
}

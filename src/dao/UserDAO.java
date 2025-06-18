package dao;

import model.User;
import utils.DAOLogger;
import java.sql.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for User entity
 * Handles all database operations for user authentication and management
 */
public class UserDAO extends AbstractDAO implements BaseDAO<User> {
    
    // SQL Queries
    private static final String AUTHENTICATE_USER = 
        "SELECT user_id, name, role, department, access_level, status, password_hash " +
        "FROM user_access WHERE user_id = ? AND status = 'Active'";
    
    private static final String INSERT_USER = 
        "INSERT INTO user_access (user_id, name, role, department, access_level, status, password_hash) " +
        "VALUES (?, ?, ?, ?, ?, ?, ?)";
    
    private static final String UPDATE_USER = 
        "UPDATE user_access SET name = ?, role = ?, department = ?, access_level = ?, status = ? " +
        "WHERE user_id = ?";
    
    private static final String UPDATE_PASSWORD = 
        "UPDATE user_access SET password_hash = ? WHERE user_id = ?";
    
    private static final String SELECT_ALL_USERS = 
        "SELECT user_id, name, role, department, access_level, status FROM user_access ORDER BY name";
    
    private static final String SELECT_USER_BY_ID = 
        "SELECT user_id, name, role, department, access_level, status FROM user_access WHERE user_id = ?";
    
    private static final String DELETE_USER = 
        "UPDATE user_access SET status = 'Inactive' WHERE user_id = ?";
    
    private static final String CHECK_USER_EXISTS = 
        "SELECT COUNT(*) FROM user_access WHERE user_id = ?";

    /**
     * Authenticate user with username and password
     * @param username The username
     * @param password The plain text password
     * @return User object if authentication successful, null otherwise
     */
    public User authenticateUser(String username, String password) {
        DAOLogger.info(className, "authenticateUser", "Attempting to authenticate user: " + username);
        
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(AUTHENTICATE_USER)) {
            
            statement.setString(1, username);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    String storedHash = resultSet.getString("password_hash");
                    
                    // Verify password
                    if (storedHash != null && verifyPassword(password, storedHash)) {
                        User user = mapResultSetToUser(resultSet);
                        DAOLogger.success(className, "authenticateUser", 
                            "Authentication successful for user: " + username + " (Role: " + user.getRole() + ")");
                        return user;
                    } else {
                        DAOLogger.warn(className, "authenticateUser", "Invalid password for user: " + username);
                        return null;
                    }
                } else {
                    DAOLogger.warn(className, "authenticateUser", "User not found: " + username);
                    return null;
                }
            }
            
        } catch (SQLException e) {
            DAOLogger.error(className, "authenticateUser", "Database error during authentication", e);
            return null;
        }
    }

    /**
     * Hash password using SHA-256 with salt
     * @param password Plain text password
     * @return Hashed password with salt
     */
    public String hashPassword(String password) {
        try {
            // Generate salt
            SecureRandom random = new SecureRandom();
            byte[] salt = new byte[16];
            random.nextBytes(salt);
            
            // Hash password with salt
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salt);
            byte[] hashedPassword = md.digest(password.getBytes());
            
            // Combine salt and hash
            StringBuilder sb = new StringBuilder();
            for (byte b : salt) {
                sb.append(String.format("%02x", b));
            }
            sb.append(":");
            for (byte b : hashedPassword) {
                sb.append(String.format("%02x", b));
            }
            
            return sb.toString();
            
        } catch (NoSuchAlgorithmException e) {
            DAOLogger.error(className, "hashPassword", "Error hashing password", e);
            return null;
        }
    }

    /**
     * Verify password against stored hash
     * @param password Plain text password
     * @param storedHash Stored hash with salt
     * @return true if password matches, false otherwise
     */
    public boolean verifyPassword(String password, String storedHash) {
        try {
            if (storedHash == null || !storedHash.contains(":")) {
                return false;
            }
            
            String[] parts = storedHash.split(":");
            if (parts.length != 2) {
                return false;
            }
            
            // Extract salt
            byte[] salt = new byte[16];
            String saltHex = parts[0];
            for (int i = 0; i < salt.length; i++) {
                salt[i] = (byte) Integer.parseInt(saltHex.substring(i * 2, i * 2 + 2), 16);
            }
            
            // Hash input password with same salt
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salt);
            byte[] hashedPassword = md.digest(password.getBytes());
            
            // Convert to hex
            StringBuilder sb = new StringBuilder();
            for (byte b : hashedPassword) {
                sb.append(String.format("%02x", b));
            }
            
            // Compare with stored hash
            return sb.toString().equals(parts[1]);
            
        } catch (Exception e) {
            DAOLogger.error(className, "verifyPassword", "Error verifying password", e);
            return false;
        }
    }

    @Override
    public boolean insert(User user) {
        DAOLogger.info(className, "insert", "Creating new user: " + user.getUserId());
        
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(INSERT_USER)) {
            
            statement.setString(1, user.getUserId());
            statement.setString(2, user.getName());
            statement.setString(3, user.getRole());
            statement.setString(4, user.getDepartment());
            statement.setString(5, user.getAccessLevel());
            statement.setString(6, user.getStatus());
            statement.setString(7, user.getPasswordHash());
            
            int rowsAffected = statement.executeUpdate();
            boolean success = rowsAffected > 0;
            
            if (success) {
                DAOLogger.success(className, "insert", "User created successfully: " + user.getUserId());
            } else {
                DAOLogger.error(className, "insert", "Failed to create user: " + user.getUserId(), null);
            }
            
            return success;
            
        } catch (SQLException e) {
            DAOLogger.error(className, "insert", "Database error creating user: " + user.getUserId(), e);
            return false;
        }
    }

    @Override
    public User findById(String userId) {
        DAOLogger.info(className, "findById", "Reading user: " + userId);
        
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(SELECT_USER_BY_ID)) {
            
            statement.setString(1, userId);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    User user = mapResultSetToUser(resultSet);
                    DAOLogger.success(className, "findById", "User found: " + userId);
                    return user;
                } else {
                    DAOLogger.warn(className, "findById", "User not found: " + userId);
                    return null;
                }
            }
            
        } catch (SQLException e) {
            DAOLogger.error(className, "findById", "Database error reading user: " + userId, e);
            return null;
        }
    }

    @Override
    public boolean update(User user) {
        DAOLogger.info(className, "update", "Updating user: " + user.getUserId());
        
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(UPDATE_USER)) {
            
            statement.setString(1, user.getName());
            statement.setString(2, user.getRole());
            statement.setString(3, user.getDepartment());
            statement.setString(4, user.getAccessLevel());
            statement.setString(5, user.getStatus());
            statement.setString(6, user.getUserId());
            
            int rowsAffected = statement.executeUpdate();
            boolean success = rowsAffected > 0;
            
            if (success) {
                DAOLogger.success(className, "update", "User updated successfully: " + user.getUserId());
            } else {
                DAOLogger.error(className, "update", "Failed to update user: " + user.getUserId(), null);
            }
            
            return success;
            
        } catch (SQLException e) {
            DAOLogger.error(className, "update", "Database error updating user: " + user.getUserId(), e);
            return false;
        }
    }

    @Override
    public boolean delete(String userId) {
        DAOLogger.info(className, "delete", "Deactivating user: " + userId);
        
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(DELETE_USER)) {
            
            statement.setString(1, userId);
            
            int rowsAffected = statement.executeUpdate();
            boolean success = rowsAffected > 0;
            
            if (success) {
                DAOLogger.success(className, "delete", "User deactivated successfully: " + userId);
            } else {
                DAOLogger.error(className, "delete", "Failed to deactivate user: " + userId, null);
            }
            
            return success;
            
        } catch (SQLException e) {
            DAOLogger.error(className, "delete", "Database error deactivating user: " + userId, e);
            return false;
        }
    }

    @Override
    public List<User> findAll() {
        DAOLogger.info(className, "findAll", "Reading all users");
        List<User> users = new ArrayList<>();
        
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(SELECT_ALL_USERS);
             ResultSet resultSet = statement.executeQuery()) {
            
            while (resultSet.next()) {
                users.add(mapResultSetToUser(resultSet));
            }
            
            DAOLogger.success(className, "findAll", "Retrieved " + users.size() + " users");
            
        } catch (SQLException e) {
            DAOLogger.error(className, "findAll", "Database error reading all users", e);
        }
        
        return users;
    }

    /**
     * Get database connection (public implementation for BaseDAO interface)
     * @return Database connection
     * @throws SQLException if connection fails
     */
    @Override
    public Connection getConnection() throws SQLException {
        return super.getConnection();
    }

    /**
     * Update user password
     * @param userId User ID
     * @param newPassword New plain text password
     * @return true if update successful, false otherwise
     */
    public boolean updatePassword(String userId, String newPassword) {
        DAOLogger.info(className, "updatePassword", "Updating password for user: " + userId);
        
        String hashedPassword = hashPassword(newPassword);
        if (hashedPassword == null) {
            return false;
        }
        
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(UPDATE_PASSWORD)) {
            
            statement.setString(1, hashedPassword);
            statement.setString(2, userId);
            
            int rowsAffected = statement.executeUpdate();
            boolean success = rowsAffected > 0;
            
            if (success) {
                DAOLogger.success(className, "updatePassword", "Password updated successfully for user: " + userId);
            } else {
                DAOLogger.error(className, "updatePassword", "Failed to update password for user: " + userId, null);
            }
            
            return success;
            
        } catch (SQLException e) {
            DAOLogger.error(className, "updatePassword", "Database error updating password for user: " + userId, e);
            return false;
        }
    }

    /**
     * Check if user exists
     * @param userId User ID to check
     * @return true if user exists, false otherwise
     */
    public boolean userExists(String userId) {
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(CHECK_USER_EXISTS)) {
            
            statement.setString(1, userId);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1) > 0;
                }
            }
            
        } catch (SQLException e) {
            DAOLogger.error(className, "userExists", "Database error checking if user exists: " + userId, e);
        }
        
        return false;
    }

    /**
     * Map ResultSet to User object
     * @param resultSet Database result set
     * @return User object
     * @throws SQLException if mapping fails
     */
    private User mapResultSetToUser(ResultSet resultSet) throws SQLException {
        return new User(
            resultSet.getString("user_id"),
            resultSet.getString("name"),
            resultSet.getString("role"),
            resultSet.getString("department"),
            resultSet.getString("access_level"),
            resultSet.getString("status"),
            null // Don't expose password hash
        );
    }
}

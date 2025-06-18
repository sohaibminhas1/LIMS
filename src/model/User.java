package model;

/**
 * User model class representing a user in the LIMS system
 */
public class User {
    private String userId;
    private String name;
    private String role;
    private String department;
    private String accessLevel;
    private String status;
    private String passwordHash;

    /**
     * Default constructor
     */
    public User() {
    }

    /**
     * Constructor with all fields
     */
    public User(String userId, String name, String role, String department, 
                String accessLevel, String status, String passwordHash) {
        this.userId = userId;
        this.name = name;
        this.role = role;
        this.department = department;
        this.accessLevel = accessLevel;
        this.status = status;
        this.passwordHash = passwordHash;
    }

    /**
     * Constructor without password hash (for security)
     */
    public User(String userId, String name, String role, String department, 
                String accessLevel, String status) {
        this(userId, name, role, department, accessLevel, status, null);
    }

    // Getters and Setters
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getAccessLevel() {
        return accessLevel;
    }

    public void setAccessLevel(String accessLevel) {
        this.accessLevel = accessLevel;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    /**
     * Check if user is active
     */
    public boolean isActive() {
        return "Active".equals(status);
    }

    /**
     * Check if user is admin
     */
    public boolean isAdmin() {
        return "Admin".equals(role);
    }

    /**
     * Check if user is teacher
     */
    public boolean isTeacher() {
        return "Teacher".equals(role);
    }

    /**
     * Check if user is student
     */
    public boolean isStudent() {
        return "Student".equals(role);
    }

    /**
     * Check if user has full access
     */
    public boolean hasFullAccess() {
        return "Full".equals(accessLevel);
    }

    @Override
    public String toString() {
        return "User{" +
                "userId='" + userId + '\'' +
                ", name='" + name + '\'' +
                ", role='" + role + '\'' +
                ", department='" + department + '\'' +
                ", accessLevel='" + accessLevel + '\'' +
                ", status='" + status + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        User user = (User) obj;
        return userId != null ? userId.equals(user.userId) : user.userId == null;
    }

    @Override
    public int hashCode() {
        return userId != null ? userId.hashCode() : 0;
    }
}

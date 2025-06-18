package service;

/**
 * Main service class that coordinates all LIMS services for the GUI application.
 * This class acts as a facade for all the individual service classes.
 */
public class LIMSService {
    private static LIMSService instance;
    
    // Service instances
    private ComplaintService complaintService;
    private ComputerService computerService;
    private FeedbackService feedbackService;
    private LabReservationService labReservationService;
    private SoftwareRequestService softwareRequestService;
    private UserService userService;
    
    private LIMSService() {
        initializeServices();
    }
    
    public static synchronized LIMSService getInstance() {
        if (instance == null) {
            instance = new LIMSService();
        }
        return instance;
    }
    
    private void initializeServices() {
        complaintService = new ComplaintService();
        computerService = new ComputerService();
        feedbackService = new FeedbackService();
        labReservationService = new LabReservationService();
        softwareRequestService = new SoftwareRequestService();
        userService = new UserService();

        System.out.println("✅ All LIMS services initialized successfully");
    }
    

    
    /**
     * Authenticates a user with username and password using database authentication.
     * @param username The username
     * @param password The password
     * @return The user's role if authentication successful, null otherwise
     */
    public String authenticateUser(String username, String password) {
        model.User user = userService.authenticateUser(username, password);
        if (user != null && user.isActive()) {
            System.out.println("✅ Authentication successful for user: " + username + " (Role: " + user.getRole() + ")");
            return user.getRole();
        } else {
            System.out.println("❌ Authentication failed for user: " + username);
            return null;
        }
    }




    
    /**
     * Registers a new user using the UserService.
     * @param username The username
     * @param name The full name
     * @param password The password
     * @param role The user role
     * @param department The department
     * @param accessLevel The access level
     * @return true if registration successful, false if username already exists
     */
    public boolean registerUser(String username, String name, String password, String role,
                               String department, String accessLevel) {
        return userService.createUser(username, name, password, role, department, accessLevel);
    }
    
    /**
     * Changes a user's password using the UserService.
     * @param username The username
     * @param oldPassword The current password
     * @param newPassword The new password
     * @return true if password change successful, false otherwise
     */
    public boolean changePassword(String username, String oldPassword, String newPassword) {
        return userService.updatePassword(username, oldPassword, newPassword);
    }
    
    // Getter methods for service instances
    public ComplaintService getComplaintService() {
        return complaintService;
    }
    
    public ComputerService getComputerService() {
        return computerService;
    }
    
    public FeedbackService getFeedbackService() {
        return feedbackService;
    }
    
    public LabReservationService getLabReservationService() {
        return labReservationService;
    }
    
    public SoftwareRequestService getSoftwareRequestService() {
        return softwareRequestService;
    }
    
    /**
     * Gets a user's role using the UserService.
     * @param username The username
     * @return The user's role, or null if user doesn't exist
     */
    public String getUserRole(String username) {
        model.User user = userService.getUser(username);
        return user != null ? user.getRole() : null;
    }

    /**
     * Checks if a user exists using the UserService.
     * @param username The username
     * @return true if user exists, false otherwise
     */
    public boolean userExists(String username) {
        return userService.userExists(username);
    }

    /**
     * Get the UserService instance
     * @return UserService instance
     */
    public UserService getUserService() {
        return userService;
    }
}

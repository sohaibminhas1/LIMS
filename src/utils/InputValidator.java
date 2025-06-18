package utils;

import java.util.regex.Pattern;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Arrays;
import java.util.List;

public class InputValidator {
    // Regular expressions for validation
    private static final Pattern COMPUTER_ID_PATTERN = Pattern.compile("^PC-[A-Z0-9]{6}$");
    private static final Pattern LAB_ID_PATTERN = Pattern.compile("^LAB[0-9]{3}$");
    private static final Pattern USER_ID_PATTERN = Pattern.compile("^U[0-9]{3}$");
    private static final Pattern IP_ADDRESS_PATTERN = Pattern.compile("^\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}$");
    private static final Pattern DATE_PATTERN = Pattern.compile("^\\d{4}-\\d{2}-\\d{2}$");
    private static final Pattern TIME_PATTERN = Pattern.compile("^([01]?[0-9]|2[0-3]):[0-5][0-9]$");
    private static final Pattern VERSION_PATTERN = Pattern.compile("^[0-9]+\\.[0-9]+(\\.[0-9]+)?([a-zA-Z][0-9]*)?$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^[+]?[0-9]{10,15}$");
    private static final Pattern NAME_PATTERN = Pattern.compile("^[A-Za-z\\s.'-]{2,50}$");
    private static final Pattern ALPHANUMERIC_PATTERN = Pattern.compile("^[A-Za-z0-9\\s-_]{1,}$");

    // Valid options for dropdowns
    private static final List<String> VALID_ROLES = Arrays.asList("Admin", "Teacher", "Student", "Lab Technician");
    private static final List<String> VALID_DEPARTMENTS = Arrays.asList("IT", "Computer Science", "Software Engineering", "AI", "Data Science");
    private static final List<String> VALID_ACCESS_LEVELS = Arrays.asList("Full", "Limited", "Basic", "Read Only");
    private static final List<String> VALID_STATUSES = Arrays.asList("Active", "Inactive", "Pending", "Suspended", "Maintenance");
    private static final List<String> VALID_COMPUTER_STATUSES = Arrays.asList("Available", "In Use", "Maintenance", "Retired");
    private static final List<String> VALID_SOFTWARE_STATUSES = Arrays.asList("Pending", "In Progress", "Completed", "Approved", "Rejected");
    private static final List<String> VALID_COMPLAINT_STATUSES = Arrays.asList("Open", "In Progress", "Resolved", "Closed", "Cancelled");
    private static final List<String> VALID_LICENSE_TYPES = Arrays.asList("Open Source", "Academic", "Professional", "Enterprise", "Subscription");
    private static final List<String> VALID_URGENCY_LEVELS = Arrays.asList("Low", "Medium", "High", "Critical");
    private static final List<String> VALID_ISSUE_TYPES = Arrays.asList("Hardware", "Software", "Equipment", "Network", "Other");
    private static final List<String> VALID_PURPOSES = Arrays.asList("Class", "Research", "Workshop", "Training", "Meeting", "Exam");
    private static final List<String> VALID_MAINTENANCE_TYPES = Arrays.asList("Routine", "Emergency", "Upgrade", "Repair", "Cleaning");
    
    // Validation methods
    public static void validateComputerId(String computerId) throws IllegalArgumentException {
        if (computerId == null || computerId.trim().isEmpty()) {
            throw new IllegalArgumentException("Computer ID cannot be empty");
        }
        if (!COMPUTER_ID_PATTERN.matcher(computerId).matches()) {
            throw new IllegalArgumentException("Invalid Computer ID format. Expected format: PC-XXXXXX (where X is alphanumeric)");
        }
    }

    public static void validateIpAddress(String ipAddress) throws IllegalArgumentException {
        if (ipAddress == null || ipAddress.trim().isEmpty()) {
            throw new IllegalArgumentException("IP Address cannot be empty");
        }
        if (!IP_ADDRESS_PATTERN.matcher(ipAddress).matches()) {
            throw new IllegalArgumentException("Invalid IP Address format. Expected format: XXX.XXX.XXX.XXX");
        }
        // Validate each octet
        String[] octets = ipAddress.split("\\.");
        for (String octet : octets) {
            int value = Integer.parseInt(octet);
            if (value < 0 || value > 255) {
                throw new IllegalArgumentException("Invalid IP Address: Each octet must be between 0 and 255");
            }
        }
    }

    public static void validateDate(String date) throws IllegalArgumentException {
        if (date == null || date.trim().isEmpty()) {
            throw new IllegalArgumentException("Date cannot be empty");
        }
        if (!DATE_PATTERN.matcher(date).matches()) {
            throw new IllegalArgumentException("Invalid date format. Expected format: YYYY-MM-DD");
        }
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            sdf.setLenient(false);
            Date parsedDate = sdf.parse(date);
            // Check if date is not in the past
            if (parsedDate.before(new Date())) {
                throw new IllegalArgumentException("Date cannot be in the past");
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid date: " + e.getMessage());
        }
    }

    public static void validateVersion(String version) throws IllegalArgumentException {
        if (version == null || version.trim().isEmpty()) {
            throw new IllegalArgumentException("Version cannot be empty");
        }
        if (!VERSION_PATTERN.matcher(version).matches()) {
            throw new IllegalArgumentException("Invalid version format. Expected format: X.Y or X.Y.Z");
        }
    }

    public static void validateText(String text, String fieldName, int minLength, int maxLength) throws IllegalArgumentException {
        if (text == null || text.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " cannot be empty");
        }
        if (text.length() < minLength) {
            throw new IllegalArgumentException(fieldName + " must be at least " + minLength + " characters long");
        }
        if (text.length() > maxLength) {
            throw new IllegalArgumentException(fieldName + " must not exceed " + maxLength + " characters");
        }
    }

    public static void validateSelection(Object selection, String fieldName) throws IllegalArgumentException {
        if (selection == null) {
            throw new IllegalArgumentException(fieldName + " must be selected");
        }
    }

    public static void validateDuration(String duration) throws IllegalArgumentException {
        try {
            int hours = Integer.parseInt(duration);
            if (hours < 1 || hours > 8) {
                throw new IllegalArgumentException("Duration must be between 1 and 8 hours");
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid duration format. Please enter a number between 1 and 8");
        }
    }

    // Enhanced validation methods
    public static void validateLabId(String labId) throws IllegalArgumentException {
        if (labId == null || labId.trim().isEmpty()) {
            throw new IllegalArgumentException("Lab ID cannot be empty");
        }
        if (!LAB_ID_PATTERN.matcher(labId).matches()) {
            throw new IllegalArgumentException("Invalid Lab ID format. Expected format: LAB### (e.g., LAB001)");
        }
    }

    public static void validateUserId(String userId) throws IllegalArgumentException {
        if (userId == null || userId.trim().isEmpty()) {
            throw new IllegalArgumentException("User ID cannot be empty");
        }
        if (!USER_ID_PATTERN.matcher(userId).matches()) {
            throw new IllegalArgumentException("Invalid User ID format. Expected format: U### (e.g., U001)");
        }
    }

    public static void validateEmail(String email) throws IllegalArgumentException {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be empty");
        }
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new IllegalArgumentException("Invalid email format. Please enter a valid email address");
        }
    }

    public static void validatePhone(String phone) throws IllegalArgumentException {
        if (phone == null || phone.trim().isEmpty()) {
            throw new IllegalArgumentException("Phone number cannot be empty");
        }
        if (!PHONE_PATTERN.matcher(phone).matches()) {
            throw new IllegalArgumentException("Invalid phone number format. Please enter 10-15 digits");
        }
    }

    public static void validateName(String name) throws IllegalArgumentException {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be empty");
        }
        if (!NAME_PATTERN.matcher(name).matches()) {
            throw new IllegalArgumentException("Invalid name format. Only letters, spaces, dots, apostrophes, and hyphens are allowed");
        }
    }

    public static void validateAlphanumeric(String text, String fieldName) throws IllegalArgumentException {
        if (text == null || text.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " cannot be empty");
        }
        if (!ALPHANUMERIC_PATTERN.matcher(text).matches()) {
            throw new IllegalArgumentException(fieldName + " can only contain letters, numbers, spaces, hyphens, and underscores");
        }
    }

    public static void validateTime(String time) throws IllegalArgumentException {
        if (time == null || time.trim().isEmpty()) {
            throw new IllegalArgumentException("Time cannot be empty");
        }
        if (!TIME_PATTERN.matcher(time).matches()) {
            throw new IllegalArgumentException("Invalid time format. Expected format: HH:MM (24-hour format)");
        }
    }

    public static void validateNumericRange(String value, String fieldName, int min, int max) throws IllegalArgumentException {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " cannot be empty");
        }
        try {
            int numValue = Integer.parseInt(value.trim());
            if (numValue < min || numValue > max) {
                throw new IllegalArgumentException(fieldName + " must be between " + min + " and " + max);
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(fieldName + " must be a valid number");
        }
    }

    public static void validatePositiveInteger(String value, String fieldName) throws IllegalArgumentException {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " cannot be empty");
        }
        try {
            int numValue = Integer.parseInt(value.trim());
            if (numValue <= 0) {
                throw new IllegalArgumentException(fieldName + " must be a positive number");
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(fieldName + " must be a valid positive number");
        }
    }

    // Dropdown validation methods
    public static void validateRole(String role) throws IllegalArgumentException {
        if (role == null || role.trim().isEmpty()) {
            throw new IllegalArgumentException("Role must be selected");
        }
        if (!VALID_ROLES.contains(role)) {
            throw new IllegalArgumentException("Invalid role. Valid roles are: " + String.join(", ", VALID_ROLES));
        }
    }

    public static void validateDepartment(String department) throws IllegalArgumentException {
        if (department == null || department.trim().isEmpty()) {
            throw new IllegalArgumentException("Department must be selected");
        }
        if (!VALID_DEPARTMENTS.contains(department)) {
            throw new IllegalArgumentException("Invalid department. Valid departments are: " + String.join(", ", VALID_DEPARTMENTS));
        }
    }

    public static void validateAccessLevel(String accessLevel) throws IllegalArgumentException {
        if (accessLevel == null || accessLevel.trim().isEmpty()) {
            throw new IllegalArgumentException("Access level must be selected");
        }
        if (!VALID_ACCESS_LEVELS.contains(accessLevel)) {
            throw new IllegalArgumentException("Invalid access level. Valid levels are: " + String.join(", ", VALID_ACCESS_LEVELS));
        }
    }

    public static void validateStatus(String status) throws IllegalArgumentException {
        if (status == null || status.trim().isEmpty()) {
            throw new IllegalArgumentException("Status must be selected");
        }
        if (!VALID_STATUSES.contains(status)) {
            throw new IllegalArgumentException("Invalid status. Valid statuses are: " + String.join(", ", VALID_STATUSES));
        }
    }

    public static void validateComputerStatus(String status) throws IllegalArgumentException {
        if (status == null || status.trim().isEmpty()) {
            throw new IllegalArgumentException("Computer status must be selected");
        }
        if (!VALID_COMPUTER_STATUSES.contains(status)) {
            throw new IllegalArgumentException("Invalid computer status. Valid statuses are: " + String.join(", ", VALID_COMPUTER_STATUSES));
        }
    }

    public static void validateSoftwareStatus(String status) throws IllegalArgumentException {
        if (status == null || status.trim().isEmpty()) {
            throw new IllegalArgumentException("Software status must be selected");
        }
        if (!VALID_SOFTWARE_STATUSES.contains(status)) {
            throw new IllegalArgumentException("Invalid software status. Valid statuses are: " + String.join(", ", VALID_SOFTWARE_STATUSES));
        }
    }

    public static void validateComplaintStatus(String status) throws IllegalArgumentException {
        if (status == null || status.trim().isEmpty()) {
            throw new IllegalArgumentException("Complaint status must be selected");
        }
        if (!VALID_COMPLAINT_STATUSES.contains(status)) {
            throw new IllegalArgumentException("Invalid complaint status. Valid statuses are: " + String.join(", ", VALID_COMPLAINT_STATUSES));
        }
    }

    public static void validateLicenseType(String licenseType) throws IllegalArgumentException {
        if (licenseType == null || licenseType.trim().isEmpty()) {
            throw new IllegalArgumentException("License type must be selected");
        }
        if (!VALID_LICENSE_TYPES.contains(licenseType)) {
            throw new IllegalArgumentException("Invalid license type. Valid types are: " + String.join(", ", VALID_LICENSE_TYPES));
        }
    }

    public static void validateUrgencyLevel(String urgency) throws IllegalArgumentException {
        if (urgency == null || urgency.trim().isEmpty()) {
            throw new IllegalArgumentException("Urgency level must be selected");
        }
        if (!VALID_URGENCY_LEVELS.contains(urgency)) {
            throw new IllegalArgumentException("Invalid urgency level. Valid levels are: " + String.join(", ", VALID_URGENCY_LEVELS));
        }
    }

    public static void validateIssueType(String issueType) throws IllegalArgumentException {
        if (issueType == null || issueType.trim().isEmpty()) {
            throw new IllegalArgumentException("Issue type must be selected");
        }
        if (!VALID_ISSUE_TYPES.contains(issueType)) {
            throw new IllegalArgumentException("Invalid issue type. Valid types are: " + String.join(", ", VALID_ISSUE_TYPES));
        }
    }

    public static void validatePurpose(String purpose) throws IllegalArgumentException {
        if (purpose == null || purpose.trim().isEmpty()) {
            throw new IllegalArgumentException("Purpose must be selected");
        }
        if (!VALID_PURPOSES.contains(purpose)) {
            throw new IllegalArgumentException("Invalid purpose. Valid purposes are: " + String.join(", ", VALID_PURPOSES));
        }
    }

    public static void validateMaintenanceType(String maintenanceType) throws IllegalArgumentException {
        if (maintenanceType == null || maintenanceType.trim().isEmpty()) {
            throw new IllegalArgumentException("Maintenance type must be selected");
        }
        if (!VALID_MAINTENANCE_TYPES.contains(maintenanceType)) {
            throw new IllegalArgumentException("Invalid maintenance type. Valid types are: " + String.join(", ", VALID_MAINTENANCE_TYPES));
        }
    }

    // Special validation methods
    public static void validatePassword(String password) throws IllegalArgumentException {
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be empty");
        }
        if (password.length() < 8) {
            throw new IllegalArgumentException("Password must be at least 8 characters long");
        }
        if (!password.matches(".*[A-Z].*")) {
            throw new IllegalArgumentException("Password must contain at least one uppercase letter");
        }
        if (!password.matches(".*[a-z].*")) {
            throw new IllegalArgumentException("Password must contain at least one lowercase letter");
        }
        if (!password.matches(".*[0-9].*")) {
            throw new IllegalArgumentException("Password must contain at least one digit");
        }
        if (!password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?].*")) {
            throw new IllegalArgumentException("Password must contain at least one special character");
        }
    }

    public static void validateNotEmpty(String value, String fieldName) throws IllegalArgumentException {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " cannot be empty");
        }
    }

    public static void validateStringLength(String value, String fieldName, int minLength, int maxLength) throws IllegalArgumentException {
        validateNotEmpty(value, fieldName);
        if (value.length() < minLength) {
            throw new IllegalArgumentException(fieldName + " must be at least " + minLength + " characters long");
        }
        if (value.length() > maxLength) {
            throw new IllegalArgumentException(fieldName + " cannot exceed " + maxLength + " characters");
        }
    }

    public static void validateFutureDate(String date) throws IllegalArgumentException {
        validateDate(date);
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            sdf.setLenient(false);
            Date parsedDate = sdf.parse(date);
            Date today = new Date();
            if (parsedDate.before(today)) {
                throw new IllegalArgumentException("Date must be today or in the future");
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid date: " + e.getMessage());
        }
    }

    public static void validatePastOrPresentDate(String date) throws IllegalArgumentException {
        if (date == null || date.trim().isEmpty()) {
            throw new IllegalArgumentException("Date cannot be empty");
        }
        if (!DATE_PATTERN.matcher(date).matches()) {
            throw new IllegalArgumentException("Invalid date format. Expected format: YYYY-MM-DD");
        }
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            sdf.setLenient(false);
            Date parsedDate = sdf.parse(date);
            Date today = new Date();
            if (parsedDate.after(today)) {
                throw new IllegalArgumentException("Date cannot be in the future");
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid date: " + e.getMessage());
        }
    }
}
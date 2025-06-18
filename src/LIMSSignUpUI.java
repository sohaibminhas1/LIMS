import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.sql.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Pattern;

public class LIMSSignUpUI {
    private static JFrame frame;
    private static JTextField nameField;
    private static JTextField emailField;
    private static JTextField usernameField;
    private static JPasswordField passwordField;
    private static JPasswordField confirmPasswordField;

    public static void main(String[] args) {
        // Create the main frame
        frame = new JFrame("LIMS System - Sign Up");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH); // Full screen
        frame.setUndecorated(false);
        frame.getContentPane().setBackground(new Color(70, 120, 200));
        frame.setLayout(null);

        // Get screen dimensions
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int centerX = screenSize.width / 2;
        int centerY = screenSize.height / 2;

        // Create header label
        JLabel headerLabel = new JLabel("LIMS Sign Up", SwingConstants.CENTER);
        headerLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        headerLabel.setForeground(Color.WHITE);
        headerLabel.setBounds(0, 20, screenSize.width, 40);
        frame.add(headerLabel);

        // Real-time clock label in top-left corner
        JLabel clockLabel = new JLabel();
        clockLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        clockLabel.setForeground(Color.WHITE);
        clockLabel.setBounds(20, 20, 300, 30);
        frame.add(clockLabel);

        // Timer to update clock every second
        Timer timer = new Timer(1000, e -> {
            SimpleDateFormat formatter = new SimpleDateFormat("EEEE, MMM dd yyyy | HH:mm:ss");
            clockLabel.setText(formatter.format(new Date()));
        });
        timer.start();

        // Create sign up panel
        JPanel signupPanel = new JPanel();
        signupPanel.setLayout(null);
        signupPanel.setSize(400, 500);
        signupPanel.setBackground(Color.WHITE);
        signupPanel.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 1));
        signupPanel.setBounds(centerX - 200, centerY - 250, 400, 500);

        // Heading
        JLabel titleLabel = new JLabel("Create Account", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Montserrat", Font.BOLD, 24));
        titleLabel.setBounds(0, 30, 400, 30);
        signupPanel.add(titleLabel);

        // Full Name label and field
        JLabel nameLabel = new JLabel("Full Name:");
        nameLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        nameLabel.setBounds(75, 80, 250, 20);
        signupPanel.add(nameLabel);

        nameField = new JTextField();
        nameField.setBounds(75, 105, 250, 35);
        nameField.setFont(new Font("SansSerif", Font.PLAIN, 14));
        signupPanel.add(nameField);

        // Email label and field
        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        emailLabel.setBounds(75, 150, 250, 20);
        signupPanel.add(emailLabel);

        emailField = new JTextField();
        emailField.setBounds(75, 175, 250, 35);
        emailField.setFont(new Font("SansSerif", Font.PLAIN, 14));
        signupPanel.add(emailField);

        // Username label and field
        JLabel userLabel = new JLabel("Username:");
        userLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        userLabel.setBounds(75, 220, 250, 20);
        signupPanel.add(userLabel);

        usernameField = new JTextField();
        usernameField.setBounds(75, 245, 250, 35);
        usernameField.setFont(new Font("SansSerif", Font.PLAIN, 14));
        signupPanel.add(usernameField);

        // Password label and field
        JLabel passLabel = new JLabel("Password:");
        passLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        passLabel.setBounds(75, 290, 250, 20);
        signupPanel.add(passLabel);

        passwordField = new JPasswordField();
        passwordField.setBounds(75, 315, 250, 35);
        passwordField.setFont(new Font("SansSerif", Font.PLAIN, 14));
        signupPanel.add(passwordField);

        // Confirm password label and field
        JLabel confirmPassLabel = new JLabel("Confirm Password:");
        confirmPassLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        confirmPassLabel.setBounds(75, 360, 250, 20);
        signupPanel.add(confirmPassLabel);

        confirmPasswordField = new JPasswordField();
        confirmPasswordField.setBounds(75, 385, 250, 35);
        confirmPasswordField.setFont(new Font("SansSerif", Font.PLAIN, 14));
        signupPanel.add(confirmPasswordField);

        // Sign Up button with functionality
        JButton signupButton = new JButton("Sign Up");
        signupButton.setBounds(75, 430, 250, 35);
        signupButton.setBackground(new Color(46, 204, 113)); // Modern green color
        signupButton.setForeground(Color.WHITE);
        signupButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        signupButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // Add hover effect
        signupButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                signupButton.setBackground(new Color(39, 174, 96));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                signupButton.setBackground(new Color(46, 204, 113));
            }
        });

        // Add signup functionality
        signupButton.addActionListener(e -> {
            System.out.println("🚀 Signup button clicked!");
            handleSignup();
        });

        signupPanel.add(signupButton);

        // Login label with click behavior
        JLabel loginLabel = new JLabel("<HTML><U>Already have an account? Log in</U></HTML>");
        loginLabel.setForeground(Color.GRAY);
        loginLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        loginLabel.setBounds(100, 470, 200, 20);
        loginLabel.setHorizontalAlignment(SwingConstants.CENTER);
        loginLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        loginLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                LIMSNavigator.showLoginUI(); // Navigates back to login screen
            }
        });
        signupPanel.add(loginLabel);

        // Add panel and show frame
        frame.add(signupPanel);
        frame.setVisible(true);
    }

    /**
     * Handle signup form submission
     */
    private static void handleSignup() {
        System.out.println("🔄 Processing signup form...");
        try {
            // Get form data
            String fullName = nameField.getText().trim();
            String email = emailField.getText().trim();
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword());
            String confirmPassword = new String(confirmPasswordField.getPassword());

            // Validate inputs
            if (!validateSignupInputs(fullName, email, username, password, confirmPassword)) {
                return;
            }

            // Check if user already exists
            if (userExists(username, email)) {
                JOptionPane.showMessageDialog(frame,
                    "❌ User already exists!\n\nUsername or email is already registered.",
                    "Registration Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Hash password
            String hashedPassword = hashPassword(password);

            // Insert user into database
            if (insertUser(fullName, email, username, hashedPassword)) {
                // Success message
                JOptionPane.showMessageDialog(frame,
                    "🎉 Account created successfully!\n\n" +
                    "Username: " + username + "\n" +
                    "Email: " + email + "\n\n" +
                    "You can now log in with your credentials.",
                    "Registration Successful", JOptionPane.INFORMATION_MESSAGE);

                // Clear form fields
                nameField.setText("");
                emailField.setText("");
                usernameField.setText("");
                passwordField.setText("");
                confirmPasswordField.setText("");

                // Navigate back to login
                frame.dispose();
                LIMSNavigator.showLoginUI();

            } else {
                JOptionPane.showMessageDialog(frame,
                    "❌ Registration failed!\n\nPlease try again later.",
                    "Database Error", JOptionPane.ERROR_MESSAGE);
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame,
                "❌ An error occurred during registration:\n\n" + e.getMessage(),
                "Registration Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Validate signup form inputs
     */
    private static boolean validateSignupInputs(String fullName, String email, String username,
                                              String password, String confirmPassword) {
        // Check for empty fields
        if (fullName.isEmpty() || email.isEmpty() || username.isEmpty() ||
            password.isEmpty() || confirmPassword.isEmpty()) {
            JOptionPane.showMessageDialog(null,
                "⚠️ All fields are required!\n\nPlease fill in all the information.",
                "Validation Error", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        // Validate full name (at least 2 words)
        if (fullName.split("\\s+").length < 2) {
            JOptionPane.showMessageDialog(null,
                "⚠️ Please enter your full name!\n\nFirst and last name are required.",
                "Validation Error", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        // Validate email format
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        if (!Pattern.matches(emailRegex, email)) {
            JOptionPane.showMessageDialog(null,
                "⚠️ Invalid email format!\n\nPlease enter a valid email address.",
                "Validation Error", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        // Validate username (alphanumeric, 3-20 characters)
        if (username.length() < 3 || username.length() > 20) {
            JOptionPane.showMessageDialog(null,
                "⚠️ Username must be 3-20 characters long!",
                "Validation Error", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        if (!username.matches("^[a-zA-Z0-9._-]+$")) {
            JOptionPane.showMessageDialog(null,
                "⚠️ Username can only contain letters, numbers, dots, hyphens, and underscores!",
                "Validation Error", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        // Validate password strength
        if (password.length() < 6) {
            JOptionPane.showMessageDialog(null,
                "⚠️ Password must be at least 6 characters long!",
                "Validation Error", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        // Check password confirmation
        if (!password.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(null,
                "⚠️ Passwords do not match!\n\nPlease make sure both password fields are identical.",
                "Validation Error", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        return true;
    }

    /**
     * Check if user already exists in database
     */
    private static boolean userExists(String username, String email) {
        String checkSQL = "SELECT COUNT(*) FROM user_access WHERE user_id = ? OR name LIKE ?";

        try (Connection conn = DriverManager.getConnection(
                "jdbc:postgresql://localhost:5434/lims_db", "postgres", "superadmin");
             PreparedStatement stmt = conn.prepareStatement(checkSQL)) {

            stmt.setString(1, username);
            stmt.setString(2, "%" + email + "%");

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }

        } catch (SQLException e) {
            System.err.println("Error checking user existence: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Insert new user into database
     */
    private static boolean insertUser(String fullName, String email, String username, String hashedPassword) {
        String insertSQL = "INSERT INTO user_access (user_id, name, role, department, access_level, status, password_hash) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(
                "jdbc:postgresql://localhost:5434/lims_db", "postgres", "superadmin");
             PreparedStatement stmt = conn.prepareStatement(insertSQL)) {

            // Determine role based on email domain or default to Student
            String role = determineUserRole(email);
            String department = determineDepartment(email);
            String accessLevel = determineAccessLevel(role);

            stmt.setString(1, username);
            stmt.setString(2, fullName + " (" + email + ")");
            stmt.setString(3, role);
            stmt.setString(4, department);
            stmt.setString(5, accessLevel);
            stmt.setString(6, "Active");
            stmt.setString(7, hashedPassword);  // Store the hashed password

            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("✅ User registered successfully: " + username + " (" + fullName + ") with hashed password");
                return true;
            }

        } catch (SQLException e) {
            System.err.println("❌ Error inserting user: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Determine user role based on email domain
     */
    private static String determineUserRole(String email) {
        String domain = email.substring(email.indexOf("@") + 1).toLowerCase();

        // Check for academic domains
        if (domain.contains("edu") || domain.contains("ac.") || domain.contains("university")) {
            if (email.toLowerCase().contains("admin") || email.toLowerCase().contains("staff")) {
                return "Admin";
            } else if (email.toLowerCase().contains("teacher") || email.toLowerCase().contains("prof") ||
                      email.toLowerCase().contains("instructor") || email.toLowerCase().contains("faculty")) {
                return "Teacher";
            } else {
                return "Student";
            }
        }

        // Default to Student for other domains
        return "Student";
    }

    /**
     * Determine department based on email
     */
    private static String determineDepartment(String email) {
        String emailLower = email.toLowerCase();

        if (emailLower.contains("cs") || emailLower.contains("computer")) {
            return "Computer Science";
        } else if (emailLower.contains("se") || emailLower.contains("software")) {
            return "Software Engineering";
        } else if (emailLower.contains("ai") || emailLower.contains("ml")) {
            return "AI & Machine Learning";
        } else if (emailLower.contains("data")) {
            return "Data Science";
        } else if (emailLower.contains("it")) {
            return "IT";
        } else {
            return "General";
        }
    }

    /**
     * Determine access level based on role
     */
    private static String determineAccessLevel(String role) {
        switch (role) {
            case "Admin":
                return "Full";
            case "Teacher":
            case "Lab Technician":
                return "Limited";
            case "Student":
            default:
                return "Basic";
        }
    }

    /**
     * Hash password using SHA-256
     */
    private static String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = md.digest(password.getBytes());

            StringBuilder sb = new StringBuilder();
            for (byte b : hashedBytes) {
                sb.append(String.format("%02x", b));
            }

            return sb.toString();

        } catch (NoSuchAlgorithmException e) {
            System.err.println("Error hashing password: " + e.getMessage());
            return password; // Fallback to plain text (not recommended for production)
        }
    }
}

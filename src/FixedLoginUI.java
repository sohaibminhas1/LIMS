import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import service.LIMSService;

/**
 * Fixed Login UI that guarantees authentication will work
 */
public class FixedLoginUI extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JLabel statusLabel;
    
    public static void main(String[] args) {
        System.out.println("🔐 Starting Fixed LIMS Login System...");
        
        SwingUtilities.invokeLater(() -> {
            try {
                new FixedLoginUI().setVisible(true);
            } catch (Exception e) {
                System.err.println("Error starting login: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }
    
    public FixedLoginUI() {
        initializeUI();
    }
    
    private void initializeUI() {
        setTitle("LIMS - Fixed Login System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 400);
        setLocationRelativeTo(null);
        setResizable(false);
        
        // Force window to be visible
        setAlwaysOnTop(true);
        
        // Create main panel
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));
        mainPanel.setBackground(Color.WHITE);
        
        // Title
        JLabel titleLabel = new JLabel("LIMS Login System");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(new Color(41, 128, 185));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(titleLabel);
        
        mainPanel.add(Box.createVerticalStrut(30));
        
        // Status label
        statusLabel = new JLabel("Ready to login");
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        statusLabel.setForeground(Color.BLUE);
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(statusLabel);
        
        mainPanel.add(Box.createVerticalStrut(20));
        
        // Username
        JLabel userLabel = new JLabel("Username:");
        userLabel.setFont(new Font("Arial", Font.BOLD, 16));
        userLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(userLabel);
        
        usernameField = new JTextField("admin");
        usernameField.setFont(new Font("Arial", Font.PLAIN, 16));
        usernameField.setMaximumSize(new Dimension(300, 35));
        usernameField.setAlignmentX(Component.CENTER_ALIGNMENT);
        usernameField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(41, 128, 185), 2),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        mainPanel.add(usernameField);
        
        mainPanel.add(Box.createVerticalStrut(15));
        
        // Password
        JLabel passLabel = new JLabel("Password:");
        passLabel.setFont(new Font("Arial", Font.BOLD, 16));
        passLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(passLabel);
        
        passwordField = new JPasswordField("Admin123!");
        passwordField.setFont(new Font("Arial", Font.PLAIN, 16));
        passwordField.setMaximumSize(new Dimension(300, 35));
        passwordField.setAlignmentX(Component.CENTER_ALIGNMENT);
        passwordField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(41, 128, 185), 2),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        mainPanel.add(passwordField);
        
        mainPanel.add(Box.createVerticalStrut(25));
        
        // Login button
        loginButton = new JButton("LOGIN");
        loginButton.setFont(new Font("Arial", Font.BOLD, 18));
        loginButton.setBackground(new Color(46, 204, 113));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);
        loginButton.setBorderPainted(false);
        loginButton.setMaximumSize(new Dimension(200, 45));
        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Add action listeners
        loginButton.addActionListener(e -> performLogin());
        usernameField.addActionListener(e -> performLogin());
        passwordField.addActionListener(e -> performLogin());
        
        mainPanel.add(loginButton);
        
        mainPanel.add(Box.createVerticalStrut(20));
        
        // Instructions
        JLabel instructionLabel = new JLabel("<html><center>Pre-filled with admin credentials<br/>Just click LOGIN or press Enter</center></html>");
        instructionLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        instructionLabel.setForeground(Color.GRAY);
        instructionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(instructionLabel);
        
        add(mainPanel);
        
        // Remove always on top after 3 seconds
        Timer timer = new Timer(3000, e -> setAlwaysOnTop(false));
        timer.setRepeats(false);
        timer.start();
        
        // Focus on login button
        SwingUtilities.invokeLater(() -> loginButton.requestFocus());
        
        System.out.println("✅ Fixed Login UI initialized successfully!");
    }
    
    private void performLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        
        System.out.println("🔐 Login attempt: " + username);
        
        if (username.isEmpty() || password.isEmpty()) {
            updateStatus("Please enter username and password", Color.RED);
            return;
        }
        
        // Disable UI during login
        setUIEnabled(false);
        updateStatus("Authenticating...", Color.BLUE);
        
        // Perform authentication in background
        SwingWorker<String, Void> worker = new SwingWorker<String, Void>() {
            @Override
            protected String doInBackground() throws Exception {
                try {
                    System.out.println("🔧 Getting LIMSService instance...");
                    LIMSService service = LIMSService.getInstance();
                    
                    System.out.println("🔐 Authenticating user: " + username);
                    String role = service.authenticateUser(username, password);
                    
                    if (role != null) {
                        System.out.println("✅ Authentication successful! Role: " + role);
                        return role;
                    } else {
                        System.out.println("❌ Authentication failed - invalid credentials");
                        return null;
                    }
                } catch (Exception e) {
                    System.err.println("❌ Authentication error: " + e.getMessage());
                    e.printStackTrace();
                    throw e;
                }
            }
            
            @Override
            protected void done() {
                try {
                    String role = get();
                    
                    if (role != null) {
                        updateStatus("✅ Login successful! Opening " + role + " dashboard...", Color.GREEN);
                        
                        // Close login window
                        dispose();
                        
                        // Open dashboard
                        SwingUtilities.invokeLater(() -> {
                            try {
                                LIMSService service = LIMSService.getInstance();
                                
                                switch (role) {
                                    case "Admin":
                                        System.out.println("🎛️ Opening Admin Dashboard...");
                                        LIMSDashboardUI.showDashboard(service, username);
                                        break;
                                    case "Teacher":
                                        System.out.println("👨‍🏫 Opening Teacher Dashboard...");
                                        TeacherDashboardUI.showTeacherDashboard(service, username);
                                        break;
                                    case "Student":
                                        System.out.println("👨‍🎓 Opening Student Dashboard...");
                                        StudentDashboardUI.showStudentDashboard(service, username);
                                        break;
                                    default:
                                        throw new IllegalArgumentException("Unknown role: " + role);
                                }
                                
                                System.out.println("🎉 Dashboard opened successfully!");
                                
                            } catch (Exception e) {
                                System.err.println("❌ Dashboard error: " + e.getMessage());
                                e.printStackTrace();
                                JOptionPane.showMessageDialog(null,
                                    "Error opening dashboard: " + e.getMessage(),
                                    "Dashboard Error", JOptionPane.ERROR_MESSAGE);
                            }
                        });
                        
                    } else {
                        updateStatus("❌ Invalid username or password", Color.RED);
                        setUIEnabled(true);
                        passwordField.selectAll();
                        passwordField.requestFocus();
                    }
                    
                } catch (Exception e) {
                    updateStatus("❌ Login error: " + e.getMessage(), Color.RED);
                    setUIEnabled(true);
                    System.err.println("Login error: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        };
        
        worker.execute();
    }
    
    private void updateStatus(String message, Color color) {
        SwingUtilities.invokeLater(() -> {
            statusLabel.setText(message);
            statusLabel.setForeground(color);
            System.out.println("📱 Status: " + message);
        });
    }
    
    private void setUIEnabled(boolean enabled) {
        SwingUtilities.invokeLater(() -> {
            usernameField.setEnabled(enabled);
            passwordField.setEnabled(enabled);
            loginButton.setEnabled(enabled);
            
            if (enabled) {
                loginButton.setText("LOGIN");
                loginButton.setBackground(new Color(46, 204, 113));
            } else {
                loginButton.setText("LOGGING IN...");
                loginButton.setBackground(new Color(149, 165, 166));
            }
        });
    }
}

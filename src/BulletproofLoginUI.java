import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import service.LIMSService;

/**
 * Bulletproof Login UI that guarantees visibility and authentication
 */
public class BulletproofLoginUI {
    private static JFrame frame;
    private static JTextField usernameField;
    private static JPasswordField passwordField;
    private static JLabel statusLabel;
    
    public static void main(String[] args) {
        System.out.println("🚀 Starting Bulletproof LIMS Login...");
        
        // Use default look and feel
        
        SwingUtilities.invokeLater(() -> createAndShowGUI());
    }
    
    private static void createAndShowGUI() {
        frame = new JFrame("LIMS - Bulletproof Login");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 400);
        
        // Force window to be visible and on top
        frame.setAlwaysOnTop(true);
        frame.setLocationRelativeTo(null); // Center on screen
        frame.setResizable(false);
        
        // Create main panel
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));
        mainPanel.setBackground(Color.WHITE);
        
        // Title
        JLabel titleLabel = new JLabel("LIMS Login System");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(41, 128, 185));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(titleLabel);
        
        mainPanel.add(Box.createVerticalStrut(30));
        
        // Status label for feedback
        statusLabel = new JLabel("Ready to login");
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        statusLabel.setForeground(Color.BLUE);
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(statusLabel);
        
        mainPanel.add(Box.createVerticalStrut(20));
        
        // Username field
        JLabel userLabel = new JLabel("Username:");
        userLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        userLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(userLabel);
        
        usernameField = new JTextField("admin"); // Pre-fill admin
        usernameField.setFont(new Font("Arial", Font.PLAIN, 14));
        usernameField.setMaximumSize(new Dimension(300, 30));
        usernameField.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(usernameField);
        
        mainPanel.add(Box.createVerticalStrut(15));
        
        // Password field
        JLabel passLabel = new JLabel("Password:");
        passLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        passLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(passLabel);
        
        passwordField = new JPasswordField("Admin123!"); // Pre-fill password
        passwordField.setFont(new Font("Arial", Font.PLAIN, 14));
        passwordField.setMaximumSize(new Dimension(300, 30));
        passwordField.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(passwordField);
        
        mainPanel.add(Box.createVerticalStrut(25));
        
        // Login button
        JButton loginButton = new JButton("LOGIN");
        loginButton.setFont(new Font("Arial", Font.BOLD, 16));
        loginButton.setBackground(new Color(46, 204, 113));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);
        loginButton.setBorderPainted(false);
        loginButton.setMaximumSize(new Dimension(200, 40));
        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        loginButton.addActionListener(e -> performLogin());
        
        // Add Enter key support
        passwordField.addActionListener(e -> performLogin());
        usernameField.addActionListener(e -> performLogin());
        
        mainPanel.add(loginButton);
        
        mainPanel.add(Box.createVerticalStrut(20));
        
        // Test button for diagnostics
        JButton testButton = new JButton("Run Authentication Test");
        testButton.setFont(new Font("Arial", Font.PLAIN, 12));
        testButton.setBackground(new Color(52, 152, 219));
        testButton.setForeground(Color.WHITE);
        testButton.setFocusPainted(false);
        testButton.setBorderPainted(false);
        testButton.setMaximumSize(new Dimension(200, 30));
        testButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        testButton.addActionListener(e -> runDiagnosticTest());
        mainPanel.add(testButton);
        
        frame.add(mainPanel);
        
        // Make sure window is visible
        frame.setVisible(true);
        frame.toFront();
        frame.requestFocus();
        
        // Remove always on top after 2 seconds
        Timer timer = new Timer(2000, e -> frame.setAlwaysOnTop(false));
        timer.setRepeats(false);
        timer.start();
        
        System.out.println("✅ Bulletproof Login UI created and visible!");
    }
    
    private static void performLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        
        updateStatus("Authenticating...", Color.BLUE);
        
        // Disable button during authentication
        Component[] components = frame.getContentPane().getComponents();
        setComponentsEnabled(components, false);
        
        // Perform authentication in background thread
        SwingWorker<String, Void> worker = new SwingWorker<String, Void>() {
            @Override
            protected String doInBackground() throws Exception {
                try {
                    System.out.println("🔐 Attempting login: " + username);
                    LIMSService service = LIMSService.getInstance();
                    return service.authenticateUser(username, password);
                } catch (Exception e) {
                    System.err.println("❌ Login error: " + e.getMessage());
                    e.printStackTrace();
                    throw e;
                }
            }
            
            @Override
            protected void done() {
                try {
                    String role = get();
                    if (role != null) {
                        updateStatus("✅ Login Successful! Opening " + role + " Dashboard...", Color.GREEN);
                        
                        // Close login window
                        frame.dispose();
                        
                        // Open appropriate dashboard
                        SwingUtilities.invokeLater(() -> {
                            try {
                                LIMSService service = LIMSService.getInstance();
                                switch (role) {
                                    case "Admin":
                                        LIMSDashboardUI.showDashboard(service, username);
                                        break;
                                    case "Teacher":
                                        TeacherDashboardUI.showTeacherDashboard(service, username);
                                        break;
                                    case "Student":
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
                        updateStatus("❌ Invalid credentials! Please try again.", Color.RED);
                        setComponentsEnabled(frame.getContentPane().getComponents(), true);
                    }
                } catch (Exception e) {
                    updateStatus("❌ Login failed: " + e.getMessage(), Color.RED);
                    setComponentsEnabled(frame.getContentPane().getComponents(), true);
                    e.printStackTrace();
                }
            }
        };
        
        worker.execute();
    }
    
    private static void runDiagnosticTest() {
        updateStatus("Running diagnostic test...", Color.ORANGE);
        
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                try {
                    // Run the diagnostic
                    AuthenticationDiagnostic.main(new String[]{});
                } catch (Exception e) {
                    System.err.println("Diagnostic error: " + e.getMessage());
                }
                return null;
            }
            
            @Override
            protected void done() {
                updateStatus("✅ Diagnostic completed! Check console output.", Color.GREEN);
            }
        };
        
        worker.execute();
    }
    
    private static void updateStatus(String message, Color color) {
        SwingUtilities.invokeLater(() -> {
            statusLabel.setText(message);
            statusLabel.setForeground(color);
            System.out.println("📱 Status: " + message);
        });
    }
    
    private static void setComponentsEnabled(Component[] components, boolean enabled) {
        for (Component component : components) {
            if (component instanceof JPanel) {
                setComponentsEnabled(((JPanel) component).getComponents(), enabled);
            } else if (component instanceof JButton || component instanceof JTextField) {
                component.setEnabled(enabled);
            }
        }
    }
}

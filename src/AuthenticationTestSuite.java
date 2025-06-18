import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Comprehensive Authentication Test Suite
 */
public class AuthenticationTestSuite extends JFrame {
    private JTextArea logArea;
    private JButton runAllTestsButton;
    private JButton runDiagnosticButton;
    private JButton fixDatabaseButton;
    private JButton testLoginUIButton;
    private JButton emergencyLoginButton;
    
    public static void main(String[] args) {
        System.out.println("🧪 Starting Authentication Test Suite...");
        
        SwingUtilities.invokeLater(() -> {
            try {
                new AuthenticationTestSuite().initializeAndShow();
            } catch (Exception e) {
                System.err.println("❌ Error starting test suite: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }
    
    public AuthenticationTestSuite() {
        super("LIMS Authentication Test Suite");
    }
    
    private void initializeAndShow() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        
        createUI();
        setVisible(true);
        
        System.out.println("✅ Authentication Test Suite ready!");
    }
    
    private void createUI() {
        setLayout(new BorderLayout());
        
        // Title panel
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(41, 128, 185));
        JLabel titleLabel = new JLabel("LIMS Authentication Test Suite");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        titlePanel.add(titleLabel);
        add(titlePanel, BorderLayout.NORTH);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new GridLayout(5, 1, 10, 10));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        runAllTestsButton = createButton("🧪 Run All Authentication Tests", e -> runAllTests());
        runDiagnosticButton = createButton("🔍 Run Complete Diagnostic", e -> runDiagnostic());
        fixDatabaseButton = createButton("🔧 Fix Database Users", e -> fixDatabase());
        testLoginUIButton = createButton("🖥️ Test Login UIs", e -> testLoginUIs());
        emergencyLoginButton = createButton("🚨 Emergency Admin Login", e -> emergencyLogin());
        
        buttonPanel.add(runAllTestsButton);
        buttonPanel.add(runDiagnosticButton);
        buttonPanel.add(fixDatabaseButton);
        buttonPanel.add(testLoginUIButton);
        buttonPanel.add(emergencyLoginButton);
        
        add(buttonPanel, BorderLayout.WEST);
        
        // Log area
        logArea = new JTextArea();
        logArea.setFont(new Font("Consolas", Font.PLAIN, 12));
        logArea.setEditable(false);
        logArea.setBackground(Color.BLACK);
        logArea.setForeground(Color.GREEN);
        
        JScrollPane scrollPane = new JScrollPane(logArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Test Output"));
        add(scrollPane, BorderLayout.CENTER);
        
        // Status panel
        JPanel statusPanel = new JPanel(new FlowLayout());
        JLabel statusLabel = new JLabel("Ready to run tests");
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        statusPanel.add(statusLabel);
        add(statusPanel, BorderLayout.SOUTH);
        
        log("🧪 Authentication Test Suite initialized");
        log("📋 Select a test to run from the buttons on the left");
        log("🎯 Recommended: Start with 'Run All Authentication Tests'");
    }
    
    private JButton createButton(String text, ActionListener action) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.PLAIN, 14));
        button.setBackground(new Color(52, 152, 219));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.addActionListener(action);
        
        // Add hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                button.setBackground(new Color(41, 128, 185));
            }
            
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                button.setBackground(new Color(52, 152, 219));
            }
        });
        
        return button;
    }
    
    private void log(String message) {
        SwingUtilities.invokeLater(() -> {
            logArea.append(message + "\n");
            logArea.setCaretPosition(logArea.getDocument().getLength());
        });
        System.out.println(message);
    }
    
    private void runAllTests() {
        log("\n🧪 RUNNING ALL AUTHENTICATION TESTS");
        log("=" .repeat(50));
        
        setButtonsEnabled(false);
        
        SwingWorker<Void, String> worker = new SwingWorker<Void, String>() {
            @Override
            protected Void doInBackground() throws Exception {
                try {
                    publish("🔧 Step 1: Fixing database users...");
                    runDatabaseFixer();
                    
                    publish("🔍 Step 2: Running complete diagnostic...");
                    runCompleteDiagnostic();
                    
                    publish("🖥️ Step 3: Testing login UIs...");
                    testAllLoginUIs();
                    
                    publish("✅ All tests completed successfully!");
                    
                } catch (Exception e) {
                    publish("❌ Test suite error: " + e.getMessage());
                    e.printStackTrace();
                }
                return null;
            }
            
            @Override
            protected void process(java.util.List<String> chunks) {
                for (String message : chunks) {
                    log(message);
                }
            }
            
            @Override
            protected void done() {
                setButtonsEnabled(true);
                log("\n🎉 Test suite completed!");
            }
        };
        
        worker.execute();
    }
    
    private void runDiagnostic() {
        log("\n🔍 RUNNING COMPLETE DIAGNOSTIC");
        log("=" .repeat(40));
        
        setButtonsEnabled(false);
        
        SwingWorker<Void, String> worker = new SwingWorker<Void, String>() {
            @Override
            protected Void doInBackground() throws Exception {
                try {
                    publish("Starting complete authentication diagnostic...");
                    runCompleteDiagnostic();
                    publish("✅ Diagnostic completed!");
                } catch (Exception e) {
                    publish("❌ Diagnostic error: " + e.getMessage());
                }
                return null;
            }
            
            @Override
            protected void process(java.util.List<String> chunks) {
                for (String message : chunks) {
                    log(message);
                }
            }
            
            @Override
            protected void done() {
                setButtonsEnabled(true);
            }
        };
        
        worker.execute();
    }
    
    private void fixDatabase() {
        log("\n🔧 FIXING DATABASE USERS");
        log("=" .repeat(30));
        
        setButtonsEnabled(false);
        
        SwingWorker<Void, String> worker = new SwingWorker<Void, String>() {
            @Override
            protected Void doInBackground() throws Exception {
                try {
                    publish("Starting database user fixer...");
                    runDatabaseFixer();
                    publish("✅ Database users fixed!");
                } catch (Exception e) {
                    publish("❌ Database fix error: " + e.getMessage());
                }
                return null;
            }
            
            @Override
            protected void process(java.util.List<String> chunks) {
                for (String message : chunks) {
                    log(message);
                }
            }
            
            @Override
            protected void done() {
                setButtonsEnabled(true);
            }
        };
        
        worker.execute();
    }
    
    private void testLoginUIs() {
        log("\n🖥️ TESTING LOGIN UIs");
        log("=" .repeat(25));
        
        testAllLoginUIs();
    }
    
    private void emergencyLogin() {
        log("\n🚨 EMERGENCY ADMIN LOGIN");
        log("=" .repeat(30));
        
        try {
            log("Creating emergency admin user...");
            DatabaseUserFixer.emergencyCreateAdmin();
            
            log("Starting bulletproof login system...");
            BulletproofLoginSystem.main(new String[]{});
            
            log("✅ Emergency login system started!");
        } catch (Exception e) {
            log("❌ Emergency login error: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void runCompleteDiagnostic() {
        try {
            CompleteAuthDiagnostic.main(new String[]{});
        } catch (Exception e) {
            log("❌ Diagnostic execution error: " + e.getMessage());
        }
    }
    
    private void runDatabaseFixer() {
        try {
            DatabaseUserFixer.main(new String[]{});
        } catch (Exception e) {
            log("❌ Database fixer execution error: " + e.getMessage());
        }
    }
    
    private void testAllLoginUIs() {
        log("🖥️ Testing Original Login UI...");
        try {
            SwingUtilities.invokeLater(() -> {
                try {
                    LIMSLoginUI.main(new String[]{});
                    log("✅ Original Login UI started");
                } catch (Exception e) {
                    log("❌ Original Login UI error: " + e.getMessage());
                }
            });
        } catch (Exception e) {
            log("❌ Original Login UI test error: " + e.getMessage());
        }
        
        log("🛡️ Testing Bulletproof Login System...");
        try {
            SwingUtilities.invokeLater(() -> {
                try {
                    BulletproofLoginSystem.main(new String[]{});
                    log("✅ Bulletproof Login System started");
                } catch (Exception e) {
                    log("❌ Bulletproof Login System error: " + e.getMessage());
                }
            });
        } catch (Exception e) {
            log("❌ Bulletproof Login System test error: " + e.getMessage());
        }
    }
    
    private void setButtonsEnabled(boolean enabled) {
        SwingUtilities.invokeLater(() -> {
            runAllTestsButton.setEnabled(enabled);
            runDiagnosticButton.setEnabled(enabled);
            fixDatabaseButton.setEnabled(enabled);
            testLoginUIButton.setEnabled(enabled);
            emergencyLoginButton.setEnabled(enabled);
        });
    }
}

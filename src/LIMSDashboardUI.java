import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.awt.image.BufferedImage;
import service.LIMSService;
import service.StatisticsService;
import model.*;
import java.util.List;

import ui.DialogComponents;
import controller.*;
import java.util.Map;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

public class LIMSDashboardUI extends JFrame {
    private static JFrame mainFrame;
    private static JLabel clockLabel;
    private static LIMSService limsService;
    private static String currentUsername;
    
    // Controller instances
    private static ComputerController computerController;
    private static ComplaintController complaintController;
    private static FeedbackController feedbackController;
    private static LabReservationController labReservationController;
    private static SoftwareRequestController softwareRequestController;
    private static ReportController reportController;

    private static JPanel rightPanel;
    private static JPanel dynamicContentPanel;

    public LIMSDashboardUI(LIMSService service, String username) {
        limsService = service;
        currentUsername = username;
        
        // Initialize controllers
        computerController = new ComputerController(limsService.getComputerService());
        complaintController = new ComplaintController(limsService.getComplaintService());
        softwareRequestController = new SoftwareRequestController(limsService.getSoftwareRequestService());
        feedbackController = new FeedbackController(limsService.getFeedbackService());
        labReservationController = new LabReservationController(limsService.getLabReservationService());
        reportController = new ReportController(
            limsService.getComplaintService(),
            limsService.getSoftwareRequestService(),
            limsService.getFeedbackService()
        );

        // Initialize DialogComponents
        DialogComponents.initialize(limsService, username);

        // Create main frame
        mainFrame = new JFrame("LIMS Admin Dashboard");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setSize(1200, 800);
        mainFrame.setLocationRelativeTo(null);

        // Create main panel with BorderLayout
        JPanel mainPanel = new JPanel(new BorderLayout());

        // Create header panel
        JPanel headerPanel = createHeaderPanel();
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Create content panel
        JPanel contentPanel = createContentPanel();
        mainPanel.add(contentPanel, BorderLayout.CENTER);

        // Add main panel to frame
        mainFrame.add(mainPanel);
        mainFrame.setVisible(true);

        // Start clock
        startClock();
    }

    private static JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(41, 128, 185)); // Modern blue color
        headerPanel.setPreferredSize(new Dimension(1200, 70));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        // Welcome label with logo
        JPanel welcomePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        welcomePanel.setOpaque(false);
        JLabel logoLabel = new JLabel();
        try {
            ImageIcon icon = new ImageIcon(ImageIO.read(new File("src/ui/logo.jpg")));
            Image img = icon.getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH);
            logoLabel.setIcon(new ImageIcon(img));
        } catch (IOException e) {
            // If logo not found, just skip
        }
        welcomePanel.add(logoLabel);
        JLabel welcomeLabel = new JLabel("Welcome to LIMS Dashboard");
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        welcomeLabel.setForeground(Color.WHITE);
        welcomePanel.add(welcomeLabel);
        headerPanel.add(welcomePanel, BorderLayout.WEST);

        // Right panel with clock and profile button
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightPanel.setOpaque(false);

        // Clock label
        clockLabel = new JLabel();
        clockLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        clockLabel.setForeground(Color.WHITE);
        rightPanel.add(clockLabel);

        // Profile button
        JButton profileButton = createProfileButton();
        rightPanel.add(profileButton);

        headerPanel.add(rightPanel, BorderLayout.EAST);

        return headerPanel;
    }

    private static JPanel createContentPanel() {
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Create left panel for navigation
        JPanel leftPanel = createNavigationPanel();
        contentPanel.add(leftPanel, BorderLayout.WEST);

        // Create right panel for content
        JPanel rightPanel = createRightPanel();
        contentPanel.add(rightPanel, BorderLayout.CENTER);

        return contentPanel;
    }

    private static JPanel createNavigationPanel() {
        JPanel navPanel = new JPanel();
        navPanel.setLayout(new BoxLayout(navPanel, BoxLayout.Y_AXIS));
        navPanel.setPreferredSize(new Dimension(250, 0));
        navPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 20));
        navPanel.setBackground(new Color(245, 245, 245)); // Light gray background

        // Add navigation buttons
        String[] navItems = {
            "Lab Schedule",
            "Computer Inventory",
            "Software Management",
            "Report Management",
            "Laboratory Usage Reports",
            "Software Inventory",
            "Laboratory Management",
            "Complaint Tracking",
            "Installation Requests",
            "User Access Control",
            "Inventory Reports"
        };

        for (String item : navItems) {
            JButton button = createStyledButton(item);
            navPanel.add(button);
            navPanel.add(Box.createVerticalStrut(10));
        }

        return navPanel;
    }

    private static JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setMaximumSize(new Dimension(230, 45));
        button.setPreferredSize(new Dimension(230, 45));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        button.setBackground(new Color(41, 128, 185));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Add hover effect
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(52, 152, 219));
            }
            public void mouseExited(MouseEvent e) {
                button.setBackground(new Color(41, 128, 185));
            }
        });

        button.addActionListener(e -> handleNavigation(text));
        return button;
    }

    private static JPanel createRightPanel() {
        rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        rightPanel.setBackground(Color.WHITE);

        JLabel welcomeMessage = new JLabel("Welcome to LIMS Dashboard");
        welcomeMessage.setFont(new Font("Segoe UI", Font.BOLD, 28));
        welcomeMessage.setForeground(new Color(41, 128, 185));
        welcomeMessage.setHorizontalAlignment(SwingConstants.CENTER);
        rightPanel.add(welcomeMessage, BorderLayout.NORTH);

        dynamicContentPanel = new JPanel(new BorderLayout());
        dynamicContentPanel.setOpaque(false);
        rightPanel.add(dynamicContentPanel, BorderLayout.CENTER);

        JPanel statsPanel = new JPanel(new GridLayout(2, 2, 15, 15));
        statsPanel.setBackground(Color.WHITE);
        statsPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        // Get real statistics from database
        Map<String, String> stats = StatisticsService.getAdminStatistics();
        statsPanel.add(createStatBox("Active Labs", stats.get("Active Labs"), new Color(52, 152, 219)));
        statsPanel.add(createStatBox("Total Computers", stats.get("Total Computers"), new Color(46, 204, 113)));
        statsPanel.add(createStatBox("Software Requests", stats.get("Software Requests"), new Color(155, 89, 182)));
        statsPanel.add(createStatBox("Pending Reports", stats.get("Pending Reports"), new Color(241, 196, 15)));
        rightPanel.add(statsPanel, BorderLayout.SOUTH);

        return rightPanel;
    }

    private static JPanel createStatBox(String title, String value, Color color) {
        JPanel box = new JPanel();
        box.setLayout(new BoxLayout(box, BoxLayout.Y_AXIS));
        box.setBackground(Color.WHITE);
        box.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 230)),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        valueLabel.setForeground(color);
        valueLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        titleLabel.setForeground(Color.GRAY);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        box.add(valueLabel);
        box.add(Box.createVerticalStrut(5));
        box.add(titleLabel);

        return box;
    }

    private static void handleNavigation(String item) {
        System.out.println("\n🔄 Dashboard.handleNavigation called for: " + item);
        System.out.println("📤 Removing all components from dynamicContentPanel...");

        // Clear content immediately for responsive UI
        dynamicContentPanel.removeAll();

        // Show loading indicator
        JLabel loadingLabel = new JLabel("🔄 Loading " + item + "...", SwingConstants.CENTER);
        loadingLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        loadingLabel.setForeground(new Color(41, 128, 185));
        dynamicContentPanel.add(loadingLabel, BorderLayout.CENTER);
        dynamicContentPanel.revalidate();
        dynamicContentPanel.repaint();

        // Create panel asynchronously with data loading
        SwingWorker<JPanel, String> worker = new SwingWorker<JPanel, String>() {
            @Override
            protected JPanel doInBackground() throws Exception {
                publish("🏗️ Creating panel for: " + item);

                // Ensure database connections are ready
                publish("📊 Loading data for " + item + "...");
                Thread.sleep(100); // Brief pause for data loading

                JPanel panel = null;
                switch (item) {
                    case "Lab Schedule":
                        publish("📅 Loading lab schedule data...");
                        panel = DialogComponents.getLabSchedulePanel();
                        break;
                    case "Computer Inventory":
                        publish("💻 Loading computer inventory data...");
                        panel = DialogComponents.getComputerInventoryPanel();
                        break;
                    case "Software Management":
                        publish("🔧 Loading software management data...");
                        panel = DialogComponents.getSoftwareManagementPanel();
                        break;
                    case "Report Management":
                        publish("📊 Loading report data...");
                        panel = DialogComponents.getReportStatusPanel();
                        break;
                    case "Laboratory Usage Reports":
                        publish("📈 Loading usage reports...");
                        panel = DialogComponents.getLaboratoryUsageReportPanel();
                        break;
                    case "Software Inventory":
                        publish("📦 Loading software inventory...");
                        panel = DialogComponents.getSoftwareInventoryPanel();
                        break;
                    case "Laboratory Management":
                        publish("🔬 Loading laboratory data...");
                        panel = DialogComponents.getLaboratoryManagementPanel();
                        break;
                    case "Complaint Tracking":
                        publish("🎫 Loading complaint data...");
                        panel = DialogComponents.getComplaintTrackingPanel();
                        break;
                    case "Installation Requests":
                        publish("⚙️ Loading installation requests...");
                        panel = DialogComponents.getInstallationRequestsPanel();
                        break;
                    case "User Access Control":
                        publish("👥 Loading user access data...");
                        panel = DialogComponents.getUserAccessControlPanel();
                        break;
                    case "Inventory Reports":
                        publish("📋 Loading inventory reports...");
                        panel = DialogComponents.getInventoryReportPanel();
                        break;
                    default:
                        System.out.println("⚠️ Unknown navigation item: " + item);
                        break;
                }

                if (panel != null) {
                    publish("✅ Data loaded successfully for " + item);
                    Thread.sleep(200); // Allow data to settle
                }

                return panel;
            }

            @Override
            protected void process(java.util.List<String> chunks) {
                // Update loading indicator with progress
                if (!chunks.isEmpty()) {
                    String latestMessage = chunks.get(chunks.size() - 1);
                    SwingUtilities.invokeLater(() -> {
                        // Update the loading label text
                        Component[] components = dynamicContentPanel.getComponents();
                        for (Component comp : components) {
                            if (comp instanceof JLabel) {
                                ((JLabel) comp).setText("🔄 " + latestMessage);
                                dynamicContentPanel.revalidate();
                                break;
                            }
                        }
                    });
                }
            }

            @Override
            protected void done() {
                try {
                    JPanel panel = get();

                    // Clear loading indicator
                    dynamicContentPanel.removeAll();

                    if (panel != null) {
                        System.out.println("✅ Panel created successfully for: " + item);
                        System.out.println("📦 Panel components: " + panel.getComponentCount());

                        System.out.println("➕ Adding panel to dynamicContentPanel...");
                        dynamicContentPanel.add(panel, BorderLayout.CENTER);

                        System.out.println("🔄 Calling revalidate() and repaint()...");
                        dynamicContentPanel.revalidate();
                        dynamicContentPanel.repaint();

                        System.out.println("✅ Panel navigation completed for: " + item);
                    } else {
                        System.out.println("❌ Panel is null for: " + item);
                        JLabel errorLabel = new JLabel("❌ Failed to load " + item, SwingConstants.CENTER);
                        errorLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
                        errorLabel.setForeground(Color.RED);
                        dynamicContentPanel.add(errorLabel, BorderLayout.CENTER);
                        dynamicContentPanel.revalidate();
                        dynamicContentPanel.repaint();
                    }

                } catch (Exception e) {
                    System.err.println("❌ Error creating panel for " + item + ": " + e.getMessage());
                    e.printStackTrace();

                    dynamicContentPanel.removeAll();
                    JLabel errorLabel = new JLabel("❌ Error loading " + item + ": " + e.getMessage(), SwingConstants.CENTER);
                    errorLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
                    errorLabel.setForeground(Color.RED);
                    dynamicContentPanel.add(errorLabel, BorderLayout.CENTER);
                    dynamicContentPanel.revalidate();
                    dynamicContentPanel.repaint();
                }
            }
        };

        worker.execute();
    }

    private static JButton createProfileButton() {
        JButton profileButton = new JButton("👤 " + currentUsername);
        profileButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        profileButton.setForeground(Color.WHITE);
        profileButton.setBackground(new Color(52, 152, 219));
        profileButton.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        profileButton.setFocusPainted(false);
        profileButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Hover effect
        profileButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                profileButton.setBackground(new Color(41, 128, 185));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                profileButton.setBackground(new Color(52, 152, 219));
            }
        });

        profileButton.addActionListener(e -> showProfilePopup(profileButton));
        return profileButton;
    }

    private static void showProfilePopup(JButton profileButton) {
        JDialog profileDialog = new JDialog(mainFrame, "User Profile", true);
        profileDialog.setSize(300, 400);
        profileDialog.setLocationRelativeTo(profileButton);
        profileDialog.setResizable(false);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Profile image panel
        JPanel imagePanel = new JPanel(new FlowLayout());
        imagePanel.setBackground(Color.WHITE);

        // Create default user avatar
        JLabel avatarLabel = new JLabel();
        avatarLabel.setIcon(createDefaultAvatar());
        imagePanel.add(avatarLabel);

        // User info panel
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBackground(Color.WHITE);
        infoPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        JLabel nameLabel = new JLabel(currentUsername);
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        nameLabel.setForeground(new Color(41, 128, 185));
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Get user role from service
        String userRole = "User";
        try {
            if (limsService != null) {
                userRole = limsService.getUserRole(currentUsername);
                if (userRole == null) userRole = "User";
            }
        } catch (Exception e) {
            System.err.println("Error getting user role: " + e.getMessage());
        }

        JLabel roleLabel = new JLabel(userRole);
        roleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        roleLabel.setForeground(Color.GRAY);
        roleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        infoPanel.add(nameLabel);
        infoPanel.add(Box.createVerticalStrut(5));
        infoPanel.add(roleLabel);

        // Sign out button
        JButton signOutButton = new JButton("Sign Out");
        signOutButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        signOutButton.setBackground(new Color(231, 76, 60));
        signOutButton.setForeground(Color.WHITE);
        signOutButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        signOutButton.setFocusPainted(false);
        signOutButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        signOutButton.addActionListener(e -> {
            int result = JOptionPane.showConfirmDialog(
                profileDialog,
                "Are you sure you want to sign out?",
                "Confirm Sign Out",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
            );

            if (result == JOptionPane.YES_OPTION) {
                profileDialog.dispose();
                mainFrame.dispose();
                // Return to login screen
                SwingUtilities.invokeLater(() -> {
                    new LIMSLoginUI().setVisible(true);
                });
            }
        });

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(signOutButton);

        mainPanel.add(imagePanel, BorderLayout.NORTH);
        mainPanel.add(infoPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        profileDialog.add(mainPanel);
        profileDialog.setVisible(true);
    }

    private static ImageIcon createDefaultAvatar() {
        int size = 80;
        BufferedImage avatar = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = avatar.createGraphics();

        // Enable antialiasing
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Draw circle background
        g2d.setColor(new Color(52, 152, 219));
        g2d.fillOval(0, 0, size, size);

        // Draw user icon
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Segoe UI", Font.BOLD, 36));
        FontMetrics fm = g2d.getFontMetrics();
        String userIcon = "👤";
        int x = (size - fm.stringWidth(userIcon)) / 2;
        int y = ((size - fm.getHeight()) / 2) + fm.getAscent();
        g2d.drawString(userIcon, x, y);

        g2d.dispose();
        return new ImageIcon(avatar);
    }

    private static void startClock() {
        Timer timer = new Timer(1000, e -> {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            clockLabel.setText(sdf.format(new Date()));
        });
        timer.start();
    }

    public static void showDashboard(LIMSService service, String username) {
        System.out.println("\n👨‍💼 INITIALIZING ADMIN DASHBOARD");
        System.out.println("=" .repeat(50));
        System.out.println("👤 Username: " + username);
        System.out.println("🔧 Service: " + (service != null ? "✅ Available" : "❌ Null"));

        // Ensure UI creation happens on EDT
        SwingUtilities.invokeLater(() -> {
            try {
                System.out.println("🏗️ Creating LIMSDashboardUI instance on EDT...");

                // Show loading indicator
                JFrame loadingFrame = createLoadingFrame();
                loadingFrame.setVisible(true);

                // Create dashboard in background with proper data loading
                SwingWorker<LIMSDashboardUI, String> worker = new SwingWorker<LIMSDashboardUI, String>() {
                    @Override
                    protected LIMSDashboardUI doInBackground() throws Exception {
                        publish("🔧 Initializing services and controllers...");
                        Thread.sleep(200); // Allow UI to settle

                        publish("📊 Pre-loading database connections...");
                        // Pre-warm database connections and verify data availability
                        preloadDatabaseData();

                        publish("🏗️ Creating dashboard components...");
                        LIMSDashboardUI dashboard = new LIMSDashboardUI(service, username);

                        publish("✅ Dashboard ready!");
                        Thread.sleep(300); // Brief pause to show completion

                        return dashboard;
                    }

                    @Override
                    protected void process(java.util.List<String> chunks) {
                        // Update loading screen with progress
                        if (!chunks.isEmpty()) {
                            String latestMessage = chunks.get(chunks.size() - 1);
                            updateLoadingMessage(loadingFrame, latestMessage);
                        }
                    }

                    @Override
                    protected void done() {
                        try {
                            LIMSDashboardUI dashboard = get();

                            // Ensure all data is loaded before showing dashboard
                            SwingUtilities.invokeLater(() -> {
                                loadingFrame.dispose();

                                System.out.println("🖥️ Making dashboard visible with loaded data...");
                                mainFrame.setVisible(true);
                                LIMSNavigator.setCurrentFrame(mainFrame);

                                // Trigger initial data refresh for all components
                                refreshAllDashboardData();

                                System.out.println("✅ Admin Dashboard initialized successfully with data!");
                                System.out.println("📋 Ready for navigation - data should be visible immediately");
                            });

                        } catch (Exception e) {
                            loadingFrame.dispose();
                            System.err.println("❌ Error creating dashboard: " + e.getMessage());
                            e.printStackTrace();
                            JOptionPane.showMessageDialog(null,
                                "Error initializing dashboard: " + e.getMessage(),
                                "Initialization Error",
                                JOptionPane.ERROR_MESSAGE);
                        }
                    }
                };
                worker.execute();

            } catch (Exception e) {
                System.err.println("❌ Error initializing Admin Dashboard:");
                e.printStackTrace();
                JOptionPane.showMessageDialog(null,
                    "Error initializing dashboard: " + e.getMessage(),
                    "Initialization Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    private static JFrame createLoadingFrame() {
        JFrame loadingFrame = new JFrame("Loading Dashboard...");
        loadingFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        loadingFrame.setSize(400, 200);
        loadingFrame.setLocationRelativeTo(null);
        loadingFrame.setUndecorated(true);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createLineBorder(new Color(41, 128, 185), 2));

        JLabel loadingLabel = new JLabel("🔄 Loading LIMS Dashboard...", SwingConstants.CENTER);
        loadingLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        loadingLabel.setForeground(new Color(41, 128, 185));

        JProgressBar progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);
        progressBar.setStringPainted(true);
        progressBar.setString("Initializing components...");

        panel.add(loadingLabel, BorderLayout.CENTER);
        panel.add(progressBar, BorderLayout.SOUTH);

        loadingFrame.add(panel);
        return loadingFrame;
    }

    private static void preloadDatabaseData() {
        try {
            System.out.println("🔄 Pre-loading database connections and data...");

            // Test database connectivity
            LIMSService service = LIMSService.getInstance();

            // Pre-warm table models to ensure data is available
            System.out.println("📊 Pre-loading complaint data...");
            ui.DatabaseTableModel.getComplaintTableModel();

            System.out.println("💻 Pre-loading computer data...");
            ui.DatabaseTableModel.getComputerTableModel();

            System.out.println("🔬 Pre-loading lab data...");
            ui.DatabaseTableModel.getLabTableModel();

            System.out.println("📋 Pre-loading software request data...");
            ui.DatabaseTableModel.getSoftwareRequestTableModel();

            System.out.println("✅ Database data pre-loaded successfully!");

        } catch (Exception e) {
            System.err.println("⚠️ Warning: Could not pre-load all data: " + e.getMessage());
            // Continue anyway - data will load on demand
        }
    }

    private static void updateLoadingMessage(JFrame loadingFrame, String message) {
        SwingUtilities.invokeLater(() -> {
            Component[] components = loadingFrame.getContentPane().getComponents();
            if (components.length > 0 && components[0] instanceof JPanel) {
                JPanel panel = (JPanel) components[0];
                Component[] panelComponents = panel.getComponents();
                for (Component comp : panelComponents) {
                    if (comp instanceof JProgressBar) {
                        ((JProgressBar) comp).setString(message);
                        break;
                    }
                }
            }
        });
    }

    private static void refreshAllDashboardData() {
        try {
            System.out.println("🔄 Refreshing all dashboard data...");

            // Refresh table refresh manager
            ui.TableRefreshManager.getInstance().refreshAllTables();

            System.out.println("✅ Dashboard data refresh completed!");

        } catch (Exception e) {
            System.err.println("⚠️ Warning: Could not refresh all dashboard data: " + e.getMessage());
        }
    }
}
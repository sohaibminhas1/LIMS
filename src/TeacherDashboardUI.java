import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.awt.image.BufferedImage;
import service.LIMSService;
import service.StatisticsService;
import model.*;
import java.util.List;
import java.util.Map;
import controller.*;
import ui.DialogComponents;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

public class TeacherDashboardUI extends JFrame {
    private static LIMSService limsService;
    private JFrame mainFrame;
    private JLabel clockLabel;
    private String currentUsername;
    
    // Controller instances
    private SoftwareRequestController softwareRequestController;

    private JPanel rightPanel;
    private JPanel dynamicContentPanel;

    public TeacherDashboardUI(LIMSService service, String username) {
        limsService = service;
        currentUsername = username;
        
        // Initialize controllers
        softwareRequestController = new SoftwareRequestController(limsService.getSoftwareRequestService());

        // Initialize DialogComponents
        DialogComponents.initialize(limsService, username);

        // Create main frame
        mainFrame = new JFrame("LIMS Teacher Dashboard");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setSize(1200, 800);
        mainFrame.setLocationRelativeTo(null);

        // Create main panel with BorderLayout
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(245, 245, 245));

        // Create header panel
        JPanel headerPanel = createHeaderPanel();
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Create content panel
        JPanel contentPanel = createContentPanel();
        mainPanel.add(contentPanel, BorderLayout.CENTER);

        // Add main panel to frame
        mainFrame.add(mainPanel);

        // Start clock
        startClock();
    }

    public static void showTeacherDashboard(LIMSService service, String username) {
        System.out.println("\n🎓 INITIALIZING TEACHER DASHBOARD");
        System.out.println("=" .repeat(50));
        System.out.println("👤 Username: " + username);
        System.out.println("🔧 Service: " + (service != null ? "✅ Available" : "❌ Null"));

        // Ensure UI creation happens on EDT
        SwingUtilities.invokeLater(() -> {
            try {
                System.out.println("🏗️ Creating TeacherDashboardUI instance on EDT...");

                // Show loading indicator
                JFrame loadingFrame = createLoadingFrame();
                loadingFrame.setVisible(true);

                // Create dashboard in background with proper data loading
                SwingWorker<TeacherDashboardUI, String> worker = new SwingWorker<TeacherDashboardUI, String>() {
                    @Override
                    protected TeacherDashboardUI doInBackground() throws Exception {
                        publish("🔧 Initializing teacher services...");
                        Thread.sleep(200); // Allow UI to settle

                        publish("📊 Pre-loading teacher data...");
                        preloadTeacherData();

                        publish("🏗️ Creating teacher dashboard...");
                        TeacherDashboardUI dashboard = new TeacherDashboardUI(service, username);

                        publish("✅ Teacher dashboard ready!");
                        Thread.sleep(300); // Brief pause to show completion

                        return dashboard;
                    }

                    @Override
                    protected void process(java.util.List<String> chunks) {
                        if (!chunks.isEmpty()) {
                            String latestMessage = chunks.get(chunks.size() - 1);
                            updateLoadingMessage(loadingFrame, latestMessage);
                        }
                    }

                    @Override
                    protected void done() {
                        try {
                            TeacherDashboardUI dashboard = get();

                            SwingUtilities.invokeLater(() -> {
                                loadingFrame.dispose();

                                System.out.println("🖥️ Making teacher dashboard visible with loaded data...");
                                dashboard.mainFrame.setVisible(true);
                                LIMSNavigator.setCurrentFrame(dashboard.mainFrame);

                                // Refresh teacher-specific data
                                refreshTeacherData();

                                System.out.println("✅ Teacher Dashboard initialized successfully with data!");
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
                System.err.println("❌ Error initializing Teacher Dashboard:");
                e.printStackTrace();
                JOptionPane.showMessageDialog(null,
                    "Error initializing dashboard: " + e.getMessage(),
                    "Initialization Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    private static JFrame createLoadingFrame() {
        JFrame loadingFrame = new JFrame("Loading Teacher Dashboard...");
        loadingFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        loadingFrame.setSize(400, 200);
        loadingFrame.setLocationRelativeTo(null);
        loadingFrame.setUndecorated(true);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createLineBorder(new Color(41, 128, 185), 2));

        JLabel loadingLabel = new JLabel("🔄 Loading Teacher Dashboard...", SwingConstants.CENTER);
        loadingLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        loadingLabel.setForeground(new Color(41, 128, 185));

        JProgressBar progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);
        progressBar.setStringPainted(true);
        progressBar.setString("Initializing teacher features...");

        panel.add(loadingLabel, BorderLayout.CENTER);
        panel.add(progressBar, BorderLayout.SOUTH);

        loadingFrame.add(panel);
        return loadingFrame;
    }

    private static void preloadTeacherData() {
        try {
            System.out.println("🔄 Pre-loading teacher-specific data...");

            // Pre-warm teacher-relevant table models
            System.out.println("📋 Pre-loading software request data...");
            ui.DatabaseTableModel.getSoftwareRequestTableModel();

            System.out.println("🔬 Pre-loading lab schedule data...");
            ui.DatabaseTableModel.getLabTableModel();

            System.out.println("📊 Pre-loading feedback data...");
            ui.DatabaseTableModel.getFeedbackTableModel();

            System.out.println("✅ Teacher data pre-loaded successfully!");

        } catch (Exception e) {
            System.err.println("⚠️ Warning: Could not pre-load all teacher data: " + e.getMessage());
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

    private static void refreshTeacherData() {
        try {
            System.out.println("🔄 Refreshing teacher dashboard data...");
            ui.TableRefreshManager.getInstance().refreshAllTables();
            System.out.println("✅ Teacher data refresh completed!");
        } catch (Exception e) {
            System.err.println("⚠️ Warning: Could not refresh teacher data: " + e.getMessage());
        }
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(41, 128, 185));
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
        JLabel welcomeLabel = new JLabel("Welcome, " + currentUsername);
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

    private JPanel createContentPanel() {
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        contentPanel.setBackground(new Color(245, 245, 245));

        // Create left panel for navigation
        JPanel leftPanel = createNavigationPanel();
        contentPanel.add(leftPanel, BorderLayout.WEST);

        // Create right panel for content
        JPanel rightPanel = createRightPanel();
        contentPanel.add(rightPanel, BorderLayout.CENTER);

        return contentPanel;
    }

    private JPanel createNavigationPanel() {
        JPanel navPanel = new JPanel();
        navPanel.setLayout(new BoxLayout(navPanel, BoxLayout.Y_AXIS));
        navPanel.setPreferredSize(new Dimension(250, 0));
        navPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 20));
        navPanel.setBackground(new Color(245, 245, 245));

        // Add navigation buttons
        String[] navItems = {
            "Software Requests",
            "Submit Feedback",
            "View Your Request Status",
            "Lab Schedule"
        };

        for (String item : navItems) {
            JButton button = createStyledButton(item);
            navPanel.add(button);
            navPanel.add(Box.createVerticalStrut(10));
        }

        return navPanel;
    }

    private JButton createStyledButton(String text) {
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

    private JPanel createRightPanel() {
        rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        rightPanel.setBackground(Color.WHITE);

        JLabel welcomeMessage = new JLabel("Teacher Dashboard Overview");
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
        Map<String, String> stats = StatisticsService.getTeacherStatistics(currentUsername);
        statsPanel.add(createStatBox("Pending Complaints", stats.get("Pending Complaints"), new Color(52, 152, 219)));
        statsPanel.add(createStatBox("Software Requests", stats.get("Software Requests"), new Color(46, 204, 113)));
        statsPanel.add(createStatBox("Active Labs", stats.get("Active Labs"), new Color(155, 89, 182)));
        statsPanel.add(createStatBox("Total Computers", stats.get("Total Computers"), new Color(241, 196, 15)));
        rightPanel.add(statsPanel, BorderLayout.SOUTH);

        return rightPanel;
    }

    private JPanel createStatBox(String title, String value, Color color) {
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

    private void handleNavigation(String item) {
        System.out.println("\n🔄 TeacherDashboard.handleNavigation called for: " + item);
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

                // Ensure data is ready for teacher panels
                publish("📊 Loading data for " + item + "...");
                Thread.sleep(100); // Brief pause for data loading

                JPanel panel = null;
                switch (item) {
                    case "Software Requests":
                        publish("📦 Loading software requests...");
                        panel = DialogComponents.getSoftwareRequestPanel();
                        break;
                    case "Submit Feedback":
                        publish("💬 Loading feedback form...");
                        panel = DialogComponents.getFeedbackPanel();
                        break;
                    case "View Your Request Status":
                        publish("📋 Loading request status...");
                        panel = DialogComponents.getComplaintTrackingPanel();
                        break;
                    case "Lab Schedule":
                        publish("📅 Loading lab schedule...");
                        panel = DialogComponents.getLabSchedulePanel();
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

    private void showSoftwareRequestDetailsDialog(JDialog parent, String computerId, String softwareName, String version) {
        JDialog detailsDialog = new JDialog(parent, "Software Request Details", true);
        detailsDialog.setSize(400, 300);
        detailsDialog.setLayout(new BorderLayout());
        
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
    
        panel.add(createFieldPanel("Computer ID:", new JLabel(computerId)));
        panel.add(Box.createVerticalStrut(10));
        panel.add(createFieldPanel("Software:", new JLabel(softwareName)));
        panel.add(Box.createVerticalStrut(10));
        panel.add(createFieldPanel("Version:", new JLabel(version)));
        panel.add(Box.createVerticalStrut(10));
        
        List<SoftwareRequest> requests = softwareRequestController.getAllRequests();
        for (SoftwareRequest request : requests) {
            if (request.getComputerId().equals(computerId) &&
                request.getSoftwareName().equals(softwareName) &&
                request.getVersion().equals(version)) {
                panel.add(createFieldPanel("Status:", new JLabel(request.getStatus())));
                panel.add(Box.createVerticalStrut(10));
                panel.add(createFieldPanel("Request Date:", new JLabel(request.getRequestDate().toString())));
                panel.add(Box.createVerticalStrut(10));
                panel.add(createFieldPanel("Urgency:", new JLabel(request.getUrgency())));
                panel.add(Box.createVerticalStrut(15));
                
                JTextArea justificationArea = new JTextArea(5, 30);
                justificationArea.setText(request.getJustification());
                justificationArea.setEditable(false);
                panel.add(new JScrollPane(justificationArea));
                break;
            }
        }
    
        detailsDialog.add(panel, BorderLayout.CENTER);
        detailsDialog.setLocationRelativeTo(parent);
        detailsDialog.setVisible(true);
    }

    private void showReportDetailsDialog(JDialog parent, Map<String, String> details) {
        JDialog detailsDialog = new JDialog(parent, "Report Details", true);
        detailsDialog.setSize(400, 300);
        detailsDialog.setLayout(new BorderLayout());
        
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
    
        panel.add(createFieldPanel("Report ID:", new JLabel(details.get("id"))));
        panel.add(Box.createVerticalStrut(10));
        panel.add(createFieldPanel("Type:", new JLabel(details.get("type"))));
        panel.add(Box.createVerticalStrut(10));
        panel.add(createFieldPanel("Status:", new JLabel(details.get("status"))));
        panel.add(Box.createVerticalStrut(10));
        panel.add(createFieldPanel("Date:", new JLabel(details.get("date"))));
        panel.add(Box.createVerticalStrut(15));
        
        JTextArea descriptionArea = new JTextArea(5, 30);
        descriptionArea.setText(details.get("description"));
        descriptionArea.setEditable(false);
        panel.add(new JScrollPane(descriptionArea));
    
        detailsDialog.add(panel, BorderLayout.CENTER);
        detailsDialog.setLocationRelativeTo(parent);
        detailsDialog.setVisible(true);
    }

    private JPanel createFieldPanel(String label, JComponent component) {
        JPanel panel = new JPanel(new BorderLayout(10, 5));
        panel.add(new JLabel(label), BorderLayout.WEST);
        panel.add(component, BorderLayout.CENTER);
        return panel;
    }

    private JButton createProfileButton() {
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

    private void showProfilePopup(JButton profileButton) {
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

        JLabel roleLabel = new JLabel("Teacher");
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

    private ImageIcon createDefaultAvatar() {
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

    private void startClock() {
        Timer timer = new Timer(1000, e -> {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            clockLabel.setText(sdf.format(new Date()));
        });
        timer.start();
    }
}
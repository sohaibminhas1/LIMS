package ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import utils.DAOLogger;
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import service.LIMSService;
import model.*;
import controller.*;
import utils.InputValidator;
import utils.DataRefreshUtil;
import utils.DatabaseDropdownPopulator;

public class DialogComponents {
    private static LIMSService limsService;
    private static String currentUsername;
    private static ComplaintController complaintController;
    private static SoftwareRequestController softwareRequestController;
    private static FeedbackController feedbackController;
    private static ReportController reportController;

    public static void initialize(LIMSService service, String username) {
        DAOLogger.info("DialogComponents", "initialize", "🔧 Initializing DialogComponents with service and username: " + username);

        limsService = service;
        currentUsername = username;

        try {
            // Initialize controllers with null checks
            if (limsService == null) {
                throw new IllegalArgumentException("LIMSService cannot be null");
            }

            DAOLogger.debug("DialogComponents", "initialize", "Creating ComplaintController");
            complaintController = new ComplaintController(limsService.getComplaintService());

            DAOLogger.debug("DialogComponents", "initialize", "Creating SoftwareRequestController");
            softwareRequestController = new SoftwareRequestController(limsService.getSoftwareRequestService());

            DAOLogger.debug("DialogComponents", "initialize", "Creating FeedbackController");
            feedbackController = new FeedbackController(limsService.getFeedbackService());

            DAOLogger.debug("DialogComponents", "initialize", "Creating ReportController");
            reportController = new ReportController(
                limsService.getComplaintService(),
                limsService.getSoftwareRequestService(),
                limsService.getFeedbackService()
            );

            DAOLogger.success("DialogComponents", "initialize", "✅ All controllers initialized successfully");

        } catch (Exception e) {
            DAOLogger.error("DialogComponents", "initialize", "🚨 CRITICAL ERROR: Failed to initialize controllers", e);
            throw new RuntimeException("Failed to initialize DialogComponents", e);
        }
    }

    private static void styleDialog(JDialog dialog) {
        dialog.getRootPane().setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        dialog.getContentPane().setBackground(Color.WHITE);
    }

    private static JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        button.setBackground(new Color(41, 128, 185));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Prevent button glitches
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        button.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));

        // Add smooth hover effect with proper state management
        button.addMouseListener(new MouseAdapter() {
            private final Color normalColor = new Color(41, 128, 185);
            private final Color hoverColor = new Color(52, 152, 219);

            @Override
            public void mouseEntered(MouseEvent e) {
                if (button.isEnabled()) {
                    button.setBackground(hoverColor);
                    button.repaint();
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (button.isEnabled()) {
                    button.setBackground(normalColor);
                    button.repaint();
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {
                if (button.isEnabled()) {
                    button.setBackground(new Color(31, 108, 165)); // Darker on press
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (button.isEnabled()) {
                    button.setBackground(button.contains(e.getPoint()) ? hoverColor : normalColor);
                }
            }
        });

        return button;
    }

    private static JTextField createStyledTextField() {
        JTextField field = new JTextField();
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        return field;
    }

    private static JTextArea createStyledTextArea() {
        JTextArea area = new JTextArea();
        area.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        area.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        return area;
    }

    private static JComboBox<String> createStyledComboBox(String[] items) {
        JComboBox<String> combo = new JComboBox<>(items);
        combo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        combo.setBackground(Color.WHITE);
        combo.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        return combo;
    }

    /**
     * Ensures that DialogComponents is properly initialized before use
     */
    private static void ensureInitialized() {
        if (limsService == null || complaintController == null) {
            DAOLogger.error("DialogComponents", "ensureInitialized",
                "🚨 CRITICAL ERROR: DialogComponents not initialized! Call DialogComponents.initialize() first.");
            throw new IllegalStateException(
                "DialogComponents is not initialized. Please call DialogComponents.initialize(service, username) first."
            );
        }
    }

    public static void showComplaintDialog(Frame parent) {
        ensureInitialized(); // Ensure initialization before proceeding
        JDialog dialog = new JDialog(parent, "Register Complaint", true);
        dialog.setSize(450, 500);
        dialog.setLayout(new BorderLayout());
        styleDialog(dialog);
    
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        formPanel.setBackground(Color.WHITE);
    
        JLabel titleLabel = new JLabel("Register New Complaint");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(new Color(41, 128, 185));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        formPanel.add(titleLabel);
        formPanel.add(Box.createVerticalStrut(20));
    
        JTextField computerIdField = createStyledTextField();
        JComboBox<String> departmentCombo = DatabaseDropdownPopulator.createDepartmentCombo();
        JComboBox<String> issueTypeCombo = DatabaseDropdownPopulator.createIssueTypeCombo();
        JTextArea descArea = createStyledTextArea();
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);
        JScrollPane descScroll = new JScrollPane(descArea);
        descScroll.setPreferredSize(new Dimension(400, 100));
    
        formPanel.add(createFieldPanel("Computer ID:", computerIdField));
        formPanel.add(Box.createVerticalStrut(15));
        formPanel.add(createFieldPanel("Department:", departmentCombo));
        formPanel.add(Box.createVerticalStrut(15));
        formPanel.add(createFieldPanel("Issue Type:", issueTypeCombo));
        formPanel.add(Box.createVerticalStrut(15));
        
        JPanel descPanel = new JPanel(new BorderLayout(10, 5));
        descPanel.setBackground(Color.WHITE);
        descPanel.add(new JLabel("Description:"), BorderLayout.WEST);
        descPanel.add(descScroll, BorderLayout.CENTER);
        formPanel.add(descPanel);
        formPanel.add(Box.createVerticalStrut(20));
    
        JButton submitButton = createStyledButton("Submit Complaint");
        submitButton.setMaximumSize(new Dimension(200, 40));
        submitButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        submitButton.addActionListener(e -> {
            DAOLogger.info("DialogComponents", "showComplaintDialog", "🖱️ UI EVENT: Submit button clicked");

            String computerId = computerIdField.getText();
            String department = departmentCombo.getSelectedItem() != null ? departmentCombo.getSelectedItem().toString() : "";
            String issueType = issueTypeCombo.getSelectedItem() != null ? issueTypeCombo.getSelectedItem().toString() : "";
            String description = descArea.getText();

            DAOLogger.debug("DialogComponents", "showComplaintDialog",
                String.format("Form data - Computer: %s, Dept: %s, Type: %s, Desc length: %d",
                    computerId, department, issueType, description.length()));

            try {
                DAOLogger.debug("DialogComponents", "showComplaintDialog", "Starting input validation");

                // Enhanced validation
                InputValidator.validateComputerId(computerId);
                InputValidator.validateDepartment(department);
                InputValidator.validateIssueType(issueType);
                InputValidator.validateStringLength(description, "Description", 10, 500);

                DAOLogger.success("DialogComponents", "showComplaintDialog", "UI validation passed, calling controller");

                // Check if controller is initialized
                if (complaintController == null) {
                    DAOLogger.error("DialogComponents", "showComplaintDialog", "🚨 CRITICAL ERROR: complaintController is null! DialogComponents.initialize() may not have been called.");
                    throw new IllegalStateException("ComplaintController is not initialized. Please ensure DialogComponents.initialize() is called first.");
                }

                complaintController.addComplaint(computerId, department, issueType, description);

                DAOLogger.success("DialogComponents", "showComplaintDialog", "Controller call completed successfully");

                // Immediate table refresh - force refresh all complaint tables
                SwingUtilities.invokeLater(() -> {
                    DAOLogger.info("DialogComponents", "showComplaintDialog", "🔄 Forcing immediate table refresh");

                    // Refresh all complaint-related tables
                    TableRefreshManager.getInstance().refreshTable("complaints");
                    TableRefreshManager.getInstance().refreshRelatedTables("complaint");

                    // Also refresh any complaint tracking panels that might be open
                    DatabaseTableModel.getComplaintTableModel().refreshData();

                    DAOLogger.success("DialogComponents", "showComplaintDialog", "✅ Table refresh completed");
                });

                // Show success message
                JOptionPane.showMessageDialog(dialog,
                    "✅ Complaint submitted successfully!\n\nData has been saved and tables refreshed.",
                    "Success", JOptionPane.INFORMATION_MESSAGE);

                // Clear form fields
                computerIdField.setText("");
                descArea.setText("");

                DAOLogger.info("DialogComponents", "showComplaintDialog", "🖱️ UI SUCCESS: Form submitted and dialog closing");
                dialog.dispose();

            } catch (IllegalArgumentException ex) {
                DAOLogger.error("DialogComponents", "showComplaintDialog", "🖱️ UI VALIDATION ERROR: " + ex.getMessage());
                JOptionPane.showMessageDialog(dialog, "Error: " + ex.getMessage(),
                    "Validation Error", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                DAOLogger.error("DialogComponents", "showComplaintDialog", "🖱️ UI UNEXPECTED ERROR", ex);
                JOptionPane.showMessageDialog(dialog, "Unexpected error: " + ex.getMessage(),
                    "System Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    
        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(submitButton, BorderLayout.SOUTH);
        dialog.setLocationRelativeTo(parent);
        dialog.setVisible(true);
    }

    public static JPanel getComplaintPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(BorderFactory.createTitledBorder("Complaint Management"));

        // Create split pane for form and table
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(400);
        splitPane.setResizeWeight(0.4);

        // Form panel
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBorder(BorderFactory.createTitledBorder("Submit New Complaint"));
        formPanel.setBackground(Color.WHITE);

        JTextField computerIdField = createStyledTextField();
        JComboBox<String> departmentCombo = DatabaseDropdownPopulator.createDepartmentCombo();
        JComboBox<String> issueTypeCombo = DatabaseDropdownPopulator.createIssueTypeCombo();
        JTextArea descArea = createStyledTextArea();
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);
        JScrollPane descScroll = new JScrollPane(descArea);
        descScroll.setPreferredSize(new Dimension(350, 80));

        formPanel.add(Box.createVerticalStrut(10));
        formPanel.add(createFieldPanel("Computer ID:", computerIdField));
        formPanel.add(Box.createVerticalStrut(10));
        formPanel.add(createFieldPanel("Department:", departmentCombo));
        formPanel.add(Box.createVerticalStrut(10));
        formPanel.add(createFieldPanel("Issue Type:", issueTypeCombo));
        formPanel.add(Box.createVerticalStrut(10));
        JPanel descPanel = new JPanel(new BorderLayout(5, 5));
        descPanel.setBackground(Color.WHITE);
        descPanel.add(new JLabel("Description:"), BorderLayout.NORTH);
        descPanel.add(descScroll, BorderLayout.CENTER);
        formPanel.add(descPanel);
        formPanel.add(Box.createVerticalStrut(15));

        JButton submitButton = createStyledButton("Submit Complaint");
        submitButton.setMaximumSize(new Dimension(180, 35));
        submitButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        submitButton.addActionListener(e -> {
            try {
                InputValidator.validateComputerId(computerIdField.getText());
                InputValidator.validateSelection(departmentCombo.getSelectedItem(), "Department");
                InputValidator.validateSelection(issueTypeCombo.getSelectedItem(), "Issue Type");
                InputValidator.validateText(descArea.getText(), "Description", 10, 500);
                complaintController.addComplaint(
                    computerIdField.getText(),
                    departmentCombo.getSelectedItem().toString(),
                    issueTypeCombo.getSelectedItem().toString(),
                    descArea.getText()
                );

                // Handle successful submission with automatic refresh and form clearing
                DataRefreshUtil.handleSuccessfulSubmission(
                    formPanel,
                    "Complaint submitted successfully!",
                    "complaint",
                    computerIdField, descArea
                );
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(formPanel, "Error: " + ex.getMessage(), "Validation Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        formPanel.add(submitButton);
        formPanel.add(Box.createVerticalGlue());

        // Data table panel
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBorder(BorderFactory.createTitledBorder("Current Complaints"));
        tablePanel.setBackground(Color.WHITE);

        // Create table with live data
        JTable complaintsTable = new JTable(DatabaseTableModel.getComplaintTableModel());
        complaintsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        complaintsTable.setRowHeight(25);
        styleTable(complaintsTable);

        // Register table for auto-refresh
        TableRefreshManager.getInstance().registerTable("complaints", complaintsTable, (DatabaseTableModel) complaintsTable.getModel());

        JScrollPane scrollPane = new JScrollPane(complaintsTable);
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        // Table action buttons
        JPanel tableButtonPanel = new JPanel(new FlowLayout());
        tableButtonPanel.setBackground(Color.WHITE);

        JButton refreshButton = createStyledButton("Refresh");
        refreshButton.addActionListener(e -> {
            refreshButton.setEnabled(false);
            SwingUtilities.invokeLater(() -> {
                try {
                    DataRefreshUtil.refreshComplaintData();
                    JOptionPane.showMessageDialog(mainPanel, "✓ Complaint data refreshed successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(mainPanel, "Error refreshing data: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                } finally {
                    refreshButton.setEnabled(true);
                }
            });
        });

        JButton updateStatusButton = createStyledButton("Update Status");
        updateStatusButton.addActionListener(e -> {
            int selectedRow = complaintsTable.getSelectedRow();
            if (selectedRow >= 0) {
                String computerId = (String) complaintsTable.getValueAt(selectedRow, 0);
                Window window = SwingUtilities.getWindowAncestor(mainPanel);
                if (window instanceof Frame) {
                    showUpdateComplaintStatusDialog((Frame) window, computerId);
                } else {
                    JOptionPane.showMessageDialog(mainPanel, "Update status functionality is available in the main dashboard.");
                }
            } else {
                JOptionPane.showMessageDialog(mainPanel, "Please select a complaint to update.");
            }
        });

        tableButtonPanel.add(refreshButton);
        tableButtonPanel.add(updateStatusButton);
        tablePanel.add(tableButtonPanel, BorderLayout.SOUTH);

        splitPane.setLeftComponent(formPanel);
        splitPane.setRightComponent(tablePanel);
        mainPanel.add(splitPane, BorderLayout.CENTER);

        return mainPanel;
    }

    public static void showSoftwareRequestDialog(Frame parent) {
        ensureInitialized(); // Ensure initialization before proceeding
        JDialog dialog = new JDialog(parent, "Software Installation Request", true);
        dialog.setSize(500, 550);
        dialog.setLayout(new BorderLayout());
        styleDialog(dialog);
    
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        formPanel.setBackground(Color.WHITE);
    
        JLabel titleLabel = new JLabel("Request Software Installation");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(new Color(41, 128, 185));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        formPanel.add(titleLabel);
        formPanel.add(Box.createVerticalStrut(20));
    
        JTextField computerIdField = createStyledTextField();
        JTextField softwareNameField = createStyledTextField();
        JTextField versionField = createStyledTextField();
        JComboBox<String> urgencyCombo = DatabaseDropdownPopulator.createUrgencyCombo();
        JTextArea justificationArea = createStyledTextArea();
        justificationArea.setLineWrap(true);
        justificationArea.setWrapStyleWord(true);
        JScrollPane justificationScroll = new JScrollPane(justificationArea);
        justificationScroll.setPreferredSize(new Dimension(450, 100));
    
        formPanel.add(createFieldPanel("Computer ID:", computerIdField));
        formPanel.add(Box.createVerticalStrut(15));
        formPanel.add(createFieldPanel("Software Name:", softwareNameField));
        formPanel.add(Box.createVerticalStrut(15));
        formPanel.add(createFieldPanel("Version:", versionField));
        formPanel.add(Box.createVerticalStrut(15));
        formPanel.add(createFieldPanel("Urgency:", urgencyCombo));
        formPanel.add(Box.createVerticalStrut(15));
        
        JPanel justificationPanel = new JPanel(new BorderLayout(10, 5));
        justificationPanel.setBackground(Color.WHITE);
        justificationPanel.add(new JLabel("Justification:"), BorderLayout.WEST);
        justificationPanel.add(justificationScroll, BorderLayout.CENTER);
        formPanel.add(justificationPanel);
        formPanel.add(Box.createVerticalStrut(20));
    
        JButton submitButton = createStyledButton("Submit Request");
        submitButton.setMaximumSize(new Dimension(200, 40));
        submitButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        submitButton.addActionListener(e -> {
            try {
                // Enhanced validation
                InputValidator.validateComputerId(computerIdField.getText());
                InputValidator.validateStringLength(softwareNameField.getText(), "Software Name", 2, 50);
                InputValidator.validateAlphanumeric(softwareNameField.getText(), "Software Name");
                InputValidator.validateVersion(versionField.getText());
                InputValidator.validateUrgencyLevel(urgencyCombo.getSelectedItem().toString());
                InputValidator.validateStringLength(justificationArea.getText(), "Justification", 10, 500);

                softwareRequestController.addRequest(
                    computerIdField.getText(),
                    softwareNameField.getText(),
                    versionField.getText(),
                    urgencyCombo.getSelectedItem().toString(),
                    justificationArea.getText()
                );

                // Immediate table refresh - force refresh all software request tables
                SwingUtilities.invokeLater(() -> {
                    DAOLogger.info("DialogComponents", "showSoftwareRequestDialog", "🔄 Forcing immediate table refresh");

                    // Refresh all software-related tables
                    TableRefreshManager.getInstance().refreshTable("software_requests");
                    TableRefreshManager.getInstance().refreshRelatedTables("software");

                    // Also refresh the software request model directly
                    DatabaseTableModel.getSoftwareRequestTableModel().refreshData();

                    DAOLogger.success("DialogComponents", "showSoftwareRequestDialog", "✅ Table refresh completed");
                });

                // Show success message
                JOptionPane.showMessageDialog(dialog,
                    "✅ Software request submitted successfully!\n\nData has been saved and tables refreshed.",
                    "Success", JOptionPane.INFORMATION_MESSAGE);

                // Clear form fields
                computerIdField.setText("");
                softwareNameField.setText("");
                versionField.setText("");
                justificationArea.setText("");

                dialog.dispose();
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(formPanel, "Error: " + ex.getMessage(), "Validation Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    
        formPanel.add(submitButton);
        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.setLocationRelativeTo(parent);
        dialog.setVisible(true);
    }

    public static JPanel getSoftwareRequestPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(BorderFactory.createTitledBorder("Software Request Management"));

        // Create split pane for form and table
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(400);
        splitPane.setResizeWeight(0.4);

        // Form panel
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBorder(BorderFactory.createTitledBorder("Submit New Request"));
        formPanel.setBackground(Color.WHITE);

        JTextField computerIdField = createStyledTextField();
        JTextField softwareNameField = createStyledTextField();
        JTextField versionField = createStyledTextField();
        JComboBox<String> urgencyCombo = DatabaseDropdownPopulator.createUrgencyCombo();
        JTextArea justificationArea = createStyledTextArea();
        justificationArea.setLineWrap(true);
        justificationArea.setWrapStyleWord(true);
        JScrollPane justificationScroll = new JScrollPane(justificationArea);
        justificationScroll.setPreferredSize(new Dimension(350, 80));

        formPanel.add(Box.createVerticalStrut(10));
        formPanel.add(createFieldPanel("Computer ID:", computerIdField));
        formPanel.add(Box.createVerticalStrut(10));
        formPanel.add(createFieldPanel("Software Name:", softwareNameField));
        formPanel.add(Box.createVerticalStrut(10));
        formPanel.add(createFieldPanel("Version:", versionField));
        formPanel.add(Box.createVerticalStrut(10));
        formPanel.add(createFieldPanel("Urgency:", urgencyCombo));
        formPanel.add(Box.createVerticalStrut(10));
        JPanel justificationPanel = new JPanel(new BorderLayout(5, 5));
        justificationPanel.setBackground(Color.WHITE);
        justificationPanel.add(new JLabel("Justification:"), BorderLayout.NORTH);
        justificationPanel.add(justificationScroll, BorderLayout.CENTER);
        formPanel.add(justificationPanel);
        formPanel.add(Box.createVerticalStrut(15));

        JButton submitButton = createStyledButton("Submit Request");
        submitButton.setMaximumSize(new Dimension(180, 35));
        submitButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        submitButton.addActionListener(e -> {
            try {
                InputValidator.validateComputerId(computerIdField.getText());
                InputValidator.validateText(softwareNameField.getText(), "Software Name", 2, 50);
                InputValidator.validateVersion(versionField.getText());
                InputValidator.validateSelection(urgencyCombo.getSelectedItem(), "Urgency");
                InputValidator.validateText(justificationArea.getText(), "Justification", 10, 500);
                softwareRequestController.addRequest(
                    computerIdField.getText(),
                    softwareNameField.getText(),
                    versionField.getText(),
                    urgencyCombo.getSelectedItem().toString(),
                    justificationArea.getText()
                );

                // Immediate table refresh - force refresh all software request tables
                SwingUtilities.invokeLater(() -> {
                    DAOLogger.info("DialogComponents", "showSoftwareRequestDialog", "🔄 Forcing immediate table refresh");

                    // Refresh all software-related tables
                    TableRefreshManager.getInstance().refreshTable("software_requests");
                    TableRefreshManager.getInstance().refreshRelatedTables("software");

                    // Also refresh the software request model directly
                    DatabaseTableModel.getSoftwareRequestTableModel().refreshData();

                    DAOLogger.success("DialogComponents", "showSoftwareRequestDialog", "✅ Table refresh completed");
                });

                // Show success message
                JOptionPane.showMessageDialog(formPanel,
                    "✅ Software request submitted successfully!\n\nData has been saved and tables refreshed.",
                    "Success", JOptionPane.INFORMATION_MESSAGE);

                // Clear form fields
                computerIdField.setText("");
                softwareNameField.setText("");
                versionField.setText("");
                justificationArea.setText("");
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(formPanel, "Error: " + ex.getMessage(), "Validation Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        formPanel.add(submitButton);
        formPanel.add(Box.createVerticalGlue());

        // Data table panel
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBorder(BorderFactory.createTitledBorder("Current Software Requests"));
        tablePanel.setBackground(Color.WHITE);

        // Create table with live data
        JTable softwareTable = new JTable(DatabaseTableModel.getSoftwareRequestTableModel());
        softwareTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        softwareTable.setRowHeight(25);
        styleTable(softwareTable);

        // Register table for auto-refresh
        TableRefreshManager.getInstance().registerTable("software_requests", softwareTable, (DatabaseTableModel) softwareTable.getModel());

        JScrollPane scrollPane = new JScrollPane(softwareTable);
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        // Table action buttons
        JPanel tableButtonPanel = new JPanel(new FlowLayout());
        tableButtonPanel.setBackground(Color.WHITE);

        JButton refreshButton = createStyledButton("Refresh");
        refreshButton.addActionListener(e -> {
            refreshButton.setEnabled(false);
            SwingUtilities.invokeLater(() -> {
                try {
                    DataRefreshUtil.refreshSoftwareRequestData();
                    JOptionPane.showMessageDialog(mainPanel, "✓ Software request data refreshed successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(mainPanel, "Error refreshing data: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                } finally {
                    refreshButton.setEnabled(true);
                }
            });
        });

        // Check if current user is admin to show approval buttons
        boolean isAdmin = currentUsername != null && (currentUsername.equals("admin") || currentUsername.startsWith("admin"));

        if (isAdmin) {
            JButton approveButton = createStyledButton("Approve");
            approveButton.setBackground(new Color(76, 175, 80));
            approveButton.addActionListener(e -> {
                int selectedRow = softwareTable.getSelectedRow();
                if (selectedRow >= 0) {
                    String requestId = (String) softwareTable.getValueAt(selectedRow, 0);
                    String computerId = (String) softwareTable.getValueAt(selectedRow, 0);
                    String softwareName = (String) softwareTable.getValueAt(selectedRow, 1);
                    showApproveRequestDialog(SwingUtilities.getWindowAncestor(mainPanel), requestId, computerId, softwareName);
                } else {
                    JOptionPane.showMessageDialog(mainPanel, "Please select a request to approve.");
                }
            });

            JButton rejectButton = createStyledButton("Reject");
            rejectButton.setBackground(new Color(244, 67, 54));
            rejectButton.addActionListener(e -> {
                int selectedRow = softwareTable.getSelectedRow();
                if (selectedRow >= 0) {
                    String requestId = (String) softwareTable.getValueAt(selectedRow, 0);
                    String computerId = (String) softwareTable.getValueAt(selectedRow, 0);
                    String softwareName = (String) softwareTable.getValueAt(selectedRow, 1);
                    showRejectRequestDialog(SwingUtilities.getWindowAncestor(mainPanel), requestId, computerId, softwareName);
                } else {
                    JOptionPane.showMessageDialog(mainPanel, "Please select a request to reject.");
                }
            });

            tableButtonPanel.add(approveButton);
            tableButtonPanel.add(rejectButton);
        }

        tableButtonPanel.add(refreshButton);
        tablePanel.add(tableButtonPanel, BorderLayout.SOUTH);

        splitPane.setLeftComponent(formPanel);
        splitPane.setRightComponent(tablePanel);
        mainPanel.add(splitPane, BorderLayout.CENTER);

        return mainPanel;
    }

    public static void showFeedbackDialog(Frame parent) {
        ensureInitialized(); // Ensure initialization before proceeding
        JDialog dialog = new JDialog(parent, "Submit Feedback", true);
        dialog.setSize(550, 500);
        dialog.setLayout(new BorderLayout());
        styleDialog(dialog);

        // Main panel with gradient background
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Header panel with icon and title
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        headerPanel.setBackground(Color.WHITE);

        JLabel iconLabel = new JLabel("💬");
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 24));

        JLabel titleLabel = new JLabel("Submit Your Feedback");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(new Color(41, 128, 185));

        headerPanel.add(iconLabel);
        headerPanel.add(Box.createHorizontalStrut(10));
        headerPanel.add(titleLabel);

        // Subtitle
        JLabel subtitleLabel = new JLabel("<html><center>Your feedback helps us improve the LIMS system.<br>Please share your thoughts, suggestions, or concerns.</center></html>");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(108, 117, 125));
        subtitleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(Color.WHITE);
        titlePanel.add(headerPanel, BorderLayout.CENTER);
        titlePanel.add(subtitleLabel, BorderLayout.SOUTH);
        titlePanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        // Form panel with improved styling
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBackground(Color.WHITE);

        // Name field with icon
        JPanel namePanel = new JPanel(new BorderLayout(10, 5));
        namePanel.setBackground(Color.WHITE);
        JLabel nameIcon = new JLabel("👤");
        nameIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));
        JLabel nameLabel = new JLabel("Your Name:");
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        JTextField nameField = createStyledTextField();
        nameField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(206, 212, 218), 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));

        JPanel nameLabelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        nameLabelPanel.setBackground(Color.WHITE);
        nameLabelPanel.add(nameIcon);
        nameLabelPanel.add(Box.createHorizontalStrut(5));
        nameLabelPanel.add(nameLabel);

        namePanel.add(nameLabelPanel, BorderLayout.NORTH);
        namePanel.add(nameField, BorderLayout.CENTER);

        // Category field with icon
        JPanel categoryPanel = new JPanel(new BorderLayout(10, 5));
        categoryPanel.setBackground(Color.WHITE);
        JLabel categoryIcon = new JLabel("📂");
        categoryIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));
        JLabel categoryLabel = new JLabel("Category:");
        categoryLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        JComboBox<String> categoryCombo = DatabaseDropdownPopulator.createFeedbackCategoryCombo();
        categoryCombo.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(206, 212, 218), 1),
            BorderFactory.createEmptyBorder(4, 8, 4, 8)
        ));

        JPanel categoryLabelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        categoryLabelPanel.setBackground(Color.WHITE);
        categoryLabelPanel.add(categoryIcon);
        categoryLabelPanel.add(Box.createHorizontalStrut(5));
        categoryLabelPanel.add(categoryLabel);

        categoryPanel.add(categoryLabelPanel, BorderLayout.NORTH);
        categoryPanel.add(categoryCombo, BorderLayout.CENTER);

        // Feedback text area with improved styling
        JPanel feedbackPanel = new JPanel(new BorderLayout(10, 5));
        feedbackPanel.setBackground(Color.WHITE);
        JLabel feedbackIcon = new JLabel("💭");
        feedbackIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));
        JLabel feedbackLabel = new JLabel("Your Feedback:");
        feedbackLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));

        JTextArea feedbackArea = createStyledTextArea();
        feedbackArea.setLineWrap(true);
        feedbackArea.setWrapStyleWord(true);
        feedbackArea.setRows(6);
        feedbackArea.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        feedbackArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        feedbackArea.setBackground(new Color(248, 249, 250));

        JScrollPane feedbackScroll = new JScrollPane(feedbackArea);
        feedbackScroll.setBorder(BorderFactory.createLineBorder(new Color(206, 212, 218), 1));
        feedbackScroll.setPreferredSize(new Dimension(480, 120));

        JPanel feedbackLabelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        feedbackLabelPanel.setBackground(Color.WHITE);
        feedbackLabelPanel.add(feedbackIcon);
        feedbackLabelPanel.add(Box.createHorizontalStrut(5));
        feedbackLabelPanel.add(feedbackLabel);

        feedbackPanel.add(feedbackLabelPanel, BorderLayout.NORTH);
        feedbackPanel.add(feedbackScroll, BorderLayout.CENTER);

        // Add components to form
        formPanel.add(namePanel);
        formPanel.add(Box.createVerticalStrut(15));
        formPanel.add(categoryPanel);
        formPanel.add(Box.createVerticalStrut(15));
        formPanel.add(feedbackPanel);
        formPanel.add(Box.createVerticalStrut(20));

        // Submit button with improved styling
        JButton submitButton = new JButton("📤 Submit Feedback");
        submitButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        submitButton.setBackground(new Color(40, 167, 69));
        submitButton.setForeground(Color.WHITE);
        submitButton.setBorder(BorderFactory.createEmptyBorder(12, 24, 12, 24));
        submitButton.setFocusPainted(false);
        submitButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Hover effect
        submitButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                submitButton.setBackground(new Color(33, 136, 56));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                submitButton.setBackground(new Color(40, 167, 69));
            }
        });

        submitButton.addActionListener(e -> {
            try {
                // Validate inputs
                InputValidator.validateText(nameField.getText(), "Name", 2, 50);
                InputValidator.validateSelection(categoryCombo.getSelectedItem(), "Category");
                InputValidator.validateText(feedbackArea.getText(), "Feedback", 10, 1000);

                // Extract category without emoji
                String selectedCategory = categoryCombo.getSelectedItem().toString();
                String category = selectedCategory.substring(selectedCategory.indexOf(' ') + 1);

                feedbackController.addFeedback(
                    nameField.getText(),
                    category,
                    feedbackArea.getText()
                );

                // Immediate table refresh - force refresh all feedback tables
                SwingUtilities.invokeLater(() -> {
                    DAOLogger.info("DialogComponents", "showFeedbackDialog", "🔄 Forcing immediate table refresh");

                    // Refresh all feedback-related tables
                    TableRefreshManager.getInstance().refreshTable("feedback");
                    TableRefreshManager.getInstance().refreshRelatedTables("feedback");

                    // Also refresh the feedback model directly
                    DatabaseTableModel.getFeedbackTableModel().refreshData();

                    DAOLogger.success("DialogComponents", "showFeedbackDialog", "✅ Table refresh completed");
                });

                // Show improved success message
                JOptionPane.showMessageDialog(dialog,
                    "🎉 Thank you for your valuable feedback!\n\n" +
                    "Your feedback has been successfully submitted and will help us\n" +
                    "improve the LIMS system for everyone.",
                    "Feedback Submitted Successfully", JOptionPane.INFORMATION_MESSAGE);

                // Clear form fields
                nameField.setText("");
                categoryCombo.setSelectedIndex(0);
                feedbackArea.setText("");

                dialog.dispose();
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(dialog,
                    "⚠️ Please check your input:\n\n" + ex.getMessage(),
                    "Input Validation Error", JOptionPane.WARNING_MESSAGE);
            }
        });

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(submitButton);

        // Cancel button
        JButton cancelButton = new JButton("Cancel");
        cancelButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cancelButton.setBackground(new Color(108, 117, 125));
        cancelButton.setForeground(Color.WHITE);
        cancelButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        cancelButton.setFocusPainted(false);
        cancelButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        cancelButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(Box.createHorizontalStrut(10));
        buttonPanel.add(cancelButton);

        // Assemble dialog
        mainPanel.add(titlePanel, BorderLayout.NORTH);
        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        dialog.add(mainPanel, BorderLayout.CENTER);
        dialog.setLocationRelativeTo(parent);
        dialog.setVisible(true);
    }

    public static JPanel getFeedbackPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(BorderFactory.createTitledBorder("Feedback Management"));

        // Create split pane for form and table
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(400);
        splitPane.setResizeWeight(0.4);

        // Form panel
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBorder(BorderFactory.createTitledBorder("Submit New Feedback"));
        formPanel.setBackground(Color.WHITE);

        JTextField nameField = createStyledTextField();
        JComboBox<String> categoryCombo = DatabaseDropdownPopulator.createFeedbackCategoryCombo();
        JTextArea feedbackArea = createStyledTextArea();
        feedbackArea.setLineWrap(true);
        feedbackArea.setWrapStyleWord(true);
        JScrollPane feedbackScroll = new JScrollPane(feedbackArea);
        feedbackScroll.setPreferredSize(new Dimension(350, 80));

        formPanel.add(Box.createVerticalStrut(10));
        formPanel.add(createFieldPanel("Your Name:", nameField));
        formPanel.add(Box.createVerticalStrut(10));
        formPanel.add(createFieldPanel("Category:", categoryCombo));
        formPanel.add(Box.createVerticalStrut(10));
        JPanel feedbackPanel = new JPanel(new BorderLayout(5, 5));
        feedbackPanel.setBackground(Color.WHITE);
        feedbackPanel.add(new JLabel("Feedback:"), BorderLayout.NORTH);
        feedbackPanel.add(feedbackScroll, BorderLayout.CENTER);
        formPanel.add(feedbackPanel);
        formPanel.add(Box.createVerticalStrut(15));

        JButton submitButton = createStyledButton("Submit Feedback");
        submitButton.setMaximumSize(new Dimension(180, 35));
        submitButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        submitButton.addActionListener(e -> {
            try {
                InputValidator.validateText(nameField.getText(), "Name", 2, 50);
                InputValidator.validateSelection(categoryCombo.getSelectedItem(), "Category");
                InputValidator.validateText(feedbackArea.getText(), "Feedback", 10, 1000);
                feedbackController.addFeedback(
                    nameField.getText(),
                    categoryCombo.getSelectedItem().toString(),
                    feedbackArea.getText()
                );

                // Immediate table refresh - force refresh all feedback tables
                SwingUtilities.invokeLater(() -> {
                    DAOLogger.info("DialogComponents", "getFeedbackPanel", "🔄 Forcing immediate table refresh");

                    // Refresh all feedback-related tables
                    TableRefreshManager.getInstance().refreshTable("feedback");
                    TableRefreshManager.getInstance().refreshRelatedTables("feedback");

                    // Also refresh the feedback model directly
                    DatabaseTableModel.getFeedbackTableModel().refreshData();

                    DAOLogger.success("DialogComponents", "getFeedbackPanel", "✅ Table refresh completed");
                });

                // Show success message
                JOptionPane.showMessageDialog(formPanel,
                    "✅ Thank you for your feedback!\n\nData has been saved and tables refreshed.",
                    "Success", JOptionPane.INFORMATION_MESSAGE);

                // Clear form fields
                nameField.setText("");
                feedbackArea.setText("");
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(formPanel, "Error: " + ex.getMessage(), "Validation Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        formPanel.add(submitButton);
        formPanel.add(Box.createVerticalGlue());

        // Data table panel
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBorder(BorderFactory.createTitledBorder("Current Feedback"));
        tablePanel.setBackground(Color.WHITE);

        // Create table with live data
        JTable feedbackTable = new JTable(DatabaseTableModel.getFeedbackTableModel());
        feedbackTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        feedbackTable.setRowHeight(25);
        styleTable(feedbackTable);

        // Register table for auto-refresh
        TableRefreshManager.getInstance().registerTable("feedback", feedbackTable, (DatabaseTableModel) feedbackTable.getModel());

        JScrollPane scrollPane = new JScrollPane(feedbackTable);
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        // Table action buttons
        JPanel tableButtonPanel = new JPanel(new FlowLayout());
        tableButtonPanel.setBackground(Color.WHITE);

        JButton refreshButton = createStyledButton("Refresh");
        refreshButton.addActionListener(e -> DataRefreshUtil.refreshFeedbackData());

        JButton viewButton = createStyledButton("View Details");
        viewButton.addActionListener(e -> {
            int selectedRow = feedbackTable.getSelectedRow();
            if (selectedRow >= 0) {
                String feedbackId = (String) feedbackTable.getValueAt(selectedRow, 0);
                String name = (String) feedbackTable.getValueAt(selectedRow, 1);
                String category = (String) feedbackTable.getValueAt(selectedRow, 2);
                String feedback = (String) feedbackTable.getValueAt(selectedRow, 3);
                JOptionPane.showMessageDialog(mainPanel,
                    "Feedback Details:\n\n" +
                    "ID: " + feedbackId + "\n" +
                    "Name: " + name + "\n" +
                    "Category: " + category + "\n" +
                    "Feedback: " + feedback,
                    "Feedback Details", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(mainPanel, "Please select feedback to view details.");
            }
        });

        tableButtonPanel.add(refreshButton);
        tableButtonPanel.add(viewButton);
        tablePanel.add(tableButtonPanel, BorderLayout.SOUTH);

        splitPane.setLeftComponent(formPanel);
        splitPane.setRightComponent(tablePanel);
        mainPanel.add(splitPane, BorderLayout.CENTER);

        return mainPanel;
    }

    private static JPanel createFieldPanel(String label, JComponent component) {
        JPanel panel = new JPanel(new BorderLayout(10, 5));
        panel.setBackground(Color.WHITE);
        JLabel labelComponent = new JLabel(label);
        labelComponent.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        panel.add(labelComponent, BorderLayout.WEST);
        panel.add(component, BorderLayout.CENTER);
        return panel;
    }

    public static void showReportStatusDialog(Frame parent) {
        JDialog dialog = new JDialog(parent, "Report Status", true);
        dialog.setSize(800, 600);
        dialog.setLayout(new BorderLayout());
        styleDialog(dialog);
        
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(Color.WHITE);
    
        // Title
        JLabel titleLabel = new JLabel("Report Status");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(new Color(41, 128, 185));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // Create tabbed pane
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        // Complaint Reports Tab
        JPanel complaintPanel = new JPanel(new BorderLayout());
        complaintPanel.setBackground(Color.WHITE);
        
        // Search panel for complaints
        JPanel complaintSearchPanel = new JPanel(new BorderLayout(10, 0));
        complaintSearchPanel.setBackground(Color.WHITE);
        JTextField complaintSearchField = createStyledTextField();
        JButton complaintSearchButton = createStyledButton("Search");
        
        complaintSearchPanel.add(new JLabel("Search Complaint ID:"), BorderLayout.WEST);
        complaintSearchPanel.add(complaintSearchField, BorderLayout.CENTER);
        complaintSearchPanel.add(complaintSearchButton, BorderLayout.EAST);
        
        // Use database-connected complaint table
        DatabaseTableModel complaintTableModel = DatabaseTableModel.getComplaintTableModel();
        JTable complaintTable = new JTable(complaintTableModel);

        // Register table for auto-refresh
        TableRefreshManager.getInstance().registerTable("report_complaints", complaintTable, complaintTableModel);

        styleTable(complaintTable);
        JScrollPane complaintScrollPane = new JScrollPane(complaintTable);
        
        // Complaint buttons
        JPanel complaintButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        complaintButtonPanel.setBackground(Color.WHITE);
        JButton viewComplaintButton = createStyledButton("View Details");
        JButton updateComplaintButton = createStyledButton("Update Status");
        complaintButtonPanel.add(viewComplaintButton);
        complaintButtonPanel.add(updateComplaintButton);
        
        complaintPanel.add(complaintSearchPanel, BorderLayout.NORTH);
        complaintPanel.add(complaintScrollPane, BorderLayout.CENTER);
        complaintPanel.add(complaintButtonPanel, BorderLayout.SOUTH);
        
        // Software Installation Updates Tab
        JPanel softwarePanel = new JPanel(new BorderLayout());
        softwarePanel.setBackground(Color.WHITE);
        
        // Search panel for software requests
        JPanel softwareSearchPanel = new JPanel(new BorderLayout(10, 0));
        softwareSearchPanel.setBackground(Color.WHITE);
        JTextField softwareSearchField = createStyledTextField();
        JButton softwareSearchButton = createStyledButton("Search");
        
        softwareSearchPanel.add(new JLabel("Search Request ID:"), BorderLayout.WEST);
        softwareSearchPanel.add(softwareSearchField, BorderLayout.CENTER);
        softwareSearchPanel.add(softwareSearchButton, BorderLayout.EAST);
        
        // Use database-connected software request table
        DatabaseTableModel softwareTableModel = DatabaseTableModel.getSoftwareRequestTableModel();
        JTable softwareTable = new JTable(softwareTableModel);

        // Register table for auto-refresh
        TableRefreshManager.getInstance().registerTable("report_software_requests", softwareTable, softwareTableModel);

        styleTable(softwareTable);
        JScrollPane softwareScrollPane = new JScrollPane(softwareTable);
        
        // Software buttons
        JPanel softwareButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        softwareButtonPanel.setBackground(Color.WHITE);
        JButton viewSoftwareButton = createStyledButton("View Details");
        JButton updateSoftwareButton = createStyledButton("Update Status");
        softwareButtonPanel.add(viewSoftwareButton);
        softwareButtonPanel.add(updateSoftwareButton);
        
        softwarePanel.add(softwareSearchPanel, BorderLayout.NORTH);
        softwarePanel.add(softwareScrollPane, BorderLayout.CENTER);
        softwarePanel.add(softwareButtonPanel, BorderLayout.SOUTH);
        
        // Add tabs
        tabbedPane.addTab("Complaint Reports", complaintPanel);
        tabbedPane.addTab("Software Installation Updates", softwarePanel);
        
        // Add action listeners
        viewComplaintButton.addActionListener(e -> {
            int selectedRow = complaintTable.getSelectedRow();
            if (selectedRow >= 0) {
                String complaintId = (String) complaintTable.getValueAt(selectedRow, 0);
                Complaint complaint = complaintController.getComplaintById(complaintId);
                if (complaint != null) {
                    showComplaintDetailsDialog(dialog, complaint);
                }
            } else {
                JOptionPane.showMessageDialog(dialog, "Please select a complaint to view details");
            }
        });
        
        viewSoftwareButton.addActionListener(e -> {
            int selectedRow = softwareTable.getSelectedRow();
            if (selectedRow >= 0) {
                String requestId = (String) softwareTable.getValueAt(selectedRow, 0);
                showSoftwareRequestDetailsDialog(dialog, requestId);
            } else {
                JOptionPane.showMessageDialog(dialog, "Please select a request to view details");
            }
        });
        
        updateComplaintButton.addActionListener(e -> {
            int selectedRow = complaintTable.getSelectedRow();
            if (selectedRow >= 0) {
                String complaintId = (String) complaintTable.getValueAt(selectedRow, 0);
                showUpdateComplaintStatusDialog(dialog, complaintId);
            } else {
                JOptionPane.showMessageDialog(dialog, "Please select a complaint to update");
            }
        });
        
        updateSoftwareButton.addActionListener(e -> {
            int selectedRow = softwareTable.getSelectedRow();
            if (selectedRow >= 0) {
                String requestId = (String) softwareTable.getValueAt(selectedRow, 0);
                showUpdateSoftwareStatusDialog(dialog, requestId);
            } else {
                JOptionPane.showMessageDialog(dialog, "Please select a request to update");
            }
        });
        
        mainPanel.add(tabbedPane, BorderLayout.CENTER);
        dialog.add(mainPanel);
        dialog.setLocationRelativeTo(parent);
        dialog.setVisible(true);
    }

    private static void styleTable(JTable table) {
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setRowHeight(30);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        table.getTableHeader().setBackground(new Color(41, 128, 185));
        table.getTableHeader().setForeground(Color.WHITE);
    }

    private static void showComplaintDetailsDialog(JDialog parent, Complaint complaint) {
        JDialog detailsDialog = new JDialog(parent, "Complaint Details", true);
        detailsDialog.setSize(500, 400);
        detailsDialog.setLayout(new BorderLayout());
        styleDialog(detailsDialog);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBackground(Color.WHITE);

        JLabel titleLabel = new JLabel("Complaint Details");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(new Color(41, 128, 185));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(titleLabel);
        panel.add(Box.createVerticalStrut(20));

        panel.add(createFieldPanel("Complaint ID:", new JLabel(complaint.getId())));
        panel.add(Box.createVerticalStrut(10));
        panel.add(createFieldPanel("Type:", new JLabel(complaint.getType())));
        panel.add(Box.createVerticalStrut(10));
        panel.add(createFieldPanel("Status:", new JLabel(complaint.getStatus())));
        panel.add(Box.createVerticalStrut(10));
        panel.add(createFieldPanel("Date:", new JLabel(complaint.getSubmissionDate().toString())));
        panel.add(Box.createVerticalStrut(10));
        panel.add(createFieldPanel("Assigned To:", new JLabel(complaint.getAssignedTo())));
        panel.add(Box.createVerticalStrut(15));

        JTextArea descriptionArea = createStyledTextArea();
        descriptionArea.setText(complaint.getDescription());
        descriptionArea.setEditable(false);
        panel.add(new JScrollPane(descriptionArea));

        detailsDialog.add(panel, BorderLayout.CENTER);
        detailsDialog.setLocationRelativeTo(parent);
        detailsDialog.setVisible(true);
    }

    private static void showUpdateComplaintStatusDialog(Frame parent, String complaintId) {
        JDialog updateDialog = new JDialog(parent, "Update Complaint Status", true);
        showUpdateComplaintStatusDialogImpl(updateDialog, complaintId);
    }

    private static void showUpdateComplaintStatusDialog(JDialog parent, String complaintId) {
        JDialog updateDialog = new JDialog(parent, "Update Complaint Status", true);
        showUpdateComplaintStatusDialogImpl(updateDialog, complaintId);
    }

    private static void showUpdateComplaintStatusDialogImpl(JDialog updateDialog, String complaintId) {
        updateDialog.setSize(400, 200);
        updateDialog.setLayout(new BorderLayout());
        styleDialog(updateDialog);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBackground(Color.WHITE);

        JComboBox<String> statusCombo = createStyledComboBox(new String[]{"Open", "In Progress", "Resolved", "Closed"});
        JTextArea updateNotes = createStyledTextArea();
        updateNotes.setLineWrap(true);
        updateNotes.setWrapStyleWord(true);
        JScrollPane notesScroll = new JScrollPane(updateNotes);
        notesScroll.setPreferredSize(new Dimension(350, 60));

        panel.add(createFieldPanel("New Status:", statusCombo));
        panel.add(Box.createVerticalStrut(10));
        panel.add(createFieldPanel("Update Notes:", notesScroll));

        JButton updateButton = createStyledButton("Update Status");
        updateButton.addActionListener(e -> {
            try {
                complaintController.updateComplaintStatus(complaintId, 
                    new Date(), 
                    statusCombo.getSelectedItem().toString());
                JOptionPane.showMessageDialog(updateDialog, "Status updated successfully!");

                // Auto-refresh complaint tables
                TableRefreshManager.getInstance().refreshRelatedTables("complaint");

                updateDialog.dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(updateDialog, "Error: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        updateDialog.add(panel, BorderLayout.CENTER);
        updateDialog.add(updateButton, BorderLayout.SOUTH);
        updateDialog.setLocationRelativeTo(updateDialog.getParent());
        updateDialog.setVisible(true);
    }

    private static void showUpdateSoftwareStatusDialog(JDialog parent, String requestId) {
        JDialog updateDialog = new JDialog(parent, "Update Software Request Status", true);
        updateDialog.setSize(400, 200);
        updateDialog.setLayout(new BorderLayout());
        styleDialog(updateDialog);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBackground(Color.WHITE);

        JComboBox<String> statusCombo = createStyledComboBox(new String[]{"Pending", "Approved", "In Progress", "Completed", "Rejected"});
        JTextArea updateNotes = createStyledTextArea();
        updateNotes.setLineWrap(true);
        updateNotes.setWrapStyleWord(true);
        JScrollPane notesScroll = new JScrollPane(updateNotes);
        notesScroll.setPreferredSize(new Dimension(350, 60));

        panel.add(createFieldPanel("New Status:", statusCombo));
        panel.add(Box.createVerticalStrut(10));
        panel.add(createFieldPanel("Update Notes:", notesScroll));

        JButton updateButton = createStyledButton("Update Status");
        updateButton.addActionListener(e -> {
            try {
                softwareRequestController.updateRequestStatus(requestId, 
                    new Date(), 
                    statusCombo.getSelectedItem().toString());
                JOptionPane.showMessageDialog(updateDialog, "Status updated successfully!");

                // Auto-refresh software request tables
                TableRefreshManager.getInstance().refreshRelatedTables("software");

                updateDialog.dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(updateDialog, "Error: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        updateDialog.add(panel, BorderLayout.CENTER);
        updateDialog.add(updateButton, BorderLayout.SOUTH);
        updateDialog.setLocationRelativeTo(parent);
        updateDialog.setVisible(true);
    }

    public static void showLabScheduleDialog(Frame parent) {
        JDialog dialog = new JDialog(parent, "Lab Schedule", true);
        dialog.setSize(600, 500);
        dialog.setLayout(new BorderLayout());
        
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
    
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        filterPanel.add(new JLabel("Filter by:"));
        
        JComboBox<String> labCombo = DatabaseDropdownPopulator.createLabCombo();
        filterPanel.add(labCombo);

        JComboBox<String> dateCombo = new JComboBox<>(new String[]{"Today", "This Week", "This Month", "Custom Date"});
        filterPanel.add(dateCombo);

        JButton refreshButton = createStyledButton("Refresh");
        filterPanel.add(refreshButton);

        // Use database-connected lab reservations table
        DatabaseTableModel scheduleTableModel = DatabaseTableModel.getLabReservationTableModel();
        JTable scheduleTable = new JTable(scheduleTableModel);

        // Register table for auto-refresh
        TableRefreshManager.getInstance().registerTable("lab_schedule", scheduleTable, scheduleTableModel);
        scheduleTable.setAutoCreateRowSorter(true);
        JScrollPane scrollPane = new JScrollPane(scheduleTable);
    
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton reserveButton = new JButton("New Reservation");
        reserveButton.setBackground(new Color(70, 120, 200));
        reserveButton.setForeground(Color.WHITE);
        buttonPanel.add(reserveButton);
        
        JButton printButton = new JButton("Print Schedule");
        printButton.setBackground(new Color(70, 120, 200));
        printButton.setForeground(Color.WHITE);
        buttonPanel.add(printButton);
    
        mainPanel.add(filterPanel);
        mainPanel.add(Box.createVerticalStrut(15));
        mainPanel.add(scrollPane);
        mainPanel.add(Box.createVerticalStrut(15));
        mainPanel.add(buttonPanel);
    
        reserveButton.addActionListener(e -> {
            showNewReservationDialog((Frame)dialog.getOwner());
            dialog.dispose();
        });
        
        printButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(dialog, "Schedule sent to printer");
        });
        
        refreshButton.addActionListener(e -> {
            SwingUtilities.invokeLater(() -> {
                try {
                    TableRefreshManager.getInstance().refreshTable("lab_reservations");
                    DatabaseTableModel.getLabReservationTableModel().refreshData();
                    JOptionPane.showMessageDialog(dialog, "✓ Lab schedule refreshed successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(dialog, "Error refreshing data: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            });
        });
    
        dialog.add(mainPanel, BorderLayout.CENTER);
        dialog.setLocationRelativeTo(parent);
        dialog.setVisible(true);
    }

    private static void showNewReservationDialog(Frame parent) {
        JDialog dialog = new JDialog(parent, "New Lab Reservation", true);
        dialog.setSize(450, 400);
        dialog.setLayout(new BorderLayout());
        
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JComboBox<String> labCombo = DatabaseDropdownPopulator.createLabCombo();
        JTextField dateField = new JTextField();
        JComboBox<String> startTimeCombo = DatabaseDropdownPopulator.createTimeSlotCombo();
        JComboBox<String> durationCombo = new JComboBox<>(new String[]{"1", "2", "3", "4"});
        JTextField purposeField = new JTextField();
        
        formPanel.add(createFieldPanel("Lab:", labCombo));
        formPanel.add(Box.createVerticalStrut(10));
        formPanel.add(createFieldPanel("Date:", dateField));
        formPanel.add(Box.createVerticalStrut(10));
        formPanel.add(createFieldPanel("Start Time:", startTimeCombo));
        formPanel.add(Box.createVerticalStrut(10));
        formPanel.add(createFieldPanel("Duration (hours):", durationCombo));
        formPanel.add(Box.createVerticalStrut(10));
        formPanel.add(createFieldPanel("Purpose:", purposeField));
        formPanel.add(Box.createVerticalStrut(15));
        
        JButton submitButton = new JButton("Submit Reservation");
        submitButton.addActionListener(e -> {
            try {
                // Validate inputs
                InputValidator.validateSelection(labCombo.getSelectedItem(), "Lab");
                InputValidator.validateDate(dateField.getText());
                InputValidator.validateSelection(startTimeCombo.getSelectedItem(), "Start Time");
                InputValidator.validateDuration(durationCombo.getSelectedItem().toString());
                InputValidator.validateText(purposeField.getText(), "Purpose", 5, 200);

                LabReservation reservation = new LabReservation(
                    labCombo.getSelectedItem().toString(),
                    new SimpleDateFormat("yyyy-MM-dd").parse(dateField.getText()),
                    startTimeCombo.getSelectedItem().toString(),
                    currentUsername,
                    purposeField.getText(),
                    "", // Course
                    ""  // Instructor
                );
                limsService.getLabReservationService().addReservation(reservation);

                // Handle successful submission with automatic refresh
                DataRefreshUtil.showSuccessWithRefresh(
                    formPanel,
                    "Reservation submitted successfully!",
                    "reservation"
                );

                dialog.dispose();
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(dialog, "Error: " + ex.getMessage(), 
                    "Validation Error", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Error: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(submitButton, BorderLayout.SOUTH);
        dialog.setLocationRelativeTo(parent);
        dialog.setVisible(true);
    }

    public static void showComputerInventoryDialog(Frame parent) {
        JDialog dialog = new JDialog(parent, "Computer Inventory Management", true);
        dialog.setSize(800, 600);
        dialog.setLayout(new BorderLayout());
        
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        filterPanel.add(new JLabel("Filter by:"));
        
        JComboBox<String> labFilter = new JComboBox<>(new String[]{"All Labs", "CS Lab 1", "CS Lab 2", "CS Lab 3", "SE Lab 1", "SE Lab 2", "AI Lab", "Data Lab"});
        filterPanel.add(labFilter);
        
        JComboBox<String> statusFilter = new JComboBox<>(new String[]{"All Statuses", "Available", "In Use", "Maintenance", "Retired"});
        filterPanel.add(statusFilter);
        
        JButton refreshButton = createStyledButton("Refresh");
        refreshButton.addActionListener(e -> {
            SwingUtilities.invokeLater(() -> {
                try {
                    TableRefreshManager.getInstance().refreshTable("computers");
                    DatabaseTableModel.getComputerTableModel().refreshData();
                    JOptionPane.showMessageDialog(dialog, "✓ Computer inventory refreshed successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(dialog, "Error refreshing data: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            });
        });
        filterPanel.add(refreshButton);
        
        JButton exportButton = new JButton("Export to CSV");
        exportButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            if (fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
                // Export data using service
                JOptionPane.showMessageDialog(null, "Inventory exported to CSV");
            }
        });
        filterPanel.add(exportButton);
    
        // Use database-connected table model
        DatabaseTableModel computerTableModel = DatabaseTableModel.getComputerTableModel();
        JTable inventoryTable = new JTable(computerTableModel);

        // Register table for auto-refresh
        TableRefreshManager.getInstance().registerTable("computers", inventoryTable, computerTableModel);
        inventoryTable.setAutoCreateRowSorter(true);
        JScrollPane scrollPane = new JScrollPane(inventoryTable);
    
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton addButton = new JButton("Add New Computer");
        addButton.addActionListener(e -> {
            showAddComputerDialog((Frame)dialog.getOwner());
        });
        
        JButton editButton = new JButton("Edit Selected");
        editButton.addActionListener(e -> {
            int selectedRow = inventoryTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(dialog, "Please select a computer to edit");
                return;
            }
            // Edit computer using service
            JOptionPane.showMessageDialog(dialog, "Editing computer: " + inventoryTable.getValueAt(selectedRow, 0));
        });
        
        JButton maintenanceButton = new JButton("Record Maintenance");
        maintenanceButton.addActionListener(e -> {
            int selectedRow = inventoryTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(dialog, "Please select a computer to record maintenance");
                return;
            }
            // Record maintenance using service
            JOptionPane.showMessageDialog(dialog, "Recording maintenance for: " + inventoryTable.getValueAt(selectedRow, 0));
        });
        
        actionPanel.add(addButton);
        actionPanel.add(editButton);
        actionPanel.add(maintenanceButton);
    
        mainPanel.add(filterPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(actionPanel, BorderLayout.SOUTH);
    
        dialog.add(mainPanel);
        dialog.setLocationRelativeTo(parent);
        dialog.setVisible(true);
    }

    private static void showAddComputerDialog(Frame parent) {
        JDialog dialog = new JDialog(parent, "Add New Computer", true);
        dialog.setSize(400, 350);
        dialog.setLayout(new BorderLayout());
        
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JComboBox<String> labCombo = new JComboBox<>(new String[]{"CS Lab 1", "CS Lab 2", "CS Lab 3", "SE Lab 1", "SE Lab 2", "AI Lab", "Data Lab"});
        JTextField computerNameField = new JTextField();
        JTextField ipAddressField = new JTextField();
        JTextField specificationsField = new JTextField();
        JComboBox<String> statusCombo = new JComboBox<>(new String[]{"Available", "In Use", "Maintenance"});
        
        formPanel.add(createFieldPanel("Lab:", labCombo));
        formPanel.add(Box.createVerticalStrut(10));
        formPanel.add(createFieldPanel("Computer Name:", computerNameField));
        formPanel.add(Box.createVerticalStrut(10));
        formPanel.add(createFieldPanel("IP Address:", ipAddressField));
        formPanel.add(Box.createVerticalStrut(10));
        formPanel.add(createFieldPanel("Specifications:", specificationsField));
        formPanel.add(Box.createVerticalStrut(10));
        formPanel.add(createFieldPanel("Initial Status:", statusCombo));
        formPanel.add(Box.createVerticalStrut(15));
        
        JButton submitButton = new JButton("Add Computer");
        submitButton.addActionListener(e -> {
            try {
                // Validate inputs
                InputValidator.validateSelection(labCombo.getSelectedItem(), "Lab");
                InputValidator.validateText(computerNameField.getText(), "Computer Name", 2, 50);
                InputValidator.validateIpAddress(ipAddressField.getText());
                InputValidator.validateText(specificationsField.getText(), "Specifications", 5, 200);
                InputValidator.validateSelection(statusCombo.getSelectedItem(), "Status");

                Computer computer = new Computer(
                    "PC-" + System.currentTimeMillis(), // Generate unique ID
                    labCombo.getSelectedItem().toString(),
                    computerNameField.getText(),
                    ipAddressField.getText(),
                    specificationsField.getText(),
                    statusCombo.getSelectedItem().toString(),
                    new Date().toString(),
                    ""
                );
                limsService.getComputerService().addComputer(computer);

                // Handle successful submission with automatic refresh
                DataRefreshUtil.showSuccessWithRefresh(
                    formPanel,
                    "Computer added to inventory successfully!",
                    "computer"
                );

                dialog.dispose();
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(dialog, "Error: " + ex.getMessage(), 
                    "Validation Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(submitButton, BorderLayout.SOUTH);
        dialog.setLocationRelativeTo(parent);
        dialog.setVisible(true);
    }

    public static void showSoftwareInstallationUpdateDialog(Frame parent) {
        JDialog dialog = new JDialog(parent, "Software Installation Updates", true);
        dialog.setSize(800, 600);
        dialog.setLayout(new BorderLayout());
        
        // Create table model
        String[] columns = {"Computer ID", "Software", "Version", "Status", "Request Date"};
        List<SoftwareRequest> requests = softwareRequestController.getAllRequests();
        Object[][] data = new Object[requests.size()][5];
        
        for (int i = 0; i < requests.size(); i++) {
            SoftwareRequest request = requests.get(i);
            data[i][0] = request.getComputerId();
            data[i][1] = request.getSoftwareName();
            data[i][2] = request.getVersion();
            data[i][3] = request.getStatus();
            data[i][4] = request.getRequestDate();
        }
        
        JTable table = new JTable(data, columns);
        JScrollPane scrollPane = new JScrollPane(table);
    
        JButton viewButton = new JButton("View Details");
        viewButton.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow >= 0) {
                String computerId = (String) table.getValueAt(selectedRow, 0);
                String softwareName = (String) table.getValueAt(selectedRow, 1);
                String version = (String) table.getValueAt(selectedRow, 2);
                showSoftwareRequestDetailsDialog(dialog, computerId, softwareName, version);
            } else {
                JOptionPane.showMessageDialog(dialog, "Please select a request to view details.");
            }
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(viewButton);
    
        dialog.add(scrollPane, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setLocationRelativeTo(parent);
        dialog.setVisible(true);
    }

    private static void showSoftwareRequestDetailsDialog(JDialog parent, String requestId) {
        SoftwareRequest request = softwareRequestController.getRequestById(requestId);
        if (request != null) {
            showSoftwareRequestDetailsDialog(parent, request);
        }
    }

    private static void showSoftwareRequestDetailsDialog(JDialog parent, SoftwareRequest request) {
        JDialog detailsDialog = new JDialog(parent, "Request Details", true);
        detailsDialog.setSize(400, 300);
        detailsDialog.setLayout(new BorderLayout());
        
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
    
        panel.add(createFieldPanel("Request ID:", new JLabel(request.getId())));
        panel.add(Box.createVerticalStrut(10));
        panel.add(createFieldPanel("Software:", new JLabel(request.getSoftwareName())));
        panel.add(Box.createVerticalStrut(10));
        panel.add(createFieldPanel("Version:", new JLabel(request.getVersion())));
        panel.add(Box.createVerticalStrut(10));
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
    
        detailsDialog.add(panel, BorderLayout.CENTER);
        detailsDialog.setLocationRelativeTo(parent);
        detailsDialog.setVisible(true);
    }

    private static void showSoftwareRequestDetailsDialog(JDialog parent, String computerId, String softwareName, String version) {
        List<SoftwareRequest> requests = softwareRequestController.getAllRequests();
        for (SoftwareRequest request : requests) {
            if (request.getComputerId().equals(computerId) &&
                request.getSoftwareName().equals(softwareName) &&
                request.getVersion().equals(version)) {
                showSoftwareRequestDetailsDialog(parent, request);
                break;
            }
        }
    }

    public static void showLaboratoryUsageReportDialog(Frame parent) {
        JDialog dialog = new JDialog(parent, "Laboratory Usage Reports", true);
        dialog.setSize(800, 600);
        dialog.setLayout(new BorderLayout());
        styleDialog(dialog);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(Color.WHITE);

        // Title
        JLabel titleLabel = new JLabel("Laboratory Usage Reports");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(new Color(41, 128, 185));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // Filter panel
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterPanel.setBackground(Color.WHITE);
        JComboBox<String> labCombo = createStyledComboBox(new String[]{"All Labs", "Lab A", "Lab B", "Lab C"});
        JComboBox<String> periodCombo = createStyledComboBox(new String[]{"Today", "This Week", "This Month", "Custom"});
        filterPanel.add(new JLabel("Lab:"));
        filterPanel.add(labCombo);
        filterPanel.add(new JLabel("Period:"));
        filterPanel.add(periodCombo);
        JButton generateButton = createStyledButton("Generate Report");
        filterPanel.add(generateButton);

        // Table
        String[] columns = {"Lab", "Date", "Usage Hours", "Users", "Purpose"};
        Object[][] data = {
            {"Lab A", "2024-03-20", "8", "25", "Programming"},
            {"Lab B", "2024-03-20", "6", "18", "Research"},
            {"Lab C", "2024-03-20", "4", "12", "Training"}
        };
        JTable table = new JTable(data, columns);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setRowHeight(30);
        JScrollPane scrollPane = new JScrollPane(table);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Color.WHITE);
        JButton exportButton = createStyledButton("Export to PDF");
        JButton printButton = createStyledButton("Print Report");
        exportButton.addActionListener(e -> JOptionPane.showMessageDialog(mainPanel, "Exported to PDF"));
        printButton.addActionListener(e -> JOptionPane.showMessageDialog(mainPanel, "Report sent to printer"));
        buttonPanel.add(exportButton);
        buttonPanel.add(printButton);

        mainPanel.add(filterPanel, BorderLayout.CENTER);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        dialog.add(mainPanel);
        dialog.setLocationRelativeTo(parent);
        dialog.setVisible(true);
    }

    public static void showSoftwareInventoryDialog(Frame parent) {
        JDialog dialog = new JDialog(parent, "Software Inventory Management", true);
        dialog.setSize(800, 600);
        dialog.setLayout(new BorderLayout());
        styleDialog(dialog);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(Color.WHITE);

        // Title
        JLabel titleLabel = new JLabel("Software Inventory");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(new Color(41, 128, 185));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // Table
        String[] columns = {"Software", "Version", "License", "Installations", "Status"};
        Object[][] data = {
            {"Windows 10", "21H2", "Enterprise", "45", "Active"},
            {"Office 365", "2021", "Subscription", "40", "Active"},
            {"Visual Studio", "2022", "Professional", "25", "Active"},
            {"MATLAB", "R2023b", "Academic", "15", "Active"},
            {"Adobe Creative Suite", "2024", "Enterprise", "20", "Active"},
            {"AutoCAD", "2024", "Educational", "10", "Active"},
            {"Python", "3.11.0", "Open Source", "35", "Active"},
            {"R Studio", "2023.12", "Academic", "12", "Active"}
        };
        JTable table = new JTable(data, columns);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setRowHeight(30);
        JScrollPane scrollPane = new JScrollPane(table);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Color.WHITE);
        JButton addButton = createStyledButton("Add Software");
        JButton editButton = createStyledButton("Edit");
        JButton deleteButton = createStyledButton("Delete");
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);

        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        dialog.add(mainPanel);
        dialog.setLocationRelativeTo(parent);
        dialog.setVisible(true);
    }

    public static void showLaboratoryManagementDialog(Frame parent) {
        JDialog dialog = new JDialog(parent, "Laboratory Management", true);
        dialog.setSize(800, 600);
        dialog.setLayout(new BorderLayout());
        styleDialog(dialog);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(Color.WHITE);

        // Title
        JLabel titleLabel = new JLabel("Laboratory Management");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(new Color(41, 128, 185));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // Table
        // Use database-connected table model
        DatabaseTableModel labTableModel = DatabaseTableModel.getLabTableModel();
        JTable table = new JTable(labTableModel);

        // Register table for auto-refresh
        TableRefreshManager.getInstance().registerTable("labs", table, labTableModel);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setRowHeight(30);
        JScrollPane scrollPane = new JScrollPane(table);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Color.WHITE);
        JButton addButton = createStyledButton("Add Laboratory");
        JButton editButton = createStyledButton("Edit");
        JButton maintenanceButton = createStyledButton("Schedule Maintenance");
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(maintenanceButton);

        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        dialog.add(mainPanel);
        dialog.setLocationRelativeTo(parent);
        dialog.setVisible(true);
    }

    public static void showComplaintTrackingDialog(Frame parent) {
        JDialog dialog = new JDialog(parent, "Complaint Tracking", true);
        dialog.setSize(800, 600);
        dialog.setLayout(new BorderLayout());
        styleDialog(dialog);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(Color.WHITE);

        // Title
        JLabel titleLabel = new JLabel("Complaint Tracking");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(new Color(41, 128, 185));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // Filter panel
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterPanel.setBackground(Color.WHITE);
        JComboBox<String> statusCombo = createStyledComboBox(new String[]{"All", "Open", "In Progress", "Resolved"});
        JComboBox<String> typeCombo = createStyledComboBox(new String[]{"All", "Hardware", "Software", "Network"});
        filterPanel.add(new JLabel("Status:"));
        filterPanel.add(statusCombo);
        filterPanel.add(new JLabel("Type:"));
        filterPanel.add(typeCombo);

        // Table
        // Use database-connected table model
        DatabaseTableModel complaintTableModel = DatabaseTableModel.getComplaintTableModel();
        JTable table = new JTable(complaintTableModel);

        // Register table for auto-refresh
        TableRefreshManager.getInstance().registerTable("complaints", table, complaintTableModel);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setRowHeight(30);
        JScrollPane scrollPane = new JScrollPane(table);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Color.WHITE);
        JButton updateButton = createStyledButton("Update Status");
        JButton assignButton = createStyledButton("Assign");
        JButton resolveButton = createStyledButton("Mark Resolved");
        buttonPanel.add(updateButton);
        buttonPanel.add(assignButton);
        buttonPanel.add(resolveButton);

        mainPanel.add(filterPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        dialog.add(mainPanel);
        dialog.setLocationRelativeTo(parent);
        dialog.setVisible(true);
    }

    public static void showInstallationRequestApprovalDialog(Frame parent) {
        JDialog dialog = new JDialog(parent, "Software Installation Requests", true);
        dialog.setSize(800, 600);
        dialog.setLayout(new BorderLayout());
        styleDialog(dialog);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(Color.WHITE);

        // Title
        JLabel titleLabel = new JLabel("Software Installation Requests");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(new Color(41, 128, 185));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // Table
        String[] columns = {"Request ID", "Software", "Version", "Requested By", "Date", "Status"};
        Object[][] data = {
            {"R001", "Visual Studio", "2022", "John Doe", "2024-03-20", "Pending"},
            {"R002", "Adobe Photoshop", "2024", "Jane Smith", "2024-03-19", "Pending"},
            {"R003", "MATLAB", "R2023b", "Mike Johnson", "2024-03-18", "Approved"},
            {"R004", "AutoCAD", "2024", "Sarah Williams", "2024-03-18", "Pending"},
            {"R005", "Python", "3.11.0", "David Brown", "2024-03-17", "Approved"}
        };
        JTable table = new JTable(data, columns);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setRowHeight(30);
        JScrollPane scrollPane = new JScrollPane(table);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Color.WHITE);
        JButton approveButton = createStyledButton("Approve");
        JButton rejectButton = createStyledButton("Reject");
        JButton viewButton = createStyledButton("View Details");
        buttonPanel.add(viewButton);
        buttonPanel.add(approveButton);
        buttonPanel.add(rejectButton);

        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        dialog.add(mainPanel);
        dialog.setLocationRelativeTo(parent);
        dialog.setVisible(true);
    }

    public static void showUserAccessControlDialog(Frame parent) {
        JDialog dialog = new JDialog(parent, "User Access Control", true);
        dialog.setSize(800, 600);
        dialog.setLayout(new BorderLayout());
        styleDialog(dialog);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(Color.WHITE);

        // Title
        JLabel titleLabel = new JLabel("User Access Control");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(new Color(41, 128, 185));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // Table
        // Use database-connected table model
        DatabaseTableModel userTableModel = DatabaseTableModel.getUserAccessTableModel();
        JTable table = new JTable(userTableModel);

        // Register table for auto-refresh
        TableRefreshManager.getInstance().registerTable("user_access", table, userTableModel);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setRowHeight(30);
        JScrollPane scrollPane = new JScrollPane(table);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Color.WHITE);
        JButton addButton = createStyledButton("Add User");
        JButton editButton = createStyledButton("Edit Access");
        JButton deactivateButton = createStyledButton("Deactivate");
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deactivateButton);

        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        dialog.add(mainPanel);
        dialog.setLocationRelativeTo(parent);
        dialog.setVisible(true);
    }

    public static void showInventoryReportDialog(Frame parent) {
        JDialog dialog = new JDialog(parent, "Inventory Reports", true);
        dialog.setSize(800, 600);
        dialog.setLayout(new BorderLayout());
        styleDialog(dialog);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(Color.WHITE);

        // Title
        JLabel titleLabel = new JLabel("Inventory Reports");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(new Color(41, 128, 185));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // Filter panel
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterPanel.setBackground(Color.WHITE);
        JComboBox<String> categoryCombo = createStyledComboBox(new String[]{"All", "Hardware", "Software", "Equipment"});
        JComboBox<String> statusCombo = createStyledComboBox(new String[]{"All", "Available", "In Use", "Maintenance"});
        filterPanel.add(new JLabel("Category:"));
        filterPanel.add(categoryCombo);
        filterPanel.add(new JLabel("Status:"));
        filterPanel.add(statusCombo);
        JButton generateButton = createStyledButton("Generate Report");
        filterPanel.add(generateButton);

        // Table
        // Use database-connected table model
        DatabaseTableModel softwareTableModel = DatabaseTableModel.getSoftwareInventoryTableModel();
        JTable table = new JTable(softwareTableModel);

        // Register table for auto-refresh
        TableRefreshManager.getInstance().registerTable("software", table, softwareTableModel);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setRowHeight(30);
        JScrollPane scrollPane = new JScrollPane(table);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Color.WHITE);
        JButton exportButton = createStyledButton("Export to PDF");
        JButton printButton = createStyledButton("Print Report");
        exportButton.addActionListener(e -> JOptionPane.showMessageDialog(mainPanel, "Exported to PDF"));
        printButton.addActionListener(e -> JOptionPane.showMessageDialog(mainPanel, "Report sent to printer"));
        buttonPanel.add(exportButton);
        buttonPanel.add(printButton);

        mainPanel.add(filterPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        dialog.add(mainPanel);
        dialog.setLocationRelativeTo(parent);
        dialog.setVisible(true);
    }

    public static void showLabSlotRequestDialog(Frame parent) {
        JDialog dialog = new JDialog(parent, "Request Lab Slot", true);
        dialog.setSize(500, 600);
        dialog.setLayout(new BorderLayout());
        styleDialog(dialog);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(Color.WHITE);

        // Title
        JLabel titleLabel = new JLabel("Request Lab Slot");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(new Color(41, 128, 185));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(titleLabel);
        mainPanel.add(Box.createVerticalStrut(20));

        // Form fields
        JComboBox<String> labCombo = createStyledComboBox(new String[]{"Lab A", "Lab B", "Lab C", "Lab D"});
        JTextField dateField = createStyledTextField();
        JComboBox<String> startTimeCombo = createStyledComboBox(new String[]{
            "08:00", "09:00", "10:00", "11:00", "12:00", "13:00", "14:00", "15:00", "16:00"
        });
        JComboBox<String> durationCombo = createStyledComboBox(new String[]{"1", "2", "3", "4"});
        JComboBox<String> purposeCombo = createStyledComboBox(new String[]{
            "Lecture", "Practical", "Workshop", "Exam", "Other"
        });
        JTextArea descriptionArea = createStyledTextArea();
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        JScrollPane descriptionScroll = new JScrollPane(descriptionArea);
        descriptionScroll.setPreferredSize(new Dimension(450, 100));

        // Add form fields to panel
        mainPanel.add(createFieldPanel("Select Lab:", labCombo));
        mainPanel.add(Box.createVerticalStrut(15));
        mainPanel.add(createFieldPanel("Date (YYYY-MM-DD):", dateField));
        mainPanel.add(Box.createVerticalStrut(15));
        mainPanel.add(createFieldPanel("Start Time:", startTimeCombo));
        mainPanel.add(Box.createVerticalStrut(15));
        mainPanel.add(createFieldPanel("Duration (hours):", durationCombo));
        mainPanel.add(Box.createVerticalStrut(15));
        mainPanel.add(createFieldPanel("Purpose:", purposeCombo));
        mainPanel.add(Box.createVerticalStrut(15));

        // Description field
        JPanel descPanel = new JPanel(new BorderLayout(10, 5));
        descPanel.setBackground(Color.WHITE);
        descPanel.add(new JLabel("Additional Details:"), BorderLayout.WEST);
        descPanel.add(descriptionScroll, BorderLayout.CENTER);
        mainPanel.add(descPanel);
        mainPanel.add(Box.createVerticalStrut(20));

        // Check availability button
        JButton checkButton = createStyledButton("Check Availability");
        checkButton.setMaximumSize(new Dimension(200, 40));
        checkButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        checkButton.addActionListener(e -> {
            // TODO: Implement availability check
            JOptionPane.showMessageDialog(dialog, "Checking availability...");
        });
        mainPanel.add(checkButton);
        mainPanel.add(Box.createVerticalStrut(15));

        // Submit button
        JButton submitButton = createStyledButton("Submit Request");
        submitButton.setMaximumSize(new Dimension(200, 40));
        submitButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        submitButton.addActionListener(e -> {
            try {
                // Validate inputs
                InputValidator.validateSelection(labCombo.getSelectedItem(), "Lab");
                InputValidator.validateDate(dateField.getText());
                InputValidator.validateSelection(startTimeCombo.getSelectedItem(), "Start Time");
                InputValidator.validateDuration(durationCombo.getSelectedItem().toString());
                InputValidator.validateSelection(purposeCombo.getSelectedItem(), "Purpose");
                InputValidator.validateText(descriptionArea.getText(), "Description", 5, 500);

                // TODO: Implement lab slot request submission
                JOptionPane.showMessageDialog(dialog, "Lab slot request submitted successfully!");
                dialog.dispose();
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(dialog, "Error: " + ex.getMessage(), 
                    "Validation Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        dialog.add(mainPanel, BorderLayout.CENTER);
        dialog.add(submitButton, BorderLayout.SOUTH);
        dialog.setLocationRelativeTo(parent);
        dialog.setVisible(true);
    }

    public static void showUserRequestStatusDialog(JFrame parent, String username) {
        JDialog dialog = new JDialog(parent, "Your Request Status", true);
        dialog.setSize(800, 600);
        dialog.setLocationRelativeTo(parent);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Create table model
        String[] columnNames = {"Request ID", "Type", "Status", "Date", "Details"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        // Get user's requests
        List<SoftwareRequest> softwareRequests = softwareRequestController.getAllRequests();
        for (SoftwareRequest request : softwareRequests) {
            if (request.getComputerId().equals(username)) {
                Object[] row = {
                    request.getId(),
                    "Software Request",
                    request.getStatus(),
                    request.getRequestDate(),
                    "View Details"
                };
                model.addRow(row);
            }
        }

        // Create table
        JTable table = new JTable(model);
        table.setRowHeight(30);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Add table to scroll pane
        JScrollPane scrollPane = new JScrollPane(table);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Add button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> dialog.dispose());
        buttonPanel.add(closeButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Add double-click listener for viewing details
        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = table.getSelectedRow();
                    if (row != -1) {
                        String requestId = (String) table.getValueAt(row, 0);
                        String type = (String) table.getValueAt(row, 1);
                        
                        if (type.equals("Software Request")) {
                            for (SoftwareRequest request : softwareRequests) {
                                if (request.getId().equals(requestId)) {
                                    showSoftwareRequestDetailsDialog(dialog, request);
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        });

        dialog.add(mainPanel);
        dialog.setVisible(true);
    }

    public static JPanel getLabSchedulePanel() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(Color.WHITE);

        JLabel titleLabel = new JLabel("Lab Schedule");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(new Color(41, 128, 185));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(titleLabel);
        mainPanel.add(Box.createVerticalStrut(20));

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        filterPanel.add(new JLabel("Filter by:"));
        JComboBox<String> labCombo = new JComboBox<>(new String[]{"All Labs", "Lab A", "Lab B", "Lab C", "Lab D"});
        filterPanel.add(labCombo);
        JComboBox<String> dateCombo = new JComboBox<>(new String[]{"Today", "This Week", "This Month", "Custom Date"});
        filterPanel.add(dateCombo);
        JButton refreshButton = createStyledButton("Refresh");
        filterPanel.add(refreshButton);
        mainPanel.add(filterPanel);
        mainPanel.add(Box.createVerticalStrut(15));

        // Use database-connected table model
        DatabaseTableModel reservationTableModel = DatabaseTableModel.getLabReservationTableModel();
        JTable scheduleTable = new JTable(reservationTableModel);

        // Configure table for better display
        scheduleTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        scheduleTable.setRowHeight(25);
        scheduleTable.setAutoCreateRowSorter(true);
        scheduleTable.setFillsViewportHeight(true);

        // Register with both managers for comprehensive data management
        TableRefreshManager.getInstance().registerTable("lab_reservations", scheduleTable, reservationTableModel);
        JPanelDataManager.getInstance().registerPanel("lab_schedule", mainPanel, reservationTableModel, scheduleTable);

        JScrollPane scrollPane = new JScrollPane(scheduleTable);
        scrollPane.setPreferredSize(new Dimension(800, 400));
        mainPanel.add(scrollPane);

        // Ensure data is properly loaded and displayed
        SwingUtilities.invokeLater(() -> {
            System.out.println("🔄 Ensuring lab schedule data is loaded and displayed...");
            JPanelDataManager.getInstance().ensurePanelDataLoaded("lab_schedule");
        });
        mainPanel.add(Box.createVerticalStrut(15));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton reserveButton = createStyledButton("New Reservation");
        buttonPanel.add(reserveButton);
        JButton printButton = createStyledButton("Print Schedule");
        buttonPanel.add(printButton);
        mainPanel.add(buttonPanel);

        reserveButton.addActionListener(e -> {
            showLabReservationDialog(SwingUtilities.getWindowAncestor(mainPanel));
        });
        printButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(mainPanel, "Schedule sent to printer");
        });
        refreshButton.addActionListener(e -> {
            SwingUtilities.invokeLater(() -> {
                try {
                    // Refresh the lab reservation data
                    reservationTableModel.refreshData();
                    TableRefreshManager.getInstance().refreshTable("lab_reservations");
                    JOptionPane.showMessageDialog(mainPanel, "Lab schedule refreshed successfully!",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(mainPanel, "Error refreshing data: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            });
        });

        return mainPanel;
    }

    public static JPanel getComplaintTrackingPanel() {
        System.out.println("🖥️ Creating Complaint Tracking Panel...");

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(Color.WHITE);

        JLabel titleLabel = new JLabel("Complaint Tracking");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(new Color(41, 128, 185));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // Create database-connected table model
        System.out.println("📊 Creating complaint table model...");
        DatabaseTableModel tableModel = DatabaseTableModel.getComplaintTableModel();
        System.out.println("📋 Complaint table model created with " + tableModel.getRowCount() + " rows");

        JTable table = new JTable(tableModel);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setRowHeight(30);
        table.setAutoCreateRowSorter(true);
        table.setFillsViewportHeight(true);

        // Register table for automatic refresh
        TableRefreshManager.getInstance().registerTable("complaints", table, tableModel);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(800, 400));
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Force immediate data refresh to ensure visibility
        SwingUtilities.invokeLater(() -> {
            System.out.println("🔄 Force refreshing complaint data for UI display...");
            tableModel.forceRefresh();
            table.revalidate();
            table.repaint();
            System.out.println("📊 Complaint table now has " + tableModel.getRowCount() + " rows");
        });

        // Add button panel for actions
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Color.WHITE);

        JButton refreshButton = createStyledButton("Refresh");
        refreshButton.addActionListener(e -> {
            tableModel.refreshData();
            JOptionPane.showMessageDialog(mainPanel, "Complaint data refreshed!", "Success", JOptionPane.INFORMATION_MESSAGE);
        });

        JButton updateStatusButton = createStyledButton("Update Status");
        updateStatusButton.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow >= 0) {
                String computerId = table.getValueAt(selectedRow, 1).toString(); // Computer ID column
                Window window = SwingUtilities.getWindowAncestor(mainPanel);
                if (window instanceof Frame) {
                    showUpdateComplaintStatusDialog((Frame) window, computerId);
                } else {
                    JOptionPane.showMessageDialog(mainPanel, "Update status functionality is available in the main dashboard.");
                }
            } else {
                JOptionPane.showMessageDialog(mainPanel, "Please select a complaint to update.", "No Selection", JOptionPane.WARNING_MESSAGE);
            }
        });

        buttonPanel.add(refreshButton);
        buttonPanel.add(updateStatusButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        return mainPanel;
    }

    public static JPanel getComputerInventoryPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        filterPanel.add(new JLabel("Filter by:"));

        // Create dynamic lab filter from database
        JComboBox<String> labFilter = new JComboBox<>();
        labFilter.addItem("All Labs");
        // Populate labs from database
        try {
            DatabaseTableModel labModel = DatabaseTableModel.getLabTableModel();
            for (int i = 0; i < labModel.getRowCount(); i++) {
                String labName = labModel.getValueAt(i, 1).toString(); // Lab name column
                labFilter.addItem(labName);
            }
        } catch (Exception e) {
            // Fallback if database is not available
            labFilter.addItem("Lab 1");
            labFilter.addItem("Lab 2");
        }
        filterPanel.add(labFilter);

        // Create dynamic status filter
        JComboBox<String> statusFilter = new JComboBox<>(new String[]{"All Statuses", "Available", "In Use", "Maintenance", "Retired"});
        filterPanel.add(statusFilter);

        JButton refreshButton = createStyledButton("Refresh");
        refreshButton.addActionListener(e -> {
            // Get the computer table model and refresh it
            DatabaseTableModel computerModel = DatabaseTableModel.getComputerTableModel();
            computerModel.refreshData();
            JOptionPane.showMessageDialog(mainPanel, "Inventory data refreshed!");
        });
        filterPanel.add(refreshButton);

        JButton exportButton = new JButton("Export to CSV");
        exportButton.addActionListener(e -> JOptionPane.showMessageDialog(mainPanel, "Export functionality would be implemented here"));
        filterPanel.add(exportButton);
        mainPanel.add(filterPanel, BorderLayout.NORTH);

        // Use database-connected table model
        DatabaseTableModel computerTableModel = DatabaseTableModel.getComputerTableModel();
        JTable inventoryTable = new JTable(computerTableModel);

        // Configure table for better display
        inventoryTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        inventoryTable.setRowHeight(25);
        inventoryTable.setAutoCreateRowSorter(true);
        inventoryTable.setFillsViewportHeight(true);

        // Register with both managers for comprehensive data management
        TableRefreshManager.getInstance().registerTable("computers", inventoryTable, computerTableModel);
        JPanelDataManager.getInstance().registerPanel("computer_inventory", mainPanel, computerTableModel, inventoryTable);

        JScrollPane scrollPane = new JScrollPane(inventoryTable);
        scrollPane.setPreferredSize(new Dimension(800, 400));
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Ensure data is properly loaded and displayed
        SwingUtilities.invokeLater(() -> {
            System.out.println("🔄 Ensuring computer inventory data is loaded and displayed...");
            JPanelDataManager.getInstance().ensurePanelDataLoaded("computer_inventory");
        });

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton addButton = createStyledButton("Add New Computer");
        addButton.addActionListener(e -> showAddComputerDialog(SwingUtilities.getWindowAncestor(mainPanel)));
        JButton editButton = createStyledButton("Edit Selected");
        editButton.addActionListener(e -> showEditComputerDialog(SwingUtilities.getWindowAncestor(mainPanel), inventoryTable));
        JButton maintenanceButton = createStyledButton("Record Maintenance");
        maintenanceButton.addActionListener(e -> showRecordMaintenanceDialog(SwingUtilities.getWindowAncestor(mainPanel), inventoryTable));
        actionPanel.add(addButton);
        actionPanel.add(editButton);
        actionPanel.add(maintenanceButton);
        mainPanel.add(actionPanel, BorderLayout.SOUTH);
        return mainPanel;
    }

    public static JPanel getSoftwareManagementPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(Color.WHITE);
        // Use database-connected table model
        DatabaseTableModel softwareTableModel = DatabaseTableModel.getSoftwareRequestTableModel();
        JTable table = new JTable(softwareTableModel);

        // Configure table for better display
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setRowHeight(25);
        table.setAutoCreateRowSorter(true);
        table.setFillsViewportHeight(true);

        // Register with both managers for comprehensive data management
        TableRefreshManager.getInstance().registerTable("software_requests", table, softwareTableModel);
        JPanelDataManager.getInstance().registerPanel("software_management", mainPanel, softwareTableModel, table);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(800, 400));

        // Ensure data is properly loaded and displayed
        SwingUtilities.invokeLater(() -> {
            System.out.println("🔄 Ensuring software request data is loaded and displayed...");
            JPanelDataManager.getInstance().ensurePanelDataLoaded("software_management");
        });

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Color.WHITE);
        JButton addSoftwareButton = createStyledButton("Add Software");
        JButton viewButton = createStyledButton("View Details");
        JButton updateStatusButton = createStyledButton("Update Status");

        addSoftwareButton.addActionListener(e -> showAddSoftwareDialog(SwingUtilities.getWindowAncestor(mainPanel)));
        viewButton.addActionListener(e -> showViewSoftwareDetailsDialog(SwingUtilities.getWindowAncestor(mainPanel), table));
        updateStatusButton.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow >= 0) {
                String requestId = table.getValueAt(selectedRow, 0).toString();
                showUpdateSoftwareStatusDialog((JDialog) SwingUtilities.getWindowAncestor(mainPanel), requestId);
            } else {
                JOptionPane.showMessageDialog(mainPanel, "Please select a software request to update.");
            }
        });

        buttonPanel.add(addSoftwareButton);
        buttonPanel.add(viewButton);
        buttonPanel.add(updateStatusButton);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        return mainPanel;
    }

    public static JPanel getReportStatusPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(Color.WHITE);
        JLabel titleLabel = new JLabel("Report Status");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(new Color(41, 128, 185));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        mainPanel.add(titleLabel, BorderLayout.NORTH);
        // Use database-connected table model
        DatabaseTableModel complaintTableModel = DatabaseTableModel.getComplaintTableModel();
        JTable table = new JTable(complaintTableModel);

        // Register table for auto-refresh
        TableRefreshManager.getInstance().registerTable("complaints", table, complaintTableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Color.WHITE);
        JButton changeStatusButton = createStyledButton("Change Status");
        JButton assignButton = createStyledButton("Assign To");
        JButton viewDetailsButton = createStyledButton("View Details");

        changeStatusButton.addActionListener(e -> showChangeStatusDialog(SwingUtilities.getWindowAncestor(mainPanel), table));
        assignButton.addActionListener(e -> showAssignComplaintDialog(SwingUtilities.getWindowAncestor(mainPanel), table));
        viewDetailsButton.addActionListener(e -> showViewComplaintDetailsDialog(SwingUtilities.getWindowAncestor(mainPanel), table));

        buttonPanel.add(viewDetailsButton);
        buttonPanel.add(assignButton);
        buttonPanel.add(changeStatusButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        return mainPanel;
    }

    public static JPanel getLaboratoryUsageReportPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(Color.WHITE);
        JLabel titleLabel = new JLabel("Laboratory Usage Reports");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(new Color(41, 128, 185));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // Use database-connected table model for lab reservations
        DatabaseTableModel tableModel = DatabaseTableModel.getLabReservationTableModel();
        JTable table = new JTable(tableModel);

        // Register table for automatic refresh
        TableRefreshManager.getInstance().registerTable("lab_reservations", table, tableModel);

        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setRowHeight(30);
        JScrollPane scrollPane = new JScrollPane(table);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Force refresh data after UI is created to ensure visibility
        SwingUtilities.invokeLater(() -> {
            System.out.println("🔄 Force refreshing lab reservation data for UI display...");
            tableModel.forceRefresh();
        });
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Color.WHITE);
        JButton exportButton = createStyledButton("Export to PDF");
        JButton printButton = createStyledButton("Print Report");
        exportButton.addActionListener(e -> JOptionPane.showMessageDialog(mainPanel, "Exported to PDF"));
        printButton.addActionListener(e -> JOptionPane.showMessageDialog(mainPanel, "Report sent to printer"));
        buttonPanel.add(exportButton);
        buttonPanel.add(printButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        return mainPanel;
    }

    public static JPanel getSoftwareInventoryPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(Color.WHITE);
        JLabel titleLabel = new JLabel("Software Inventory");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(new Color(41, 128, 185));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // Use database-connected table model for software inventory
        DatabaseTableModel tableModel = DatabaseTableModel.getSoftwareInventoryTableModel();
        JTable table = new JTable(tableModel);

        // Register table for automatic refresh
        TableRefreshManager.getInstance().registerTable("software", table, tableModel);

        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setRowHeight(30);
        JScrollPane scrollPane = new JScrollPane(table);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Color.WHITE);
        JButton addButton = createStyledButton("Add Software");
        JButton editButton = createStyledButton("Edit");
        JButton deleteButton = createStyledButton("Delete");
        addButton.addActionListener(e -> showAddSoftwareInventoryDialog(SwingUtilities.getWindowAncestor(mainPanel)));
        editButton.addActionListener(e -> showEditSoftwareDialog(SwingUtilities.getWindowAncestor(mainPanel), table));
        deleteButton.addActionListener(e -> showDeleteSoftwareDialog(SwingUtilities.getWindowAncestor(mainPanel), table));
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        return mainPanel;
    }

    public static JPanel getLaboratoryManagementPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(Color.WHITE);
        JLabel titleLabel = new JLabel("Laboratory Management");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(new Color(41, 128, 185));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // Use database-connected table model for labs
        DatabaseTableModel tableModel = DatabaseTableModel.getLabTableModel();
        JTable table = new JTable(tableModel);

        // Register table for automatic refresh
        TableRefreshManager.getInstance().registerTable("labs", table, tableModel);

        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setRowHeight(30);
        JScrollPane scrollPane = new JScrollPane(table);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Color.WHITE);
        JButton addButton = createStyledButton("Add Laboratory");
        JButton editButton = createStyledButton("Edit");
        JButton maintenanceButton = createStyledButton("Schedule Maintenance");
        addButton.addActionListener(e -> showAddLaboratoryDialog(SwingUtilities.getWindowAncestor(mainPanel)));
        editButton.addActionListener(e -> showEditLaboratoryDialog(SwingUtilities.getWindowAncestor(mainPanel), table));
        maintenanceButton.addActionListener(e -> showScheduleMaintenanceDialog(SwingUtilities.getWindowAncestor(mainPanel), table));
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(maintenanceButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        return mainPanel;
    }

    public static JPanel getInstallationRequestsPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(Color.WHITE);
        JLabel titleLabel = new JLabel("Software Installation Requests");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(new Color(41, 128, 185));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // Create database-connected table model
        DatabaseTableModel tableModel = DatabaseTableModel.getSoftwareRequestTableModel();
        JTable table = new JTable(tableModel);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setRowHeight(30);

        // Register table for automatic refresh
        TableRefreshManager.getInstance().registerTable("software_requests", table, tableModel);

        JScrollPane scrollPane = new JScrollPane(table);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Color.WHITE);

        JButton refreshButton = createStyledButton("Refresh");
        refreshButton.addActionListener(e -> {
            tableModel.refreshData();
            JOptionPane.showMessageDialog(mainPanel, "Software requests refreshed!", "Success", JOptionPane.INFORMATION_MESSAGE);
        });

        JButton approveButton = createStyledButton("Approve");
        JButton rejectButton = createStyledButton("Reject");
        JButton viewButton = createStyledButton("View Details");
        approveButton.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow >= 0) {
                String requestId = table.getValueAt(selectedRow, 0).toString();
                String computerId = table.getValueAt(selectedRow, 1).toString();
                String softwareName = table.getValueAt(selectedRow, 2).toString();
                showApproveRequestDialog(SwingUtilities.getWindowAncestor(mainPanel), requestId, computerId, softwareName);
            } else {
                JOptionPane.showMessageDialog(mainPanel, "Please select a request to approve.");
            }
        });

        rejectButton.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow >= 0) {
                String requestId = table.getValueAt(selectedRow, 0).toString();
                String computerId = table.getValueAt(selectedRow, 1).toString();
                String softwareName = table.getValueAt(selectedRow, 2).toString();
                showRejectRequestDialog(SwingUtilities.getWindowAncestor(mainPanel), requestId, computerId, softwareName);
            } else {
                JOptionPane.showMessageDialog(mainPanel, "Please select a request to reject.");
            }
        });
        viewButton.addActionListener(e -> showViewSoftwareDetailsDialog(SwingUtilities.getWindowAncestor(mainPanel), table));
        buttonPanel.add(refreshButton);
        buttonPanel.add(viewButton);
        buttonPanel.add(approveButton);
        buttonPanel.add(rejectButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        return mainPanel;
    }

    public static JPanel getUserAccessControlPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(Color.WHITE);
        JLabel titleLabel = new JLabel("User Access Control");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(new Color(41, 128, 185));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // Use database-connected table model for user access
        DatabaseTableModel tableModel = DatabaseTableModel.getUserAccessTableModel();
        JTable table = new JTable(tableModel);

        // Register table for automatic refresh
        TableRefreshManager.getInstance().registerTable("user_access", table, tableModel);

        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setRowHeight(30);
        JScrollPane scrollPane = new JScrollPane(table);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Color.WHITE);
        JButton addButton = createStyledButton("Add User");
        JButton editButton = createStyledButton("Edit Access");
        JButton deactivateButton = createStyledButton("Deactivate");
        JButton manageUsersButton = createStyledButton("Manage Users");
        addButton.addActionListener(e -> showAddUserDialog(SwingUtilities.getWindowAncestor(mainPanel)));
        editButton.addActionListener(e -> showEditUserAccessDialog(SwingUtilities.getWindowAncestor(mainPanel), table));
        deactivateButton.addActionListener(e -> showDeactivateUserDialog(SwingUtilities.getWindowAncestor(mainPanel), table));
        manageUsersButton.addActionListener(e -> showUserManagementDialog(SwingUtilities.getWindowAncestor(mainPanel)));
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deactivateButton);
        buttonPanel.add(manageUsersButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        return mainPanel;
    }

    public static void showAddUserDialog(Window parent) {
        JDialog dialog = new JDialog((Frame) parent, "Add User", true);
        dialog.setSize(450, 450);
        dialog.setLayout(new BorderLayout());
        styleDialog(dialog);

        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        formPanel.setBackground(Color.WHITE);

        JLabel titleLabel = new JLabel("Add New User");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(new Color(41, 128, 185));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        formPanel.add(titleLabel);
        formPanel.add(Box.createVerticalStrut(20));

        JTextField userIdField = createStyledTextField();
        JTextField nameField = createStyledTextField();
        JComboBox<String> roleCombo = createStyledComboBox(new String[]{"Admin", "Teacher", "Student", "Lab Technician"});
        JComboBox<String> departmentCombo = createStyledComboBox(new String[]{"IT", "Computer Science", "Software Engineering", "AI", "Data Science"});
        JComboBox<String> accessLevelCombo = createStyledComboBox(new String[]{"Full", "Limited", "Basic", "Read Only"});
        JComboBox<String> statusCombo = createStyledComboBox(new String[]{"Active", "Inactive", "Pending", "Suspended"});

        formPanel.add(createFieldPanel("User ID:", userIdField));
        formPanel.add(Box.createVerticalStrut(15));
        formPanel.add(createFieldPanel("Name:", nameField));
        formPanel.add(Box.createVerticalStrut(15));
        formPanel.add(createFieldPanel("Role:", roleCombo));
        formPanel.add(Box.createVerticalStrut(15));
        formPanel.add(createFieldPanel("Department:", departmentCombo));
        formPanel.add(Box.createVerticalStrut(15));
        formPanel.add(createFieldPanel("Access Level:", accessLevelCombo));
        formPanel.add(Box.createVerticalStrut(15));
        formPanel.add(createFieldPanel("Status:", statusCombo));
        formPanel.add(Box.createVerticalStrut(20));

        JButton addButton = createStyledButton("Add User");
        addButton.setMaximumSize(new Dimension(150, 40));
        addButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        addButton.addActionListener(e -> {
            try {
                // Enhanced validation
                InputValidator.validateUserId(userIdField.getText());
                InputValidator.validateName(nameField.getText());
                InputValidator.validateStringLength(nameField.getText(), "Name", 2, 50);
                InputValidator.validateRole(roleCombo.getSelectedItem().toString());
                InputValidator.validateDepartment(departmentCombo.getSelectedItem().toString());
                InputValidator.validateAccessLevel(accessLevelCombo.getSelectedItem().toString());
                InputValidator.validateStatus(statusCombo.getSelectedItem().toString());

                JOptionPane.showMessageDialog(dialog, "User added successfully!");

                // Auto-refresh the user management table
                TableRefreshManager.getInstance().refreshRelatedTables("user");

                dialog.dispose();
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(dialog, "Error: " + ex.getMessage(),
                    "Validation Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        formPanel.add(addButton);
        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.setLocationRelativeTo(parent);
        dialog.setVisible(true);
    }

    public static void showEditUserAccessDialog(Window parent, JTable table) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(parent, "Please select a user to edit access.",
                "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JDialog dialog = new JDialog((Frame) parent, "Edit User Access", true);
        dialog.setSize(450, 400);
        dialog.setLayout(new BorderLayout());
        styleDialog(dialog);

        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        formPanel.setBackground(Color.WHITE);

        JLabel titleLabel = new JLabel("Edit User Access");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(new Color(41, 128, 185));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        formPanel.add(titleLabel);
        formPanel.add(Box.createVerticalStrut(20));

        String userName = table.getValueAt(selectedRow, 1).toString();
        JLabel userLabel = new JLabel("User: " + userName);
        userLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        formPanel.add(userLabel);
        formPanel.add(Box.createVerticalStrut(15));

        // Pre-fill with current values
        JComboBox<String> roleCombo = createStyledComboBox(new String[]{"Admin", "Teacher", "Student", "Lab Technician"});
        roleCombo.setSelectedItem(table.getValueAt(selectedRow, 2).toString());
        JComboBox<String> departmentCombo = createStyledComboBox(new String[]{"IT", "Computer Science", "Software Engineering", "AI", "Data Science"});
        departmentCombo.setSelectedItem(table.getValueAt(selectedRow, 3).toString());
        JComboBox<String> accessLevelCombo = createStyledComboBox(new String[]{"Full", "Limited", "Basic", "Read Only"});
        accessLevelCombo.setSelectedItem(table.getValueAt(selectedRow, 4).toString());
        JComboBox<String> statusCombo = createStyledComboBox(new String[]{"Active", "Inactive", "Pending", "Suspended"});
        statusCombo.setSelectedItem(table.getValueAt(selectedRow, 5).toString());

        formPanel.add(createFieldPanel("Role:", roleCombo));
        formPanel.add(Box.createVerticalStrut(15));
        formPanel.add(createFieldPanel("Department:", departmentCombo));
        formPanel.add(Box.createVerticalStrut(15));
        formPanel.add(createFieldPanel("Access Level:", accessLevelCombo));
        formPanel.add(Box.createVerticalStrut(15));
        formPanel.add(createFieldPanel("Status:", statusCombo));
        formPanel.add(Box.createVerticalStrut(20));

        JButton updateButton = createStyledButton("Update Access");
        updateButton.setMaximumSize(new Dimension(150, 40));
        updateButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        updateButton.addActionListener(e -> {
            // Update table values
            table.setValueAt(roleCombo.getSelectedItem(), selectedRow, 2);
            table.setValueAt(departmentCombo.getSelectedItem(), selectedRow, 3);
            table.setValueAt(accessLevelCombo.getSelectedItem(), selectedRow, 4);
            table.setValueAt(statusCombo.getSelectedItem(), selectedRow, 5);

            JOptionPane.showMessageDialog(dialog, "User access updated successfully!");
            dialog.dispose();
        });

        formPanel.add(updateButton);
        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.setLocationRelativeTo(parent);
        dialog.setVisible(true);
    }

    public static void showDeactivateUserDialog(Window parent, JTable table) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(parent, "Please select a user to deactivate.",
                "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String userName = table.getValueAt(selectedRow, 1).toString();
        String currentStatus = table.getValueAt(selectedRow, 5).toString();

        if ("Inactive".equals(currentStatus)) {
            int result = JOptionPane.showConfirmDialog(parent,
                "User '" + userName + "' is already inactive. Do you want to reactivate them?",
                "Reactivate User", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

            if (result == JOptionPane.YES_OPTION) {
                table.setValueAt("Active", selectedRow, 5);
                JOptionPane.showMessageDialog(parent, "User reactivated successfully!");
            }
        } else {
            int result = JOptionPane.showConfirmDialog(parent,
                "Are you sure you want to deactivate user '" + userName + "'?",
                "Confirm Deactivation", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

            if (result == JOptionPane.YES_OPTION) {
                table.setValueAt("Inactive", selectedRow, 5);
                JOptionPane.showMessageDialog(parent, "User deactivated successfully!");
            }
        }
    }

    public static void showAddLaboratoryDialog(Window parent) {
        JDialog dialog = new JDialog((Frame) parent, "Add Laboratory", true);
        dialog.setSize(450, 400);
        dialog.setLayout(new BorderLayout());
        styleDialog(dialog);

        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        formPanel.setBackground(Color.WHITE);

        JLabel titleLabel = new JLabel("Add New Laboratory");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(new Color(41, 128, 185));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        formPanel.add(titleLabel);
        formPanel.add(Box.createVerticalStrut(20));

        JTextField labIdField = createStyledTextField();
        JTextField nameField = createStyledTextField();
        JTextField capacityField = createStyledTextField();
        JTextField equipmentField = createStyledTextField();
        JComboBox<String> statusCombo = createStyledComboBox(new String[]{"Active", "Maintenance", "Inactive", "Under Construction"});

        formPanel.add(createFieldPanel("Lab ID:", labIdField));
        formPanel.add(Box.createVerticalStrut(15));
        formPanel.add(createFieldPanel("Name:", nameField));
        formPanel.add(Box.createVerticalStrut(15));
        formPanel.add(createFieldPanel("Capacity:", capacityField));
        formPanel.add(Box.createVerticalStrut(15));
        formPanel.add(createFieldPanel("Equipment:", equipmentField));
        formPanel.add(Box.createVerticalStrut(15));
        formPanel.add(createFieldPanel("Status:", statusCombo));
        formPanel.add(Box.createVerticalStrut(20));

        JButton addButton = createStyledButton("Add Laboratory");
        addButton.setMaximumSize(new Dimension(150, 40));
        addButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        addButton.addActionListener(e -> {
            try {
                // Enhanced validation
                InputValidator.validateLabId(labIdField.getText());
                InputValidator.validateAlphanumeric(nameField.getText(), "Name");
                InputValidator.validateStringLength(nameField.getText(), "Name", 3, 50);
                InputValidator.validatePositiveInteger(capacityField.getText(), "Capacity");
                InputValidator.validateNumericRange(capacityField.getText(), "Capacity", 1, 100);
                InputValidator.validateStringLength(equipmentField.getText(), "Equipment", 5, 200);
                InputValidator.validateStatus(statusCombo.getSelectedItem().toString());

                JOptionPane.showMessageDialog(dialog, "Laboratory added successfully!");

                // Auto-refresh the lab management table
                TableRefreshManager.getInstance().refreshRelatedTables("lab");

                dialog.dispose();
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(dialog, "Error: " + ex.getMessage(),
                    "Validation Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        formPanel.add(addButton);
        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.setLocationRelativeTo(parent);
        dialog.setVisible(true);
    }

    public static void showEditLaboratoryDialog(Window parent, JTable table) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(parent, "Please select a laboratory to edit.",
                "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JDialog dialog = new JDialog((Frame) parent, "Edit Laboratory", true);
        dialog.setSize(450, 400);
        dialog.setLayout(new BorderLayout());
        styleDialog(dialog);

        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        formPanel.setBackground(Color.WHITE);

        JLabel titleLabel = new JLabel("Edit Laboratory");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(new Color(41, 128, 185));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        formPanel.add(titleLabel);
        formPanel.add(Box.createVerticalStrut(20));

        // Pre-fill with current values
        JTextField labIdField = createStyledTextField();
        labIdField.setText(table.getValueAt(selectedRow, 0).toString());
        JTextField nameField = createStyledTextField();
        nameField.setText(table.getValueAt(selectedRow, 1).toString());
        JTextField capacityField = createStyledTextField();
        capacityField.setText(table.getValueAt(selectedRow, 2).toString());
        JTextField equipmentField = createStyledTextField();
        equipmentField.setText(table.getValueAt(selectedRow, 3).toString());
        JComboBox<String> statusCombo = createStyledComboBox(new String[]{"Active", "Maintenance", "Inactive", "Under Construction"});
        statusCombo.setSelectedItem(table.getValueAt(selectedRow, 4).toString());

        formPanel.add(createFieldPanel("Lab ID:", labIdField));
        formPanel.add(Box.createVerticalStrut(15));
        formPanel.add(createFieldPanel("Name:", nameField));
        formPanel.add(Box.createVerticalStrut(15));
        formPanel.add(createFieldPanel("Capacity:", capacityField));
        formPanel.add(Box.createVerticalStrut(15));
        formPanel.add(createFieldPanel("Equipment:", equipmentField));
        formPanel.add(Box.createVerticalStrut(15));
        formPanel.add(createFieldPanel("Status:", statusCombo));
        formPanel.add(Box.createVerticalStrut(20));

        JButton updateButton = createStyledButton("Update Laboratory");
        updateButton.setMaximumSize(new Dimension(150, 40));
        updateButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        updateButton.addActionListener(e -> {
            // Update table values
            table.setValueAt(labIdField.getText(), selectedRow, 0);
            table.setValueAt(nameField.getText(), selectedRow, 1);
            table.setValueAt(capacityField.getText(), selectedRow, 2);
            table.setValueAt(equipmentField.getText(), selectedRow, 3);
            table.setValueAt(statusCombo.getSelectedItem(), selectedRow, 4);

            JOptionPane.showMessageDialog(dialog, "Laboratory updated successfully!");
            dialog.dispose();
        });

        formPanel.add(updateButton);
        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.setLocationRelativeTo(parent);
        dialog.setVisible(true);
    }

    public static void showScheduleMaintenanceDialog(Window parent, JTable table) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(parent, "Please select a laboratory to schedule maintenance.",
                "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JDialog dialog = new JDialog((Frame) parent, "Schedule Maintenance", true);
        dialog.setSize(450, 350);
        dialog.setLayout(new BorderLayout());
        styleDialog(dialog);

        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        formPanel.setBackground(Color.WHITE);

        JLabel titleLabel = new JLabel("Schedule Maintenance");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(new Color(41, 128, 185));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        formPanel.add(titleLabel);
        formPanel.add(Box.createVerticalStrut(20));

        String labName = table.getValueAt(selectedRow, 1).toString();
        JLabel labLabel = new JLabel("Laboratory: " + labName);
        labLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        formPanel.add(labLabel);
        formPanel.add(Box.createVerticalStrut(15));

        JTextField dateField = createStyledTextField();
        dateField.setText("YYYY-MM-DD");
        JComboBox<String> timeCombo = createStyledComboBox(new String[]{"08:00", "10:00", "12:00", "14:00", "16:00", "18:00"});
        JComboBox<String> typeCombo = createStyledComboBox(new String[]{"Routine", "Emergency", "Upgrade", "Repair", "Cleaning"});
        JTextArea descriptionArea = createStyledTextArea();
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        JScrollPane descScroll = new JScrollPane(descriptionArea);
        descScroll.setPreferredSize(new Dimension(400, 60));

        formPanel.add(createFieldPanel("Date:", dateField));
        formPanel.add(Box.createVerticalStrut(15));
        formPanel.add(createFieldPanel("Time:", timeCombo));
        formPanel.add(Box.createVerticalStrut(15));
        formPanel.add(createFieldPanel("Type:", typeCombo));
        formPanel.add(Box.createVerticalStrut(15));

        JPanel descPanel = new JPanel(new BorderLayout(10, 5));
        descPanel.setBackground(Color.WHITE);
        descPanel.add(new JLabel("Description:"), BorderLayout.WEST);
        descPanel.add(descScroll, BorderLayout.CENTER);
        formPanel.add(descPanel);
        formPanel.add(Box.createVerticalStrut(20));

        JButton scheduleButton = createStyledButton("Schedule Maintenance");
        scheduleButton.setMaximumSize(new Dimension(180, 40));
        scheduleButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        scheduleButton.addActionListener(e -> {
            // Set lab status to maintenance
            table.setValueAt("Maintenance", selectedRow, 4);
            JOptionPane.showMessageDialog(dialog, "Maintenance scheduled successfully!");
            dialog.dispose();
        });

        formPanel.add(scheduleButton);
        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.setLocationRelativeTo(parent);
        dialog.setVisible(true);
    }

    public static void showAddSoftwareInventoryDialog(Window parent) {
        JDialog dialog = new JDialog((Frame) parent, "Add Software to Inventory", true);
        dialog.setSize(450, 450);
        dialog.setLayout(new BorderLayout());
        styleDialog(dialog);

        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        formPanel.setBackground(Color.WHITE);

        JLabel titleLabel = new JLabel("Add Software to Inventory");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(new Color(41, 128, 185));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        formPanel.add(titleLabel);
        formPanel.add(Box.createVerticalStrut(20));

        JTextField softwareNameField = createStyledTextField();
        JTextField versionField = createStyledTextField();
        JComboBox<String> licenseCombo = createStyledComboBox(new String[]{"Open Source", "Academic", "Professional", "Enterprise", "Subscription"});
        JTextField installationsField = createStyledTextField();
        JComboBox<String> statusCombo = createStyledComboBox(new String[]{"Active", "Inactive", "Expired", "Pending"});

        formPanel.add(createFieldPanel("Software Name:", softwareNameField));
        formPanel.add(Box.createVerticalStrut(15));
        formPanel.add(createFieldPanel("Version:", versionField));
        formPanel.add(Box.createVerticalStrut(15));
        formPanel.add(createFieldPanel("License Type:", licenseCombo));
        formPanel.add(Box.createVerticalStrut(15));
        formPanel.add(createFieldPanel("Installations:", installationsField));
        formPanel.add(Box.createVerticalStrut(15));
        formPanel.add(createFieldPanel("Status:", statusCombo));
        formPanel.add(Box.createVerticalStrut(20));

        JButton addButton = createStyledButton("Add Software");
        addButton.setMaximumSize(new Dimension(150, 40));
        addButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        addButton.addActionListener(e -> {
            try {
                // Enhanced validation
                InputValidator.validateAlphanumeric(softwareNameField.getText(), "Software Name");
                InputValidator.validateStringLength(softwareNameField.getText(), "Software Name", 2, 50);
                InputValidator.validateVersion(versionField.getText());
                InputValidator.validateLicenseType(licenseCombo.getSelectedItem().toString());
                InputValidator.validatePositiveInteger(installationsField.getText(), "Installations");
                InputValidator.validateNumericRange(installationsField.getText(), "Installations", 1, 1000);
                InputValidator.validateStatus(statusCombo.getSelectedItem().toString());

                JOptionPane.showMessageDialog(dialog, "Software added to inventory successfully!");

                // Auto-refresh the software inventory table
                TableRefreshManager.getInstance().refreshRelatedTables("software");

                dialog.dispose();
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(dialog, "Error: " + ex.getMessage(),
                    "Validation Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        formPanel.add(addButton);
        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.setLocationRelativeTo(parent);
        dialog.setVisible(true);
    }

    public static void showEditSoftwareDialog(Window parent, JTable table) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(parent, "Please select a software to edit.",
                "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JDialog dialog = new JDialog((Frame) parent, "Edit Software", true);
        dialog.setSize(450, 450);
        dialog.setLayout(new BorderLayout());
        styleDialog(dialog);

        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        formPanel.setBackground(Color.WHITE);

        JLabel titleLabel = new JLabel("Edit Software");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(new Color(41, 128, 185));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        formPanel.add(titleLabel);
        formPanel.add(Box.createVerticalStrut(20));

        // Pre-fill with current values
        JTextField softwareNameField = createStyledTextField();
        softwareNameField.setText(table.getValueAt(selectedRow, 0).toString());
        JTextField versionField = createStyledTextField();
        versionField.setText(table.getValueAt(selectedRow, 1).toString());
        JComboBox<String> licenseCombo = createStyledComboBox(new String[]{"Open Source", "Academic", "Professional", "Enterprise", "Subscription"});
        licenseCombo.setSelectedItem(table.getValueAt(selectedRow, 2).toString());
        JTextField installationsField = createStyledTextField();
        installationsField.setText(table.getValueAt(selectedRow, 3).toString());
        JComboBox<String> statusCombo = createStyledComboBox(new String[]{"Active", "Inactive", "Expired", "Pending"});
        statusCombo.setSelectedItem(table.getValueAt(selectedRow, 4).toString());

        formPanel.add(createFieldPanel("Software Name:", softwareNameField));
        formPanel.add(Box.createVerticalStrut(15));
        formPanel.add(createFieldPanel("Version:", versionField));
        formPanel.add(Box.createVerticalStrut(15));
        formPanel.add(createFieldPanel("License Type:", licenseCombo));
        formPanel.add(Box.createVerticalStrut(15));
        formPanel.add(createFieldPanel("Installations:", installationsField));
        formPanel.add(Box.createVerticalStrut(15));
        formPanel.add(createFieldPanel("Status:", statusCombo));
        formPanel.add(Box.createVerticalStrut(20));

        JButton updateButton = createStyledButton("Update Software");
        updateButton.setMaximumSize(new Dimension(150, 40));
        updateButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        updateButton.addActionListener(e -> {
            // Update table values
            table.setValueAt(softwareNameField.getText(), selectedRow, 0);
            table.setValueAt(versionField.getText(), selectedRow, 1);
            table.setValueAt(licenseCombo.getSelectedItem(), selectedRow, 2);
            table.setValueAt(installationsField.getText(), selectedRow, 3);
            table.setValueAt(statusCombo.getSelectedItem(), selectedRow, 4);

            JOptionPane.showMessageDialog(dialog, "Software updated successfully!");
            dialog.dispose();
        });

        formPanel.add(updateButton);
        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.setLocationRelativeTo(parent);
        dialog.setVisible(true);
    }

    public static void showDeleteSoftwareDialog(Window parent, JTable table) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(parent, "Please select a software to delete.",
                "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String softwareName = table.getValueAt(selectedRow, 0).toString();
        int result = JOptionPane.showConfirmDialog(parent,
            "Are you sure you want to delete '" + softwareName + "' from the inventory?",
            "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (result == JOptionPane.YES_OPTION) {
            DefaultTableModel model = (DefaultTableModel) table.getModel();
            model.removeRow(selectedRow);
            JOptionPane.showMessageDialog(parent, "Software deleted successfully!");
        }
    }

    public static void showChangeStatusDialog(Window parent, JTable table) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(parent, "Please select a complaint to change status.",
                "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JDialog dialog = new JDialog((Frame) parent, "Change Status", true);
        dialog.setSize(400, 300);
        dialog.setLayout(new BorderLayout());
        styleDialog(dialog);

        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        formPanel.setBackground(Color.WHITE);

        JLabel titleLabel = new JLabel("Change Complaint Status");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(new Color(41, 128, 185));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        formPanel.add(titleLabel);
        formPanel.add(Box.createVerticalStrut(20));

        String complaintId = table.getValueAt(selectedRow, 0).toString();
        String currentStatus = table.getValueAt(selectedRow, 3).toString();

        JLabel idLabel = new JLabel("Complaint ID: " + complaintId);
        idLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        formPanel.add(idLabel);
        formPanel.add(Box.createVerticalStrut(10));

        JLabel currentLabel = new JLabel("Current Status: " + currentStatus);
        formPanel.add(currentLabel);
        formPanel.add(Box.createVerticalStrut(15));

        JComboBox<String> statusCombo = createStyledComboBox(new String[]{"Open", "In Progress", "Resolved", "Closed", "Cancelled"});
        statusCombo.setSelectedItem(currentStatus);
        formPanel.add(createFieldPanel("New Status:", statusCombo));
        formPanel.add(Box.createVerticalStrut(15));

        JTextArea reasonArea = createStyledTextArea();
        reasonArea.setLineWrap(true);
        reasonArea.setWrapStyleWord(true);
        JScrollPane reasonScroll = new JScrollPane(reasonArea);
        reasonScroll.setPreferredSize(new Dimension(350, 60));

        JPanel reasonPanel = new JPanel(new BorderLayout(10, 5));
        reasonPanel.setBackground(Color.WHITE);
        reasonPanel.add(new JLabel("Reason:"), BorderLayout.WEST);
        reasonPanel.add(reasonScroll, BorderLayout.CENTER);
        formPanel.add(reasonPanel);
        formPanel.add(Box.createVerticalStrut(20));

        JButton updateButton = createStyledButton("Update Status");
        updateButton.setMaximumSize(new Dimension(150, 40));
        updateButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        updateButton.addActionListener(e -> {
            String newStatus = statusCombo.getSelectedItem().toString();
            table.setValueAt(newStatus, selectedRow, 3);
            JOptionPane.showMessageDialog(dialog, "Status updated successfully!");
            dialog.dispose();
        });

        formPanel.add(updateButton);
        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.setLocationRelativeTo(parent);
        dialog.setVisible(true);
    }

    public static void showAddSoftwareDialog(Window parent) {
        JDialog dialog = new JDialog((Frame) parent, "Add Software", true);
        dialog.setSize(450, 500);
        dialog.setLayout(new BorderLayout());
        styleDialog(dialog);

        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        formPanel.setBackground(Color.WHITE);

        JLabel titleLabel = new JLabel("Add New Software");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(new Color(41, 128, 185));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        formPanel.add(titleLabel);
        formPanel.add(Box.createVerticalStrut(20));

        JTextField computerIdField = createStyledTextField();
        JTextField softwareNameField = createStyledTextField();
        JTextField versionField = createStyledTextField();
        JComboBox<String> statusCombo = createStyledComboBox(new String[]{"Pending", "In Progress", "Completed", "Approved", "Rejected"});
        JTextField requestDateField = createStyledTextField();
        requestDateField.setText("YYYY-MM-DD");
        JTextArea notesArea = createStyledTextArea();
        notesArea.setLineWrap(true);
        notesArea.setWrapStyleWord(true);
        JScrollPane notesScroll = new JScrollPane(notesArea);
        notesScroll.setPreferredSize(new Dimension(400, 80));

        formPanel.add(createFieldPanel("Computer ID:", computerIdField));
        formPanel.add(Box.createVerticalStrut(15));
        formPanel.add(createFieldPanel("Software Name:", softwareNameField));
        formPanel.add(Box.createVerticalStrut(15));
        formPanel.add(createFieldPanel("Version:", versionField));
        formPanel.add(Box.createVerticalStrut(15));
        formPanel.add(createFieldPanel("Status:", statusCombo));
        formPanel.add(Box.createVerticalStrut(15));
        formPanel.add(createFieldPanel("Request Date:", requestDateField));
        formPanel.add(Box.createVerticalStrut(15));

        JPanel notesPanel = new JPanel(new BorderLayout(10, 5));
        notesPanel.setBackground(Color.WHITE);
        notesPanel.add(new JLabel("Notes:"), BorderLayout.WEST);
        notesPanel.add(notesScroll, BorderLayout.CENTER);
        formPanel.add(notesPanel);
        formPanel.add(Box.createVerticalStrut(20));

        JButton submitButton = createStyledButton("Add Software");
        submitButton.setMaximumSize(new Dimension(200, 40));
        submitButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        submitButton.addActionListener(e -> {
            try {
                // Enhanced validation
                InputValidator.validateComputerId(computerIdField.getText());
                InputValidator.validateAlphanumeric(softwareNameField.getText(), "Software Name");
                InputValidator.validateStringLength(softwareNameField.getText(), "Software Name", 2, 50);
                InputValidator.validateVersion(versionField.getText());
                InputValidator.validateSoftwareStatus(statusCombo.getSelectedItem().toString());
                InputValidator.validatePastOrPresentDate(requestDateField.getText());
                if (notesArea.getText() != null && !notesArea.getText().trim().isEmpty()) {
                    InputValidator.validateStringLength(notesArea.getText(), "Notes", 0, 500);
                }

                // Create and save software request to database
                java.util.Date requestDate = java.sql.Date.valueOf(requestDateField.getText());
                SoftwareRequest newRequest = new SoftwareRequest(
                    computerIdField.getText(),
                    softwareNameField.getText(),
                    versionField.getText(),
                    "Medium", // Default urgency
                    notesArea.getText() != null ? notesArea.getText() : "",
                    requestDate,
                    statusCombo.getSelectedItem().toString(),
                    "System User" // Default requester
                );

                // Save to database through service
                limsService.getSoftwareRequestService().addRequest(newRequest);

                // Handle successful submission with automatic refresh
                DataRefreshUtil.showSuccessWithRefresh(
                    formPanel,
                    "Software request added successfully!",
                    "software"
                );

                dialog.dispose();
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(dialog, "Error: " + ex.getMessage(),
                    "Validation Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        formPanel.add(submitButton);
        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.setLocationRelativeTo(parent);
        dialog.setVisible(true);
    }

    public static void showAddComputerDialog(Window parent) {
        JDialog dialog = new JDialog((Frame) parent, "Add New Computer", true);
        dialog.setSize(500, 650);
        dialog.setLayout(new BorderLayout());
        styleDialog(dialog);

        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        formPanel.setBackground(Color.WHITE);

        JLabel titleLabel = new JLabel("Add New Computer");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(new Color(41, 128, 185));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        formPanel.add(titleLabel);
        formPanel.add(Box.createVerticalStrut(20));

        JTextField computerIdField = createStyledTextField();
        JComboBox<String> labCombo = createStyledComboBox(new String[]{"CS Lab 1", "CS Lab 2", "CS Lab 3", "SE Lab 1", "SE Lab 2", "AI Lab", "Data Lab"});
        JTextField computerNameField = createStyledTextField();
        JTextField ipAddressField = createStyledTextField();
        JTextField specificationsField = createStyledTextField();
        JComboBox<String> statusCombo = createStyledComboBox(new String[]{"Available", "In Use", "Maintenance", "Retired"});
        JTextField installDateField = createStyledTextField();
        installDateField.setText("YYYY-MM-DD");
        JTextArea notesArea = createStyledTextArea();
        notesArea.setLineWrap(true);
        notesArea.setWrapStyleWord(true);
        JScrollPane notesScroll = new JScrollPane(notesArea);
        notesScroll.setPreferredSize(new Dimension(450, 80));

        formPanel.add(createFieldPanel("Computer ID:", computerIdField));
        formPanel.add(Box.createVerticalStrut(15));
        formPanel.add(createFieldPanel("Laboratory:", labCombo));
        formPanel.add(Box.createVerticalStrut(15));
        formPanel.add(createFieldPanel("Computer Name:", computerNameField));
        formPanel.add(Box.createVerticalStrut(15));
        formPanel.add(createFieldPanel("IP Address:", ipAddressField));
        formPanel.add(Box.createVerticalStrut(15));
        formPanel.add(createFieldPanel("Specifications:", specificationsField));
        formPanel.add(Box.createVerticalStrut(15));
        formPanel.add(createFieldPanel("Status:", statusCombo));
        formPanel.add(Box.createVerticalStrut(15));
        formPanel.add(createFieldPanel("Install Date:", installDateField));
        formPanel.add(Box.createVerticalStrut(15));

        JPanel notesPanel = new JPanel(new BorderLayout(10, 5));
        notesPanel.setBackground(Color.WHITE);
        notesPanel.add(new JLabel("Notes:"), BorderLayout.WEST);
        notesPanel.add(notesScroll, BorderLayout.CENTER);
        formPanel.add(notesPanel);
        formPanel.add(Box.createVerticalStrut(20));

        JButton submitButton = createStyledButton("Add Computer");
        submitButton.setMaximumSize(new Dimension(200, 40));
        submitButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        submitButton.addActionListener(e -> {
            try {
                // Enhanced validation
                InputValidator.validateComputerId(computerIdField.getText());
                InputValidator.validateNotEmpty(labCombo.getSelectedItem().toString(), "Laboratory");
                InputValidator.validateAlphanumeric(computerNameField.getText(), "Computer Name");
                InputValidator.validateStringLength(computerNameField.getText(), "Computer Name", 2, 50);
                InputValidator.validateIpAddress(ipAddressField.getText());
                InputValidator.validateStringLength(specificationsField.getText(), "Specifications", 5, 200);
                InputValidator.validateComputerStatus(statusCombo.getSelectedItem().toString());
                InputValidator.validatePastOrPresentDate(installDateField.getText());

                // Add computer to service
                Computer newComputer = new Computer(
                    computerIdField.getText(),
                    labCombo.getSelectedItem().toString(),
                    computerNameField.getText(),
                    ipAddressField.getText(),
                    specificationsField.getText(),
                    statusCombo.getSelectedItem().toString(),
                    installDateField.getText(),
                    ""
                );
                if (notesArea.getText() != null && !notesArea.getText().trim().isEmpty()) {
                    InputValidator.validateStringLength(notesArea.getText(), "Notes", 0, 500);
                    newComputer.setNotes(notesArea.getText());
                }
                limsService.getComputerService().addComputer(newComputer);

                // Handle successful submission with automatic refresh
                DataRefreshUtil.showSuccessWithRefresh(
                    formPanel,
                    "Computer added successfully!",
                    "computer"
                );

                dialog.dispose();
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(dialog, "Error: " + ex.getMessage(),
                    "Validation Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        formPanel.add(submitButton);
        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.setLocationRelativeTo(parent);
        dialog.setVisible(true);
    }

    public static void showLabReservationDialog(Window parent) {
        JDialog dialog = new JDialog((Frame) parent, "New Lab Reservation", true);
        dialog.setSize(500, 600);
        dialog.setLayout(new BorderLayout());
        styleDialog(dialog);

        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        formPanel.setBackground(Color.WHITE);

        JLabel titleLabel = new JLabel("New Lab Reservation");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(new Color(41, 128, 185));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        formPanel.add(titleLabel);
        formPanel.add(Box.createVerticalStrut(20));

        JTextField nameField = createStyledTextField();
        JComboBox<String> labCombo = createStyledComboBox(new String[]{"Lab A", "Lab B", "Lab C", "Lab D", "AI Lab", "Data Lab"});
        JTextField dateField = createStyledTextField();
        dateField.setText("YYYY-MM-DD");
        JComboBox<String> timeSlotCombo = createStyledComboBox(new String[]{"08:00-10:00", "10:00-12:00", "12:00-14:00", "14:00-16:00", "16:00-18:00"});
        JComboBox<String> purposeCombo = createStyledComboBox(new String[]{"Class", "Research", "Workshop", "Training", "Meeting"});
        JTextArea descriptionArea = createStyledTextArea();
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        JScrollPane descScroll = new JScrollPane(descriptionArea);
        descScroll.setPreferredSize(new Dimension(450, 100));

        formPanel.add(createFieldPanel("Requester Name:", nameField));
        formPanel.add(Box.createVerticalStrut(15));
        formPanel.add(createFieldPanel("Laboratory:", labCombo));
        formPanel.add(Box.createVerticalStrut(15));
        formPanel.add(createFieldPanel("Date:", dateField));
        formPanel.add(Box.createVerticalStrut(15));
        formPanel.add(createFieldPanel("Time Slot:", timeSlotCombo));
        formPanel.add(Box.createVerticalStrut(15));
        formPanel.add(createFieldPanel("Purpose:", purposeCombo));
        formPanel.add(Box.createVerticalStrut(15));

        JPanel descPanel = new JPanel(new BorderLayout(10, 5));
        descPanel.setBackground(Color.WHITE);
        descPanel.add(new JLabel("Description:"), BorderLayout.WEST);
        descPanel.add(descScroll, BorderLayout.CENTER);
        formPanel.add(descPanel);
        formPanel.add(Box.createVerticalStrut(20));

        JButton submitButton = createStyledButton("Submit Reservation");
        submitButton.setMaximumSize(new Dimension(200, 40));
        submitButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        submitButton.addActionListener(e -> {
            try {
                // Enhanced validation
                InputValidator.validateName(nameField.getText());
                InputValidator.validateStringLength(nameField.getText(), "Name", 2, 50);
                InputValidator.validateNotEmpty(labCombo.getSelectedItem().toString(), "Laboratory");
                InputValidator.validateFutureDate(dateField.getText());
                InputValidator.validateNotEmpty(timeSlotCombo.getSelectedItem().toString(), "Time Slot");
                InputValidator.validatePurpose(purposeCombo.getSelectedItem().toString());
                InputValidator.validateStringLength(descriptionArea.getText(), "Description", 5, 500);

                // Create and save lab reservation to database
                java.util.Date reservationDate = java.sql.Date.valueOf(dateField.getText());
                LabReservation newReservation = new LabReservation(
                    labCombo.getSelectedItem().toString(),
                    reservationDate,
                    timeSlotCombo.getSelectedItem().toString(),
                    nameField.getText(),
                    purposeCombo.getSelectedItem().toString(),
                    "General Course", // Default course
                    nameField.getText() // Use requester as instructor for now
                );

                // Save to database through service
                limsService.getLabReservationService().addReservation(newReservation);

                // Handle successful submission with automatic refresh
                DataRefreshUtil.showSuccessWithRefresh(
                    formPanel,
                    "Lab reservation submitted successfully!",
                    "reservation"
                );

                dialog.dispose();
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(dialog, "Error: " + ex.getMessage(),
                    "Validation Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        formPanel.add(submitButton);
        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.setLocationRelativeTo(parent);
        dialog.setVisible(true);
    }

    public static JPanel getInventoryReportPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(Color.WHITE);

        JLabel titleLabel = new JLabel("Inventory Reports");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(new Color(41, 128, 185));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // Use database-connected table model for computer inventory
        DatabaseTableModel tableModel = DatabaseTableModel.getComputerTableModel();
        JTable table = new JTable(tableModel);

        // Register table for automatic refresh
        TableRefreshManager.getInstance().registerTable("computers", table, tableModel);

        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setRowHeight(30);
        table.setAutoCreateRowSorter(true);
        JScrollPane scrollPane = new JScrollPane(table);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Force refresh data after UI is created to ensure visibility
        SwingUtilities.invokeLater(() -> {
            System.out.println("🔄 Force refreshing inventory report data for UI display...");
            tableModel.forceRefresh();
        });

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Color.WHITE);

        JButton refreshButton = createStyledButton("Refresh");
        refreshButton.addActionListener(e -> {
            tableModel.refreshData();
            JOptionPane.showMessageDialog(mainPanel, "Inventory data refreshed successfully!",
                "Success", JOptionPane.INFORMATION_MESSAGE);
        });

        JButton exportButton = createStyledButton("Export to PDF");
        JButton printButton = createStyledButton("Print Report");

        exportButton.addActionListener(e -> JOptionPane.showMessageDialog(mainPanel,
            "Export functionality - would export " + tableModel.getRowCount() + " inventory items to PDF"));
        printButton.addActionListener(e -> JOptionPane.showMessageDialog(mainPanel,
            "Print functionality - would print " + tableModel.getRowCount() + " inventory items"));

        buttonPanel.add(refreshButton);
        buttonPanel.add(exportButton);
        buttonPanel.add(printButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        return mainPanel;
    }

    /**
     * Show approve request dialog
     */
    public static void showApproveRequestDialog(Window parent, String requestId, String computerId, String softwareName) {
        JDialog dialog = new JDialog((Frame)parent, "Approve Software Request", true);
        dialog.setSize(400, 300);
        dialog.setLayout(new BorderLayout());
        styleDialog(dialog);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(Color.WHITE);

        JLabel titleLabel = new JLabel("Approve Software Request");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(new Color(41, 128, 185));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(titleLabel);
        mainPanel.add(Box.createVerticalStrut(20));

        JLabel infoLabel = new JLabel("<html><center>Request ID: " + requestId + "<br>" +
                                     "Computer ID: " + computerId + "<br>" +
                                     "Software: " + softwareName + "</center></html>");
        infoLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        infoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(infoLabel);
        mainPanel.add(Box.createVerticalStrut(20));

        JTextField approverField = new JTextField("Admin User");
        approverField.setMaximumSize(new Dimension(300, 30));

        mainPanel.add(createFieldPanel("Approved By:", approverField));
        mainPanel.add(Box.createVerticalStrut(20));

        JButton approveButton = createStyledButton("Approve Request");
        approveButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        approveButton.addActionListener(e -> {
            try {
                String approvedBy = approverField.getText().trim();
                if (approvedBy.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Please enter approver name.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Use current date for approval
                softwareRequestController.approveRequest(computerId, new Date(), approvedBy);

                // Handle successful approval with automatic refresh
                DataRefreshUtil.showSuccessWithRefresh(
                    mainPanel,
                    "Software request approved successfully!",
                    "software"
                );

                dialog.dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Error approving request: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        mainPanel.add(approveButton);

        dialog.add(mainPanel, BorderLayout.CENTER);
        dialog.setLocationRelativeTo(parent);
        dialog.setVisible(true);
    }

    /**
     * Show reject request dialog
     */
    public static void showRejectRequestDialog(Window parent, String requestId, String computerId, String softwareName) {
        JDialog dialog = new JDialog((Frame)parent, "Reject Software Request", true);
        dialog.setSize(400, 250);
        dialog.setLayout(new BorderLayout());
        styleDialog(dialog);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(Color.WHITE);

        JLabel titleLabel = new JLabel("Reject Software Request");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(new Color(231, 76, 60));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(titleLabel);
        mainPanel.add(Box.createVerticalStrut(20));

        JLabel infoLabel = new JLabel("<html><center>Request ID: " + requestId + "<br>" +
                                     "Computer ID: " + computerId + "<br>" +
                                     "Software: " + softwareName + "</center></html>");
        infoLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        infoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(infoLabel);
        mainPanel.add(Box.createVerticalStrut(20));

        JLabel confirmLabel = new JLabel("Are you sure you want to reject this request?");
        confirmLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        confirmLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(confirmLabel);
        mainPanel.add(Box.createVerticalStrut(20));

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(Color.WHITE);

        JButton rejectButton = createStyledButton("Reject Request");
        rejectButton.setBackground(new Color(231, 76, 60));
        rejectButton.addActionListener(e -> {
            try {
                // Use current date for rejection
                softwareRequestController.rejectRequest(computerId, new Date());

                // Handle successful rejection with automatic refresh
                DataRefreshUtil.showSuccessWithRefresh(
                    mainPanel,
                    "Software request rejected successfully!",
                    "software"
                );

                dialog.dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Error rejecting request: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        JButton cancelButton = createStyledButton("Cancel");
        cancelButton.setBackground(new Color(149, 165, 166));
        cancelButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(rejectButton);
        buttonPanel.add(cancelButton);
        mainPanel.add(buttonPanel);

        dialog.add(mainPanel, BorderLayout.CENTER);
        dialog.setLocationRelativeTo(parent);
        dialog.setVisible(true);
    }

    public static void showEditComputerDialog(Window parent, JTable table) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(parent, "Please select a computer to edit.",
                "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JDialog dialog = new JDialog((Frame) parent, "Edit Computer", true);
        dialog.setSize(500, 650);
        dialog.setLayout(new BorderLayout());
        styleDialog(dialog);

        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        formPanel.setBackground(Color.WHITE);

        JLabel titleLabel = new JLabel("Edit Computer");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(new Color(41, 128, 185));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        formPanel.add(titleLabel);
        formPanel.add(Box.createVerticalStrut(20));

        // Pre-fill with current values
        JTextField computerIdField = createStyledTextField();
        computerIdField.setText(table.getValueAt(selectedRow, 0).toString());
        computerIdField.setEditable(false); // ID should not be editable

        JComboBox<String> labCombo = DatabaseDropdownPopulator.createLabCombo();
        labCombo.setSelectedItem(table.getValueAt(selectedRow, 1).toString());

        JTextField computerNameField = createStyledTextField();
        computerNameField.setText(table.getValueAt(selectedRow, 2).toString());

        JTextField ipAddressField = createStyledTextField();
        ipAddressField.setText(table.getValueAt(selectedRow, 3).toString());

        JTextField specificationsField = createStyledTextField();
        specificationsField.setText(table.getValueAt(selectedRow, 4).toString());

        JComboBox<String> statusCombo = createStyledComboBox(new String[]{"Available", "In Use", "Maintenance", "Retired"});
        statusCombo.setSelectedItem(table.getValueAt(selectedRow, 5).toString());

        JTextField notesField = createStyledTextField();
        if (table.getColumnCount() > 7) {
            notesField.setText(table.getValueAt(selectedRow, 7).toString());
        }

        formPanel.add(createFieldPanel("Computer ID:", computerIdField));
        formPanel.add(Box.createVerticalStrut(15));
        formPanel.add(createFieldPanel("Laboratory:", labCombo));
        formPanel.add(Box.createVerticalStrut(15));
        formPanel.add(createFieldPanel("Computer Name:", computerNameField));
        formPanel.add(Box.createVerticalStrut(15));
        formPanel.add(createFieldPanel("IP Address:", ipAddressField));
        formPanel.add(Box.createVerticalStrut(15));
        formPanel.add(createFieldPanel("Specifications:", specificationsField));
        formPanel.add(Box.createVerticalStrut(15));
        formPanel.add(createFieldPanel("Status:", statusCombo));
        formPanel.add(Box.createVerticalStrut(15));
        formPanel.add(createFieldPanel("Notes:", notesField));
        formPanel.add(Box.createVerticalStrut(20));

        JButton updateButton = createStyledButton("Update Computer");
        updateButton.setMaximumSize(new Dimension(200, 40));
        updateButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        updateButton.addActionListener(e -> {
            try {
                // Validate input
                InputValidator.validateName(computerNameField.getText());
                InputValidator.validateIpAddress(ipAddressField.getText());
                InputValidator.validateStringLength(specificationsField.getText(), "Specifications", 5, 200);

                // Create updated computer object
                Computer updatedComputer = new Computer(
                    computerIdField.getText(),
                    labCombo.getSelectedItem().toString(),
                    computerNameField.getText(),
                    ipAddressField.getText(),
                    specificationsField.getText(),
                    statusCombo.getSelectedItem().toString(),
                    table.getValueAt(selectedRow, 6).toString(), // Keep original install date
                    notesField.getText()
                );

                // Update through service
                limsService.getComputerService().updateComputer(updatedComputer);

                // Handle successful submission with automatic refresh
                DataRefreshUtil.showSuccessWithRefresh(
                    formPanel,
                    "Computer updated successfully!",
                    "computer"
                );

                dialog.dispose();
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(dialog, "Error: " + ex.getMessage(),
                    "Validation Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        formPanel.add(updateButton);
        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.setLocationRelativeTo(parent);
        dialog.setVisible(true);
    }

    public static void showRecordMaintenanceDialog(Window parent, JTable table) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(parent, "Please select a computer to record maintenance.",
                "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JDialog dialog = new JDialog((Frame) parent, "Record Maintenance", true);
        dialog.setSize(450, 400);
        dialog.setLayout(new BorderLayout());
        styleDialog(dialog);

        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        formPanel.setBackground(Color.WHITE);

        JLabel titleLabel = new JLabel("Record Maintenance");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(new Color(41, 128, 185));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        formPanel.add(titleLabel);
        formPanel.add(Box.createVerticalStrut(20));

        String computerId = table.getValueAt(selectedRow, 0).toString();
        String computerName = table.getValueAt(selectedRow, 2).toString();

        JLabel computerLabel = new JLabel("Computer: " + computerId + " - " + computerName);
        computerLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        formPanel.add(computerLabel);
        formPanel.add(Box.createVerticalStrut(15));

        JTextField maintenanceDateField = createStyledTextField();
        maintenanceDateField.setText(new java.text.SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date()));

        JComboBox<String> maintenanceTypeCombo = createStyledComboBox(new String[]{
            "Routine Maintenance", "Hardware Repair", "Software Update",
            "Cleaning", "Component Replacement", "Performance Optimization"
        });

        JTextArea descriptionArea = new JTextArea(4, 30);
        descriptionArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        descriptionArea.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(8, 8, 8, 8)
        ));
        JScrollPane descriptionScroll = new JScrollPane(descriptionArea);

        JTextField technicianField = createStyledTextField();
        technicianField.setText(currentUsername);

        formPanel.add(createFieldPanel("Maintenance Date:", maintenanceDateField));
        formPanel.add(Box.createVerticalStrut(15));
        formPanel.add(createFieldPanel("Maintenance Type:", maintenanceTypeCombo));
        formPanel.add(Box.createVerticalStrut(15));
        formPanel.add(new JLabel("Description:"));
        formPanel.add(Box.createVerticalStrut(5));
        formPanel.add(descriptionScroll);
        formPanel.add(Box.createVerticalStrut(15));
        formPanel.add(createFieldPanel("Technician:", technicianField));
        formPanel.add(Box.createVerticalStrut(20));

        JButton recordButton = createStyledButton("Record Maintenance");
        recordButton.setMaximumSize(new Dimension(200, 40));
        recordButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        recordButton.addActionListener(e -> {
            try {
                // Validate input
                InputValidator.validateDate(maintenanceDateField.getText());
                InputValidator.validateStringLength(descriptionArea.getText(), "Description", 10, 500);
                InputValidator.validateName(technicianField.getText());

                // Update computer status to maintenance
                table.setValueAt("Maintenance", selectedRow, 5);

                JOptionPane.showMessageDialog(dialog,
                    "Maintenance recorded successfully!\nComputer status updated to 'Maintenance'.");

                // Auto-refresh the computer table
                TableRefreshManager.getInstance().refreshRelatedTables("computer");

                dialog.dispose();
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(dialog, "Error: " + ex.getMessage(),
                    "Validation Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        formPanel.add(recordButton);
        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.setLocationRelativeTo(parent);
        dialog.setVisible(true);
    }

    public static void showViewSoftwareDetailsDialog(Window parent, JTable table) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(parent, "Please select a software request to view details.",
                "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JDialog dialog = new JDialog((Frame) parent, "Software Request Details", true);
        dialog.setSize(500, 400);
        dialog.setLayout(new BorderLayout());
        styleDialog(dialog);

        JPanel detailsPanel = new JPanel();
        detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));
        detailsPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        detailsPanel.setBackground(Color.WHITE);

        JLabel titleLabel = new JLabel("Software Request Details");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(new Color(41, 128, 185));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        detailsPanel.add(titleLabel);
        detailsPanel.add(Box.createVerticalStrut(20));

        // Display request details
        String[] labels = {"Request ID:", "Computer ID:", "Software Name:", "Version:", "Status:", "Urgency:", "Requested By:", "Request Date:"};
        for (int i = 0; i < Math.min(labels.length, table.getColumnCount()); i++) {
            JPanel fieldPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            fieldPanel.setBackground(Color.WHITE);

            JLabel label = new JLabel(labels[i]);
            label.setFont(new Font("Segoe UI", Font.BOLD, 14));
            label.setPreferredSize(new Dimension(120, 25));

            JLabel value = new JLabel(table.getValueAt(selectedRow, i).toString());
            value.setFont(new Font("Segoe UI", Font.PLAIN, 14));

            fieldPanel.add(label);
            fieldPanel.add(value);
            detailsPanel.add(fieldPanel);
            detailsPanel.add(Box.createVerticalStrut(10));
        }

        JButton closeButton = createStyledButton("Close");
        closeButton.setMaximumSize(new Dimension(100, 40));
        closeButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        closeButton.addActionListener(e -> dialog.dispose());

        detailsPanel.add(Box.createVerticalStrut(20));
        detailsPanel.add(closeButton);

        dialog.add(detailsPanel, BorderLayout.CENTER);
        dialog.setLocationRelativeTo(parent);
        dialog.setVisible(true);
    }

    public static void showAssignComplaintDialog(Window parent, JTable table) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(parent, "Please select a complaint to assign.",
                "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JDialog dialog = new JDialog((Frame) parent, "Assign Complaint", true);
        dialog.setSize(400, 300);
        dialog.setLayout(new BorderLayout());
        styleDialog(dialog);

        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        formPanel.setBackground(Color.WHITE);

        JLabel titleLabel = new JLabel("Assign Complaint");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(new Color(41, 128, 185));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        formPanel.add(titleLabel);
        formPanel.add(Box.createVerticalStrut(20));

        String complaintId = table.getValueAt(selectedRow, 0).toString();
        String issueType = table.getValueAt(selectedRow, 3).toString();

        JLabel complaintLabel = new JLabel("Complaint: " + complaintId + " - " + issueType);
        complaintLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        formPanel.add(complaintLabel);
        formPanel.add(Box.createVerticalStrut(15));

        JComboBox<String> assigneeCombo = createStyledComboBox(new String[]{
            "John Smith", "Sarah Johnson", "Mike Wilson", "Lisa Brown", "David Lee"
        });

        JComboBox<String> priorityCombo = createStyledComboBox(new String[]{
            "Low", "Medium", "High", "Critical"
        });

        formPanel.add(createFieldPanel("Assign To:", assigneeCombo));
        formPanel.add(Box.createVerticalStrut(15));
        formPanel.add(createFieldPanel("Priority:", priorityCombo));
        formPanel.add(Box.createVerticalStrut(20));

        JButton assignButton = createStyledButton("Assign Complaint");
        assignButton.setMaximumSize(new Dimension(200, 40));
        assignButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        assignButton.addActionListener(e -> {
            // Update table with assignment
            table.setValueAt(assigneeCombo.getSelectedItem(), selectedRow, 7); // Assigned To column

            JOptionPane.showMessageDialog(dialog,
                "Complaint assigned to " + assigneeCombo.getSelectedItem() + " successfully!");

            // Auto-refresh the complaints table
            TableRefreshManager.getInstance().refreshRelatedTables("complaint");

            dialog.dispose();
        });

        formPanel.add(assignButton);
        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.setLocationRelativeTo(parent);
        dialog.setVisible(true);
    }

    public static void showViewComplaintDetailsDialog(Window parent, JTable table) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(parent, "Please select a complaint to view details.",
                "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JDialog dialog = new JDialog((Frame) parent, "Complaint Details", true);
        dialog.setSize(500, 400);
        dialog.setLayout(new BorderLayout());
        styleDialog(dialog);

        JPanel detailsPanel = new JPanel();
        detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));
        detailsPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        detailsPanel.setBackground(Color.WHITE);

        JLabel titleLabel = new JLabel("Complaint Details");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(new Color(41, 128, 185));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        detailsPanel.add(titleLabel);
        detailsPanel.add(Box.createVerticalStrut(20));

        // Display complaint details
        String[] labels = {"ID:", "Computer ID:", "Department:", "Issue Type:", "Description:", "Status:", "Urgency:", "Assigned To:", "Created Date:"};
        for (int i = 0; i < Math.min(labels.length, table.getColumnCount()); i++) {
            JPanel fieldPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            fieldPanel.setBackground(Color.WHITE);

            JLabel label = new JLabel(labels[i]);
            label.setFont(new Font("Segoe UI", Font.BOLD, 14));
            label.setPreferredSize(new Dimension(120, 25));

            JLabel value = new JLabel(table.getValueAt(selectedRow, i).toString());
            value.setFont(new Font("Segoe UI", Font.PLAIN, 14));

            fieldPanel.add(label);
            fieldPanel.add(value);
            detailsPanel.add(fieldPanel);
            detailsPanel.add(Box.createVerticalStrut(10));
        }

        JButton closeButton = createStyledButton("Close");
        closeButton.setMaximumSize(new Dimension(100, 40));
        closeButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        closeButton.addActionListener(e -> dialog.dispose());

        detailsPanel.add(Box.createVerticalStrut(20));
        detailsPanel.add(closeButton);

        dialog.add(detailsPanel, BorderLayout.CENTER);
        dialog.setLocationRelativeTo(parent);
        dialog.setVisible(true);
    }

    /**
     * Show user management dialog for admin
     */
    public static void showUserManagementDialog(Window parent) {
        JDialog dialog = new JDialog((Frame) parent, "User Management", true);
        dialog.setSize(800, 600);
        dialog.setLayout(new BorderLayout());
        styleDialog(dialog);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("User Management");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(new Color(41, 128, 185));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // Create user table
        String[] columnNames = {"User ID", "Name", "Role", "Department", "Access Level", "Status"};
        Object[][] data = getUserTableData();

        JTable userTable = new JTable(data, columnNames);
        userTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        userTable.setRowHeight(25);
        userTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        userTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPane = new JScrollPane(userTable);
        scrollPane.setPreferredSize(new Dimension(750, 400));
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(Color.WHITE);

        JButton addUserButton = createStyledButton("Add User");
        addUserButton.addActionListener(e -> showAddUserDialog(dialog, userTable));

        JButton editUserButton = createStyledButton("Edit User");
        editUserButton.addActionListener(e -> showEditUserDialog(dialog, userTable));

        JButton resetPasswordButton = createStyledButton("Reset Password");
        resetPasswordButton.addActionListener(e -> showResetPasswordDialog(dialog, userTable));

        JButton deactivateUserButton = createStyledButton("Deactivate User");
        deactivateUserButton.addActionListener(e -> deactivateSelectedUser(dialog, userTable));

        JButton refreshButton = createStyledButton("Refresh");
        refreshButton.addActionListener(e -> refreshUserTable(userTable));

        JButton closeButton = createStyledButton("Close");
        closeButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(addUserButton);
        buttonPanel.add(editUserButton);
        buttonPanel.add(resetPasswordButton);
        buttonPanel.add(deactivateUserButton);
        buttonPanel.add(refreshButton);
        buttonPanel.add(closeButton);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        dialog.add(mainPanel, BorderLayout.CENTER);
        dialog.setLocationRelativeTo(parent);
        dialog.setVisible(true);
    }

    /**
     * Get user table data from database
     */
    private static Object[][] getUserTableData() {
        try {
            service.UserService userService = new service.UserService();
            java.util.List<model.User> users = userService.getAllUsers();

            Object[][] data = new Object[users.size()][6];
            for (int i = 0; i < users.size(); i++) {
                model.User user = users.get(i);
                data[i][0] = user.getUserId();
                data[i][1] = user.getName();
                data[i][2] = user.getRole();
                data[i][3] = user.getDepartment();
                data[i][4] = user.getAccessLevel();
                data[i][5] = user.getStatus();
            }
            return data;
        } catch (Exception e) {
            System.err.println("Error loading user data: " + e.getMessage());
            return new Object[0][6];
        }
    }

    /**
     * Refresh user table data
     */
    private static void refreshUserTable(JTable table) {
        Object[][] newData = getUserTableData();
        String[] columnNames = {"User ID", "Name", "Role", "Department", "Access Level", "Status"};

        javax.swing.table.DefaultTableModel model = new javax.swing.table.DefaultTableModel(newData, columnNames);
        table.setModel(model);

        JOptionPane.showMessageDialog(table, "User table refreshed successfully!");
    }

    /**
     * Show add user dialog
     */
    private static void showAddUserDialog(Window parent, JTable table) {
        JDialog dialog = new JDialog((Frame) parent, "Add New User", true);
        dialog.setSize(450, 500);
        dialog.setLayout(new BorderLayout());
        styleDialog(dialog);

        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        formPanel.setBackground(Color.WHITE);

        JLabel titleLabel = new JLabel("Add New User");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(new Color(41, 128, 185));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        formPanel.add(titleLabel);
        formPanel.add(Box.createVerticalStrut(20));

        JTextField userIdField = createStyledTextField();
        JTextField nameField = createStyledTextField();
        JPasswordField passwordField = new JPasswordField();
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        passwordField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(8, 8, 8, 8)
        ));

        JComboBox<String> roleCombo = createStyledComboBox(new String[]{"Admin", "Teacher", "Student", "Lab Technician"});
        JTextField departmentField = createStyledTextField();
        JComboBox<String> accessLevelCombo = createStyledComboBox(new String[]{"Full", "Limited", "Basic", "Read Only"});

        formPanel.add(createFieldPanel("User ID:", userIdField));
        formPanel.add(Box.createVerticalStrut(15));
        formPanel.add(createFieldPanel("Full Name:", nameField));
        formPanel.add(Box.createVerticalStrut(15));
        formPanel.add(createFieldPanel("Password:", passwordField));
        formPanel.add(Box.createVerticalStrut(15));
        formPanel.add(createFieldPanel("Role:", roleCombo));
        formPanel.add(Box.createVerticalStrut(15));
        formPanel.add(createFieldPanel("Department:", departmentField));
        formPanel.add(Box.createVerticalStrut(15));
        formPanel.add(createFieldPanel("Access Level:", accessLevelCombo));
        formPanel.add(Box.createVerticalStrut(20));

        JButton createButton = createStyledButton("Create User");
        createButton.setMaximumSize(new Dimension(150, 40));
        createButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        createButton.addActionListener(e -> {
            try {
                service.UserService userService = new service.UserService();
                boolean success = userService.createUser(
                    userIdField.getText(),
                    nameField.getText(),
                    new String(passwordField.getPassword()),
                    roleCombo.getSelectedItem().toString(),
                    departmentField.getText(),
                    accessLevelCombo.getSelectedItem().toString()
                );

                if (success) {
                    JOptionPane.showMessageDialog(dialog, "User created successfully!");
                    refreshUserTable(table);
                    dialog.dispose();
                } else {
                    JOptionPane.showMessageDialog(dialog, "Failed to create user. User ID may already exist.",
                        "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Error creating user: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        formPanel.add(createButton);
        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.setLocationRelativeTo(parent);
        dialog.setVisible(true);
    }

    /**
     * Show edit user dialog
     */
    private static void showEditUserDialog(Window parent, JTable table) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(parent, "Please select a user to edit.",
                "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Implementation similar to showAddUserDialog but with pre-filled fields
        JOptionPane.showMessageDialog(parent, "Edit user functionality - Implementation in progress");
    }

    /**
     * Show reset password dialog
     */
    private static void showResetPasswordDialog(Window parent, JTable table) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(parent, "Please select a user to reset password.",
                "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String userId = table.getValueAt(selectedRow, 0).toString();
        String userName = table.getValueAt(selectedRow, 1).toString();

        JDialog dialog = new JDialog((Frame) parent, "Reset Password", true);
        dialog.setSize(400, 250);
        dialog.setLayout(new BorderLayout());
        styleDialog(dialog);

        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        formPanel.setBackground(Color.WHITE);

        JLabel titleLabel = new JLabel("Reset Password for " + userName);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLabel.setForeground(new Color(41, 128, 185));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        formPanel.add(titleLabel);
        formPanel.add(Box.createVerticalStrut(20));

        JPasswordField newPasswordField = new JPasswordField();
        newPasswordField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        newPasswordField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(8, 8, 8, 8)
        ));

        formPanel.add(createFieldPanel("New Password:", newPasswordField));
        formPanel.add(Box.createVerticalStrut(20));

        JButton resetButton = createStyledButton("Reset Password");
        resetButton.setMaximumSize(new Dimension(150, 40));
        resetButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        resetButton.addActionListener(e -> {
            try {
                service.UserService userService = new service.UserService();
                boolean success = userService.resetPassword(userId, new String(newPasswordField.getPassword()));

                if (success) {
                    JOptionPane.showMessageDialog(dialog, "Password reset successfully!");
                    dialog.dispose();
                } else {
                    JOptionPane.showMessageDialog(dialog, "Failed to reset password.",
                        "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Error resetting password: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        formPanel.add(resetButton);
        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.setLocationRelativeTo(parent);
        dialog.setVisible(true);
    }

    /**
     * Deactivate selected user
     */
    private static void deactivateSelectedUser(Window parent, JTable table) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(parent, "Please select a user to deactivate.",
                "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String userId = table.getValueAt(selectedRow, 0).toString();
        String userName = table.getValueAt(selectedRow, 1).toString();

        int confirm = JOptionPane.showConfirmDialog(parent,
            "Are you sure you want to deactivate user: " + userName + " (" + userId + ")?",
            "Confirm Deactivation", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                service.UserService userService = new service.UserService();
                boolean success = userService.deactivateUser(userId);

                if (success) {
                    JOptionPane.showMessageDialog(parent, "User deactivated successfully!");
                    refreshUserTable(table);
                } else {
                    JOptionPane.showMessageDialog(parent, "Failed to deactivate user.",
                        "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(parent, "Error deactivating user: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
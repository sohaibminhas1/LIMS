package ui;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Comprehensive JPanel Data Manager that ensures proper data initialization and flow
 */
public class JPanelDataManager {
    
    private static JPanelDataManager instance;
    private Map<String, JPanel> registeredPanels;
    private Map<String, DatabaseTableModel> panelModels;
    private Map<String, JTable> panelTables;
    
    private JPanelDataManager() {
        this.registeredPanels = new HashMap<>();
        this.panelModels = new HashMap<>();
        this.panelTables = new HashMap<>();
    }
    
    public static JPanelDataManager getInstance() {
        if (instance == null) {
            instance = new JPanelDataManager();
        }
        return instance;
    }
    
    /**
     * Register a panel with its associated data components
     */
    public void registerPanel(String panelKey, JPanel panel, DatabaseTableModel model, JTable table) {
        System.out.println("📋 Registering panel: " + panelKey);
        
        registeredPanels.put(panelKey, panel);
        panelModels.put(panelKey, model);
        panelTables.put(panelKey, table);
        
        // Also register with TableRefreshManager
        TableRefreshManager.getInstance().registerTable(panelKey, table, model);
        
        // Ensure data is loaded immediately
        ensurePanelDataLoaded(panelKey);
        
        System.out.println("✅ Panel registered: " + panelKey + " with " + model.getRowCount() + " rows");
    }
    
    /**
     * Ensure data is properly loaded for a panel
     */
    public void ensurePanelDataLoaded(String panelKey) {
        DatabaseTableModel model = panelModels.get(panelKey);
        JTable table = panelTables.get(panelKey);
        
        if (model != null && table != null) {
            System.out.println("🔄 Ensuring data loaded for panel: " + panelKey);
            
            SwingUtilities.invokeLater(() -> {
                try {
                    // Ensure model has data
                    model.ensureDataLoaded();
                    
                    // Force table to recognize the model
                    table.setModel(model);
                    
                    // Configure table for optimal display
                    configureTableForOptimalDisplay(table);
                    
                    // Update UI
                    table.revalidate();
                    table.repaint();
                    
                    System.out.println("✅ Data ensured for panel: " + panelKey + " (" + model.getRowCount() + " rows)");
                    
                } catch (Exception e) {
                    System.err.println("❌ Error ensuring data for panel " + panelKey + ": " + e.getMessage());
                    e.printStackTrace();
                }
            });
        }
    }
    
    /**
     * Configure table for optimal data display
     */
    private void configureTableForOptimalDisplay(JTable table) {
        // Set optimal display properties
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setRowHeight(25);
        table.setAutoCreateRowSorter(true);
        table.setFillsViewportHeight(true);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // Set column widths for better display
        if (table.getColumnModel().getColumnCount() > 0) {
            for (int i = 0; i < table.getColumnModel().getColumnCount(); i++) {
                table.getColumnModel().getColumn(i).setPreferredWidth(120);
            }
        }
        
        // Enable grid lines for better readability
        table.setShowGrid(true);
        table.setGridColor(new Color(230, 230, 230));
    }
    
    /**
     * Refresh all registered panels
     */
    public void refreshAllPanels() {
        System.out.println("🔄 Refreshing all registered panels...");
        
        for (String panelKey : registeredPanels.keySet()) {
            refreshPanel(panelKey);
        }
        
        System.out.println("✅ All panels refreshed");
    }
    
    /**
     * Refresh a specific panel
     */
    public void refreshPanel(String panelKey) {
        System.out.println("🔄 Refreshing panel: " + panelKey);
        
        DatabaseTableModel model = panelModels.get(panelKey);
        JTable table = panelTables.get(panelKey);
        JPanel panel = registeredPanels.get(panelKey);
        
        if (model != null && table != null && panel != null) {
            SwingUtilities.invokeLater(() -> {
                try {
                    // Force refresh the model
                    model.forceRefresh();
                    
                    // Re-configure table
                    table.setModel(model);
                    configureTableForOptimalDisplay(table);
                    
                    // Update UI
                    table.revalidate();
                    table.repaint();
                    panel.revalidate();
                    panel.repaint();
                    
                    System.out.println("✅ Panel refreshed: " + panelKey + " (" + model.getRowCount() + " rows)");
                    
                } catch (Exception e) {
                    System.err.println("❌ Error refreshing panel " + panelKey + ": " + e.getMessage());
                    e.printStackTrace();
                }
            });
        } else {
            System.out.println("⚠️ Panel components not found for: " + panelKey);
        }
    }
    
    /**
     * Create a properly configured data panel
     */
    public JPanel createDataPanel(String panelKey, String title, DatabaseTableModel model) {
        System.out.println("🏗️ Creating data panel: " + panelKey);
        
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(Color.WHITE);
        
        // Title
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(new Color(41, 128, 185));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        mainPanel.add(titleLabel, BorderLayout.NORTH);
        
        // Table
        JTable table = new JTable(model);
        configureTableForOptimalDisplay(table);
        
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(800, 400));
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        
        // Action panel
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actionPanel.setBackground(Color.WHITE);
        
        JButton refreshButton = createStyledButton("Refresh Data");
        refreshButton.addActionListener(e -> {
            refreshPanel(panelKey);
            JOptionPane.showMessageDialog(mainPanel, 
                "Data refreshed successfully!\nRows: " + model.getRowCount(),
                "Success", JOptionPane.INFORMATION_MESSAGE);
        });
        
        actionPanel.add(refreshButton);
        mainPanel.add(actionPanel, BorderLayout.SOUTH);
        
        // Register the panel
        registerPanel(panelKey, mainPanel, model, table);
        
        // Ensure data is loaded
        SwingUtilities.invokeLater(() -> {
            ensurePanelDataLoaded(panelKey);
        });
        
        System.out.println("✅ Data panel created: " + panelKey);
        return mainPanel;
    }
    
    /**
     * Create a styled button
     */
    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        button.setBackground(new Color(52, 152, 219));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
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
    
    /**
     * Get panel status for debugging
     */
    public String getPanelStatus(String panelKey) {
        DatabaseTableModel model = panelModels.get(panelKey);
        JTable table = panelTables.get(panelKey);
        
        if (model != null && table != null) {
            return "Panel: " + panelKey + 
                   ", Model Rows: " + model.getRowCount() + 
                   ", Table Rows: " + table.getRowCount() + 
                   ", Columns: " + table.getColumnCount();
        } else {
            return "Panel: " + panelKey + " - NOT REGISTERED";
        }
    }
    
    /**
     * Get all panel statuses
     */
    public void printAllPanelStatuses() {
        System.out.println("\n📊 PANEL STATUS REPORT");
        System.out.println("=" .repeat(50));
        
        for (String panelKey : registeredPanels.keySet()) {
            System.out.println(getPanelStatus(panelKey));
        }
        
        System.out.println("=" .repeat(50));
    }
    
    /**
     * Force refresh all data and UI
     */
    public void forceRefreshAllData() {
        System.out.println("🔄 Force refreshing all panel data...");
        
        SwingUtilities.invokeLater(() -> {
            for (String panelKey : registeredPanels.keySet()) {
                ensurePanelDataLoaded(panelKey);
                refreshPanel(panelKey);
            }
            
            // Also refresh through TableRefreshManager
            TableRefreshManager.getInstance().refreshAllTables();
            
            System.out.println("✅ All panel data force refreshed");
        });
    }
}

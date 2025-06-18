package ui;

import javax.swing.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Manages table refresh operations across the application
 */
public class TableRefreshManager {
    private static TableRefreshManager instance;
    private Map<String, DatabaseTableModel> activeTableModels;
    private Map<String, JTable> activeTables;
    
    private TableRefreshManager() {
        activeTableModels = new HashMap<>();
        activeTables = new HashMap<>();
    }
    
    public static TableRefreshManager getInstance() {
        if (instance == null) {
            instance = new TableRefreshManager();
        }
        return instance;
    }
    
    /**
     * Register a table for automatic refresh
     */
    public void registerTable(String tableKey, JTable table, DatabaseTableModel model) {
        activeTables.put(tableKey, table);
        activeTableModels.put(tableKey, model);
    }
    
    /**
     * Refresh a specific table
     */
    public void refreshTable(String tableKey) {
        DatabaseTableModel model = activeTableModels.get(tableKey);
        JTable table = activeTables.get(tableKey);

        if (model != null && table != null) {
            System.out.println("🔄 Refreshing table: " + tableKey);

            SwingUtilities.invokeLater(() -> {
                try {
                    // Ensure data is loaded first
                    model.ensureDataLoaded();

                    // Force refresh the model
                    model.forceRefresh();

                    // Comprehensive UI update
                    table.setModel(model); // Re-set model to ensure recognition
                    table.revalidate();
                    table.repaint();

                    // Update row sorter if enabled
                    if (table.getAutoCreateRowSorter()) {
                        table.setAutoCreateRowSorter(false);
                        table.setAutoCreateRowSorter(true);
                    }

                    System.out.println("✅ Table " + tableKey + " refreshed successfully (" + model.getRowCount() + " rows)");

                } catch (Exception e) {
                    System.err.println("❌ Error refreshing table " + tableKey + ": " + e.getMessage());
                    e.printStackTrace();
                }
            });
        } else {
            System.out.println("⚠️ Table " + tableKey + " not found in registry");
        }
    }
    
    /**
     * Refresh all registered tables
     */
    public void refreshAllTables() {
        for (String tableKey : activeTableModels.keySet()) {
            refreshTable(tableKey);
        }
    }
    
    /**
     * Refresh tables related to a specific entity type
     */
    public void refreshRelatedTables(String entityType) {
        switch (entityType.toLowerCase()) {
            case "computer":
                refreshTable("computers");
                refreshTable("complaints"); // Complaints might reference computers
                break;
            case "lab":
                refreshTable("labs");
                refreshTable("lab_reservations");
                refreshTable("computers"); // Computers are assigned to labs
                break;
            case "complaint":
                refreshTable("complaints");
                break;
            case "reservation":
                refreshTable("lab_reservations");
                break;
            case "software":
                refreshTable("software");
                refreshTable("software_requests");
                break;
            case "user":
                refreshTable("user_access");
                break;
            default:
                refreshAllTables();
                break;
        }
    }
    
    /**
     * Unregister a table (cleanup)
     */
    public void unregisterTable(String tableKey) {
        activeTableModels.remove(tableKey);
        activeTables.remove(tableKey);
    }
    
    /**
     * Get registered table model
     */
    public DatabaseTableModel getTableModel(String tableKey) {
        return activeTableModels.get(tableKey);
    }
    
    /**
     * Get registered table
     */
    public JTable getTable(String tableKey) {
        return activeTables.get(tableKey);
    }
    
    /**
     * Check if a table is registered
     */
    public boolean isTableRegistered(String tableKey) {
        return activeTableModels.containsKey(tableKey) && activeTables.containsKey(tableKey);
    }
}

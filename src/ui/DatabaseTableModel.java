package ui;

import javax.swing.table.AbstractTableModel;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Database-connected table model that automatically refreshes data
 */
public class DatabaseTableModel extends AbstractTableModel {
    private static final String URL = "jdbc:postgresql://localhost:5434/lims_db";
    private static final String USER = "postgres";
    private static final String PASSWORD = "superadmin";
    
    private String[] columnNames;
    private List<Object[]> data;
    private String tableName;
    private String selectQuery;
    
    public DatabaseTableModel(String tableName, String[] columnNames, String selectQuery) {
        this.tableName = tableName;
        this.columnNames = columnNames;
        this.selectQuery = selectQuery;
        this.data = new ArrayList<>();

        System.out.println("🔧 Creating DatabaseTableModel for: " + tableName);

        // Load initial data synchronously to ensure it's available immediately
        refreshDataInternal();

        System.out.println("✅ DatabaseTableModel created for " + tableName + " with " + data.size() + " rows");
    }
    
    @Override
    public int getRowCount() {
        return data.size();
    }
    
    @Override
    public int getColumnCount() {
        return columnNames.length;
    }
    
    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }
    
    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        if (rowIndex < data.size() && columnIndex < columnNames.length) {
            Object value = data.get(rowIndex)[columnIndex];
            // Debug: Only log for first few calls to avoid spam
            if (rowIndex == 0 && columnIndex < 3) {
                System.out.println("🔍 getValueAt(" + rowIndex + "," + columnIndex + ") = " + value + " for " + tableName);
            }
            return value;
        }
        System.out.println("⚠️ getValueAt(" + rowIndex + "," + columnIndex + ") out of bounds for " + tableName + " (size: " + data.size() + ")");
        return null;
    }
    
    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false; // Make table read-only
    }
    
    /**
     * Refresh data from database
     */
    public void refreshData() {
        System.out.println("🔄 DatabaseTableModel.refreshData() called for table: " + tableName);
        System.out.println("📊 Query: " + selectQuery);

        // Ensure data refresh happens on EDT for proper UI updates
        if (javax.swing.SwingUtilities.isEventDispatchThread()) {
            refreshDataInternal();
        } else {
            javax.swing.SwingUtilities.invokeLater(this::refreshDataInternal);
        }
    }

    /**
     * Internal method to refresh data - always called on EDT
     */
    private void refreshDataInternal() {
        System.out.println("🔄 refreshDataInternal() called for " + tableName);

        // Store old data size for comparison
        int oldSize = data.size();
        data.clear();

        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(selectQuery)) {

            System.out.println("✅ Database connection successful for " + tableName);

            int rowCount = 0;
            while (resultSet.next()) {
                Object[] row = new Object[columnNames.length];
                for (int i = 0; i < columnNames.length; i++) {
                    Object value = resultSet.getObject(i + 1);
                    // Handle null values properly
                    row[i] = (value != null) ? value : "";
                }
                data.add(row);
                rowCount++;
            }

            System.out.println("📈 Loaded " + rowCount + " rows for " + tableName + " (was " + oldSize + ")");

            // Ensure we're on EDT for UI updates
            if (javax.swing.SwingUtilities.isEventDispatchThread()) {
                // Notify table that data has changed
                fireTableDataChanged();
                System.out.println("🔔 fireTableDataChanged() called for " + tableName + " on EDT");
            } else {
                // Schedule UI update on EDT
                javax.swing.SwingUtilities.invokeLater(() -> {
                    fireTableDataChanged();
                    System.out.println("🔔 fireTableDataChanged() scheduled for " + tableName + " on EDT");
                });
            }

            // Log sample data for verification
            if (rowCount > 0 && data.size() > 0) {
                Object[] firstRow = data.get(0);
                System.out.print("📋 Sample data for " + tableName + ": ");
                for (int i = 0; i < Math.min(3, firstRow.length); i++) {
                    System.out.print("[" + firstRow[i] + "] ");
                }
                System.out.println();
            }

        } catch (SQLException e) {
            System.err.println("❌ Error refreshing " + tableName + " data: " + e.getMessage());
            e.printStackTrace();

            // Ensure UI is updated even on error
            if (javax.swing.SwingUtilities.isEventDispatchThread()) {
                fireTableDataChanged();
            } else {
                javax.swing.SwingUtilities.invokeLater(this::fireTableDataChanged);
            }
        }
    }

    /**
     * Force refresh data and ensure UI update
     */
    public void forceRefresh() {
        System.out.println("🔄 Force refresh requested for " + tableName);
        javax.swing.SwingUtilities.invokeLater(() -> {
            refreshDataInternal();
            // Additional UI update to ensure visibility
            fireTableStructureChanged();
            System.out.println("🔔 fireTableStructureChanged() called for " + tableName);
        });
    }

    /**
     * Ensure data is loaded and UI is updated
     */
    public void ensureDataLoaded() {
        System.out.println("🔍 Ensuring data is loaded for " + tableName);

        if (data.isEmpty()) {
            System.out.println("⚠️ No data found, forcing refresh for " + tableName);
            refreshDataInternal();
        }

        // Always fire table events to ensure UI is updated
        javax.swing.SwingUtilities.invokeLater(() -> {
            fireTableDataChanged();
            fireTableStructureChanged();
            System.out.println("🔔 UI update events fired for " + tableName + " (" + data.size() + " rows)");
        });
    }

    /**
     * Get data status for debugging
     */
    public String getDataStatus() {
        return "Table: " + tableName + ", Rows: " + data.size() + ", Columns: " + columnNames.length;
    }
    
    /**
     * Get computer table model
     */
    public static DatabaseTableModel getComputerTableModel() {
        String[] columns = {"Computer ID", "Lab", "Computer Name", "IP Address", "Specifications", "Status", "Install Date", "Notes"};
        String query = "SELECT c.computer_id, " +
                      "COALESCE(l.lab_name, 'Lab ' || c.lab_id) as lab_name, " +
                      "c.computer_name, c.ip_address, c.specifications, c.status, c.install_date, " +
                      "COALESCE(c.notes, '') as notes " +
                      "FROM computers c " +
                      "LEFT JOIN labs l ON l.lab_id = c.lab_id " +
                      "ORDER BY c.created_at DESC";
        return new DatabaseTableModel("computers", columns, query);
    }
    
    /**
     * Get lab table model
     */
    public static DatabaseTableModel getLabTableModel() {
        String[] columns = {"Lab ID", "Name", "Capacity", "Equipment", "Status"};
        String query = "SELECT lab_id, lab_name, capacity, " +
                      "COALESCE(equipment, '') as equipment, status " +
                      "FROM labs ORDER BY lab_id";
        return new DatabaseTableModel("labs", columns, query);
    }
    
    /**
     * Get complaint table model
     */
    public static DatabaseTableModel getComplaintTableModel() {
        String[] columns = {"ID", "Computer ID", "Department", "Issue Type", "Description", "Status", "Urgency", "Assigned To", "Created Date"};
        String query = "SELECT id, computer_id, department, issue_type, " +
                      "CASE WHEN LENGTH(description) > 50 THEN SUBSTRING(description, 1, 50) || '...' ELSE description END, " +
                      "status, urgency, COALESCE(assigned_to, 'Unassigned') as assigned_to, " +
                      "created_at::date FROM complaints ORDER BY created_at DESC";
        return new DatabaseTableModel("complaints", columns, query);
    }
    
    /**
     * Get lab reservation table model
     */
    public static DatabaseTableModel getLabReservationTableModel() {
        String[] columns = {"ID", "Requester", "Lab", "Date", "Time Slot", "Purpose", "Status"};
        String query = "SELECT id, requester_name, lab_name, reservation_date, time_slot, " +
                      "COALESCE(purpose, '') as purpose, status " +
                      "FROM lab_reservations ORDER BY reservation_date DESC, created_at DESC";
        return new DatabaseTableModel("lab_reservations", columns, query);
    }
    
    /**
     * Get software request table model
     */
    public static DatabaseTableModel getSoftwareRequestTableModel() {
        String[] columns = {"ID", "Computer ID", "Software", "Version", "Status", "Urgency", "Requested By", "Created Date"};
        String query = "SELECT id, computer_id, software_name, version, status, urgency, requested_by, created_at::date " +
                      "FROM software_requests ORDER BY created_at DESC";
        return new DatabaseTableModel("software_requests", columns, query);
    }

    /**
     * Get feedback table model (if feedback table exists)
     */
    public static DatabaseTableModel getFeedbackTableModel() {
        String[] columns = {"ID", "Name", "Category", "Feedback", "Status", "Created Date"};
        String query = "SELECT id, name, category, " +
                      "CASE WHEN LENGTH(feedback) > 50 THEN SUBSTRING(feedback, 1, 50) || '...' ELSE feedback END, " +
                      "status, created_at::date FROM feedback ORDER BY created_at DESC";
        return new DatabaseTableModel("feedback", columns, query);
    }
    
    /**
     * Get user access table model
     */
    public static DatabaseTableModel getUserAccessTableModel() {
        String[] columns = {"User ID", "Name", "Role", "Department", "Access Level", "Status"};
        String query = "SELECT user_id, name, role, department, access_level, status " +
                      "FROM user_access ORDER BY created_at DESC";
        return new DatabaseTableModel("user_access", columns, query);
    }
    
    /**
     * Get software inventory table model
     */
    public static DatabaseTableModel getSoftwareInventoryTableModel() {
        String[] columns = {"ID", "Software Name", "Version", "License Type", "Installations", "Status"};
        String query = "SELECT id, software_name, version, license_type, installations, status " +
                      "FROM software ORDER BY created_at DESC";
        return new DatabaseTableModel("software", columns, query);
    }
}

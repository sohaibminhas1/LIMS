import ui.DatabaseTableModel;
import ui.TableRefreshManager;
import javax.swing.*;
import java.awt.*;

/**
 * Fixes data display issues in LIMS panels
 */
public class DataDisplayFixer {
    
    public static void main(String[] args) {
        System.out.println("🔧 DATA DISPLAY FIXER");
        System.out.println("=" .repeat(40));
        
        testAllTableModels();
        createTestUI();
        
        System.out.println("\n✅ Data display verification completed!");
    }
    
    private static void testAllTableModels() {
        System.out.println("\n📊 Testing all table models...");
        
        // Test each table model
        testTableModel("Complaints", DatabaseTableModel.getComplaintTableModel());
        testTableModel("Computers", DatabaseTableModel.getComputerTableModel());
        testTableModel("Lab Reservations", DatabaseTableModel.getLabReservationTableModel());
        testTableModel("Software Requests", DatabaseTableModel.getSoftwareRequestTableModel());
        testTableModel("Labs", DatabaseTableModel.getLabTableModel());
        testTableModel("User Access", DatabaseTableModel.getUserAccessTableModel());
        
        try {
            testTableModel("Feedback", DatabaseTableModel.getFeedbackTableModel());
        } catch (Exception e) {
            System.out.println("⚠️ Feedback table might not exist: " + e.getMessage());
        }
        
        try {
            testTableModel("Software Inventory", DatabaseTableModel.getSoftwareInventoryTableModel());
        } catch (Exception e) {
            System.out.println("⚠️ Software inventory table might not exist: " + e.getMessage());
        }
    }
    
    private static void testTableModel(String name, DatabaseTableModel model) {
        System.out.println("\n🧪 Testing " + name + " table model:");
        System.out.println("   Columns: " + model.getColumnCount());
        System.out.println("   Initial rows: " + model.getRowCount());
        
        // Force refresh
        model.forceRefresh();
        System.out.println("   After refresh: " + model.getRowCount() + " rows");
        
        // Show sample data
        if (model.getRowCount() > 0) {
            System.out.print("   Sample row: ");
            for (int col = 0; col < Math.min(3, model.getColumnCount()); col++) {
                Object value = model.getValueAt(0, col);
                System.out.print("[" + (value != null ? value.toString() : "null") + "] ");
            }
            System.out.println();
            System.out.println("✅ " + name + " data loaded successfully");
        } else {
            System.out.println("⚠️ " + name + " has no data");
        }
    }
    
    private static void createTestUI() {
        System.out.println("\n🖥️ Creating test UI to verify data display...");
        
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("LIMS Data Display Test");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(1000, 700);
            frame.setLocationRelativeTo(null);
            
            JTabbedPane tabbedPane = new JTabbedPane();
            
            // Add test panels
            tabbedPane.addTab("Complaints", createTestPanel("Complaints", DatabaseTableModel.getComplaintTableModel()));
            tabbedPane.addTab("Computers", createTestPanel("Computers", DatabaseTableModel.getComputerTableModel()));
            tabbedPane.addTab("Lab Schedule", createTestPanel("Lab Reservations", DatabaseTableModel.getLabReservationTableModel()));
            tabbedPane.addTab("Software", createTestPanel("Software Requests", DatabaseTableModel.getSoftwareRequestTableModel()));
            
            frame.add(tabbedPane);
            frame.setVisible(true);
            
            System.out.println("✅ Test UI created and visible!");
            System.out.println("🎯 Check each tab to verify data is displayed correctly");
        });
    }
    
    private static JPanel createTestPanel(String title, DatabaseTableModel model) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Title
        JLabel titleLabel = new JLabel(title + " Data Test");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(titleLabel, BorderLayout.NORTH);
        
        // Table
        JTable table = new JTable(model);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setRowHeight(25);
        table.setAutoCreateRowSorter(true);
        table.setFillsViewportHeight(true);
        
        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Info panel
        JPanel infoPanel = new JPanel(new FlowLayout());
        JLabel rowCountLabel = new JLabel("Rows: " + model.getRowCount());
        JLabel columnCountLabel = new JLabel("Columns: " + model.getColumnCount());
        
        JButton refreshButton = new JButton("Refresh Data");
        refreshButton.addActionListener(e -> {
            model.forceRefresh();
            rowCountLabel.setText("Rows: " + model.getRowCount());
            table.revalidate();
            table.repaint();
            JOptionPane.showMessageDialog(panel, "Data refreshed! Rows: " + model.getRowCount());
        });
        
        infoPanel.add(rowCountLabel);
        infoPanel.add(columnCountLabel);
        infoPanel.add(refreshButton);
        panel.add(infoPanel, BorderLayout.SOUTH);
        
        // Force refresh after panel creation
        SwingUtilities.invokeLater(() -> {
            model.forceRefresh();
            rowCountLabel.setText("Rows: " + model.getRowCount());
            table.revalidate();
            table.repaint();
        });
        
        return panel;
    }
}

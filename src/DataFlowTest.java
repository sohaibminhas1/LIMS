import ui.DatabaseTableModel;
import ui.JPanelDataManager;
import ui.TableRefreshManager;
import utils.DataRefreshUtil;
import service.LIMSService;
import model.Computer;
import model.LabReservation;
import javax.swing.*;
import java.awt.*;
import java.util.Date;

/**
 * Comprehensive Data Flow Test - Tests complete data flow from database to UI and back
 */
public class DataFlowTest {
    
    public static void main(String[] args) {
        System.out.println("🧪 COMPREHENSIVE DATA FLOW TEST");
        System.out.println("=" .repeat(60));
        
        SwingUtilities.invokeLater(() -> {
            try {
                new DataFlowTest().runCompleteTest();
            } catch (Exception e) {
                System.err.println("❌ Test error: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }
    
    private void runCompleteTest() {
        System.out.println("🚀 Starting comprehensive data flow test...");
        
        // Test 1: Database Table Model Creation and Data Loading
        testDatabaseTableModels();
        
        // Test 2: JPanel Data Manager
        testJPanelDataManager();
        
        // Test 3: Data Entry and Saving
        testDataEntryAndSaving();
        
        // Test 4: UI Refresh Mechanisms
        testUIRefreshMechanisms();
        
        // Test 5: Complete Data Flow
        testCompleteDataFlow();
        
        System.out.println("\n🎉 Comprehensive data flow test completed!");
    }
    
    private void testDatabaseTableModels() {
        System.out.println("\n📊 TEST 1: Database Table Model Creation and Data Loading");
        System.out.println("-" .repeat(50));
        
        // Test all table models
        testTableModel("Computers", DatabaseTableModel.getComputerTableModel());
        testTableModel("Lab Reservations", DatabaseTableModel.getLabReservationTableModel());
        testTableModel("Software Requests", DatabaseTableModel.getSoftwareRequestTableModel());
        testTableModel("Complaints", DatabaseTableModel.getComplaintTableModel());
        testTableModel("Labs", DatabaseTableModel.getLabTableModel());
        testTableModel("User Access", DatabaseTableModel.getUserAccessTableModel());
        
        System.out.println("✅ Database table model test completed");
    }
    
    private void testTableModel(String name, DatabaseTableModel model) {
        System.out.println("🧪 Testing " + name + " model:");
        System.out.println("   Columns: " + model.getColumnCount());
        System.out.println("   Rows: " + model.getRowCount());
        System.out.println("   Status: " + model.getDataStatus());
        
        // Test data access
        if (model.getRowCount() > 0) {
            Object value = model.getValueAt(0, 0);
            System.out.println("   Sample data: " + value);
            System.out.println("✅ " + name + " model working correctly");
        } else {
            System.out.println("⚠️ " + name + " model has no data");
        }
    }
    
    private void testJPanelDataManager() {
        System.out.println("\n🏗️ TEST 2: JPanel Data Manager");
        System.out.println("-" .repeat(40));
        
        JPanelDataManager manager = JPanelDataManager.getInstance();
        
        // Create test panels
        DatabaseTableModel computerModel = DatabaseTableModel.getComputerTableModel();
        JPanel computerPanel = manager.createDataPanel("test_computers", "Test Computer Panel", computerModel);
        
        DatabaseTableModel reservationModel = DatabaseTableModel.getLabReservationTableModel();
        JPanel reservationPanel = manager.createDataPanel("test_reservations", "Test Reservation Panel", reservationModel);
        
        // Test panel status
        manager.printAllPanelStatuses();
        
        // Create test window to display panels
        JFrame testFrame = new JFrame("Data Flow Test - Panel Display");
        testFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        testFrame.setSize(1000, 700);
        testFrame.setLocationRelativeTo(null);
        
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Computers", computerPanel);
        tabbedPane.addTab("Reservations", reservationPanel);
        
        testFrame.add(tabbedPane);
        testFrame.setVisible(true);
        
        System.out.println("✅ JPanel Data Manager test completed - Test window opened");
    }
    
    private void testDataEntryAndSaving() {
        System.out.println("\n💾 TEST 3: Data Entry and Saving");
        System.out.println("-" .repeat(35));
        
        try {
            LIMSService limsService = LIMSService.getInstance();
            
            // Test computer creation
            Computer testComputer = new Computer(
                "TEST-PC-" + System.currentTimeMillis(),
                "CS Lab 1",
                "Test Computer",
                "192.168.1.100",
                "Test Specifications",
                "Available",
                "2024-01-01",
                "Test computer for data flow testing"
            );
            
            System.out.println("🔧 Creating test computer: " + testComputer.getId());
            try {
                limsService.getComputerService().addComputer(testComputer);
                System.out.println("✅ Test computer created successfully");

                // Test data refresh after creation
                DataRefreshUtil.refreshComputerData();
                System.out.println("✅ Data refresh triggered after computer creation");
            } catch (Exception e) {
                System.out.println("❌ Failed to create test computer: " + e.getMessage());
            }
            
            // Test lab reservation creation
            LabReservation testReservation = new LabReservation(
                "CS Lab 1",
                new Date(),
                "14:00-16:00",
                "Test User",
                "Testing",
                "Data Flow Test",
                "Test Instructor"
            );
            
            System.out.println("🔧 Creating test lab reservation");
            try {
                limsService.getLabReservationService().addReservation(testReservation);
                System.out.println("✅ Test lab reservation created successfully");

                // Test data refresh after creation
                DataRefreshUtil.refreshLabReservationData();
                System.out.println("✅ Data refresh triggered after reservation creation");
            } catch (Exception e) {
                System.out.println("❌ Failed to create test lab reservation: " + e.getMessage());
            }
            
        } catch (Exception e) {
            System.err.println("❌ Data entry test error: " + e.getMessage());
            e.printStackTrace();
        }
        
        System.out.println("✅ Data entry and saving test completed");
    }
    
    private void testUIRefreshMechanisms() {
        System.out.println("\n🔄 TEST 4: UI Refresh Mechanisms");
        System.out.println("-" .repeat(35));
        
        // Test TableRefreshManager
        System.out.println("🧪 Testing TableRefreshManager...");
        TableRefreshManager.getInstance().refreshAllTables();
        System.out.println("✅ TableRefreshManager refresh completed");
        
        // Test JPanelDataManager
        System.out.println("🧪 Testing JPanelDataManager...");
        JPanelDataManager.getInstance().refreshAllPanels();
        System.out.println("✅ JPanelDataManager refresh completed");
        
        // Test DataRefreshUtil
        System.out.println("🧪 Testing DataRefreshUtil...");
        DataRefreshUtil.refreshAllData();
        System.out.println("✅ DataRefreshUtil refresh completed");
        
        System.out.println("✅ UI refresh mechanisms test completed");
    }
    
    private void testCompleteDataFlow() {
        System.out.println("\n🔄 TEST 5: Complete Data Flow");
        System.out.println("-" .repeat(30));
        
        // Create a comprehensive test window that demonstrates complete data flow
        JFrame dataFlowFrame = new JFrame("Complete Data Flow Test");
        dataFlowFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        dataFlowFrame.setSize(1200, 800);
        dataFlowFrame.setLocationRelativeTo(null);
        
        JPanel mainPanel = new JPanel(new BorderLayout());
        
        // Title
        JLabel titleLabel = new JLabel("Complete Data Flow Test - Live Data Display");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.add(titleLabel, BorderLayout.NORTH);
        
        // Tabbed pane with all data panels
        JTabbedPane tabbedPane = new JTabbedPane();
        
        // Add all data panels using JPanelDataManager
        JPanelDataManager manager = JPanelDataManager.getInstance();
        
        tabbedPane.addTab("Computers", 
            manager.createDataPanel("flow_test_computers", "Computer Inventory", 
                DatabaseTableModel.getComputerTableModel()));
        
        tabbedPane.addTab("Lab Schedule", 
            manager.createDataPanel("flow_test_schedule", "Lab Reservations", 
                DatabaseTableModel.getLabReservationTableModel()));
        
        tabbedPane.addTab("Software Requests", 
            manager.createDataPanel("flow_test_software", "Software Management", 
                DatabaseTableModel.getSoftwareRequestTableModel()));
        
        tabbedPane.addTab("Complaints", 
            manager.createDataPanel("flow_test_complaints", "Complaint Tracking", 
                DatabaseTableModel.getComplaintTableModel()));
        
        mainPanel.add(tabbedPane, BorderLayout.CENTER);
        
        // Control panel
        JPanel controlPanel = new JPanel(new FlowLayout());
        
        JButton refreshAllButton = new JButton("Refresh All Data");
        refreshAllButton.setFont(new Font("Arial", Font.BOLD, 14));
        refreshAllButton.setBackground(new Color(46, 204, 113));
        refreshAllButton.setForeground(Color.WHITE);
        refreshAllButton.addActionListener(e -> {
            System.out.println("🔄 Manual refresh all data triggered...");
            DataRefreshUtil.refreshAllData();
            JOptionPane.showMessageDialog(dataFlowFrame, 
                "All data refreshed successfully!\nCheck console for detailed logs.", 
                "Refresh Complete", JOptionPane.INFORMATION_MESSAGE);
        });
        
        JButton statusButton = new JButton("Show Panel Status");
        statusButton.setFont(new Font("Arial", Font.BOLD, 14));
        statusButton.setBackground(new Color(52, 152, 219));
        statusButton.setForeground(Color.WHITE);
        statusButton.addActionListener(e -> {
            manager.printAllPanelStatuses();
            JOptionPane.showMessageDialog(dataFlowFrame, 
                "Panel status printed to console.\nCheck console for detailed information.", 
                "Status Report", JOptionPane.INFORMATION_MESSAGE);
        });
        
        controlPanel.add(refreshAllButton);
        controlPanel.add(statusButton);
        mainPanel.add(controlPanel, BorderLayout.SOUTH);
        
        dataFlowFrame.add(mainPanel);
        dataFlowFrame.setVisible(true);
        
        System.out.println("✅ Complete data flow test window opened");
        System.out.println("🎯 Test Instructions:");
        System.out.println("   1. Check that all tabs show data from the database");
        System.out.println("   2. Use 'Refresh All Data' to test refresh mechanisms");
        System.out.println("   3. Use 'Show Panel Status' to see panel information");
        System.out.println("   4. Add new data through the main LIMS application");
        System.out.println("   5. Refresh to see if new data appears immediately");
        
        System.out.println("✅ Complete data flow test completed");
    }
}

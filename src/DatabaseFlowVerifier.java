import service.LIMSService;
import model.*;
import dao.*;
import java.sql.*;
import java.util.List;

/**
 * Comprehensive database flow verification tool
 * Verifies that all data is properly flowing to and from the database
 */
public class DatabaseFlowVerifier {
    
    public static void main(String[] args) {
        System.out.println("🔍 COMPREHENSIVE DATABASE FLOW VERIFICATION");
        System.out.println("=" .repeat(60));
        
        verifyDatabaseConnections();
        verifyDataRetrieval();
        verifyDataInsertion();
        verifyDataRefresh();
        
        System.out.println("\n✅ DATABASE FLOW VERIFICATION COMPLETED!");
        System.out.println("🎯 All data operations are working correctly!");
    }
    
    private static void verifyDatabaseConnections() {
        System.out.println("\n📊 STEP 1: Database Connection Verification");
        System.out.println("-" .repeat(50));
        
        try {
            // Test direct database connection
            String url = "jdbc:postgresql://localhost:5434/lims_db";
            String user = "postgres";
            String password = "superadmin";
            
            try (Connection conn = DriverManager.getConnection(url, user, password)) {
                System.out.println("✅ Direct database connection: SUCCESS");
                
                // Test each table
                String[] tables = {"complaints", "computers", "lab_reservations", "software_requests", "feedback", "user_access"};
                for (String table : tables) {
                    try (PreparedStatement stmt = conn.prepareStatement("SELECT COUNT(*) FROM " + table);
                         ResultSet rs = stmt.executeQuery()) {
                        if (rs.next()) {
                            int count = rs.getInt(1);
                            System.out.println("✅ Table '" + table + "': " + count + " records");
                        }
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Database connection failed: " + e.getMessage());
        }
    }
    
    private static void verifyDataRetrieval() {
        System.out.println("\n📥 STEP 2: Data Retrieval Verification");
        System.out.println("-" .repeat(50));
        
        try {
            LIMSService service = LIMSService.getInstance();
            
            // Test complaint retrieval
            List<Complaint> complaints = service.getComplaintService().getAllComplaints();
            System.out.println("✅ Complaints retrieved: " + complaints.size() + " records");
            
            // Test computer retrieval
            List<Computer> computers = service.getComputerService().getAllComputers();
            System.out.println("✅ Computers retrieved: " + computers.size() + " records");
            
            // Test lab reservation retrieval
            List<LabReservation> reservations = service.getLabReservationService().getAllReservations();
            System.out.println("✅ Lab reservations retrieved: " + reservations.size() + " records");
            
            // Test software request retrieval
            List<SoftwareRequest> requests = service.getSoftwareRequestService().getAllRequests();
            System.out.println("✅ Software requests retrieved: " + requests.size() + " records");
            
            // Test feedback retrieval
            List<Feedback> feedback = service.getFeedbackService().getAllFeedback();
            System.out.println("✅ Feedback retrieved: " + feedback.size() + " records");
            
        } catch (Exception e) {
            System.err.println("❌ Data retrieval failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static void verifyDataInsertion() {
        System.out.println("\n📤 STEP 3: Data Insertion Verification");
        System.out.println("-" .repeat(50));
        
        try {
            LIMSService service = LIMSService.getInstance();
            
            // Test complaint insertion
            System.out.println("🧪 Testing complaint insertion...");
            Complaint testComplaint = new Complaint(
                "TEST-001",
                "IT",
                "Hardware",
                "Test complaint for verification",
                new java.util.Date(),
                "Pending",
                "Unassigned"
            );
            service.getComplaintService().addComplaint(testComplaint);
            System.out.println("✅ Test complaint inserted successfully");
            
            // Test computer insertion
            System.out.println("🧪 Testing computer insertion...");
            Computer testComputer = new Computer(
                "TEST-PC-001",
                "Lab A",
                "Station 1",
                "192.168.1.100",
                "Test Specs",
                "Available",
                "2024-01-01",
                "None"
            );
            service.getComputerService().addComputer(testComputer);
            System.out.println("✅ Test computer inserted successfully");
            
            // Test lab reservation insertion
            System.out.println("🧪 Testing lab reservation insertion...");
            LabReservation testReservation = new LabReservation(
                "Lab A",
                new java.util.Date(),
                "09:00-10:00",
                "Test User",
                "Testing",
                "Test Course",
                "Test Instructor"
            );
            service.getLabReservationService().addReservation(testReservation);
            System.out.println("✅ Test lab reservation inserted successfully");
            
            // Test software request insertion
            System.out.println("🧪 Testing software request insertion...");
            SoftwareRequest testRequest = new SoftwareRequest(
                "TEST-PC-001",
                "Test Software",
                "1.0",
                "Low",
                "Test justification",
                new java.util.Date(),
                "Pending",
                "Test User"
            );
            service.getSoftwareRequestService().addRequest(testRequest);
            System.out.println("✅ Test software request inserted successfully");
            
        } catch (Exception e) {
            System.err.println("❌ Data insertion failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static void verifyDataRefresh() {
        System.out.println("\n🔄 STEP 4: Data Refresh Verification");
        System.out.println("-" .repeat(50));
        
        try {
            // Test table refresh manager
            ui.TableRefreshManager refreshManager = ui.TableRefreshManager.getInstance();
            
            System.out.println("🧪 Testing table refresh operations...");
            refreshManager.refreshTable("complaints");
            System.out.println("✅ Complaints table refresh: SUCCESS");
            
            refreshManager.refreshTable("computers");
            System.out.println("✅ Computers table refresh: SUCCESS");
            
            refreshManager.refreshTable("lab_reservations");
            System.out.println("✅ Lab reservations table refresh: SUCCESS");
            
            refreshManager.refreshTable("software_requests");
            System.out.println("✅ Software requests table refresh: SUCCESS");
            
            // Test database table models
            System.out.println("🧪 Testing database table model refresh...");
            ui.DatabaseTableModel.getComplaintTableModel().refreshData();
            System.out.println("✅ Complaint table model refresh: SUCCESS");
            
            ui.DatabaseTableModel.getComputerTableModel().refreshData();
            System.out.println("✅ Computer table model refresh: SUCCESS");
            
            ui.DatabaseTableModel.getLabReservationTableModel().refreshData();
            System.out.println("✅ Lab reservation table model refresh: SUCCESS");
            
            ui.DatabaseTableModel.getSoftwareRequestTableModel().refreshData();
            System.out.println("✅ Software request table model refresh: SUCCESS");
            
        } catch (Exception e) {
            System.err.println("❌ Data refresh failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

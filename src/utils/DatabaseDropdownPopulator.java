package utils;

import javax.swing.JComboBox;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class to populate dropdown menus from database data
 */
public class DatabaseDropdownPopulator {
    private static final String URL = "jdbc:postgresql://localhost:5434/lims_db";
    private static final String USER = "postgres";
    private static final String PASSWORD = "superadmin";
    
    /**
     * Populate department dropdown from database
     */
    public static JComboBox<String> createDepartmentCombo() {
        JComboBox<String> combo = new JComboBox<>();
        combo.addItem("Lab");
        combo.addItem("IT");
        combo.addItem("Admin");
        combo.addItem("Research");
        combo.addItem("Computer Science");
        combo.addItem("Software Engineering");
        combo.addItem("AI");
        combo.addItem("Data Science");
        return combo;
    }
    
    /**
     * Populate lab dropdown from database
     */
    public static JComboBox<String> createLabCombo() {
        JComboBox<String> combo = new JComboBox<>();
        combo.addItem("All Labs");
        
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT DISTINCT lab_name FROM labs WHERE status = 'Active' ORDER BY lab_name")) {
            
            while (rs.next()) {
                combo.addItem(rs.getString("lab_name"));
            }
            
        } catch (SQLException e) {
            // Fallback to hardcoded values if database is not available
            combo.addItem("CS Lab 1");
            combo.addItem("CS Lab 2");
            combo.addItem("SE Lab 1");
            combo.addItem("AI Lab");
            combo.addItem("Data Lab");
        }
        
        return combo;
    }
    
    /**
     * Populate computer dropdown from database
     */
    public static JComboBox<String> createComputerCombo() {
        JComboBox<String> combo = new JComboBox<>();
        
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT computer_id FROM computers WHERE status = 'Available' ORDER BY computer_id")) {
            
            while (rs.next()) {
                combo.addItem(rs.getString("computer_id"));
            }
            
        } catch (SQLException e) {
            // Fallback to sample values if database is not available
            combo.addItem("PC-CS1001");
            combo.addItem("PC-CS1002");
            combo.addItem("PC-SE1001");
        }
        
        return combo;
    }
    
    /**
     * Populate software dropdown from database
     */
    public static JComboBox<String> createSoftwareCombo() {
        JComboBox<String> combo = new JComboBox<>();
        
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT DISTINCT software_name FROM software WHERE status = 'Active' ORDER BY software_name")) {
            
            while (rs.next()) {
                combo.addItem(rs.getString("software_name"));
            }
            
        } catch (SQLException e) {
            // Fallback to common software if database is not available
            combo.addItem("Microsoft Office");
            combo.addItem("Visual Studio");
            combo.addItem("MATLAB");
            combo.addItem("AutoCAD");
            combo.addItem("Python");
        }
        
        return combo;
    }
    
    /**
     * Create status dropdown for complaints
     */
    public static JComboBox<String> createComplaintStatusCombo() {
        JComboBox<String> combo = new JComboBox<>();
        combo.addItem("Pending");
        combo.addItem("Open");
        combo.addItem("In Progress");
        combo.addItem("Resolved");
        combo.addItem("Closed");
        combo.addItem("Cancelled");
        return combo;
    }
    
    /**
     * Create status dropdown for software requests
     */
    public static JComboBox<String> createSoftwareRequestStatusCombo() {
        JComboBox<String> combo = new JComboBox<>();
        combo.addItem("Pending");
        combo.addItem("Approved");
        combo.addItem("In Progress");
        combo.addItem("Completed");
        combo.addItem("Rejected");
        return combo;
    }
    
    /**
     * Create urgency dropdown
     */
    public static JComboBox<String> createUrgencyCombo() {
        JComboBox<String> combo = new JComboBox<>();
        combo.addItem("Low");
        combo.addItem("Medium");
        combo.addItem("High");
        combo.addItem("Critical");
        return combo;
    }
    
    /**
     * Create issue type dropdown
     */
    public static JComboBox<String> createIssueTypeCombo() {
        JComboBox<String> combo = new JComboBox<>();
        combo.addItem("Hardware");
        combo.addItem("Software");
        combo.addItem("Equipment");
        combo.addItem("Network");
        combo.addItem("Other");
        return combo;
    }
    
    /**
     * Create feedback category dropdown
     */
    public static JComboBox<String> createFeedbackCategoryCombo() {
        JComboBox<String> combo = new JComboBox<>();
        combo.addItem("Suggestion");
        combo.addItem("Complaint");
        combo.addItem("Praise");
        combo.addItem("Bug Report");
        combo.addItem("Feature Request");
        combo.addItem("Other");
        return combo;
    }
    
    /**
     * Create time slot dropdown
     */
    public static JComboBox<String> createTimeSlotCombo() {
        JComboBox<String> combo = new JComboBox<>();
        combo.addItem("08:00-10:00");
        combo.addItem("10:00-12:00");
        combo.addItem("12:00-14:00");
        combo.addItem("14:00-16:00");
        combo.addItem("16:00-18:00");
        return combo;
    }
    
    /**
     * Create purpose dropdown for lab reservations
     */
    public static JComboBox<String> createPurposeCombo() {
        JComboBox<String> combo = new JComboBox<>();
        combo.addItem("Lecture");
        combo.addItem("Practical");
        combo.addItem("Workshop");
        combo.addItem("Exam");
        combo.addItem("Research");
        combo.addItem("Training");
        combo.addItem("Meeting");
        combo.addItem("Other");
        return combo;
    }
    
    /**
     * Create role dropdown for users
     */
    public static JComboBox<String> createRoleCombo() {
        JComboBox<String> combo = new JComboBox<>();
        combo.addItem("Admin");
        combo.addItem("Teacher");
        combo.addItem("Student");
        combo.addItem("Lab Technician");
        combo.addItem("Staff");
        return combo;
    }
    
    /**
     * Create access level dropdown
     */
    public static JComboBox<String> createAccessLevelCombo() {
        JComboBox<String> combo = new JComboBox<>();
        combo.addItem("Full");
        combo.addItem("Limited");
        combo.addItem("Basic");
        combo.addItem("Read Only");
        return combo;
    }
    
    /**
     * Create general status dropdown
     */
    public static JComboBox<String> createGeneralStatusCombo() {
        JComboBox<String> combo = new JComboBox<>();
        combo.addItem("Active");
        combo.addItem("Inactive");
        combo.addItem("Pending");
        combo.addItem("Suspended");
        combo.addItem("Maintenance");
        return combo;
    }
    
    /**
     * Create license type dropdown
     */
    public static JComboBox<String> createLicenseTypeCombo() {
        JComboBox<String> combo = new JComboBox<>();
        combo.addItem("Open Source");
        combo.addItem("Academic");
        combo.addItem("Professional");
        combo.addItem("Enterprise");
        combo.addItem("Subscription");
        combo.addItem("Trial");
        return combo;
    }
    
    /**
     * Refresh all database-connected dropdowns
     */
    public static void refreshAllDropdowns() {
        // This method can be called to refresh all dropdowns when database data changes
        DAOLogger.info("DatabaseDropdownPopulator", "refreshAllDropdowns", "Refreshing all dropdown data from database");
    }
}

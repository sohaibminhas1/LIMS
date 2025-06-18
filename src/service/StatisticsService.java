package service;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Service class for fetching dashboard statistics from the database
 */
public class StatisticsService {
    private static final String URL = "jdbc:postgresql://localhost:5434/lims_db";
    private static final String USER = "postgres";
    private static final String PASSWORD = "superadmin";
    
    /**
     * Get statistics for admin dashboard
     */
    public static Map<String, String> getAdminStatistics() {
        Map<String, String> stats = new HashMap<>();
        
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
            // Active Labs
            String labQuery = "SELECT COUNT(*) FROM labs WHERE status = 'Active'";
            try (PreparedStatement stmt = connection.prepareStatement(labQuery);
                 ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    stats.put("Active Labs", String.valueOf(rs.getInt(1)));
                }
            }
            
            // Total Computers
            String computerQuery = "SELECT COUNT(*) FROM computers";
            try (PreparedStatement stmt = connection.prepareStatement(computerQuery);
                 ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    stats.put("Total Computers", String.valueOf(rs.getInt(1)));
                }
            }
            
            // Software Requests
            String softwareQuery = "SELECT COUNT(*) FROM software_requests WHERE status = 'Pending'";
            try (PreparedStatement stmt = connection.prepareStatement(softwareQuery);
                 ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    stats.put("Software Requests", String.valueOf(rs.getInt(1)));
                }
            }
            
            // Pending Reports (Complaints)
            String complaintQuery = "SELECT COUNT(*) FROM complaints WHERE status = 'Open'";
            try (PreparedStatement stmt = connection.prepareStatement(complaintQuery);
                 ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    stats.put("Pending Reports", String.valueOf(rs.getInt(1)));
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error fetching admin statistics: " + e.getMessage());
            // Return default values on error
            stats.put("Active Labs", "0");
            stats.put("Total Computers", "0");
            stats.put("Software Requests", "0");
            stats.put("Pending Reports", "0");
        }
        
        return stats;
    }
    
    /**
     * Get statistics for student dashboard
     */
    public static Map<String, String> getStudentStatistics(String username) {
        Map<String, String> stats = new HashMap<>();
        
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
            // My Complaints (for this user)
            String myComplaintQuery = "SELECT COUNT(*) FROM complaints WHERE created_by = ? OR assigned_to = ?";
            try (PreparedStatement stmt = connection.prepareStatement(myComplaintQuery)) {
                stmt.setString(1, username);
                stmt.setString(2, username);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        stats.put("My Complaints", String.valueOf(rs.getInt(1)));
                    }
                }
            }
            
            // Software Requests (for this user)
            String mySoftwareQuery = "SELECT COUNT(*) FROM software_requests WHERE requested_by = ?";
            try (PreparedStatement stmt = connection.prepareStatement(mySoftwareQuery)) {
                stmt.setString(1, username);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        stats.put("Software Requests", String.valueOf(rs.getInt(1)));
                    }
                }
            }
            
            // Available Labs
            String availableLabQuery = "SELECT COUNT(*) FROM labs WHERE status = 'Active'";
            try (PreparedStatement stmt = connection.prepareStatement(availableLabQuery);
                 ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    stats.put("Available Labs", String.valueOf(rs.getInt(1)));
                }
            }
            
            // Available Computers
            String availableComputerQuery = "SELECT COUNT(*) FROM computers WHERE status = 'Available'";
            try (PreparedStatement stmt = connection.prepareStatement(availableComputerQuery);
                 ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    stats.put("Available Computers", String.valueOf(rs.getInt(1)));
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error fetching student statistics: " + e.getMessage());
            // Return default values on error
            stats.put("My Complaints", "0");
            stats.put("Software Requests", "0");
            stats.put("Available Labs", "0");
            stats.put("Available Computers", "0");
        }
        
        return stats;
    }
    
    /**
     * Get statistics for teacher dashboard
     */
    public static Map<String, String> getTeacherStatistics(String username) {
        Map<String, String> stats = new HashMap<>();
        
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
            // Pending Complaints
            String pendingComplaintQuery = "SELECT COUNT(*) FROM complaints WHERE status = 'Open'";
            try (PreparedStatement stmt = connection.prepareStatement(pendingComplaintQuery);
                 ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    stats.put("Pending Complaints", String.valueOf(rs.getInt(1)));
                }
            }
            
            // Software Requests (for this user)
            String mySoftwareQuery = "SELECT COUNT(*) FROM software_requests WHERE requested_by = ?";
            try (PreparedStatement stmt = connection.prepareStatement(mySoftwareQuery)) {
                stmt.setString(1, username);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        stats.put("Software Requests", String.valueOf(rs.getInt(1)));
                    }
                }
            }
            
            // Active Labs
            String activeLabQuery = "SELECT COUNT(*) FROM labs WHERE status = 'Active'";
            try (PreparedStatement stmt = connection.prepareStatement(activeLabQuery);
                 ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    stats.put("Active Labs", String.valueOf(rs.getInt(1)));
                }
            }
            
            // Total Computers
            String totalComputerQuery = "SELECT COUNT(*) FROM computers";
            try (PreparedStatement stmt = connection.prepareStatement(totalComputerQuery);
                 ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    stats.put("Total Computers", String.valueOf(rs.getInt(1)));
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Error fetching teacher statistics: " + e.getMessage());
            // Return default values on error
            stats.put("Pending Complaints", "0");
            stats.put("Software Requests", "0");
            stats.put("Active Labs", "0");
            stats.put("Total Computers", "0");
        }
        
        return stats;
    }
}

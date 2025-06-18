package dao;

import model.Complaint;
import utils.DAOLogger;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Complaint entity
 * Handles all database operations for complaints
 */
public class ComplaintDAO extends AbstractDAO implements BaseDAO<Complaint> {
    
    // SQL Queries
    private static final String INSERT_COMPLAINT =
        "INSERT INTO complaints (computer_id, department, issue_type, description, status, urgency, created_at) " +
        "VALUES (?, ?, ?, ?, ?, ?, ?)";

    private static final String UPDATE_COMPLAINT =
        "UPDATE complaints SET department = ?, issue_type = ?, description = ?, status = ?, urgency = ? " +
        "WHERE computer_id = ?";

    private static final String UPDATE_COMPLAINT_STATUS =
        "UPDATE complaints SET status = ? WHERE computer_id = ?";

    private static final String UPDATE_COMPLAINT_STATUS_BY_ID =
        "UPDATE complaints SET status = ? WHERE id = ?";

    private static final String DELETE_COMPLAINT =
        "DELETE FROM complaints WHERE computer_id = ?";

    private static final String SELECT_COMPLAINT_BY_ID =
        "SELECT * FROM complaints WHERE computer_id = ? ORDER BY created_at DESC LIMIT 1";

    private static final String SELECT_ALL_COMPLAINTS =
        "SELECT * FROM complaints ORDER BY created_at DESC";

    private static final String SELECT_COMPLAINTS_BY_STATUS =
        "SELECT * FROM complaints WHERE status = ? ORDER BY created_at DESC";

    private static final String GET_LATEST_COMPLAINT_ID =
        "SELECT id FROM complaints WHERE computer_id = ? ORDER BY created_at DESC LIMIT 1";
    
    @Override
    public boolean insert(Complaint complaint) {
        DAOLogger.logMethodEntry("ComplaintDAO", "insert", complaint.getComputerId(), complaint.getDepartment(), complaint.getIssueType());

        Connection connection = null;
        PreparedStatement statement = null;
        long startTime = System.currentTimeMillis();

        try {
            DAOLogger.info("ComplaintDAO", "insert", "Attempting to insert complaint for computer: " + complaint.getComputerId());

            connection = getConnection();
            DAOLogger.debug("ComplaintDAO", "insert", "Database connection established");

            statement = connection.prepareStatement(INSERT_COMPLAINT);

            // Set parameters with logging
            statement.setString(1, complaint.getComputerId());
            statement.setString(2, complaint.getDepartment());
            statement.setString(3, complaint.getIssueType());
            statement.setString(4, complaint.getDescription());
            statement.setString(5, complaint.getStatus());
            statement.setString(6, "Medium"); // Default urgency since model doesn't have urgency field
            statement.setTimestamp(7, new Timestamp(complaint.getSubmissionDate().getTime()));

            DAOLogger.debug("ComplaintDAO", "insert", "Parameters set - executing SQL: " + INSERT_COMPLAINT);

            int rowsAffected = statement.executeUpdate();
            boolean success = rowsAffected > 0;

            long executionTime = System.currentTimeMillis() - startTime;
            DAOLogger.logPerformance("ComplaintDAO", "insert", executionTime);

            if (success) {
                DAOLogger.logDatabaseSuccess("ComplaintDAO", "INSERT", "complaint", rowsAffected);
                DAOLogger.success("ComplaintDAO", "insert", "Complaint inserted successfully: " + complaint.getComputerId());
            } else {
                DAOLogger.logDatabaseFailure("ComplaintDAO", "INSERT", "complaint", "No rows affected");
            }

            DAOLogger.logMethodExit("ComplaintDAO", "insert", success);
            return success;

        } catch (SQLException e) {
            DAOLogger.error("ComplaintDAO", "insert", "SQL error during complaint insertion", e);
            return false;
        } finally {
            closeResources(connection, statement);
            DAOLogger.debug("ComplaintDAO", "insert", "Resources closed");
        }
    }
    
    @Override
    public boolean update(Complaint complaint) {
        Connection connection = null;
        PreparedStatement statement = null;
        
        try {
            connection = getConnection();
            statement = connection.prepareStatement(UPDATE_COMPLAINT);
            
            statement.setString(1, complaint.getDepartment());
            statement.setString(2, complaint.getIssueType());
            statement.setString(3, complaint.getDescription());
            statement.setString(4, complaint.getStatus());
            statement.setString(5, "Medium"); // Default urgency since model doesn't have urgency field
            statement.setString(6, complaint.getComputerId());
            statement.setTimestamp(7, new Timestamp(complaint.getSubmissionDate().getTime()));
            
            int rowsAffected = statement.executeUpdate();
            boolean success = rowsAffected > 0;
            
            if (success) {
                System.out.println("✅ Complaint updated successfully: " + complaint.getComputerId());
            } else {
                System.out.println("❌ Failed to update complaint: " + complaint.getComputerId());
            }
            
            return success;
            
        } catch (SQLException e) {
            System.err.println("❌ Error updating complaint: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            closeResources(connection, statement);
        }
    }
    
    /**
     * Update complaint status by computer ID
     * @param computerId Computer ID
     * @param status New status
     * @return true if update was successful
     */
    public boolean updateStatus(String computerId, String status) {
        Connection connection = null;
        PreparedStatement statement = null;

        try {
            connection = getConnection();
            statement = connection.prepareStatement(UPDATE_COMPLAINT_STATUS);

            statement.setString(1, status);
            statement.setString(2, computerId);

            int rowsAffected = statement.executeUpdate();
            boolean success = rowsAffected > 0;

            if (success) {
                System.out.println("✅ Complaint status updated successfully: " + computerId + " -> " + status);
            } else {
                System.out.println("❌ Failed to update complaint status: " + computerId);
            }

            return success;

        } catch (SQLException e) {
            System.err.println("❌ Error updating complaint status: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            closeResources(connection, statement);
        }
    }

    /**
     * Update complaint status by ID (for exact record targeting)
     * @param complaintId Complaint ID
     * @param status New status
     * @return true if update was successful
     */
    public boolean updateStatusById(int complaintId, String status) {
        Connection connection = null;
        PreparedStatement statement = null;

        try {
            connection = getConnection();
            statement = connection.prepareStatement(UPDATE_COMPLAINT_STATUS_BY_ID);

            statement.setString(1, status);
            statement.setInt(2, complaintId);

            int rowsAffected = statement.executeUpdate();
            boolean success = rowsAffected > 0;

            if (success) {
                System.out.println("✅ Complaint status updated successfully: ID " + complaintId + " -> " + status);
            } else {
                System.out.println("❌ Failed to update complaint status: ID " + complaintId);
            }

            return success;

        } catch (SQLException e) {
            System.err.println("❌ Error updating complaint status: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            closeResources(connection, statement);
        }
    }

    /**
     * Legacy method for backward compatibility
     */
    public boolean updateStatus(String computerId, java.util.Date submissionDate, String status) {
        return updateStatus(computerId, status);
    }
    
    @Override
    public boolean delete(String computerId) {
        // For complaints, we need both computer_id and date, so this method finds the latest complaint
        Complaint complaint = findById(computerId);
        if (complaint == null) {
            return false;
        }
        
        Connection connection = null;
        PreparedStatement statement = null;
        
        try {
            connection = getConnection();
            statement = connection.prepareStatement(DELETE_COMPLAINT);
            
            statement.setString(1, computerId);
            statement.setTimestamp(2, new Timestamp(complaint.getSubmissionDate().getTime()));
            
            int rowsAffected = statement.executeUpdate();
            boolean success = rowsAffected > 0;
            
            if (success) {
                System.out.println("✅ Complaint deleted successfully: " + computerId);
            } else {
                System.out.println("❌ Failed to delete complaint: " + computerId);
            }
            
            return success;
            
        } catch (SQLException e) {
            System.err.println("❌ Error deleting complaint: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            closeResources(connection, statement);
        }
    }
    
    @Override
    public Complaint findById(String computerId) {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        
        try {
            connection = getConnection();
            statement = connection.prepareStatement(SELECT_COMPLAINT_BY_ID);
            statement.setString(1, computerId);
            
            resultSet = statement.executeQuery();
            
            if (resultSet.next()) {
                return mapResultSetToComplaint(resultSet);
            }
            
            return null;
            
        } catch (SQLException e) {
            System.err.println("❌ Error finding complaint by ID: " + e.getMessage());
            e.printStackTrace();
            return null;
        } finally {
            closeResources(connection, statement, resultSet);
        }
    }
    
    @Override
    public List<Complaint> findAll() {
        List<Complaint> complaints = new ArrayList<>();
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        
        try {
            connection = getConnection();
            statement = connection.prepareStatement(SELECT_ALL_COMPLAINTS);
            resultSet = statement.executeQuery();
            
            while (resultSet.next()) {
                complaints.add(mapResultSetToComplaint(resultSet));
            }
            
            System.out.println("✅ Retrieved " + complaints.size() + " complaints from database");
            
        } catch (SQLException e) {
            System.err.println("❌ Error retrieving all complaints: " + e.getMessage());
            e.printStackTrace();
        } finally {
            closeResources(connection, statement, resultSet);
        }
        
        return complaints;
    }
    
    /**
     * Find complaints by status
     * @param status Status to filter by
     * @return List of complaints with the specified status
     */
    public List<Complaint> findByStatus(String status) {
        List<Complaint> complaints = new ArrayList<>();
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        
        try {
            connection = getConnection();
            statement = connection.prepareStatement(SELECT_COMPLAINTS_BY_STATUS);
            statement.setString(1, status);
            resultSet = statement.executeQuery();
            
            while (resultSet.next()) {
                complaints.add(mapResultSetToComplaint(resultSet));
            }
            
            System.out.println("✅ Retrieved " + complaints.size() + " complaints with status: " + status);
            
        } catch (SQLException e) {
            System.err.println("❌ Error retrieving complaints by status: " + e.getMessage());
            e.printStackTrace();
        } finally {
            closeResources(connection, statement, resultSet);
        }
        
        return complaints;
    }
    
    /**
     * Map ResultSet to Complaint object
     * @param resultSet Database result set
     * @return Complaint object
     * @throws SQLException if mapping fails
     */
    private Complaint mapResultSetToComplaint(ResultSet resultSet) throws SQLException {
        DAOLogger.debug("ComplaintDAO", "mapResultSetToComplaint", "Mapping result set to Complaint object");

        try {
            Complaint complaint = new Complaint(
                resultSet.getString("computer_id"),
                resultSet.getString("department"),
                resultSet.getString("issue_type"),
                resultSet.getString("description"),
                resultSet.getTimestamp("created_at"),
                resultSet.getString("status"),
                resultSet.getString("assigned_to")  // Fixed: use assigned_to instead of urgency
            );

            DAOLogger.debug("ComplaintDAO", "mapResultSetToComplaint", "Successfully mapped complaint: " + complaint.getComputerId());
            return complaint;

        } catch (SQLException e) {
            DAOLogger.error("ComplaintDAO", "mapResultSetToComplaint", "Error mapping result set to complaint", e);
            throw e;
        }
    }
    
    @Override
    public Connection getConnection() throws SQLException {
        return super.getConnection();
    }
}

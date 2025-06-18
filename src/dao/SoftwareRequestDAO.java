package dao;

import model.SoftwareRequest;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for SoftwareRequest entity
 * Handles all database operations for software requests
 */
public class SoftwareRequestDAO extends AbstractDAO implements BaseDAO<SoftwareRequest> {
    
    // SQL Queries
    private static final String INSERT_REQUEST =
        "INSERT INTO software_requests (computer_id, software_name, version, urgency, justification, status, requested_by, created_at) " +
        "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
    
    private static final String UPDATE_REQUEST = 
        "UPDATE software_requests SET software_name = ?, version = ?, urgency = ?, justification = ?, status = ? " +
        "WHERE computer_id = ? AND created_at = ?";
    
    private static final String UPDATE_REQUEST_STATUS =
        "UPDATE software_requests SET status = ?, requested_by = ? WHERE computer_id = ?";

    private static final String APPROVE_REQUEST =
        "UPDATE software_requests SET status = 'Approved', requested_by = ? WHERE computer_id = ?";

    private static final String REJECT_REQUEST =
        "UPDATE software_requests SET status = 'Rejected' WHERE computer_id = ?";

    private static final String UPDATE_REQUEST_STATUS_BY_ID =
        "UPDATE software_requests SET status = ?, requested_by = ? WHERE id = ?";

    private static final String APPROVE_REQUEST_BY_ID =
        "UPDATE software_requests SET status = 'Approved', requested_by = ? WHERE id = ?";
    
    private static final String DELETE_REQUEST = 
        "DELETE FROM software_requests WHERE computer_id = ? AND created_at = ?";
    
    private static final String SELECT_REQUEST_BY_ID = 
        "SELECT * FROM software_requests WHERE computer_id = ? ORDER BY created_at DESC LIMIT 1";
    
    private static final String SELECT_ALL_REQUESTS = 
        "SELECT * FROM software_requests ORDER BY created_at DESC";
    
    private static final String SELECT_REQUESTS_BY_STATUS = 
        "SELECT * FROM software_requests WHERE status = ? ORDER BY created_at DESC";
    
    @Override
    public boolean insert(SoftwareRequest request) {
        Connection connection = null;
        PreparedStatement statement = null;
        
        try {
            connection = getConnection();
            statement = connection.prepareStatement(INSERT_REQUEST);
            
            statement.setString(1, request.getComputerId());
            statement.setString(2, request.getSoftwareName());
            statement.setString(3, request.getVersion());
            statement.setString(4, request.getUrgency());
            statement.setString(5, request.getJustification());
            statement.setString(6, request.getStatus());
            statement.setString(7, request.getApprovedBy()); // Using approvedBy field as requester
            statement.setTimestamp(8, new Timestamp(request.getRequestDate().getTime()));
            
            int rowsAffected = statement.executeUpdate();
            boolean success = rowsAffected > 0;
            
            if (success) {
                System.out.println("✅ Software request inserted successfully: " + request.getComputerId() + " - " + request.getSoftwareName());
            } else {
                System.out.println("❌ Failed to insert software request: " + request.getComputerId());
            }
            
            return success;
            
        } catch (SQLException e) {
            System.err.println("❌ Error inserting software request: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            closeResources(connection, statement);
        }
    }
    
    @Override
    public boolean update(SoftwareRequest request) {
        Connection connection = null;
        PreparedStatement statement = null;
        
        try {
            connection = getConnection();
            statement = connection.prepareStatement(UPDATE_REQUEST);
            
            statement.setString(1, request.getSoftwareName());
            statement.setString(2, request.getVersion());
            statement.setString(3, request.getUrgency());
            statement.setString(4, request.getJustification());
            statement.setString(5, request.getStatus());
            statement.setString(6, request.getComputerId());
            statement.setTimestamp(7, new Timestamp(request.getRequestDate().getTime()));
            
            int rowsAffected = statement.executeUpdate();
            boolean success = rowsAffected > 0;
            
            if (success) {
                System.out.println("✅ Software request updated successfully: " + request.getComputerId());
            } else {
                System.out.println("❌ Failed to update software request: " + request.getComputerId());
            }
            
            return success;
            
        } catch (SQLException e) {
            System.err.println("❌ Error updating software request: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            closeResources(connection, statement);
        }
    }
    
    /**
     * Approve a software request by computer ID
     * @param computerId Computer ID
     * @param approvedBy Who approved the request
     * @return true if approval was successful
     */
    public boolean approveRequest(String computerId, String approvedBy) {
        Connection connection = null;
        PreparedStatement statement = null;

        try {
            connection = getConnection();
            statement = connection.prepareStatement(APPROVE_REQUEST);

            statement.setString(1, approvedBy);
            statement.setString(2, computerId);

            int rowsAffected = statement.executeUpdate();
            boolean success = rowsAffected > 0;

            if (success) {
                System.out.println("✅ Software request approved successfully: " + computerId + " by " + approvedBy);
            } else {
                System.out.println("❌ Failed to approve software request: " + computerId);
            }

            return success;

        } catch (SQLException e) {
            System.err.println("❌ Error approving software request: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            closeResources(connection, statement);
        }
    }

    /**
     * Legacy method for backward compatibility
     */
    public boolean approveRequest(String computerId, java.util.Date requestDate, String approvedBy) {
        return approveRequest(computerId, approvedBy);
    }
    
    /**
     * Reject a software request by computer ID
     * @param computerId Computer ID
     * @return true if rejection was successful
     */
    public boolean rejectRequest(String computerId) {
        Connection connection = null;
        PreparedStatement statement = null;

        try {
            connection = getConnection();
            statement = connection.prepareStatement(REJECT_REQUEST);

            statement.setString(1, computerId);

            int rowsAffected = statement.executeUpdate();
            boolean success = rowsAffected > 0;

            if (success) {
                System.out.println("✅ Software request rejected successfully: " + computerId);
            } else {
                System.out.println("❌ Failed to reject software request: " + computerId);
            }

            return success;

        } catch (SQLException e) {
            System.err.println("❌ Error rejecting software request: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            closeResources(connection, statement);
        }
    }

    /**
     * Legacy method for backward compatibility
     */
    public boolean rejectRequest(String computerId, java.util.Date requestDate) {
        return rejectRequest(computerId);
    }
    
    /**
     * Update request status by computer ID
     * @param computerId Computer ID
     * @param status New status
     * @param approvedBy Who changed the status (optional)
     * @return true if update was successful
     */
    public boolean updateStatus(String computerId, String status, String approvedBy) {
        Connection connection = null;
        PreparedStatement statement = null;

        try {
            connection = getConnection();
            statement = connection.prepareStatement(UPDATE_REQUEST_STATUS);

            statement.setString(1, status);
            statement.setString(2, approvedBy);
            statement.setString(3, computerId);

            int rowsAffected = statement.executeUpdate();
            boolean success = rowsAffected > 0;

            if (success) {
                System.out.println("✅ Software request status updated successfully: " + computerId + " -> " + status);
            } else {
                System.out.println("❌ Failed to update software request status: " + computerId);
            }

            return success;

        } catch (SQLException e) {
            System.err.println("❌ Error updating software request status: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            closeResources(connection, statement);
        }
    }

    /**
     * Legacy method for backward compatibility
     */
    public boolean updateStatus(String computerId, java.util.Date requestDate, String status, String approvedBy) {
        return updateStatus(computerId, status, approvedBy);
    }
    
    @Override
    public boolean delete(String computerId) {
        // For software requests, we need both computer_id and date, so this method finds the latest request
        SoftwareRequest request = findById(computerId);
        if (request == null) {
            return false;
        }
        
        Connection connection = null;
        PreparedStatement statement = null;
        
        try {
            connection = getConnection();
            statement = connection.prepareStatement(DELETE_REQUEST);
            
            statement.setString(1, computerId);
            statement.setTimestamp(2, new Timestamp(request.getRequestDate().getTime()));
            
            int rowsAffected = statement.executeUpdate();
            boolean success = rowsAffected > 0;
            
            if (success) {
                System.out.println("✅ Software request deleted successfully: " + computerId);
            } else {
                System.out.println("❌ Failed to delete software request: " + computerId);
            }
            
            return success;
            
        } catch (SQLException e) {
            System.err.println("❌ Error deleting software request: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            closeResources(connection, statement);
        }
    }
    
    @Override
    public SoftwareRequest findById(String computerId) {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        
        try {
            connection = getConnection();
            statement = connection.prepareStatement(SELECT_REQUEST_BY_ID);
            statement.setString(1, computerId);
            
            resultSet = statement.executeQuery();
            
            if (resultSet.next()) {
                return mapResultSetToSoftwareRequest(resultSet);
            }
            
            return null;
            
        } catch (SQLException e) {
            System.err.println("❌ Error finding software request by ID: " + e.getMessage());
            e.printStackTrace();
            return null;
        } finally {
            closeResources(connection, statement, resultSet);
        }
    }
    
    @Override
    public List<SoftwareRequest> findAll() {
        List<SoftwareRequest> requests = new ArrayList<>();
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        
        try {
            connection = getConnection();
            statement = connection.prepareStatement(SELECT_ALL_REQUESTS);
            resultSet = statement.executeQuery();
            
            while (resultSet.next()) {
                requests.add(mapResultSetToSoftwareRequest(resultSet));
            }
            
            System.out.println("✅ Retrieved " + requests.size() + " software requests from database");
            
        } catch (SQLException e) {
            System.err.println("❌ Error retrieving all software requests: " + e.getMessage());
            e.printStackTrace();
        } finally {
            closeResources(connection, statement, resultSet);
        }
        
        return requests;
    }
    
    /**
     * Find software requests by status
     * @param status Status to filter by
     * @return List of software requests with the specified status
     */
    public List<SoftwareRequest> findByStatus(String status) {
        List<SoftwareRequest> requests = new ArrayList<>();
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        
        try {
            connection = getConnection();
            statement = connection.prepareStatement(SELECT_REQUESTS_BY_STATUS);
            statement.setString(1, status);
            resultSet = statement.executeQuery();
            
            while (resultSet.next()) {
                requests.add(mapResultSetToSoftwareRequest(resultSet));
            }
            
            System.out.println("✅ Retrieved " + requests.size() + " software requests with status: " + status);
            
        } catch (SQLException e) {
            System.err.println("❌ Error retrieving software requests by status: " + e.getMessage());
            e.printStackTrace();
        } finally {
            closeResources(connection, statement, resultSet);
        }
        
        return requests;
    }
    
    /**
     * Map ResultSet to SoftwareRequest object
     * @param resultSet Database result set
     * @return SoftwareRequest object
     * @throws SQLException if mapping fails
     */
    private SoftwareRequest mapResultSetToSoftwareRequest(ResultSet resultSet) throws SQLException {
        return new SoftwareRequest(
            resultSet.getString("computer_id"),
            resultSet.getString("software_name"),
            resultSet.getString("version"),
            resultSet.getString("urgency"),
            resultSet.getString("justification"),
            resultSet.getTimestamp("created_at"),
            resultSet.getString("status"),
            resultSet.getString("requested_by") != null ? resultSet.getString("requested_by") : ""
        );
    }
    
    @Override
    public Connection getConnection() throws SQLException {
        return super.getConnection();
    }
}

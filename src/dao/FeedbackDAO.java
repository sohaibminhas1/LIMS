package dao;

import model.Feedback;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Feedback entity
 * Handles all database operations for feedback
 */
public class FeedbackDAO extends AbstractDAO implements BaseDAO<Feedback> {
    
    // SQL Queries
    private static final String INSERT_FEEDBACK = 
        "INSERT INTO feedback (name, category, feedback, status, created_at) " +
        "VALUES (?, ?, ?, ?, ?)";
    
    private static final String UPDATE_FEEDBACK = 
        "UPDATE feedback SET category = ?, feedback = ?, status = ? " +
        "WHERE name = ? AND created_at = ?";
    
    private static final String UPDATE_FEEDBACK_STATUS = 
        "UPDATE feedback SET status = ? WHERE id = ?";
    
    private static final String DELETE_FEEDBACK = 
        "DELETE FROM feedback WHERE id = ?";
    
    private static final String SELECT_FEEDBACK_BY_ID = 
        "SELECT * FROM feedback WHERE id = ?";
    
    private static final String SELECT_FEEDBACK_BY_NAME = 
        "SELECT * FROM feedback WHERE name = ? ORDER BY created_at DESC LIMIT 1";
    
    private static final String SELECT_ALL_FEEDBACK = 
        "SELECT * FROM feedback ORDER BY created_at DESC";
    
    private static final String SELECT_FEEDBACK_BY_STATUS = 
        "SELECT * FROM feedback WHERE status = ? ORDER BY created_at DESC";
    
    private static final String SELECT_FEEDBACK_BY_CATEGORY = 
        "SELECT * FROM feedback WHERE category = ? ORDER BY created_at DESC";
    
    @Override
    public boolean insert(Feedback feedback) {
        Connection connection = null;
        PreparedStatement statement = null;
        
        try {
            connection = getConnection();
            statement = connection.prepareStatement(INSERT_FEEDBACK);
            
            statement.setString(1, feedback.getName());
            statement.setString(2, feedback.getCategory());
            statement.setString(3, feedback.getContent());
            statement.setString(4, feedback.getStatus());
            statement.setTimestamp(5, new Timestamp(feedback.getSubmissionDate().getTime()));
            
            int rowsAffected = statement.executeUpdate();
            boolean success = rowsAffected > 0;
            
            if (success) {
                System.out.println("✅ Feedback inserted successfully: " + feedback.getName() + " - " + feedback.getCategory());
            } else {
                System.out.println("❌ Failed to insert feedback: " + feedback.getName());
            }
            
            return success;
            
        } catch (SQLException e) {
            System.err.println("❌ Error inserting feedback: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            closeResources(connection, statement);
        }
    }
    
    @Override
    public boolean update(Feedback feedback) {
        Connection connection = null;
        PreparedStatement statement = null;
        
        try {
            connection = getConnection();
            statement = connection.prepareStatement(UPDATE_FEEDBACK);
            
            statement.setString(1, feedback.getCategory());
            statement.setString(2, feedback.getContent());
            statement.setString(3, feedback.getStatus());
            statement.setString(4, feedback.getName());
            statement.setTimestamp(5, new Timestamp(feedback.getSubmissionDate().getTime()));
            
            int rowsAffected = statement.executeUpdate();
            boolean success = rowsAffected > 0;
            
            if (success) {
                System.out.println("✅ Feedback updated successfully: " + feedback.getName());
            } else {
                System.out.println("❌ Failed to update feedback: " + feedback.getName());
            }
            
            return success;
            
        } catch (SQLException e) {
            System.err.println("❌ Error updating feedback: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            closeResources(connection, statement);
        }
    }
    
    /**
     * Update feedback status by ID
     * @param feedbackId Feedback ID
     * @param status New status
     * @return true if update was successful
     */
    public boolean updateStatus(int feedbackId, String status) {
        Connection connection = null;
        PreparedStatement statement = null;
        
        try {
            connection = getConnection();
            statement = connection.prepareStatement(UPDATE_FEEDBACK_STATUS);
            
            statement.setString(1, status);
            statement.setInt(2, feedbackId);
            
            int rowsAffected = statement.executeUpdate();
            boolean success = rowsAffected > 0;
            
            if (success) {
                System.out.println("✅ Feedback status updated successfully: ID " + feedbackId + " -> " + status);
            } else {
                System.out.println("❌ Failed to update feedback status: ID " + feedbackId);
            }
            
            return success;
            
        } catch (SQLException e) {
            System.err.println("❌ Error updating feedback status: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            closeResources(connection, statement);
        }
    }
    
    @Override
    public boolean delete(String id) {
        Connection connection = null;
        PreparedStatement statement = null;
        
        try {
            connection = getConnection();
            statement = connection.prepareStatement(DELETE_FEEDBACK);
            statement.setInt(1, Integer.parseInt(id));
            
            int rowsAffected = statement.executeUpdate();
            boolean success = rowsAffected > 0;
            
            if (success) {
                System.out.println("✅ Feedback deleted successfully: ID " + id);
            } else {
                System.out.println("❌ Failed to delete feedback: ID " + id);
            }
            
            return success;
            
        } catch (SQLException | NumberFormatException e) {
            System.err.println("❌ Error deleting feedback: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            closeResources(connection, statement);
        }
    }
    
    @Override
    public Feedback findById(String id) {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        
        try {
            connection = getConnection();
            statement = connection.prepareStatement(SELECT_FEEDBACK_BY_ID);
            statement.setInt(1, Integer.parseInt(id));
            
            resultSet = statement.executeQuery();
            
            if (resultSet.next()) {
                return mapResultSetToFeedback(resultSet);
            }
            
            return null;
            
        } catch (SQLException | NumberFormatException e) {
            System.err.println("❌ Error finding feedback by ID: " + e.getMessage());
            e.printStackTrace();
            return null;
        } finally {
            closeResources(connection, statement, resultSet);
        }
    }
    
    /**
     * Find feedback by name (latest entry)
     * @param name Name to search for
     * @return Latest feedback from the specified name
     */
    public Feedback findByName(String name) {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        
        try {
            connection = getConnection();
            statement = connection.prepareStatement(SELECT_FEEDBACK_BY_NAME);
            statement.setString(1, name);
            
            resultSet = statement.executeQuery();
            
            if (resultSet.next()) {
                return mapResultSetToFeedback(resultSet);
            }
            
            return null;
            
        } catch (SQLException e) {
            System.err.println("❌ Error finding feedback by name: " + e.getMessage());
            e.printStackTrace();
            return null;
        } finally {
            closeResources(connection, statement, resultSet);
        }
    }
    
    @Override
    public List<Feedback> findAll() {
        List<Feedback> feedbackList = new ArrayList<>();
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        
        try {
            connection = getConnection();
            statement = connection.prepareStatement(SELECT_ALL_FEEDBACK);
            resultSet = statement.executeQuery();
            
            while (resultSet.next()) {
                feedbackList.add(mapResultSetToFeedback(resultSet));
            }
            
            System.out.println("✅ Retrieved " + feedbackList.size() + " feedback entries from database");
            
        } catch (SQLException e) {
            System.err.println("❌ Error retrieving all feedback: " + e.getMessage());
            e.printStackTrace();
        } finally {
            closeResources(connection, statement, resultSet);
        }
        
        return feedbackList;
    }
    
    /**
     * Find feedback by status
     * @param status Status to filter by
     * @return List of feedback with the specified status
     */
    public List<Feedback> findByStatus(String status) {
        List<Feedback> feedbackList = new ArrayList<>();
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        
        try {
            connection = getConnection();
            statement = connection.prepareStatement(SELECT_FEEDBACK_BY_STATUS);
            statement.setString(1, status);
            resultSet = statement.executeQuery();
            
            while (resultSet.next()) {
                feedbackList.add(mapResultSetToFeedback(resultSet));
            }
            
            System.out.println("✅ Retrieved " + feedbackList.size() + " feedback entries with status: " + status);
            
        } catch (SQLException e) {
            System.err.println("❌ Error retrieving feedback by status: " + e.getMessage());
            e.printStackTrace();
        } finally {
            closeResources(connection, statement, resultSet);
        }
        
        return feedbackList;
    }
    
    /**
     * Find feedback by category
     * @param category Category to filter by
     * @return List of feedback with the specified category
     */
    public List<Feedback> findByCategory(String category) {
        List<Feedback> feedbackList = new ArrayList<>();
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        
        try {
            connection = getConnection();
            statement = connection.prepareStatement(SELECT_FEEDBACK_BY_CATEGORY);
            statement.setString(1, category);
            resultSet = statement.executeQuery();
            
            while (resultSet.next()) {
                feedbackList.add(mapResultSetToFeedback(resultSet));
            }
            
            System.out.println("✅ Retrieved " + feedbackList.size() + " feedback entries with category: " + category);
            
        } catch (SQLException e) {
            System.err.println("❌ Error retrieving feedback by category: " + e.getMessage());
            e.printStackTrace();
        } finally {
            closeResources(connection, statement, resultSet);
        }
        
        return feedbackList;
    }
    
    /**
     * Map ResultSet to Feedback object
     * @param resultSet Database result set
     * @return Feedback object
     * @throws SQLException if mapping fails
     */
    private Feedback mapResultSetToFeedback(ResultSet resultSet) throws SQLException {
        return new Feedback(
            resultSet.getString("name"),
            resultSet.getString("category"),
            resultSet.getString("feedback"),
            resultSet.getTimestamp("created_at"),
            resultSet.getString("status"),
            "" // response column doesn't exist in current schema
        );
    }
    
    @Override
    public Connection getConnection() throws SQLException {
        return super.getConnection();
    }
}

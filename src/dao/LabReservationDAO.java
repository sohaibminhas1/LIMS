package dao;

import model.LabReservation;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for LabReservation entity
 * Handles all database operations for lab reservations
 */
public class LabReservationDAO extends AbstractDAO implements BaseDAO<LabReservation> {
    
    // SQL Queries
    private static final String INSERT_RESERVATION = 
        "INSERT INTO lab_reservations (requester_name, lab_name, reservation_date, time_slot, purpose, status, created_at) " +
        "VALUES (?, ?, ?, ?, ?, ?, ?)";
    
    private static final String UPDATE_RESERVATION = 
        "UPDATE lab_reservations SET lab_name = ?, reservation_date = ?, time_slot = ?, purpose = ?, status = ? " +
        "WHERE requester_name = ? AND created_at = ?";
    
    private static final String UPDATE_RESERVATION_STATUS = 
        "UPDATE lab_reservations SET status = ? WHERE id = ?";
    
    private static final String DELETE_RESERVATION = 
        "DELETE FROM lab_reservations WHERE id = ?";
    
    private static final String SELECT_RESERVATION_BY_ID = 
        "SELECT * FROM lab_reservations WHERE id = ?";
    
    private static final String SELECT_RESERVATION_BY_NAME = 
        "SELECT * FROM lab_reservations WHERE requester_name = ? ORDER BY created_at DESC LIMIT 1";
    
    private static final String SELECT_ALL_RESERVATIONS = 
        "SELECT * FROM lab_reservations ORDER BY created_at DESC";
    
    private static final String SELECT_RESERVATIONS_BY_STATUS = 
        "SELECT * FROM lab_reservations WHERE status = ? ORDER BY created_at DESC";
    
    private static final String SELECT_RESERVATIONS_BY_LAB = 
        "SELECT * FROM lab_reservations WHERE lab_name = ? ORDER BY reservation_date DESC";
    
    private static final String SELECT_RESERVATIONS_BY_DATE = 
        "SELECT * FROM lab_reservations WHERE reservation_date = ? ORDER BY time_slot";
    
    @Override
    public boolean insert(LabReservation reservation) {
        Connection connection = null;
        PreparedStatement statement = null;
        
        try {
            connection = getConnection();
            statement = connection.prepareStatement(INSERT_RESERVATION);
            
            statement.setString(1, reservation.getReservedBy());
            statement.setString(2, reservation.getLab());
            statement.setDate(3, new java.sql.Date(reservation.getDate().getTime()));
            statement.setString(4, reservation.getTimeSlot());
            statement.setString(5, reservation.getPurpose());
            statement.setString(6, "Pending"); // Default status
            statement.setTimestamp(7, new Timestamp(new java.util.Date().getTime()));
            
            int rowsAffected = statement.executeUpdate();
            boolean success = rowsAffected > 0;
            
            if (success) {
                System.out.println("✅ Lab reservation inserted successfully: " + reservation.getReservedBy() + " - " + reservation.getLab());
            } else {
                System.out.println("❌ Failed to insert lab reservation: " + reservation.getReservedBy());
            }
            
            return success;
            
        } catch (SQLException e) {
            System.err.println("❌ Error inserting lab reservation: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            closeResources(connection, statement);
        }
    }
    
    @Override
    public boolean update(LabReservation reservation) {
        Connection connection = null;
        PreparedStatement statement = null;
        
        try {
            connection = getConnection();
            statement = connection.prepareStatement(UPDATE_RESERVATION);
            
            statement.setString(1, reservation.getLab());
            statement.setDate(2, new java.sql.Date(reservation.getDate().getTime()));
            statement.setString(3, reservation.getTimeSlot());
            statement.setString(4, reservation.getPurpose());
            statement.setString(5, "Pending"); // Default status
            statement.setString(6, reservation.getReservedBy());
            statement.setTimestamp(7, new Timestamp(new java.util.Date().getTime()));
            
            int rowsAffected = statement.executeUpdate();
            boolean success = rowsAffected > 0;
            
            if (success) {
                System.out.println("✅ Lab reservation updated successfully: " + reservation.getReservedBy());
            } else {
                System.out.println("❌ Failed to update lab reservation: " + reservation.getReservedBy());
            }
            
            return success;
            
        } catch (SQLException e) {
            System.err.println("❌ Error updating lab reservation: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            closeResources(connection, statement);
        }
    }
    
    /**
     * Update reservation status by ID
     * @param reservationId Reservation ID
     * @param status New status
     * @return true if update was successful
     */
    public boolean updateStatus(int reservationId, String status) {
        Connection connection = null;
        PreparedStatement statement = null;
        
        try {
            connection = getConnection();
            statement = connection.prepareStatement(UPDATE_RESERVATION_STATUS);
            
            statement.setString(1, status);
            statement.setInt(2, reservationId);
            
            int rowsAffected = statement.executeUpdate();
            boolean success = rowsAffected > 0;
            
            if (success) {
                System.out.println("✅ Lab reservation status updated successfully: ID " + reservationId + " -> " + status);
            } else {
                System.out.println("❌ Failed to update lab reservation status: ID " + reservationId);
            }
            
            return success;
            
        } catch (SQLException e) {
            System.err.println("❌ Error updating lab reservation status: " + e.getMessage());
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
            statement = connection.prepareStatement(DELETE_RESERVATION);
            statement.setInt(1, Integer.parseInt(id));
            
            int rowsAffected = statement.executeUpdate();
            boolean success = rowsAffected > 0;
            
            if (success) {
                System.out.println("✅ Lab reservation deleted successfully: ID " + id);
            } else {
                System.out.println("❌ Failed to delete lab reservation: ID " + id);
            }
            
            return success;
            
        } catch (SQLException | NumberFormatException e) {
            System.err.println("❌ Error deleting lab reservation: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            closeResources(connection, statement);
        }
    }
    
    @Override
    public LabReservation findById(String id) {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        
        try {
            connection = getConnection();
            statement = connection.prepareStatement(SELECT_RESERVATION_BY_ID);
            statement.setInt(1, Integer.parseInt(id));
            
            resultSet = statement.executeQuery();
            
            if (resultSet.next()) {
                return mapResultSetToLabReservation(resultSet);
            }
            
            return null;
            
        } catch (SQLException | NumberFormatException e) {
            System.err.println("❌ Error finding lab reservation by ID: " + e.getMessage());
            e.printStackTrace();
            return null;
        } finally {
            closeResources(connection, statement, resultSet);
        }
    }
    
    /**
     * Find reservation by requester name (latest entry)
     * @param requesterName Name to search for
     * @return Latest reservation from the specified requester
     */
    public LabReservation findByRequesterName(String requesterName) {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        
        try {
            connection = getConnection();
            statement = connection.prepareStatement(SELECT_RESERVATION_BY_NAME);
            statement.setString(1, requesterName);
            
            resultSet = statement.executeQuery();
            
            if (resultSet.next()) {
                return mapResultSetToLabReservation(resultSet);
            }
            
            return null;
            
        } catch (SQLException e) {
            System.err.println("❌ Error finding lab reservation by requester name: " + e.getMessage());
            e.printStackTrace();
            return null;
        } finally {
            closeResources(connection, statement, resultSet);
        }
    }
    
    @Override
    public List<LabReservation> findAll() {
        List<LabReservation> reservations = new ArrayList<>();
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        
        try {
            connection = getConnection();
            statement = connection.prepareStatement(SELECT_ALL_RESERVATIONS);
            resultSet = statement.executeQuery();
            
            while (resultSet.next()) {
                reservations.add(mapResultSetToLabReservation(resultSet));
            }
            
            System.out.println("✅ Retrieved " + reservations.size() + " lab reservations from database");
            
        } catch (SQLException e) {
            System.err.println("❌ Error retrieving all lab reservations: " + e.getMessage());
            e.printStackTrace();
        } finally {
            closeResources(connection, statement, resultSet);
        }
        
        return reservations;
    }
    
    /**
     * Find reservations by status
     * @param status Status to filter by
     * @return List of reservations with the specified status
     */
    public List<LabReservation> findByStatus(String status) {
        List<LabReservation> reservations = new ArrayList<>();
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        
        try {
            connection = getConnection();
            statement = connection.prepareStatement(SELECT_RESERVATIONS_BY_STATUS);
            statement.setString(1, status);
            resultSet = statement.executeQuery();
            
            while (resultSet.next()) {
                reservations.add(mapResultSetToLabReservation(resultSet));
            }
            
            System.out.println("✅ Retrieved " + reservations.size() + " lab reservations with status: " + status);
            
        } catch (SQLException e) {
            System.err.println("❌ Error retrieving lab reservations by status: " + e.getMessage());
            e.printStackTrace();
        } finally {
            closeResources(connection, statement, resultSet);
        }
        
        return reservations;
    }
    
    /**
     * Find reservations by lab name
     * @param labName Lab name to filter by
     * @return List of reservations for the specified lab
     */
    public List<LabReservation> findByLabName(String labName) {
        List<LabReservation> reservations = new ArrayList<>();
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        
        try {
            connection = getConnection();
            statement = connection.prepareStatement(SELECT_RESERVATIONS_BY_LAB);
            statement.setString(1, labName);
            resultSet = statement.executeQuery();
            
            while (resultSet.next()) {
                reservations.add(mapResultSetToLabReservation(resultSet));
            }
            
            System.out.println("✅ Retrieved " + reservations.size() + " lab reservations for lab: " + labName);
            
        } catch (SQLException e) {
            System.err.println("❌ Error retrieving lab reservations by lab name: " + e.getMessage());
            e.printStackTrace();
        } finally {
            closeResources(connection, statement, resultSet);
        }
        
        return reservations;
    }
    
    /**
     * Find reservations by date (String format)
     * @param date Date to filter by (String format)
     * @return List of reservations for the specified date
     */
    public List<LabReservation> findByDate(String date) {
        List<LabReservation> reservations = new ArrayList<>();
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            connection = getConnection();
            statement = connection.prepareStatement(SELECT_RESERVATIONS_BY_DATE);
            statement.setString(1, date);
            resultSet = statement.executeQuery();

            while (resultSet.next()) {
                reservations.add(mapResultSetToLabReservation(resultSet));
            }

            System.out.println("✅ Retrieved " + reservations.size() + " lab reservations for date: " + date);

        } catch (SQLException e) {
            System.err.println("❌ Error retrieving lab reservations by date: " + e.getMessage());
            e.printStackTrace();
        } finally {
            closeResources(connection, statement, resultSet);
        }

        return reservations;
    }

    /**
     * Find reservations by date (Date object)
     * @param date Date to filter by (Date object)
     * @return List of reservations for the specified date
     */
    public List<LabReservation> findByDate(java.util.Date date) {
        List<LabReservation> reservations = new ArrayList<>();
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            connection = getConnection();
            statement = connection.prepareStatement(SELECT_RESERVATIONS_BY_DATE);
            statement.setDate(1, new java.sql.Date(date.getTime()));
            resultSet = statement.executeQuery();

            while (resultSet.next()) {
                reservations.add(mapResultSetToLabReservation(resultSet));
            }

            System.out.println("✅ Retrieved " + reservations.size() + " lab reservations for date: " + date);

        } catch (SQLException e) {
            System.err.println("❌ Error retrieving lab reservations by date: " + e.getMessage());
            e.printStackTrace();
        } finally {
            closeResources(connection, statement, resultSet);
        }

        return reservations;
    }
    
    /**
     * Map ResultSet to LabReservation object
     * @param resultSet Database result set
     * @return LabReservation object
     * @throws SQLException if mapping fails
     */
    private LabReservation mapResultSetToLabReservation(ResultSet resultSet) throws SQLException {
        return new LabReservation(
            resultSet.getString("lab_name"),
            resultSet.getDate("reservation_date"),
            resultSet.getString("time_slot"),
            resultSet.getString("requester_name"),
            resultSet.getString("purpose"),
            resultSet.getString("purpose"), // course - using purpose for now
            resultSet.getString("requester_name") // instructor - using requester for now
        );
    }
    
    @Override
    public Connection getConnection() throws SQLException {
        return super.getConnection();
    }
}

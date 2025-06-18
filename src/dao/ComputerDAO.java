package dao;

import model.Computer;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Computer entity
 * Handles all database operations for computer inventory
 */
public class ComputerDAO extends AbstractDAO implements BaseDAO<Computer> {
    
    // SQL Queries
    private static final String INSERT_COMPUTER =
        "INSERT INTO computers (computer_id, lab_id, computer_name, ip_address, specifications, status, install_date, notes, created_at) " +
        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?) " +
        "ON CONFLICT (computer_id) DO UPDATE SET " +
        "lab_id = EXCLUDED.lab_id, computer_name = EXCLUDED.computer_name, ip_address = EXCLUDED.ip_address, " +
        "specifications = EXCLUDED.specifications, status = EXCLUDED.status, install_date = EXCLUDED.install_date, " +
        "notes = EXCLUDED.notes";

    private static final String INSERT_COMPUTER_SIMPLE =
        "INSERT INTO computers (computer_id, lab_id, computer_name, ip_address, specifications, status, install_date, notes, created_at) " +
        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

    private static final String UPDATE_COMPUTER =
        "UPDATE computers SET lab_id = ?, computer_name = ?, ip_address = ?, specifications = ?, status = ?, install_date = ?, notes = ? " +
        "WHERE computer_id = ?";

    private static final String UPDATE_COMPUTER_STATUS =
        "UPDATE computers SET status = ? WHERE computer_id = ?";

    private static final String DELETE_COMPUTER =
        "DELETE FROM computers WHERE computer_id = ?";

    private static final String SELECT_COMPUTER_BY_ID =
        "SELECT * FROM computers WHERE computer_id = ?";

    private static final String SELECT_ALL_COMPUTERS =
        "SELECT * FROM computers ORDER BY computer_name";

    private static final String SELECT_COMPUTERS_BY_STATUS =
        "SELECT * FROM computers WHERE status = ? ORDER BY computer_name";

    private static final String SELECT_COMPUTERS_BY_LAB =
        "SELECT * FROM computers WHERE lab_id = ? ORDER BY computer_name";

    private static final String SELECT_AVAILABLE_COMPUTERS =
        "SELECT * FROM computers WHERE status = 'Available' ORDER BY computer_name";

    private static final String CHECK_COMPUTER_EXISTS =
        "SELECT COUNT(*) FROM computers WHERE computer_id = ?";
    
    @Override
    public boolean insert(Computer computer) {
        // First check if computer already exists
        if (computerExists(computer.getId())) {
            System.out.println("⚠️ Computer already exists, updating instead: " + computer.getId());
            return update(computer);
        }

        Connection connection = null;
        PreparedStatement statement = null;

        try {
            connection = getConnection();
            statement = connection.prepareStatement(INSERT_COMPUTER_SIMPLE);

            statement.setString(1, computer.getId());
            statement.setInt(2, 1); // Default lab_id since model doesn't have labId as int
            statement.setString(3, computer.getLocation()); // Using location as computer_name
            statement.setString(4, computer.getIpAddress());
            statement.setString(5, computer.getSpecifications());
            statement.setString(6, computer.getStatus());
            statement.setString(7, computer.getInstallDate());
            statement.setString(8, computer.getNotes());
            statement.setTimestamp(9, new Timestamp(new java.util.Date().getTime()));

            int rowsAffected = statement.executeUpdate();
            boolean success = rowsAffected > 0;

            if (success) {
                System.out.println("✅ Computer inserted successfully: " + computer.getId() + " - " + computer.getLocation());
            } else {
                System.out.println("❌ Failed to insert computer: " + computer.getId());
            }

            return success;

        } catch (SQLException e) {
            // Handle duplicate key constraint
            if (e.getMessage().contains("duplicate key") || e.getMessage().contains("unique constraint")) {
                System.out.println("⚠️ Computer already exists, attempting update: " + computer.getId());
                return update(computer);
            }

            System.err.println("❌ Error inserting computer: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            closeResources(connection, statement);
        }
    }

    /**
     * Insert or update computer (upsert operation)
     * @param computer Computer to insert or update
     * @return true if operation was successful
     */
    public boolean insertOrUpdate(Computer computer) {
        Connection connection = null;
        PreparedStatement statement = null;

        try {
            connection = getConnection();
            statement = connection.prepareStatement(INSERT_COMPUTER);

            statement.setString(1, computer.getId());
            statement.setInt(2, 1); // Default lab_id since model doesn't have labId as int
            statement.setString(3, computer.getLocation()); // Using location as computer_name
            statement.setString(4, computer.getIpAddress());
            statement.setString(5, computer.getSpecifications());
            statement.setString(6, computer.getStatus());
            statement.setString(7, computer.getInstallDate());
            statement.setString(8, computer.getNotes());
            statement.setTimestamp(9, new Timestamp(new java.util.Date().getTime()));

            int rowsAffected = statement.executeUpdate();
            boolean success = rowsAffected > 0;

            if (success) {
                System.out.println("✅ Computer inserted/updated successfully: " + computer.getId() + " - " + computer.getLocation());
            } else {
                System.out.println("❌ Failed to insert/update computer: " + computer.getId());
            }

            return success;

        } catch (SQLException e) {
            System.err.println("❌ Error inserting/updating computer: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            closeResources(connection, statement);
        }
    }

    /**
     * Check if computer exists
     * @param computerId Computer ID to check
     * @return true if computer exists
     */
    private boolean computerExists(String computerId) {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            connection = getConnection();
            statement = connection.prepareStatement(CHECK_COMPUTER_EXISTS);
            statement.setString(1, computerId);
            resultSet = statement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getInt(1) > 0;
            }

            return false;

        } catch (SQLException e) {
            System.err.println("❌ Error checking if computer exists: " + e.getMessage());
            return false;
        } finally {
            closeResources(connection, statement, resultSet);
        }
    }
    
    @Override
    public boolean update(Computer computer) {
        Connection connection = null;
        PreparedStatement statement = null;
        
        try {
            connection = getConnection();
            statement = connection.prepareStatement(UPDATE_COMPUTER);
            
            statement.setInt(1, 1); // Default lab_id since model doesn't have labId as int
            statement.setString(2, computer.getLocation()); // Using location as computer_name
            statement.setString(3, computer.getIpAddress());
            statement.setString(4, computer.getSpecifications());
            statement.setString(5, computer.getStatus());
            statement.setString(6, computer.getInstallDate());
            statement.setString(7, computer.getNotes());
            statement.setString(8, computer.getId());
            
            int rowsAffected = statement.executeUpdate();
            boolean success = rowsAffected > 0;
            
            if (success) {
                System.out.println("✅ Computer updated successfully: " + computer.getId());
            } else {
                System.out.println("❌ Failed to update computer: " + computer.getId());
            }
            
            return success;
            
        } catch (SQLException e) {
            System.err.println("❌ Error updating computer: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            closeResources(connection, statement);
        }
    }
    
    /**
     * Update computer status
     * @param computerId Computer ID
     * @param status New status
     * @return true if update was successful
     */
    public boolean updateStatus(String computerId, String status) {
        Connection connection = null;
        PreparedStatement statement = null;
        
        try {
            connection = getConnection();
            statement = connection.prepareStatement(UPDATE_COMPUTER_STATUS);
            
            statement.setString(1, status);
            statement.setString(2, computerId);
            
            int rowsAffected = statement.executeUpdate();
            boolean success = rowsAffected > 0;
            
            if (success) {
                System.out.println("✅ Computer status updated successfully: " + computerId + " -> " + status);
            } else {
                System.out.println("❌ Failed to update computer status: " + computerId);
            }
            
            return success;
            
        } catch (SQLException e) {
            System.err.println("❌ Error updating computer status: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            closeResources(connection, statement);
        }
    }
    
    @Override
    public boolean delete(String computerId) {
        Connection connection = null;
        PreparedStatement statement = null;
        
        try {
            connection = getConnection();
            statement = connection.prepareStatement(DELETE_COMPUTER);
            statement.setString(1, computerId);
            
            int rowsAffected = statement.executeUpdate();
            boolean success = rowsAffected > 0;
            
            if (success) {
                System.out.println("✅ Computer deleted successfully: " + computerId);
            } else {
                System.out.println("❌ Failed to delete computer: " + computerId);
            }
            
            return success;
            
        } catch (SQLException e) {
            System.err.println("❌ Error deleting computer: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            closeResources(connection, statement);
        }
    }
    
    @Override
    public Computer findById(String computerId) {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        
        try {
            connection = getConnection();
            statement = connection.prepareStatement(SELECT_COMPUTER_BY_ID);
            statement.setString(1, computerId);
            
            resultSet = statement.executeQuery();
            
            if (resultSet.next()) {
                return mapResultSetToComputer(resultSet);
            }
            
            return null;
            
        } catch (SQLException e) {
            System.err.println("❌ Error finding computer by ID: " + e.getMessage());
            e.printStackTrace();
            return null;
        } finally {
            closeResources(connection, statement, resultSet);
        }
    }
    
    @Override
    public List<Computer> findAll() {
        List<Computer> computers = new ArrayList<>();
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        
        try {
            connection = getConnection();
            statement = connection.prepareStatement(SELECT_ALL_COMPUTERS);
            resultSet = statement.executeQuery();
            
            while (resultSet.next()) {
                computers.add(mapResultSetToComputer(resultSet));
            }
            
            System.out.println("✅ Retrieved " + computers.size() + " computers from database");
            
        } catch (SQLException e) {
            System.err.println("❌ Error retrieving all computers: " + e.getMessage());
            e.printStackTrace();
        } finally {
            closeResources(connection, statement, resultSet);
        }
        
        return computers;
    }
    
    /**
     * Find computers by status
     * @param status Status to filter by
     * @return List of computers with the specified status
     */
    public List<Computer> findByStatus(String status) {
        List<Computer> computers = new ArrayList<>();
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        
        try {
            connection = getConnection();
            statement = connection.prepareStatement(SELECT_COMPUTERS_BY_STATUS);
            statement.setString(1, status);
            resultSet = statement.executeQuery();
            
            while (resultSet.next()) {
                computers.add(mapResultSetToComputer(resultSet));
            }
            
            System.out.println("✅ Retrieved " + computers.size() + " computers with status: " + status);
            
        } catch (SQLException e) {
            System.err.println("❌ Error retrieving computers by status: " + e.getMessage());
            e.printStackTrace();
        } finally {
            closeResources(connection, statement, resultSet);
        }
        
        return computers;
    }
    
    /**
     * Find computers by lab ID
     * @param labId Lab ID to filter by
     * @return List of computers in the specified lab
     */
    public List<Computer> findByLabId(int labId) {
        List<Computer> computers = new ArrayList<>();
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        
        try {
            connection = getConnection();
            statement = connection.prepareStatement(SELECT_COMPUTERS_BY_LAB);
            statement.setInt(1, labId);
            resultSet = statement.executeQuery();
            
            while (resultSet.next()) {
                computers.add(mapResultSetToComputer(resultSet));
            }
            
            System.out.println("✅ Retrieved " + computers.size() + " computers for lab ID: " + labId);
            
        } catch (SQLException e) {
            System.err.println("❌ Error retrieving computers by lab ID: " + e.getMessage());
            e.printStackTrace();
        } finally {
            closeResources(connection, statement, resultSet);
        }
        
        return computers;
    }
    
    /**
     * Find available computers
     * @return List of available computers
     */
    public List<Computer> findAvailableComputers() {
        List<Computer> computers = new ArrayList<>();
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        
        try {
            connection = getConnection();
            statement = connection.prepareStatement(SELECT_AVAILABLE_COMPUTERS);
            resultSet = statement.executeQuery();
            
            while (resultSet.next()) {
                computers.add(mapResultSetToComputer(resultSet));
            }
            
            System.out.println("✅ Retrieved " + computers.size() + " available computers");
            
        } catch (SQLException e) {
            System.err.println("❌ Error retrieving available computers: " + e.getMessage());
            e.printStackTrace();
        } finally {
            closeResources(connection, statement, resultSet);
        }
        
        return computers;
    }
    
    /**
     * Map ResultSet to Computer object
     * @param resultSet Database result set
     * @return Computer object
     * @throws SQLException if mapping fails
     */
    private Computer mapResultSetToComputer(ResultSet resultSet) throws SQLException {
        return new Computer(
            resultSet.getString("computer_id"),
            "Lab " + resultSet.getInt("lab_id"), // Convert lab_id to lab name
            resultSet.getString("computer_name"),
            resultSet.getString("ip_address"),
            resultSet.getString("specifications"),
            resultSet.getString("status"),
            resultSet.getString("install_date"),
            resultSet.getString("notes") != null ? resultSet.getString("notes") : ""
        );
    }
    
    @Override
    public Connection getConnection() throws SQLException {
        return super.getConnection();
    }
}

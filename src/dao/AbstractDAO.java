package dao;

import utils.DAOLogger;
import java.sql.*;

/**
 * Abstract base class for all DAO implementations
 * Provides common database connection and utility methods
 */
public abstract class AbstractDAO {

    // Database connection parameters
    protected static final String URL = "jdbc:postgresql://localhost:5434/lims_db";
    protected static final String USER = "postgres";
    protected static final String PASSWORD = "superadmin";

    protected final String className = this.getClass().getSimpleName();
    
    /**
     * Get database connection
     * @return Database connection
     * @throws SQLException if connection fails
     */
    protected Connection getConnection() throws SQLException {
        try {
            // Load PostgreSQL driver
            Class.forName("org.postgresql.Driver");
            Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
            DAOLogger.debug(className, "getConnection", "Database connection established successfully");
            return connection;
        } catch (ClassNotFoundException e) {
            DAOLogger.error(className, "getConnection", "PostgreSQL driver not found", e);
            throw new SQLException("PostgreSQL driver not found", e);
        } catch (SQLException e) {
            DAOLogger.error(className, "getConnection", "Failed to establish database connection", e);
            throw e;
        }
    }
    
    /**
     * Close database resources safely
     * @param connection Database connection
     * @param statement SQL statement
     * @param resultSet Result set
     */
    protected void closeResources(Connection connection, Statement statement, ResultSet resultSet) {
        try {
            if (resultSet != null) {
                resultSet.close();
                DAOLogger.debug(className, "closeResources", "ResultSet closed successfully");
            }
        } catch (SQLException e) {
            DAOLogger.error(className, "closeResources", "Error closing ResultSet", e);
        }

        try {
            if (statement != null) {
                statement.close();
                DAOLogger.debug(className, "closeResources", "Statement closed successfully");
            }
        } catch (SQLException e) {
            DAOLogger.error(className, "closeResources", "Error closing Statement", e);
        }

        try {
            if (connection != null) {
                connection.close();
                DAOLogger.debug(className, "closeResources", "Connection closed successfully");
            }
        } catch (SQLException e) {
            DAOLogger.error(className, "closeResources", "Error closing Connection", e);
        }
    }
    
    /**
     * Close database resources safely (without ResultSet)
     * @param connection Database connection
     * @param statement SQL statement
     */
    protected void closeResources(Connection connection, Statement statement) {
        closeResources(connection, statement, null);
    }
    
    /**
     * Execute a query and return the number of affected rows
     * @param query SQL query
     * @param parameters Query parameters
     * @return Number of affected rows
     */
    protected int executeUpdate(String query, Object... parameters) {
        Connection connection = null;
        PreparedStatement statement = null;
        long startTime = System.currentTimeMillis();

        try {
            DAOLogger.logDatabaseOperation(className, "executeUpdate", "query", query);

            connection = getConnection();
            statement = connection.prepareStatement(query);

            // Set parameters
            for (int i = 0; i < parameters.length; i++) {
                statement.setObject(i + 1, parameters[i]);
                DAOLogger.debug(className, "executeUpdate",
                    String.format("Parameter %d: %s", i + 1, parameters[i]));
            }

            int result = statement.executeUpdate();
            long executionTime = System.currentTimeMillis() - startTime;

            DAOLogger.logDatabaseSuccess(className, "executeUpdate", "query", result);
            DAOLogger.logPerformance(className, "executeUpdate", executionTime);

            return result;

        } catch (SQLException e) {
            long executionTime = System.currentTimeMillis() - startTime;
            DAOLogger.logDatabaseFailure(className, "executeUpdate", "query", e.getMessage());
            DAOLogger.logPerformance(className, "executeUpdate", executionTime);
            DAOLogger.error(className, "executeUpdate", "Error executing update query", e);
            return 0;
        } finally {
            closeResources(connection, statement);
        }
    }
    
    /**
     * Test database connection
     * @return true if connection is successful, false otherwise
     */
    public boolean testConnection() {
        try (Connection connection = getConnection()) {
            boolean connected = connection != null && !connection.isClosed();
            DAOLogger.logConnectionStatus(className, connected);
            return connected;
        } catch (SQLException e) {
            DAOLogger.logConnectionStatus(className, false);
            DAOLogger.error(className, "testConnection", "Database connection test failed", e);
            return false;
        }
    }

    /**
     * Validate required field
     * @param fieldName Field name
     * @param value Field value
     * @param methodName Method name for logging
     * @return true if valid, false otherwise
     */
    protected boolean validateRequired(String fieldName, Object value, String methodName) {
        if (value == null || (value instanceof String && ((String) value).trim().isEmpty())) {
            DAOLogger.logValidationError(className, methodName, fieldName,
                String.valueOf(value), "Field is required but was null or empty");
            return false;
        }
        return true;
    }

    /**
     * Validate string length
     * @param fieldName Field name
     * @param value Field value
     * @param maxLength Maximum allowed length
     * @param methodName Method name for logging
     * @return true if valid, false otherwise
     */
    protected boolean validateLength(String fieldName, String value, int maxLength, String methodName) {
        if (value != null && value.length() > maxLength) {
            DAOLogger.logValidationError(className, methodName, fieldName, value,
                String.format("Length exceeds maximum of %d characters", maxLength));
            return false;
        }
        return true;
    }
}

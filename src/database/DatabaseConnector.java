package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseConnector {
    private static final String URL = "jdbc:postgresql://localhost:5434/lims_db";
    private static final String USER = "postgres";
    private static final String PASSWORD = "superadmin";
    private static Connection connection = null;

    private DatabaseConnector() {
        // Private constructor to prevent instantiation
    }

    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                // Load the PostgreSQL JDBC driver
                Class.forName("org.postgresql.Driver");

                // Set connection properties
                Properties props = new Properties();
                props.setProperty("user", USER);
                props.setProperty("password", PASSWORD);
                props.setProperty("useSSL", "false");
                props.setProperty("autoReconnect", "true");
                props.setProperty("allowPublicKeyRetrieval", "true");

                // Create connection
                connection = DriverManager.getConnection(URL, props);

                // Test the connection
                if (!connection.isValid(5)) {
                    throw new SQLException("Failed to establish a valid connection");
                }

            } catch (ClassNotFoundException e) {
                throw new SQLException("PostgreSQL JDBC Driver not found", e);
            }
        }
        return connection;
    }

    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                connection = null;
            } catch (SQLException e) {
                System.err.println("Error closing connection: " + e.getMessage());
            }
        }
    }

    public static void initializeDatabase() {
        try (Connection conn = getConnection()) {
            // Create users table if it doesn't exist (PostgreSQL syntax)
            String createTableSQL =
                "CREATE TABLE IF NOT EXISTS users (" +
                "    id SERIAL PRIMARY KEY," +
                "    name VARCHAR(100) NOT NULL," +
                "    email VARCHAR(100) UNIQUE NOT NULL," +
                "    role VARCHAR(20) NOT NULL," +
                "    password VARCHAR(255) NOT NULL," +
                "    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                "    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                ")";

            try (var stmt = conn.createStatement()) {
                stmt.execute(createTableSQL);
                System.out.println("Users table created or already exists");
            }

            // Insert sample user if not exists (PostgreSQL syntax)
            String insertUserSQL =
                "INSERT INTO users (name, email, role, password) " +
                "VALUES (?, ?, ?, ?) " +
                "ON CONFLICT (email) DO NOTHING";

            try (var pstmt = conn.prepareStatement(insertUserSQL)) {
                pstmt.setString(1, "Admin User");
                pstmt.setString(2, "admin@example.com");
                pstmt.setString(3, "ADMIN");
                pstmt.setString(4, "admin123"); // In production, use proper password hashing
                pstmt.executeUpdate();
                System.out.println("Sample user inserted or already exists");
            }

            // Retrieve and print all users
            String selectUsersSQL = "SELECT * FROM users";
            try (var stmt = conn.createStatement();
                 var rs = stmt.executeQuery(selectUsersSQL)) {

                System.out.println("\nCurrent users in database:");
                System.out.println("ID | Name | Email | Role");
                System.out.println("------------------------");

                while (rs.next()) {
                    System.out.printf("%d | %s | %s | %s%n",
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("role")
                    );
                }
            }

        } catch (SQLException e) {
            System.err.println("Database initialization error: " + e.getMessage());
            e.printStackTrace();
        }
    }
} 
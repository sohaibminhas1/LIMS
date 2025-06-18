package utils;

import dao.AbstractDAO;
import java.sql.*;
import java.util.*;

/**
 * Utility class to validate database schema and ensure all required tables and columns exist
 */
public class DatabaseSchemaValidator extends AbstractDAO {
    
    private static final String CLASS_NAME = "DatabaseSchemaValidator";
    
    // Required tables and their columns
    private static final Map<String, List<String>> REQUIRED_SCHEMA = new HashMap<>();
    
    static {
        // Complaints table
        REQUIRED_SCHEMA.put("complaints", Arrays.asList(
            "id", "computer_id", "department", "issue_type", "description", 
            "status", "urgency", "created_at"
        ));
        
        // Software requests table
        REQUIRED_SCHEMA.put("software_requests", Arrays.asList(
            "id", "computer_id", "software_name", "version", "urgency", 
            "justification", "status", "requested_by", "created_at"
        ));
        
        // Feedback table
        REQUIRED_SCHEMA.put("feedback", Arrays.asList(
            "id", "name", "category", "feedback", "status", "created_at"
        ));
        
        // Lab reservations table
        REQUIRED_SCHEMA.put("lab_reservations", Arrays.asList(
            "id", "requester_name", "lab_name", "reservation_date", 
            "time_slot", "purpose", "status", "created_at"
        ));
        
        // Computers table
        REQUIRED_SCHEMA.put("computers", Arrays.asList(
            "computer_id", "lab_id", "computer_name", "ip_address", 
            "specifications", "status", "install_date", "notes", "created_at"
        ));
    }
    
    /**
     * Validate the entire database schema
     * @return ValidationResult containing validation status and details
     */
    public ValidationResult validateSchema() {
        DAOLogger.info(CLASS_NAME, "validateSchema", "Starting database schema validation");
        
        ValidationResult result = new ValidationResult();
        
        try (Connection connection = getConnection()) {
            DatabaseMetaData metaData = connection.getMetaData();
            
            for (Map.Entry<String, List<String>> entry : REQUIRED_SCHEMA.entrySet()) {
                String tableName = entry.getKey();
                List<String> requiredColumns = entry.getValue();
                
                TableValidationResult tableResult = validateTable(metaData, tableName, requiredColumns);
                result.addTableResult(tableName, tableResult);
            }
            
        } catch (SQLException e) {
            DAOLogger.error(CLASS_NAME, "validateSchema", "Error during schema validation", e);
            result.setOverallValid(false);
            result.addError("Database connection failed: " + e.getMessage());
        }
        
        DAOLogger.info(CLASS_NAME, "validateSchema", 
            String.format("Schema validation completed. Valid: %s", result.isOverallValid()));
        
        return result;
    }
    
    /**
     * Validate a specific table and its columns
     */
    private TableValidationResult validateTable(DatabaseMetaData metaData, String tableName, List<String> requiredColumns) {
        TableValidationResult result = new TableValidationResult();
        
        try {
            // Check if table exists
            ResultSet tables = metaData.getTables(null, null, tableName, new String[]{"TABLE"});
            if (!tables.next()) {
                result.setTableExists(false);
                result.addError("Table '" + tableName + "' does not exist");
                DAOLogger.error(CLASS_NAME, "validateTable", "Table does not exist: " + tableName);
                return result;
            }
            
            result.setTableExists(true);
            DAOLogger.debug(CLASS_NAME, "validateTable", "Table exists: " + tableName);
            
            // Get existing columns
            Set<String> existingColumns = new HashSet<>();
            ResultSet columns = metaData.getColumns(null, null, tableName, null);
            while (columns.next()) {
                existingColumns.add(columns.getString("COLUMN_NAME").toLowerCase());
            }
            
            // Check required columns
            for (String requiredColumn : requiredColumns) {
                if (existingColumns.contains(requiredColumn.toLowerCase())) {
                    result.addExistingColumn(requiredColumn);
                    DAOLogger.debug(CLASS_NAME, "validateTable", 
                        String.format("Column exists: %s.%s", tableName, requiredColumn));
                } else {
                    result.addMissingColumn(requiredColumn);
                    result.addError(String.format("Column '%s' is missing from table '%s'", requiredColumn, tableName));
                    DAOLogger.warn(CLASS_NAME, "validateTable", 
                        String.format("Column missing: %s.%s", tableName, requiredColumn));
                }
            }
            
        } catch (SQLException e) {
            result.addError("Error validating table '" + tableName + "': " + e.getMessage());
            DAOLogger.error(CLASS_NAME, "validateTable", "Error validating table: " + tableName, e);
        }
        
        return result;
    }
    
    /**
     * Generate SQL statements to create missing tables and columns
     */
    public List<String> generateMissingSchemaSQL(ValidationResult validationResult) {
        List<String> sqlStatements = new ArrayList<>();
        
        for (Map.Entry<String, TableValidationResult> entry : validationResult.getTableResults().entrySet()) {
            String tableName = entry.getKey();
            TableValidationResult tableResult = entry.getValue();
            
            if (!tableResult.isTableExists()) {
                sqlStatements.add(generateCreateTableSQL(tableName));
            } else if (!tableResult.getMissingColumns().isEmpty()) {
                for (String column : tableResult.getMissingColumns()) {
                    sqlStatements.add(generateAddColumnSQL(tableName, column));
                }
            }
        }
        
        return sqlStatements;
    }
    
    /**
     * Generate CREATE TABLE SQL for a specific table
     */
    private String generateCreateTableSQL(String tableName) {
        StringBuilder sql = new StringBuilder();
        sql.append("CREATE TABLE ").append(tableName).append(" (\n");
        
        switch (tableName) {
            case "complaints":
                sql.append("    id SERIAL PRIMARY KEY,\n");
                sql.append("    computer_id VARCHAR(50) NOT NULL,\n");
                sql.append("    department VARCHAR(100) NOT NULL,\n");
                sql.append("    issue_type VARCHAR(100) NOT NULL,\n");
                sql.append("    description TEXT NOT NULL,\n");
                sql.append("    status VARCHAR(50) DEFAULT 'Pending',\n");
                sql.append("    urgency VARCHAR(20) DEFAULT 'Medium',\n");
                sql.append("    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP\n");
                break;
                
            case "software_requests":
                sql.append("    id SERIAL PRIMARY KEY,\n");
                sql.append("    computer_id VARCHAR(50) NOT NULL,\n");
                sql.append("    software_name VARCHAR(200) NOT NULL,\n");
                sql.append("    version VARCHAR(50),\n");
                sql.append("    urgency VARCHAR(20) DEFAULT 'Medium',\n");
                sql.append("    justification TEXT,\n");
                sql.append("    status VARCHAR(50) DEFAULT 'Pending',\n");
                sql.append("    requested_by VARCHAR(100),\n");
                sql.append("    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP\n");
                break;
                
            case "feedback":
                sql.append("    id SERIAL PRIMARY KEY,\n");
                sql.append("    name VARCHAR(100) NOT NULL,\n");
                sql.append("    category VARCHAR(50) NOT NULL,\n");
                sql.append("    feedback TEXT NOT NULL,\n");
                sql.append("    status VARCHAR(50) DEFAULT 'Pending',\n");
                sql.append("    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP\n");
                break;
                
            case "lab_reservations":
                sql.append("    id SERIAL PRIMARY KEY,\n");
                sql.append("    requester_name VARCHAR(100) NOT NULL,\n");
                sql.append("    lab_name VARCHAR(100) NOT NULL,\n");
                sql.append("    reservation_date DATE NOT NULL,\n");
                sql.append("    time_slot VARCHAR(50) NOT NULL,\n");
                sql.append("    purpose TEXT,\n");
                sql.append("    status VARCHAR(50) DEFAULT 'Pending',\n");
                sql.append("    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP\n");
                break;
                
            case "computers":
                sql.append("    computer_id VARCHAR(50) PRIMARY KEY,\n");
                sql.append("    lab_id INTEGER,\n");
                sql.append("    computer_name VARCHAR(100) NOT NULL,\n");
                sql.append("    ip_address VARCHAR(15),\n");
                sql.append("    specifications TEXT,\n");
                sql.append("    status VARCHAR(50) DEFAULT 'Available',\n");
                sql.append("    install_date VARCHAR(20),\n");
                sql.append("    notes TEXT,\n");
                sql.append("    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP\n");
                break;
        }
        
        sql.append(");");
        return sql.toString();
    }
    
    /**
     * Generate ADD COLUMN SQL for a specific column
     */
    private String generateAddColumnSQL(String tableName, String columnName) {
        String columnDefinition = getColumnDefinition(tableName, columnName);
        return String.format("ALTER TABLE %s ADD COLUMN %s %s;", tableName, columnName, columnDefinition);
    }
    
    /**
     * Get column definition for a specific column
     */
    private String getColumnDefinition(String tableName, String columnName) {
        // Default definitions - can be customized based on requirements
        switch (columnName) {
            case "id": return "SERIAL PRIMARY KEY";
            case "created_at": return "TIMESTAMP DEFAULT CURRENT_TIMESTAMP";
            case "status": return "VARCHAR(50) DEFAULT 'Pending'";
            case "urgency": return "VARCHAR(20) DEFAULT 'Medium'";
            case "reservation_date": return "DATE NOT NULL";
            case "lab_id": return "INTEGER";
            default: return "VARCHAR(255)";
        }
    }
    
    /**
     * Print validation results in a formatted way
     */
    public void printValidationResults(ValidationResult result) {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("DATABASE SCHEMA VALIDATION RESULTS");
        System.out.println("=".repeat(60));
        
        if (result.isOverallValid()) {
            System.out.println("✅ Overall Status: VALID - All required tables and columns exist");
        } else {
            System.out.println("❌ Overall Status: INVALID - Some tables or columns are missing");
        }
        
        System.out.println("\nTable Details:");
        for (Map.Entry<String, TableValidationResult> entry : result.getTableResults().entrySet()) {
            String tableName = entry.getKey();
            TableValidationResult tableResult = entry.getValue();
            
            System.out.println(String.format("\n📋 Table: %s", tableName.toUpperCase()));
            
            if (tableResult.isTableExists()) {
                System.out.println("   ✅ Table exists");
                
                if (!tableResult.getMissingColumns().isEmpty()) {
                    System.out.println("   ❌ Missing columns: " + String.join(", ", tableResult.getMissingColumns()));
                } else {
                    System.out.println("   ✅ All required columns exist");
                }
                
                if (!tableResult.getExistingColumns().isEmpty()) {
                    System.out.println("   ✅ Existing columns: " + String.join(", ", tableResult.getExistingColumns()));
                }
            } else {
                System.out.println("   ❌ Table does not exist");
            }
            
            if (!tableResult.getErrors().isEmpty()) {
                System.out.println("   ⚠️ Errors:");
                for (String error : tableResult.getErrors()) {
                    System.out.println("      - " + error);
                }
            }
        }
        
        if (!result.getErrors().isEmpty()) {
            System.out.println("\n⚠️ General Errors:");
            for (String error : result.getErrors()) {
                System.out.println("   - " + error);
            }
        }
        
        System.out.println("\n" + "=".repeat(60));
    }
}

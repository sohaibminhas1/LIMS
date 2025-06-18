package utils;

import java.util.*;

/**
 * Container for database schema validation results
 */
public class ValidationResult {
    private boolean overallValid = true;
    private List<String> errors = new ArrayList<>();
    private Map<String, TableValidationResult> tableResults = new HashMap<>();
    
    public boolean isOverallValid() {
        return overallValid;
    }
    
    public void setOverallValid(boolean overallValid) {
        this.overallValid = overallValid;
    }
    
    public List<String> getErrors() {
        return errors;
    }
    
    public void addError(String error) {
        this.errors.add(error);
        this.overallValid = false;
    }
    
    public Map<String, TableValidationResult> getTableResults() {
        return tableResults;
    }
    
    public void addTableResult(String tableName, TableValidationResult result) {
        this.tableResults.put(tableName, result);
        if (!result.isValid()) {
            this.overallValid = false;
        }
    }
    
    public TableValidationResult getTableResult(String tableName) {
        return tableResults.get(tableName);
    }
    
    /**
     * Get summary of validation results
     */
    public String getSummary() {
        StringBuilder summary = new StringBuilder();
        summary.append(String.format("Overall Valid: %s\n", overallValid));
        summary.append(String.format("Tables Validated: %d\n", tableResults.size()));
        
        int validTables = 0;
        int invalidTables = 0;
        
        for (TableValidationResult result : tableResults.values()) {
            if (result.isValid()) {
                validTables++;
            } else {
                invalidTables++;
            }
        }
        
        summary.append(String.format("Valid Tables: %d\n", validTables));
        summary.append(String.format("Invalid Tables: %d\n", invalidTables));
        summary.append(String.format("Total Errors: %d", errors.size()));
        
        return summary.toString();
    }
}

/**
 * Container for individual table validation results
 */
class TableValidationResult {
    private boolean tableExists = false;
    private List<String> existingColumns = new ArrayList<>();
    private List<String> missingColumns = new ArrayList<>();
    private List<String> errors = new ArrayList<>();
    
    public boolean isTableExists() {
        return tableExists;
    }
    
    public void setTableExists(boolean tableExists) {
        this.tableExists = tableExists;
    }
    
    public List<String> getExistingColumns() {
        return existingColumns;
    }
    
    public void addExistingColumn(String column) {
        this.existingColumns.add(column);
    }
    
    public List<String> getMissingColumns() {
        return missingColumns;
    }
    
    public void addMissingColumn(String column) {
        this.missingColumns.add(column);
    }
    
    public List<String> getErrors() {
        return errors;
    }
    
    public void addError(String error) {
        this.errors.add(error);
    }
    
    /**
     * Check if this table validation is valid
     */
    public boolean isValid() {
        return tableExists && missingColumns.isEmpty() && errors.isEmpty();
    }
    
    /**
     * Get summary of this table's validation
     */
    public String getSummary() {
        StringBuilder summary = new StringBuilder();
        summary.append(String.format("Table Exists: %s\n", tableExists));
        summary.append(String.format("Existing Columns: %d\n", existingColumns.size()));
        summary.append(String.format("Missing Columns: %d\n", missingColumns.size()));
        summary.append(String.format("Errors: %d", errors.size()));
        return summary.toString();
    }
}

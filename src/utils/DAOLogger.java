package utils;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Comprehensive logging utility for DAO operations
 */
public class DAOLogger {
    
    private static final SimpleDateFormat timestampFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static boolean debugMode = true;
    
    public enum LogLevel {
        DEBUG, INFO, WARN, ERROR, SUCCESS
    }
    
    /**
     * Log a message with specified level
     * @param level Log level
     * @param className Class name where log is called
     * @param methodName Method name where log is called
     * @param message Log message
     */
    public static void log(LogLevel level, String className, String methodName, String message) {
        String timestamp = timestampFormat.format(new Date());
        String emoji = getEmojiForLevel(level);
        String colorCode = getColorCodeForLevel(level);
        
        String logMessage = String.format("[%s] %s %s.%s: %s%s%s", 
            timestamp, emoji, className, methodName, colorCode, message, "\u001B[0m");
        
        if (level == LogLevel.ERROR) {
            System.err.println(logMessage);
        } else {
            System.out.println(logMessage);
        }
    }
    
    /**
     * Log debug message
     */
    public static void debug(String className, String methodName, String message) {
        if (debugMode) {
            log(LogLevel.DEBUG, className, methodName, message);
        }
    }
    
    /**
     * Log info message
     */
    public static void info(String className, String methodName, String message) {
        log(LogLevel.INFO, className, methodName, message);
    }
    
    /**
     * Log warning message
     */
    public static void warn(String className, String methodName, String message) {
        log(LogLevel.WARN, className, methodName, message);
    }
    
    /**
     * Log error message
     */
    public static void error(String className, String methodName, String message) {
        log(LogLevel.ERROR, className, methodName, message);
    }
    
    /**
     * Log success message
     */
    public static void success(String className, String methodName, String message) {
        log(LogLevel.SUCCESS, className, methodName, message);
    }
    
    /**
     * Log error with exception details
     */
    public static void error(String className, String methodName, String message, Exception e) {
        String fullMessage = message + " - " + e.getMessage();
        log(LogLevel.ERROR, className, methodName, fullMessage);
        
        if (debugMode) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            log(LogLevel.DEBUG, className, methodName, "Stack trace: " + sw.toString());
        }
    }
    
    /**
     * Log database operation start
     */
    public static void logDatabaseOperation(String className, String operation, String entity, String details) {
        info(className, operation, String.format("Starting %s operation for %s: %s", operation, entity, details));
    }
    
    /**
     * Log database operation success
     */
    public static void logDatabaseSuccess(String className, String operation, String entity, int rowsAffected) {
        success(className, operation, String.format("%s operation completed for %s. Rows affected: %d", 
            operation, entity, rowsAffected));
    }
    
    /**
     * Log database operation failure
     */
    public static void logDatabaseFailure(String className, String operation, String entity, String reason) {
        error(className, operation, String.format("%s operation failed for %s: %s", operation, entity, reason));
    }
    
    /**
     * Log connection status
     */
    public static void logConnectionStatus(String className, boolean connected) {
        if (connected) {
            success(className, "testConnection", "Database connection successful");
        } else {
            error(className, "testConnection", "Database connection failed");
        }
    }
    
    /**
     * Log validation error
     */
    public static void logValidationError(String className, String methodName, String field, String value, String reason) {
        error(className, methodName, String.format("Validation failed for field '%s' with value '%s': %s", 
            field, value, reason));
    }
    
    /**
     * Log performance metrics
     */
    public static void logPerformance(String className, String methodName, long executionTimeMs) {
        if (executionTimeMs > 1000) {
            warn(className, methodName, String.format("Slow operation detected: %d ms", executionTimeMs));
        } else {
            debug(className, methodName, String.format("Operation completed in %d ms", executionTimeMs));
        }
    }
    
    /**
     * Get emoji for log level
     */
    private static String getEmojiForLevel(LogLevel level) {
        switch (level) {
            case DEBUG: return "🔍";
            case INFO: return "ℹ️";
            case WARN: return "⚠️";
            case ERROR: return "❌";
            case SUCCESS: return "✅";
            default: return "📝";
        }
    }
    
    /**
     * Get ANSI color code for log level
     */
    private static String getColorCodeForLevel(LogLevel level) {
        switch (level) {
            case DEBUG: return "\u001B[36m"; // Cyan
            case INFO: return "\u001B[34m";  // Blue
            case WARN: return "\u001B[33m";  // Yellow
            case ERROR: return "\u001B[31m"; // Red
            case SUCCESS: return "\u001B[32m"; // Green
            default: return "\u001B[0m";    // Reset
        }
    }
    
    /**
     * Enable or disable debug mode
     */
    public static void setDebugMode(boolean enabled) {
        debugMode = enabled;
    }
    
    /**
     * Check if debug mode is enabled
     */
    public static boolean isDebugMode() {
        return debugMode;
    }
    
    /**
     * Log method entry (for debugging)
     */
    public static void logMethodEntry(String className, String methodName, Object... params) {
        if (debugMode) {
            StringBuilder sb = new StringBuilder();
            sb.append("Method entry");
            if (params.length > 0) {
                sb.append(" with parameters: ");
                for (int i = 0; i < params.length; i++) {
                    if (i > 0) sb.append(", ");
                    sb.append(params[i]);
                }
            }
            debug(className, methodName, sb.toString());
        }
    }
    
    /**
     * Log method exit (for debugging)
     */
    public static void logMethodExit(String className, String methodName, Object result) {
        if (debugMode) {
            debug(className, methodName, "Method exit with result: " + result);
        }
    }
}

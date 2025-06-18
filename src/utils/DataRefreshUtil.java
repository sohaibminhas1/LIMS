package utils;

import ui.TableRefreshManager;
import ui.JPanelDataManager;
import javax.swing.*;

/**
 * Utility class to handle data refresh operations across the LIMS application.
 * Ensures that UI components are updated immediately after database operations.
 */
public class DataRefreshUtil {
    
    /**
     * Refresh all tables related to complaints
     */
    public static void refreshComplaintData() {
        SwingUtilities.invokeLater(() -> {
            TableRefreshManager.getInstance().refreshTable("complaints");
            TableRefreshManager.getInstance().refreshRelatedTables("complaint");
            System.out.println("✓ Complaint data refreshed");
        });
    }
    
    /**
     * Refresh all tables related to software requests
     */
    public static void refreshSoftwareRequestData() {
        SwingUtilities.invokeLater(() -> {
            TableRefreshManager.getInstance().refreshTable("software_requests");
            TableRefreshManager.getInstance().refreshRelatedTables("software");
            JPanelDataManager.getInstance().refreshPanel("software_management");
            System.out.println("✓ Software request data refreshed");
        });
    }
    
    /**
     * Refresh all tables related to feedback
     */
    public static void refreshFeedbackData() {
        SwingUtilities.invokeLater(() -> {
            TableRefreshManager.getInstance().refreshTable("feedback");
            TableRefreshManager.getInstance().refreshAllTables(); // Feedback might affect multiple areas
            System.out.println("✓ Feedback data refreshed");
        });
    }
    
    /**
     * Refresh all tables related to lab reservations
     */
    public static void refreshLabReservationData() {
        SwingUtilities.invokeLater(() -> {
            TableRefreshManager.getInstance().refreshTable("lab_reservations");
            TableRefreshManager.getInstance().refreshRelatedTables("reservation");
            JPanelDataManager.getInstance().refreshPanel("lab_schedule");
            System.out.println("✓ Lab reservation data refreshed");
        });
    }
    
    /**
     * Refresh all tables related to computers
     */
    public static void refreshComputerData() {
        SwingUtilities.invokeLater(() -> {
            TableRefreshManager.getInstance().refreshTable("computers");
            TableRefreshManager.getInstance().refreshRelatedTables("computer");
            JPanelDataManager.getInstance().refreshPanel("computer_inventory");
            System.out.println("✓ Computer data refreshed");
        });
    }
    
    /**
     * Refresh all tables related to labs
     */
    public static void refreshLabData() {
        SwingUtilities.invokeLater(() -> {
            TableRefreshManager.getInstance().refreshTable("labs");
            TableRefreshManager.getInstance().refreshRelatedTables("lab");
            System.out.println("✓ Lab data refreshed");
        });
    }
    
    /**
     * Refresh all tables related to users
     */
    public static void refreshUserData() {
        SwingUtilities.invokeLater(() -> {
            TableRefreshManager.getInstance().refreshTable("user_access");
            TableRefreshManager.getInstance().refreshRelatedTables("user");
            System.out.println("✓ User data refreshed");
        });
    }
    
    /**
     * Refresh all tables in the application
     */
    public static void refreshAllData() {
        SwingUtilities.invokeLater(() -> {
            TableRefreshManager.getInstance().refreshAllTables();
            JPanelDataManager.getInstance().refreshAllPanels();
            System.out.println("✓ All data refreshed");
        });
    }
    
    /**
     * Show a success message with automatic data refresh
     */
    public static void showSuccessWithRefresh(JComponent parent, String message, String dataType) {
        // Refresh data first
        switch (dataType.toLowerCase()) {
            case "complaint":
                refreshComplaintData();
                break;
            case "software":
            case "software_request":
                refreshSoftwareRequestData();
                break;
            case "feedback":
                refreshFeedbackData();
                break;
            case "reservation":
            case "lab_reservation":
                refreshLabReservationData();
                break;
            case "computer":
                refreshComputerData();
                break;
            case "lab":
                refreshLabData();
                break;
            case "user":
                refreshUserData();
                break;
            default:
                refreshAllData();
                break;
        }
        
        // Show success message
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(parent, 
                message + "\n✓ Data has been refreshed automatically.", 
                "Success", 
                JOptionPane.INFORMATION_MESSAGE);
        });
    }
    
    /**
     * Clear form fields after successful submission
     */
    public static void clearFormFields(JComponent... components) {
        SwingUtilities.invokeLater(() -> {
            for (JComponent component : components) {
                if (component instanceof JTextField) {
                    ((JTextField) component).setText("");
                } else if (component instanceof JTextArea) {
                    ((JTextArea) component).setText("");
                } else if (component instanceof JComboBox) {
                    ((JComboBox<?>) component).setSelectedIndex(0);
                }
            }
        });
    }
    
    /**
     * Handle successful form submission with automatic refresh and form clearing
     */
    public static void handleSuccessfulSubmission(JComponent parent, String message, String dataType, JComponent... formFields) {
        // Clear form fields
        clearFormFields(formFields);
        
        // Show success message with refresh
        showSuccessWithRefresh(parent, message, dataType);
    }
}

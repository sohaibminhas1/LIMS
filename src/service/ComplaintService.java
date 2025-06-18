package service;

import dao.ComplaintDAO;
import model.Complaint;
import ui.TableRefreshManager;
import utils.DAOLogger;
import java.util.Date;
import java.util.List;

/**
 * Service class for managing complaints in the LIMS system.
 * Handles all business logic related to complaint management.
 */
public class ComplaintService {
    private ComplaintDAO complaintDAO;

    public ComplaintService() {
        complaintDAO = new ComplaintDAO();

        // Test database connection
        if (!complaintDAO.testConnection()) {
            System.err.println("⚠️ Warning: Database connection failed. Some features may not work properly.");
        }
    }

    public List<Complaint> getAllComplaints() {
        return complaintDAO.findAll();
    }

    public List<Complaint> getComplaintsByStatus(String status) {
        return complaintDAO.findByStatus(status);
    }

    public List<Complaint> getComplaintsByDepartment(String department) {
        // Filter by department using DAO results
        return getAllComplaints().stream()
            .filter(complaint -> complaint.getDepartment().equals(department))
            .collect(java.util.stream.Collectors.toList());
    }

    public List<Complaint> getComplaintsByIssueType(String issueType) {
        // Filter by issue type using DAO results
        return getAllComplaints().stream()
            .filter(complaint -> complaint.getIssueType().equals(issueType))
            .collect(java.util.stream.Collectors.toList());
    }

    public void addComplaint(Complaint complaint) {
        DAOLogger.logMethodEntry("ComplaintService", "addComplaint", complaint.getComputerId(), complaint.getDepartment());
        DAOLogger.info("ComplaintService", "addComplaint", "Processing complaint addition for computer: " + complaint.getComputerId());

        long startTime = System.currentTimeMillis();

        try {
            // Validate complaint data
            if (complaint.getComputerId() == null || complaint.getComputerId().trim().isEmpty()) {
                DAOLogger.error("ComplaintService", "addComplaint", "Invalid computer ID provided");
                throw new IllegalArgumentException("Computer ID cannot be null or empty");
            }

            if (complaint.getDescription() == null || complaint.getDescription().trim().isEmpty()) {
                DAOLogger.error("ComplaintService", "addComplaint", "Invalid description provided");
                throw new IllegalArgumentException("Description cannot be null or empty");
            }

            DAOLogger.debug("ComplaintService", "addComplaint", "Validation passed, calling DAO insert");
            boolean success = complaintDAO.insert(complaint);

            long executionTime = System.currentTimeMillis() - startTime;
            DAOLogger.logPerformance("ComplaintService", "addComplaint", executionTime);

            if (success) {
                DAOLogger.success("ComplaintService", "addComplaint", "Complaint added successfully, refreshing UI tables");
                // Refresh the dashboard table immediately
                TableRefreshManager.getInstance().refreshTable("complaints");
                DAOLogger.info("ComplaintService", "addComplaint", "Table refresh triggered for complaints");
            } else {
                DAOLogger.error("ComplaintService", "addComplaint", "DAO insert operation failed");
            }

            DAOLogger.logMethodExit("ComplaintService", "addComplaint", success);

        } catch (Exception e) {
            DAOLogger.error("ComplaintService", "addComplaint", "Exception during complaint addition", e);
            throw e;
        }
    }

    public void updateComplaint(Complaint complaint) {
        boolean success = complaintDAO.update(complaint);

        if (success) {
            TableRefreshManager.getInstance().refreshTable("complaints");
        }
    }

    public void deleteComplaint(String computerId, Date submissionDate) {
        boolean success = complaintDAO.delete(computerId);

        if (success) {
            TableRefreshManager.getInstance().refreshTable("complaints");
        }
    }

    public void assignComplaint(String computerId, Date submissionDate, String assignedTo) {
        // First find the complaint, then update it
        Complaint complaint = complaintDAO.findById(computerId);
        if (complaint != null) {
            complaint.setAssignedTo(assignedTo);
            complaint.setStatus("In Progress");
            updateComplaint(complaint);
        }
    }

    public void updateComplaintStatus(String computerId, Date submissionDate, String status) {
        boolean success = complaintDAO.updateStatus(computerId, status);

        if (success) {
            TableRefreshManager.getInstance().refreshTable("complaints");
        }
    }
}
package service;

import dao.SoftwareRequestDAO;
import model.SoftwareRequest;
import ui.TableRefreshManager;
import java.util.Date;
import java.util.List;

/**
 * Service class for managing software requests in the LIMS system.
 * Handles all business logic related to software request management.
 */
public class SoftwareRequestService {
    private SoftwareRequestDAO softwareRequestDAO;

    public SoftwareRequestService() {
        softwareRequestDAO = new SoftwareRequestDAO();

        // Test database connection
        if (!softwareRequestDAO.testConnection()) {
            System.err.println("⚠️ Warning: Database connection failed. Some features may not work properly.");
        }
    }

    public List<SoftwareRequest> getAllRequests() {
        return softwareRequestDAO.findAll();
    }

    public List<SoftwareRequest> getRequestsByStatus(String status) {
        return softwareRequestDAO.findByStatus(status);
    }

    public List<SoftwareRequest> getRequestsByUrgency(String urgency) {
        // Filter by urgency using DAO results
        return getAllRequests().stream()
            .filter(request -> request.getUrgency().equals(urgency))
            .collect(java.util.stream.Collectors.toList());
    }

    public void addRequest(SoftwareRequest request) {
        boolean success = softwareRequestDAO.insert(request);

        if (success) {
            // Refresh the dashboard table immediately
            TableRefreshManager.getInstance().refreshTable("software_requests");
        }
    }

    public void updateRequest(SoftwareRequest request) {
        boolean success = softwareRequestDAO.update(request);

        if (success) {
            TableRefreshManager.getInstance().refreshTable("software_requests");
        }
    }

    public void deleteRequest(String computerId, Date requestDate) {
        boolean success = softwareRequestDAO.delete(computerId);

        if (success) {
            TableRefreshManager.getInstance().refreshTable("software_requests");
        }
    }

    public void approveRequest(String computerId, Date requestDate, String approvedBy) {
        boolean success = softwareRequestDAO.approveRequest(computerId, approvedBy);

        if (success) {
            TableRefreshManager.getInstance().refreshTable("software_requests");
        }
    }

    public void rejectRequest(String computerId, Date requestDate) {
        boolean success = softwareRequestDAO.rejectRequest(computerId);

        if (success) {
            TableRefreshManager.getInstance().refreshTable("software_requests");
        }
    }

    public void updateRequestStatus(String computerId, Date requestDate, String status) {
        boolean success = softwareRequestDAO.updateStatus(computerId, status, null);

        if (success) {
            TableRefreshManager.getInstance().refreshTable("software_requests");
        }
    }
}
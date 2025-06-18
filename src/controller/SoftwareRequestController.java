package controller;

import model.SoftwareRequest;
import service.SoftwareRequestService;
import java.util.List;
import java.util.Date;

public class SoftwareRequestController extends BaseController {
    private final SoftwareRequestService softwareRequestService;
    
    public SoftwareRequestController(SoftwareRequestService softwareRequestService) {
        this.softwareRequestService = softwareRequestService;
    }
    
    public void addRequest(String computerId, String softwareName, String version, 
                         String urgency, String reason) {
        // Validate inputs
        if (!isValidString(computerId, 3, 50)) {
            throw new IllegalArgumentException("Invalid computer ID");
        }
        if (!isValidString(softwareName, 2, 100)) {
            throw new IllegalArgumentException("Invalid software name");
        }
        if (!isValidString(version, 1, 50)) {
            throw new IllegalArgumentException("Invalid version");
        }
        if (!isValidString(urgency, 2, 20)) {
            throw new IllegalArgumentException("Invalid urgency level");
        }
        if (!isValidString(reason, 5, 500)) {
            throw new IllegalArgumentException("Invalid reason");
        }
        
        // Sanitize inputs
        computerId = sanitizeInput(computerId);
        softwareName = sanitizeInput(softwareName);
        version = sanitizeInput(version);
        urgency = sanitizeInput(urgency);
        reason = sanitizeInput(reason);
        
        // Create software request object
        SoftwareRequest request = new SoftwareRequest(
            computerId,
            softwareName,
            version,
            urgency,
            reason,
            new Date(),
            "Pending",
            ""
        );
        
        softwareRequestService.addRequest(request);
    }
    
    public void updateRequest(String computerId, Date requestDate, String softwareName, 
                            String version, String urgency, String reason, String status) {
        // Validate inputs
        if (!isValidString(computerId, 3, 50)) {
            throw new IllegalArgumentException("Invalid computer ID");
        }
        if (requestDate == null) {
            throw new IllegalArgumentException("Invalid request date");
        }
        if (!isValidString(softwareName, 2, 100)) {
            throw new IllegalArgumentException("Invalid software name");
        }
        if (!isValidString(version, 1, 50)) {
            throw new IllegalArgumentException("Invalid version");
        }
        if (!isValidString(urgency, 2, 20)) {
            throw new IllegalArgumentException("Invalid urgency level");
        }
        if (!isValidString(reason, 5, 500)) {
            throw new IllegalArgumentException("Invalid reason");
        }
        if (!isValidString(status, 2, 20)) {
            throw new IllegalArgumentException("Invalid status");
        }
        
        // Sanitize inputs
        computerId = sanitizeInput(computerId);
        softwareName = sanitizeInput(softwareName);
        version = sanitizeInput(version);
        urgency = sanitizeInput(urgency);
        reason = sanitizeInput(reason);
        status = sanitizeInput(status);
        
        // Create software request object
        SoftwareRequest request = new SoftwareRequest(
            computerId,
            softwareName,
            version,
            urgency,
            reason,
            requestDate,
            status,
            ""
        );
        
        softwareRequestService.updateRequest(request);
    }
    
    public void approveRequest(String computerId, Date requestDate, String approvedBy) {
        // Validate inputs
        if (!isValidString(computerId, 3, 50)) {
            throw new IllegalArgumentException("Invalid computer ID");
        }
        if (requestDate == null) {
            throw new IllegalArgumentException("Invalid request date");
        }
        if (!isValidString(approvedBy, 2, 50)) {
            throw new IllegalArgumentException("Invalid approver");
        }
        
        // Sanitize inputs
        computerId = sanitizeInput(computerId);
        approvedBy = sanitizeInput(approvedBy);
        
        softwareRequestService.approveRequest(computerId, requestDate, approvedBy);
    }
    
    public void rejectRequest(String computerId, Date requestDate) {
        // Validate inputs
        if (!isValidString(computerId, 3, 50)) {
            throw new IllegalArgumentException("Invalid computer ID");
        }
        if (requestDate == null) {
            throw new IllegalArgumentException("Invalid request date");
        }
        
        // Sanitize inputs
        computerId = sanitizeInput(computerId);
        
        softwareRequestService.rejectRequest(computerId, requestDate);
    }
    
    public void updateRequestStatus(String computerId, Date requestDate, String status) {
        // Validate inputs
        if (!isValidString(computerId, 3, 50)) {
            throw new IllegalArgumentException("Invalid computer ID");
        }
        if (requestDate == null) {
            throw new IllegalArgumentException("Invalid request date");
        }
        if (!isValidString(status, 2, 20)) {
            throw new IllegalArgumentException("Invalid status");
        }
        
        // Sanitize inputs
        computerId = sanitizeInput(computerId);
        status = sanitizeInput(status);
        
        softwareRequestService.updateRequestStatus(computerId, requestDate, status);
    }
    
    public void deleteRequest(String computerId, Date requestDate) {
        // Validate inputs
        if (!isValidString(computerId, 3, 50)) {
            throw new IllegalArgumentException("Invalid computer ID");
        }
        if (requestDate == null) {
            throw new IllegalArgumentException("Invalid request date");
        }
        
        // Sanitize inputs
        computerId = sanitizeInput(computerId);
        
        softwareRequestService.deleteRequest(computerId, requestDate);
    }
    
    public List<SoftwareRequest> getAllRequests() {
        return softwareRequestService.getAllRequests();
    }
    
    public List<SoftwareRequest> getRequestsByStatus(String status) {
        if (!isValidString(status, 2, 20)) {
            throw new IllegalArgumentException("Invalid status");
        }
        status = sanitizeInput(status);
        return softwareRequestService.getRequestsByStatus(status);
    }
    
    public List<SoftwareRequest> getRequestsByUrgency(String urgency) {
        if (!isValidString(urgency, 2, 20)) {
            throw new IllegalArgumentException("Invalid urgency level");
        }
        urgency = sanitizeInput(urgency);
        return softwareRequestService.getRequestsByUrgency(urgency);
    }

    public SoftwareRequest getRequestById(String id) {
        if (!isValidString(id, 3, 50)) {
            throw new IllegalArgumentException("Invalid request ID");
        }
        id = sanitizeInput(id);
        List<SoftwareRequest> requests = softwareRequestService.getAllRequests();
        for (SoftwareRequest request : requests) {
            if (request.getId().equals(id)) {
                return request;
            }
        }
        return null;
    }
} 
package controller;

import model.Complaint;
import service.ComplaintService;
import utils.DAOLogger;
import java.util.List;
import java.util.Date;

public class ComplaintController extends BaseController {
    private final ComplaintService complaintService;
    
    public ComplaintController(ComplaintService complaintService) {
        this.complaintService = complaintService;
    }
    
    public void addComplaint(String computerId, String department, String issueType,
                           String description) {
        DAOLogger.logMethodEntry("ComplaintController", "addComplaint", computerId, department, issueType);
        DAOLogger.info("ComplaintController", "addComplaint", "🎯 UI REQUEST: Adding complaint for computer: " + computerId);

        long startTime = System.currentTimeMillis();

        try {
            // Validate inputs with detailed logging
            DAOLogger.debug("ComplaintController", "addComplaint", "Validating input parameters");

            if (!isValidString(computerId, 3, 50)) {
                DAOLogger.logValidationError("ComplaintController", "addComplaint", "computerId", computerId, "Length must be 3-50 characters");
                throw new IllegalArgumentException("Invalid computer ID");
            }
            if (!isValidString(department, 2, 50)) {
                DAOLogger.logValidationError("ComplaintController", "addComplaint", "department", department, "Length must be 2-50 characters");
                throw new IllegalArgumentException("Invalid department");
            }
            if (!isValidString(issueType, 2, 50)) {
                DAOLogger.logValidationError("ComplaintController", "addComplaint", "issueType", issueType, "Length must be 2-50 characters");
                throw new IllegalArgumentException("Invalid issue type");
            }
            if (!isValidString(description, 5, 500)) {
                DAOLogger.logValidationError("ComplaintController", "addComplaint", "description", description, "Length must be 5-500 characters");
                throw new IllegalArgumentException("Invalid description");
            }

            DAOLogger.success("ComplaintController", "addComplaint", "All input validation passed");

            // Sanitize inputs
            DAOLogger.debug("ComplaintController", "addComplaint", "Sanitizing input parameters");
            computerId = sanitizeInput(computerId);
            department = sanitizeInput(department);
            issueType = sanitizeInput(issueType);
            description = sanitizeInput(description);

            // Create complaint object
            DAOLogger.debug("ComplaintController", "addComplaint", "Creating Complaint object");
            Complaint complaint = new Complaint(
                computerId,
                department,
                issueType,
                description,
                new Date(),
                "Pending",
                ""
            );

            DAOLogger.info("ComplaintController", "addComplaint", "Calling ComplaintService.addComplaint()");
            complaintService.addComplaint(complaint);

            long executionTime = System.currentTimeMillis() - startTime;
            DAOLogger.logPerformance("ComplaintController", "addComplaint", executionTime);
            DAOLogger.success("ComplaintController", "addComplaint", "🎯 CONTROLLER SUCCESS: Complaint processing completed");
            DAOLogger.logMethodExit("ComplaintController", "addComplaint", "success");

        } catch (Exception e) {
            DAOLogger.error("ComplaintController", "addComplaint", "🎯 CONTROLLER ERROR: Exception during complaint processing", e);
            throw e;
        }
    }
    
    public void updateComplaint(String computerId, Date submissionDate, String department, 
                              String issueType, String description, String status) {
        // Validate inputs
        if (!isValidString(computerId, 3, 50)) {
            throw new IllegalArgumentException("Invalid computer ID");
        }
        if (submissionDate == null) {
            throw new IllegalArgumentException("Invalid submission date");
        }
        if (!isValidString(department, 2, 50)) {
            throw new IllegalArgumentException("Invalid department");
        }
        if (!isValidString(issueType, 2, 50)) {
            throw new IllegalArgumentException("Invalid issue type");
        }
        if (!isValidString(description, 5, 500)) {
            throw new IllegalArgumentException("Invalid description");
        }
        if (!isValidString(status, 2, 20)) {
            throw new IllegalArgumentException("Invalid status");
        }
        
        // Sanitize inputs
        computerId = sanitizeInput(computerId);
        department = sanitizeInput(department);
        issueType = sanitizeInput(issueType);
        description = sanitizeInput(description);
        status = sanitizeInput(status);
        
        // Create complaint object
        Complaint complaint = new Complaint(
            computerId,
            department,
            issueType,
            description,
            submissionDate,
            status,
            ""
        );
        
        complaintService.updateComplaint(complaint);
    }
    
    public void assignComplaint(String computerId, Date submissionDate, String assignedTo) {
        // Validate inputs
        if (!isValidString(computerId, 3, 50)) {
            throw new IllegalArgumentException("Invalid computer ID");
        }
        if (submissionDate == null) {
            throw new IllegalArgumentException("Invalid submission date");
        }
        if (!isValidString(assignedTo, 2, 50)) {
            throw new IllegalArgumentException("Invalid assignee");
        }
        
        // Sanitize inputs
        computerId = sanitizeInput(computerId);
        assignedTo = sanitizeInput(assignedTo);
        
        complaintService.assignComplaint(computerId, submissionDate, assignedTo);
    }
    
    public void updateComplaintStatus(String computerId, Date submissionDate, String status) {
        // Validate inputs
        if (!isValidString(computerId, 3, 50)) {
            throw new IllegalArgumentException("Invalid computer ID");
        }
        if (submissionDate == null) {
            throw new IllegalArgumentException("Invalid submission date");
        }
        if (!isValidString(status, 2, 20)) {
            throw new IllegalArgumentException("Invalid status");
        }
        
        // Sanitize inputs
        computerId = sanitizeInput(computerId);
        status = sanitizeInput(status);
        
        complaintService.updateComplaintStatus(computerId, submissionDate, status);
    }
    
    public void deleteComplaint(String computerId, Date submissionDate) {
        // Validate inputs
        if (!isValidString(computerId, 3, 50)) {
            throw new IllegalArgumentException("Invalid computer ID");
        }
        if (submissionDate == null) {
            throw new IllegalArgumentException("Invalid submission date");
        }
        
        // Sanitize inputs
        computerId = sanitizeInput(computerId);
        
        complaintService.deleteComplaint(computerId, submissionDate);
    }
    
    public List<Complaint> getAllComplaints() {
        return complaintService.getAllComplaints();
    }
    
    public List<Complaint> getComplaintsByStatus(String status) {
        if (!isValidString(status, 2, 20)) {
            throw new IllegalArgumentException("Invalid status");
        }
        status = sanitizeInput(status);
        return complaintService.getComplaintsByStatus(status);
    }
    
    public List<Complaint> getComplaintsByDepartment(String department) {
        if (!isValidString(department, 2, 50)) {
            throw new IllegalArgumentException("Invalid department");
        }
        department = sanitizeInput(department);
        return complaintService.getComplaintsByDepartment(department);
    }

    public Complaint getComplaintById(String id) {
        if (!isValidString(id, 3, 50)) {
            throw new IllegalArgumentException("Invalid complaint ID");
        }
        id = sanitizeInput(id);
        List<Complaint> complaints = complaintService.getAllComplaints();
        for (Complaint complaint : complaints) {
            if (complaint.getId().equals(id)) {
                return complaint;
            }
        }
        return null;
    }
} 
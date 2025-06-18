package model;

import java.util.Date;

public class Complaint {
    private String computerId;
    private String department;
    private String issueType;
    private String description;
    private Date submissionDate;
    private String status;
    private String assignedTo;

    public Complaint(String computerId, String department, String issueType, 
                    String description, Date submissionDate, String status, String assignedTo) {
        this.computerId = computerId;
        this.department = department;
        this.issueType = issueType;
        this.description = description;
        this.submissionDate = submissionDate;
        this.status = status;
        this.assignedTo = assignedTo;
    }

    // Getters and Setters
    public String getComputerId() { return computerId; }
    public void setComputerId(String computerId) { this.computerId = computerId; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public String getIssueType() { return issueType; }
    public void setIssueType(String issueType) { this.issueType = issueType; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Date getSubmissionDate() { return submissionDate; }
    public void setSubmissionDate(Date submissionDate) { this.submissionDate = submissionDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getAssignedTo() { return assignedTo; }
    public void setAssignedTo(String assignedTo) { this.assignedTo = assignedTo; }

    public String getId() { return computerId; }
    public String getType() { return issueType; }
    public Date getDate() { return submissionDate; }
} 
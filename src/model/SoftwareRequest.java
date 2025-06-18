package model;

import java.util.Date;

public class SoftwareRequest {
    private String computerId;
    private String softwareName;
    private String version;
    private String urgency;
    private String justification;
    private Date requestDate;
    private String status;
    private String approvedBy;

    public SoftwareRequest(String computerId, String softwareName, String version,
                          String urgency, String justification, Date requestDate,
                          String status, String approvedBy) {
        this.computerId = computerId;
        this.softwareName = softwareName;
        this.version = version;
        this.urgency = urgency;
        this.justification = justification;
        this.requestDate = requestDate;
        this.status = status;
        this.approvedBy = approvedBy;
    }

    // Getters and Setters
    public String getComputerId() { return computerId; }
    public void setComputerId(String computerId) { this.computerId = computerId; }

    public String getSoftwareName() { return softwareName; }
    public void setSoftwareName(String softwareName) { this.softwareName = softwareName; }

    public String getVersion() { return version; }
    public void setVersion(String version) { this.version = version; }

    public String getUrgency() { return urgency; }
    public void setUrgency(String urgency) { this.urgency = urgency; }

    public String getJustification() { return justification; }
    public void setJustification(String justification) { this.justification = justification; }

    public Date getRequestDate() { return requestDate; }
    public void setRequestDate(Date requestDate) { this.requestDate = requestDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getApprovedBy() { return approvedBy; }
    public void setApprovedBy(String approvedBy) { this.approvedBy = approvedBy; }

    public String getId() { return computerId; }
} 
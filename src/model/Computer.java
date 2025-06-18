package model;

import java.util.Date;

/**
 * Computer model class representing a computer in the lab.
 */
public class Computer {
    private String id;
    private String lab;
    private String location;
    private String ipAddress;
    private String specifications;
    private String status;
    private String installDate;
    private String currentSession;
    private Date lastMaintenance;
    private String notes;

    // Default constructor
    public Computer() {
    }

    // Constructor with basic fields
    public Computer(String id, String lab, String location, String ipAddress, 
                   String specifications, String status, String installDate, String currentSession) {
        this.id = id;
        this.lab = lab;
        this.location = location;
        this.ipAddress = ipAddress;
        this.specifications = specifications;
        this.status = status;
        this.installDate = installDate;
        this.currentSession = currentSession;
    }

    // Full constructor
    public Computer(String id, String lab, String location, String ipAddress, 
                   String specifications, String status, String installDate, 
                   String currentSession, Date lastMaintenance, String notes) {
        this(id, lab, location, ipAddress, specifications, status, installDate, currentSession);
        this.lastMaintenance = lastMaintenance;
        this.notes = notes;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLab() {
        return lab;
    }

    public void setLab(String lab) {
        this.lab = lab;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getSpecifications() {
        return specifications;
    }

    public void setSpecifications(String specifications) {
        this.specifications = specifications;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getInstallDate() {
        return installDate;
    }

    public void setInstallDate(String installDate) {
        this.installDate = installDate;
    }

    public String getCurrentSession() {
        return currentSession;
    }

    public void setCurrentSession(String currentSession) {
        this.currentSession = currentSession;
    }

    public Date getLastMaintenance() {
        return lastMaintenance;
    }

    public void setLastMaintenance(Date lastMaintenance) {
        this.lastMaintenance = lastMaintenance;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    // Utility methods
    public boolean isAvailable() {
        return "Available".equalsIgnoreCase(status);
    }

    public boolean isInUse() {
        return "In Use".equalsIgnoreCase(status);
    }

    public boolean isUnderMaintenance() {
        return "Under Maintenance".equalsIgnoreCase(status);
    }

    @Override
    public String toString() {
        return String.format("Computer{id='%s', lab='%s', location='%s', status='%s'}", 
                           id, lab, location, status);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Computer computer = (Computer) obj;
        return id != null ? id.equals(computer.id) : computer.id == null;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}

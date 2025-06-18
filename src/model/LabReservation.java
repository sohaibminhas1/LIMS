package model;

import java.util.Date;

public class LabReservation {
    private String lab;
    private Date date;
    private String timeSlot;
    private String reservedBy;
    private String purpose;
    private String course;
    private String instructor;

    public LabReservation(String lab, Date date, String timeSlot, String reservedBy, 
                         String purpose, String course, String instructor) {
        this.lab = lab;
        this.date = date;
        this.timeSlot = timeSlot;
        this.reservedBy = reservedBy;
        this.purpose = purpose;
        this.course = course;
        this.instructor = instructor;
    }

    // Getters and Setters
    public String getLab() { return lab; }
    public void setLab(String lab) { this.lab = lab; }

    public Date getDate() { return date; }
    public void setDate(Date date) { this.date = date; }

    public String getTimeSlot() { return timeSlot; }
    public void setTimeSlot(String timeSlot) { this.timeSlot = timeSlot; }

    public String getReservedBy() { return reservedBy; }
    public void setReservedBy(String reservedBy) { this.reservedBy = reservedBy; }

    public String getPurpose() { return purpose; }
    public void setPurpose(String purpose) { this.purpose = purpose; }

    public String getCourse() { return course; }
    public void setCourse(String course) { this.course = course; }

    public String getInstructor() { return instructor; }
    public void setInstructor(String instructor) { this.instructor = instructor; }
} 
package controller;

import model.LabReservation;
import service.LabReservationService;
import java.util.List;
import java.util.Date;

public class LabReservationController extends BaseController {
    private final LabReservationService labReservationService;
    
    public LabReservationController(LabReservationService labReservationService) {
        this.labReservationService = labReservationService;
    }
    
    public void addReservation(String lab, Date date, String timeSlot, String reservedBy, 
                             String purpose, String course, String instructor) {
        // Validate inputs
        if (!isValidString(lab, 2, 50)) {
            throw new IllegalArgumentException("Invalid lab name");
        }
        if (date == null) {
            throw new IllegalArgumentException("Invalid date");
        }
        if (!isValidString(timeSlot, 5, 20)) {
            throw new IllegalArgumentException("Invalid time slot");
        }
        if (!isValidString(reservedBy, 2, 50)) {
            throw new IllegalArgumentException("Invalid reserver name");
        }
        if (!isValidString(purpose, 5, 200)) {
            throw new IllegalArgumentException("Invalid purpose");
        }
        if (!isValidString(course, 2, 50)) {
            throw new IllegalArgumentException("Invalid course");
        }
        if (!isValidString(instructor, 2, 50)) {
            throw new IllegalArgumentException("Invalid instructor");
        }
        
        // Sanitize inputs
        lab = sanitizeInput(lab);
        timeSlot = sanitizeInput(timeSlot);
        reservedBy = sanitizeInput(reservedBy);
        purpose = sanitizeInput(purpose);
        course = sanitizeInput(course);
        instructor = sanitizeInput(instructor);
        
        // Create lab reservation object
        LabReservation reservation = new LabReservation(
            lab,
            date,
            timeSlot,
            reservedBy,
            purpose,
            course,
            instructor
        );
        
        labReservationService.addReservation(reservation);
    }
    
    public void updateReservation(String lab, Date date, String timeSlot, String reservedBy, 
                                String purpose, String course, String instructor) {
        // Validate inputs
        if (!isValidString(lab, 2, 50)) {
            throw new IllegalArgumentException("Invalid lab name");
        }
        if (date == null) {
            throw new IllegalArgumentException("Invalid date");
        }
        if (!isValidString(timeSlot, 5, 20)) {
            throw new IllegalArgumentException("Invalid time slot");
        }
        if (!isValidString(reservedBy, 2, 50)) {
            throw new IllegalArgumentException("Invalid reserver name");
        }
        if (!isValidString(purpose, 5, 200)) {
            throw new IllegalArgumentException("Invalid purpose");
        }
        if (!isValidString(course, 2, 50)) {
            throw new IllegalArgumentException("Invalid course");
        }
        if (!isValidString(instructor, 2, 50)) {
            throw new IllegalArgumentException("Invalid instructor");
        }
        
        // Sanitize inputs
        lab = sanitizeInput(lab);
        timeSlot = sanitizeInput(timeSlot);
        reservedBy = sanitizeInput(reservedBy);
        purpose = sanitizeInput(purpose);
        course = sanitizeInput(course);
        instructor = sanitizeInput(instructor);
        
        // Create lab reservation object
        LabReservation reservation = new LabReservation(
            lab,
            date,
            timeSlot,
            reservedBy,
            purpose,
            course,
            instructor
        );
        
        labReservationService.updateReservation(reservation);
    }
    
    public void deleteReservation(String lab, Date date, String timeSlot) {
        // Validate inputs
        if (!isValidString(lab, 2, 50)) {
            throw new IllegalArgumentException("Invalid lab name");
        }
        if (date == null) {
            throw new IllegalArgumentException("Invalid date");
        }
        if (!isValidString(timeSlot, 5, 20)) {
            throw new IllegalArgumentException("Invalid time slot");
        }
        
        // Sanitize inputs
        lab = sanitizeInput(lab);
        timeSlot = sanitizeInput(timeSlot);
        
        // Create lab reservation object for deletion
        LabReservation reservation = new LabReservation(
            lab,
            date,
            timeSlot,
            "",
            "",
            "",
            ""
        );
        
        labReservationService.deleteReservation(reservation);
    }
    
    public List<LabReservation> getAllReservations() {
        return labReservationService.getAllReservations();
    }
    
    public List<LabReservation> getReservationsByLab(String lab) {
        if (!isValidString(lab, 2, 50)) {
            throw new IllegalArgumentException("Invalid lab name");
        }
        lab = sanitizeInput(lab);
        return labReservationService.getReservationsByLab(lab);
    }
    
    public List<LabReservation> getReservationsByDate(Date date) {
        if (date == null) {
            throw new IllegalArgumentException("Invalid date");
        }
        return labReservationService.getReservationsByDate(date);
    }
    
    public List<LabReservation> getReservationsByInstructor(String instructor) {
        if (!isValidString(instructor, 2, 50)) {
            throw new IllegalArgumentException("Invalid instructor");
        }
        instructor = sanitizeInput(instructor);
        return labReservationService.getReservationsByInstructor(instructor);
    }
    
    public boolean isTimeSlotAvailable(String lab, Date date, String timeSlot) {
        if (!isValidString(lab, 2, 50)) {
            throw new IllegalArgumentException("Invalid lab name");
        }
        if (date == null) {
            throw new IllegalArgumentException("Invalid date");
        }
        if (!isValidString(timeSlot, 5, 20)) {
            throw new IllegalArgumentException("Invalid time slot");
        }
        
        lab = sanitizeInput(lab);
        timeSlot = sanitizeInput(timeSlot);
        
        return labReservationService.isTimeSlotAvailable(lab, date, timeSlot);
    }

    public LabReservation getReservation(String lab, Date date, String timeSlot) {
        if (!isValidString(lab, 2, 50)) {
            throw new IllegalArgumentException("Invalid lab name");
        }
        if (date == null) {
            throw new IllegalArgumentException("Invalid date");
        }
        if (!isValidString(timeSlot, 5, 20)) {
            throw new IllegalArgumentException("Invalid time slot");
        }
        
        lab = sanitizeInput(lab);
        timeSlot = sanitizeInput(timeSlot);
        
        List<LabReservation> reservations = labReservationService.getAllReservations();
        for (LabReservation reservation : reservations) {
            if (reservation.getLab().equals(lab) &&
                reservation.getDate().equals(date) &&
                reservation.getTimeSlot().equals(timeSlot)) {
                return reservation;
            }
        }
        return null;
    }
} 
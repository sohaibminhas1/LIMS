package service;

import dao.LabReservationDAO;
import model.LabReservation;
import ui.TableRefreshManager;
import java.util.Date;
import java.util.List;

/**
 * Service class for managing lab reservations in the LIMS system.
 * Handles all business logic related to lab reservation management.
 */
public class LabReservationService {
    private LabReservationDAO labReservationDAO;

    public LabReservationService() {
        labReservationDAO = new LabReservationDAO();

        // Test database connection
        if (!labReservationDAO.testConnection()) {
            System.err.println("⚠️ Warning: Database connection failed. Some features may not work properly.");
        }
    }

    public List<LabReservation> getAllReservations() {
        return labReservationDAO.findAll();
    }

    public List<LabReservation> getReservationsByLab(String lab) {
        return labReservationDAO.findByLabName(lab);
    }

    public List<LabReservation> getReservationsByDate(Date date) {
        return labReservationDAO.findByDate(date.toString());
    }

    public List<LabReservation> getReservationsByInstructor(String instructor) {
        // Filter by instructor using DAO results
        return getAllReservations().stream()
            .filter(reservation -> reservation.getInstructor().equals(instructor))
            .collect(java.util.stream.Collectors.toList());
    }

    public void addReservation(LabReservation reservation) {
        boolean success = labReservationDAO.insert(reservation);

        if (success) {
            // Refresh the dashboard table immediately
            TableRefreshManager.getInstance().refreshTable("lab_reservations");
        }
    }

    public void updateReservation(LabReservation reservation) {
        boolean success = labReservationDAO.update(reservation);

        if (success) {
            TableRefreshManager.getInstance().refreshTable("lab_reservations");
        }
    }

    public void deleteReservation(LabReservation reservation) {
        // Find reservation by requester name first, then delete by ID
        LabReservation found = labReservationDAO.findByRequesterName(reservation.getReservedBy());
        if (found != null) {
            boolean success = labReservationDAO.delete(String.valueOf(found.hashCode())); // Using hashCode as ID placeholder

            if (success) {
                TableRefreshManager.getInstance().refreshTable("lab_reservations");
            }
        }
    }

    public boolean isTimeSlotAvailable(String lab, Date date, String timeSlot) {
        List<LabReservation> reservations = getReservationsByLab(lab);
        for (LabReservation reservation : reservations) {
            if (reservation.getDate().equals(date) &&
                reservation.getTimeSlot().equals(timeSlot)) {
                return false;
            }
        }
        return true;
    }
}
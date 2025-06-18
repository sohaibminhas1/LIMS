package service;

import dao.FeedbackDAO;
import model.Feedback;
import ui.TableRefreshManager;
import java.util.Date;
import java.util.List;

/**
 * Service class for managing feedback in the LIMS system.
 * Handles all business logic related to feedback management.
 */
public class FeedbackService {
    private FeedbackDAO feedbackDAO;

    public FeedbackService() {
        feedbackDAO = new FeedbackDAO();

        // Test database connection
        if (!feedbackDAO.testConnection()) {
            System.err.println("⚠️ Warning: Database connection failed. Some features may not work properly.");
        }
    }

    public List<Feedback> getAllFeedback() {
        return feedbackDAO.findAll();
    }

    public List<Feedback> getFeedbackByCategory(String category) {
        return feedbackDAO.findByCategory(category);
    }

    public List<Feedback> getFeedbackByStatus(String status) {
        return feedbackDAO.findByStatus(status);
    }

    public void addFeedback(Feedback feedback) {
        boolean success = feedbackDAO.insert(feedback);

        if (success) {
            // Refresh the dashboard table immediately
            TableRefreshManager.getInstance().refreshTable("feedback");
        }
    }

    public void updateFeedback(Feedback feedback) {
        boolean success = feedbackDAO.update(feedback);

        if (success) {
            TableRefreshManager.getInstance().refreshTable("feedback");
        }
    }

    public void deleteFeedback(String name, Date submissionDate) {
        // Find feedback by name first, then delete by ID
        Feedback feedback = feedbackDAO.findByName(name);
        if (feedback != null) {
            boolean success = feedbackDAO.delete(String.valueOf(feedback.hashCode())); // Using hashCode as ID placeholder

            if (success) {
                TableRefreshManager.getInstance().refreshTable("feedback");
            }
        }
    }

    public void respondToFeedback(String name, Date submissionDate, String response) {
        Feedback feedback = feedbackDAO.findByName(name);
        if (feedback != null) {
            feedback.setResponse(response);
            feedback.setStatus("Responded");
            updateFeedback(feedback);
        }
    }

    public void updateFeedbackStatus(String name, Date submissionDate, String status) {
        Feedback feedback = feedbackDAO.findByName(name);
        if (feedback != null) {
            feedback.setStatus(status);
            updateFeedback(feedback);
        }
    }
}
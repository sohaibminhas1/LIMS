package controller;

import model.Feedback;
import service.FeedbackService;
import java.util.List;
import java.util.Date;

public class FeedbackController extends BaseController {
    private final FeedbackService feedbackService;
    
    public FeedbackController(FeedbackService feedbackService) {
        this.feedbackService = feedbackService;
    }
    
    public void addFeedback(String name, String category, String content) {
        // Validate inputs
        if (!isValidString(name, 2, 50)) {
            throw new IllegalArgumentException("Invalid name");
        }
        if (!isValidString(category, 2, 50)) {
            throw new IllegalArgumentException("Invalid category");
        }
        if (!isValidString(content, 5, 500)) {
            throw new IllegalArgumentException("Invalid content");
        }
        
        // Sanitize inputs
        name = sanitizeInput(name);
        category = sanitizeInput(category);
        content = sanitizeInput(content);
        
        // Create feedback object
        Feedback feedback = new Feedback(
            name,
            category,
            content,
            new Date(),
            "Pending",
            ""
        );
        
        feedbackService.addFeedback(feedback);
    }
    
    public void updateFeedback(String name, Date submissionDate, String content, String status) {
        // Validate inputs
        if (!isValidString(name, 2, 50)) {
            throw new IllegalArgumentException("Invalid name");
        }
        if (submissionDate == null) {
            throw new IllegalArgumentException("Invalid submission date");
        }
        if (!isValidString(content, 5, 500)) {
            throw new IllegalArgumentException("Invalid content");
        }
        if (!isValidString(status, 2, 20)) {
            throw new IllegalArgumentException("Invalid status");
        }
        
        // Sanitize inputs
        name = sanitizeInput(name);
        content = sanitizeInput(content);
        status = sanitizeInput(status);
        
        // Create feedback object
        Feedback feedback = new Feedback(
            name,
            "",  // category is not needed for update
            content,
            submissionDate,
            status,
            ""
        );
        
        feedbackService.updateFeedback(feedback);
    }
    
    public void respondToFeedback(String name, Date submissionDate, String response) {
        // Validate inputs
        if (!isValidString(name, 2, 50)) {
            throw new IllegalArgumentException("Invalid name");
        }
        if (submissionDate == null) {
            throw new IllegalArgumentException("Invalid submission date");
        }
        if (!isValidString(response, 5, 500)) {
            throw new IllegalArgumentException("Invalid response");
        }
        
        // Sanitize inputs
        name = sanitizeInput(name);
        response = sanitizeInput(response);
        
        feedbackService.respondToFeedback(name, submissionDate, response);
    }
    
    public void updateFeedbackStatus(String name, Date submissionDate, String status) {
        // Validate inputs
        if (!isValidString(name, 2, 50)) {
            throw new IllegalArgumentException("Invalid name");
        }
        if (submissionDate == null) {
            throw new IllegalArgumentException("Invalid submission date");
        }
        if (!isValidString(status, 2, 20)) {
            throw new IllegalArgumentException("Invalid status");
        }
        
        // Sanitize inputs
        name = sanitizeInput(name);
        status = sanitizeInput(status);
        
        feedbackService.updateFeedbackStatus(name, submissionDate, status);
    }
    
    public void deleteFeedback(String name, Date submissionDate) {
        // Validate inputs
        if (!isValidString(name, 2, 50)) {
            throw new IllegalArgumentException("Invalid name");
        }
        if (submissionDate == null) {
            throw new IllegalArgumentException("Invalid submission date");
        }
        
        // Sanitize inputs
        name = sanitizeInput(name);
        
        feedbackService.deleteFeedback(name, submissionDate);
    }
    
    public List<Feedback> getAllFeedback() {
        return feedbackService.getAllFeedback();
    }
    
    public List<Feedback> getFeedbackByCategory(String category) {
        if (!isValidString(category, 2, 50)) {
            throw new IllegalArgumentException("Invalid category");
        }
        category = sanitizeInput(category);
        return feedbackService.getFeedbackByCategory(category);
    }
    
    public List<Feedback> getFeedbackByStatus(String status) {
        if (!isValidString(status, 2, 20)) {
            throw new IllegalArgumentException("Invalid status");
        }
        status = sanitizeInput(status);
        return feedbackService.getFeedbackByStatus(status);
    }
} 
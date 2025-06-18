package controller;

import model.*;
import service.*;
import java.util.*;
import java.text.SimpleDateFormat;

public class ReportController extends BaseController {
    private final ComplaintService complaintService;
    private final SoftwareRequestService softwareRequestService;
    private final FeedbackService feedbackService;
    private final SimpleDateFormat dateFormat;

    public ReportController(ComplaintService complaintService, 
                          SoftwareRequestService softwareRequestService,
                          FeedbackService feedbackService) {
        this.complaintService = complaintService;
        this.softwareRequestService = softwareRequestService;
        this.feedbackService = feedbackService;
        this.dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    }

    public List<Map<String, String>> getAllReports() {
        List<Map<String, String>> reports = new ArrayList<>();
        
        // Get complaints
        List<Complaint> complaints = complaintService.getAllComplaints();
        for (Complaint complaint : complaints) {
            reports.add(createReportMap(
                complaint.getComputerId(),
                "Complaint",
                complaint.getStatus(),
                complaint.getSubmissionDate(),
                complaint.getDescription()
            ));
        }
        
        // Get software requests
        List<SoftwareRequest> requests = softwareRequestService.getAllRequests();
        for (SoftwareRequest request : requests) {
            reports.add(createReportMap(
                request.getComputerId(),
                "Software",
                request.getStatus(),
                request.getRequestDate(),
                request.getJustification()
            ));
        }
        
        // Get feedback
        List<Feedback> feedbacks = feedbackService.getAllFeedback();
        for (Feedback feedback : feedbacks) {
            reports.add(createReportMap(
                feedback.getName(),
                "Feedback",
                feedback.getStatus(),
                feedback.getSubmissionDate(),
                feedback.getContent()
            ));
        }
        
        return reports;
    }

    public Map<String, String> getReportDetails(String reportId) {
        if (reportId == null || reportId.trim().isEmpty()) {
            throw new IllegalArgumentException("Report ID cannot be empty");
        }

        // Try to find in complaints
        List<Complaint> complaints = complaintService.getAllComplaints();
        for (Complaint complaint : complaints) {
            if (complaint.getComputerId().equals(reportId)) {
                return createReportDetailsMap(
                    complaint.getComputerId(),
                    "Complaint",
                    complaint.getStatus(),
                    complaint.getSubmissionDate(),
                    complaint.getDescription()
                );
            }
        }
        
        // Try to find in software requests
        List<SoftwareRequest> requests = softwareRequestService.getAllRequests();
        for (SoftwareRequest request : requests) {
            if (request.getComputerId().equals(reportId)) {
                return createReportDetailsMap(
                    request.getComputerId(),
                    "Software",
                    request.getStatus(),
                    request.getRequestDate(),
                    request.getJustification()
                );
            }
        }
        
        // Try to find in feedback
        List<Feedback> feedbacks = feedbackService.getAllFeedback();
        for (Feedback feedback : feedbacks) {
            if (feedback.getName().equals(reportId)) {
                return createReportDetailsMap(
                    feedback.getName(),
                    "Feedback",
                    feedback.getStatus(),
                    feedback.getSubmissionDate(),
                    feedback.getContent()
                );
            }
        }
        
        return null;
    }

    private Map<String, String> createReportMap(String id, String type, String status, Date date, String description) {
        Map<String, String> report = new HashMap<>();
        report.put("id", id);
        report.put("type", type);
        report.put("status", status);
        report.put("date", dateFormat.format(date));
        report.put("description", description);
        return report;
    }

    private Map<String, String> createReportDetailsMap(String id, String type, String status, Date date, String description) {
        Map<String, String> details = new HashMap<>();
        details.put("id", id);
        details.put("type", type);
        details.put("status", status);
        details.put("date", dateFormat.format(date));
        details.put("description", description);
        return details;
    }
} 
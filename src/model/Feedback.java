package model;

import java.util.Date;

public class Feedback {
    private String name;
    private String category;
    private String content;
    private Date submissionDate;
    private String status;
    private String response;

    public Feedback(String name, String category, String content, 
                   Date submissionDate, String status, String response) {
        this.name = name;
        this.category = category;
        this.content = content;
        this.submissionDate = submissionDate;
        this.status = status;
        this.response = response;
    }

    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public Date getSubmissionDate() { return submissionDate; }
    public void setSubmissionDate(Date submissionDate) { this.submissionDate = submissionDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getResponse() { return response; }
    public void setResponse(String response) { this.response = response; }
} 
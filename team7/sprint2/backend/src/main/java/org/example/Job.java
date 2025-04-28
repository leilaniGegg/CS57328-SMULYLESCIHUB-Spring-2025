package org.example;

import java.time.LocalDateTime;

public class Job {
    private Long id;
    private String title;
    private String company;
    private String description;
    private String applicationInstructions;
    private String status;
    private LocalDateTime datePosted;

    public Job() {} // default constructor

    public Job(Long id, String title, String company, String description, String applicationInstructions, String status) {
        this.id = id;
        this.title = title;
        this.company = company;
        this.description = description;
        this.applicationInstructions = applicationInstructions;
        this.status = status;
        this.datePosted = LocalDateTime.now();
    }

    // Getters and setters

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getCompany() { return company; }
    public void setCompany(String company) { this.company = company; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getApplicationInstructions() { return applicationInstructions; }
    public void setApplicationInstructions(String applicationInstructions) { this.applicationInstructions = applicationInstructions; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getDatePosted() { return datePosted; }
    public void setDatePosted(LocalDateTime datePosted) { this.datePosted = datePosted; }
}

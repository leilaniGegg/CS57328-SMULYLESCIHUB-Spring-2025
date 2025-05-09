package org.example;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Job {
    private Long id;
    private String title;
    private String company;
    private String description;
    private String applicationInstructions;
    private String status;
    private LocalDateTime datePosted;
    // Add the employerId field that the controller uses
    private Long employerId;


    // List of applicants for this job
    // Initialize here or in constructor/setter
    private List<Application> applicants = new ArrayList<>();


    public Job() {} // Default constructor is needed for Spring/JSON deserialization

    // Existing constructor - doesn't include employerId
    // If employerId is always present, consider adding it here too,
    // but the controller sets it using the setter in some cases.
    public Job(Long id, String title, String company, String description, String applicationInstructions, String status) {
        this.id = id;
        this.title = title;
        this.company = company;
        this.description = description;
        this.applicationInstructions = applicationInstructions;
        this.status = status;
        this.datePosted = LocalDateTime.now(); // Setting datePosted here might conflict with controller setting it
        this.applicants = new ArrayList<>(); // Initialize list here as well
    }

    // Recommended constructor matching fields often set at creation
    public Job(Long id, String title, String company, String description, String applicationInstructions, String status, LocalDateTime datePosted, Long employerId) {
        this.id = id;
        this.title = title;
        this.company = company;
        this.description = description;
        this.applicationInstructions = applicationInstructions;
        this.status = status;
        this.datePosted = datePosted;
        this.employerId = employerId;
        this.applicants = new ArrayList<>(); // Initialize list
    }


    // --- Getters and setters ---

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

    // Add getter and setter for employerId
    public Long getEmployerId() { return employerId; }
    public void setEmployerId(Long employerId) { this.employerId = employerId; }

    // Getters and setters for the applicants list
    // Make sure to never return null for the list from the getter
    public List<Application> getApplicants() {
        if (applicants == null) {
            applicants = new ArrayList<>(); // Ensure list is never null when accessed
        }
        return applicants;
    }

    public void setApplicants(List<Application> applicants) {
        this.applicants = applicants;
    }

    // Method to add a single applicant
    public void addApplicant(Application application) {
        if (this.applicants == null) {
            this.applicants = new ArrayList<>(); // Ensure list is initialized before adding
        }
        this.applicants.add(application);
    }

    // Optional: Override toString, equals, hashCode if needed
}
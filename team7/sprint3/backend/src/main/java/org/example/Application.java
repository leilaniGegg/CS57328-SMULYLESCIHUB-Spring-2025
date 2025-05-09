package org.example;

import java.time.LocalDateTime;

public class Application {
    private Long id;
    private Long jobId; // ID of the job this application is for
    private String name; // Applicant's name
    private String resumeFileName; // The stored filename of the resume
    private String status; // Application status (e.g., "submitted", "reviewed", "rejected")
    private LocalDateTime dateApplied; // Date the application was submitted

    // Default constructor (often needed for serialization/deserialization)
    public Application() {}

    // Constructor matching the usage in JobController.applyToJob
    public Application(Long id, Long jobId, String name, String resumeFileName, String status) {
        this.id = id;
        this.jobId = jobId;
        this.name = name;
        this.resumeFileName = resumeFileName;
        this.status = status;
        this.dateApplied = LocalDateTime.now(); // Set date applied when created via this constructor
    }

    // You might want other constructors depending on your needs

    // --- Getters ---
    // Add getters for fields the JobController needs to read (id, jobId, name, resumeFileName, status)
    public Long getId() {
        return id;
    }

    public Long getJobId() {
        return jobId;
    }

    public String getName() {
        return name;
    }

    public String getResumeFileName() {
        return resumeFileName;
    }

    public String getStatus() {
        return status;
    }

    public LocalDateTime getDateApplied() {
        return dateApplied;
    }


    // --- Setters ---
    // Add setters if you need to modify these fields after creation
    // The JobController doesn't strictly need setters based on your current code,
    // but they are often useful.
    public void setId(Long id) {
        this.id = id;
    }

    public void setJobId(Long jobId) {
        this.jobId = jobId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setResumeFileName(String resumeFileName) {
        this.resumeFileName = resumeFileName;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setDateApplied(LocalDateTime dateApplied) {
        this.dateApplied = dateApplied;
    }
}
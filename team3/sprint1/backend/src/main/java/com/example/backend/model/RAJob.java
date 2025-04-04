package com.example.backend.model;

import jakarta.persistence.*;
import java.sql.Date;
import java.sql.Time;
import java.util.List;

@Entity
@Table(name = "jobs")
public class RAJob {
    public enum Status {
        OPEN, CLOSED
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    private Status status;

    private Time updateTime;

    private String department;

    private String location;

    private Date startDate;

    private Date endDate;

    private String timeCommitment;

    private boolean paid;

    private Double stipendAmount;

    @ElementCollection
    private List<String> preferredMajors;

    @ElementCollection
    private List<String> skillsRequired;

    // --- Getters & Setters ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Time getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Time updateTime) {
        this.updateTime = updateTime;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getTimeCommitment() {
        return timeCommitment;
    }

    public void setTimeCommitment(String timeCommitment) {
        this.timeCommitment = timeCommitment;
    }

    public boolean isPaid() {
        return paid;
    }

    public void setPaid(boolean paid) {
        this.paid = paid;
    }

    public Double getStipendAmount() {
        return stipendAmount;
    }

    public void setStipendAmount(Double stipendAmount) {
        this.stipendAmount = stipendAmount;
    }

    public List<String> getPreferredMajors() {
        return preferredMajors;
    }

    public void setPreferredMajors(List<String> preferredMajors) {
        this.preferredMajors = preferredMajors;
    }

    public List<String> getSkillsRequired() {
        return skillsRequired;
    }

    public void setSkillsRequired(List<String> skillsRequired) {
        this.skillsRequired = skillsRequired;
    }
}
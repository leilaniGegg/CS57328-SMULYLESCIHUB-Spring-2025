package com.example.ta_ms.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.util.Date;
import java.util.Set;
import java.util.HashSet;
import jakarta.persistence.CascadeType;


@Entity
@Table(name = "job_postings")
public class JobPosting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int jobid;

    private String facultyName;
    private String facultyEmail;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "course_number", nullable = false)
    private Course course;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "jobposting_required_courses",
            joinColumns = @JoinColumn(name = "jobposting_id"),
            inverseJoinColumns = @JoinColumn(name = "course_number", referencedColumnName = "courseNumber")
    )
    private Set<Course> requiredCourses = new HashSet<>();

    private String skills;

    @ElementCollection
    @CollectionTable(
            name = "jobposting_standings",
            joinColumns = @JoinColumn(name = "jobposting_id")
    )
    @Column(name = "standing")
    private Set<String> standings = new HashSet<>();

    private String jobDetails;

// Constructors
    public JobPosting() {
    }

    public JobPosting(String facultyName, String facultyEmail, Course course, String jobDetails, Date createdDate) {
        this.facultyName = facultyName;
        this.facultyEmail = facultyEmail;
        this.course = course;
        this.jobDetails = jobDetails;
    }

    // Getters and Setters
    public int getJobid() {
        return jobid;
    }

    public void setJobid(int jobid) {
        this.jobid = jobid;
    }

    public String getFacultyName() {
        return facultyName;
    }

    public void setFacultyName(String facultyName) {
        this.facultyName = facultyName;
    }

    public String getFacultyEmail() {
        return facultyEmail;
    }

    public void setFacultyEmail(String facultyEmail) {
        this.facultyEmail = facultyEmail;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public Set<Course> getRequiredCourses() {
        return requiredCourses;
    }

    public void setRequiredCourses(Set<Course> requiredCourses) {
        this.requiredCourses = requiredCourses;
    }

    public String getSkills() {
        return skills;
    }

    public void setSkills(String skills) {
        this.skills = skills;
    }

    public Set<String> getStandings() {
        return standings;
    }

    public void setStandings(Set<String> standings) {
        this.standings = standings;
    }

    public String getJobDetails() {
        return jobDetails;
    }

    public void setJobDetails(String jobDetails) {
        this.jobDetails = jobDetails;
    }

    // Example entity methods
    public void updatePosting(String details) {
        this.jobDetails = details;
    }

    public String getPostingInfo() {
        String courseNum = (course != null) ? course.getCourseNumber() : "[NULL COURSE]";
        return "Job ID: " + jobid +
                " for course " + courseNum +
                ". Details: " + jobDetails;
    }
}
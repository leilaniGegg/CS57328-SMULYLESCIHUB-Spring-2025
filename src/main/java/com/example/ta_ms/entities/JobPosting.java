package com.example.universityta.entities;

import jakarta.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.List;

@Entity
@Table(name = "job_postings")
public class JobPosting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int jobid;

    private String facultyName; // changed from facultyId

    @OneToOne
    @JoinColumn(name = "course_number")  // matches Course.courseNumber
    private Course course;

    // A many-to-many table that links a job posting to multiple required courses
    @ManyToMany
    @JoinTable(
            name = "jobposting_required_courses",
            joinColumns = @JoinColumn(name = "jobposting_id"),
            inverseJoinColumns = @JoinColumn(name = "course_number")
    )
    private Set<Course> requiredCourses = new HashSet<>();

    // A simple string for listing skills (could also use @ElementCollection if multiple)
    private String skills;

    // A set of possible standings (Freshman, Sophomore, Junior, Senior, Graduate)
    @ElementCollection
    @CollectionTable(
            name = "jobposting_standings",
            joinColumns = @JoinColumn(name = "jobposting_id")
    )
    @Column(name = "standing")
    private Set<String> standings = new HashSet<>();

    private String jobDetails;
    private Date createdDate;

    // Constructors
    public JobPosting() {}

    public JobPosting(String facultyName, Course course, String jobDetails, Date createdDate) {
        this.facultyName = facultyName;
        this.course = course;
        this.jobDetails = jobDetails;
        this.createdDate = createdDate;
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

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    // Example entity methods
    public void updatePosting(String details) {
        this.jobDetails = details;
    }

    public String getPostingInfo() {
        return "Job ID: " + jobid +
                " for course " + (course != null ? course.getCourseNumber() : "N/A") +
                ". Details: " + jobDetails;
    }
}

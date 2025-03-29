package com.example.ta_ms.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "courses")
public class Course {

    @Id
    private String courseNumber;

    private String courseName;
    private String description;

    // Constructors
    public Course() {}

    public Course(String courseNumber, String courseName, String description) {
        this.courseNumber = courseNumber;
        this.courseName = courseName;
        this.description = description;
    }

    // Getters and Setters
    public String getCourseNumber() {
        return courseNumber;
    }

    public void setCourseNumber(String courseNumber) {
        this.courseNumber = courseNumber;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    // Example helper method
    public String getCourseDetails() {
        return courseName + ": " + description;
    }
}

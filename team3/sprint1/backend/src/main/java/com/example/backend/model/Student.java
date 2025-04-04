package com.example.backend.model;

import jakarta.persistence.*;

// TODO create students table
@Entity
@Table(name = "students")
public class Student {
    // TODO could potentially use these for grade
    public enum Grade {
        Freshman,
        Sophomore,
        Junior,
        Senior
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String grade;

    private float gpa;

    private String major;

    private String email;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public float getGPA() {
        return gpa;
    }

    public void setGPA(float gpa) {
        this.gpa = gpa;
    }

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}

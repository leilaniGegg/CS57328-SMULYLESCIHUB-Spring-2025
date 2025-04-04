package com.example.backend.model;

import jakarta.persistence.*;

// TODO create applications table
@Entity
@Table(name = "applications")
public class RAApplication {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long studentId;

    /*
    private String studentGrade;

    private float studentGPA;

    private String studentMajor;

    private String studentEmail;

    public Long getId() {
        return id;
    }

    void setId(Long id) {
        this.id = id;
    }

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public String getStudentGrade() {
        return studentGrade;
    }

    public void setStudentGrade(String studentGrade) {
        this.studentGrade = studentGrade;
    }

    public float getStudentGPA() {
        return studentGPA;
    }

    public void setStudentGPA(float studentGPA) {
        this.studentGPA = studentGPA;
    }

    public String getStudentMajor() {
        return studentMajor;
    }

    public void setStudentMajor(String studentMajor) {
        this.studentMajor = studentMajor;
    }

    public String getStudentEmail() {
        return studentEmail;
    }

    public void setStudentEmail(String studentEmail) {
        this.studentEmail = studentEmail;
    }
     */
}

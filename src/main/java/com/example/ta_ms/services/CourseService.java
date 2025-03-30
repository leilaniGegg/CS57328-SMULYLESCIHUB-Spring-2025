package com.example.ta_ms.services;

import com.example.ta_ms.entities.Course;
import com.example.ta_ms.repositories.CourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CourseService {

    @Autowired
    private CourseRepository courseRepository;

    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    public List<Course> filterCoursesByName(String courseName) {
        return courseRepository.findByCourseNameContainingIgnoreCase(courseName);
    }

    public List<Course> filterCoursesByNumber(String courseNumber) {
        return courseRepository.findByCourseNumberContainingIgnoreCase(courseNumber);
    }
}
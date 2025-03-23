package com.example.universityta.controllers;

import com.example.universityta.entities.Course;
import com.example.universityta.services.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/courses")
public class FilterClassesController {

    @Autowired
    private CourseService courseService;

    @GetMapping
    public List<Course> getCourses(
            @RequestParam(required = false) String courseNumber,
            @RequestParam(required = false) String courseName
            // add quries for skill, standing, etc.
    ) {
        // Example: filter by courseNumber if present, else by courseName, else return all
        if (courseNumber != null && !courseNumber.isEmpty()) {
            return courseService.filterCoursesByNumber(courseNumber);
        } else if (courseName != null && !courseName.isEmpty()) {
            return courseService.filterCoursesByName(courseName);
        } else {
            return courseService.getAllCourses();
        }
    }
}

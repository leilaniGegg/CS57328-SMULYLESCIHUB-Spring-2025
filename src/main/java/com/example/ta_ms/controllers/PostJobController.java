package com.example.universityta.controllers;

import com.example.universityta.entities.Course;
import com.example.universityta.entities.JobPosting;
import com.example.universityta.repositories.CourseRepository;
import com.example.universityta.services.JobPostingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/jobpostings")
public class PostJobController {

    @Autowired
    private JobPostingService jobPostingService;

    @Autowired
    private CourseRepository courseRepository;

    @PostMapping
    public ResponseEntity<?> createJobPosting(@RequestBody JobPosting jobData) {
        try {
            // 1. Check if the main (primary) course already exists.
            if (jobData.getCourse() != null && jobData.getCourse().getCourseNumber() != null) {
                Course existingCourse = courseRepository.findById(jobData.getCourse().getCourseNumber())
                        .orElse(null);

                if (existingCourse != null) {
                    // If found, attach existing entity to avoid duplicates
                    jobData.setCourse(existingCourse);
                } else {
                    // If not found, we are creating a new Course
                    // Provide default placeholders if no name/description are given
                    if (jobData.getCourse().getCourseName() == null || jobData.getCourse().getCourseName().isEmpty()) {
                        jobData.getCourse().setCourseName("New Course");
                    }
                    if (jobData.getCourse().getDescription() == null || jobData.getCourse().getDescription().isEmpty()) {
                        jobData.getCourse().setDescription("No description provided.");
                    }
                }
            }

            // 2. Check each "required course" to see if it exists; if not, create it.
            if (jobData.getRequiredCourses() != null && !jobData.getRequiredCourses().isEmpty()) {
                jobData.setRequiredCourses(
                        jobData.getRequiredCourses().stream().map(course -> {
                            if (course.getCourseNumber() != null) {
                                Course existing = courseRepository.findById(course.getCourseNumber()).orElse(null);
                                if (existing != null) {
                                    return existing;
                                } else {
                                    // If a required course does not exist, create it with defaults
                                    if (course.getCourseName() == null || course.getCourseName().isEmpty()) {
                                        course.setCourseName("New Course");
                                    }
                                    if (course.getDescription() == null || course.getDescription().isEmpty()) {
                                        course.setDescription("No description provided.");
                                    }
                                    return course;
                                }
                            }
                            return course; // If courseNumber is null, just return as is
                        }).collect(Collectors.toSet())
                );
            }

            // 3. Save job posting. Cascade settings in JobPosting will handle new courses.
            JobPosting saved = jobPostingService.saveJobPosting(jobData);
            return new ResponseEntity<>(saved, HttpStatus.CREATED);

        } catch (Exception e) {
            // 4. Catch any exceptions for more explicit error feedback
            e.printStackTrace();
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error creating job posting: " + e.getMessage());
        }
    }
}

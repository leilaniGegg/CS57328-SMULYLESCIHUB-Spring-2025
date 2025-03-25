package com.example.universityta.controllers;

import com.example.universityta.entities.Course;
import com.example.universityta.entities.JobPosting;
import com.example.universityta.repositories.CourseRepository;
import com.example.universityta.services.JobPostingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/jobpostings")
public class PostJobController {

    @Autowired
    private JobPostingService jobPostingService;

    @Autowired
    private CourseRepository courseRepository;

    @PostMapping
    public JobPosting createJobPosting(@RequestBody JobPosting jobData) {
        // Ensure the primary course exists in the database.
        if (jobData.getCourse() != null && jobData.getCourse().getCourseNumber() != null) {
            Course existingCourse = courseRepository.findById(jobData.getCourse().getCourseNumber()).orElse(null);
            if (existingCourse != null) {
                jobData.setCourse(existingCourse);
            } else {
                // Create and persist the new course.
                Course newCourse = new Course(
                        jobData.getCourse().getCourseNumber(),
                        jobData.getCourse().getCourseName(),
                        jobData.getCourse().getDescription()
                );
                courseRepository.save(newCourse);
                jobData.setCourse(newCourse);
            }
        }

        // For each required course, ensure we use the persisted version.
        if (jobData.getRequiredCourses() != null && !jobData.getRequiredCourses().isEmpty()) {
            jobData.setRequiredCourses(
                    jobData.getRequiredCourses().stream().map(course -> {
                        if (course.getCourseNumber() != null) {
                            return courseRepository.findById(course.getCourseNumber()).orElse(course);
                        }
                        return course;
                    }).collect(java.util.stream.Collectors.toSet())
            );
        }

        // Save and return the job posting.
        return jobPostingService.saveJobPosting(jobData);
    }
}

// PostJobController.java
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
        // Check if the primary course exists. If yes, attach it from the database.
        if (jobData.getCourse() != null && jobData.getCourse().getCourseNumber() != null) {
            Course existingCourse = courseRepository.findById(jobData.getCourse().getCourseNumber()).orElse(null);
            if (existingCourse != null) {
                jobData.setCourse(existingCourse);
            } else {
                // If the course doesn't exist, create and persist it using provided details.
                Course newCourse = new Course(
                        jobData.getCourse().getCourseNumber(),
                        jobData.getCourse().getCourseName(),       // expecting course name from the front end
                        jobData.getCourse().getDescription()         // expecting course description from the front end
                );
                courseRepository.save(newCourse);
                jobData.setCourse(newCourse);
            }
        }


        // For each required course, check if it exists to avoid duplicate entries.
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


        // Save job posting. Cascade settings will persist any new course data.
        return jobPostingService.saveJobPosting(jobData);
    }
}

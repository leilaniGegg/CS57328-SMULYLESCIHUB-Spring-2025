package com.example.universityta.controllers;

import com.example.universityta.entities.JobPosting;
import com.example.universityta.services.JobPostingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/jobpostings")
public class PostJobController {

    @Autowired
    private JobPostingService jobPostingService;

    @PostMapping
    public JobPosting createJobPosting(@RequestBody JobPosting jobData) {
        // Additional validation should be added here (e.g., verifying facultyName, requiredCourses, etc)
        return jobPostingService.saveJobPosting(jobData);
    }
}

package com.example.ta_ms.controllers;

import com.example.ta_ms.entities.JobPosting;
import com.example.ta_ms.services.JobPostingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/jobpostings")
public class FilterClassesController {

    @Autowired
    private JobPostingService jobPostingService;

    // Note: We added an additional "/filter" path to differentiate filtering from a plain GET all.
    @GetMapping("/filter")
    public ResponseEntity<?> getFilteredJobPostings(
            @RequestParam(required = false) String courseNumber,
            @RequestParam(required = false) String courseName,
            @RequestParam(required = false) String skill,
            @RequestParam(required = false) String instructorName,
            @RequestParam(required = false) String standing
    ) {
        try {
            List<JobPosting> postings;
            // Prioritize filtering by skill/instructor/standing if provided
            if ((skill != null && !skill.trim().isEmpty()) ||
                    (instructorName != null && !instructorName.trim().isEmpty()) ||
                    (standing != null && !standing.trim().isEmpty())) {
                postings = jobPostingService.filterJobPostings(skill, instructorName, standing);
            } else if ((courseNumber != null && !courseNumber.trim().isEmpty()) ||
                    (courseName != null && !courseName.trim().isEmpty())) {
                postings = jobPostingService.filterJobPostingsByCourse(courseNumber, courseName);
            } else {
                postings = jobPostingService.getAllJobPostings();
            }
            return ResponseEntity.ok(postings);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error filtering job postings: " + e.getMessage());
        }
    }
}
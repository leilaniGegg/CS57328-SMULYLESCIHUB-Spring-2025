package com.example.ta_ms.services;

import com.example.ta_ms.entities.JobPosting;
import com.example.ta_ms.repositories.JobPostingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class JobPostingService {

    @Autowired
    private JobPostingRepository jobPostingRepository;

    public JobPosting saveJobPosting(JobPosting jobPosting) {
        return jobPostingRepository.save(jobPosting);
    }

    public List<JobPosting> getAllJobPostings() {
        return jobPostingRepository.findAll();
    }

    // Filters by skill, instructor name (facultyName), and standing (grade level)
    public List<JobPosting> filterJobPostings(String skill, String instructorName, String standing) {
        List<JobPosting> postings = jobPostingRepository.findAll();
        return postings.stream().filter(posting -> {
            boolean matches = true;
            if (skill != null && !skill.isEmpty()) {
                matches &= posting.getSkills() != null &&
                        posting.getSkills().toLowerCase().contains(skill.toLowerCase());
            }
            if (instructorName != null && !instructorName.isEmpty()) {
                matches &= posting.getFacultyName() != null &&
                        posting.getFacultyName().toLowerCase().contains(instructorName.toLowerCase());
            }
            if (standing != null && !standing.isEmpty()) {
                matches &= posting.getStandings() != null &&
                        posting.getStandings().stream().anyMatch(s -> s.equalsIgnoreCase(standing));
            }
            return matches;
        }).collect(Collectors.toList());
    }

    // Additional filtering by course attributes (course number and/or course name)
    public List<JobPosting> filterJobPostingsByCourse(String courseNumber, String courseName) {
        List<JobPosting> postings = jobPostingRepository.findAll();
        return postings.stream().filter(posting -> {
            boolean matches = true;
            if (courseNumber != null && !courseNumber.isEmpty()) {
                if (posting.getCourse() == null) {
                    System.err.println("Error: JobPosting with ID " + posting.getJobid() + " has a null course reference.");
                    return false;
                }
                matches &= posting.getCourse().getCourseNumber().toLowerCase().contains(courseNumber.toLowerCase());
            }
            if (courseName != null && !courseName.isEmpty()) {
                if (posting.getCourse() == null) {
                    System.err.println("Error: JobPosting with ID " + posting.getJobid() + " has a null course reference.");
                    return false;
                }
                matches &= posting.getCourse().getCourseName().toLowerCase().contains(courseName.toLowerCase());
            }
            return matches;
        }).collect(Collectors.toList());
    }
}
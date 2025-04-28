package org.example;

import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicLong;

@RestController
@RequestMapping("/api/jobs")
@CrossOrigin(origins = "http://localhost:3000") // Ensure frontend can access the API
public class JobController {

    private final Map<Long, Job> jobs = new HashMap<>();
    private final AtomicLong idCounter = new AtomicLong();

    // Create a job
    @PostMapping
    public Job createJob(@RequestBody Job job) {
        long id = idCounter.incrementAndGet();
        job.setId(id);
        job.setDatePosted(LocalDateTime.now());
        jobs.put(id, job);
        return job;
    }

    // Update status of a job
    @PutMapping("/{id}/status")
    public Job updateStatus(@PathVariable Long id, @RequestBody Map<String, String> update) {
        Job job = jobs.get(id);
        if (job != null && update.containsKey("status")) {
            job.setStatus(update.get("status"));
        }
        return job;
    }

    // Delete a job
    @DeleteMapping("/{id}")
    public String deleteJob(@PathVariable Long id) {
        jobs.remove(id);
        return "Deleted job with ID " + id;
    }

    // (Optional) Get all jobs
    @GetMapping
    public Collection<Job> getAllJobs() {
        return jobs.values();
    }
}
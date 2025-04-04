package com.example.backend.controller;

import org.springframework.web.bind.annotation.*;

import java.util.List;

import com.example.backend.service.RAJobService;
import com.example.backend.model.RAJob;

// TODO this was not boiler-plate code from the lab,
//I needed this for it to work for me specifically for some reason
@CrossOrigin(origins = "https://localhost:3000")

@RestController
@RequestMapping("/api/jobs")
public class RAJobController {
    private final RAJobService service;

    public RAJobController(RAJobService service) {
        this.service = service;
    }

    @GetMapping
    public List<RAJob> getJobs() {
        return service.getAllJobs();
    }

    @PostMapping
    public RAJob addJob(@RequestBody RAJob job) {
        return service.saveRAJob(job);
    }

    @DeleteMapping
    void deleteJob(@RequestParam Long id) {
        service.deleteRAJob(id);
    }
}

package com.example.backend.service;

import org.springframework.stereotype.Service;

import java.util.List;

import com.example.backend.model.RAJob;
import com.example.backend.repository.RAJobRepository;

@Service
public class RAJobService {
    private final RAJobRepository repository;

    public RAJobService(RAJobRepository repository) {
        this.repository = repository;
    }

    public List<RAJob> getAllJobs() {
        return repository.findAll();
    }

    public RAJob saveRAJob(RAJob job) {
        return repository.save(job);
    }

    public void deleteRAJob(Long id) {
        repository.deleteById(id);
    }
}

package com.example.ta_ms.repositories;

import com.example.ta_ms.entities.JobPosting;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobPostingRepository extends JpaRepository<JobPosting, Integer> {
}

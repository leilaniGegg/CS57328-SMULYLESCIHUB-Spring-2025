package com.example.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.backend.model.RAJob;

public interface RAJobRepository extends JpaRepository<RAJob, Long> {
}

package com.example.ta_ms.repositories;

import com.example.ta_ms.entities.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CourseRepository extends JpaRepository<Course, String> {

    List<Course> findByCourseNameContainingIgnoreCase(String courseName);

    List<Course> findByCourseNumberContainingIgnoreCase(String courseNumber);
}

package com.example.universityta.repositories;

import com.example.universityta.entities.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CourseRepository extends JpaRepository<Course, String> {

    List<Course> findByCourseNameContainingIgnoreCase(String courseName);

    List<Course> findByCourseNumberContainingIgnoreCase(String courseNumber);
}

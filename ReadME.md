# SMU TA Module Sprint 1

This document provides an overview of the current state of Team 5's TA Module project. The sprint aims to allow faculty to add courses and post TA job listings while enabling students to filter and view job postings.

---

## Overview of User Stories

1. **Faculty User Story**
    - **Functionality:**
        - Faculty can add a new course to the system.
        - Faculty can post a TA job listing associated with a course.
        - The job posting contains details such as faculty name, email, required skills, and additional requirements (e.g., standings).
    - **Process:**
        - A faculty member can add a course to be added to the system.
        - When posting a job, the system checks if the course already exists. If so, the inputted fields are fetched.
        - The job posting is persisted to the database along with its relationships to the course and any additional required courses.

2. **Student User Story**
    - **Functionality:**
        - Students can filter available TA job postings based on multiple criteria such as course number, course name, skill requirements, instructor name, and academic standing.
    - **Process:**
        - The student filters are applied via query parameters that are processed on the backend, returning the relevant job postings for the student to review.

---

## Backend Details (Java with Spring Boot)

### Domain Entities

- **Course.java**
    - Represents a course offered at the university.
    - Attributes: `courseNumber`, `courseName`, `description`.
    - Includes helper methods such as `getCourseDetails()` for generating human-readable course details.

- **JobPosting.java**
    - Represents a TA job posting.
    - Attributes include:
        - `jobid`: Auto-generated identifier.
        - `facultyName` and `facultyEmail`: Faculty contact information.
        - `course`: A **Many-to-One** relationship with the `Course` entity.
        - `requiredCourses`: A **Many-to-Many** relationship with `Course` for listing additional required courses.
        - `skills`, `standings`, `jobDetails`, and `createdDate`: Other details for the job posting.
    - Cascade settings are used on relationships (e.g., `CascadeType.ALL` for the primary course) to help persist new course entries automatically.

### Repositories

- **CourseRepository.java**
    - Extends `JpaRepository` to perform CRUD operations.
    - Provides filtering methods such as `findByCourseNameContainingIgnoreCase` and `findByCourseNumberContainingIgnoreCase`.

- **JobPostingRepository.java**
    - Extends `JpaRepository` for the `JobPosting` entity.

### Service Layer

- **CourseService.java**
    - Provides business logic related to course management.
    - Includes methods to retrieve all courses and filter by course name or number.

- **JobPostingService.java**
    - Manages job postings.
    - Contains methods to save, retrieve, and filter job postings based on:
        - Skills, faculty/instructor name, and academic standings.
        - Course attributes such as course number and course name.
    - Uses Java Streams to filter the job posting list according to various criteria.

### REST Controllers

- **PostJobController.java**
    - Manages TA job postings.
    - Ensures that the referenced course exists; if not, it creates the course.
    - Updates `requiredCourses` with the persisted course entries.

- **FilterClassesController.java**
    - Handles GET requests for job postings.
    - Applies filtering based on query parameters (skills, instructor name, standing, course number, and course name) to support the student user story.

- **CourseController.java**
    - Provides endpoints to add a new course.

### CORS Configuration

- **WebConfig.java**
    - Configures CORS to allow requests from the local frontend running at `http://localhost:3000`.
    - Permits typical HTTP methods (GET, POST, PUT, DELETE) and allows credentials.

### Known Backend Issues

There is a current error when:
- submutting a course, a job posting, and a filtering the job postings.

Potential reasons for these issues include:
- **Overlapping Endpoint Mappings:** Conflicts in API endpoint paths may be causing routing issues.
- **Cascade on the Many-to-One Relationship:** Improper cascading settings might lead to persistence errors.
- **Incomplete Required Course Data:** Missing or incomplete data for courses could be causing failures.
- **Filtering Logic Ambiguity:** The filtering logic might not properly handle certain edge cases.

*Team 5 will work to debug these issues before Sprint 2.*

---

## Frontend Details (React with Axios)

### Components Overview

1. **FacultyPostJob Component**
    - **Purpose:**
        - Allows faculty members to add courses and post TA job listings.
    - **Functionality:**
        - Renders two forms: one for adding a course and another for posting a job.
        - Handles form submissions by sending POST requests to `/api/courses` and `/api/jobpostings`.
        - Parses comma-separated inputs for required courses and manages checkboxes for academic standings.
        - Uses state management (via React hooks) to capture and reset form data upon successful submission.

2. **StudentFilterClasses Component**
    - **Purpose:**
        - Provides a user interface for students to filter available TA job postings.
    - **Functionality:**
        - Includes input fields for filtering by course number, course name, skills, instructor name, and standing.
        - Constructs a query URL based on the provided filters and sends a GET request to `/api/jobpostings`.
        - Displays the filtered job postings in a list, including course details and faculty contact information.

3. **Main App Component**
    - **Purpose:**
        - Acts as the main entry point and allows users to switch between the Faculty and Student views.
    - **Functionality:**
        - Provides two buttons to toggle the display of either the `FacultyPostJob` or `StudentFilterClasses` component.

### API Service

- An example service file is provided to centralize API calls using axios. This pattern enhances code reusability and maintainability.

---

## Conclusion

The SMU TA Module is designed to provide a smooth workflow for faculty and students by integrating course and job posting functionalities with a clear separation between backend services and frontend components. While the core structure and relationships are in place, there are known backend errors that are preventing the system from operating as intended. Team 5 will address these issues in Sprint 2, focusing on endpoint mapping, cascade settings, course data completeness, and filtering logic.

This ReadME outlines the progress made so far, along with the areas that require further debugging and refinement.

package org.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest // Loads the full Spring application context
public class JobControllerTest {

    @Autowired
    private JobController jobController; // Inject the JobController bean

    @Autowired
    private AuthController authController; // Inject AuthController to get user IDs

    private Long employerUserId;
    private Long studentUserId;

    // Define the upload directory path using the same logic as JobController
    private final Path testUploadDir = Paths.get("uploads/resumes").toAbsolutePath().normalize();


    // Reset the in-memory data and create default users before each test
    @BeforeEach
    void setUp() throws IOException { // Added throws IOException for directory creation
        // Clear jobs and applications
        jobController.getAllJobs().clear(); // Access internal map via getAllJobs()
        // Need a way to clear allApplications map if necessary, currently not exposed via getter

        // Clear and re-initialize users in AuthController
        authController.getUsers().clear(); // Access internal map via getUsers()
        authController.initDefaultUsers(); // Re-add default users

        // Get the IDs of the default users
        employerUserId = authController.getUsers().values().stream()
                .filter(user -> "employer".equals(user.getRole()))
                .findFirst().get().getId();

        studentUserId = authController.getUsers().values().stream()
                .filter(user -> "student".equals(user.getRole()))
                .findFirst().get().getId();

        System.out.println("--- Test Setup: Employer ID=" + employerUserId + ", Student ID=" + studentUserId + " ---");

        // Ensure the test upload directory exists before tests that create files
        Files.createDirectories(this.testUploadDir);
        // Optional: Clean up any leftover files from previous runs
        Files.walk(this.testUploadDir)
                .filter(Files::isRegularFile)
                .forEach(file -> {
                    try {
                        Files.delete(file);
                    } catch (IOException e) {
                        System.err.println("Failed to clean up test file: " + file + " - " + e.getMessage());
                    }
                });
    }

    @Test
    void contextLoads() {
        assertNotNull(jobController);
        assertNotNull(authController);
    }

    @Test
    void testGetAllJobs_AccessibleByAnyone() {
        // Create a job first (as employer)
        Job job = new Job(null, "Test Job", "Company", "Desc", "Apply", "open");
        jobController.createJob(job, employerUserId); // Use employer ID

        // Get all jobs without authentication
        Collection<Job> jobs = jobController.getAllJobs();

        assertNotNull(jobs);
        assertFalse(jobs.isEmpty());
        assertEquals(1, jobs.size());
        assertEquals("Test Job", jobs.iterator().next().getTitle());
    }

    @Test
    void testCreateJob_AsEmployer_Success() {
        Job job = new Job(null, "New Employer Job", "Employer Co", "Great job", "Apply here", "open");

        ResponseEntity<Job> response = jobController.createJob(job, employerUserId); // Authenticate as employer

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getId());
        assertEquals("New Employer Job", response.getBody().getTitle());
        assertEquals(employerUserId, response.getBody().getEmployerId()); // Verify employer ID is set
        assertNotNull(response.getBody().getApplicants()); // Applicants list should be initialized

        // Verify job is added to the in-memory map
        assertTrue(jobController.getAllJobs().stream()
                .anyMatch(j -> "New Employer Job".equals(j.getTitle())));
    }

    @Test
    void testCreateJob_AsStudent_Forbidden() {
        Job job = new Job(null, "Student Job", "Student Co", "Job", "Apply", "open");

        // Attempt to create job as student - Expect Forbidden (403)
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            jobController.createJob(job, studentUserId); // Authenticate as student
        });

        assertEquals(HttpStatus.FORBIDDEN, exception.getStatusCode());
        assertTrue(exception.getReason().contains("Only employers can create jobs"));
        assertEquals(0, jobController.getAllJobs().size()); // No job should be created
    }

    @Test
    void testCreateJob_Unauthenticated_Unauthorized() {
        Job job = new Job(null, "Anon Job", "Anon Co", "Job", "Apply", "open");

        // Attempt to create job without authentication (null userId) - Expect Unauthorized (401)
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            jobController.createJob(job, null); // No user ID header
        });

        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatusCode());
        assertTrue(exception.getReason().contains("User ID header missing"));
        assertEquals(0, jobController.getAllJobs().size()); // No job should be created
    }

    @Test
    void testApplyToJob_AsStudent_Success() throws IOException {
        // Create a job first (as employer)
        Job job = new Job(null, "Job to Apply", "Apply Co", "Apply now", "Instructions", "open");
        ResponseEntity<Job> jobResponse = jobController.createJob(job, employerUserId);
        Long jobId = jobResponse.getBody().getId();

        // Create a mock resume file
        MockMultipartFile resumeFile = new MockMultipartFile(
                "resume", // Must match @RequestPart name
                "resume.pdf", // Original filename
                "application/pdf", // Content type
                "resume content".getBytes() // File content
        );

        // Apply to the job as a student
        ResponseEntity<Application> applicationResponse = jobController.applyToJob(
                jobId,
                "Applicant Name", // Matches @RequestParam name
                resumeFile,
                studentUserId // Authenticate as student
        );

        assertEquals(HttpStatus.CREATED, applicationResponse.getStatusCode());
        assertNotNull(applicationResponse.getBody());
        assertNotNull(applicationResponse.getBody().getId());
        assertEquals(jobId, applicationResponse.getBody().getJobId());
        assertEquals("Applicant Name", applicationResponse.getBody().getName());
        assertNotNull(applicationResponse.getBody().getResumeFileName()); // Filename should be stored
        assertEquals("submitted", applicationResponse.getBody().getStatus());

        // Verify application is added to the job's applicant list
        Job updatedJob = jobController.getAllJobs().stream()
                .filter(j -> j.getId().equals(jobId))
                .findFirst().get();
        assertNotNull(updatedJob.getApplicants());
        assertEquals(1, updatedJob.getApplicants().size());
        assertEquals("Applicant Name", updatedJob.getApplicants().get(0).getName());

        // Clean up the created file (basic cleanup)
        // Use the testUploadDir path for cleanup
        Path uploadedFilePath = this.testUploadDir.resolve(applicationResponse.getBody().getResumeFileName());
        Files.deleteIfExists(uploadedFilePath);
    }

    @Test
    void testApplyToJob_AsEmployer_Forbidden() throws IOException {
        // Create a job first (as employer)
        Job job = new Job(null, "Job to Apply", "Apply Co", "Apply now", "Instructions", "open");
        ResponseEntity<Job> jobResponse = jobController.createJob(job, employerUserId);
        Long jobId = jobResponse.getBody().getId();

        MockMultipartFile resumeFile = new MockMultipartFile("resume", "resume.pdf", "application/pdf", "content".getBytes());

        // Attempt to apply as employer - Expect Forbidden (403)
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            jobController.applyToJob(jobId, "Employer Applicant", resumeFile, employerUserId); // Authenticate as employer
        });

        assertEquals(HttpStatus.FORBIDDEN, exception.getStatusCode());
        assertTrue(exception.getReason().contains("Only students can apply to jobs"));

        // Verify no application is added
        Job updatedJob = jobController.getAllJobs().stream()
                .filter(j -> j.getId().equals(jobId))
                .findFirst().get();
        assertNotNull(updatedJob.getApplicants());
        assertTrue(updatedJob.getApplicants().isEmpty());
    }

    @Test
    void testApplyToJob_Unauthenticated_Unauthorized() throws IOException {
        // Create a job first (as employer)
        Job job = new Job(null, "Job to Apply", "Apply Co", "Apply now", "Instructions", "open");
        ResponseEntity<Job> jobResponse = jobController.createJob(job, employerUserId);
        Long jobId = jobResponse.getBody().getId();

        MockMultipartFile resumeFile = new MockMultipartFile("resume", "resume.pdf", "application/pdf", "content".getBytes());

        // Attempt to apply unauthenticated - Expect Unauthorized (401)
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            jobController.applyToJob(jobId, "Anonymous Applicant", resumeFile, null); // No user ID header
        });

        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatusCode());
        assertTrue(exception.getReason().contains("User ID header missing"));

        // Verify no application is added
        Job updatedJob = jobController.getAllJobs().stream()
                .filter(j -> j.getId().equals(jobId))
                .findFirst().get();
        assertNotNull(updatedJob.getApplicants());
        assertTrue(updatedJob.getApplicants().isEmpty());
    }

    @Test
    void testGetApplicants_AsJobOwnerEmployer_Success() throws IOException {
        // Create a job (as employer)
        Job job = new Job(null, "Job with Applicants", "Employer Co", "Job", "Apply", "open");
        ResponseEntity<Job> jobResponse = jobController.createJob(job, employerUserId);
        Long jobId = jobResponse.getBody().getId();

        // Apply to the job as a student
        MockMultipartFile resumeFile = new MockMultipartFile("resume", "resume1.pdf", "application/pdf", "content1".getBytes());
        jobController.applyToJob(jobId, "Student Applicant 1", resumeFile, studentUserId);

        // Apply again with another student (if you have more users)
        // For this test, we'll just use one applicant

        // Get applicants as the job owner (employer)
        ResponseEntity<List<Map<String, String>>> applicantsResponse = jobController.getApplicants(jobId, employerUserId); // Authenticate as job owner

        assertEquals(HttpStatus.OK, applicantsResponse.getStatusCode());
        assertNotNull(applicantsResponse.getBody());
        assertEquals(1, applicantsResponse.getBody().size());
        Map<String, String> applicantInfo = applicantsResponse.getBody().get(0);
        assertEquals("Student Applicant 1", applicantInfo.get("name"));
        assertNotNull(applicantInfo.get("resumeFileName"));

        // Clean up the created file
        // Use the testUploadDir path for cleanup
        Path uploadedFilePath = this.testUploadDir.resolve(applicantInfo.get("resumeFileName"));
        Files.deleteIfExists(uploadedFilePath);
    }

    @Test
    void testGetApplicants_AsAnotherEmployer_Forbidden() throws IOException {
        // Create a job (as employer)
        Job job = new Job(null, "Job with Applicants", "Employer Co", "Job", "Apply", "open");
        ResponseEntity<Job> jobResponse = jobController.createJob(job, employerUserId);
        Long jobId = jobResponse.getBody().getId();

        // Apply to the job as a student
        MockMultipartFile resumeFile = new MockMultipartFile("resume", "resume1.pdf", "application/pdf", "content1".getBytes());
        jobController.applyToJob(jobId, "Student Applicant 1", resumeFile, studentUserId);

        // Create another employer user
        User anotherEmployer = new User(null, "Another Employer", "anotherpass", "employer");
        authController.registerUser(anotherEmployer);
        Long anotherEmployerId = authController.getUsers().values().stream()
                .filter(user -> "Another Employer".equals(user.getName()))
                .findFirst().get().getId();


        // Attempt to get applicants as another employer - Expect Forbidden (403)
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            jobController.getApplicants(jobId, anotherEmployerId); // Authenticate as another employer
        });

        assertEquals(HttpStatus.FORBIDDEN, exception.getStatusCode());
        assertTrue(exception.getReason().contains("You can only view applicants for your own jobs"));

        // Clean up the created file
        // Need to retrieve the filename first
        Job updatedJob = jobController.getAllJobs().stream()
                .filter(j -> j.getId().equals(jobId))
                .findFirst().get();
        if (!updatedJob.getApplicants().isEmpty()) {
            // Use the testUploadDir path for cleanup
            Path uploadedFilePath = this.testUploadDir.resolve(updatedJob.getApplicants().get(0).getResumeFileName());
            Files.deleteIfExists(uploadedFilePath);
        }
    }

    @Test
    void testGetApplicants_AsStudent_Forbidden() throws IOException {
        // Create a job (as employer)
        Job job = new Job(null, "Job with Applicants", "Employer Co", "Job", "Apply", "open");
        ResponseEntity<Job> jobResponse = jobController.createJob(job, employerUserId);
        Long jobId = jobResponse.getBody().getId();

        // Apply to the job as a student
        MockMultipartFile resumeFile = new MockMultipartFile("resume", "resume1.pdf", "application/pdf", "content1".getBytes());
        jobController.applyToJob(jobId, "Student Applicant 1", resumeFile, studentUserId);

        // Attempt to get applicants as the student who applied - Expect Forbidden (403)
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            jobController.getApplicants(jobId, studentUserId); // Authenticate as student
        });

        assertEquals(HttpStatus.FORBIDDEN, exception.getStatusCode());
        assertTrue(exception.getReason().contains("You can only view applicants for your own jobs"));

        // Clean up the created file
        Job updatedJob = jobController.getAllJobs().stream()
                .filter(j -> j.getId().equals(jobId))
                .findFirst().get();
        if (!updatedJob.getApplicants().isEmpty()) {
            // Use the testUploadDir path for cleanup
            Path uploadedFilePath = this.testUploadDir.resolve(updatedJob.getApplicants().get(0).getResumeFileName());
            Files.deleteIfExists(uploadedFilePath);
        }
    }

    @Test
    void testGetApplicants_Unauthenticated_Unauthorized() throws IOException {
        // Create a job (as employer)
        Job job = new Job(null, "Job with Applicants", "Employer Co", "Job", "Apply", "open");
        ResponseEntity<Job> jobResponse = jobController.createJob(job, employerUserId);
        Long jobId = jobResponse.getBody().getId();

        // Apply to the job as a student
        MockMultipartFile resumeFile = new MockMultipartFile("resume", "resume1.pdf", "application/pdf", "content1".getBytes());
        jobController.applyToJob(jobId, "Student Applicant 1", resumeFile, studentUserId);

        // Attempt to get applicants unauthenticated - Expect Unauthorized (401)
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            jobController.getApplicants(jobId, null); // No user ID header
        });

        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatusCode());
        assertTrue(exception.getReason().contains("User ID header missing"));

        // Clean up the created file
        Job updatedJob = jobController.getAllJobs().stream()
                .filter(j -> j.getId().equals(jobId))
                .findFirst().get();
        if (!updatedJob.getApplicants().isEmpty()) {
            // Use the testUploadDir path for cleanup
            Path uploadedFilePath = this.testUploadDir.resolve(updatedJob.getApplicants().get(0).getResumeFileName());
            Files.deleteIfExists(uploadedFilePath);
        }
    }

    @Test
    void testUpdateStatus_AsJobOwnerEmployer_Success() {
        // Create a job (as employer)
        Job job = new Job(null, "Job to Update", "Employer Co", "Job", "open", "open");
        ResponseEntity<Job> jobResponse = jobController.createJob(job, employerUserId);
        Long jobId = jobResponse.getBody().getId();

        // Verify job exists initially
        assertEquals(1, jobController.getAllJobs().size());

        // Update status as the job owner
        Map<String, String> update = new HashMap<>();
        update.put("status", "closed");
        ResponseEntity<Job> updateResponse = jobController.updateStatus(jobId, update, employerUserId); // Authenticate as job owner

        assertEquals(HttpStatus.OK, updateResponse.getStatusCode());
        assertNotNull(updateResponse.getBody());
        assertEquals("closed", updateResponse.getBody().getStatus());

        // Verify status is updated in the in-memory map
        Job updatedJob = jobController.getAllJobs().stream()
                .filter(j -> j.getId().equals(jobId))
                .findFirst().get();
        assertEquals("closed", updatedJob.getStatus());
    }

    @Test
    void testUpdateStatus_AsAnotherEmployer_Forbidden() {
        // Create a job (as employer)
        Job job = new Job(null, "Job to Update", "Employer Co", "Job", "open", "open");
        ResponseEntity<Job> jobResponse = jobController.createJob(job, employerUserId);
        Long jobId = jobResponse.getBody().getId();

        // Create another employer user
        User anotherEmployer = new User(null, "Another Employer", "anotherpass", "employer");
        authController.registerUser(anotherEmployer);
        Long anotherEmployerId = authController.getUsers().values().stream()
                .filter(user -> "Another Employer".equals(user.getName()))
                .findFirst().get().getId();

        // Verify job exists initially
        assertEquals(1, jobController.getAllJobs().size());

        // Attempt to update status as another employer - Expect Forbidden (403)
        Map<String, String> update = new HashMap<>();
        update.put("status", "closed");
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            jobController.updateStatus(jobId, update, anotherEmployerId); // Authenticate as another employer
        });

        assertEquals(HttpStatus.FORBIDDEN, exception.getStatusCode());
        assertTrue(exception.getReason().contains("Only the job owner can update status"));

        // Verify status is NOT updated
        Job updatedJob = jobController.getAllJobs().stream()
                .filter(j -> j.getId().equals(jobId))
                .findFirst().get();
        assertEquals("open", updatedJob.getStatus());
    }

    @Test
    void testUpdateStatus_AsStudent_Forbidden() {
        // Create a job (as employer)
        Job job = new Job(null, "Job to Update", "Employer Co", "Job", "open", "open");
        ResponseEntity<Job> jobResponse = jobController.createJob(job, employerUserId);
        Long jobId = jobResponse.getBody().getId();

        // Attempt to update status as student - Expect Forbidden (403)
        Map<String, String> update = new HashMap<>();
        update.put("status", "closed");
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            jobController.updateStatus(jobId, update, studentUserId); // Authenticate as student
        });

        assertEquals(HttpStatus.FORBIDDEN, exception.getStatusCode());
        assertTrue(exception.getReason().contains("Only the job owner can update status"));

        // Verify status is NOT updated
        Job updatedJob = jobController.getAllJobs().stream()
                .filter(j -> j.getId().equals(jobId))
                .findFirst().get();
        assertEquals("open", updatedJob.getStatus());
    }

    @Test
    void testUpdateStatus_Unauthenticated_Unauthorized() {
        // Create a job (as employer)
        Job job = new Job(null, "Job to Update", "Employer Co", "Job", "open", "open");
        ResponseEntity<Job> jobResponse = jobController.createJob(job, employerUserId);
        Long jobId = jobResponse.getBody().getId();

        // Attempt to update status unauthenticated - Expect Unauthorized (401)
        Map<String, String> update = new HashMap<>();
        update.put("status", "closed");
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            jobController.updateStatus(jobId, update, null); // No user ID header
        });

        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatusCode());
        assertTrue(exception.getReason().contains("User ID header missing"));

        // Verify status is NOT updated
        Job updatedJob = jobController.getAllJobs().stream()
                .filter(j -> j.getId().equals(jobId))
                .findFirst().get();
        assertEquals("open", updatedJob.getStatus());
    }

    @Test
    void testDeleteJob_AsJobOwnerEmployer_Success() {
        // Create a job (as employer)
        Job job = new Job(null, "Job to Delete", "Employer Co", "Job", "open", "open");
        ResponseEntity<Job> jobResponse = jobController.createJob(job, employerUserId);
        Long jobId = jobResponse.getBody().getId();

        // Verify job exists initially
        assertEquals(1, jobController.getAllJobs().size());

        // Delete job as the job owner
        ResponseEntity<Void> deleteResponse = jobController.deleteJob(jobId, employerUserId); // Authenticate as job owner

        assertEquals(HttpStatus.NO_CONTENT, deleteResponse.getStatusCode()); // 204 No Content

        // Verify job is deleted from the in-memory map
        assertEquals(0, jobController.getAllJobs().size());
    }

    @Test
    void testDeleteJob_AsAnotherEmployer_Forbidden() {
        // Create a job (as employer)
        Job job = new Job(null, "Job to Delete", "Employer Co", "Job", "open", "open");
        ResponseEntity<Job> jobResponse = jobController.createJob(job, employerUserId);
        Long jobId = jobResponse.getBody().getId();

        // Create another employer user
        User anotherEmployer = new User(null, "Another Employer", "anotherpass", "employer");
        authController.registerUser(anotherEmployer);
        Long anotherEmployerId = authController.getUsers().values().stream()
                .filter(user -> "Another Employer".equals(user.getName()))
                .findFirst().get().getId();

        // Verify job exists initially
        assertEquals(1, jobController.getAllJobs().size());

        // Attempt to delete job as another employer - Expect Forbidden (403)
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            jobController.deleteJob(jobId, anotherEmployerId); // Authenticate as another employer
        });

        assertEquals(HttpStatus.FORBIDDEN, exception.getStatusCode());
        assertTrue(exception.getReason().contains("Only the job owner can delete this job"));

        // Verify job is NOT deleted
        assertEquals(1, jobController.getAllJobs().size());
    }

    @Test
    void testDeleteJob_AsStudent_Forbidden() {
        // Create a job (as employer)
        Job job = new Job(null, "Job to Delete", "Employer Co", "Job", "open", "open");
        ResponseEntity<Job> jobResponse = jobController.createJob(job, employerUserId);
        Long jobId = jobResponse.getBody().getId();

        // Verify job exists initially
        assertEquals(1, jobController.getAllJobs().size());

        // Attempt to delete job as student - Expect Forbidden (403)
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            jobController.deleteJob(jobId, studentUserId); // Authenticate as student
        });

        assertEquals(HttpStatus.FORBIDDEN, exception.getStatusCode());
        assertTrue(exception.getReason().contains("Only the job owner can delete this job"));

        // Verify job is NOT deleted
        assertEquals(1, jobController.getAllJobs().size());
    }

    @Test
    void testDeleteJob_Unauthenticated_Unauthorized() {
        // Create a job (as employer)
        Job job = new Job(null, "Job to Delete", "Employer Co", "Job", "open", "open");
        ResponseEntity<Job> jobResponse = jobController.createJob(job, employerUserId);
        Long jobId = jobResponse.getBody().getId();

        // Verify job exists initially
        assertEquals(1, jobController.getAllJobs().size());

        // Attempt to delete job unauthenticated - Expect Unauthorized (401)
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            jobController.deleteJob(jobId, null); // No user ID header
        });

        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatusCode());
        assertTrue(exception.getReason().contains("User ID header missing"));

        // Verify job is NOT deleted
        assertEquals(1, jobController.getAllJobs().size());
    }

    @Test
    void testGetResume_Success() throws IOException {
        // Create a job (as employer)
        Job job = new Job(null, "Job with Resume", "Employer Co", "Job", "open", "open");
        ResponseEntity<Job> jobResponse = jobController.createJob(job, employerUserId);
        Long jobId = jobResponse.getBody().getId();

        // Apply to the job as a student to create a resume file
        MockMultipartFile resumeFile = new MockMultipartFile("resume", "test_resume.txt", "text/plain", "This is test content.".getBytes());
        ResponseEntity<Application> appResponse = jobController.applyToJob(jobId, "Resume Applicant", resumeFile, studentUserId);
        String storedFilename = appResponse.getBody().getResumeFileName();

        // Get the resume using the stored filename
        ResponseEntity<Resource> resumeResponse = jobController.getResume(storedFilename);

        assertEquals(HttpStatus.OK, resumeResponse.getStatusCode());
        assertNotNull(resumeResponse.getBody());
        assertTrue(resumeResponse.getBody().exists());

        // Clean up the created file (basic cleanup)
        // Use the testUploadDir path for cleanup
        Path uploadedFilePath = this.testUploadDir.resolve(storedFilename);
        Files.deleteIfExists(uploadedFilePath);
    }

    @Test
    void testGetResume_NotFound() {
        // Attempt to get a resume that doesn't exist
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            jobController.getResume("non_existent_resume.pdf");
        });

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        assertTrue(exception.getReason().contains("Resume not found or not accessible"));
    }
}

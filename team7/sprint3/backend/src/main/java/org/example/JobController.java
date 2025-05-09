package org.example;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/jobs")
@CrossOrigin(origins = "http://localhost:3000") // Ensure frontend can access the API
public class JobController {

    private final Map<Long, Job> jobs = new HashMap<>();
    private final AtomicLong idCounter = new AtomicLong();
    private final Map<Long, Application> allApplications = new HashMap<>(); // Keeping track of all for reference
    private final AtomicLong applicationIdCounter = new AtomicLong();

    // Define a directory to save uploaded resumes
    // IMPORTANT: Replace with a proper file storage solution in production!
    private final Path uploadDir = Paths.get("uploads/resumes").toAbsolutePath().normalize(); // Ensure absolute path

    // --- Inject the Spring-managed AuthController instance ---
    private final AuthController authController; // Declare as final, will be injected

    @Autowired // Use Autowired to inject the AuthController
    public JobController(AuthController authController) {
        this.authController = authController; // Assign the injected instance
        // Ensure the upload directory exists when the controller is created
        try {
            Files.createDirectories(this.uploadDir);
        } catch (IOException e) {
            // This should be logged properly in a real app
            throw new RuntimeException("Could not create upload directory!", e);
        }
    }
    // --- End Injection ---


    // Helper method to get the authenticated user from the header
    private User getAuthenticatedUser(@RequestHeader(value = "X-User-Id", required = false) Long userId) {
        // --- Debug Log: Check received User ID header ---
        System.out.println("--- getAuthenticatedUser called ---");
        System.out.println("Received X-User-Id header: " + userId);
        // --- End Debug Log ---

        if (userId == null) {
            System.out.println("User ID header missing. Returning UNAUTHORIZED.");
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User ID header missing");
        }
        // Use the injected authController to find the user
        Optional<User> userOptional = authController.findUserById(userId);

        // --- Debug Log: Check if user was found ---
        System.out.println("User found by ID " + userId + ": " + userOptional.isPresent());
        // --- End Debug Log ---

        if (!userOptional.isPresent()) {
            System.out.println("User not found for ID " + userId + ". Returning UNAUTHORIZED.");
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found");
        }
        User user = userOptional.get();
        // --- Debug Log: Log found user's details ---
        System.out.println("Authenticated User: ID=" + user.getId() + ", Name=" + user.getName() + ", Role=" + user.getRole());
        System.out.println("--- End getAuthenticatedUser ---");
        // --- End Debug Log ---
        return user;
    }


    // Create a job - Only Employers
    @PostMapping
    public ResponseEntity<Job> createJob(@RequestBody Job job,
                                         @RequestHeader(value = "X-User-Id") Long userId) { // Expect User ID header
        User authenticatedUser = getAuthenticatedUser(userId);

        // --- Debug Log: Check role after authentication ---
        System.out.println("Checking role for createJob. User role: " + authenticatedUser.getRole());
        // --- End Debug Log ---

        if (!"employer".equals(authenticatedUser.getRole())) {
            System.out.println("User is not an employer. Returning FORBIDDEN.");
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only employers can create jobs");
        }

        long id = idCounter.incrementAndGet();
        job.setId(id);
        job.setDatePosted(LocalDateTime.now());
        // Set the employerId based on the authenticated user's ID
        job.setEmployerId(authenticatedUser.getId());
        // Initialize applicants list to avoid NullPointerException later
        if (job.getApplicants() == null) {
            job.setApplicants(new ArrayList<>());
        }
        jobs.put(id, job);
        return new ResponseEntity<>(job, HttpStatus.CREATED);
    }

    // Update status of a job - Only Employer who owns the job
    @PutMapping("/{id}/status")
    public ResponseEntity<Job> updateStatus(@PathVariable Long id,
                                            @RequestBody Map<String, String> update,
                                            @RequestHeader(value = "X-User-Id") Long userId) { // Expect User ID header
        User authenticatedUser = getAuthenticatedUser(userId);

        Job job = jobs.get(id);
        if (job == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Job not found");
        }

        // --- Debug Log: Check role and ownership for updateStatus ---
        System.out.println("Checking role and ownership for updateStatus. User role: " + authenticatedUser.getRole() + ", User ID: " + authenticatedUser.getId() + ", Job Employer ID: " + job.getEmployerId());
        // --- End Debug Log ---


        // Check if the authenticated user is the employer who owns this job
        if (!"employer".equals(authenticatedUser.getRole()) || !authenticatedUser.getId().equals(job.getEmployerId())) {
            System.out.println("User is not employer or not job owner. Returning FORBIDDEN.");
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only the job owner can update status");
        }


        if (update.containsKey("status")) {
            job.setStatus(update.get("status"));
            return new ResponseEntity<>(job, HttpStatus.OK);
        }
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Status not provided");
    }

    // Delete a job - Only Employer who owns the job
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteJob(@PathVariable Long id,
                                          @RequestHeader(value = "X-User-Id") Long userId) { // Expect User ID header
        User authenticatedUser = getAuthenticatedUser(userId);

        Job job = jobs.get(id);
        if (job == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Job not found");
        }

        // --- Debug Log: Check role and ownership for deleteJob ---
        System.out.println("Checking role and ownership for deleteJob. User role: " + authenticatedUser.getRole() + ", User ID: " + authenticatedUser.getId() + ", Job Employer ID: " + job.getEmployerId());
        // --- End Debug Log ---

        // Check if the authenticated user is the employer who owns this job
        if (!"employer".equals(authenticatedUser.getRole()) || !authenticatedUser.getId().equals(job.getEmployerId())) {
            System.out.println("User is not employer or not job owner. Returning FORBIDDEN.");
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only the job owner can delete this job");
        }


        if (jobs.containsKey(id)) {
            jobs.remove(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT); // 204 No Content for successful deletion
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Job not found");
    }

    // Get all jobs - Accessible by anyone (students and employers)
    @GetMapping
    public Collection<Job> getAllJobs() {
        return jobs.values();
    }

    // Get jobs by employer (considering your frontend has employerId) - Only the employer can see their jobs
    @GetMapping("/employer/{employerId}")
    public ResponseEntity<List<Job>> getJobsByEmployer(@PathVariable Long employerId,
                                                       @RequestHeader(value = "X-User-Id") Long userId) { // Expect User ID header
        User authenticatedUser = getAuthenticatedUser(userId);

        // --- Debug Log: Check role and ownership for getJobsByEmployer ---
        System.out.println("Checking role and ownership for getJobsByEmployer. User role: " + authenticatedUser.getRole() + ", User ID: " + authenticatedUser.getId() + ", Requested Employer ID: " + employerId);
        // --- End Debug Log ---


        // Check if the authenticated user is the employer whose jobs are being requested
        if (!"employer".equals(authenticatedUser.getRole()) || !authenticatedUser.getId().equals(employerId)) {
            System.out.println("User is not employer or not the requested employer. Returning FORBIDDEN.");
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only view your own job listings");
        }


        List<Job> result = new ArrayList<>();
        for (Job job : jobs.values()) {
            // Use the getter for employerId
            if (job.getEmployerId() != null && job.getEmployerId().equals(employerId)) {
                result.add(job);
            }
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    // Submit an application (file upload) - Only Students
    @PostMapping("/{jobId}/apply")
    public ResponseEntity<Application> applyToJob(
            @PathVariable Long jobId,
            @RequestParam("name") String applicantName, // Matches frontend formData.append("name", ...)
            @RequestPart("resume") MultipartFile resumeFile, // Matches frontend formData.append("resume", ...)
            @RequestHeader(value = "X-User-Id") Long userId // Expect User ID header
    ) throws IOException { // Added throws IOException as file operations can throw it

        // --- Debug Log: Entering applyToJob backend method ---
        System.out.println("--- Entering applyToJob backend method for jobId: " + jobId + " ---");
        // --- End Debug Log ---

        // Use the injected authController to get the authenticated user
        User authenticatedUser = getAuthenticatedUser(userId);

        // --- Debug Log: Check role after authentication in applyToJob ---
        System.out.println("Checking role for applyToJob. User role: " + authenticatedUser.getRole());
        // --- End Debug Log ---


        if (!"student".equals(authenticatedUser.getRole())) {
            System.out.println("User is not a student. Returning FORBIDDEN.");
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only students can apply to jobs");
        }

        Job job = jobs.get(jobId);
        if (job == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Job not found");
        }

        // Basic validation
        if (applicantName == null || applicantName.trim().isEmpty() || resumeFile == null || resumeFile.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Applicant name and resume file are required");
        }

        // --- File Storage Logic (Example) ---
        String originalFilename = resumeFile.getOriginalFilename();
        // Create a safer filename (e.g., UUID or timestamp + original name)
        String storedFilename = UUID.randomUUID().toString() + "_" + originalFilename;
        Path filePath = this.uploadDir.resolve(storedFilename).normalize(); // Use the configured upload directory


        // Check if the file path is within the upload directory to prevent directory traversal
        if (!filePath.startsWith(this.uploadDir)) {
            throw new IOException("Attempted to store file outside of configured directory: " + storedFilename);
        }

        try {
            // Save the file to the server's file system
            Files.copy(resumeFile.getInputStream(), filePath);
        } catch (IOException e) {
            e.printStackTrace(); // Log the error
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to store resume file", e);
        }

        // --- Create and Save Application ---
        Long appId = applicationIdCounter.incrementAndGet();
        Application application = new Application(appId, jobId, applicantName, storedFilename, "submitted"); // Store the *generated* filename

        // Save application (in-memory)
        allApplications.put(appId, application);

        // Add application to the job's list (important for the /applicants endpoint)
        // Use the addApplicant method from the Job class
        job.addApplicant(application);

        System.out.println("Application submitted successfully for jobId " + jobId + " by user " + authenticatedUser.getName());
        return new ResponseEntity<>(application, HttpStatus.CREATED); // Return created application with 201 status
    }

    // File reviewing - Accessible by anyone who has the filename (consider restricting this in a real app)
    @GetMapping("/resumes/{filename}")
    public ResponseEntity<Resource> getResume(@PathVariable String filename) throws IOException {
        // Resolve the requested filename against the upload directory
        Path filePath = this.uploadDir.resolve(filename).normalize();

        // Security check: ensure the requested file is within the upload directory
        if (!Files.exists(filePath) || !Files.isReadable(filePath) || !filePath.startsWith(this.uploadDir)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Resume not found or not accessible");
        }


        Resource file;
        try {
            file = new UrlResource(filePath.toUri());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Resume file path invalid", e);
        }


        // Determine content type dynamically
        String contentType = null;
        try {
            contentType = Files.probeContentType(filePath);
        } catch (IOException e) {
            // Log error, but continue with default content type
            System.err.println("Could not determine file content type: " + e.getMessage());
        }

        if (contentType == null) {
            contentType = "application/octet-stream"; // Default fallback
        }


        return ResponseEntity.ok()
                .contentType(org.springframework.http.MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"") // Use attachment to prompt download
                .body(file);
    }

    // View applicants for a job - Only Employer who owns the job
    @GetMapping("/{id}/applicants")
    public ResponseEntity<List<Map<String, String>>> getApplicants(@PathVariable Long id,
                                                                   @RequestHeader(value = "X-User-Id") Long userId) { // Expect User ID header
        User authenticatedUser = getAuthenticatedUser(userId);

        Job job = jobs.get(id);
        if (job == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Job not found");
        }

        // --- Debug Log: Check role and ownership for getApplicants ---
        System.out.println("Checking role and ownership for getApplicants. User role: " + authenticatedUser.getRole() + ", User ID: " + authenticatedUser.getId() + ", Job Employer ID: " + job.getEmployerId());
        // --- End Debug Log ---


        // Check if the authenticated user is the employer who owns this job
        if (!"employer".equals(authenticatedUser.getRole()) || !authenticatedUser.getId().equals(job.getEmployerId())) {
            System.out.println("User is not employer or not the job owner. Returning FORBIDDEN.");
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only view applicants for your own jobs");
        }


        if (job != null) {
            // Assuming Job.getApplicants() returns the list of applications
            List<Map<String, String>> applicantInfoList = job.getApplicants().stream().map(app -> {
                Map<String, String> info = new HashMap<>();
                // Use getters from the Application class
                info.put("name", app.getName());
                info.put("resumeFileName", app.getResumeFileName()); // Return the stored filename
                // You might also want to include a link to download the resume
                // info.put("resumeDownloadLink", "/api/jobs/resumes/" + app.getResumeFileName());
                return info;
            }).collect(Collectors.toList());
            return new ResponseEntity<>(applicantInfoList, HttpStatus.OK);
        }
        // This line should not be reached if the job was null check passed
        return new ResponseEntity<>(Collections.emptyList(), HttpStatus.OK);
    }
}

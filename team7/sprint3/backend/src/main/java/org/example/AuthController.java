package org.example;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;


import jakarta.annotation.PostConstruct; // Uncomment this line if using Java 9+

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@RestController
@Service
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:3000")
public class AuthController {

    private final Map<Long, User> users = new HashMap<>();
    private final AtomicLong idCounter = new AtomicLong();

    // Add some initial users when this Spring bean is created
    public AuthController() {
        // Initial users will only be added when Spring creates this bean
        // Users registered via /api/auth/register will also be added to this map
        System.out.println("--- AuthController Bean Created by Spring ---");
    }

    // Method to initialize default users after construction and dependency injection
    // This ensures initial users are present even if no registration happens
    @PostConstruct
    public void initDefaultUsers() {
        if (users.isEmpty()) { // Only add if no users exist yet (e.g., on first startup)
            User employer = new User(idCounter.incrementAndGet(), "Employer User", "employerpass", "employer");
            users.put(employer.getId(), employer);

            User student = new User(idCounter.incrementAndGet(), "Student User", "studentpass", "student");
            users.put(student.getId(), student);

            System.out.println("--- Default Initial Users Added to Spring-managed AuthController ---");
            users.values().forEach(user -> System.out.println("ID: " + user.getId() + ", Name: " + user.getName() + ", Role: " + user.getRole()));
            System.out.println("-------------------------------------------------------------------");
        }
    }


    // Simple registration endpoint (for demonstration)
    @PostMapping("/register")
    public ResponseEntity<User> registerUser(@RequestBody User newUser) {
        // Basic validation
        if (newUser.getName() == null || newUser.getName().trim().isEmpty() ||
                newUser.getPassword() == null || newUser.getPassword().trim().isEmpty() ||
                newUser.getRole() == null || newUser.getRole().trim().isEmpty() ||
                (!newUser.getRole().equals("employer") && !newUser.getRole().equals("student"))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid user data");
        }

        // Check if user already exists (simple name check for in-memory)
        boolean userExists = users.values().stream()
                .anyMatch(user -> user.getName().equalsIgnoreCase(newUser.getName()));
        if (userExists) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Username already exists");
        }

        long id = idCounter.incrementAndGet();
        newUser.setId(id);
        users.put(id, newUser);

        System.out.println("--- User Registered: ID=" + newUser.getId() + ", Name=" + newUser.getName() + ", Role=" + newUser.getRole() + " ---");

        // Return user details (excluding password in a real app)
        return new ResponseEntity<>(newUser, HttpStatus.CREATED);
    }

    // Login endpoint
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody Map<String, String> credentials) {
        String name = credentials.get("name");
        String password = credentials.get("password");

        if (name == null || name.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Name and password are required");
        }

        // Find user by name (case-insensitive for simplicity)
        Optional<User> foundUser = users.values().stream()
                .filter(user -> user.getName().equalsIgnoreCase(name))
                .findFirst();

        if (foundUser.isPresent()) {
            User user = foundUser.get();
            if (user.getPassword().equals(password)) {
                // Login successful - return user info (excluding password)
                Map<String, Object> userInfo = new HashMap<>();
                userInfo.put("id", user.getId());
                userInfo.put("name", user.getName());
                userInfo.put("role", user.getRole());

                System.out.println("--- User Logged In: ID=" + user.getId() + ", Name=" + user.getName() + ", Role=" + user.getRole() + " ---");

                return new ResponseEntity<>(userInfo, HttpStatus.OK);
            } else {
                // Incorrect password
                System.out.println("--- Login Failed: Incorrect password for user " + name + " ---");
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Incorrect password");
            }
        } else {
            // User not found
            System.out.println("--- Login Failed: User not found with name " + name + " ---");
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found");
        }
    }

    // Helper method to get a user by ID (used by JobController)
    // This method accesses the users map managed by this Spring bean instance
    public Optional<User> findUserById(Long userId) {
        // --- Debug Log: Check lookup in findUserById ---
        System.out.println("--- AuthController.findUserById called for ID: " + userId + " ---");
        Optional<User> found = Optional.ofNullable(users.get(userId));
        System.out.println("--- Result of lookup for ID " + userId + ": " + found.isPresent() + " ---");
        // --- End Debug Log ---
        return found;
    }

    public Map<Long, User> getUsers() {
        return users;
    }
}

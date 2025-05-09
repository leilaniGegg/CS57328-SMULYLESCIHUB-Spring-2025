package org.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest // Loads the full Spring application context
public class AuthControllerTest {

    @Autowired
    private AuthController authController; // Inject the AuthController bean

    // Reset the in-memory users before each test
    @BeforeEach
    void setUp() {
        authController.getUsers().clear(); // Clear users before each test
        authController.initDefaultUsers(); // Re-add default users
    }

    @Test
    void contextLoads() {
        // Simple test to ensure the Spring context loads and the controller is injected
        assertNotNull(authController);
    }

    @Test
    void testRegisterUser_Success_Student() {
        User newUser = new User(null, "newstudent", "studentpass", "student");
        ResponseEntity<User> response = authController.registerUser(newUser);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getId()); // ID should be assigned by controller
        assertEquals("newstudent", response.getBody().getName());
        assertEquals("student", response.getBody().getRole());

        // Verify user is added to the in-memory map
        assertTrue(authController.getUsers().values().stream()
                .anyMatch(user -> "newstudent".equals(user.getName()) && "student".equals(user.getRole())));
    }

    @Test
    void testRegisterUser_Success_Employer() {
        User newUser = new User(null, "newemployer", "employerpass", "employer");
        ResponseEntity<User> response = authController.registerUser(newUser);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertNotNull(response.getBody().getId());
        assertEquals("newemployer", response.getBody().getName());
        assertEquals("employer", response.getBody().getRole());
    }

    @Test
    void testRegisterUser_DuplicateName() {
        // Register a user first
        User user1 = new User(null, "duplicateuser", "pass1", "student");
        authController.registerUser(user1);

        // Attempt to register another user with the same name
        User user2 = new User(null, "duplicateuser", "pass2", "employer");

        // Expect a Conflict (409) status exception
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            authController.registerUser(user2);
        });

        assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
        assertTrue(exception.getReason().contains("Username already exists"));
    }

    @Test
    void testRegisterUser_InvalidData() {
        // Test missing name - Use a new variable
        User newUserMissingName = new User(null, "", "password", "student");
        assertThrows(ResponseStatusException.class, () -> authController.registerUser(newUserMissingName));

        // Test missing password - Use a new variable
        User newUserMissingPassword = new User(null, "testuser", "", "student");
        assertThrows(ResponseStatusException.class, () -> authController.registerUser(newUserMissingPassword));

        // Test invalid role - Use a new variable
        User newUserInvalidRole = new User(null, "testuser", "password", "invalidrole");
        assertThrows(ResponseStatusException.class, () -> authController.registerUser(newUserInvalidRole));
    }

    @Test
    void testLoginUser_Success_Employer() {
        // Use the default employer user created in initDefaultUsers
        Map<String, String> credentials = new HashMap<>();
        credentials.put("name", "Employer User");
        credentials.put("password", "employerpass");

        ResponseEntity<?> response = authController.loginUser(credentials);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody() instanceof Map); // Expecting a Map
        Map<String, Object> userInfo = (Map<String, Object>) response.getBody();
        assertEquals("Employer User", userInfo.get("name"));
        assertEquals("employer", userInfo.get("role"));
        assertNotNull(userInfo.get("id"));
    }

    @Test
    void testLoginUser_Success_Student() {
        // Use the default student user created in initDefaultUsers
        Map<String, String> credentials = new HashMap<>();
        credentials.put("name", "Student User");
        credentials.put("password", "studentpass");

        ResponseEntity<?> response = authController.loginUser(credentials);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody() instanceof Map);
        Map<String, Object> userInfo = (Map<String, Object>) response.getBody();
        assertEquals("Student User", userInfo.get("name"));
        assertEquals("student", userInfo.get("role"));
        assertNotNull(userInfo.get("id"));
    }

    @Test
    void testLoginUser_IncorrectPassword() {
        Map<String, String> credentials = new HashMap<>();
        credentials.put("name", "Employer User");
        credentials.put("password", "wrongpassword"); // Incorrect password

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            authController.loginUser(credentials);
        });

        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatusCode());
        assertTrue(exception.getReason().contains("Incorrect password"));
    }

    @Test
    void testLoginUser_UserNotFound() {
        Map<String, String> credentials = new HashMap<>();
        credentials.put("name", "NonExistentUser"); // User does not exist
        credentials.put("password", "somepass");

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            authController.loginUser(credentials);
        });

        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatusCode());
        assertTrue(exception.getReason().contains("User not found"));
    }

    @Test
    void testLoginUser_MissingCredentials() {
        Map<String, String> credentials = new HashMap<>();
        credentials.put("name", "testuser"); // Missing password

        assertThrows(ResponseStatusException.class, () -> authController.loginUser(credentials));

        credentials.clear();
        credentials.put("password", "testpass"); // Missing name
        assertThrows(ResponseStatusException.class, () -> authController.loginUser(credentials));
    }

    @Test
    void testFindUserById_Found() {
        // Find the default employer user by name first
        Optional<User> employerUserOptional = authController.getUsers().values().stream()
                .filter(user -> "Employer User".equals(user.getName()))
                .findFirst();

        assertTrue(employerUserOptional.isPresent(), "Default Employer User should be present");
        Long employerUserId = employerUserOptional.get().getId(); // Get the actual ID

        // Now use the actual ID to find the user by ID
        Optional<User> foundUser = authController.findUserById(employerUserId);

        assertTrue(foundUser.isPresent(), "User should be found by their actual ID");
        assertEquals(employerUserId, foundUser.get().getId()); // Assert ID matches
        assertEquals("Employer User", foundUser.get().getName()); // Assert name matches
    }

    @Test
    void testFindUserById_NotFound() {
        // Search for an ID that doesn't exist
        Optional<User> foundUser = authController.findUserById(999L);

        assertFalse(foundUser.isPresent());
    }
}

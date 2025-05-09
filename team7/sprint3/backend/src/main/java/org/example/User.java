package org.example;

public class User {
    private Long id;
    private String name;
    private String password; // Added password field
    private String role; // "employer" or "student"

    public User() {}

    public User(Long id, String name, String password, String role) {
        this.id = id;
        this.name = name;
        this.password = password;
        this.role = role;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    // Getter for password (might be needed for checking, but avoid exposing in responses)
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

}

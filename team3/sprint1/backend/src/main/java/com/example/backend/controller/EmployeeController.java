package com.example.backend.controller;

import org.springframework.web.bind.annotation.*;

import java.util.List;

import com.example.backend.service.EmployeeService;
import com.example.backend.model.Employee;

// not part of the lab; I added this
@CrossOrigin(origins = "https://localhost:3000")

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {
    private final EmployeeService service;

    public EmployeeController(EmployeeService service) {
        this.service = service;
    }

    @GetMapping
    public List<Employee> getEmployees() {
        return service.getAllEmployees();
    }

    @PostMapping
    public Employee addEmployee(@RequestBody Employee employee) {
        return service.saveEmployee(employee);
    }
}

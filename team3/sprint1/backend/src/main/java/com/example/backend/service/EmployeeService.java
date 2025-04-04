package com.example.backend.service;

import org.springframework.stereotype.Service;

import java.util.List;

import com.example.backend.model.Employee;
import com.example.backend.repository.EmployeeRepository;

@Service
public class EmployeeService {
    private final EmployeeRepository repository;

    public EmployeeService(EmployeeRepository repository) {
        this.repository = repository;
    }

    public List<Employee> getAllEmployees() {
        return repository.findAll();
    }

    public Employee saveEmployee(Employee employee) {
        return repository.save(employee);
    }

}

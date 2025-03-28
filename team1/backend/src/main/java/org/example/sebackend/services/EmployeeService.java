package org.example.sebackend.services;

import org.example.sebackend.models.Employee;
import org.example.sebackend.repositories.EmployeeRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmployeeService {

    private final EmployeeRepository repository;

    public EmployeeService(EmployeeRepository repository) {
        this.repository = repository;
    }

    public Employee saveEmployee(Employee employee) {
        return repository.save(employee);
    }

    public List<Employee> getAllEmployees() {
        return repository.findAll();
    }

    public void deleteEmployee(Long id) {
        repository.deleteById(id); // Delete employee from the database
    }
}

package com.consumerlending.generative.appraisal.service;

import com.consumerlending.generative.appraisal.domain.Employee;
import com.consumerlending.generative.appraisal.repository.EmployeeRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
public class EmployeeService {    
    private final EmployeeRepository employeeRepository;

    @Autowired
    public EmployeeService(EmployeeRepository employeeRepository) {

        this.employeeRepository = employeeRepository;
    }

    public List<Employee> findAll() {
        return employeeRepository.findAll();
    }

    public Optional<Employee> findByName(String name) {
        List<Employee> employees = employeeRepository.findByName(name);
        if (!employees.isEmpty()) {
            return Optional.of(employees.get(0));
        } else {
            return Optional.empty();
        }
    }
}
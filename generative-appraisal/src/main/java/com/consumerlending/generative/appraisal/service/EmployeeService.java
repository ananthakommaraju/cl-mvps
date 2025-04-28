package com.example.appraisal.service;

import com.example.appraisal.domain.Employee;
import com.example.appraisal.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;

    public List<Employee> findAll() {
        return employeeRepository.findAll();
    }

    public Employee findByName(String name) {
        return employeeRepository.findByName(name);
    }
}
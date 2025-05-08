package com.consumerlending.generative.appraisal.service;

import com.consumerlending.generative.appraisal.domain.Employee;
import com.consumerlending.generative.appraisal.repository.EmployeeRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.List;


@Service
public class EmployeeService {

    @Autowired
    private EmployeeRepository employeeRepository;

    public Optional<Employee> getEmployeeById(Long id) {
        return employeeRepository.findById(id);
    }
    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }
     public Employee saveEmployee(Employee employee) {
        return employeeRepository.save(employee);
    }
     public void deleteEmployee(Long id) {
         employeeRepository.deleteById(id);
    }
    
}
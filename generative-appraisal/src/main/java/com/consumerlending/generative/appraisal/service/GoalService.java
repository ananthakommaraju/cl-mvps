package com.consumerlending.generative.appraisal.service;

import com.consumerlending.generative.appraisal.domain.Goal;
import com.consumerlending.generative.appraisal.domain.Employee;
import com.consumerlending.generative.appraisal.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import com.consumerlending.generative.appraisal.repository.GoalRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class GoalService {

    @Autowired
    private GoalRepository goalRepository;
    @Autowired
    private EmployeeRepository employeeRepository;

    public List<Goal> findAll() {
        return goalRepository.findAll();
    }

    public List<Goal> findByEmployee(Long employeeId) {
        Optional<Employee> optionalEmployee = employeeRepository.findById(employeeId);
        if(optionalEmployee.isPresent()) {
            return goalRepository.findByEmployee(optionalEmployee.get());
        }
        return List.of();
    }
}
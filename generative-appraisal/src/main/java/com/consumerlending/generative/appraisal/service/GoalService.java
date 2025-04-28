package com.consumerlending.generative.appraisal.service;

import com.consumerlending.generative.appraisal.domain.Employee;
import com.consumerlending.generative.appraisal.domain.Goal;
import com.consumerlending.generative.appraisal.repository.EmployeeRepository;
import com.consumerlending.generative.appraisal.repository.GoalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class GoalService {

    private final GoalRepository goalRepository;
    private final EmployeeRepository employeeRepository;

    @Autowired
    public GoalService(GoalRepository goalRepository, EmployeeRepository employeeRepository) {
        this.goalRepository = goalRepository;
        this.employeeRepository = employeeRepository;
    }

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

    public Goal create(Goal goal) {
        return goalRepository.save(goal);
    }

    public Goal update(Goal goal) {
        return goalRepository.save(goal);
    }

    public void delete(Long id) {
        goalRepository.deleteById(id);
    }
}
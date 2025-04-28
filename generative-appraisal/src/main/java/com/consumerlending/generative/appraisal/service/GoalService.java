package com.example.appraisal.service;

import com.example.appraisal.domain.Employee;
import com.example.appraisal.domain.Goal;
import com.example.appraisal.repository.GoalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GoalService {

    @Autowired
    private GoalRepository goalRepository;

    public List<Goal> findAll() {
        return goalRepository.findAll();
    }

    public List<Goal> findByEmployee(Employee employee) {
        return goalRepository.findByEmployee(employee);
    }
}
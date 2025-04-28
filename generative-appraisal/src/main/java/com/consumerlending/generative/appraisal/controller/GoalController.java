package com.consumerlending.generative.appraisal.controller;

import com.consumerlending.generative.appraisal.domain.Goal;
import org.springframework.beans.factory.annotation.Autowired;
import com.consumerlending.generative.appraisal.service.GoalService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/goals")
public class GoalController {

    @Autowired
    private GoalService goalService;

    @GetMapping
    public List<Goal> findAll() {
        return goalService.findAll();
    }

    @GetMapping("/employee/{employeeId}")
    public List<Goal> findByEmployee(@PathVariable Long employeeId) {
        return goalService.findByEmployee(employeeId);
    }
}
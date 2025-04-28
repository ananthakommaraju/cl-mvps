package com.consumerlending.generative.appraisal.controller;

import com.consumerlending.generative.appraisal.service.GoalService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import java.util.List;
import com.consumerlending.generative.appraisal.domain.Goal;

@RestController
@RequestMapping("/api/goals")
public class GoalController {

    private final GoalService goalService;

    @Autowired
    public GoalController(GoalService goalService) {
        this.goalService = goalService;
    }
    
    @GetMapping
    public List<Goal> findAll() {
        return goalService.findAll();
    }

    @GetMapping("/employee/{employeeId}")
    public List<Goal> findByEmployee(@PathVariable Long employeeId) {
        return goalService.findByEmployee(employeeId);
    }

    @PostMapping
    public Goal createGoal(@Valid @RequestBody Goal goal) {
        return goalService.create(goal);
    }

    @PutMapping("/{id}")
    public Goal updateGoal(@PathVariable Long id, @Valid @RequestBody Goal goal) {
        goal.setId(id);
        return goalService.update(goal);
    }

    @DeleteMapping("/{id}")
    public void deleteGoal(@PathVariable Long id) {
        goalService.delete(id);
    }
}
package com.example.appraisal.controller;

import com.example.appraisal.domain.Objective;
import com.example.appraisal.service.ObjectiveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/objectives")
public class ObjectiveController {

    @Autowired
    private ObjectiveService objectiveService;

    @GetMapping
    public List<Objective> findAll() {
        return objectiveService.findAll();
    }

    @GetMapping("/byGoal/{goalId}")
    public List<Objective> findByGoal(@PathVariable Long goalId) {
        return objectiveService.findByGoal(goalId);
    }
}
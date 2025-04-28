package com.consumerlending.generative.appraisal.controller;

import com.consumerlending.generative.appraisal.domain.Objective;
import com.consumerlending.generative.appraisal.service.ObjectiveService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/objectives")
public class ObjectiveController {

    private final ObjectiveService objectiveService;

    @Autowired
    public ObjectiveController(ObjectiveService objectiveService) {
        this.objectiveService = objectiveService;
    }

    @GetMapping
    public List<Objective> findAll() {
        return objectiveService.findAll();
    }

    @GetMapping("/byGoal/{goalId}")
    public List<Objective> findByGoal(@PathVariable Long goalId) {
        return objectiveService.findByGoal(goalId);
    }
}
package com.consumerlending.generative.appraisal.controller;

import com.consumerlending.generative.appraisal.domain.Objective;
import com.consumerlending.generative.appraisal.service.ObjectiveService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


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

    @PostMapping
    public ResponseEntity<Objective> createObjective(@RequestBody Objective objective) {
        Objective createdObjective = objectiveService.createObjective(objective);
        return new ResponseEntity<>(createdObjective, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Objective> updateObjective(@PathVariable Long id, @RequestBody Objective objective) {
        if (!id.equals(objective.getId())) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        Objective updatedObjective = objectiveService.updateObjective(objective);
        return new ResponseEntity<>(updatedObjective, HttpStatus.OK);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteObjective(@PathVariable Long id) {
        objectiveService.deleteObjective(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
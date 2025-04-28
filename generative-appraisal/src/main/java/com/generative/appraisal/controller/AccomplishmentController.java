package com.generative.appraisal.controller;

import com.generative.appraisal.domain.Accomplishment;
import com.generative.appraisal.service.AccomplishmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/accomplishments")
public class AccomplishmentController {

    @Autowired
    private AccomplishmentService accomplishmentService;

    @GetMapping
    public List<Accomplishment> findAll() {
        return accomplishmentService.findAll();
    }

    @GetMapping("/byObjective/{objectiveId}")
    public List<Accomplishment> findByObjective(@PathVariable Long objectiveId) {
        return accomplishmentService.findByObjective(objectiveId);
    }
}
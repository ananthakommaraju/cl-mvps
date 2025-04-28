package com.consumerlending.generative.appraisal.controller;

import com.consumerlending.generative.appraisal.domain.Accomplishment;
import com.consumerlending.generative.appraisal.service.AccomplishmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/accomplishments")
public class AccomplishmentController {

    private final AccomplishmentService accomplishmentService;

    @Autowired
    public AccomplishmentController(AccomplishmentService accomplishmentService) {
        this.accomplishmentService = accomplishmentService;
    }

    @org.springframework.web.bind.annotation.GetMapping
    public List<Accomplishment> findAll() {
        return accomplishmentService.findAll();
    }

    @org.springframework.web.bind.annotation.GetMapping("/byObjective/{objectiveId}")
    public List<Accomplishment> findByObjective(@PathVariable Long objectiveId) {
        return accomplishmentService.findByObjective(objectiveId);
    }
}
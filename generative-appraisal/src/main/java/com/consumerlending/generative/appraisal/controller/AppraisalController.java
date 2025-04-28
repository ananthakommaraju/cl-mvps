package com.consumerlending.generative.appraisal.controller;

import com.consumerlending.generative.appraisal.domain.Appraisal;
import com.consumerlending.generative.appraisal.service.AppraisalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/appraisals")
public class AppraisalController {

    private final AppraisalService appraisalService;
    
    @Autowired
    public AppraisalController(AppraisalService appraisalService) {
        this.appraisalService = appraisalService;
    }

    @GetMapping
    public List<Appraisal> getAllAppraisals() {
        return appraisalService.findAll();
    }

    @GetMapping("/employee/{employeeId}")
    public List<Appraisal> getAppraisalsByEmployee(@PathVariable Long employeeId) {
        return appraisalService.findByEmployee(employeeId);
    }
}
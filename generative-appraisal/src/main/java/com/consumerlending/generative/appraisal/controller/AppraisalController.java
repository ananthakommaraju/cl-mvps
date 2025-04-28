package com.consumerlending.generative.appraisal.controller;

import com.consumerlending.generative.appraisal.domain.Appraisal;
import com.consumerlending.generative.appraisal.service.AppraisalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpStatus;
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

    @PostMapping
    public Appraisal createAppraisal(@RequestBody Appraisal appraisal) {
        return appraisalService.createAppraisal(appraisal);
    }

    @PutMapping("/{id}")
    public Appraisal updateAppraisal(@PathVariable Long id, @RequestBody Appraisal appraisal) {
        appraisal.setId(id);
        return appraisalService.updateAppraisal(appraisal);
    }

    @DeleteMapping("/{id}")
    public void deleteAppraisal(@PathVariable Long id) {
        appraisalService.deleteAppraisal(id);
    }

}
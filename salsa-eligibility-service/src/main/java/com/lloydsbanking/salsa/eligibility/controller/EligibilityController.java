package com.lloydsbanking.salsa.eligibility.controller;

import com.lloydsbanking.salsa.eligibility.EligibilityService;
import com.lloydsbanking.salsa.eligibility.model.DetermineElegibileInstructionsRequest;
import com.lloydsbanking.salsa.eligibility.model.DetermineElegibleInstructionsResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/eligibility")
public class EligibilityController {

    @Autowired
    private EligibilityService eligibilityService;

    @PostMapping("/determine")
    public DetermineElegibleInstructionsResponse determineEligibility(@RequestBody DetermineElegibileInstructionsRequest request) {
        return eligibilityService.determineEligibleInstructions(request);
    }
}
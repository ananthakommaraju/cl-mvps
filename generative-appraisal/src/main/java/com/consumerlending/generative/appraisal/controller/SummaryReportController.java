package com.consumerlending.generative.appraisal.controller;

import com.consumerlending.generative.appraisal.domain.AppraisalSummary;
import com.consumerlending.generative.appraisal.domain.SummaryReport;
import com.consumerlending.generative.appraisal.service.AppraisalService;
import com.consumerlending.generative.appraisal.service.SummaryReportService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@RestController
@RequestMapping("/api/appraisalsummaries")
public class SummaryReportController {

    private final SummaryReportService summaryReportService;
    private final AppraisalService appraisalService;

    @Autowired
    public SummaryReportController(SummaryReportService summaryReportService, AppraisalService appraisalService) {
        this.summaryReportService = summaryReportService;
        this.appraisalService = appraisalService;
    }

    @GetMapping
    public List<SummaryReport> getAllSummaryReports() {
        return summaryReportService.findAll();
    }

    @GetMapping("/employee/{employeeId}")
    public List<SummaryReport> getSummaryReportsByEmployee(@PathVariable Long employeeId) {
        return summaryReportService.findByEmployee(employeeId);
    }

    @PostMapping("/employee/{employeeId}")
    public AppraisalSummary generateAppraisalSummary(@PathVariable Long employeeId) {
        return appraisalService.generateAppraisalSummary(employeeId);
    }
}
package com.consumerlending.generative.appraisal.controller;

import com.consumerlending.generative.appraisal.domain.SummaryReport;
import com.consumerlending.generative.appraisal.service.SummaryReportService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/summaryreports")
public class SummaryReportController {

    private final SummaryReportService summaryReportService;

    @Autowired
    public SummaryReportController(SummaryReportService summaryReportService) {
        this.summaryReportService = summaryReportService;
    }

    @GetMapping
    public List<SummaryReport> getAllSummaryReports() {
        return summaryReportService.findAll();
    }

    @GetMapping("/employee/{employeeId}")
    public List<SummaryReport> getSummaryReportsByEmployee(@PathVariable Long employeeId) {
        return summaryReportService.findByEmployee(employeeId);
    }
}
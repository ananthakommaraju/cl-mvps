package com.consumerlending.generative.appraisal.service;

import com.consumerlending.generative.appraisal.repository.EmployeeRepository;
import com.consumerlending.generative.appraisal.dto.StatusReportRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import com.consumerlending.generative.appraisal.domain.Employee;
import com.consumerlending.generative.appraisal.domain.SummaryReport;
import com.consumerlending.generative.appraisal.repository.SummaryReportRepository;

import java.io.IOException;

@Service
public class SummaryReportService {

    private final EmployeeRepository employeeRepository;
    private final SummaryReportRepository summaryReportRepository;

    @Autowired
    public SummaryReportService(EmployeeRepository employeeRepository,
                                SummaryReportRepository summaryReportRepository) {
        this.employeeRepository = employeeRepository;
        this.summaryReportRepository = summaryReportRepository;
    }
    
    public List<SummaryReport> findAll() {
        return summaryReportRepository.findAll();
    }

    public List<SummaryReport> findByEmployee(Long employeeId) {
        Employee employee = employeeRepository.findById(employeeId).orElse(null);
        return summaryReportRepository.findByEmployee(employee);
    }

    public void generateReportFromPPT(StatusReportRequest statusReportRequest) throws IOException {
    }

}
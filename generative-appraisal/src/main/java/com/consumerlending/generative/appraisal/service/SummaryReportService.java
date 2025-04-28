package com.consumerlending.generative.appraisal.service;

import com.consumerlending.generative.appraisal.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import com.consumerlending.generative.appraisal.domain.Employee;
import com.consumerlending.generative.appraisal.domain.SummaryReport;
import com.consumerlending.generative.appraisal.repository.SummaryReportRepository;

@Service
public class SummaryReportService {

    @Autowired
    private EmployeeRepository employeeRepository;
    @Autowired
    private EmployeeService employeeService;
    @Autowired
    private SummaryReportRepository summaryReportRepository;

    public List<SummaryReport> findAll() {
        return summaryReportRepository.findAll();
    }

    public List<SummaryReport> findByEmployee(Long employeeId) {
        Employee employee = employeeRepository.findById(employeeId).orElse(null);
        return summaryReportRepository.findByEmployee(employee);
    }

}
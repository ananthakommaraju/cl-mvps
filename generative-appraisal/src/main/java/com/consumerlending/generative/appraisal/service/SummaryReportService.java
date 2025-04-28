package com.example.appraisal.service;

import com.example.appraisal.domain.Employee;
import com.example.appraisal.domain.SummaryReport;
import com.example.appraisal.repository.SummaryReportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SummaryReportService {

    @Autowired
    private SummaryReportRepository summaryReportRepository;

    public List<SummaryReport> findAll() {
        return summaryReportRepository.findAll();
    }

    public List<SummaryReport> findByEmployee(Employee employee) {
        return summaryReportRepository.findByEmployee(employee);
    }
}
package com.consumerlending.generative.appraisal.service;

import com.consumerlending.generative.appraisal.domain.Appraisal;
import com.consumerlending.generative.appraisal.repository.EmployeeRepository;

import com.consumerlending.generative.appraisal.repository.AppraisalRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AppraisalService {
    private final AppraisalRepository appraisalRepository;
    private final EmployeeRepository employeeRepository;

    @Autowired
    public AppraisalService(AppraisalRepository appraisalRepository, EmployeeRepository employeeRepository) {
        this.appraisalRepository = appraisalRepository;
        this.employeeRepository = employeeRepository;
    }

    public List<Appraisal> findAll() {
        return appraisalRepository.findAll();
    }

    public List<Appraisal> findByEmployee(Long employeeId) {
        com.consumerlending.generative.appraisal.domain.Employee employee = employeeRepository.findById(employeeId).orElse(null);
        return appraisalRepository.findByEmployee(employee);
    }
}
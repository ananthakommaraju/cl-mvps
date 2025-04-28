package com.consumerlending.generative.appraisal.service;

import com.consumerlending.generative.appraisal.domain.Appraisal;
import com.consumerlending.generative.appraisal.domain.Employee;
import org.springframework.beans.factory.annotation.Autowired;
import com.consumerlending.generative.appraisal.repository.EmployeeRepository;

import com.consumerlending.generative.appraisal.repository.AppraisalRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AppraisalService {

    @Autowired
    private AppraisalRepository appraisalRepository;
    
    @Autowired
    private EmployeeRepository employeeRepository;

    public List<Appraisal> findAll() {
        return appraisalRepository.findAll();
    }
    
    public List<Appraisal> findByEmployee(Long employeeId) {
        Employee employee = employeeRepository.findById(employeeId).orElse(null);
        return appraisalRepository.findByEmployee(employee);
    }
}
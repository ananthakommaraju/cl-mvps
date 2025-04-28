package com.example.appraisal.service;

import com.example.appraisal.domain.Appraisal;
import com.example.appraisal.domain.Employee;
import com.example.appraisal.repository.AppraisalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AppraisalService {

    @Autowired
    private AppraisalRepository appraisalRepository;

    public List<Appraisal> findAll() {
        return appraisalRepository.findAll();
    }

    public List<Appraisal> findByEmployee(Employee employee) {
        return appraisalRepository.findByEmployee(employee);
    }
}
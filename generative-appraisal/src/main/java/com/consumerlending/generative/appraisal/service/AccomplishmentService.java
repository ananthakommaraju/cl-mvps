package com.example.appraisal.service;

import com.example.appraisal.domain.Accomplishment;
import com.example.appraisal.domain.Objective;
import com.example.appraisal.repository.AccomplishmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AccomplishmentService {

    @Autowired
    private AccomplishmentRepository accomplishmentRepository;

    public List<Accomplishment> findAll() {
        return accomplishmentRepository.findAll();
    }

    public List<Accomplishment> findByObjective(Objective objective) {
        return accomplishmentRepository.findByObjective(objective);
    }
}
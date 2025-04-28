package com.consumerlending.generative.appraisal.service;

import com.consumerlending.generative.appraisal.domain.Accomplishment;
import com.consumerlending.generative.appraisal.domain.Objective;
import org.springframework.beans.factory.annotation.Autowired;
import com.consumerlending.generative.appraisal.repository.AccomplishmentRepository;
import com.consumerlending.generative.appraisal.repository.ObjectiveRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AccomplishmentService {

    @Autowired
    private AccomplishmentRepository accomplishmentRepository;
    @Autowired
    private ObjectiveRepository objectiveRepository;

    public List<Accomplishment> findAll() {
        return accomplishmentRepository.findAll();
    }

    public List<Accomplishment> findByObjective(Long objectiveId) {
        Objective objective = objectiveRepository.findById(objectiveId).orElse(null);
        if(objective == null) return List.of();
        return accomplishmentRepository.findByObjective(objective);
    }
}
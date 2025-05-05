package com.consumerlending.generative.appraisal.service;

import com.consumerlending.generative.appraisal.domain.Accomplishment;
import com.consumerlending.generative.appraisal.domain.Objective;

import com.consumerlending.generative.appraisal.repository.AccomplishmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import com.consumerlending.generative.appraisal.repository.ObjectiveRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class AccomplishmentService {

    private final AccomplishmentRepository accomplishmentRepository;
    private final ObjectiveRepository objectiveRepository;

    @Autowired
    public AccomplishmentService(AccomplishmentRepository accomplishmentRepository, ObjectiveRepository objectiveRepository) {
        this.accomplishmentRepository = accomplishmentRepository;
        this.objectiveRepository = objectiveRepository;
    }

    public List<Accomplishment> findAll() {
        return accomplishmentRepository.findAll();
    }

    public List<Accomplishment> findByObjective(Long objectiveId) {
        Objective objective = objectiveRepository.findById(objectiveId).orElse(null); if(objective == null) return List.of();
        return accomplishmentRepository.findByObjective(objective);
    }
    public Accomplishment createAccomplishment(Accomplishment accomplishment) {
        return accomplishmentRepository.save(accomplishment);
    }

    public Accomplishment updateAccomplishment(Long id, Accomplishment accomplishment) {
        accomplishment.setId(id);
        return accomplishmentRepository.save(accomplishment);
    }

    public void deleteAccomplishment(Long id) {
        accomplishmentRepository.deleteById(id);
    }
}
package com.example.appraisal.service;

import com.example.appraisal.domain.Goal;
import com.example.appraisal.domain.Objective;
import com.example.appraisal.repository.ObjectiveRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ObjectiveService {

    @Autowired
    private ObjectiveRepository objectiveRepository;

    public List<Objective> findAll() {
        return objectiveRepository.findAll();
    }

    public List<Objective> findByGoal(Goal goal) {
        return objectiveRepository.findByGoal(goal);
    }
}
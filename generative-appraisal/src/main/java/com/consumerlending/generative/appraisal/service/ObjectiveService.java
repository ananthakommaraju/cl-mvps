package com.consumerlending.generative.appraisal.service;

import com.consumerlending.generative.appraisal.domain.Goal;
import com.consumerlending.generative.appraisal.domain.Objective;
import com.consumerlending.generative.appraisal.repository.ObjectiveRepository;
import com.consumerlending.generative.appraisal.repository.GoalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ObjectiveService {

    @Autowired
    private ObjectiveRepository objectiveRepository;
    @Autowired
    private GoalRepository goalRepository;

    public List<Objective> findAll() {
        return objectiveRepository.findAll();
    }

    public List<Objective> findByGoal(Long goalId) {
        Goal goal = goalRepository.findById(goalId).orElse(null);
        if (goal == null)
            return null;
         return objectiveRepository.findByGoal(goal);
    }
}
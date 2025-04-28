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

    private ObjectiveRepository objectiveRepository;

    private GoalRepository goalRepository;

    @Autowired
    public ObjectiveService(ObjectiveRepository objectiveRepository, GoalRepository goalRepository) {
        this.objectiveRepository = objectiveRepository;
        this.goalRepository = goalRepository;
    }

    public List<Objective> findAll() {
        return objectiveRepository.findAll();
    }
    public List<Objective> findByGoal(Long goalId) {
        Goal goal = goalRepository.findById(goalId).orElse(null);
        if (goal == null)
            return null;
         return objectiveRepository.findByGoal(goal);
    }
    public Objective createObjective(Objective objective) {
        return objectiveRepository.save(objective);
    }

    public Objective updateObjective(Objective objective) {
        return objectiveRepository.save(objective);
    }

    public void deleteObjective(Long id) {
        objectiveRepository.deleteById(id);
    }
}
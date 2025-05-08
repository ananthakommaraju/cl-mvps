package com.consumerlending.generative.appraisal.service;

import com.consumerlending.generative.appraisal.domain.Employee;
import com.consumerlending.generative.appraisal.domain.Objective;
import com.consumerlending.generative.appraisal.domain.Team;
import com.consumerlending.generative.appraisal.repository.ObjectiveRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ObjectiveService {

    @Autowired
    private ObjectiveRepository objectiveRepository;

    public Optional<Objective> getObjectiveById(Long id) {
        return objectiveRepository.findById(id);
    }

    public List<Objective> getObjectivesByEmployee(Employee employee) {
        return objectiveRepository.findByEmployee(employee);
    }

    public Objective addObjectiveToEmployee(Employee employee, Objective objective) {
        objective.setEmployee(employee);
        return objectiveRepository.save(objective);
    }

    public Objective updateObjective(Objective objective) {
        return objectiveRepository.save(objective);
    }

    public List<Objective> getObjectivesByTeam(Team team) {
        List<Employee> employees = team.getEmployees();
        return objectiveRepository.findByEmployeeIn(employees);
    }

    public void deleteObjective(Long id) {
        objectiveRepository.deleteById(id);
    }

}
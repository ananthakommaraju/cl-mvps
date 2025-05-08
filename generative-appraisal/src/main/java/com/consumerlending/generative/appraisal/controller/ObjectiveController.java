package com.consumerlending.generative.appraisal.controller;

import com.consumerlending.generative.appraisal.domain.Employee;
import com.consumerlending.generative.appraisal.domain.Objective;
import com.consumerlending.generative.appraisal.domain.Team;
import com.consumerlending.generative.appraisal.service.EmployeeService;
import com.consumerlending.generative.appraisal.service.ObjectiveService;

import com.consumerlending.generative.appraisal.service.TeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
public class ObjectiveController {

    @Autowired
    private ObjectiveService objectiveService;
    @Autowired
    private EmployeeService employeeService;
    @Autowired
    private TeamService teamService;

    @GetMapping("/objectives/{id}")
    public ResponseEntity<Objective> getObjectiveById(@PathVariable Long id) {
        Optional<Objective> objective = objectiveService.getObjectiveById(id);
        return objective.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/employees/{employeeId}/objectives")
    public List<Objective> getObjectivesByEmployee(@PathVariable Long employeeId) {
        Optional<Employee> employee = employeeService.getEmployeeById(employeeId);
        return employee.map(objectiveService::getObjectivesByEmployee).orElse(List.of());
    }

    @PostMapping("/employees/{employeeId}/objectives")
    public Objective addObjectiveToEmployee(@PathVariable Long employeeId, @RequestBody Objective objective) {
        Optional<Employee> employee = employeeService.getEmployeeById(employeeId);
        return employee.map(e -> objectiveService.addObjectiveToEmployee(e, objective)).orElse(null);
    }

    @PutMapping("/objectives")
    public Objective updateObjective(@RequestBody Objective objective) {
        return objectiveService.updateObjective(objective);
    }

    @GetMapping("/teams/{teamId}/objectives")
    public List<Objective> getObjectivesByTeam(@PathVariable Long teamId) {
        Optional<Team> team = teamService.getTeamById(teamId);
        return team.map(objectiveService::getObjectivesByTeam).orElse(List.of());
    }
    @DeleteMapping("/objectives/{id}")
    public ResponseEntity<Void> deleteObjective(@PathVariable Long id) {
        objectiveService.deleteObjective(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
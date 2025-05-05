package com.consumerlending.generative.appraisal.service;

import com.consumerlending.generative.appraisal.domain.Accomplishment;
import com.consumerlending.generative.appraisal.domain.Appraisal;
import com.consumerlending.generative.appraisal.domain.AppraisalSummary;
import com.consumerlending.generative.appraisal.domain.Employee;
import com.consumerlending.generative.appraisal.domain.Goal;
import com.consumerlending.generative.appraisal.domain.Objective;
import com.consumerlending.generative.appraisal.repository.AppraisalSummaryRepository;
import com.consumerlending.generative.appraisal.repository.EmployeeRepository;

import com.consumerlending.generative.appraisal.repository.AppraisalRepository;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
public class AppraisalService {
    private final AppraisalRepository appraisalRepository;
    private final EmployeeRepository employeeRepository;
    private final GeminiService geminiService;
    private final AppraisalSummaryRepository appraisalSummaryRepository;

    @Autowired
    public AppraisalService(AppraisalRepository appraisalRepository, EmployeeRepository employeeRepository, GeminiService geminiService, AppraisalSummaryRepository appraisalSummaryRepository) {
        this.appraisalRepository = appraisalRepository;
        this.employeeRepository = employeeRepository;
        this.geminiService = geminiService;
        this.appraisalSummaryRepository = appraisalSummaryRepository;
    }

    public List<Appraisal> findAll() {
        return appraisalRepository.findAll();
    }

    public List<Appraisal> findByEmployee(Long employeeId) {
        com.consumerlending.generative.appraisal.domain.Employee employee = employeeRepository.findById(employeeId).orElse(null);
        return appraisalRepository.findByEmployee(employee);
    }

    public Appraisal createAppraisal(Appraisal appraisal) {
        return appraisalRepository.save(appraisal);
    }

    public Appraisal updateAppraisal(Appraisal updatedAppraisal) {
        return appraisalRepository.save(updatedAppraisal);
    }
    
    public void deleteAppraisal(Long id) {
        appraisalRepository.deleteById(id);
    }

    public AppraisalSummary generateAppraisalSummary(Long employeeId) {
        Employee employee = employeeRepository.findById(employeeId).orElseThrow(() -> new RuntimeException("Employee not found"));

        List<Goal> goals = employee.getGoals();

        List<String> goalSummaries = new ArrayList<>();
        for (Goal goal : goals) {
            // Summarize Goal
            StringBuilder goalPromptBuilder = new StringBuilder("Summarize the following goal and the related objectives:\n");
            goalPromptBuilder.append("Goal: ").append(goal.getDescription()).append("\n");

            for (Objective objective : goal.getObjectives()) {
                goalPromptBuilder.append("Objective: ").append(objective.getDescription()).append("\n");
                if (!objective.getAccomplishments().isEmpty()) {
                    goalPromptBuilder.append("Accomplishments:\n");
                    for (Accomplishment accomplishment : objective.getAccomplishments()) {
                        goalPromptBuilder.append("- ").append(accomplishment.getDescription()).append("\n");
                    }
                }
            }

            String goalPrompt = goalPromptBuilder.toString();
            String goalSummary = geminiService.generateSummary(goalPrompt);
            goal.setSummary(goalSummary);
            
            for (Objective objective : goal.getObjectives()){
                objective.setSummary(geminiService.generateSummary(objective.getDescription()));
            }
            goalSummaries.add(goalSummary);
        }

        // Summarize Appraisal
        StringBuilder appraisalPromptBuilder = new StringBuilder("Summarize the following goals:\n");
        goalSummaries.forEach(s -> appraisalPromptBuilder.append("- ").append(s).append("\n"));
        String appraisalPrompt = appraisalPromptBuilder.toString();
        String appraisalSummaryText = geminiService.generateSummary(appraisalPrompt);

        AppraisalSummary appraisalSummary = new AppraisalSummary();
        appraisalSummary.setEmployee(employee);
        appraisalSummary.setSummary(appraisalSummaryText);
        return appraisalSummaryRepository.save(appraisalSummary);
    }
}
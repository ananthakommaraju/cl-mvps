package com.consumerlending.generative.appraisal.repository;

import com.consumerlending.generative.appraisal.domain.Goal;
import com.consumerlending.generative.appraisal.domain.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GoalRepository extends JpaRepository<Goal, Long> {
    List<Goal> findByEmployee(Employee employee);
}
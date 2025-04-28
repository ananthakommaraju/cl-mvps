package com.example.appraisal.repository;

import com.example.appraisal.domain.Goal;
import com.example.appraisal.domain.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GoalRepository extends JpaRepository<Goal, Long> {
    List<Goal> findByEmployee(Employee employee);
}
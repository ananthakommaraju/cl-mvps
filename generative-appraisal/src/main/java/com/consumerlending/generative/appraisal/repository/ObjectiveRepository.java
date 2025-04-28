package com.example.appraisal.repository;

import com.example.appraisal.domain.Goal;
import com.example.appraisal.domain.Objective;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ObjectiveRepository extends JpaRepository<Objective, Long> {
    List<Objective> findByGoal(Goal goal);
}
package com.consumerlending.generative.appraisal.repository;

import com.consumerlending.generative.appraisal.domain.Employee;
import com.consumerlending.generative.appraisal.domain.Objective;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ObjectiveRepository extends JpaRepository<Objective, Long> {
    List<Objective> findByEmployee(Employee employee);
    List<Objective> findByEmployeeIn(List<Employee> employees);
}
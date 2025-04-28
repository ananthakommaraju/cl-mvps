package com.consumerlending.generative.appraisal.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.consumerlending.generative.appraisal.domain.Appraisal;
import com.consumerlending.generative.appraisal.domain.Employee;
import java.util.List;

@Repository
public interface AppraisalRepository extends JpaRepository<Appraisal, Long> {
    List<Appraisal> findByEmployee(Employee employee);
}
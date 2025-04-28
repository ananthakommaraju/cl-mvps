package com.example.appraisal.repository;

import com.example.appraisal.domain.Appraisal;
import com.example.appraisal.domain.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AppraisalRepository extends JpaRepository<Appraisal, Long> {
    List<Appraisal> findByEmployee(Employee employee);
}
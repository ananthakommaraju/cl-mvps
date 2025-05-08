package com.consumerlending.generative.appraisal.repository;

import com.consumerlending.generative.appraisal.domain.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
}
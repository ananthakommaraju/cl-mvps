package com.consumerlending.generative.appraisal.repository;

import com.consumerlending.generative.appraisal.domain.Employee;
import com.consumerlending.generative.appraisal.domain.SummaryReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SummaryReportRepository extends JpaRepository<SummaryReport, Long> {
    List<SummaryReport> findByEmployee(Employee employee);
}
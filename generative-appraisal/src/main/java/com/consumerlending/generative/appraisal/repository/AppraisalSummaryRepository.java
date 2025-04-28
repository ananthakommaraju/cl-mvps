package com.consumerlending.generative.appraisal.repository;

import com.consumerlending.generative.appraisal.domain.AppraisalSummary;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppraisalSummaryRepository extends JpaRepository<AppraisalSummary, Long> {
}
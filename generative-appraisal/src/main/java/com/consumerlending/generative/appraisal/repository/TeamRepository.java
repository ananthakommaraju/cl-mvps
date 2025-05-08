package com.consumerlending.generative.appraisal.repository;

import com.consumerlending.generative.appraisal.domain.Team;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamRepository extends JpaRepository<Team, Long> {
}
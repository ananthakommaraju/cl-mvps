package com.consumerlending.generative.appraisal.repository;

import com.consumerlending.generative.appraisal.domain.Accomplishment;
import com.consumerlending.generative.appraisal.domain.Objective;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AccomplishmentRepository extends JpaRepository<Accomplishment, Long> {
    List<Accomplishment> findByObjective(Objective objective);
}
package com.consumerlending.generative.appraisal.domain;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class AppraisalSummary {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Employee employee;

    @Column(length = 10000)
    private String summary;
}
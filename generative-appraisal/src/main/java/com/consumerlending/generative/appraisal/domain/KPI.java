package com.consumerlending.generative.appraisal.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class KPI {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;
    private String target;  // Or Double/Integer if appropriate
    private String actual;  // Or Double/Integer if appropriate

    // (Optional) Add a unit of measure if needed
    private String unit;
}
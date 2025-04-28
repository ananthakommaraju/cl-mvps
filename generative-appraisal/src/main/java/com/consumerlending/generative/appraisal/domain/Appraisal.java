package com.consumerlending.generative.appraisal.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Appraisal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Employee employee;

    @OneToMany(mappedBy = "appraisal", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Goal> goals;
}
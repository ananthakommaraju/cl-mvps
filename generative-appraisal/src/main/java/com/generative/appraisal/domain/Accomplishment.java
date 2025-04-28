package com.example.appraisal.domain;

import javax.persistence.*;

@Entity
public class Accomplishment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String description;

    @ManyToOne
    @JoinColumn(name = "objective_id")
    private Objective objective;

    // Constructors, getters, and setters

    public Accomplishment() {
    }

    public Accomplishment(String description, Objective objective) {
        this.description = description;
        this.objective = objective;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Objective getObjective() {
        return objective;
    }

    public void setObjective(Objective objective) {
        this.objective = objective;
    }
}
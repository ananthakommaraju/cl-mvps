package com.example.appraisal.domain;

import javax.persistence.*;
import java.util.List;

@Entity
public class Objective {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String description;

    @ManyToOne
    @JoinColumn(name = "goal_id")
    private Goal goal;

    @OneToMany(mappedBy = "objective", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Accomplishment> accomplishments;

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

    public Goal getGoal() {
        return goal;
    }

    public void setGoal(Goal goal) {
        this.goal = goal;
    }

    public List<Accomplishment> getAccomplishments() {
        return accomplishments;
    }

    public void setAccomplishments(List<Accomplishment> accomplishments) {
        this.accomplishments = accomplishments;
    }
}
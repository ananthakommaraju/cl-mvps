package com.lloydsbanking.salsa.eligibility.service.questions;

import lib_sim_bo.businessobjects.Individual;

public abstract class AbstractIndividualQuestion implements AskQuestion {

    protected Individual individual;
    protected String threshold;

    protected AbstractIndividualQuestion() {
    }

    public AbstractIndividualQuestion givenAnIndividual(Individual individual) {
        this.individual = individual;
        return this;
    }

    public AbstractIndividualQuestion givenAValue(String threshold) {
        this.threshold = threshold;
        return this;
    }
}

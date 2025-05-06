package com.lloydsbanking.salsa.eligibility.service.questions;

public abstract class AbstractCustomerQuestion implements AskQuestion {
    protected String existingSortCode;

    protected String existingAccountNumber;

    public AbstractCustomerQuestion givenAnExistingSortCode(String existingSortCode) {
        this.existingSortCode = existingSortCode;
        return this;
    }

    public AbstractCustomerQuestion givenAnExistingAccountNumber(String existingAccountNumber) {
        this.existingAccountNumber = existingAccountNumber;
        return this;
    }
}

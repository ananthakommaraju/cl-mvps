package com.lloydsbanking.salsa.eligibility.service.questions;

public abstract class AbstractProductListAccountQuestion extends AbstractProductListQuestion {

    protected String accountNumber;
    protected String sortCode;

    public AbstractProductListAccountQuestion givenAnAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
        return this;
    }

    public AbstractProductListAccountQuestion givenASortCode(String sortCode) {
        this.sortCode = sortCode;
        return this;
    }

}

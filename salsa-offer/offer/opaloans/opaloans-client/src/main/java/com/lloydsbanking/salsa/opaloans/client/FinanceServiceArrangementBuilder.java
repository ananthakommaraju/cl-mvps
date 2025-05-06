package com.lloydsbanking.salsa.opaloans.client;

import lib_sim_bo.businessobjects.Channel;
import lib_sim_bo.businessobjects.Customer;
import lib_sim_bo.businessobjects.FinanceServiceArrangement;
import lib_sim_bo.businessobjects.Product;

public class FinanceServiceArrangementBuilder {

    FinanceServiceArrangement financeServiceArrangement;

    public FinanceServiceArrangementBuilder() {
        this.financeServiceArrangement = new FinanceServiceArrangement();
    }

    public FinanceServiceArrangement build() {
        return financeServiceArrangement;
    }

    public FinanceServiceArrangementBuilder arrangementType() {
        financeServiceArrangement.setArrangementType("Loans");
        return this;
    }

    public FinanceServiceArrangementBuilder associatedProduct(Product associatedProduct) {
        financeServiceArrangement.setAssociatedProduct(associatedProduct);
        return this;
    }

    public FinanceServiceArrangementBuilder initiatedThrough(Channel initiatedThrough) {
        financeServiceArrangement.setInitiatedThrough(initiatedThrough);
        return this;
    }

    public FinanceServiceArrangementBuilder primaryInvolvedParty(Customer primaryInvolvedParty) {
        financeServiceArrangement.setPrimaryInvolvedParty(primaryInvolvedParty);
        return this;
    }

    public FinanceServiceArrangementBuilder applicationType(String applicationType) {
        financeServiceArrangement.setApplicationType(applicationType);
        return this;
    }
}

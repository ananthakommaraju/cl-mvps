package com.lloydsbanking.salsa.eligibility.service.questions;

import com.lloydsbanking.salsa.eligibility.service.utility.ProductArrangementFacade;

public class QuestionHasEligibleCreditCardForBalanceTransfer extends AbstractProductListQuestion implements AskQuestion  {

    public static QuestionHasEligibleCreditCardForBalanceTransfer pose() {
        return new QuestionHasEligibleCreditCardForBalanceTransfer();
    }

    public boolean ask() {
        for (ProductArrangementFacade customerArrangement : productArrangements) {
            if (threshold.equalsIgnoreCase(customerArrangement.getParentInstructionMnemonic())) {
                return true;
            }
        }
        return false;
    }
}

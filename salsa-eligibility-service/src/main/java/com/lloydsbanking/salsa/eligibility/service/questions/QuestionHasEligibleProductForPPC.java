package com.lloydsbanking.salsa.eligibility.service.questions;

import com.lloydsbanking.salsa.eligibility.service.utility.ProductArrangementFacade;

public class QuestionHasEligibleProductForPPC extends AbstractProductListQuestion implements AskQuestion  {

    public static QuestionHasEligibleProductForPPC pose() {
        return new QuestionHasEligibleProductForPPC();
    }

    public boolean ask() {
        for (ProductArrangementFacade customerArrangement : productArrangements) {
            if (customerArrangement.isCreditCardFinanceServiceArrangement() &&
                    !customerArrangement.isHasEmbeddedInsurance()) {
                return true;
            }
        }
        return false;
    }
}

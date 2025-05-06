package com.lloydsbanking.salsa.eligibility.service.questions;

import com.lloydsbanking.salsa.eligibility.service.utility.ProductArrangementFacade;

public class QuestionHasActiveCurrentAccount extends AbstractProductListQuestion implements AskQuestion {

    private static final String CURRENT_ACCOUNT_STATUS_CODE = "001";

    private static final String DORMANT = "Dormant";

    public static QuestionHasActiveCurrentAccount pose() {
        return new QuestionHasActiveCurrentAccount();
    }

    public boolean ask() {
        for (ProductArrangementFacade customerArrangement : productArrangements) {
            if (null != customerArrangement && null != customerArrangement.getAssociatedProduct() && null != customerArrangement.getAssociatedProduct()
                    .getStatusCode()) {
                String status = customerArrangement.getLifecycleStatus();
                if (CURRENT_ACCOUNT_STATUS_CODE.equals(customerArrangement.getAssociatedProduct().getStatusCode()) && !(DORMANT.equalsIgnoreCase(status))) {
                    return true;
                }
            }
        }
        return false;
    }

}

package com.lloydsbanking.salsa.eligibility.service.questions;

import com.lloydsbanking.salsa.eligibility.service.utility.ProductArrangementFacade;
import lib_sim_bo.businessobjects.Product;

public class QuestionHasMoreThanThresholdInstancesOfCreditCard extends AbstractProductListQuestion implements AskQuestion {

    private static final String PRODUCT_TYPE_CC = "3";

    public static QuestionHasMoreThanThresholdInstancesOfCreditCard pose() {
        return new QuestionHasMoreThanThresholdInstancesOfCreditCard();
    }

    public boolean ask() {
        int matches = 0;

        for (ProductArrangementFacade customerArrangement : productArrangements) {
            Product associatedProduct = customerArrangement.getAssociatedProduct();
            if (null != associatedProduct && null != associatedProduct.getProductType() && PRODUCT_TYPE_CC.equals(associatedProduct.getProductType())) {

                matches++;

                if (matches > Integer.valueOf(threshold).intValue()) {
                    return true;
                }
            }

        }
        return false;
    }
}

package com.lloydsbanking.salsa.eligibility.service.questions;

import com.lloydsbanking.salsa.eligibility.service.utility.ProductArrangementFacade;

public class QuestionIsNotStolenOrBankruptOrChargedOff extends AbstractProductListQuestion implements AskQuestion {

    public static final String STATUS_BANKRUPT = "B";
    public static final String STATUS_STOLEN = "Z";
    public static final String STATUS_CHARGED_OFF = "U";

    public static QuestionIsNotStolenOrBankruptOrChargedOff pose() {
        return new QuestionIsNotStolenOrBankruptOrChargedOff();
    }

    public boolean ask() {
        for (ProductArrangementFacade customerArrangement : productArrangements) {
            if (customerArrangement.isCreditCardFinanceServiceArrangement()) {
                if (customerArrangement.getCardStatus() != null) {
                    String status = customerArrangement.getCardStatus().value();
                    if (!(STATUS_BANKRUPT.equals(status) || STATUS_STOLEN.equals(status) || STATUS_CHARGED_OFF.equals(status))) {
                        return true;
                    }
                }
                else {
                    return true;
                }

            }
        }
        return false;
    }
}
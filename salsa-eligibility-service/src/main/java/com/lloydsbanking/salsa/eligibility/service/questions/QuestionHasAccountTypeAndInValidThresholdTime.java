package com.lloydsbanking.salsa.eligibility.service.questions;

import com.lloydsbanking.salsa.date.DateFactory;
import com.lloydsbanking.salsa.eligibility.service.utility.ProductArrangementFacade;

public class QuestionHasAccountTypeAndInValidThresholdTime extends AbstractProductListAccountQuestion implements AskQuestion {
    private static final String CURRENT_ACCOUNT = "CURRENT";

    private DateFactory dateFactory = new DateFactory();

    public static QuestionHasAccountTypeAndInValidThresholdTime pose() {
        return new QuestionHasAccountTypeAndInValidThresholdTime();
    }

    public boolean ask() {
        boolean accountTypeAvailable = false;
        long thresholdVal = Long.valueOf(threshold);
        for (ProductArrangementFacade productArrangement : productArrangements) {
            if (null != productArrangement.getArrangementType() && productArrangement.getArrangementType().equals(accountType)) {
                if (accountType.equals(CURRENT_ACCOUNT)) {
                    if (checkForNull(productArrangement) && differenceFromCurrentDate(productArrangement) <= thresholdVal) {
                        accountTypeAvailable = true;
                    }
                }
                else if (null != productArrangement.getStartDate() && Math.abs(dateFactory.getDifferenceFromCurrentDate(productArrangement.getStartDate().toGregorianCalendar().getTime())) <= thresholdVal) {
                    accountTypeAvailable = true;
                }
            }
        }
        return accountTypeAvailable;
    }

    private long differenceFromCurrentDate(ProductArrangementFacade productArrangement) {
        return Math.abs(dateFactory.getDifferenceFromCurrentDate(productArrangement.getOvrdrftDtls().getStartDate().toGregorianCalendar().getTime()));
    }

    private boolean checkForNull(ProductArrangementFacade productArrangement) {
        return null != productArrangement.getOvrdrftDtls() && null != productArrangement.getOvrdrftDtls().getStartDate();
    }
}

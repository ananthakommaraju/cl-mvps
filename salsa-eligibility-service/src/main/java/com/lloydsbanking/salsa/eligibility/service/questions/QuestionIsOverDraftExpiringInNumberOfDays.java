package com.lloydsbanking.salsa.eligibility.service.questions;

import com.lloydsbanking.salsa.date.DateFactory;
import com.lloydsbanking.salsa.eligibility.service.utility.EligibilityException;
import com.lloydsbanking.salsa.eligibility.service.utility.ProductArrangementFacade;

import java.util.Date;

public class QuestionIsOverDraftExpiringInNumberOfDays extends AbstractProductListAccountQuestion implements AskQuestion {

    private DateFactory dateFactory = new DateFactory();

    public static QuestionIsOverDraftExpiringInNumberOfDays pose() {
        return new QuestionIsOverDraftExpiringInNumberOfDays();
    }

    @Override
    public boolean ask() throws EligibilityException {
        for (ProductArrangementFacade customerArrangement : productArrangements) {
            String accountNumberInRequest = customerArrangement.getAccountNumber();
            String sortCodeInRequest = customerArrangement.getSortCode();
            if (accountNumber.equalsIgnoreCase(accountNumberInRequest) && sortCode.equals(sortCodeInRequest)) {
                Date endDate = getEndDate(customerArrangement);
                int daysUntilExpiring = Integer.valueOf(threshold);
                if (!(Math.abs(dateFactory.getDifferenceFromCurrentDate(endDate)) > daysUntilExpiring)) {
                    return false;

                }
            }
        }
        return true;
    }

    private Date getEndDate(ProductArrangementFacade customerArrangement) {
        if (null != customerArrangement.getOvrdrftDtls().getEndDate()) {
            return customerArrangement.getOvrdrftDtls().getEndDate().toGregorianCalendar().getTime();
        }
        return null;
    }
}

package com.lloydsbanking.salsa.eligibility.service.questions;

import com.lloydsbanking.salsa.date.DateFactory;
import com.lloydsbanking.salsa.eligibility.service.utility.EligibilityException;
import com.lloydsbanking.salsa.eligibility.service.utility.ProductArrangementFacade;

import java.util.Date;

public class QuestionIsOverDraftAppliedAlready extends AbstractProductListAccountQuestion implements AskQuestion {

    private DateFactory dateFactory = new DateFactory();

    public static QuestionIsOverDraftAppliedAlready pose() {
        return new QuestionIsOverDraftAppliedAlready();
    }

    @Override
    public boolean ask() throws EligibilityException {
        for (ProductArrangementFacade customerArrangement : productArrangements) {
            String accountNumberInRequest = customerArrangement.getAccountNumber();
            String sortCodeInRequest = customerArrangement.getSortCode();
            if (accountNumber.equalsIgnoreCase(accountNumberInRequest) && sortCode.equals(sortCodeInRequest)) {
                Date startDate = customerArrangement.getOvrdrftDtls().getStartDate().toGregorianCalendar().getTime();
                long numOfDaysSinceApplied = Long.valueOf(threshold);
                if (!(Math.abs(dateFactory.getDifferenceFromCurrentDate(startDate)) > numOfDaysSinceApplied)) {
                    return false;

                }

            }
        }
        return true;
    }
}

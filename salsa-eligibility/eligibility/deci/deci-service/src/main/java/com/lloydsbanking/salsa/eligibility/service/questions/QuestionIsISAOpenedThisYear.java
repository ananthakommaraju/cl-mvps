package com.lloydsbanking.salsa.eligibility.service.questions;

import com.lloydsbanking.salsa.date.DateFactory;
import com.lloydsbanking.salsa.eligibility.service.utility.ProductArrangementFacade;
import com.lloydsbanking.salsa.eligibility.service.utility.constants.Mnemonics;

public class QuestionIsISAOpenedThisYear extends AbstractProductListQuestion implements AskQuestion {

    private DateFactory dateFactory = new DateFactory();

    public static QuestionIsISAOpenedThisYear pose() {
        return new QuestionIsISAOpenedThisYear();
    }

    public boolean ask() {
        for (ProductArrangementFacade customerArrangement : productArrangements) {
            if (null != customerArrangement.getParentInstructionMnemonic() && Mnemonics.GROUP_ISA.equalsIgnoreCase(customerArrangement.getParentInstructionMnemonic())) {
                if (customerArrangement.getStartDate().toGregorianCalendar().getTime().after(dateFactory.getStartOfTaxYear())) {
                    return true;

                }

            }

        }
        return false;
    }
}

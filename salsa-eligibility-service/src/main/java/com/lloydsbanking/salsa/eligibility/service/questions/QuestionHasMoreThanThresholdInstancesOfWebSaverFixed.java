package com.lloydsbanking.salsa.eligibility.service.questions;

import com.lloydsbanking.salsa.eligibility.service.utility.ProductArrangementFacade;
import com.lloydsbanking.salsa.eligibility.service.utility.constants.Mnemonics;

public class QuestionHasMoreThanThresholdInstancesOfWebSaverFixed extends AbstractProductListQuestion implements AskQuestion {

    public static QuestionHasMoreThanThresholdInstancesOfWebSaverFixed pose() {
        return new QuestionHasMoreThanThresholdInstancesOfWebSaverFixed();
    }

    public boolean ask() {
        int matches = 0;

        for (ProductArrangementFacade customerArrangement : productArrangements) {
            String parentMnemonic = customerArrangement.getParentInstructionMnemonic();
            if (parentMnemonic != null && Mnemonics.WEB_SAVER_FIXED.equalsIgnoreCase(parentMnemonic)) {
                matches++;
            }
        }
        if (matches >= Integer.valueOf(threshold).intValue()) {
            return false;
        }
        return true;
    }
}

package com.lloydsbanking.salsa.eligibility.service.questions;

import com.lloydsbanking.salsa.eligibility.service.utility.ProductArrangementFacade;
import org.apache.cxf.common.util.CollectionUtils;

public class QuestionHasExceededProductThreshold extends AbstractProductListQuestion implements AskQuestion  {

    public static QuestionHasExceededProductThreshold pose() {
        return new QuestionHasExceededProductThreshold();
    }

    public boolean ask() {
        int matches = 0;
        if (hasExistingProductArrangements()) {
            for (ProductArrangementFacade productArrangement : productArrangements) {
                if (null != productArrangement.getInstructionMnemonic()) {
                    if (candidateInstruction.equals(productArrangement.getInstructionMnemonic())) {
                        matches++;
                        if (matches >= Integer.valueOf(threshold).intValue()) {
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }

    private boolean hasExistingProductArrangements() {
        if (!CollectionUtils.isEmpty(productArrangements)) {
            return true;
        }
        return false;
    }
}

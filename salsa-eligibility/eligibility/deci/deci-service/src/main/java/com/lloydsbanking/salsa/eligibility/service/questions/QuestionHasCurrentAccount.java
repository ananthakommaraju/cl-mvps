package com.lloydsbanking.salsa.eligibility.service.questions;

import com.lloydsbanking.salsa.eligibility.service.utility.ProductArrangementFacade;
import com.lloydsbanking.salsa.eligibility.service.utility.constants.Mnemonics;
import org.apache.commons.collections.CollectionUtils;

public class QuestionHasCurrentAccount extends AbstractProductListQuestion implements AskQuestion {

    public static QuestionHasCurrentAccount pose() {
        return new QuestionHasCurrentAccount();
    }

    public boolean ask() {
        if (!CollectionUtils.isEmpty(productArrangements)) {
            for (ProductArrangementFacade productArrangement : productArrangements) {
                if (null != productArrangement.getAssociatedProduct()
                        && null != productArrangement.getAssociatedProduct().getInstructionDetails()
                        && null != productArrangement.getAssociatedProduct().getInstructionDetails().getParentInstructionMnemonic()) {
                    String parInstrMnemonic = productArrangement.getAssociatedProduct().getInstructionDetails().getParentInstructionMnemonic();
                    if (Mnemonics.CURRENT_ACCOUNTS.contains(parInstrMnemonic)) {
                        return true;
                    }
                }

            }

        }
        return false;
    }
}

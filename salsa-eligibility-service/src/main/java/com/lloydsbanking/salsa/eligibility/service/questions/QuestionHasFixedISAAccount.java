package com.lloydsbanking.salsa.eligibility.service.questions;

import com.lloydsbanking.salsa.eligibility.service.utility.ProductArrangementFacade;
import com.lloydsbanking.salsa.eligibility.service.utility.constants.Mnemonics;
import org.apache.commons.collections.CollectionUtils;

public class QuestionHasFixedISAAccount extends AbstractProductListQuestion implements AskQuestion  {

    public static QuestionHasFixedISAAccount pose() {
        return new QuestionHasFixedISAAccount();
    }

    public boolean ask() {
        if (!CollectionUtils.isEmpty(productArrangements)) {
            for (ProductArrangementFacade customerArrangement : productArrangements) {
                String insMnemonic = customerArrangement.getInstructionMnemonic();
                if (null != insMnemonic && Mnemonics.FIXED_ISAS.contains(insMnemonic)) {
                    return true;
                }
            }
        }
        return false;
    }
}

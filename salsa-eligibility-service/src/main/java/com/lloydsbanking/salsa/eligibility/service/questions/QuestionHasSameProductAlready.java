package com.lloydsbanking.salsa.eligibility.service.questions;

import com.lloydsbanking.salsa.eligibility.service.utility.ProductArrangementFacade;
import com.lloydsbanking.salsa.eligibility.service.utility.constants.Mnemonics;
import org.apache.commons.collections.CollectionUtils;

public class QuestionHasSameProductAlready extends AbstractProductListQuestion implements AskQuestion {

    public static QuestionHasSameProductAlready pose() {
        return new QuestionHasSameProductAlready();
    }

    public boolean ask() {
        if (!CollectionUtils.isEmpty(productArrangements)) {
            for (ProductArrangementFacade productArrangement : productArrangements) {
                if (null != productArrangement.getInstructionMnemonic()) {
                    if (isDuplicateProductApplied(productArrangement.getInstructionMnemonic(), threshold)) {
                        return true;
                    }
                }
            }

        }
        return false;
    }


    private boolean isDuplicateProductApplied(String instructionMnemonic, String candidateInstruction) {
        if ((instructionMnemonic.equalsIgnoreCase(candidateInstruction)) || (Mnemonics.CASH_ISA_SAVER.equals(candidateInstruction) && Mnemonics.CASH_ISA.equalsIgnoreCase(instructionMnemonic))) {
            return true;
        }
        return false;
    }
}

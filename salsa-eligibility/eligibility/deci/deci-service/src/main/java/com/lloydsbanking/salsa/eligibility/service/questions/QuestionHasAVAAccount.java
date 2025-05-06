package com.lloydsbanking.salsa.eligibility.service.questions;

import com.lloydsbanking.salsa.eligibility.service.utility.ProductArrangementFacade;
import com.lloydsbanking.salsa.eligibility.service.utility.constants.Mnemonics;
import lib_sim_bo.businessobjects.InstructionDetails;

public class QuestionHasAVAAccount extends AbstractProductListQuestion implements AskQuestion {

    public static QuestionHasAVAAccount pose() {
        return new QuestionHasAVAAccount();
    }

    public boolean ask() {
        for (ProductArrangementFacade customerArrangement : productArrangements) {
            InstructionDetails instructionDetails = customerArrangement.getInstructionDetails();

            if (null != instructionDetails && null != instructionDetails.getInstructionMnemonic()) {
                if (Mnemonics.AVA_ACCOUNTS.contains(instructionDetails.getInstructionMnemonic())) {
                    return true;
                }
            }
        }
        return false;
    }

}

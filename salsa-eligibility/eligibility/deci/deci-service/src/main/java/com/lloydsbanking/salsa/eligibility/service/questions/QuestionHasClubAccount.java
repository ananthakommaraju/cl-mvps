package com.lloydsbanking.salsa.eligibility.service.questions;

import com.lloydsbanking.salsa.eligibility.service.utility.ProductArrangementFacade;
import com.lloydsbanking.salsa.eligibility.service.utility.constants.Mnemonics;
import lib_sim_bo.businessobjects.InstructionDetails;
import org.apache.commons.collections.CollectionUtils;

public class QuestionHasClubAccount extends AbstractProductListQuestion implements AskQuestion {
    public static QuestionHasClubAccount pose() {
        return new QuestionHasClubAccount();
    }

    public boolean ask() {
        if (!CollectionUtils.isEmpty(productArrangements)) {
            for (ProductArrangementFacade customerArrangement : productArrangements) {
                if (null != customerArrangement) {
                    InstructionDetails instructionDetails = customerArrangement.getInstructionDetails();
                    if (null != instructionDetails) {
                        String insMnemonic = instructionDetails.getInstructionMnemonic();
                        if (null != insMnemonic && Mnemonics.CLUB_ACCOUNTS.contains(insMnemonic)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }
}


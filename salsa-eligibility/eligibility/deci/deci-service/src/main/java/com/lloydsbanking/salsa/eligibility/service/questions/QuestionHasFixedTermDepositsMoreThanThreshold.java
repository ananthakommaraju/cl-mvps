package com.lloydsbanking.salsa.eligibility.service.questions;

import com.lloydsbanking.salsa.eligibility.service.utility.ProductArrangementFacade;
import com.lloydsbanking.salsa.eligibility.service.utility.constants.Mnemonics;
import lib_sim_bo.businessobjects.InstructionDetails;
import org.apache.commons.collections.CollectionUtils;

public class QuestionHasFixedTermDepositsMoreThanThreshold extends AbstractProductListQuestion implements AskQuestion  {

    public static QuestionHasFixedTermDepositsMoreThanThreshold pose() {
        return new QuestionHasFixedTermDepositsMoreThanThreshold();
    }

    public boolean ask() {
        if (!CollectionUtils.isEmpty(productArrangements)) {
            int count = 0;
            int thresholdValue = Integer.valueOf(threshold).intValue();
            for (ProductArrangementFacade productArrangement : productArrangements) {
                InstructionDetails instructionDetails = productArrangement.getInstructionDetails();
                if (null != instructionDetails && null != instructionDetails.getParentInstructionMnemonic()) {
                    String parentInstruction = instructionDetails.getParentInstructionMnemonic();
                    if (Mnemonics.WEB_SAVER_FIXED.equalsIgnoreCase(candidateInstruction) || Mnemonics.FIXED_SAVER.equalsIgnoreCase(candidateInstruction)) {
                        if (parentInstruction.equalsIgnoreCase(candidateInstruction)) {
                            count++;
                            if (count >= thresholdValue) {
                                return false;
                            }
                        }
                    }
                }
            }
        }
        return true;
    }
}

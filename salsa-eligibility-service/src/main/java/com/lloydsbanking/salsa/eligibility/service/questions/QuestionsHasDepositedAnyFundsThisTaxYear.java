package com.lloydsbanking.salsa.eligibility.service.questions;


import com.lloydsbanking.salsa.eligibility.service.utility.ProductArrangementFacade;
import com.lloydsbanking.salsa.eligibility.service.utility.constants.Mnemonics;
import org.apache.commons.collections.CollectionUtils;

public class QuestionsHasDepositedAnyFundsThisTaxYear extends AbstractProductListQuestion implements AskQuestion {

    private static final int ISA_FUNDED_COMPARATOR = 0;

    public static QuestionsHasDepositedAnyFundsThisTaxYear pose() {
        return new QuestionsHasDepositedAnyFundsThisTaxYear();
    }

    public boolean ask() {

        if (!CollectionUtils.isEmpty(productArrangements)) {
            for (ProductArrangementFacade productArrangement : productArrangements) {
                if (productArrangement.isDepositArrangement()) {
                    if (isParentInstructionExist(productArrangement)) {
                        if (isISAfundedThisTaxYear(productArrangement, productArrangement.getParentInstructionMnemonic())) {
                            return false;
                        }
                    }

                }

            }

        }
        return true;
    }

    private boolean isParentInstructionExist(ProductArrangementFacade depositArrangement) {
        return null != depositArrangement && null != depositArrangement.getAssociatedProduct() && null != depositArrangement.getAssociatedProduct()
                .getInstructionDetails() && null != depositArrangement.getAssociatedProduct().getInstructionDetails().getParentInstructionMnemonic();
    }

    private boolean isISAfundedThisTaxYear(ProductArrangementFacade depositArrangement, String parentMnemonic) {
        if (null != parentMnemonic && parentMnemonic.contains(Mnemonics.ISA)) {
            if (null != depositArrangement.getISABalance() && null != depositArrangement.getISABalance().getMaximumLimitAmount()
                && null != depositArrangement.getISABalance().getMaximumLimitAmount().getAmount()) {
                if (depositArrangement.getISABalance().getMaximumLimitAmount().getAmount().intValue() > ISA_FUNDED_COMPARATOR) {
                    return true;
                }
            }

        }
        return false;
    }
}

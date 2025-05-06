package com.lloydsbanking.salsa.eligibility.service.questions;

import com.lloydsbanking.salsa.eligibility.service.utility.ProductArrangementFacade;
import com.lloydsbanking.salsa.eligibility.service.utility.constants.Mnemonics;
import org.apache.commons.collections.CollectionUtils;

public class QuestionIsCustomerHoldingVantageProduct extends AbstractProductListQuestion implements AskQuestion {

    public static QuestionIsCustomerHoldingVantageProduct pose() {
        return new QuestionIsCustomerHoldingVantageProduct();
    }

    public boolean ask() {
        int maxNumOfVantage = Integer.valueOf(threshold).intValue();
        int count = 0;
        if (!CollectionUtils.isEmpty(productArrangements)) {
            for (ProductArrangementFacade productArrangement : productArrangements) {
                String parentInsMnemonic = null;
                if (null != productArrangement.getAssociatedProduct() && null != productArrangement.getAssociatedProduct()
                        .getInstructionDetails() && null != productArrangement.getAssociatedProduct().getInstructionDetails().getParentInstructionMnemonic()) {
                    parentInsMnemonic = productArrangement.getAssociatedProduct().getInstructionDetails().getParentInstructionMnemonic();
                    if (null != parentInsMnemonic && Mnemonics.VANTAGE_HOLDING.contains(parentInsMnemonic)) {
                        count++;
                    }
                    if (count >= maxNumOfVantage) {
                        return false;
                    }
                }
            }
        }
        return true;
    }
}

package com.lloydsbanking.salsa.eligibility.service.questions;

import com.lloydsbanking.salsa.eligibility.service.utility.ProductArrangementFacade;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

public class QuestionHasExceededThresholdForMnemonicsGroup extends AbstractProductListQuestion implements AskQuestion {

    private String productGroupMnemonic;

    private String thresholdCount;

    public static QuestionHasExceededThresholdForMnemonicsGroup pose() {
        return new QuestionHasExceededThresholdForMnemonicsGroup();
    }

    public boolean ask() {
        if (!CollectionUtils.isEmpty(productArrangements)) {
            int count = 0;
            int maxNumOfProducts = 0;
            if (thresholdCount != null) {
                maxNumOfProducts = Integer.valueOf(thresholdCount).intValue();
            }
            for (ProductArrangementFacade productArrangement : productArrangements) {
                String insMnemonic = productArrangement.getInstructionMnemonic();
                if (!StringUtils.isEmpty(insMnemonic) && isMnemonicPresent(insMnemonic, productGroupMnemonic)) {
                    count++;
                    if (count >= maxNumOfProducts) {
                        return true;
                    }
                }
            }

        }
        return false;
    }


    private boolean isMnemonicPresent(String insMnemonic, String thresholdMnemonic) {
        String[] indicators = thresholdMnemonic.split(":");
        for(String indicator : indicators) {
            if (insMnemonic.equals(indicator)) {
                return true;
            }
        }
        return false;
    }

    public QuestionHasExceededThresholdForMnemonicsGroup givenAGroupOfProductMnemonic(String productGroupMnemonic) {
        this.productGroupMnemonic = productGroupMnemonic;
        return this;
    }

    public QuestionHasExceededThresholdForMnemonicsGroup givenThresholdCount(String thresholdCount) {
        this.thresholdCount = thresholdCount;
        return this;
    }

}

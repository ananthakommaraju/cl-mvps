package com.lloydsbanking.salsa.eligibility.service.questions;

import org.apache.commons.collections.CollectionUtils;

public class QuestionIsProductArrangementsExist extends AbstractProductListQuestion implements AskQuestion {

    public static QuestionIsProductArrangementsExist pose() {
        return new QuestionIsProductArrangementsExist();
    }

    public boolean ask() {
        if (!CollectionUtils.isEmpty(productArrangements)) {
            return true;
        }
        return false;
    }
}

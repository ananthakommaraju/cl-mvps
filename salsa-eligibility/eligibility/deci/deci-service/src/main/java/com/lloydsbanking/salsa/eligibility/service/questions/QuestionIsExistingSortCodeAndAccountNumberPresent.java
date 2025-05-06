package com.lloydsbanking.salsa.eligibility.service.questions;

import org.apache.cxf.common.util.StringUtils;

public class QuestionIsExistingSortCodeAndAccountNumberPresent extends AbstractCustomerQuestion {
    public static QuestionIsExistingSortCodeAndAccountNumberPresent pose() {
        return new QuestionIsExistingSortCodeAndAccountNumberPresent();
    }

    @Override
    public boolean ask() {
        return !StringUtils.isEmpty(existingAccountNumber) && !StringUtils.isEmpty(existingSortCode);
    }
}

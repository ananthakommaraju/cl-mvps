package com.lloydsbanking.salsa.eligibility.service.questions;

import com.lloydsbanking.salsa.eligibility.service.utility.ProductArrangementFacade;
import org.apache.commons.lang.StringUtils;

public class QuestionIsCustomerAccountEffective extends AbstractProductListQuestion implements AskQuestion {
    public static QuestionIsCustomerAccountEffective pose() {
        return new QuestionIsCustomerAccountEffective();
    }

    private boolean isWzRequest = false;

    public boolean ask() {
        boolean lifecycleStatus = false;
        boolean isStatusNull = true;
        for (ProductArrangementFacade customerArrangement : productArrangements) {
            if (!StringUtils.isEmpty(customerArrangement.getLifecycleStatus())) {
                String status = customerArrangement.getLifecycleStatus();
                if (null != status) {
                    isStatusNull = false;
                    if (!status.equalsIgnoreCase(threshold)) {
                        return true;
                    }
                }
            }
        }
        if (isWzRequest && isStatusNull) {
            return true;
        }
        return lifecycleStatus;
    }

    public QuestionIsCustomerAccountEffective givenIsWzRequest(boolean isWzRequest) {
        this.isWzRequest = isWzRequest;
        return this;
    }
}

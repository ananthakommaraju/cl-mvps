package com.lloydsbanking.salsa.eligibility.service.questions;

import com.lloydsbanking.salsa.eligibility.service.downstream.CheckBalanceRetriever;
import com.lloydsbanking.salsa.eligibility.service.utility.EligibilityException;
import com.lloydsbanking.salsa.eligibility.service.utility.exceptions.SalsaExternalServiceException;
import com.lloydsbanking.salsa.eligibility.service.utility.exceptions.SalsaInternalResourceNotAvailableException;
import lib_sim_bo.businessobjects.ProductArrangementIndicator;
import org.apache.commons.collections.CollectionUtils;

import java.util.List;

public class QuestionHasCBSIndicator extends AbstractProductListAccountQuestion implements AskQuestion {
    protected CheckBalanceRetriever checkBalanceRetriever;

    private static final boolean IS_WZ_REQUEST = true;

    public static QuestionHasCBSIndicator pose() {
        return new QuestionHasCBSIndicator();
    }

    public boolean ask() throws EligibilityException {
        if (!CollectionUtils.isEmpty(productArrangements)) {
            List<ProductArrangementIndicator> indicatorList;
            try {
                indicatorList = checkBalanceRetriever.getCBSIndicators(requestHeader, sortCode, accountNumber,
                        appGroupRetriever.callRetrieveCBSAppGroup(requestHeader, sortCode, IS_WZ_REQUEST));
            } catch (SalsaInternalResourceNotAvailableException | SalsaExternalServiceException e) {
                throw new EligibilityException(e);
            }
            if (!CollectionUtils.isEmpty(indicatorList)) {
                for (ProductArrangementIndicator productArrangementIndicator : indicatorList) {
                    int code = productArrangementIndicator.getCode();
                    String eventStr = Integer.toString(code);
                    if (eventStr.equalsIgnoreCase(threshold)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    public QuestionHasCBSIndicator givenCheckBalanceRetrieverClientInstance(CheckBalanceRetriever checkBalanceRetriever) {
        this.checkBalanceRetriever = checkBalanceRetriever;
        return this;
    }

}

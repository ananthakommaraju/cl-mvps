package com.lloydsbanking.salsa.eligibility.service.questions;

import com.lloydsbanking.salsa.eligibility.service.utility.EligibilityException;
import com.lloydsbanking.salsa.eligibility.service.utility.ProductArrangementFacade;
import com.lloydsbanking.salsa.eligibility.service.utility.exceptions.SalsaInternalResourceNotAvailableException;
import com.lloydsbanking.salsa.eligibility.service.utility.exceptions.SalsaInternalServiceException;
import com.lloydsbanking.salsa.soap.cbs.e184.objects.StandardIndicators1Gp;

import java.util.List;

public class QuestionIsCbs8IndicatorNotSet extends AbstractProductListQuestion implements AskQuestion {
    private static final String CURRENT_ACCOUNT = "CURRENT";

    private static final boolean IS_WZ_REQUEST = false;

    public static QuestionIsCbs8IndicatorNotSet pose() {
        return new QuestionIsCbs8IndicatorNotSet();
    }

    public boolean ask() throws EligibilityException {
        for (ProductArrangementFacade productArrangement : productArrangements) {
            if (CURRENT_ACCOUNT.equals(productArrangement.getArrangementType()) && !productArrangement.isCapAccountRestricted()) {
                String sortCode = productArrangement.getSortCode();
                String accountNumber = productArrangement.getAccountNumber();
                String cbsAppGroup = appGroupRetriever.callRetrieveCBSAppGroup(requestHeader, sortCode, IS_WZ_REQUEST);
                List<StandardIndicators1Gp> standardIndicators1Gps;
                try {
                    standardIndicators1Gps = cbsIndicatorRetriever.getCbsIndicator(requestHeader, sortCode, accountNumber, cbsAppGroup);
                }
                catch (SalsaInternalServiceException | SalsaInternalResourceNotAvailableException e) {
                    throw new EligibilityException(e);
                }
                if (check8IndicatorNotSet(standardIndicators1Gps, threshold)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean check8IndicatorNotSet(List<StandardIndicators1Gp> indicators, String cbsIndicator8) {
        for (StandardIndicators1Gp standardIndicators1Gp : indicators) {
            if (Long.valueOf(cbsIndicator8).longValue() == Long.valueOf(String.valueOf(standardIndicators1Gp.getIndicator1Cd())).longValue()) {
                return false;
            }
        }
        return true;
    }
}

package com.lloydsbanking.salsa.eligibility.service.questions;

import com.lloydsbanking.salsa.eligibility.service.utility.EligibilityException;
import com.lloydsbanking.salsa.eligibility.service.utility.ProductArrangementFacade;
import com.lloydsbanking.salsa.eligibility.service.utility.exceptions.SalsaInternalResourceNotAvailableException;
import com.lloydsbanking.salsa.eligibility.service.utility.exceptions.SalsaInternalServiceException;
import com.lloydsbanking.salsa.soap.cbs.e184.objects.StandardIndicators1Gp;

import java.util.List;

public class QuestionIsCbs646IndicatorNotSet extends AbstractProductListQuestion implements AskQuestion {
    private static final String CURRENT_ACCOUNT = "CURRENT";

    private static final int INDICATOR_PREFIX_LENGTH = 3;

    private static final boolean IS_WZ_REQUEST = false;

    public static QuestionIsCbs646IndicatorNotSet pose() {
        return new QuestionIsCbs646IndicatorNotSet();
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

                if (check646IndicatorNotSet(standardIndicators1Gps, threshold)) {
                    return true;
                }

            }
        }
        return false;
    }

    private boolean check646IndicatorNotSet(List<StandardIndicators1Gp> indicators, String cbsIndicator646) {
        for (StandardIndicators1Gp standardIndicators1Gp : indicators) {

            String indicator = String.valueOf(standardIndicators1Gp.getIndicator1Cd());
            if (indicator.length() >= INDICATOR_PREFIX_LENGTH) {
                indicator = indicator.substring(indicator.length() - INDICATOR_PREFIX_LENGTH, indicator.length());
            }
            if (cbsIndicator646.equalsIgnoreCase(indicator)) {
                return false;
            }
        }
        return true;

    }
}

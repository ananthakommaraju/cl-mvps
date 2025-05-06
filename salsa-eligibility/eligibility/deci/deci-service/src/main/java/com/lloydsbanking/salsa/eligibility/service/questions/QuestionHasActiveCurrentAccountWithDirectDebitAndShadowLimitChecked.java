package com.lloydsbanking.salsa.eligibility.service.questions;

import com.lloydsbanking.salsa.eligibility.service.downstream.ShadowLimitRetriever;
import com.lloydsbanking.salsa.eligibility.service.utility.EligibilityException;
import com.lloydsbanking.salsa.eligibility.service.utility.ProductArrangementFacade;
import com.lloydsbanking.salsa.eligibility.service.utility.exceptions.SalsaInternalResourceNotAvailableException;
import com.lloydsbanking.salsa.eligibility.service.utility.exceptions.SalsaInternalServiceException;

import java.util.List;

public class QuestionHasActiveCurrentAccountWithDirectDebitAndShadowLimitChecked extends AbstractProductListQuestion implements AskQuestion {
    private static final String CURRENT_ACCOUNT = "CURRENT";

    private static final String DORMANT = "Dormant";

    protected ShadowLimitRetriever shadowLimitRetriever;

    private static final boolean IS_WZ_REQUEST = false;

    public static QuestionHasActiveCurrentAccountWithDirectDebitAndShadowLimitChecked pose() {
        return new QuestionHasActiveCurrentAccountWithDirectDebitAndShadowLimitChecked();
    }

    public boolean ask() throws EligibilityException {
        String shadowLimit = null;
        boolean shadowLimitCheck;

        for (ProductArrangementFacade productArrangement : productArrangements) {
            String[] thresholdParams = threshold.split(":");
            if (null != getSortCode(productArrangements) && null != getCustomerId(productArrangements)) {
                String cbsAppGroup = appGroupRetriever.callRetrieveCBSAppGroup(requestHeader, getCustomerId(productArrangements), IS_WZ_REQUEST);
                try {
                    shadowLimit = shadowLimitRetriever.getShadowLimit(requestHeader, getSortCode(productArrangements), getCustomerId(productArrangements), cbsAppGroup);
                }
                catch (SalsaInternalServiceException | SalsaInternalResourceNotAvailableException e) {
                    throw new EligibilityException(e);
                }
            }
            shadowLimitCheck = isShadowLimitGreaterThanThreshold(shadowLimit, thresholdParams[0]);

            if ((shadowLimitCheck && isRelatedEventPresent(productArrangement, thresholdParams[1]))) {
                return true;

            }
        }
        return false;
    }

    private boolean isShadowLimitGreaterThanThreshold(final String shadowLimit, final String thresholdParams) {
        if (null != shadowLimit && shadowLimit.length() > 1) {
            return ((Float.valueOf(shadowLimit.substring(1))) > Integer.parseInt(thresholdParams));
        }
        return false;
    }

    private String getCustomerId(List<ProductArrangementFacade> customerArrangements) {
        for (ProductArrangementFacade productArrangement : customerArrangements) {

            if (CURRENT_ACCOUNT.equalsIgnoreCase(productArrangement.getArrangementType()) && !productArrangement.isCapAccountRestricted()) {

                return productArrangement.getParticipantCusomters().get(0).getPartyId();
            }
        }
        //to be null if above conditions not met
        return null;
    }

    private String getSortCode(List<ProductArrangementFacade> customerArrangements) {

        for (ProductArrangementFacade productArrangement : customerArrangements) {
            if (CURRENT_ACCOUNT.equalsIgnoreCase(productArrangement.getArrangementType()) && !productArrangement.isCapAccountRestricted()) {
                return productArrangement.getSortCode();

            }
        }
        return null;
    }

    private boolean isRelatedEventPresent(ProductArrangementFacade productArrangement, String threshold) {

        List relatedEvents = productArrangement.getRelatedEvents();
        String status = String.valueOf(productArrangement.getCardStatus());

        if (relatedEvents.contains(threshold) && !status.equalsIgnoreCase(DORMANT)) {
            return true;
        }
        return false;
    }

    public AbstractProductListQuestion givenShadowLimitRetrieverClientInstance(ShadowLimitRetriever shadowLimitRetriever) {
        this.shadowLimitRetriever = shadowLimitRetriever;
        return this;
    }

}

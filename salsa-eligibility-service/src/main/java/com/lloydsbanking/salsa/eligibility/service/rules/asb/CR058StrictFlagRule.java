package com.lloydsbanking.salsa.eligibility.service.rules.asb;

import com.lloydsbanking.salsa.eligibility.service.utility.EligibilityException;
import com.lloydsbanking.salsa.eligibility.service.downstream.AppGroupRetriever;
import com.lloydsbanking.salsa.eligibility.service.downstream.ShadowLimitRetriever;
import com.lloydsbanking.salsa.eligibility.service.rules.common.EligibilityDecision;
import com.lloydsbanking.salsa.eligibility.service.rules.common.RuleDataHolder;
import com.lloydsbanking.salsa.eligibility.service.utility.constants.DeclineReasons;
import com.lloydsbanking.salsa.eligibility.service.utility.exceptions.SalsaInternalResourceNotAvailableException;
import com.lloydsbanking.salsa.eligibility.service.utility.exceptions.SalsaInternalServiceException;
import lb_gbo_sales.messages.RequestHeader;
import org.springframework.beans.factory.annotation.Autowired;

public class CR058StrictFlagRule implements ASBEligibilityRule {

    @Autowired
    ShadowLimitRetriever shadowLimitRetriever;

    @Autowired
    AppGroupRetriever appGroupRetriever;

    private static final boolean IS_WZ_REQUEST = false;

    @Override
    public EligibilityDecision evaluate(RuleDataHolder ruleDataHolder, String selectedBusinessId, String sortCode, String custId) throws EligibilityException {
        RequestHeader header = ruleDataHolder.getHeader();

        int strictFlag = 0;
        try {
            strictFlag = shadowLimitRetriever.getStrictFlag(header, sortCode, custId, appGroupRetriever.callRetrieveCBSAppGroup(header, sortCode, IS_WZ_REQUEST));
        } catch (SalsaInternalServiceException | SalsaInternalResourceNotAvailableException e) {
            throw new EligibilityException(e);
        }

        if (ruleDataHolder.getRuleParamValue().equals(String.valueOf(strictFlag))) {
            return new EligibilityDecision(DeclineReasons.CR058_DECLINE_REASON);

        }
        return new EligibilityDecision(true);

    }
}

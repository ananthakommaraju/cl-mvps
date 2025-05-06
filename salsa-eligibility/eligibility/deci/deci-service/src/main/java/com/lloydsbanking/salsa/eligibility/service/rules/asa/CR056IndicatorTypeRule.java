package com.lloydsbanking.salsa.eligibility.service.rules.asa;

import com.lloydsbanking.salsa.eligibility.service.utility.EligibilityException;
import com.lloydsbanking.salsa.eligibility.service.downstream.AppGroupRetriever;
import com.lloydsbanking.salsa.eligibility.service.downstream.CBSIndicatorRetriever;
import com.lloydsbanking.salsa.eligibility.service.rules.common.EligibilityDecision;
import com.lloydsbanking.salsa.eligibility.service.rules.common.RuleDataHolder;
import com.lloydsbanking.salsa.eligibility.service.utility.constants.DeclineReasons;
import com.lloydsbanking.salsa.eligibility.service.utility.exceptions.SalsaInternalResourceNotAvailableException;
import com.lloydsbanking.salsa.eligibility.service.utility.exceptions.SalsaInternalServiceException;
import com.lloydsbanking.salsa.soap.cbs.e184.objects.StandardIndicators1Gp;
import lb_gbo_sales.messages.RequestHeader;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class CR056IndicatorTypeRule implements ASAEligibilityRule {


    @Autowired
    CBSIndicatorRetriever cbsIndicatorRetriever;

    @Autowired
    AppGroupRetriever appGroupRetriever;

    private static final boolean IS_WZ_REQUEST = false;

    @Override
    public EligibilityDecision evaluate(RuleDataHolder ruleDataHolder) throws EligibilityException {


        RequestHeader header = ruleDataHolder.getHeader();
        String sortCode = ruleDataHolder.getArrangementIdentifier().getSortCode();
        String accNum = ruleDataHolder.getArrangementIdentifier().getAccNum();

        String cbsAppGroup = appGroupRetriever.callRetrieveCBSAppGroup(header, sortCode, IS_WZ_REQUEST);

        List<StandardIndicators1Gp> standardIndicators1Gps;
        try {
            standardIndicators1Gps = cbsIndicatorRetriever.getCbsIndicator(header, sortCode, accNum, cbsAppGroup);
        } catch (SalsaInternalServiceException | SalsaInternalResourceNotAvailableException e) {
            throw new EligibilityException(e);
        }

        if (check646IndicatorNotSet(standardIndicators1Gps, ruleDataHolder.getRuleParamValue())) {
            return new EligibilityDecision(true);
        }
        else {
            return new EligibilityDecision(DeclineReasons.CR056_DECLINE_REASON_1 + ruleDataHolder.getRuleParamValue() + DeclineReasons.CR056_DECLINE_REASON_2);

        }
    }

    private boolean check646IndicatorNotSet(List<StandardIndicators1Gp> indicators, String cbsIndicator646) {
        for (StandardIndicators1Gp standardIndicators1Gp : indicators) {

            if (cbsIndicator646.equalsIgnoreCase(String.valueOf(standardIndicators1Gp.getIndicator1Cd()))) {
                return false;
            }
        }
        return true;

    }
}



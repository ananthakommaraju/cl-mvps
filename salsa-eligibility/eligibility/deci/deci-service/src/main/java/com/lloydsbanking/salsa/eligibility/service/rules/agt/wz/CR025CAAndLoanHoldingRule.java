package com.lloydsbanking.salsa.eligibility.service.rules.agt.wz;

import com.lloydsbanking.salsa.eligibility.service.utility.EligibilityException;
import com.lloydsbanking.salsa.eligibility.service.rules.agt.AGTEligibilityRule;
import com.lloydsbanking.salsa.eligibility.service.rules.common.EligibilityDecision;
import com.lloydsbanking.salsa.eligibility.service.rules.common.RuleDataHolder;


public class CR025CAAndLoanHoldingRule implements AGTEligibilityRule {


    @Override
    public EligibilityDecision evaluate(RuleDataHolder ruleDataHolder) throws EligibilityException {
        //logic for identifying the customer has current account and existing loan as implemented in BZ DECI rule CR025.
        // But in WZ WPS DECI this is always returning true so implementing this way,BZ WPS implementation is already done in the another class.
        return new EligibilityDecision(true);

    }
}

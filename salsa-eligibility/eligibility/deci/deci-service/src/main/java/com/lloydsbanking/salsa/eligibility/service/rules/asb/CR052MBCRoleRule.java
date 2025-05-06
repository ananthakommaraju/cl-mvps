package com.lloydsbanking.salsa.eligibility.service.rules.asb;


import com.lloydsbanking.salsa.eligibility.service.utility.EligibilityException;
import com.lloydsbanking.salsa.eligibility.service.rules.common.EligibilityDecision;
import com.lloydsbanking.salsa.eligibility.service.rules.common.RuleDataHolder;
import com.lloydsbanking.salsa.eligibility.service.utility.constants.DeclineReasons;

public class CR052MBCRoleRule implements ASBEligibilityRule {

    @Override
    public EligibilityDecision evaluate(RuleDataHolder ruleDataHolder, String selectedBusinessId, String sortCode, String custId) throws EligibilityException {
        if (ruleDataHolder.getBusinessArrangement().hasMBCRole(selectedBusinessId, ruleDataHolder.getRuleParamValue())) {
            return new EligibilityDecision(DeclineReasons.CR052_DECLINE_REASON);
        }
        return new EligibilityDecision(true);
    }

}


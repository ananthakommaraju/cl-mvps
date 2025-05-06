package com.lloydsbanking.salsa.eligibility.service.rules.asb;

import com.lloydsbanking.salsa.eligibility.service.utility.EligibilityException;
import com.lloydsbanking.salsa.eligibility.service.rules.common.EligibilityDecision;
import com.lloydsbanking.salsa.eligibility.service.rules.common.RuleDataHolder;
import com.lloydsbanking.salsa.eligibility.service.utility.constants.DeclineReasons;

import java.util.Arrays;
import java.util.List;

public class CR051ValidBusinessEntityRule implements ASBEligibilityRule {

    @Override
    public EligibilityDecision evaluate(RuleDataHolder ruleDataHolder, String selectedBusinessId, String sortCode, String custId) throws EligibilityException {

        String ruleParamValue = ruleDataHolder.getRuleParamValue();
        String entityTypes[] = ruleParamValue.split(":");
        List entityTypeList = Arrays.asList(entityTypes);

        String entityTypeInBusinessArrangement = ruleDataHolder.getBusinessArrangement().getEntityTypes(selectedBusinessId);

        if (!entityTypeList.contains(entityTypeInBusinessArrangement)) {
            return new EligibilityDecision(DeclineReasons.CR051_DECLINE_REASON_1 + entityTypeInBusinessArrangement + DeclineReasons.CR051_DECLINE_REASON_2 + ruleParamValue);

        }
        return new EligibilityDecision(true);
    }
}



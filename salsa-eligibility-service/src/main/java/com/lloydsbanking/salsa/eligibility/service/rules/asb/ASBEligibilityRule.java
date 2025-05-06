package com.lloydsbanking.salsa.eligibility.service.rules.asb;

import com.lloydsbanking.salsa.eligibility.service.utility.EligibilityException;
import com.lloydsbanking.salsa.eligibility.service.rules.common.EligibilityDecision;
import com.lloydsbanking.salsa.eligibility.service.rules.common.RuleDataHolder;

public interface ASBEligibilityRule {
    EligibilityDecision evaluate(RuleDataHolder ruleDataHolder, String selectedBusinessId, String sortCode, String custId) throws EligibilityException;
}

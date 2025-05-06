package com.lloydsbanking.salsa.eligibility.service.rules.asa;

import com.lloydsbanking.salsa.eligibility.service.utility.EligibilityException;
import com.lloydsbanking.salsa.eligibility.service.rules.common.EligibilityDecision;
import com.lloydsbanking.salsa.eligibility.service.rules.common.RuleDataHolder;

public interface ASAEligibilityRule {
    EligibilityDecision evaluate(RuleDataHolder ruleDataHolder) throws EligibilityException;
}

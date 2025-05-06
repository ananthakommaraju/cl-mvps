package com.lloydsbanking.salsa.eligibility.service.rules.avn;

import com.lloydsbanking.salsa.eligibility.service.utility.EligibilityException;
import com.lloydsbanking.salsa.eligibility.service.rules.common.EligibilityDecision;
import com.lloydsbanking.salsa.eligibility.service.rules.common.RuleDataHolder;

public interface AVNEligibilityRule {
    EligibilityDecision evaluate(RuleDataHolder ruleDataHolder, String candidateInstruction) throws EligibilityException;
}

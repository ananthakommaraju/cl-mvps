package com.lloydsbanking.salsa.eligibility.service.rules.avn;


import com.lloydsbanking.salsa.eligibility.service.utility.EligibilityException;
import com.lloydsbanking.salsa.eligibility.service.questions.QuestionHasMoreThanThresholdInstancesOfWebSaverFixed;
import com.lloydsbanking.salsa.eligibility.service.rules.common.EligibilityDecision;
import com.lloydsbanking.salsa.eligibility.service.rules.common.RuleDataHolder;
import com.lloydsbanking.salsa.eligibility.service.utility.constants.DeclineReasons;

public class CR028FRTDMaxProductHoldingRule implements AVNEligibilityRule {

    @Override
    public EligibilityDecision evaluate(RuleDataHolder ruleDataHolder, String candidateInstruction) throws EligibilityException {

        String threshold = ruleDataHolder.getRuleParamValue();
        if (QuestionHasMoreThanThresholdInstancesOfWebSaverFixed.pose().givenAProductList(ruleDataHolder.getProductArrangements()).givenAValue(threshold).ask()) {
            return new EligibilityDecision(true);
        }
        else {
            return new EligibilityDecision(DeclineReasons.CR028_DECLINE_REASON);
        }

    }

}

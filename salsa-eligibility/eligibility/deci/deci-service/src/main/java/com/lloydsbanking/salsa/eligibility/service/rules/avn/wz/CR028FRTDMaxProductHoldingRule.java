package com.lloydsbanking.salsa.eligibility.service.rules.avn.wz;

import com.lloydsbanking.salsa.eligibility.service.utility.EligibilityException;
import com.lloydsbanking.salsa.eligibility.service.questions.QuestionHasFixedTermDepositsMoreThanThreshold;
import com.lloydsbanking.salsa.eligibility.service.rules.avn.AVNEligibilityRule;
import com.lloydsbanking.salsa.eligibility.service.rules.common.EligibilityDecision;
import com.lloydsbanking.salsa.eligibility.service.rules.common.RuleDataHolder;
import com.lloydsbanking.salsa.eligibility.service.utility.constants.DeclineReasons;

public class CR028FRTDMaxProductHoldingRule implements AVNEligibilityRule {
    @Override
    public EligibilityDecision evaluate(RuleDataHolder ruleDataHolder, String candidateInstruction) throws EligibilityException {

        String threshold = ruleDataHolder.getRuleParamValue();
        String insMnemonic= ruleDataHolder.getRuleInsMnemonic();

        if ( QuestionHasFixedTermDepositsMoreThanThreshold.pose()
                .givenAProductList(ruleDataHolder.getProductArrangements())
                .givenAValue(threshold)
                .givenAnInstruction(insMnemonic)
                .ask()) {
            return new EligibilityDecision(true);
        }
        else {
            return new EligibilityDecision(DeclineReasons.CR028_DECLINE_REASON);
        }

    }
}

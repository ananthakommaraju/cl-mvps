package com.lloydsbanking.salsa.eligibility.service.rules.aga;

import com.lloydsbanking.salsa.eligibility.service.utility.EligibilityException;
import com.lloydsbanking.salsa.eligibility.service.questions.QuestionHasSameProductAlready;
import com.lloydsbanking.salsa.eligibility.service.rules.common.EligibilityDecision;
import com.lloydsbanking.salsa.eligibility.service.rules.common.RuleDataHolder;
import com.lloydsbanking.salsa.eligibility.service.utility.constants.DeclineReasons;

public class CR006ExistingProductRule implements AGAEligibilityRule {


    @Override
    public EligibilityDecision evaluate(String candidateInstruction, RuleDataHolder ruleDataHolder) throws EligibilityException {
        if (QuestionHasSameProductAlready.pose()
                .givenAProductList(ruleDataHolder.getProductArrangements())
                .givenAValue(candidateInstruction)
                .ask()) {
            return new EligibilityDecision(DeclineReasons.CR006_DECLINE_REASON);
        }
        else {
            return new EligibilityDecision(true);
        }

    }

}

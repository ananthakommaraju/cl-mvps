package com.lloydsbanking.salsa.eligibility.service.rules.aga.wz;

import com.lloydsbanking.salsa.eligibility.service.utility.EligibilityException;
import com.lloydsbanking.salsa.eligibility.service.questions.QuestionHasCreditCardStartDatePriorToThreshold;
import com.lloydsbanking.salsa.eligibility.service.rules.aga.AGAEligibilityRule;
import com.lloydsbanking.salsa.eligibility.service.rules.common.EligibilityDecision;
import com.lloydsbanking.salsa.eligibility.service.rules.common.RuleDataHolder;

public class CR064CreditCardStartDateRule implements AGAEligibilityRule {

    @Override
    public EligibilityDecision evaluate(String candidateInstruction, RuleDataHolder ruleDataHolder) throws EligibilityException {
        String threshold = ruleDataHolder.getRuleParamValue();

        if ( QuestionHasCreditCardStartDatePriorToThreshold.pose()
                .givenAProductList(ruleDataHolder.getProductArrangements())
                .givenAValue(threshold)
                .ask()) {
            return new EligibilityDecision("Customer holds a credit card opened in last " + threshold + " days");
        }
        return new EligibilityDecision(true);
    }
}

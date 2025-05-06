package com.lloydsbanking.salsa.eligibility.service.rules.aga;

import com.lloydsbanking.salsa.eligibility.service.utility.EligibilityException;
import com.lloydsbanking.salsa.eligibility.service.questions.QuestionHasAccountTypeAndInValidThresholdTime;
import com.lloydsbanking.salsa.eligibility.service.rules.common.EligibilityDecision;
import com.lloydsbanking.salsa.eligibility.service.rules.common.RuleDataHolder;

public class CR055AccountOpeningDateRule implements AGAEligibilityRule {

    @Override
    public EligibilityDecision evaluate(String candidateInstruction, RuleDataHolder ruleDataHolder) throws EligibilityException {
        if (QuestionHasAccountTypeAndInValidThresholdTime.pose()
                .givenAProductList(ruleDataHolder.getProductArrangements())
                .givenAValue(ruleDataHolder.getRuleParamValue())
                .givenAnAccountType("CURRENT")
                .ask()) {
            return new EligibilityDecision("Customer holds a current account having overdraft opened in last " + ruleDataHolder.getRuleParamValue() + " days");
        }
        return new EligibilityDecision(true);

    }
}
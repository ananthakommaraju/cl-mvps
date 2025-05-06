package com.lloydsbanking.salsa.eligibility.service.rules.agt;


import com.lloydsbanking.salsa.eligibility.service.utility.EligibilityException;
import com.lloydsbanking.salsa.eligibility.service.questions.QuestionHasEligibleCreditCardForBalanceTransfer;
import com.lloydsbanking.salsa.eligibility.service.rules.common.EligibilityDecision;
import com.lloydsbanking.salsa.eligibility.service.rules.common.RuleDataHolder;
import com.lloydsbanking.salsa.eligibility.service.utility.constants.DeclineReasons;

public class CR011ProductGroupRule implements AGTEligibilityRule {

    @Override
    public EligibilityDecision evaluate(RuleDataHolder ruleDataHolder) throws EligibilityException {

        if ( QuestionHasEligibleCreditCardForBalanceTransfer.pose()
                .givenAProductList(ruleDataHolder.getProductArrangements())
                .givenAValue(ruleDataHolder.getRuleParamValue())
                .ask()) {
            return new EligibilityDecision(true);
        }
        return new EligibilityDecision(DeclineReasons.CR011_DECLINE_REASON);
    }
}
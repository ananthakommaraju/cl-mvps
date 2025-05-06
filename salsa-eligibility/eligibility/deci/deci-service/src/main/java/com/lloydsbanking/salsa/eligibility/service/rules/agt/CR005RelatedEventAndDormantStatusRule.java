package com.lloydsbanking.salsa.eligibility.service.rules.agt;

import com.lloydsbanking.salsa.eligibility.service.utility.EligibilityException;
import com.lloydsbanking.salsa.eligibility.service.questions.QuestionIsCustomerAccountEffective;
import com.lloydsbanking.salsa.eligibility.service.rules.common.EligibilityDecision;
import com.lloydsbanking.salsa.eligibility.service.rules.common.RuleDataHolder;

public class CR005RelatedEventAndDormantStatusRule implements AGTEligibilityRule {

    @Override
    public EligibilityDecision evaluate(RuleDataHolder ruleDataHolder) throws EligibilityException {

        if (QuestionIsCustomerAccountEffective.pose()
                .givenAProductList(ruleDataHolder.getProductArrangements())
                .givenAValue(ruleDataHolder.getRuleParamValue())
                .ask()) {
            return new EligibilityDecision(true);
        }

        return new EligibilityDecision("The customer's account is " + ruleDataHolder.getRuleParamValue());
    }
}

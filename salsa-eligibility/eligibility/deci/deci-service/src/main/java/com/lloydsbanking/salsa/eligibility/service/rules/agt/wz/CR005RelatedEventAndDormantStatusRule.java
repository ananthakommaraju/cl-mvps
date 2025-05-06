package com.lloydsbanking.salsa.eligibility.service.rules.agt.wz;

import com.lloydsbanking.salsa.eligibility.service.questions.QuestionIsCustomerAccountEffective;
import com.lloydsbanking.salsa.eligibility.service.rules.agt.AGTEligibilityRule;
import com.lloydsbanking.salsa.eligibility.service.rules.common.EligibilityDecision;
import com.lloydsbanking.salsa.eligibility.service.rules.common.RuleDataHolder;
import com.lloydsbanking.salsa.eligibility.service.utility.EligibilityException;

public class CR005RelatedEventAndDormantStatusRule implements AGTEligibilityRule {
    @Override
    public EligibilityDecision evaluate(RuleDataHolder ruleDataHolder) throws EligibilityException {

        if (QuestionIsCustomerAccountEffective.pose()
            .givenIsWzRequest(true)
            .givenAProductList(ruleDataHolder.getProductArrangements())
            .givenAValue(ruleDataHolder.getRuleParamValue())
            .ask()) {
            return new EligibilityDecision(true);
        }

        return new EligibilityDecision("The customer's account is " + ruleDataHolder.getRuleParamValue());
    }
}

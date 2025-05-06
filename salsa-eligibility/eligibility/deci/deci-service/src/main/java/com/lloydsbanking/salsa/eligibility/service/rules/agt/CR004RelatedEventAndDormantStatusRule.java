package com.lloydsbanking.salsa.eligibility.service.rules.agt;

import com.lloydsbanking.salsa.eligibility.service.utility.EligibilityException;
import com.lloydsbanking.salsa.eligibility.service.questions.QuestionHasExistingProductWithRelatedEventEnabled;
import com.lloydsbanking.salsa.eligibility.service.rules.common.EligibilityDecision;
import com.lloydsbanking.salsa.eligibility.service.rules.common.RuleDataHolder;

public class CR004RelatedEventAndDormantStatusRule implements AGTEligibilityRule {

    @Override
    public EligibilityDecision evaluate(RuleDataHolder ruleDataHolder) throws EligibilityException {

        if (QuestionHasExistingProductWithRelatedEventEnabled.pose()
                .givenAProductList(ruleDataHolder.getProductArrangements())
                .givenAValue(ruleDataHolder.getRuleParamValue())
                .ask()) {
            return new EligibilityDecision(true);
        }

        return new EligibilityDecision("Customer doesn't have an existing product with the " + ruleDataHolder.getRuleParamValue() + " event enabled");
    }
}

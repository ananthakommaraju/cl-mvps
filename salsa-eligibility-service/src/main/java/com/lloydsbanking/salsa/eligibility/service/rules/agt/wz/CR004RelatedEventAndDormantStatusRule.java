package com.lloydsbanking.salsa.eligibility.service.rules.agt.wz;

import com.lloydsbanking.salsa.eligibility.service.downstream.MandateAccessDetailsRetriever;
import com.lloydsbanking.salsa.eligibility.service.questions.QuestionHasExistingProductWithRelatedEventEnabled;
import com.lloydsbanking.salsa.eligibility.service.rules.agt.AGTEligibilityRule;
import com.lloydsbanking.salsa.eligibility.service.rules.common.EligibilityDecision;
import com.lloydsbanking.salsa.eligibility.service.rules.common.RuleDataHolder;
import com.lloydsbanking.salsa.eligibility.service.utility.EligibilityException;
import org.springframework.beans.factory.annotation.Autowired;

public class CR004RelatedEventAndDormantStatusRule implements AGTEligibilityRule {

    @Autowired
    MandateAccessDetailsRetriever mandateAccessDetailsRetriever;

    @Override
    public EligibilityDecision evaluate(RuleDataHolder ruleDataHolder) throws EligibilityException {

        if (QuestionHasExistingProductWithRelatedEventEnabled
                .pose()
                .givenAWzRequest(true)
                .givenAMandateAccessDetailsRetrieverInstance(mandateAccessDetailsRetriever)
                .givenAProductList(ruleDataHolder.getProductArrangements())
                .givenAValue(ruleDataHolder.getRuleParamValue())
                .givenRequestHeader(ruleDataHolder.getHeader())
                .ask()) {
            return new EligibilityDecision(true);
        }

        return new EligibilityDecision("Customer doesn't have an existing product with the " + ruleDataHolder.getRuleParamValue() + " event enabled");
    }
}

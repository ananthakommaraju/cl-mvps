package com.lloydsbanking.salsa.eligibility.service.rules.avn;

import com.lloydsbanking.salsa.eligibility.service.utility.EligibilityException;
import com.lloydsbanking.salsa.eligibility.service.questions.QuestionHasExceededProductThreshold;
import com.lloydsbanking.salsa.eligibility.service.rules.common.EligibilityDecision;
import com.lloydsbanking.salsa.eligibility.service.rules.common.RuleDataHolder;

public class CR017MaxProductHoldingRule implements AVNEligibilityRule {

    private static String PREFIX = "Customer cannot have more than ";

    private static String SUFFIX = " instances of the product.";

    @Override
    public EligibilityDecision evaluate(RuleDataHolder ruleDataHolder, String candidateInstruction) throws EligibilityException {
        if ( QuestionHasExceededProductThreshold.pose()
                .givenAProductList(ruleDataHolder.getProductArrangements())
                .givenAValue(ruleDataHolder.getRuleParamValue())
                .givenAnInstruction(candidateInstruction)
                .ask()) {
            return new EligibilityDecision(true);
        }
        else {
            return new EligibilityDecision(PREFIX.concat(ruleDataHolder.getRuleParamValue()).concat(SUFFIX));
        }

    }
}






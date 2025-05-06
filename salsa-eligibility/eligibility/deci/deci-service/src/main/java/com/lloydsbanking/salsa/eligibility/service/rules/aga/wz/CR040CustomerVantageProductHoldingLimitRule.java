package com.lloydsbanking.salsa.eligibility.service.rules.aga.wz;

import com.lloydsbanking.salsa.eligibility.service.questions.QuestionIsCustomerHoldingVantageProduct;
import com.lloydsbanking.salsa.eligibility.service.rules.aga.AGAEligibilityRule;
import com.lloydsbanking.salsa.eligibility.service.rules.common.EligibilityDecision;
import com.lloydsbanking.salsa.eligibility.service.rules.common.RuleDataHolder;
import com.lloydsbanking.salsa.eligibility.service.utility.EligibilityException;

public class CR040CustomerVantageProductHoldingLimitRule implements AGAEligibilityRule {
    @Override
    public EligibilityDecision evaluate(String candidateInstruction, RuleDataHolder ruleDataHolder) throws EligibilityException {
        String threshold = ruleDataHolder.getRuleParamValue();
        if (QuestionIsCustomerHoldingVantageProduct.pose().givenAProductList(ruleDataHolder.getProductArrangements()).givenAValue(threshold).ask()) {
            return new EligibilityDecision(true);
        }

        return new EligibilityDecision("Customer cannot have more than " + threshold + " instances of the product.");
    }
}
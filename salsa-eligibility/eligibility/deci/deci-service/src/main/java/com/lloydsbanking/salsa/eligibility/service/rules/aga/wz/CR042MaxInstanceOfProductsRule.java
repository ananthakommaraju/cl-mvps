package com.lloydsbanking.salsa.eligibility.service.rules.aga.wz;

import com.lloydsbanking.salsa.eligibility.service.utility.EligibilityException;
import com.lloydsbanking.salsa.eligibility.service.questions.QuestionHasExceededMaxCurrentAccountsThreshold;
import com.lloydsbanking.salsa.eligibility.service.questions.QuestionIsProductArrangementsExist;
import com.lloydsbanking.salsa.eligibility.service.rules.aga.AGAEligibilityRule;
import com.lloydsbanking.salsa.eligibility.service.rules.common.EligibilityDecision;
import com.lloydsbanking.salsa.eligibility.service.rules.common.RuleDataHolder;

public class CR042MaxInstanceOfProductsRule implements AGAEligibilityRule {

    @Override
    public EligibilityDecision evaluate(String candidateInstruction, RuleDataHolder ruleDataHolder) throws EligibilityException {
        String threshold = ruleDataHolder.getRuleParamValue();
        if (QuestionIsProductArrangementsExist.pose().givenAProductList(ruleDataHolder.getProductArrangements()).ask()) {

            if (QuestionHasExceededMaxCurrentAccountsThreshold.pose().givenAProductList(ruleDataHolder.getProductArrangements()).givenAValue(threshold).ask()) {
                return new EligibilityDecision("Customer cannot have more than " + threshold + " instances of the product.");
            }
        }
        return new EligibilityDecision(true);
    }
}

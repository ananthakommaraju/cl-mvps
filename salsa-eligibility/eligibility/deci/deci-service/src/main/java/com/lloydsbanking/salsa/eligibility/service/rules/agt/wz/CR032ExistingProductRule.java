package com.lloydsbanking.salsa.eligibility.service.rules.agt.wz;

import com.lloydsbanking.salsa.eligibility.service.utility.EligibilityException;
import com.lloydsbanking.salsa.eligibility.service.questions.QuestionHasAVAAccount;
import com.lloydsbanking.salsa.eligibility.service.questions.QuestionIsProductArrangementsExist;
import com.lloydsbanking.salsa.eligibility.service.rules.agt.AGTEligibilityRule;
import com.lloydsbanking.salsa.eligibility.service.rules.common.EligibilityDecision;
import com.lloydsbanking.salsa.eligibility.service.rules.common.RuleDataHolder;
import com.lloydsbanking.salsa.eligibility.service.utility.constants.DeclineReasons;

public class CR032ExistingProductRule implements AGTEligibilityRule {
    @Override
    public EligibilityDecision evaluate(RuleDataHolder ruleDataHolder) throws EligibilityException {
        if (!QuestionIsProductArrangementsExist.pose().givenAProductList(ruleDataHolder.getProductArrangements()).ask()) {
            return new EligibilityDecision(DeclineReasons.CR032_DECLINE_REASON_NO_ARRANGEMENTS);
        }
        if (QuestionHasAVAAccount.pose().givenAProductList(ruleDataHolder.getProductArrangements()).ask()) {
            return new EligibilityDecision(true);
        }
        else {
            return new EligibilityDecision(DeclineReasons.CR032_DECLINE_REASON_NO_AVA_ACCOUNT);
        }
    }
}

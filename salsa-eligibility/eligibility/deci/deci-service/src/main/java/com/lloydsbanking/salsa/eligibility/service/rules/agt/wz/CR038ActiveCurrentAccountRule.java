package com.lloydsbanking.salsa.eligibility.service.rules.agt.wz;


import com.lloydsbanking.salsa.eligibility.service.utility.EligibilityException;
import com.lloydsbanking.salsa.eligibility.service.questions.QuestionHasActiveCurrentAccount;
import com.lloydsbanking.salsa.eligibility.service.rules.agt.AGTEligibilityRule;
import com.lloydsbanking.salsa.eligibility.service.rules.common.EligibilityDecision;
import com.lloydsbanking.salsa.eligibility.service.rules.common.RuleDataHolder;
import com.lloydsbanking.salsa.eligibility.service.utility.constants.DeclineReasons;

public class CR038ActiveCurrentAccountRule implements AGTEligibilityRule {

    @Override
    public EligibilityDecision evaluate(RuleDataHolder ruleDataHolder) throws EligibilityException {
        if (QuestionHasActiveCurrentAccount.pose().givenAProductList(ruleDataHolder.getProductArrangements()).ask()) {
            return new EligibilityDecision(true);

        }
        return new EligibilityDecision(DeclineReasons.CR038_DECLINE_REASON);

    }
}

package com.lloydsbanking.salsa.eligibility.service.rules.agt.wz;


import com.lloydsbanking.salsa.eligibility.service.utility.EligibilityException;
import com.lloydsbanking.salsa.eligibility.service.questions.QuestionIsCurrentAccount;
import com.lloydsbanking.salsa.eligibility.service.rules.agt.AGTEligibilityRule;
import com.lloydsbanking.salsa.eligibility.service.rules.common.EligibilityDecision;
import com.lloydsbanking.salsa.eligibility.service.rules.common.RuleDataHolder;
import com.lloydsbanking.salsa.eligibility.service.utility.constants.DeclineReasons;

public class CR036CurrentAccountRule implements AGTEligibilityRule {
    @Override
    public EligibilityDecision evaluate(RuleDataHolder ruleDataHolder) throws EligibilityException {
        if ( !QuestionIsCurrentAccount.pose()
                .givenAProductList(ruleDataHolder.getProductArrangements())
                .ask()) {
            return new EligibilityDecision(DeclineReasons.CR036_DECLINE_REASON);
        }
        return new EligibilityDecision(true);
    }
}





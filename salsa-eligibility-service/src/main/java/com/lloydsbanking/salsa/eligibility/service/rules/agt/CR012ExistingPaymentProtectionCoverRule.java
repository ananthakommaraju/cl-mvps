package com.lloydsbanking.salsa.eligibility.service.rules.agt;


import com.lloydsbanking.salsa.eligibility.service.utility.EligibilityException;
import com.lloydsbanking.salsa.eligibility.service.questions.QuestionHasEligibleProductForPPC;
import com.lloydsbanking.salsa.eligibility.service.rules.common.DeclineReasonAdder;
import com.lloydsbanking.salsa.eligibility.service.rules.common.EligibilityDecision;
import com.lloydsbanking.salsa.eligibility.service.rules.common.RuleDataHolder;
import com.lloydsbanking.salsa.eligibility.service.utility.constants.DeclineReasons;
import org.springframework.beans.factory.annotation.Autowired;

public class CR012ExistingPaymentProtectionCoverRule implements AGTEligibilityRule {
    @Autowired
    DeclineReasonAdder declineReasonAdder;

    @Override
    public EligibilityDecision evaluate(RuleDataHolder ruleDataHolder) throws EligibilityException {

        if (QuestionHasEligibleProductForPPC.pose()
                .givenAProductList(ruleDataHolder.getProductArrangements())
                .ask()) {
            return new EligibilityDecision(true);
        }

        return new EligibilityDecision(DeclineReasons.CR012_DECLINE_REASON);
    }
}

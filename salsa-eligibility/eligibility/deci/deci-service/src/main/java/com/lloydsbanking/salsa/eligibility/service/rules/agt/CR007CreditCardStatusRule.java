package com.lloydsbanking.salsa.eligibility.service.rules.agt;

import com.lloydsbanking.salsa.eligibility.service.utility.EligibilityException;
import com.lloydsbanking.salsa.eligibility.service.questions.QuestionIsNotStolenOrBankruptOrChargedOff;
import com.lloydsbanking.salsa.eligibility.service.rules.common.EligibilityDecision;
import com.lloydsbanking.salsa.eligibility.service.rules.common.RuleDataHolder;
import com.lloydsbanking.salsa.eligibility.service.utility.constants.DeclineReasons;

public class CR007CreditCardStatusRule implements AGTEligibilityRule {

    @Override
    public EligibilityDecision evaluate(RuleDataHolder ruleDataHolder) throws EligibilityException {
        if (QuestionIsNotStolenOrBankruptOrChargedOff.pose().givenAProductList(ruleDataHolder.getProductArrangements()).ask()) {
            return new EligibilityDecision(true);
        }
        return new EligibilityDecision(DeclineReasons.CR007_DECLINE_REASON);

    }
}

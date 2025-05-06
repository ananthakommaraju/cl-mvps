package com.lloydsbanking.salsa.eligibility.service.rules.aga;

import com.lloydsbanking.salsa.eligibility.service.utility.EligibilityException;
import com.lloydsbanking.salsa.eligibility.service.questions.QuestionIsISAOpenedThisYear;
import com.lloydsbanking.salsa.eligibility.service.rules.common.EligibilityDecision;
import com.lloydsbanking.salsa.eligibility.service.rules.common.RuleDataHolder;
import com.lloydsbanking.salsa.eligibility.service.utility.constants.DeclineReasons;

public class CR016ISACurrentYearRule implements AGAEligibilityRule {

    @Override
    public EligibilityDecision evaluate(String candidateInstruction, RuleDataHolder ruleDataHolder) throws EligibilityException {

        if (QuestionIsISAOpenedThisYear.pose().givenAProductList(ruleDataHolder.getProductArrangements()).ask()) {
            return new EligibilityDecision(DeclineReasons.CR016_DECLINE_REASON);
        }
        return new EligibilityDecision(true);
    }

}

package com.lloydsbanking.salsa.eligibility.service.rules.aga;


import com.lloydsbanking.salsa.eligibility.service.utility.EligibilityException;
import com.lloydsbanking.salsa.eligibility.service.questions.QuestionHasDepositedFundsThisYear;
import com.lloydsbanking.salsa.eligibility.service.rules.common.EligibilityDecision;
import com.lloydsbanking.salsa.eligibility.service.rules.common.RuleDataHolder;
import com.lloydsbanking.salsa.eligibility.service.utility.constants.DeclineReasons;

public class CR015ISAFundTransferRule implements AGAEligibilityRule {

    @Override
    public EligibilityDecision evaluate(String candidateInstruction, RuleDataHolder ruleDataHolder) throws EligibilityException {

        if ( QuestionHasDepositedFundsThisYear.pose()
                .givenAProductList(ruleDataHolder.getProductArrangements())
                .ask()) {
            return new EligibilityDecision(DeclineReasons.CR015_DECLINE_REASON);
        }

        return new EligibilityDecision(true);

    }

}




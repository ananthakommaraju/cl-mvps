package com.lloydsbanking.salsa.eligibility.service.rules.asa;


import com.lloydsbanking.salsa.eligibility.service.utility.EligibilityException;
import com.lloydsbanking.salsa.eligibility.service.questions.QuestionIsOverDraftAlreadyExpired;
import com.lloydsbanking.salsa.eligibility.service.questions.QuestionIsOverDraftAppliedAlready;
import com.lloydsbanking.salsa.eligibility.service.questions.QuestionIsOverDraftExpiringInNumberOfDays;
import com.lloydsbanking.salsa.eligibility.service.rules.common.EligibilityDecision;
import com.lloydsbanking.salsa.eligibility.service.rules.common.RuleDataHolder;

public class CR050OverdraftExpiryRule implements ASAEligibilityRule {

    @Override
    public EligibilityDecision evaluate(RuleDataHolder ruleDataHolder) throws EligibilityException {
        String paramDays[] = ruleDataHolder.getRuleParamValue().split(":");
        String accNum = ruleDataHolder.getArrangementIdentifier().getAccNum();
        String sortCode = ruleDataHolder.getArrangementIdentifier().getSortCode();
        String numOfDaysSinceApplied = paramDays[0];
        String daysUntilExpiring = paramDays[1];
        if (!QuestionIsOverDraftAppliedAlready.pose()
                .givenAnAccountNumber(accNum)
                .givenASortCode(sortCode)
                .givenAProductList(ruleDataHolder.getProductArrangements())
                .givenAValue(numOfDaysSinceApplied)
                .ask()) {
            return new EligibilityDecision("Overdraft has been applied in last " + numOfDaysSinceApplied + " days on the account");

        }
        else if (!QuestionIsOverDraftExpiringInNumberOfDays.pose()
            .givenAnAccountNumber(accNum)
            .givenASortCode(sortCode)
            .givenAProductList(ruleDataHolder.getProductArrangements())
            .givenAValue(daysUntilExpiring)
            .ask()) {
            return new EligibilityDecision("Overdraft is expiring in less than " + daysUntilExpiring + " days on the account");
        }
        else if (!QuestionIsOverDraftAlreadyExpired.pose()
                .givenAnAccountNumber(accNum)
                .givenASortCode(sortCode)
                .givenAProductList(ruleDataHolder.getProductArrangements())
                .ask()) {
            return new EligibilityDecision("Overdraft is already expired");
        }


        return new EligibilityDecision(true);

    }

}


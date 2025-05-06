package com.lloydsbanking.salsa.eligibility.service.rules.cst.wz;

import com.lloydsbanking.salsa.eligibility.service.utility.EligibilityException;
import com.lloydsbanking.salsa.eligibility.service.questions.QuestionHasClubAccount;
import com.lloydsbanking.salsa.eligibility.service.rules.common.EligibilityDecision;
import com.lloydsbanking.salsa.eligibility.service.rules.common.RuleDataHolder;
import com.lloydsbanking.salsa.eligibility.service.rules.cst.CSTEligibilityRule;
import com.lloydsbanking.salsa.eligibility.service.utility.constants.DeclineReasons;

import javax.xml.datatype.XMLGregorianCalendar;

public class CR049ClubAccountHoldingRule implements CSTEligibilityRule {

    @Override
    public EligibilityDecision evaluate(RuleDataHolder ruleDataHolder, XMLGregorianCalendar birthDate, String sortCode, String customerId) throws EligibilityException {
        if ( QuestionHasClubAccount.pose()
                .givenAProductList(ruleDataHolder.getProductArrangements())
                .ask()) {
            return new EligibilityDecision(true);
        }
        else {
            return new EligibilityDecision(DeclineReasons.CR049_DECLINE_REASON);
        }
    }
}

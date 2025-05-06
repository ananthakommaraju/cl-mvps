package com.lloydsbanking.salsa.eligibility.service.rules.agt;

import com.lloydsbanking.salsa.eligibility.service.utility.EligibilityException;
import com.lloydsbanking.salsa.eligibility.service.rules.common.EligibilityDecision;
import com.lloydsbanking.salsa.eligibility.service.rules.common.RuleDataHolder;
import com.lloydsbanking.salsa.eligibility.service.utility.constants.DeclineReasons;
import org.springframework.beans.factory.annotation.Autowired;

public class CR027HasCurrentAccountRule implements AGTEligibilityRule {

    private static final String CURRENT_ACCOUNT_TYPE = "C";

    @Autowired
    CheckLoanAndCurrentAccountType checkLoanAndCurrentAccountType;

    @Override
    public EligibilityDecision evaluate(RuleDataHolder ruleDataHolder) throws EligibilityException {
        boolean hasCurrentAccount = checkLoanAndCurrentAccountType.hasCurrentAccountOrLoggedInChannelLoan(ruleDataHolder.getHeader(), CURRENT_ACCOUNT_TYPE);
        if (!hasCurrentAccount) {
            return new EligibilityDecision(DeclineReasons.CR027_DECLINE_REASON);
        }
        return new EligibilityDecision(true);
    }

}

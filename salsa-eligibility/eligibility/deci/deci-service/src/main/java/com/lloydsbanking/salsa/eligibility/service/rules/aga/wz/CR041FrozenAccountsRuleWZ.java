package com.lloydsbanking.salsa.eligibility.service.rules.aga.wz;

import com.lloydsbanking.salsa.eligibility.service.downstream.AppGroupRetriever;
import com.lloydsbanking.salsa.eligibility.service.downstream.CheckBalanceRetriever;
import com.lloydsbanking.salsa.eligibility.service.questions.QuestionIsCBSIndicatorPresentOnAllAccounts;
import com.lloydsbanking.salsa.eligibility.service.rules.aga.AGAEligibilityRule;
import com.lloydsbanking.salsa.eligibility.service.rules.common.EligibilityDecision;
import com.lloydsbanking.salsa.eligibility.service.rules.common.RuleDataHolder;
import com.lloydsbanking.salsa.eligibility.service.utility.EligibilityException;
import com.lloydsbanking.salsa.eligibility.service.utility.constants.DeclineReasons;
import lb_gbo_sales.messages.RequestHeader;
import org.springframework.beans.factory.annotation.Autowired;

public class CR041FrozenAccountsRuleWZ implements AGAEligibilityRule {

    @Autowired
    CheckBalanceRetriever checkBalanceRetriever;

    @Autowired
    AppGroupRetriever appGroupRetriever;

    private static final String SAVINGS = "SA";

    private static final String CURRENT_ACCOUNT = "CA";

    @Override
    public EligibilityDecision evaluate(String candidateInstruction, RuleDataHolder ruleDataHolder) throws EligibilityException {
        String threshold = ruleDataHolder.getRuleParamValue();
        String arrangementType = ruleDataHolder.getArrangementType();
        RequestHeader header = ruleDataHolder.getHeader();
        boolean isEitherCurrentOrSavingsAccount = false;
        if (null != arrangementType && (CURRENT_ACCOUNT.equalsIgnoreCase(arrangementType) || SAVINGS.equalsIgnoreCase(arrangementType))) {
            isEitherCurrentOrSavingsAccount = true;
        }
        if (QuestionIsCBSIndicatorPresentOnAllAccounts.pose()
                .givenEitherCurrentOrSavingsAccount(isEitherCurrentOrSavingsAccount)
                .givenCheckBalanceRetrieverClientInstance(checkBalanceRetriever)
                .givenAppGroupRetrieverClientInstance(appGroupRetriever)
                .givenAProductList(ruleDataHolder.getProductArrangements())
                .givenRequestHeader(header)
                .givenAValue(threshold)
                .ask()) {
            return new EligibilityDecision(DeclineReasons.CR041_DECLINE_REASON + threshold);

        }
        return new EligibilityDecision(true);
    }


}

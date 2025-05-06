package com.lloydsbanking.salsa.eligibility.service.rules.agt.wz;

import com.lloydsbanking.salsa.eligibility.service.utility.EligibilityException;
import com.lloydsbanking.salsa.eligibility.service.questions.QuestionHasOnlyOffshoreAccounts;
import com.lloydsbanking.salsa.eligibility.service.questions.QuestionIsProductArrangementsExist;
import com.lloydsbanking.salsa.eligibility.service.rules.agt.AGTEligibilityRule;
import com.lloydsbanking.salsa.eligibility.service.rules.common.EligibilityDecision;
import com.lloydsbanking.salsa.eligibility.service.rules.common.RuleDataHolder;
import com.lloydsbanking.salsa.eligibility.service.utility.constants.DeclineReasons;

public class CR013OffshoreAccountsRule implements AGTEligibilityRule {

    public static final String PRODUCT_HOLDINGS_FOR_LOGGED_IN_BRAND = "3";

    @Override
    public EligibilityDecision evaluate(RuleDataHolder ruleDataHolder) throws EligibilityException {

        if (!QuestionIsProductArrangementsExist.pose()
                .givenAProductList(ruleDataHolder.getProductArrangements())
                .ask() || (null != ruleDataHolder.getCustomerDetails() && PRODUCT_HOLDINGS_FOR_LOGGED_IN_BRAND.equalsIgnoreCase(ruleDataHolder.getCustomerDetails()
                .getCustomerSegment()))) {
            return new EligibilityDecision(true);
        }

        if (QuestionHasOnlyOffshoreAccounts.pose().givenAProductList(ruleDataHolder.getProductArrangements()).givenAValue(ruleDataHolder.getRuleParamValue()).ask()) {
            return new EligibilityDecision(true);
        }

        return new EligibilityDecision(DeclineReasons.CR013_DECLINE_REASON);
    }

}

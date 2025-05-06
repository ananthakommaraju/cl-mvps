package com.lloydsbanking.salsa.eligibility.service.rules.aga;


import com.lloydsbanking.salsa.eligibility.service.utility.EligibilityException;
import com.lloydsbanking.salsa.eligibility.service.downstream.AppGroupRetriever;
import com.lloydsbanking.salsa.eligibility.service.downstream.ShadowLimitRetriever;
import com.lloydsbanking.salsa.eligibility.service.questions.QuestionHasActiveCurrentAccountWithDirectDebitAndShadowLimitChecked;
import com.lloydsbanking.salsa.eligibility.service.rules.common.EligibilityDecision;
import com.lloydsbanking.salsa.eligibility.service.rules.common.RuleDataHolder;
import com.lloydsbanking.salsa.eligibility.service.utility.constants.DeclineReasons;
import org.springframework.beans.factory.annotation.Autowired;

public class CR060ActiveCurrentAccountRule implements AGAEligibilityRule {

    @Autowired
    public ShadowLimitRetriever shadowLimitRetriever;

    @Autowired
    public AppGroupRetriever appGroupRetriever;

    @Override
    public EligibilityDecision evaluate(String candidateInstruction, RuleDataHolder ruleDataHolder) throws EligibilityException {

        if (QuestionHasActiveCurrentAccountWithDirectDebitAndShadowLimitChecked.pose()
                .givenShadowLimitRetrieverClientInstance(shadowLimitRetriever)
                .givenAProductList(ruleDataHolder.getProductArrangements())
                .givenAValue(ruleDataHolder.getRuleParamValue())
                .givenRequestHeader(ruleDataHolder.getHeader())
                .givenAppGroupRetrieverClientInstance(appGroupRetriever)
                .ask()) {
            return new EligibilityDecision(true);

        }
        return new EligibilityDecision(DeclineReasons.CR060_DECLINE_REASON);

    }

}

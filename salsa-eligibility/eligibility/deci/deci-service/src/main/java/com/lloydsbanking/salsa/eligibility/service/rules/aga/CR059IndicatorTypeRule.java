package com.lloydsbanking.salsa.eligibility.service.rules.aga;


import com.lloydsbanking.salsa.eligibility.service.utility.EligibilityException;
import com.lloydsbanking.salsa.eligibility.service.downstream.AppGroupRetriever;
import com.lloydsbanking.salsa.eligibility.service.downstream.CBSIndicatorRetriever;
import com.lloydsbanking.salsa.eligibility.service.questions.QuestionIsCbs646IndicatorNotSet;
import com.lloydsbanking.salsa.eligibility.service.rules.common.EligibilityDecision;
import com.lloydsbanking.salsa.eligibility.service.rules.common.RuleDataHolder;
import com.lloydsbanking.salsa.eligibility.service.utility.constants.DeclineReasons;
import org.springframework.beans.factory.annotation.Autowired;

public class CR059IndicatorTypeRule implements AGAEligibilityRule {

    @Autowired
    public CBSIndicatorRetriever cbsIndicatorRetriever;

    @Autowired
    public AppGroupRetriever appGroupRetriever;


    @Override
    public EligibilityDecision evaluate(String candidateInstruction, RuleDataHolder ruleDataHolder) throws EligibilityException {

        if (QuestionIsCbs646IndicatorNotSet.pose()
                .givenRequestHeader(ruleDataHolder.getHeader())
                .givenAppGroupRetrieverClientInstance(appGroupRetriever)
                .givenAProductList(ruleDataHolder.getProductArrangements())
                .givenAValue(ruleDataHolder.getRuleParamValue())
                .givenCbsIndicatorRetriever(cbsIndicatorRetriever)
                .ask()) {
            return new EligibilityDecision(true);
        }
        return new EligibilityDecision(DeclineReasons.CR059_DECLINE_REASON_1 + ruleDataHolder.getRuleParamValue() + DeclineReasons.CR059_DECLINE_REASON_2);

    }
}
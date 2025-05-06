package com.lloydsbanking.salsa.eligibility.service.rules.aga;


import com.lloydsbanking.salsa.eligibility.service.utility.EligibilityException;
import com.lloydsbanking.salsa.eligibility.service.downstream.AppGroupRetriever;
import com.lloydsbanking.salsa.eligibility.service.downstream.CBSIndicatorRetriever;
import com.lloydsbanking.salsa.eligibility.service.questions.QuestionIsCbs8IndicatorNotSet;
import com.lloydsbanking.salsa.eligibility.service.rules.common.EligibilityDecision;
import com.lloydsbanking.salsa.eligibility.service.rules.common.RuleDataHolder;
import com.lloydsbanking.salsa.eligibility.service.utility.constants.DeclineReasons;
import org.springframework.beans.factory.annotation.Autowired;

public class CR061IndicatorTypeRule implements AGAEligibilityRule {

    @Autowired
    public CBSIndicatorRetriever cbsIndicatorRetriever;

    @Autowired
    public AppGroupRetriever appGroupRetriever;


    @Override
    public EligibilityDecision evaluate(String candidateInstruction, RuleDataHolder ruleDataHolder) throws EligibilityException {
        String ruleParamValue = ruleDataHolder.getRuleParamValue();

        if (QuestionIsCbs8IndicatorNotSet.pose()
                .givenRequestHeader(ruleDataHolder.getHeader())
                .givenAppGroupRetrieverClientInstance(appGroupRetriever)
                .givenAProductList(ruleDataHolder.getProductArrangements())
                .givenAValue(ruleParamValue)
                .givenCbsIndicatorRetriever(cbsIndicatorRetriever)
                .ask()) {
            return new EligibilityDecision(true);
        }
        return new EligibilityDecision(DeclineReasons.CR061_DECLINE_REASON_1 + ruleParamValue + DeclineReasons.CR061_DECLINE_REASON_2);
    }
}
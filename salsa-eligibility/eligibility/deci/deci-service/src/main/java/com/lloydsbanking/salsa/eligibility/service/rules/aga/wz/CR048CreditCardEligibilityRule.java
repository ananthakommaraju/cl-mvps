package com.lloydsbanking.salsa.eligibility.service.rules.aga.wz;


import com.lloydsbanking.salsa.downstream.prd.service.AdministerProductSelectionService;
import com.lloydsbanking.salsa.eligibility.service.questions.QuestionIsNumberOfCreditCardsHeldGreaterThanZero;
import com.lloydsbanking.salsa.eligibility.service.rules.aga.AGAEligibilityRule;
import com.lloydsbanking.salsa.eligibility.service.rules.common.EligibilityDecision;
import com.lloydsbanking.salsa.eligibility.service.rules.common.RuleDataHolder;
import com.lloydsbanking.salsa.eligibility.service.utility.EligibilityException;
import com.lloydsbanking.salsa.eligibility.service.utility.constants.DeclineReasons;
import com.lloydsbanking.salsa.logging.application.ProductTraceLog;
import lib_sim_bo.businessobjects.ExtraConditions;
import lib_sim_bo.businessobjects.Product;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;


public class CR048CreditCardEligibilityRule implements AGAEligibilityRule {

    @Autowired
    AdministerProductSelectionService administerProductSelectionService;

    @Autowired
    ProductTraceLog productTraceLog;

    @Override
    public EligibilityDecision evaluate(String candidateInstruction, RuleDataHolder ruleDataHolder) throws EligibilityException {
        Product associatedProduct = ruleDataHolder.getAssociatedProduct();
        ExtraConditions extraConditions = new ExtraConditions();
        if (QuestionIsNumberOfCreditCardsHeldGreaterThanZero.pose()
                .givenProductTraceLogInstance(productTraceLog)
                .givenAssociatedProduct(associatedProduct)
                .givenAnInstanceAdministerProductSelection(administerProductSelectionService)
                .givenExtraCondition(extraConditions)
                .givenAProductList(ruleDataHolder.getProductArrangements())
                .ask()) {
            return new EligibilityDecision(DeclineReasons.CR048_DECLINE_REASON);
        }
        if (!CollectionUtils.isEmpty(extraConditions.getConditions())) {
            return new EligibilityDecision(extraConditions);
        }
        return new EligibilityDecision(true);
    }
}

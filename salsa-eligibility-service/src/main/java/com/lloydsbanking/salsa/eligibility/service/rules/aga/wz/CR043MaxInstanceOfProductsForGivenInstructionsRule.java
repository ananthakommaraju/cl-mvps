package com.lloydsbanking.salsa.eligibility.service.rules.aga.wz;

import com.lloydsbanking.salsa.downstream.prd.model.RefInstructionRulesPrdDto;
import com.lloydsbanking.salsa.eligibility.service.downstream.EligibilityPRDRetriever;
import com.lloydsbanking.salsa.eligibility.service.questions.QuestionHasExceededThresholdForMnemonicsGroup;
import com.lloydsbanking.salsa.eligibility.service.questions.QuestionIsProductArrangementsExist;
import com.lloydsbanking.salsa.eligibility.service.rules.aga.AGAEligibilityRule;
import com.lloydsbanking.salsa.eligibility.service.rules.common.EligibilityDecision;
import com.lloydsbanking.salsa.eligibility.service.rules.common.RuleDataHolder;
import com.lloydsbanking.salsa.eligibility.service.utility.EligibilityException;
import lib_sim_gmo.messages.RequestHeader;
import lib_sim_salesprocessmanagement.ia_determineeligiblecustomerinstruction.DetermineEligibleCustomerInstructionsDataNotAvailableErrorMsg;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class CR043MaxInstanceOfProductsForGivenInstructionsRule implements AGAEligibilityRule {

    @Autowired
    EligibilityPRDRetriever eligibilityPRDRetriever;

    @Override
    public EligibilityDecision evaluate(String candidateInstruction, RuleDataHolder ruleDataHolder) throws EligibilityException {
        if (QuestionIsProductArrangementsExist.pose().givenAProductList(ruleDataHolder.getProductArrangements()).ask()) {
            String thresholdMnemonics = "";
            String thresholdCount = null;
            List<RefInstructionRulesPrdDto> refInstructionRulesPrdDtos;
            try {
                refInstructionRulesPrdDtos = eligibilityPRDRetriever.getCompositeInstructionConditions(candidateInstruction, ruleDataHolder.getChannel(), new RequestHeader(), candidateInstruction);
            }
            catch (DetermineEligibleCustomerInstructionsDataNotAvailableErrorMsg errorMsg) {
                throw new EligibilityException(errorMsg);
            }
            for (RefInstructionRulesPrdDto refInstructionRulesPrdDto : refInstructionRulesPrdDtos) {
                if (refInstructionRulesPrdDto.getRule().equals(ruleDataHolder.getRule())) {
                    if (1 == refInstructionRulesPrdDto.getRuleParamSeq().intValue()) {
                        thresholdMnemonics = refInstructionRulesPrdDto.getRuleParamValue();
                    }
                    else if (2 == refInstructionRulesPrdDto.getRuleParamSeq().intValue()) {
                        thresholdCount = refInstructionRulesPrdDto.getRuleParamValue();
                    }
                }
            }
            if (QuestionHasExceededThresholdForMnemonicsGroup.pose()
                    .givenAGroupOfProductMnemonic(thresholdMnemonics)
                    .givenThresholdCount(thresholdCount)
                    .givenAProductList(ruleDataHolder.getProductArrangements())
                    .ask()) {
                return new EligibilityDecision("Customer cannot have more than " + thresholdCount + "products for group of mnemonics defined (cr 043 rule)");
            }

        }
        return new EligibilityDecision(true);
    }

}

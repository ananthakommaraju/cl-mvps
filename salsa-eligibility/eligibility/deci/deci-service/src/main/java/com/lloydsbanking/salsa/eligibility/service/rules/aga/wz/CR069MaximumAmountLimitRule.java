package com.lloydsbanking.salsa.eligibility.service.rules.aga.wz;

import com.lloydsbanking.salsa.downstream.switches.SwitchService;
import com.lloydsbanking.salsa.eligibility.service.questions.QuestionsHasDepositedAnyFundsThisTaxYear;
import com.lloydsbanking.salsa.eligibility.service.rules.aga.AGAEligibilityRule;
import com.lloydsbanking.salsa.eligibility.service.rules.common.EligibilityDecision;
import com.lloydsbanking.salsa.eligibility.service.rules.common.RuleDataHolder;
import com.lloydsbanking.salsa.eligibility.service.utility.EligibilityException;
import com.lloydsbanking.salsa.eligibility.service.utility.constants.DeclineReasons;
import com.lloydsbanking.salsa.eligibility.service.utility.constants.Mnemonics;
import org.apache.cxf.common.util.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import javax.xml.ws.WebServiceException;

public class CR069MaximumAmountLimitRule implements AGAEligibilityRule {
    private static final Logger LOGGER = Logger.getLogger(CR069MaximumAmountLimitRule.class);

    @Autowired
    SwitchService switchClient;

    @Override
    public EligibilityDecision evaluate(String candidateInstruction, RuleDataHolder ruleDataHolder) throws EligibilityException {

        boolean multiCashSwitchIsaSwitchValue = false;
        String brand = ruleDataHolder.getChannel();
        if (!CollectionUtils.isEmpty(ruleDataHolder.getCandidateInstructions())
            && null != ruleDataHolder.getCandidateInstructions().get(0)
            && ruleDataHolder.getCandidateInstructions().get(0).contains(Mnemonics.ISA)) {
            try {
                multiCashSwitchIsaSwitchValue = switchClient.getBrandedSwitchValue("SW_EnbMulCashISA", brand, false);
            }
            catch (WebServiceException e) {
                LOGGER.info("Error occurred while fetching Switch value for channel " + brand + e);
            }
        }
        if (multiCashSwitchIsaSwitchValue) {
            if (QuestionsHasDepositedAnyFundsThisTaxYear.pose()
                .givenAProductList(ruleDataHolder.getProductArrangements())
                .ask()) {
                return new EligibilityDecision(true);

            }
            EligibilityDecision eligibilityDecision = new EligibilityDecision(true);
            eligibilityDecision.setReasonText(DeclineReasons.CR069_DECLINE_REASON);
            return eligibilityDecision;
        }
        return new EligibilityDecision(true);
    }
}
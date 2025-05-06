package com.lloydsbanking.salsa.eligibility.service.rules.aga.wz;

import com.lloydsbanking.salsa.downstream.switches.SwitchService;
import com.lloydsbanking.salsa.eligibility.service.questions.QuestionIsISAOpenedThisYear;
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

public class CR016ISACurrentYearRule implements AGAEligibilityRule {

    private static final Logger LOGGER = Logger.getLogger(CR016ISACurrentYearRule.class);

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

        if (!multiCashSwitchIsaSwitchValue) {
            if (QuestionIsISAOpenedThisYear.pose()
                    .givenAProductList(ruleDataHolder.getProductArrangements())
                    .ask()) {
                return new EligibilityDecision(DeclineReasons.CR016_DECLINE_REASON);
            }
            return new EligibilityDecision(true);
        } else {
            return new EligibilityDecision(true);
        }
    }

}

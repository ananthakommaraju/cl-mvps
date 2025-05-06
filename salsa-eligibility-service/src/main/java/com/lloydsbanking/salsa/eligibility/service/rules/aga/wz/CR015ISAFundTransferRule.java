package com.lloydsbanking.salsa.eligibility.service.rules.aga.wz;

import com.lloydsbanking.salsa.downstream.switches.SwitchService;
import com.lloydsbanking.salsa.eligibility.service.downstream.AppGroupRetriever;
import com.lloydsbanking.salsa.eligibility.service.downstream.CheckBalanceRetriever;
import com.lloydsbanking.salsa.eligibility.service.questions.QuestionHasDepositedFundsThisYear;
import com.lloydsbanking.salsa.eligibility.service.rules.aga.AGAEligibilityRule;
import com.lloydsbanking.salsa.eligibility.service.rules.common.EligibilityDecision;
import com.lloydsbanking.salsa.eligibility.service.rules.common.RuleDataHolder;
import com.lloydsbanking.salsa.eligibility.service.utility.EligibilityException;
import com.lloydsbanking.salsa.eligibility.service.utility.constants.DeclineReasons;
import com.lloydsbanking.salsa.eligibility.service.utility.constants.EligibilityServiceConstants;
import com.lloydsbanking.salsa.eligibility.service.utility.constants.Mnemonics;
import org.apache.cxf.common.util.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import javax.xml.ws.WebServiceException;

public class CR015ISAFundTransferRule implements AGAEligibilityRule {
    private static final Logger LOGGER = Logger.getLogger(CR015ISAFundTransferRule.class);

    @Autowired
    SwitchService switchClient;

    @Autowired
    CheckBalanceRetriever checkBalanceRetriever;

    @Autowired
    AppGroupRetriever appGroupRetriever;

    @Override
    public EligibilityDecision evaluate(String candidateInstruction, RuleDataHolder ruleDataHolder) throws EligibilityException {
        boolean multiCashSwitchIsaSwitchValue = getMultiCashIsaSwitchValue(ruleDataHolder);

        String arrangementType = ruleDataHolder.getArrangementType();
        boolean isEitherCurrentOrSavingsAccount = false;
        if (null != arrangementType && (EligibilityServiceConstants.ARRANGEMENT_TYPE_CURRENT_ACCOUNT.equalsIgnoreCase(arrangementType) || EligibilityServiceConstants.ARRANGEMENT_TYPE_SAVINGS_ACCOUNT.equalsIgnoreCase(arrangementType))) {
            isEitherCurrentOrSavingsAccount = true;
        }

        if (!multiCashSwitchIsaSwitchValue) {
            if (QuestionHasDepositedFundsThisYear.pose().givenIsWzRequest(true).givenEitherCurrentOrSavingsAccount(isEitherCurrentOrSavingsAccount).givenCheckBalanceRetrieverClientInstance(checkBalanceRetriever).givenAppGroupRetrieverClientInstance(appGroupRetriever).givenAProductList(ruleDataHolder.getProductArrangements()).givenRequestHeader(ruleDataHolder.getHeader()).ask()) {
                return new EligibilityDecision(DeclineReasons.CR015_DECLINE_REASON);
            }
            return new EligibilityDecision(true);
        }
        else {
            return new EligibilityDecision(true);
        }

    }

    private boolean getMultiCashIsaSwitchValue(final RuleDataHolder ruleDataHolder) {
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
        return multiCashSwitchIsaSwitchValue;
    }

}




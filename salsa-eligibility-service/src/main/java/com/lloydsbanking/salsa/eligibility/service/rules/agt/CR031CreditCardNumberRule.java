package com.lloydsbanking.salsa.eligibility.service.rules.agt;

import com.lloydsbanking.salsa.eligibility.service.utility.EligibilityException;
import com.lloydsbanking.salsa.eligibility.service.downstream.ProductHoldingRetriever;
import com.lloydsbanking.salsa.eligibility.service.rules.common.EligibilityDecision;
import com.lloydsbanking.salsa.eligibility.service.rules.common.RuleDataHolder;
import com.lloydsbanking.salsa.eligibility.service.utility.constants.DeclineReasons;
import com.lloydsbanking.salsa.eligibility.service.utility.exceptions.SalsaInternalServiceException;
import com.lloydsbanking.salsa.soap.ocis.f336.objects.ProductPartyData;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class CR031CreditCardNumberRule implements AGTEligibilityRule {

    @Autowired
    ProductHoldingRetriever productHoldingRetriever;

    @Autowired
    CheckLoanAndCurrentAccountType checkLoanAndCurrentAccountType;

    @Override
    public EligibilityDecision evaluate(RuleDataHolder ruleDataHolder) throws EligibilityException {

        List<ProductPartyData> productPartyDatas;
        try {
            productPartyDatas = productHoldingRetriever.getProductHoldings(ruleDataHolder.getHeader());
        } catch (SalsaInternalServiceException e) {
            throw new EligibilityException(e);
        }

        if (checkLoanAndCurrentAccountType.existingCreditCardGroupCodeProduct(productPartyDatas) > 1) {
            return new EligibilityDecision(DeclineReasons.CR031_DECLINE_REASON);
        }
        return new EligibilityDecision(true);
    }
}

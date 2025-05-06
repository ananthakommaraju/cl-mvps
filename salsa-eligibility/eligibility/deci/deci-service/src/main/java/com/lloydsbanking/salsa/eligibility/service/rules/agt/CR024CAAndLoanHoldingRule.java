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

public class CR024CAAndLoanHoldingRule implements AGTEligibilityRule {

    private static final String CURRENT_ACCOUNT_TYPE = "C";

    private static final String LOAN_ACCOUNT_TYPE = "L";

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

        boolean hasFEPSLoan = checkLoanAndCurrentAccountType.hasFEPSLoan(productPartyDatas);
        boolean hasCurrentAccount = checkLoanAndCurrentAccountType.hasCurrentAccountOrLoggedInChannelLoan(ruleDataHolder.getHeader(), CURRENT_ACCOUNT_TYPE);
        boolean hasLoggedInChannelLoan = checkLoanAndCurrentAccountType.hasCurrentAccountOrLoggedInChannelLoan(ruleDataHolder.getHeader(), LOAN_ACCOUNT_TYPE);
        if (!hasFEPSLoan && !hasCurrentAccount && !hasLoggedInChannelLoan) {
            return new EligibilityDecision(DeclineReasons.CR024_DECLINE_REASON);
        }
        else {
            return new EligibilityDecision(true);
        }

    }
}

package com.lloydsbanking.salsa.eligibility.service.rules.cst.wz;

import com.lloydsbanking.salsa.eligibility.service.downstream.AppGroupRetriever;
import com.lloydsbanking.salsa.eligibility.service.downstream.CustomerDecisionDetailsRetriever;
import com.lloydsbanking.salsa.eligibility.service.rules.common.EligibilityDecision;
import com.lloydsbanking.salsa.eligibility.service.rules.common.RuleDataHolder;
import com.lloydsbanking.salsa.eligibility.service.rules.cst.CSTEligibilityRule;
import com.lloydsbanking.salsa.eligibility.service.utility.EligibilityException;
import com.lloydsbanking.salsa.eligibility.service.utility.constants.DeclineReasons;
import com.lloydsbanking.salsa.eligibility.service.utility.exceptions.SalsaInternalResourceNotAvailableException;
import com.lloydsbanking.salsa.eligibility.service.utility.exceptions.SalsaInternalServiceException;
import com.lloydsbanking.salsa.soap.cbs.e591.objects.DecnSubGp;
import lb_gbo_sales.messages.RequestHeader;
import org.springframework.beans.factory.annotation.Autowired;

import javax.xml.datatype.XMLGregorianCalendar;

public class CR062CBSDecisionCodeCheckRule implements CSTEligibilityRule {
    @Autowired
    CustomerDecisionDetailsRetriever customerDecisionDetailsRetriever;

    @Autowired
    AppGroupRetriever appGroupRetriever;

    private static final boolean IS_WZ_REQUEST = true;

    @Override
    public EligibilityDecision evaluate(RuleDataHolder ruleDataHolder, XMLGregorianCalendar birthDate, String sortCode, String customerId) throws EligibilityException {
        RequestHeader header = ruleDataHolder.getHeader();
        String threshold = ruleDataHolder.getRuleParamValue();
        String customerNum = ruleDataHolder.getCustomerDetails().getCustomerNumber();
        DecnSubGp decnSubGp;
        String cbsDecisionCd = "";
        String shadowLimit = "";
        String riskBand = "";
        try {
            decnSubGp = customerDecisionDetailsRetriever.getCustomerDecisionDetails(header, customerNum, appGroupRetriever.callRetrieveCBSAppGroup(header, sortCode, IS_WZ_REQUEST));
        }
        catch (SalsaInternalServiceException | SalsaInternalResourceNotAvailableException e) {
            throw new EligibilityException(e);
        }

        if (null != decnSubGp) {
            cbsDecisionCd = decnSubGp.getDcnCdCarLoanFinancIn();
            shadowLimit = decnSubGp.getCarLoanMnhShdwlmtIn();
            riskBand = String.valueOf(decnSubGp.getRskBndCdCarLoanIn());
        }

        if (!threshold.equals(cbsDecisionCd)) {
            return new EligibilityDecision(DeclineReasons.CR062_DECLINE_REASON, shadowLimit, riskBand);
        }
        return new EligibilityDecision(true, shadowLimit, riskBand);
    }
}

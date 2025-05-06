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
import org.apache.cxf.common.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import javax.xml.datatype.XMLGregorianCalendar;

public class CR067ShadowLimitCheckRule implements CSTEligibilityRule {
    @Autowired
    CustomerDecisionDetailsRetriever customerDecisionDetailsRetriever;

    @Autowired
    AppGroupRetriever appGroupRetriever;

    private static final boolean IS_WZ_REQUEST = true;

    @Override
    public EligibilityDecision evaluate(RuleDataHolder ruleDataHolder, XMLGregorianCalendar birthDate, String sortCode, String customerId) throws EligibilityException {
        RequestHeader header = ruleDataHolder.getHeader();
        float threshold = Float.parseFloat(ruleDataHolder.getRuleParamValue());
        String customerNum = ruleDataHolder.getCustomerDetails().getCustomerNumber();
        DecnSubGp decnSubGp;
        float shadowLimit = 0;
        String riskBand = "";
        try {
            decnSubGp = customerDecisionDetailsRetriever.getCustomerDecisionDetails(header, customerNum, appGroupRetriever.callRetrieveCBSAppGroup(header, sortCode, IS_WZ_REQUEST));
        }
        catch (SalsaInternalServiceException | SalsaInternalResourceNotAvailableException e) {
            throw new EligibilityException(e);
        }

        if (null != decnSubGp) {
            if (!StringUtils.isEmpty(decnSubGp.getCarLoanMnhShdwlmtIn())) {
                shadowLimit = Float.parseFloat(decnSubGp.getCarLoanMnhShdwlmtIn());
            }
            riskBand = String.valueOf(decnSubGp.getRskBndCdCarLoanIn());
        }

        if (shadowLimit > threshold) {
            return new EligibilityDecision(true, String.valueOf(shadowLimit), riskBand);
        }
        return new EligibilityDecision(DeclineReasons.CR067_DECLINE_REASON, String.valueOf(shadowLimit), riskBand);
    }
}

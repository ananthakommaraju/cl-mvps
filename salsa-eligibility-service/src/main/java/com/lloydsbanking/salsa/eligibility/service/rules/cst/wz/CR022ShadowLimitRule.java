package com.lloydsbanking.salsa.eligibility.service.rules.cst.wz;

import com.lloydsbanking.salsa.eligibility.service.downstream.AppGroupRetriever;
import com.lloydsbanking.salsa.eligibility.service.downstream.ShadowLimitRetriever;
import com.lloydsbanking.salsa.eligibility.service.questions.QuestionIsCurrentAccount;
import com.lloydsbanking.salsa.eligibility.service.rules.common.EligibilityDecision;
import com.lloydsbanking.salsa.eligibility.service.rules.common.RuleDataHolder;
import com.lloydsbanking.salsa.eligibility.service.rules.cst.CSTEligibilityRule;
import com.lloydsbanking.salsa.eligibility.service.utility.EligibilityException;
import com.lloydsbanking.salsa.eligibility.service.utility.constants.DeclineReasons;
import com.lloydsbanking.salsa.eligibility.service.utility.exceptions.SalsaInternalResourceNotAvailableException;
import com.lloydsbanking.salsa.eligibility.service.utility.exceptions.SalsaInternalServiceException;
import lb_gbo_sales.messages.RequestHeader;
import org.apache.cxf.common.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import javax.xml.datatype.XMLGregorianCalendar;

public class CR022ShadowLimitRule implements CSTEligibilityRule {
    @Autowired
    ShadowLimitRetriever shadowLimitRetriever;

    @Autowired
    AppGroupRetriever appGroupRetriever;

    private static final boolean IS_WZ_REQUEST = true;

    @Override
    public EligibilityDecision evaluate(RuleDataHolder ruleDataHolder, XMLGregorianCalendar birthDate, String sortCode, String customerId) throws EligibilityException {

        RequestHeader header = ruleDataHolder.getHeader();
        String shadowLimit;
        String customerNum = "";
        if (null != ruleDataHolder.getCustomerDetails()) {
            customerNum = ruleDataHolder.getCustomerDetails().getCustomerNumber();
        }
        try {
            shadowLimit = shadowLimitRetriever.getShadowLimit(header, null, customerNum, appGroupRetriever.callRetrieveCBSAppGroup(header, sortCode, IS_WZ_REQUEST));
        }
        catch (SalsaInternalServiceException | SalsaInternalResourceNotAvailableException e) {
            throw new EligibilityException(e);
        }

        if (StringUtils.isEmpty(shadowLimit) || 0.0 == (Double.valueOf(shadowLimit))) {
            if (QuestionIsCurrentAccount.pose().givenAProductList(ruleDataHolder.getProductArrangements()).ask()) {
                return new EligibilityDecision(DeclineReasons.CR022_DECLINE_REASON);
            }
        }
        return new EligibilityDecision(true);
    }

}

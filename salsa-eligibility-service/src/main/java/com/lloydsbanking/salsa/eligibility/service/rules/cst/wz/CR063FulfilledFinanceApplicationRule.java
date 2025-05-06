package com.lloydsbanking.salsa.eligibility.service.rules.cst.wz;

import com.lloydsbanking.salsa.eligibility.service.downstream.EligibilityPAMRetriever;
import com.lloydsbanking.salsa.eligibility.service.rules.common.EligibilityDecision;
import com.lloydsbanking.salsa.eligibility.service.rules.common.RuleDataHolder;
import com.lloydsbanking.salsa.eligibility.service.rules.cst.CSTEligibilityRule;
import com.lloydsbanking.salsa.eligibility.service.utility.constants.DeclineReasons;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import javax.xml.datatype.XMLGregorianCalendar;

public class CR063FulfilledFinanceApplicationRule implements CSTEligibilityRule {
    static final Logger LOGGER = Logger.getLogger(CR063FulfilledFinanceApplicationRule.class);

    @Autowired
    EligibilityPAMRetriever eligibilityPAMRetriever;

    private static final int NO_FULFILLED_FINANCE_APPLICATION = 0;

    @Override
    public EligibilityDecision evaluate(RuleDataHolder ruleDataHolder, XMLGregorianCalendar birthDate, String sortCode, String customerId) {
        int noOfApp = eligibilityPAMRetriever.getNumberOfFulfilledFinanceApplication(customerId);
        LOGGER.info("ApplicationCount returned by RetrievePamService: countOfApplicationExistForCustomer: " + noOfApp);
        if (noOfApp == NO_FULFILLED_FINANCE_APPLICATION) {
            return new EligibilityDecision(true);
        }
        else {
            return new EligibilityDecision(DeclineReasons.CR063_DECLINE_REASON);
        }
    }
}

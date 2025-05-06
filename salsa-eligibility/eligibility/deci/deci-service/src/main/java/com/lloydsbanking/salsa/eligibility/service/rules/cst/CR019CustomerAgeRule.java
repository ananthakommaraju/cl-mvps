package com.lloydsbanking.salsa.eligibility.service.rules.cst;


import com.lloydsbanking.salsa.eligibility.service.rules.common.EligibilityDecision;
import com.lloydsbanking.salsa.eligibility.service.rules.common.RuleDataHolder;
import com.lloydsbanking.salsa.eligibility.service.utility.DateUtility;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import javax.xml.datatype.XMLGregorianCalendar;


public class CR019CustomerAgeRule implements CSTEligibilityRule {

    private static final Logger LOGGER = Logger.getLogger(CR019CustomerAgeRule.class);

    @Autowired
    DateUtility dateUtility;

    @Override
    public EligibilityDecision evaluate(RuleDataHolder ruleDataHolder, XMLGregorianCalendar birthDate, String sortCode, String customerId) {
        String threshold = ruleDataHolder.getRuleParamValue();
        int age = 0;
        int ageThreshold = Integer.valueOf(threshold).intValue();
        if (null != birthDate) {
            age = dateUtility.calculateIndividualAge(birthDate);
        }

        if (age < ageThreshold) {
            return new EligibilityDecision("Customer cannot be younger than " + threshold + " years.");
        }
        return new EligibilityDecision(true);
    }

}

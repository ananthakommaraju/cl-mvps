package com.lloydsbanking.salsa.eligibility.service.rules.cst;


import com.lloydsbanking.salsa.eligibility.service.rules.common.EligibilityDecision;
import com.lloydsbanking.salsa.eligibility.service.rules.common.RuleDataHolder;
import com.lloydsbanking.salsa.eligibility.service.utility.DateUtility;
import org.springframework.beans.factory.annotation.Autowired;

import javax.xml.datatype.XMLGregorianCalendar;


public class CR001CustomerAgeRule implements CSTEligibilityRule {

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

        if (birthDate == null || age > ageThreshold) {
            return new EligibilityDecision("Customer's age is null or Customer cannot be older than " + threshold + " years.");
        }
        return new EligibilityDecision(true);
    }
}

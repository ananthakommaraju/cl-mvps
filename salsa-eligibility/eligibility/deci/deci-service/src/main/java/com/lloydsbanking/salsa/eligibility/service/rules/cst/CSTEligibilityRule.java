package com.lloydsbanking.salsa.eligibility.service.rules.cst;

import com.lloydsbanking.salsa.eligibility.service.utility.EligibilityException;
import com.lloydsbanking.salsa.eligibility.service.rules.common.EligibilityDecision;
import com.lloydsbanking.salsa.eligibility.service.rules.common.RuleDataHolder;

import javax.xml.datatype.XMLGregorianCalendar;

public interface CSTEligibilityRule {
    EligibilityDecision evaluate(RuleDataHolder ruleDataHolder, XMLGregorianCalendar birthDate, String sortCode, String customerId) throws EligibilityException;
}


package com.lloydsbanking.salsa.eligibility.service.rules.cst.wz;


import com.lloydsbanking.salsa.eligibility.service.utility.EligibilityException;
import com.lloydsbanking.salsa.eligibility.service.questions.QuestionIsIndividualANationalOf;
import com.lloydsbanking.salsa.eligibility.service.rules.common.EligibilityDecision;
import com.lloydsbanking.salsa.eligibility.service.rules.common.RuleDataHolder;
import com.lloydsbanking.salsa.eligibility.service.rules.cst.CSTEligibilityRule;
import com.lloydsbanking.salsa.eligibility.service.utility.constants.DeclineReasons;
import lib_sim_bo.businessobjects.Individual;

import javax.xml.datatype.XMLGregorianCalendar;

public class CR039NationalityOfCustomerRule implements CSTEligibilityRule {

    @Override
    public EligibilityDecision evaluate(RuleDataHolder ruleDataHolder, XMLGregorianCalendar birthDate, String sortCode, String customerId) throws EligibilityException {
        String blockedNationality = ruleDataHolder.getRuleParamValue();
        Individual individual = ruleDataHolder.getCustomerDetails().getIsPlayedBy();
        if (QuestionIsIndividualANationalOf.pose().givenAnIndividual(individual).givenAValue(blockedNationality).ask()) {
            return new EligibilityDecision(true);
        }
        else {
            return new EligibilityDecision(DeclineReasons.CR039_DECLINE_REASON);
        }
    }
}

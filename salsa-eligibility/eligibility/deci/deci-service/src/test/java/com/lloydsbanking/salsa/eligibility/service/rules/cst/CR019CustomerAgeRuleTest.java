package com.lloydsbanking.salsa.eligibility.service.rules.cst;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.downstream.ref.model.RefInstructionRulesDto;
import com.lloydsbanking.salsa.eligibility.service.rules.common.EligibilityDecision;
import com.lloydsbanking.salsa.eligibility.service.rules.common.RuleDataHolder;
import com.lloydsbanking.salsa.eligibility.service.utility.DateUtility;
import lb_gbo_sales.interfaces.ia_determineelegiblecustomerinstructions.DetermineEligibleInstructionsExternalBusinessErrorMsg;
import lb_gbo_sales.interfaces.ia_determineelegiblecustomerinstructions.DetermineEligibleInstructionsInternalServiceErrorMsg;
import lb_gbo_sales.interfaces.ia_determineelegiblecustomerinstructions.DetermineEligibleInstructionsResourceNotAvailableErrorMsg;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Category(UnitTest.class)
public class CR019CustomerAgeRuleTest {

    private CR019CustomerAgeRule rule;
    private EligibilityDecision testEligibility;

    @Before
    public void before() {
        rule = new CR019CustomerAgeRule();
        rule.dateUtility = mock(DateUtility.class);
    }

    @Test
    public void testCR019CustomerAgeRuleFails() throws DetermineEligibleInstructionsInternalServiceErrorMsg, DetermineEligibleInstructionsResourceNotAvailableErrorMsg, DetermineEligibleInstructionsExternalBusinessErrorMsg {


        DatatypeFactory datatypeFactory = null;

        try {
            datatypeFactory = DatatypeFactory.newInstance();
        }
        catch (DatatypeConfigurationException e) {
            e.printStackTrace();
        }
        RefInstructionRulesDto rulesDto = new RefInstructionRulesDto();
        rulesDto.setRuleParamValue("11");
        rulesDto.setCmsReason("CR012");
        rulesDto.setRule("CR019");

        RuleDataHolder ruleDataHolder = new RuleDataHolder();
        ruleDataHolder.setRuleParamValue(rulesDto.getRuleParamValue());
        ruleDataHolder.setRule(rulesDto.getRule());

        when(rule.dateUtility.calculateIndividualAge(datatypeFactory.newXMLGregorianCalendar("2000-05-05"))).thenReturn(10);

        testEligibility = rule.evaluate(ruleDataHolder, datatypeFactory.newXMLGregorianCalendar("2000-05-05"), null, null);

        assertFalse(testEligibility.isEligible());
        assertEquals("Customer cannot be younger than 11 years.", testEligibility.getReasonText());
    }

    @Test
    public void testCR019CustomerAgeRulePasses() throws DetermineEligibleInstructionsInternalServiceErrorMsg, DetermineEligibleInstructionsResourceNotAvailableErrorMsg, DetermineEligibleInstructionsExternalBusinessErrorMsg {


        DatatypeFactory datatypeFactory = null;

        try {
            datatypeFactory = DatatypeFactory.newInstance();
        }
        catch (DatatypeConfigurationException e) {
            e.printStackTrace();
        }
        RefInstructionRulesDto rulesDto = new RefInstructionRulesDto();
        rulesDto.setRuleParamValue("11");
        rulesDto.setCmsReason("CR012");
        rulesDto.setRule("CR019");

        RuleDataHolder ruleDataHolder = new RuleDataHolder();
        ruleDataHolder.setRuleParamValue(rulesDto.getRuleParamValue());
        ruleDataHolder.setRule(rulesDto.getRule());


        when(rule.dateUtility.calculateIndividualAge(datatypeFactory.newXMLGregorianCalendar("2000-05-05"))).thenReturn(15);

        testEligibility = rule.evaluate(ruleDataHolder, datatypeFactory.newXMLGregorianCalendar("2000-05-05"), null, null);
        assertTrue(testEligibility.isEligible());
        assertNull(testEligibility.getReasonText());

    }


}
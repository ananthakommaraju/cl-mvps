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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Category(UnitTest.class)
public class CR002CustomerAgeRuleTest {

    private CR002CustomerAgeRule rule;

    private EligibilityDecision testEligibility;

    @Before
    public void before() {
        rule = new CR002CustomerAgeRule();

        rule.dateUtility = mock(DateUtility.class);

    }

    @Test
    public void shouldReturnDeclinedReasonTextWhenAgeIsLessThan18() throws DetermineEligibleInstructionsInternalServiceErrorMsg, DetermineEligibleInstructionsResourceNotAvailableErrorMsg, DetermineEligibleInstructionsExternalBusinessErrorMsg {


        DatatypeFactory datatypeFactory = null;

        try {
            datatypeFactory = DatatypeFactory.newInstance();
        }
        catch (DatatypeConfigurationException e) {
            e.printStackTrace();
        }
        RefInstructionRulesDto rulesDto = new RefInstructionRulesDto();
        rulesDto.setRuleParamValue("18");
        rulesDto.setCmsReason("CR012");
        rulesDto.setRule("CR002");


        when(rule.dateUtility.calculateIndividualAge(datatypeFactory.newXMLGregorianCalendar("1998-05-05"))).thenReturn(17);
        RuleDataHolder ruleDataHolder = new RuleDataHolder();
        ruleDataHolder.setRuleParamValue(rulesDto.getRuleParamValue());
        ruleDataHolder.setRule(rulesDto.getRule());
        testEligibility = rule.evaluate(ruleDataHolder, datatypeFactory.newXMLGregorianCalendar("1998-05-05"), null, null);

        assertFalse(testEligibility.isEligible());
        assertEquals("Customer's age is null or  Customer cannot be younger than 18 years.", testEligibility.getReasonText());
    }

    @Test
    public void shouldReturnNullWhenAgeIsGreaterThan18() throws DetermineEligibleInstructionsInternalServiceErrorMsg, DetermineEligibleInstructionsResourceNotAvailableErrorMsg, DetermineEligibleInstructionsExternalBusinessErrorMsg {


        DatatypeFactory datatypeFactory = null;

        try {
            datatypeFactory = DatatypeFactory.newInstance();
        }
        catch (DatatypeConfigurationException e) {
            e.printStackTrace();
        }
        RefInstructionRulesDto rulesDto = new RefInstructionRulesDto();
        rulesDto.setRuleParamValue("18");
        rulesDto.setCmsReason("CR012");
        rulesDto.setRule("CR002");


        when(rule.dateUtility.calculateIndividualAge(datatypeFactory.newXMLGregorianCalendar("1980-05-05"))).thenReturn(34);
        RuleDataHolder ruleDataHolder = new RuleDataHolder();
        ruleDataHolder.setRuleParamValue(rulesDto.getRuleParamValue());
        ruleDataHolder.setRule(rulesDto.getRule());
        testEligibility = rule.evaluate(ruleDataHolder, datatypeFactory.newXMLGregorianCalendar("1980-05-05"), null, null);

        assertTrue(testEligibility.isEligible());
        assertNull(testEligibility.getReasonText());
    }

}
package com.lloydsbanking.salsa.eligibility.service.rules.cst;


import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.downstream.ref.model.RefInstructionRulesDto;
import com.lloydsbanking.salsa.eligibility.TestDataHelper;
import com.lloydsbanking.salsa.eligibility.service.rules.common.EligibilityDecision;
import com.lloydsbanking.salsa.eligibility.service.rules.common.RuleDataHolder;
import com.lloydsbanking.salsa.eligibility.service.utility.DateUtility;
import lb_gbo_sales.businessobjects.CustomerInstruction;
import lb_gbo_sales.messages.DetermineElegibileInstructionsRequest;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


@Category(UnitTest.class)
public class CR001CustomerAgeRuleTest {

    private TestDataHelper testDataHelper;

    private CR001CustomerAgeRule rule;

    private EligibilityDecision testEligibility;


    DetermineElegibileInstructionsRequest upstreamRequest;

    CustomerInstruction customerInstruction;

    @Before
    public void setUp() {

        rule = new CR001CustomerAgeRule();

        testDataHelper = new TestDataHelper();

        upstreamRequest = testDataHelper.createEligibilityRequest("abc", testDataHelper.TEST_OCIS_ID, testDataHelper.TEST_RETAIL_CHANNEL_ID, testDataHelper.TEST_CONTACT_POINT_ID);

        rule.dateUtility = mock(DateUtility.class);

        customerInstruction = new CustomerInstruction();

    }

    @Test
    public void shouldReturnDeclinedReasonTextIfCustomerOlderThan75() {

        DatatypeFactory datatypeFactory = null;

        try {
            datatypeFactory = DatatypeFactory.newInstance();
        }
        catch (DatatypeConfigurationException e) {
            e.printStackTrace();
        }
        RefInstructionRulesDto rulesDto = new RefInstructionRulesDto();
        rulesDto.setRuleParamValue("75");
        rulesDto.setCmsReason("CR012");
        rulesDto.setRule("CR001");


        when(rule.dateUtility.calculateIndividualAge(datatypeFactory.newXMLGregorianCalendar("1920-05-05"))).thenReturn(95);
        RuleDataHolder ruleDataHolder = new RuleDataHolder();
        ruleDataHolder.setRuleParamValue(rulesDto.getRuleParamValue());
        ruleDataHolder.setRule(rulesDto.getRule());
        ruleDataHolder.setHeader(upstreamRequest.getHeader());
        testEligibility = rule.evaluate(ruleDataHolder, datatypeFactory.newXMLGregorianCalendar("1920-05-05"), null, null);

        assertFalse(testEligibility.isEligible());
        assertEquals("Customer's age is null or Customer cannot be older than 75 years.", testEligibility.getReasonText());
    }

    @Test
    public void shouldReturnDeclinedReasonTextIfCustomerIsNotProvided() {

        RefInstructionRulesDto rulesDto = new RefInstructionRulesDto();
        rulesDto.setRuleParamValue("75");
        rulesDto.setCmsReason("CR012");
        rulesDto.setRule("CR001");


        RuleDataHolder ruleDataHolder = new RuleDataHolder();
        ruleDataHolder.setRuleParamValue(rulesDto.getRuleParamValue());
        ruleDataHolder.setRule(rulesDto.getRule());
        ruleDataHolder.setHeader(upstreamRequest.getHeader());
        testEligibility = rule.evaluate(ruleDataHolder, null, null, null);

        assertFalse(testEligibility.isEligible());
        assertEquals("Customer's age is null or Customer cannot be older than 75 years.", testEligibility.getReasonText());
    }

    @Test
    public void shouldReturnNullWhenCustomerAgeIsLessThan75() {

        DatatypeFactory datatypeFactory = null;

        try {
            datatypeFactory = DatatypeFactory.newInstance();
        }
        catch (DatatypeConfigurationException e) {
            e.printStackTrace();
        }
        RefInstructionRulesDto rulesDto = new RefInstructionRulesDto();
        rulesDto.setRuleParamValue("75");
        rulesDto.setCmsReason("CR012");
        rulesDto.setRule("CR001");


        when(rule.dateUtility.calculateIndividualAge(datatypeFactory.newXMLGregorianCalendar("1980-05-05"))).thenReturn(35);
        RuleDataHolder ruleDataHolder = new RuleDataHolder();
        ruleDataHolder.setRuleParamValue(rulesDto.getRuleParamValue());
        ruleDataHolder.setRule(rulesDto.getRule());
        ruleDataHolder.setHeader(upstreamRequest.getHeader());
        testEligibility = rule.evaluate(ruleDataHolder, datatypeFactory.newXMLGregorianCalendar("1980-05-05"), null, null);

        assertTrue(testEligibility.isEligible());
        assertNull(testEligibility.getReasonText());
    }

}

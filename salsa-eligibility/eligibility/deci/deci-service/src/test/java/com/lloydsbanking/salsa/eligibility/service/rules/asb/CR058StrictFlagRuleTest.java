package com.lloydsbanking.salsa.eligibility.service.rules.asb;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.downstream.ref.model.RefInstructionRulesDto;
import com.lloydsbanking.salsa.eligibility.TestDataHelper;
import com.lloydsbanking.salsa.eligibility.service.utility.EligibilityException;
import com.lloydsbanking.salsa.eligibility.service.downstream.AppGroupRetriever;
import com.lloydsbanking.salsa.eligibility.service.downstream.ShadowLimitRetriever;
import com.lloydsbanking.salsa.eligibility.service.rules.common.EligibilityDecision;
import com.lloydsbanking.salsa.eligibility.service.rules.common.RuleDataHolder;
import com.lloydsbanking.salsa.eligibility.service.utility.exceptions.SalsaInternalResourceNotAvailableException;
import com.lloydsbanking.salsa.eligibility.service.utility.exceptions.SalsaInternalServiceException;
import lb_gbo_sales.businessobjects.CustomerInstruction;
import lb_gbo_sales.interfaces.ia_determineelegiblecustomerinstructions.DetermineEligibleInstructionsExternalServiceErrorMsg;
import lb_gbo_sales.messages.DetermineElegibileInstructionsRequest;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import javax.xml.datatype.DatatypeConfigurationException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Category(UnitTest.class)
public class CR058StrictFlagRuleTest {
    TestDataHelper testDataHelper;

    CR058StrictFlagRule rule;

    DetermineElegibileInstructionsRequest upstreamRequest;

    CustomerInstruction customerInstruction;

    RefInstructionRulesDto rulesDto;

    @Before
    public void setUp() {

        rule = new CR058StrictFlagRule();
        testDataHelper = new TestDataHelper();
        upstreamRequest = testDataHelper.createEligibilityRequestForLoans("P_BODA_RBB", TestDataHelper.TEST_OCIS_ID, "IBH", TestDataHelper.TEST_CONTACT_POINT_ID, 1989, 01, 01);

        rule.shadowLimitRetriever = mock(ShadowLimitRetriever.class);
        rule.appGroupRetriever = mock(AppGroupRetriever.class);
        customerInstruction = new CustomerInstruction();
        rulesDto = new RefInstructionRulesDto();
        rulesDto.setRuleParamValue("2");
        rulesDto.setCmsReason("CR058");
    }


    @Test
    public void testCR058isSuccessfulStrictFlagNotEqualToThreshold() throws DetermineEligibleInstructionsExternalServiceErrorMsg, DatatypeConfigurationException, SalsaInternalResourceNotAvailableException, SalsaInternalServiceException, EligibilityException {
        when(rule.appGroupRetriever.callRetrieveCBSAppGroup(upstreamRequest.getHeader(), "770077", false)).thenReturn("1");
        when(rule.shadowLimitRetriever.getStrictFlag(upstreamRequest.getHeader(), "770077", "12345", "1")).thenReturn(1);

        RuleDataHolder ruleDataHolder = new RuleDataHolder();
        ruleDataHolder.setRule(rulesDto.getRule());
        ruleDataHolder.setRuleParamValue(rulesDto.getRuleParamValue());

        EligibilityDecision evaluate = rule.evaluate(ruleDataHolder, null, "770077", "12345");
        assertNull(evaluate.getReasonText());
        assertTrue(evaluate.isEligible());
    }

    @Test
    public void testCR058isUnSuccessfulStrictFlagEqualToThreshold() throws DetermineEligibleInstructionsExternalServiceErrorMsg, DatatypeConfigurationException, SalsaInternalResourceNotAvailableException, SalsaInternalServiceException, EligibilityException {
        when(rule.appGroupRetriever.callRetrieveCBSAppGroup(upstreamRequest.getHeader(), "770077", false)).thenReturn("1");
        when(rule.shadowLimitRetriever.getStrictFlag(upstreamRequest.getHeader(), "770077", "12345", "1")).thenReturn(2);

        RuleDataHolder ruleDataHolder = new RuleDataHolder();
        ruleDataHolder.setRule(rulesDto.getRule());
        ruleDataHolder.setRuleParamValue(rulesDto.getRuleParamValue());
        ruleDataHolder.setHeader(upstreamRequest.getHeader());


        EligibilityDecision evaluate = rule.evaluate(ruleDataHolder, null, "770077", "12345");

        assertEquals("Customer has strict flag set on one or more of the current account holding(s)", evaluate.getReasonText());
        assertFalse(evaluate.isEligible());

    }
}


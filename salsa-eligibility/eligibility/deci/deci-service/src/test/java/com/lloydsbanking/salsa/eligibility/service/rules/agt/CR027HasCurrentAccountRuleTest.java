package com.lloydsbanking.salsa.eligibility.service.rules.agt;


import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.downstream.ref.model.RefInstructionRulesDto;
import com.lloydsbanking.salsa.eligibility.TestDataHelper;
import com.lloydsbanking.salsa.eligibility.service.utility.EligibilityException;
import com.lloydsbanking.salsa.eligibility.service.rules.common.EligibilityDecision;
import com.lloydsbanking.salsa.eligibility.service.rules.common.RuleDataHolder;
import com.lloydsbanking.salsa.eligibility.service.utility.exceptions.SalsaInternalResourceNotAvailableException;
import com.lloydsbanking.salsa.eligibility.service.utility.exceptions.SalsaInternalServiceException;
import lb_gbo_sales.ProductArrangement;
import lb_gbo_sales.businessobjects.CustomerInstruction;
import lb_gbo_sales.messages.DetermineElegibileInstructionsRequest;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Category(UnitTest.class)
public class CR027HasCurrentAccountRuleTest {

    private TestDataHelper testDataHelper;

    private CR027HasCurrentAccountRule rule;


    DetermineElegibileInstructionsRequest upstreamRequest;

    CustomerInstruction customerInstruction;

    List<ProductArrangement> customerArrangements;

    @Before
    public void setUp() {

        rule = new CR027HasCurrentAccountRule();

        testDataHelper = new TestDataHelper();

        upstreamRequest = testDataHelper.createEligibilityRequest("abc", testDataHelper.TEST_OCIS_ID, testDataHelper.TEST_RETAIL_CHANNEL_ID, testDataHelper.TEST_CONTACT_POINT_ID);


        customerInstruction = new CustomerInstruction();

        rule.checkLoanAndCurrentAccountType = mock(CheckLoanAndCurrentAccountType.class);

        customerArrangements = new ArrayList();

    }

    @Test
    public void testCR027HasCurrentAccountRuleFails() throws SalsaInternalResourceNotAvailableException, SalsaInternalServiceException, EligibilityException {


        RefInstructionRulesDto rulesDto = new RefInstructionRulesDto();

        rulesDto.setCmsReason("CR012");
        rulesDto.setRule("CR027");

        when(rule.checkLoanAndCurrentAccountType.hasCurrentAccountOrLoggedInChannelLoan(upstreamRequest.getHeader(), "S")).thenReturn(false);
        RuleDataHolder ruleDataHolder = new RuleDataHolder();
        ruleDataHolder.setHeader(upstreamRequest.getHeader());
        ruleDataHolder.setRule(rulesDto.getRule());
        EligibilityDecision evaluate = rule.evaluate(ruleDataHolder);

        assertEquals("Customer doesn’t have current account of logged in channel", evaluate.getReasonText());
        assertFalse(evaluate.isEligible());
    }

    @Test
    public void testCR027HasCurrentAccountRulePasses() throws SalsaInternalResourceNotAvailableException, SalsaInternalServiceException, EligibilityException {

        RefInstructionRulesDto rulesDto = new RefInstructionRulesDto();

        rulesDto.setCmsReason("CR012");
        rulesDto.setRule("CR027");

        when(rule.checkLoanAndCurrentAccountType.hasCurrentAccountOrLoggedInChannelLoan(upstreamRequest.getHeader(), "C")).thenReturn(true);
        RuleDataHolder ruleDataHolder = new RuleDataHolder();
        ruleDataHolder.setHeader(upstreamRequest.getHeader());
        ruleDataHolder.setRule(rulesDto.getRule());
        EligibilityDecision evaluate = rule.evaluate(ruleDataHolder);

        assertNull(evaluate.getReasonText());
        assertTrue(evaluate.isEligible());
    }

}



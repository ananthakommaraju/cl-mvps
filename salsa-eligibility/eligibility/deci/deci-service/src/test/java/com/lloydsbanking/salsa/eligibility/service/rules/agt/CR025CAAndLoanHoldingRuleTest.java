package com.lloydsbanking.salsa.eligibility.service.rules.agt;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.downstream.ref.model.RefInstructionRulesDto;
import com.lloydsbanking.salsa.eligibility.TestDataHelper;
import com.lloydsbanking.salsa.eligibility.service.utility.EligibilityException;
import com.lloydsbanking.salsa.eligibility.service.downstream.ProductHoldingRetriever;
import com.lloydsbanking.salsa.eligibility.service.rules.common.EligibilityDecision;
import com.lloydsbanking.salsa.eligibility.service.rules.common.RuleDataHolder;
import com.lloydsbanking.salsa.eligibility.service.utility.exceptions.SalsaInternalResourceNotAvailableException;
import com.lloydsbanking.salsa.eligibility.service.utility.exceptions.SalsaInternalServiceException;
import com.lloydsbanking.salsa.soap.ocis.f336.objects.ProductPartyData;
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
public class CR025CAAndLoanHoldingRuleTest {

    private CR025CAAndLoanHoldingRule rule;

    TestDataHelper testDataHelper;

    DetermineElegibileInstructionsRequest upstreamRequest;

    CustomerInstruction customerInstruction;

    List<ProductArrangement> customerArrangements;

    RuleDataHolder ruleDataHolder;

    List<ProductPartyData> productPartyDatas = new ArrayList<>();

    RefInstructionRulesDto rulesDto;

    @Before
    public void setUp() {
        rule = new CR025CAAndLoanHoldingRule();
        testDataHelper = new TestDataHelper();
        upstreamRequest = testDataHelper.createEligibilityRequest("abc", testDataHelper.TEST_OCIS_ID, testDataHelper.TEST_RETAIL_CHANNEL_ID, testDataHelper.TEST_CONTACT_POINT_ID);

        rule.checkLoanAndCurrentAccountType = mock(CheckLoanAndCurrentAccountType.class);
        rule.productHoldingRetriever = mock(ProductHoldingRetriever.class);
        customerArrangements = new ArrayList<>();

        rulesDto = new RefInstructionRulesDto();
        rulesDto.setCmsReason("CR025");
        rulesDto.setRule("CR025");


        ProductPartyData productPartyData = new ProductPartyData();
        productPartyData.setProdGroupId(2);
        productPartyDatas.add(productPartyData);

        ruleDataHolder = new RuleDataHolder();
        ruleDataHolder.setHeader(upstreamRequest.getHeader());
        ruleDataHolder.setRule(rulesDto.getRule());

    }

    @Test
    public void testCR025CAAndLoanHoldingRuleFailsCaseOne() throws SalsaInternalResourceNotAvailableException, SalsaInternalServiceException, EligibilityException {

        customerInstruction = new CustomerInstruction();

        when(rule.productHoldingRetriever.getProductHoldings(upstreamRequest.getHeader())).thenReturn(productPartyDatas);
        when(rule.checkLoanAndCurrentAccountType.hasFEPSLoan(productPartyDatas)).thenReturn(true);
        when(rule.checkLoanAndCurrentAccountType.hasCurrentAccountOrLoggedInChannelLoan(upstreamRequest.getHeader(),"L")).thenReturn(true);
        when(rule.checkLoanAndCurrentAccountType.hasCurrentAccountOrLoggedInChannelLoan(upstreamRequest.getHeader(), "C")).thenReturn(false);

        EligibilityDecision evaluate = rule.evaluate(ruleDataHolder);
        assertEquals("Customer doesn’t have current account of logged in channel but has a loan", evaluate.getReasonText());
        assertFalse(evaluate.isEligible());
    }

    @Test
    public void testCR025CAAndLoanHoldingRuleFailsCaseTwo() throws SalsaInternalResourceNotAvailableException, SalsaInternalServiceException, EligibilityException {



        customerInstruction = new CustomerInstruction();



        when(rule.productHoldingRetriever.getProductHoldings(upstreamRequest.getHeader())).thenReturn(productPartyDatas);
        when(rule.checkLoanAndCurrentAccountType.hasFEPSLoan(productPartyDatas)).thenReturn(true);
        when(rule.checkLoanAndCurrentAccountType.hasCurrentAccountOrLoggedInChannelLoan(upstreamRequest.getHeader(),"L")).thenReturn(false);
        when(rule.checkLoanAndCurrentAccountType.hasCurrentAccountOrLoggedInChannelLoan(upstreamRequest.getHeader(), "C")).thenReturn(false);

        EligibilityDecision evaluate = rule.evaluate(ruleDataHolder);
        assertEquals("Customer doesn’t have current account of logged in channel but has a loan", evaluate.getReasonText());
        assertFalse(evaluate.isEligible());
    }

    @Test
    public void testCR025CAAndLoanHoldingRuleFailsCaseThree() throws SalsaInternalResourceNotAvailableException, SalsaInternalServiceException, EligibilityException {



        customerInstruction = new CustomerInstruction();



        when(rule.productHoldingRetriever.getProductHoldings(upstreamRequest.getHeader())).thenReturn(productPartyDatas);
        when(rule.checkLoanAndCurrentAccountType.hasFEPSLoan(productPartyDatas)).thenReturn(false);
        when(rule.checkLoanAndCurrentAccountType.hasCurrentAccountOrLoggedInChannelLoan(upstreamRequest.getHeader(),"L")).thenReturn(true);
        when(rule.checkLoanAndCurrentAccountType.hasCurrentAccountOrLoggedInChannelLoan(upstreamRequest.getHeader(), "C")).thenReturn(false);

        EligibilityDecision evaluate = rule.evaluate(ruleDataHolder);
        assertEquals("Customer doesn’t have current account of logged in channel but has a loan", evaluate.getReasonText());
        assertFalse(evaluate.isEligible());
    }
    @Test
    public void testCR025CAAndLoanHoldingRulePassesCaseOne() throws SalsaInternalResourceNotAvailableException, SalsaInternalServiceException, EligibilityException {



        customerInstruction = new CustomerInstruction();


        when(rule.productHoldingRetriever.getProductHoldings(upstreamRequest.getHeader())).thenReturn(productPartyDatas);
        when(rule.checkLoanAndCurrentAccountType.hasFEPSLoan(productPartyDatas)).thenReturn(true);
        when(rule.checkLoanAndCurrentAccountType.hasCurrentAccountOrLoggedInChannelLoan(upstreamRequest.getHeader(), "L")).thenReturn(true);
        when(rule.checkLoanAndCurrentAccountType.hasCurrentAccountOrLoggedInChannelLoan(upstreamRequest.getHeader(), "C")).thenReturn(true);

        EligibilityDecision evaluate = rule.evaluate(ruleDataHolder);

        assertNull(evaluate.getReasonText());
        assertTrue(evaluate.isEligible());
    }

    @Test
    public void testCR025CAAndLoanHoldingRulePassesCaseTwo() throws SalsaInternalResourceNotAvailableException, SalsaInternalServiceException, EligibilityException {



        customerInstruction = new CustomerInstruction();



        when(rule.productHoldingRetriever.getProductHoldings(upstreamRequest.getHeader())).thenReturn(productPartyDatas);
        when(rule.checkLoanAndCurrentAccountType.hasFEPSLoan(productPartyDatas)).thenReturn(true);
        when(rule.checkLoanAndCurrentAccountType.hasCurrentAccountOrLoggedInChannelLoan(upstreamRequest.getHeader(), "L")).thenReturn(false);
        when(rule.checkLoanAndCurrentAccountType.hasCurrentAccountOrLoggedInChannelLoan(upstreamRequest.getHeader(), "C")).thenReturn(true);

        EligibilityDecision evaluate = rule.evaluate(ruleDataHolder);

        assertNull(evaluate.getReasonText());
        assertTrue(evaluate.isEligible());
    }

    @Test
    public void testCR025CAAndLoanHoldingRulePassesCaseThree() throws SalsaInternalResourceNotAvailableException, SalsaInternalServiceException, EligibilityException {



        customerInstruction = new CustomerInstruction();



        when(rule.productHoldingRetriever.getProductHoldings(upstreamRequest.getHeader())).thenReturn(productPartyDatas);
        when(rule.checkLoanAndCurrentAccountType.hasFEPSLoan(productPartyDatas)).thenReturn(false);
        when(rule.checkLoanAndCurrentAccountType.hasCurrentAccountOrLoggedInChannelLoan(upstreamRequest.getHeader(), "L")).thenReturn(true);
        when(rule.checkLoanAndCurrentAccountType.hasCurrentAccountOrLoggedInChannelLoan(upstreamRequest.getHeader(), "C")).thenReturn(true);

        EligibilityDecision evaluate = rule.evaluate(ruleDataHolder);

        assertNull(evaluate.getReasonText());
        assertTrue(evaluate.isEligible());
    }

    @Test
    public void testCR025CAAndLoanHoldingRulePassesCaseFour() throws SalsaInternalResourceNotAvailableException, SalsaInternalServiceException, EligibilityException {



        customerInstruction = new CustomerInstruction();



        when(rule.productHoldingRetriever.getProductHoldings(upstreamRequest.getHeader())).thenReturn(productPartyDatas);
        when(rule.checkLoanAndCurrentAccountType.hasFEPSLoan(productPartyDatas)).thenReturn(false);
        when(rule.checkLoanAndCurrentAccountType.hasCurrentAccountOrLoggedInChannelLoan(upstreamRequest.getHeader(), "L")).thenReturn(false);
        when(rule.checkLoanAndCurrentAccountType.hasCurrentAccountOrLoggedInChannelLoan(upstreamRequest.getHeader(), "C")).thenReturn(true);

        EligibilityDecision evaluate = rule.evaluate(ruleDataHolder);

        assertNull(evaluate.getReasonText());
        assertTrue(evaluate.isEligible());
    }

    @Test
    public void testCR025CAAndLoanHoldingRulePassesCaseFive() throws SalsaInternalResourceNotAvailableException, SalsaInternalServiceException, EligibilityException {



        customerInstruction = new CustomerInstruction();



        when(rule.productHoldingRetriever.getProductHoldings(upstreamRequest.getHeader())).thenReturn(productPartyDatas);
        when(rule.checkLoanAndCurrentAccountType.hasFEPSLoan(productPartyDatas)).thenReturn(false);
        when(rule.checkLoanAndCurrentAccountType.hasCurrentAccountOrLoggedInChannelLoan(upstreamRequest.getHeader(), "L")).thenReturn(false);
        when(rule.checkLoanAndCurrentAccountType.hasCurrentAccountOrLoggedInChannelLoan(upstreamRequest.getHeader(), "C")).thenReturn(false);

        EligibilityDecision evaluate = rule.evaluate(ruleDataHolder);

        assertNull(evaluate.getReasonText());
        assertTrue(evaluate.isEligible());
    }
}





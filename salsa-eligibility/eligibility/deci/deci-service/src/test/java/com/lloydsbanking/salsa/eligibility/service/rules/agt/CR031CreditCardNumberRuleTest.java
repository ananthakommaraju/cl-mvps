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
public class CR031CreditCardNumberRuleTest {
    private TestDataHelper testDataHelper;

    private CR031CreditCardNumberRule rule;


    DetermineElegibileInstructionsRequest upstreamRequest;

    CustomerInstruction customerInstruction;

    List<ProductArrangement> customerArrangements;

    @Before
    public void setUp() {

        rule = new CR031CreditCardNumberRule();

        testDataHelper = new TestDataHelper();

        upstreamRequest = testDataHelper.createEligibilityRequest("abc", testDataHelper.TEST_OCIS_ID, testDataHelper.TEST_RETAIL_CHANNEL_ID, testDataHelper.TEST_CONTACT_POINT_ID);


        customerInstruction = new CustomerInstruction();

        rule.checkLoanAndCurrentAccountType = mock(CheckLoanAndCurrentAccountType.class);


        rule.productHoldingRetriever = mock(ProductHoldingRetriever.class);
        customerArrangements = new ArrayList<>();
    }

    @Test
    public void testCR031CreditCardNumberRuleFails() throws SalsaInternalResourceNotAvailableException, SalsaInternalServiceException, EligibilityException {


        RefInstructionRulesDto rulesDto = new RefInstructionRulesDto();

        rulesDto.setCmsReason("CR012");
        rulesDto.setRule("CR031");

        List<ProductPartyData> productPartyDatas = rule.productHoldingRetriever.getProductHoldings(upstreamRequest.getHeader());

        when(rule.checkLoanAndCurrentAccountType.existingCreditCardGroupCodeProduct(productPartyDatas)).thenReturn(2);
        RuleDataHolder ruleDataHolder = new RuleDataHolder();
        ruleDataHolder.setHeader(upstreamRequest.getHeader());
        ruleDataHolder.setRule(rulesDto.getRule());

        EligibilityDecision evaluate = rule.evaluate(ruleDataHolder);


        assertEquals("Customer has 2 or more credit cards", evaluate.getReasonText());
        assertFalse(evaluate.isEligible());
    }

    @Test
    public void testCR031CreditCardNumberRulePasses() throws SalsaInternalResourceNotAvailableException, SalsaInternalServiceException, EligibilityException {


        RefInstructionRulesDto rulesDto = new RefInstructionRulesDto();

        rulesDto.setCmsReason("CR012");
        rulesDto.setRule("CR031");

        List<ProductPartyData> productPartyDatas = rule.productHoldingRetriever.getProductHoldings(upstreamRequest.getHeader());

        when(rule.checkLoanAndCurrentAccountType.existingCreditCardGroupCodeProduct(productPartyDatas)).thenReturn(1);
        RuleDataHolder ruleDataHolder = new RuleDataHolder();
        ruleDataHolder.setHeader(upstreamRequest.getHeader());
        ruleDataHolder.setRule(rulesDto.getRule());
        EligibilityDecision evaluate = rule.evaluate(ruleDataHolder);
        assertNull(evaluate.getReasonText());
        assertTrue(evaluate.isEligible());
    }
}

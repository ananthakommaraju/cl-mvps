package com.lloydsbanking.salsa.eligibility.service.rules.agt;


import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.downstream.ref.model.RefInstructionRulesDto;
import com.lloydsbanking.salsa.eligibility.TestDataHelper;
import com.lloydsbanking.salsa.eligibility.service.utility.EligibilityException;
import com.lloydsbanking.salsa.eligibility.service.utility.ProductArrangementFacadeFactory;
import com.lloydsbanking.salsa.eligibility.service.rules.common.DeclineReasonAdder;
import com.lloydsbanking.salsa.eligibility.service.rules.common.EligibilityDecision;
import com.lloydsbanking.salsa.eligibility.service.rules.common.RuleDataHolder;
import com.lloydsbanking.salsa.eligibility.service.utility.exceptions.SalsaExternalServiceException;
import com.lloydsbanking.salsa.eligibility.service.utility.exceptions.SalsaInternalResourceNotAvailableException;
import com.lloydsbanking.salsa.eligibility.service.utility.exceptions.SalsaInternalServiceException;
import lb_gbo_sales.ProductArrangement;
import lb_gbo_sales.businessobjects.CreditCardFinanceServiceArrangement;
import lb_gbo_sales.businessobjects.CustomerInstruction;
import lb_gbo_sales.messages.DetermineElegibileInstructionsRequest;
import lb_gbo_sales.messages.RequestHeader;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

@Category(UnitTest.class)
public class CR012ExistingPaymentProtectionCoverRuleTest {

    CR012ExistingPaymentProtectionCoverRule rule;

    TestDataHelper testDataHelper;

    RequestHeader header;

    DetermineElegibileInstructionsRequest upstreamRequest;

    CustomerInstruction customerInstruction;

    RefInstructionRulesDto ruleDto;

    List<ProductArrangement> customerArrangements;

    ProductArrangement productArrangement;

    @Before
    public void setUp() {
        rule = new CR012ExistingPaymentProtectionCoverRule();
        testDataHelper = new TestDataHelper();
        rule.declineReasonAdder = new DeclineReasonAdder();
        upstreamRequest = testDataHelper.createEligibilityRequest("abc", testDataHelper.TEST_OCIS_ID, testDataHelper.TEST_RETAIL_CHANNEL_ID, testDataHelper.TEST_CONTACT_POINT_ID);
        header = upstreamRequest.getHeader();
        customerArrangements = new ArrayList<>();
        ruleDto = new RefInstructionRulesDto();

        customerInstruction = new CustomerInstruction();
        customerInstruction.setCustomerInstructionStatus("instruction");
        customerInstruction.setPriority(1);
        customerInstruction.setEligibilityIndicator(true);
    }

    @Test
    public void testCR012ExistingPaymentProtectionCoverRulePasses() throws SalsaInternalResourceNotAvailableException, SalsaInternalServiceException, SalsaExternalServiceException, EligibilityException {

        ruleDto.setRule("CR012");
        ruleDto.setCmsReason("CR012");
        ruleDto.setRuleParamValue("AAA");


        ProductArrangement productArrangement1 = new CreditCardFinanceServiceArrangement();
        productArrangement1.setParentInstructionMnemonic("AGT");
        productArrangement1.setHasEmbeddedInsurance(false);


        customerArrangements.add(productArrangement1);
        RuleDataHolder ruleDataHolder = new RuleDataHolder();
        ruleDataHolder.setRule(ruleDto.getRule());
        ruleDataHolder.setRuleParamValue(ruleDto.getRuleParamValue());
        ruleDataHolder.setProductArrangements(ProductArrangementFacadeFactory.createProductArrangementFacade(customerArrangements));
        ruleDataHolder.setHeader(upstreamRequest.getHeader());

        EligibilityDecision evaluate = rule.evaluate(ruleDataHolder);

        assertNull(evaluate.getReasonText());
        assertTrue(evaluate.isEligible());

    }

    @Test
    public void testCR012ExistingPaymentProtectionCoverRuleFails() throws SalsaInternalResourceNotAvailableException, SalsaInternalServiceException, SalsaExternalServiceException, EligibilityException {

        ruleDto.setRule("CR012");
        ruleDto.setCmsReason("CR012");
        ruleDto.setRuleParamValue("AAA");

        productArrangement = new ProductArrangement();
        productArrangement.setParentInstructionMnemonic("AGT");
        productArrangement.setHasEmbeddedInsurance(false);

        customerArrangements.add(productArrangement);

        RuleDataHolder ruleDataHolder = new RuleDataHolder();
        ruleDataHolder.setRule(ruleDto.getRule());
        ruleDataHolder.setRuleParamValue(ruleDto.getRuleParamValue());
        ruleDataHolder.setProductArrangements(ProductArrangementFacadeFactory.createProductArrangementFacade(customerArrangements));
        ruleDataHolder.setHeader(upstreamRequest.getHeader());

        EligibilityDecision evaluate = rule.evaluate(ruleDataHolder);

        assertEquals("Customer does not have an eligible product for PPC", evaluate.getReasonText());
        assertFalse(evaluate.isEligible());

    }
}

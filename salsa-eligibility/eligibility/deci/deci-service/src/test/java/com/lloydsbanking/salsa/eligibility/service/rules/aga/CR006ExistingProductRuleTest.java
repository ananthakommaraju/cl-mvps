package com.lloydsbanking.salsa.eligibility.service.rules.aga;


import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.downstream.ref.model.RefInstructionRulesDto;
import com.lloydsbanking.salsa.eligibility.TestDataHelper;
import com.lloydsbanking.salsa.eligibility.service.rules.common.EligibilityDecision;
import com.lloydsbanking.salsa.eligibility.service.rules.common.RuleDataHolder;
import com.lloydsbanking.salsa.eligibility.service.utility.EligibilityException;
import com.lloydsbanking.salsa.eligibility.service.utility.ProductArrangementFacadeFactory;
import com.lloydsbanking.salsa.eligibility.service.utility.exceptions.SalsaExternalServiceException;
import com.lloydsbanking.salsa.eligibility.service.utility.exceptions.SalsaInternalResourceNotAvailableException;
import com.lloydsbanking.salsa.eligibility.service.utility.exceptions.SalsaInternalServiceException;
import lb_gbo_sales.ProductArrangement;
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
public class CR006ExistingProductRuleTest {

    CR006ExistingProductRule rule;

    TestDataHelper testDataHelper;

    DetermineElegibileInstructionsRequest upstreamRequest;

    RequestHeader header;

    RefInstructionRulesDto rulesDto;

    CustomerInstruction customerInstruction;

    List<ProductArrangement> customerArrangements;


    @Before
    public void setUp() {

        rule = new CR006ExistingProductRule();
        testDataHelper = new TestDataHelper();
        upstreamRequest = testDataHelper.createEligibilityRequest("abc", testDataHelper.TEST_OCIS_ID, testDataHelper.TEST_RETAIL_CHANNEL_ID, testDataHelper.TEST_CONTACT_POINT_ID);
        header = upstreamRequest.getHeader();
        rulesDto = new RefInstructionRulesDto();
        customerInstruction = new CustomerInstruction();
        customerInstruction.setEligibilityIndicator(true);
        customerArrangements = new ArrayList<>();

    }

    @Test
    public void testCR006ExistingProductRuleFails() throws SalsaInternalResourceNotAvailableException, SalsaInternalServiceException, SalsaExternalServiceException, EligibilityException {
        String candidateInstruction = "ins";
        ProductArrangement productArrangement = new ProductArrangement();
        productArrangement.setInstructionMnemonic("ins");
        productArrangement.setParentInstructionMnemonic("AGA");
        customerArrangements.add(productArrangement);

        RuleDataHolder ruleDataHolder = new RuleDataHolder();
        ruleDataHolder.setRule("CR006");
        ruleDataHolder.setProductArrangements(ProductArrangementFacadeFactory.createProductArrangementFacade(customerArrangements));
        EligibilityDecision evaluate = rule.evaluate(candidateInstruction, ruleDataHolder);

        assertEquals("Customer cannot apply for a product of the same type as they already have.", evaluate.getReasonText());
        assertFalse(evaluate.isEligible());

    }

    @Test
    public void testCR006ExistingProductRulePasses() throws SalsaInternalResourceNotAvailableException, SalsaInternalServiceException, SalsaExternalServiceException, EligibilityException {
        String candidateInstruction = "c_ins";
        ProductArrangement productArrangement = new ProductArrangement();
        productArrangement.setInstructionMnemonic("ins");
        productArrangement.setParentInstructionMnemonic("AGA");
        customerArrangements.add(productArrangement);

        RuleDataHolder ruleDataHolder = new RuleDataHolder();
        ruleDataHolder.setRule("CR006");
        ruleDataHolder.setProductArrangements(ProductArrangementFacadeFactory.createProductArrangementFacade(customerArrangements));
        EligibilityDecision evaluate = rule.evaluate(candidateInstruction, ruleDataHolder);

        assertNull(evaluate.getReasonText());
        assertTrue(evaluate.isEligible());
    }

}

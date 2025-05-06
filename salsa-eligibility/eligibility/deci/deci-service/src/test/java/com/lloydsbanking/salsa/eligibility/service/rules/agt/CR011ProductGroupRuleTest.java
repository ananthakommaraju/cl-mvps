package com.lloydsbanking.salsa.eligibility.service.rules.agt;


import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.downstream.ref.model.RefInstructionRulesDto;
import com.lloydsbanking.salsa.eligibility.TestDataHelper;
import com.lloydsbanking.salsa.eligibility.service.utility.EligibilityException;
import com.lloydsbanking.salsa.eligibility.service.utility.ProductArrangementFacadeFactory;
import com.lloydsbanking.salsa.eligibility.service.rules.common.EligibilityDecision;
import com.lloydsbanking.salsa.eligibility.service.rules.common.RuleDataHolder;
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
public class CR011ProductGroupRuleTest {

    CR011ProductGroupRule rule;

    TestDataHelper testDataHelper;

    RequestHeader header;

    DetermineElegibileInstructionsRequest upstreamRequest;

    CustomerInstruction customerInstruction;

    RefInstructionRulesDto ruleDto;

    List<ProductArrangement> customerArrangements;

    ProductArrangement productArrangement;


    @Before
    public void setUp() {
        rule = new CR011ProductGroupRule();
        testDataHelper = new TestDataHelper();

        upstreamRequest = testDataHelper.createEligibilityRequest("abc", testDataHelper.TEST_OCIS_ID, testDataHelper.TEST_RETAIL_CHANNEL_ID, testDataHelper.TEST_CONTACT_POINT_ID);
        header = upstreamRequest.getHeader();
        customerArrangements = new ArrayList<>();
        ruleDto = new RefInstructionRulesDto();

    }

    @Test
    public void testCR011ProductGroupRuleFails() throws EligibilityException {

        ruleDto.setRule("CR011");
        ruleDto.setCmsReason("CR012");
        ruleDto.setRuleParamValue("AAA");

        productArrangement = new ProductArrangement();
        productArrangement.setParentInstructionMnemonic("AGT");
        customerArrangements.add(productArrangement);

        RuleDataHolder ruleDataHolder = new RuleDataHolder();
        ruleDataHolder.setRule(ruleDto.getRule());
        ruleDataHolder.setRuleParamValue(ruleDto.getRuleParamValue());
        ruleDataHolder.setProductArrangements(ProductArrangementFacadeFactory.createProductArrangementFacade(customerArrangements));
        ruleDataHolder.setHeader(upstreamRequest.getHeader());

        EligibilityDecision evaluate = rule.evaluate(ruleDataHolder);

        assertEquals("Customer doesn't have any eligible credit card accounts for Balance transfer", evaluate.getReasonText());
        assertFalse(evaluate.isEligible());

    }

    @Test
    public void testCR011ProductGroupRulePasses() throws EligibilityException {

        ruleDto.setRule("CR011");
        ruleDto.setCmsReason("CR012");
        ruleDto.setRuleParamValue("AGT");

        productArrangement = new ProductArrangement();
        productArrangement.setParentInstructionMnemonic("AGT");
        customerArrangements.add(productArrangement);
        RuleDataHolder ruleDataHolder = new RuleDataHolder();
        ruleDataHolder.setRule(ruleDto.getRule());
        ruleDataHolder.setRuleParamValue(ruleDto.getRuleParamValue());
        ruleDataHolder.setProductArrangements(ProductArrangementFacadeFactory.createProductArrangementFacade(customerArrangements));
        ruleDataHolder.setHeader(upstreamRequest.getHeader());

        EligibilityDecision evaluate = rule.evaluate(ruleDataHolder);

        assertNull(evaluate.getReasonText());
        assertTrue(evaluate.isEligible());

    }
}

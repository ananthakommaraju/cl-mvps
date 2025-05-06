package com.lloydsbanking.salsa.eligibility.service.rules.agt;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.downstream.ref.model.RefInstructionRulesDto;
import com.lloydsbanking.salsa.eligibility.TestDataHelper;
import com.lloydsbanking.salsa.eligibility.service.rules.common.EligibilityDecision;
import com.lloydsbanking.salsa.eligibility.service.rules.common.RuleDataHolder;
import com.lloydsbanking.salsa.eligibility.service.utility.ProductArrangementFacadeFactory;
import lb_gbo_sales.ProductArrangement;
import lb_gbo_sales.messages.DetermineElegibileInstructionsRequest;
import lb_gbo_sales.messages.RequestHeader;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

@Category(UnitTest.class)
public class CR004RelatedEventAndDormantStatusRuleTest {

    TestDataHelper testDataHelper;

    List<ProductArrangement> arrangementList;

    CR004RelatedEventAndDormantStatusRule rule;

    RequestHeader header;

    RefInstructionRulesDto ruleDto;


    RuleDataHolder ruleDataHolder;

    @Before
    public void setUp() {
        rule = new CR004RelatedEventAndDormantStatusRule();
        testDataHelper = new TestDataHelper();
        DetermineElegibileInstructionsRequest request = testDataHelper.createEligibilityRequest("abc", testDataHelper.TEST_OCIS_ID, testDataHelper.TEST_RETAIL_CHANNEL_ID, testDataHelper.TEST_CONTACT_POINT_ID);
        request.getCustomerArrangements().get(0).getRelatedEvents().add("37");
        arrangementList = request.getCustomerArrangements();
        header = request.getHeader();
        ruleDto = new RefInstructionRulesDto("G_PPC", "GR0003", "No valid credit card account for PPC", "GR0003", "CR004", "Customer already has PPC on an account", "CR004", "GRP", "AGT", "37", "IBL", new BigDecimal("1"));
        ruleDataHolder = new RuleDataHolder();
        ruleDataHolder.setRule(ruleDto.getRule());
        ruleDataHolder.setRuleParamValue(ruleDto.getRuleParamValue());
        ruleDataHolder.setHeader(header);
        ruleDataHolder.setProductArrangements(ProductArrangementFacadeFactory.createProductArrangementFacade(arrangementList));
    }

    @Test
    public void testCR004IsSuccessful() throws Exception {
        EligibilityDecision evaluate = rule.evaluate(ruleDataHolder);
        assertNull(evaluate.getReasonText());
        assertTrue(evaluate.isEligible());
    }

    @Test
    public void testCR004IsFail() throws Exception {
        ruleDto.setRuleParamValue("400");
        ruleDataHolder.setRuleParamValue(ruleDto.getRuleParamValue());
        EligibilityDecision evaluate = rule.evaluate(ruleDataHolder);
        assertFalse(evaluate.isEligible());
        assertEquals("Customer doesn't have an existing product with the 400 event enabled", evaluate.getReasonText());
    }

}

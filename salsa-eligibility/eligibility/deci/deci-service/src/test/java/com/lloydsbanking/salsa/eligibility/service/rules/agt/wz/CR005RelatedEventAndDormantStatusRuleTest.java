package com.lloydsbanking.salsa.eligibility.service.rules.agt.wz;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.downstream.ref.model.RefInstructionRulesDto;
import com.lloydsbanking.salsa.eligibility.TestDataHelper;
import com.lloydsbanking.salsa.eligibility.service.rules.common.EligibilityDecision;
import com.lloydsbanking.salsa.eligibility.service.rules.common.RuleDataHolder;
import com.lloydsbanking.salsa.eligibility.service.utility.ProductArrangementFacadeFactory;
import lb_gbo_sales.ProductArrangement;
import lb_gbo_sales.businessobjects.ProductArrangementLifecycleStatus;
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
public class CR005RelatedEventAndDormantStatusRuleTest {
    TestDataHelper testDataHelper;

    List<ProductArrangement> arrangementList;

    CR005RelatedEventAndDormantStatusRule rule;

    RequestHeader header;

    RefInstructionRulesDto ruleDto;

    RuleDataHolder ruleDataHolder;

    @Before
    public void setUp() {
        rule = new CR005RelatedEventAndDormantStatusRule();
        testDataHelper = new TestDataHelper();
        arrangementList = new ArrayList<>();
        arrangementList.add(new ProductArrangement());
        arrangementList.get(0).setLifecycleStatus(ProductArrangementLifecycleStatus.EFFECTIVE);
        header = testDataHelper.createEligibilityRequestHeader(TestDataHelper.TEST_RETAIL_CHANNEL_ID, TestDataHelper.TEST_INTERACTION_ID, TestDataHelper.TEST_OCIS_ID, TestDataHelper.TEST_CUSTOMER_ID, TestDataHelper.TEST_CONTACT_POINT_ID);
        ruleDto = new RefInstructionRulesDto();
        ruleDataHolder = new RuleDataHolder();
        ruleDataHolder.setRule(ruleDto.getRule());
        ruleDataHolder.setRuleParamValue(ruleDto.getRuleParamValue());
        ruleDataHolder.setHeader(header);
        ruleDataHolder.setProductArrangements(ProductArrangementFacadeFactory.createProductArrangementFacade(arrangementList));
    }

    @Test
    public void testCR005IsSuccessful() throws Exception {
        ruleDto.setRule("CR005");
        ruleDto.setRuleParamValue("A");
        ruleDataHolder.setRule(ruleDto.getRule());
        ruleDataHolder.setRuleParamValue(ruleDto.getRuleParamValue());
        EligibilityDecision evaluate = rule.evaluate(ruleDataHolder);
        assertNull(evaluate.getReasonText());
        assertTrue(evaluate.isEligible());
    }

    @Test
    public void testCR005IsFail() throws Exception {
        ruleDto.setRule("CR005");
        ruleDto.setRuleParamValue("Effective");
        ruleDataHolder.setRule(ruleDto.getRule());
        ruleDataHolder.setRuleParamValue(ruleDto.getRuleParamValue());
        EligibilityDecision evaluate = rule.evaluate(ruleDataHolder);
        assertFalse(evaluate.isEligible());
        assertEquals("The customer's account is Effective", evaluate.getReasonText());
    }

    @Test
    public void testCR005IsSuccessfulForLifeCycleStatusIsNull() throws Exception {
        ruleDto.setRule("CR005");
        ruleDto.setRuleParamValue("Effective");
        ruleDataHolder.setRule(ruleDto.getRule());
        ruleDataHolder.setRuleParamValue(ruleDto.getRuleParamValue());
        arrangementList.get(0).setLifecycleStatus(null);
        EligibilityDecision evaluate = rule.evaluate(ruleDataHolder);
        assertNull(evaluate.getReasonText());
        assertTrue(evaluate.isEligible());
    }
}

package com.lloydsbanking.salsa.eligibility.service.rules.asb;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.downstream.ref.model.RefInstructionRulesDto;
import com.lloydsbanking.salsa.eligibility.TestDataHelper;
import com.lloydsbanking.salsa.eligibility.service.utility.EligibilityException;
import com.lloydsbanking.salsa.eligibility.service.rules.common.BusinessArrangementHandlerBZ;
import com.lloydsbanking.salsa.eligibility.service.rules.common.EligibilityDecision;
import com.lloydsbanking.salsa.eligibility.service.rules.common.RuleDataHolder;
import com.lloydsbanking.salsa.eligibility.service.utility.exceptions.SalsaInternalResourceNotAvailableException;
import com.lloydsbanking.salsa.eligibility.service.utility.exceptions.SalsaInternalServiceException;
import lb_gbo_sales.businessobjects.BusinessArrangement;
import lb_gbo_sales.businessobjects.CustomerInstruction;
import lb_gbo_sales.messages.DetermineElegibileInstructionsRequest;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

@Category(UnitTest.class)
public class CR051ValidBusinessEntityRuleTest {

    TestDataHelper testDataHelper;

    CR051ValidBusinessEntityRule rule;

    DetermineElegibileInstructionsRequest upstreamRequest;

    CustomerInstruction customerInstruction;

    @Before
    public void setUp() throws Exception {
        rule = new CR051ValidBusinessEntityRule();
        testDataHelper = new TestDataHelper();
        upstreamRequest = testDataHelper.createEligibilityRequest("P_BODA_RBB", testDataHelper.TEST_OCIS_ID, testDataHelper.TEST_COMMERCIAL_CHANNEL_ID, testDataHelper.TEST_CONTACT_POINT_ID);
        customerInstruction = new CustomerInstruction();

    }

    @Test
    public void testCR051IsUnSuccessful() throws SalsaInternalResourceNotAvailableException, SalsaInternalServiceException, EligibilityException {
        RefInstructionRulesDto refInstructionRulesDto = testDataHelper.createRefInstructionRulesDto("CR051", "002:004:005:006:007:012:013:014");
        upstreamRequest.setSelctdBusnsId("1234");
        BusinessArrangement businessArrangement = new BusinessArrangement();
        businessArrangement.setEnttyTyp("011");
        businessArrangement.setBusinessId("1234");
        upstreamRequest.getBusinessArrangements().add(businessArrangement);

        RuleDataHolder ruleDataHolder = new RuleDataHolder();
        ruleDataHolder.setRule(refInstructionRulesDto.getRule());
        ruleDataHolder.setRuleParamValue(refInstructionRulesDto.getRuleParamValue());
        ruleDataHolder.setBusinessArrangement(new BusinessArrangementHandlerBZ(upstreamRequest.getBusinessArrangements()));

        EligibilityDecision evaluate = rule.evaluate(ruleDataHolder, upstreamRequest.getSelctdBusnsId(), null, null);
        assertEquals("Business Entity Type 011 is not in allowed list 002:004:005:006:007:012:013:014", evaluate.getReasonText() );
        assertFalse(evaluate.isEligible());
    }

    @Test
    public void testCR051IsSuccessful() throws SalsaInternalResourceNotAvailableException, SalsaInternalServiceException, EligibilityException {
        RefInstructionRulesDto refInstructionRulesDto = testDataHelper.createRefInstructionRulesDto("CR051", "002:004:005:006:007:012:013:014");
        upstreamRequest.setSelctdBusnsId("1234");
        BusinessArrangement businessArrangement = new BusinessArrangement();
        businessArrangement.setEnttyTyp("012");
        businessArrangement.setBusinessId("1234");
        upstreamRequest.getBusinessArrangements().add(businessArrangement);

        RuleDataHolder ruleDataHolder = new RuleDataHolder();
        ruleDataHolder.setRule(refInstructionRulesDto.getRule());
        ruleDataHolder.setRuleParamValue(refInstructionRulesDto.getRuleParamValue());
        ruleDataHolder.setBusinessArrangement(new BusinessArrangementHandlerBZ(upstreamRequest.getBusinessArrangements()));

        EligibilityDecision evaluate = rule.evaluate(ruleDataHolder, upstreamRequest.getSelctdBusnsId(), null, null);

        assertNull(evaluate.getReasonText());
        assertTrue(evaluate.isEligible());
    }

}
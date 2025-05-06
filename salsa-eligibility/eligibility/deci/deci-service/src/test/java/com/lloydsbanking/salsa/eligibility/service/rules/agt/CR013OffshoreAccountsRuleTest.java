package com.lloydsbanking.salsa.eligibility.service.rules.agt;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.downstream.ref.model.RefInstructionRulesDto;
import com.lloydsbanking.salsa.eligibility.TestDataHelper;
import com.lloydsbanking.salsa.eligibility.service.rules.common.EligibilityDecision;
import com.lloydsbanking.salsa.eligibility.service.rules.common.RuleDataHolder;
import com.lloydsbanking.salsa.eligibility.service.utility.ProductArrangementFacadeFactory;
import com.lloydsbanking.salsa.eligibility.service.utility.constants.DeclineReasons;
import lb_gbo_sales.DepositArrangement;
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
public class CR013OffshoreAccountsRuleTest {
    TestDataHelper testDataHelper;

    List<ProductArrangement> arrangementList;

    CR013OffshoreAccountsRule rule;

    RequestHeader header;

    RefInstructionRulesDto ruleDto;


    @Before
    public void setUp() {
        rule = new CR013OffshoreAccountsRule();
        testDataHelper = new TestDataHelper();
        DetermineElegibileInstructionsRequest request = testDataHelper.createEligibilityRequest("abc", testDataHelper.TEST_OCIS_ID, testDataHelper.TEST_RETAIL_CHANNEL_ID, testDataHelper.TEST_CONTACT_POINT_ID);
        arrangementList = request.getCustomerArrangements();
        header = request.getHeader();
        ruleDto = new RefInstructionRulesDto("G_PPC", "GR0003", "No valid credit card account for PPC", "GR0003", "CR004", "Customer already has PPC on an account", "CR004", "GRP", "AGT", "55555", "IBL", new BigDecimal("1"));

    }

    @Test
    public void testCR013IsSuccessful() throws Exception {
        ruleDto = new RefInstructionRulesDto("G_PPC", "GR0003", "No valid credit card account for PPC", "GR0003", "CR004", "Customer already has PPC on an account", "CR004", "GRP", "AGT", "55510:55513", "IBL", new BigDecimal("1"));
        ((DepositArrangement) (arrangementList.get(0))).setSortCode("55556");
        RuleDataHolder ruleDataHolder = new RuleDataHolder();
        ruleDataHolder.setRule(ruleDto.getRule());
        ruleDataHolder.setRuleParamValue(ruleDto.getRuleParamValue());
        ruleDataHolder.setProductArrangements(ProductArrangementFacadeFactory.createProductArrangementFacade(arrangementList));
        EligibilityDecision evaluate = rule.evaluate(ruleDataHolder);
        assertNull(evaluate.getReasonText());
        assertTrue(evaluate.isEligible());
    }

    @Test
    public void testCR013IsSuccessful2() throws Exception {
        ((DepositArrangement) (arrangementList.get(0))).setSortCode("55556");
        RuleDataHolder ruleDataHolder = new RuleDataHolder();
        ruleDataHolder.setRule(ruleDto.getRule());
        ruleDataHolder.setRuleParamValue(ruleDto.getRuleParamValue());
        ruleDataHolder.setProductArrangements(ProductArrangementFacadeFactory.createProductArrangementFacade(arrangementList));
        EligibilityDecision evaluate = rule.evaluate(ruleDataHolder);

        assertNull(evaluate.getReasonText());
        assertTrue(evaluate.isEligible());
    }

    @Test
    public void testCR013IsFail() throws Exception {
        ((DepositArrangement) (arrangementList.get(0))).setSortCode("55555");
        RuleDataHolder ruleDataHolder = new RuleDataHolder();
        ruleDataHolder.setRule(ruleDto.getRule());
        ruleDataHolder.setRuleParamValue(ruleDto.getRuleParamValue());
        ruleDataHolder.setProductArrangements(ProductArrangementFacadeFactory.createProductArrangementFacade(arrangementList));
        EligibilityDecision evaluate = rule.evaluate(ruleDataHolder);

        assertEquals(DeclineReasons.CR013_DECLINE_REASON, evaluate.getReasonText());
        assertFalse(evaluate.isEligible());
    }

    @Test
    public void testCR013IsFail2() throws Exception {
        ruleDto = new RefInstructionRulesDto("G_PPC", "GR0003", "No valid credit card account for PPC", "GR0003", "CR004", "Customer already has PPC on an account", "CR004", "GRP", "AGT", "55510:55512", "IBL", new BigDecimal("1"));
        ((DepositArrangement) (arrangementList.get(0))).setSortCode("55510");
        RuleDataHolder ruleDataHolder = new RuleDataHolder();
        ruleDataHolder.setRule(ruleDto.getRule());
        ruleDataHolder.setRuleParamValue(ruleDto.getRuleParamValue());
        ruleDataHolder.setProductArrangements(ProductArrangementFacadeFactory.createProductArrangementFacade(arrangementList));
        EligibilityDecision evaluate = rule.evaluate(ruleDataHolder);
        assertEquals(DeclineReasons.CR013_DECLINE_REASON, evaluate.getReasonText());
        assertFalse(evaluate.isEligible());
    }
}

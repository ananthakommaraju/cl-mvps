package com.lloydsbanking.salsa.eligibility.service.rules.agt;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.eligibility.TestDataHelper;
import com.lloydsbanking.salsa.eligibility.service.rules.common.EligibilityDecision;
import com.lloydsbanking.salsa.eligibility.service.rules.common.RuleDataHolder;
import com.lloydsbanking.salsa.eligibility.service.utility.ProductArrangementFacadeFactory;
import com.lloydsbanking.salsa.eligibility.service.utility.constants.DeclineReasons;
import lb_gbo_sales.ProductArrangement;
import lb_gbo_sales.businessobjects.CreditCardFinanceServiceArrangement;
import lb_gbo_sales.businessobjects.CreditCardStatus;
import lb_gbo_sales.messages.DetermineElegibileInstructionsRequest;
import lb_gbo_sales.messages.RequestHeader;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

@Category(UnitTest.class)
public class CR007CreditCardStatusRuleTest {

    TestDataHelper testDataHelper;

    List<ProductArrangement> arrangementList;

    CR007CreditCardStatusRule rule;

    RequestHeader header;

    @Before
    public void setUp() {
        rule = new CR007CreditCardStatusRule();
        testDataHelper = new TestDataHelper();
        DetermineElegibileInstructionsRequest request = testDataHelper.createEligibilityRequest("abc", testDataHelper.TEST_OCIS_ID, testDataHelper.TEST_RETAIL_CHANNEL_ID, testDataHelper.TEST_CONTACT_POINT_ID);
        arrangementList = request.getCustomerArrangements();
        header = request.getHeader();

    }

    @Test
    public void testCR007IsSuccessful() throws Exception {

        ((CreditCardFinanceServiceArrangement) (arrangementList.get(1))).setCardStatus(CreditCardStatus.F);
        RuleDataHolder ruleDataHolder = new RuleDataHolder();
        ruleDataHolder.setRule("CR007");
        ruleDataHolder.setProductArrangements(ProductArrangementFacadeFactory.createProductArrangementFacade(arrangementList));
        EligibilityDecision evaluate = rule.evaluate(ruleDataHolder);
        assertNull(evaluate.getReasonText());
        assertTrue(evaluate.isEligible());
    }

    @Test
    public void testCR007IsFail1() throws Exception {
        ((CreditCardFinanceServiceArrangement) (arrangementList.get(1))).setCardStatus(CreditCardStatus.B);
        RuleDataHolder ruleDataHolder = new RuleDataHolder();
        ruleDataHolder.setRule("CR007");
        ruleDataHolder.setProductArrangements(ProductArrangementFacadeFactory.createProductArrangementFacade(arrangementList));
        EligibilityDecision evaluate = rule.evaluate(ruleDataHolder);
        assertEquals(DeclineReasons.CR007_DECLINE_REASON, evaluate.getReasonText());
        assertFalse(evaluate.isEligible());
    }

    @Test
    public void testCR007IsFail2() throws Exception {
        ((CreditCardFinanceServiceArrangement) (arrangementList.get(1))).setCardStatus(CreditCardStatus.Z);
        RuleDataHolder ruleDataHolder = new RuleDataHolder();
        ruleDataHolder.setRule("CR007");
        ruleDataHolder.setProductArrangements(ProductArrangementFacadeFactory.createProductArrangementFacade(arrangementList));
        EligibilityDecision evaluate = rule.evaluate(ruleDataHolder);
        assertEquals(DeclineReasons.CR007_DECLINE_REASON, evaluate.getReasonText());
        assertFalse(evaluate.isEligible());
    }

    @Test
    public void testCR007IsFail3() throws Exception {
        ((CreditCardFinanceServiceArrangement) (arrangementList.get(1))).setCardStatus(CreditCardStatus.U);
        RuleDataHolder ruleDataHolder = new RuleDataHolder();
        ruleDataHolder.setRule("CR007");
        ruleDataHolder.setProductArrangements(ProductArrangementFacadeFactory.createProductArrangementFacade(arrangementList));
        EligibilityDecision evaluate = rule.evaluate(ruleDataHolder);
        assertEquals(DeclineReasons.CR007_DECLINE_REASON, evaluate.getReasonText());
        assertFalse(evaluate.isEligible());
    }

    @Test
    public void testCR007IsFail4() throws Exception {
        ((CreditCardFinanceServiceArrangement) (arrangementList.get(1))).setCardStatus(null);
        RuleDataHolder ruleDataHolder = new RuleDataHolder();
        ruleDataHolder.setRule("CR007");
        ruleDataHolder.setProductArrangements(ProductArrangementFacadeFactory.createProductArrangementFacade(arrangementList));
        EligibilityDecision evaluate = rule.evaluate(ruleDataHolder);
        assertNull(evaluate.getReasonText());
        assertTrue(evaluate.isEligible());
    }
}

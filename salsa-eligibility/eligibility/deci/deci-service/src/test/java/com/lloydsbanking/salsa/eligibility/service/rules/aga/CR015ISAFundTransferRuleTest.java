package com.lloydsbanking.salsa.eligibility.service.rules.aga;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.downstream.ref.model.RefInstructionRulesDto;
import com.lloydsbanking.salsa.eligibility.TestDataHelper;
import com.lloydsbanking.salsa.eligibility.service.rules.common.EligibilityDecision;
import com.lloydsbanking.salsa.eligibility.service.rules.common.RuleDataHolder;
import com.lloydsbanking.salsa.eligibility.service.utility.EligibilityException;
import com.lloydsbanking.salsa.eligibility.service.utility.ProductArrangementFacadeFactory;
import com.lloydsbanking.salsa.eligibility.service.utility.constants.DeclineReasons;
import com.lloydsbanking.salsa.eligibility.service.utility.exceptions.SalsaExternalServiceException;
import com.lloydsbanking.salsa.eligibility.service.utility.exceptions.SalsaInternalResourceNotAvailableException;
import com.lloydsbanking.salsa.eligibility.service.utility.exceptions.SalsaInternalServiceException;
import lb_gbo_sales.DepositArrangement;
import lb_gbo_sales.ProductArrangement;
import lb_gbo_sales.businessobjects.CustomerInstruction;
import lb_gbo_sales.businessobjects.LimitCondition;
import lb_gbo_sales.interfaces.ia_determineelegiblecustomerinstructions.DetermineEligibleInstructionsExternalBusinessErrorMsg;
import lb_gbo_sales.interfaces.ia_determineelegiblecustomerinstructions.DetermineEligibleInstructionsInternalServiceErrorMsg;
import lb_gbo_sales.interfaces.ia_determineelegiblecustomerinstructions.DetermineEligibleInstructionsResourceNotAvailableErrorMsg;
import lb_gbo_sales.messages.DetermineElegibileInstructionsRequest;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;


@Category(UnitTest.class)
public class CR015ISAFundTransferRuleTest {
    TestDataHelper testDataHelper;

    CR015ISAFundTransferRule rule;

    DetermineElegibileInstructionsRequest upstreamRequest;

    CustomerInstruction customerInstruction;

    BigDecimal maximumTransactionAmount;

    BigDecimal headRoomAmount;

    @Before
    public void setUp() throws Exception {
        rule = new CR015ISAFundTransferRule();
        testDataHelper = new TestDataHelper();
        upstreamRequest = testDataHelper.createEligibilityRequest("G_ISA", testDataHelper.TEST_OCIS_ID, testDataHelper.TEST_RETAIL_CHANNEL_ID, testDataHelper.TEST_CONTACT_POINT_ID);

        customerInstruction = new CustomerInstruction();

    }

    @Test
    public void testCR015IsUnSuccessful() throws SalsaInternalResourceNotAvailableException, SalsaInternalServiceException, SalsaExternalServiceException, EligibilityException {

        maximumTransactionAmount = new BigDecimal(100);
        headRoomAmount = new BigDecimal(1);
        setMaxTransactionAndHeadRoomAmount();
        RuleDataHolder ruleDataHolder = new RuleDataHolder();
        ruleDataHolder.setRule("CR015");
        ruleDataHolder.setProductArrangements(ProductArrangementFacadeFactory.createProductArrangementFacade(upstreamRequest.getCustomerArrangements()));
        EligibilityDecision evaluate = rule.evaluate(null, ruleDataHolder);

        assertEquals(DeclineReasons.CR015_DECLINE_REASON, evaluate.getReasonText());
        assertFalse(evaluate.isEligible());
    }

    @Test
    public void testCR015IsSuccessful() throws DetermineEligibleInstructionsInternalServiceErrorMsg, DetermineEligibleInstructionsResourceNotAvailableErrorMsg, DetermineEligibleInstructionsExternalBusinessErrorMsg, SalsaInternalResourceNotAvailableException, SalsaInternalServiceException, SalsaExternalServiceException, EligibilityException {
        RefInstructionRulesDto rulesDto = new RefInstructionRulesDto();
        rulesDto.setCmsReason("CR015");
        rulesDto.setRule("CR015");
        maximumTransactionAmount = new BigDecimal(100);
        headRoomAmount = new BigDecimal(100);
        setMaxTransactionAndHeadRoomAmount();
        RuleDataHolder ruleDataHolder = new RuleDataHolder();
        ruleDataHolder.setRule("CR015");
        ruleDataHolder.setProductArrangements(ProductArrangementFacadeFactory.createProductArrangementFacade(upstreamRequest.getCustomerArrangements()));
        EligibilityDecision evaluate = rule.evaluate(null, ruleDataHolder);
        assertNull(evaluate.getReasonText());
        assertTrue(evaluate.isEligible());
    }

    private void setMaxTransactionAndHeadRoomAmount() {
        for (ProductArrangement productArrangement : upstreamRequest.getCustomerArrangements()) {
            if (productArrangement instanceof DepositArrangement) {
                ((DepositArrangement) productArrangement).setParentInstructionMnemonic("G_ISA");
                LimitCondition maxTransactionAmoutLimitCondition = new LimitCondition();
                maxTransactionAmoutLimitCondition.setValue(maximumTransactionAmount);
                LimitCondition headRoomAmountLimitCondition = new LimitCondition();
                headRoomAmountLimitCondition.setValue(headRoomAmount);
                ((DepositArrangement) productArrangement).setMaximumTransactionAmount(maxTransactionAmoutLimitCondition);
                ((DepositArrangement) productArrangement).setHeadRoomAmount(headRoomAmountLimitCondition);
            }
        }
    }

}
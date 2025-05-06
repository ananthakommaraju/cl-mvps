package com.lloydsbanking.salsa.eligibility.service.rules.aga.wz;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.downstream.ref.model.RefInstructionRulesDto;
import com.lloydsbanking.salsa.downstream.switches.SwitchService;
import com.lloydsbanking.salsa.eligibility.service.downstream.AppGroupRetriever;
import com.lloydsbanking.salsa.eligibility.service.downstream.CheckBalanceRetriever;
import com.lloydsbanking.salsa.eligibility.service.rules.common.EligibilityDecision;
import com.lloydsbanking.salsa.eligibility.service.rules.common.RuleDataHolder;
import com.lloydsbanking.salsa.eligibility.service.utility.EligibilityException;
import com.lloydsbanking.salsa.eligibility.service.utility.ProductArrangementFacadeFactory;
import com.lloydsbanking.salsa.eligibility.service.utility.constants.DeclineReasons;
import com.lloydsbanking.salsa.eligibility.service.utility.exceptions.SalsaExternalServiceException;
import com.lloydsbanking.salsa.eligibility.service.utility.exceptions.SalsaInternalResourceNotAvailableException;
import com.lloydsbanking.salsa.eligibility.service.utility.exceptions.SalsaInternalServiceException;
import com.lloydsbanking.salsa.eligibility.wz.TestDataHelper;
import com.lloydsbanking.salsa.soap.cbs.e141.objects.E141Resp;
import lb_gbo_sales.businessobjects.CustomerInstruction;
import lb_gbo_sales.interfaces.ia_determineelegiblecustomerinstructions.DetermineEligibleInstructionsExternalBusinessErrorMsg;
import lb_gbo_sales.interfaces.ia_determineelegiblecustomerinstructions.DetermineEligibleInstructionsInternalServiceErrorMsg;
import lb_gbo_sales.interfaces.ia_determineelegiblecustomerinstructions.DetermineEligibleInstructionsResourceNotAvailableErrorMsg;
import lb_gbo_sales.messages.RequestHeader;
import lib_sim_bo.businessobjects.InstructionDetails;
import lib_sim_bo.businessobjects.ProductArrangement;
import lib_sim_salesprocessmanagement.messages.DetermineEligibleCustomerInstructionsRequest;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Category(UnitTest.class)
public class CR015ISAFundTransferRuleTest {
    TestDataHelper testDataHelper;

    CR015ISAFundTransferRule rule;

    DetermineEligibleCustomerInstructionsRequest upstreamRequest;

    CustomerInstruction customerInstruction;

    BigDecimal maximumTransactionAmount;

    BigDecimal headRoomAmount;

    E141Resp e141Resp;

    @Before
    public void setUp() throws Exception {
        rule = new CR015ISAFundTransferRule();
        testDataHelper = new TestDataHelper();
        upstreamRequest = testDataHelper.createEligibilityRequest("G_ISA", testDataHelper.TEST_OCIS_ID, testDataHelper.TEST_RETAIL_CHANNEL_ID, testDataHelper.TEST_CONTACT_POINT_ID);

        customerInstruction = new CustomerInstruction();
        rule.switchClient = mock(SwitchService.class);
        rule.checkBalanceRetriever = mock(CheckBalanceRetriever.class);
        rule.appGroupRetriever = mock(AppGroupRetriever.class);

        List<Integer> indicators = new ArrayList<>();
        indicators.add(10);
        e141Resp = testDataHelper.createE141Response(indicators, "20");

    }

    @Test
    public void testCR015IsUnSuccessful() throws SalsaInternalResourceNotAvailableException, SalsaInternalServiceException, SalsaExternalServiceException, EligibilityException {

        maximumTransactionAmount = new BigDecimal(100);
        headRoomAmount = new BigDecimal(1);
        RuleDataHolder ruleDataHolder = new RuleDataHolder();
        ruleDataHolder.setRule("CR015");
        ruleDataHolder.setArrangementType("CA");
        List<ProductArrangement> productArrangements = upstreamRequest.getExistingProductArrangments();
        productArrangements.get(0).getAssociatedProduct().setInstructionDetails(new InstructionDetails());
        productArrangements.get(0).getAssociatedProduct().getInstructionDetails().setParentInstructionMnemonic("G_ISA");
        productArrangements.get(0).getAssociatedProduct().getExternalSystemProductIdentifier().get(0).setSystemCode("00004");
        ruleDataHolder.setProductArrangements(ProductArrangementFacadeFactory.createProductArrangementFacade(productArrangements));
        when(rule.switchClient.getBrandedSwitchValue((any(String.class)), any(String.class), any(boolean.class))).thenReturn(false);
        when(rule.appGroupRetriever.callRetrieveCBSAppGroup(any(RequestHeader.class), any(String.class), any(boolean.class))).thenReturn("01");
        when(rule.checkBalanceRetriever.getCheckBalance(any(RequestHeader.class), any(String.class), any(String.class), any(String.class))).thenReturn(e141Resp);
        EligibilityDecision evaluate = rule.evaluate(null, ruleDataHolder);
        assertEquals(DeclineReasons.CR015_DECLINE_REASON, evaluate.getReasonText());
        assertFalse(evaluate.isEligible());
        assertEquals("CA", ruleDataHolder.getArrangementType());
    }

    @Test
    public void testCR015IsSuccessful() throws DetermineEligibleInstructionsInternalServiceErrorMsg, DetermineEligibleInstructionsResourceNotAvailableErrorMsg, DetermineEligibleInstructionsExternalBusinessErrorMsg, SalsaInternalResourceNotAvailableException, SalsaInternalServiceException, SalsaExternalServiceException, EligibilityException {
        RefInstructionRulesDto rulesDto = new RefInstructionRulesDto();
        rulesDto.setCmsReason("CR015");
        rulesDto.setRule("CR015");
        maximumTransactionAmount = new BigDecimal(100);
        headRoomAmount = new BigDecimal(100);
        RuleDataHolder ruleDataHolder = new RuleDataHolder();
        ruleDataHolder.setRule("CR015");
        ruleDataHolder.setArrangementType("CA");
        List<ProductArrangement> productArrangements = upstreamRequest.getExistingProductArrangments();
        productArrangements.get(0).getAssociatedProduct().setInstructionDetails(new InstructionDetails());
        productArrangements.get(0).getAssociatedProduct().getInstructionDetails().setParentInstructionMnemonic("G_ISA");
        productArrangements.get(0).getAssociatedProduct().getExternalSystemProductIdentifier().get(0).setSystemCode("00004");
        ruleDataHolder.setProductArrangements(ProductArrangementFacadeFactory.createProductArrangementFacade(productArrangements));
        e141Resp.getISADetailsGp().getISADetailsSubGp().setTaxYearTotalDepositAm("0");
        when(rule.switchClient.getBrandedSwitchValue((any(String.class)), any(String.class), any(boolean.class))).thenReturn(false);
        when(rule.appGroupRetriever.callRetrieveCBSAppGroup(any(RequestHeader.class), any(String.class), any(boolean.class))).thenReturn("01");
        when(rule.checkBalanceRetriever.getCheckBalance(any(RequestHeader.class), any(String.class), any(String.class), any(String.class))).thenReturn(e141Resp);
        EligibilityDecision evaluate = rule.evaluate(null, ruleDataHolder);
        assertNull(evaluate.getReasonText());
        assertTrue(evaluate.isEligible());
    }

}
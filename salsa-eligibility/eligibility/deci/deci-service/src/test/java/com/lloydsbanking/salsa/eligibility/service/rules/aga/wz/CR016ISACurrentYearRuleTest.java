package com.lloydsbanking.salsa.eligibility.service.rules.aga.wz;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.downstream.switches.SwitchService;
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
import java.util.Calendar;
import java.util.List;

import static junit.framework.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Category(UnitTest.class)
public class CR016ISACurrentYearRuleTest {
    CR016ISACurrentYearRule rule;

    TestDataHelper testDataHelper;

    DetermineElegibileInstructionsRequest upstreamRequest;

    RequestHeader header;

    CustomerInstruction customerInstruction;

    List<ProductArrangement> customerArrangements;

    ProductArrangement productArrangement = null;

    @Before
    public void setUp() {
        rule = new CR016ISACurrentYearRule();
        testDataHelper = new TestDataHelper();
        upstreamRequest = testDataHelper.createEligibilityRequest("abc", testDataHelper.TEST_OCIS_ID, testDataHelper.TEST_RETAIL_CHANNEL_ID, testDataHelper.TEST_CONTACT_POINT_ID);
        header = upstreamRequest.getHeader();
        customerInstruction = new CustomerInstruction();
        customerInstruction.setEligibilityIndicator(true);
        customerArrangements = new ArrayList<>();
        productArrangement = new ProductArrangement();
        productArrangement.setInstructionMnemonic("P_CASH_ISA");
        productArrangement.setParentInstructionMnemonic("G_ISA");
        customerArrangements.add(productArrangement);
        rule.switchClient = mock(SwitchService.class);
    }

    @Test
    public void testCR016ISACurrentYearRuleTestFails() throws SalsaInternalResourceNotAvailableException, SalsaInternalServiceException, SalsaExternalServiceException, EligibilityException {
        {

            Calendar today = Calendar.getInstance();
            productArrangement.setStartDate(testDataHelper.createXMLGregorianCalendar(today.get(Calendar.YEAR), 4, 7));

            RuleDataHolder ruleDataHolder = new RuleDataHolder();
            ruleDataHolder.setRule("CR016");
            ruleDataHolder.setProductArrangements(ProductArrangementFacadeFactory.createProductArrangementFacade(customerArrangements));
            ruleDataHolder.setHeader(header);
            when(rule.switchClient.getBrandedSwitchValue((any(String.class)), any(String.class), any(boolean.class))).thenReturn(false);
            EligibilityDecision evaluate = rule.evaluate("can", ruleDataHolder);
            assertEquals("ISA opened this year", evaluate.getReasonText());
            assertFalse(evaluate.isEligible());
        }
    }

    @Test
    public void testCR016ISACurrentYearRuleTestPasses() throws SalsaInternalResourceNotAvailableException, SalsaInternalServiceException, SalsaExternalServiceException, EligibilityException {

        Calendar today = Calendar.getInstance();
        productArrangement.setStartDate(testDataHelper.createXMLGregorianCalendar(today.get(Calendar.YEAR - 1), 4, 4));

        RuleDataHolder ruleDataHolder = new RuleDataHolder();
        ruleDataHolder.setRule("CR016");
        ruleDataHolder.setProductArrangements(ProductArrangementFacadeFactory.createProductArrangementFacade(customerArrangements));
        ruleDataHolder.setHeader(header);

        when(rule.switchClient.getBrandedSwitchValue((any(String.class)), any(String.class), any(boolean.class))).thenReturn(false);
        EligibilityDecision evaluate = rule.evaluate("can", ruleDataHolder);

        assertNull(evaluate.getReasonText());
        assertTrue(evaluate.isEligible());

    }

    @Test
    public void testCR016ISACurrentYearRuleReturnsTrueForMultiCashSwitchOn() throws SalsaInternalResourceNotAvailableException, SalsaInternalServiceException, SalsaExternalServiceException, EligibilityException {
        {

            Calendar today = Calendar.getInstance();
            productArrangement.setStartDate(testDataHelper.createXMLGregorianCalendar(today.get(Calendar.YEAR), 4, 7));

            RuleDataHolder ruleDataHolder = new RuleDataHolder();
            ruleDataHolder.setRule("CR016");
            ruleDataHolder.setProductArrangements(ProductArrangementFacadeFactory.createProductArrangementFacade(customerArrangements));
            ruleDataHolder.setHeader(header);
            List<String> candidateInstructions = new ArrayList<>();
            candidateInstructions.add("G_ISA");
            ruleDataHolder.setCandidateInstructions(candidateInstructions);
            when(rule.switchClient.getBrandedSwitchValue((any(String.class)), any(String.class), any(boolean.class))).thenReturn(true);
            EligibilityDecision evaluate = rule.evaluate("can", ruleDataHolder);

            assertTrue(evaluate.isEligible());
        }
    }
}

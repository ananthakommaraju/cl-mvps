package com.lloydsbanking.salsa.eligibility.service.rules.aga;


import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.eligibility.TestDataHelper;
import com.lloydsbanking.salsa.eligibility.service.utility.EligibilityException;
import com.lloydsbanking.salsa.eligibility.service.downstream.AppGroupRetriever;
import com.lloydsbanking.salsa.eligibility.service.downstream.ShadowLimitRetriever;
import com.lloydsbanking.salsa.eligibility.service.utility.ProductArrangementFacade;
import com.lloydsbanking.salsa.eligibility.service.utility.ProductArrangementFacadeFactory;
import com.lloydsbanking.salsa.eligibility.service.rules.common.EligibilityDecision;
import com.lloydsbanking.salsa.eligibility.service.rules.common.RuleDataHolder;
import com.lloydsbanking.salsa.eligibility.service.utility.exceptions.SalsaExternalServiceException;
import com.lloydsbanking.salsa.eligibility.service.utility.exceptions.SalsaInternalResourceNotAvailableException;
import com.lloydsbanking.salsa.eligibility.service.utility.exceptions.SalsaInternalServiceException;
import lb_gbo_sales.ProductArrangement;
import lb_gbo_sales.businessobjects.CustomerInstruction;
import lb_gbo_sales.messages.DetermineElegibileInstructionsRequest;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Category(UnitTest.class)
public class CR060ActiveCurrentAccountRuleTest {

    private TestDataHelper testDataHelper;

    private CR060ActiveCurrentAccountRule rule;


    private List<ProductArrangementFacade> productArrangementFacade;

    DetermineElegibileInstructionsRequest upstreamRequest;

    private RuleDataHolder ruleDataHolder;

    CustomerInstruction customerInstruction;

    List<ProductArrangement> customerArrangements;

    @Before
    public void setUp() {

        ruleDataHolder = new RuleDataHolder();
        rule = new CR060ActiveCurrentAccountRule();

        testDataHelper = new TestDataHelper();

        upstreamRequest = testDataHelper.createEligibilityRequestForLoans("P_BLN_RBB", TestDataHelper.TEST_OCIS_ID, TestDataHelper.TEST_RETAIL_CHANNEL_ID, TestDataHelper.TEST_CONTACT_POINT_ID, 1989, 01, 01);
        productArrangementFacade = ProductArrangementFacadeFactory.createProductArrangementFacade(upstreamRequest.getCustomerArrangements());

        customerInstruction = new CustomerInstruction();

        rule.shadowLimitRetriever = mock(ShadowLimitRetriever.class);

        rule.appGroupRetriever = mock(AppGroupRetriever.class);

        ruleDataHolder.setProductArrangements(productArrangementFacade);


        ruleDataHolder.setRule("CR060");
        ruleDataHolder.setRuleParamValue("0:37");
    }

    @Test
    public void testCR060ReturnsFalseForNullShadowLimit() throws SalsaInternalResourceNotAvailableException, SalsaInternalServiceException, SalsaExternalServiceException, EligibilityException {
        when(rule.shadowLimitRetriever.getShadowLimit(upstreamRequest.getHeader(), "772519", "77251902224906", rule.appGroupRetriever.callRetrieveCBSAppGroup(upstreamRequest.getHeader(), "772519", false)))
                .thenReturn(null);

        EligibilityDecision evaluate = rule.evaluate(null, ruleDataHolder);

        assertEquals("Customer does not hold an active current account having DirectDebit and shadow limit greater than 0.", evaluate.getReasonText());

    }

    @Test
    public void testCR060ReturnsFalseForShadowLimitLessThanSetThreshold() throws SalsaInternalResourceNotAvailableException, SalsaInternalServiceException, SalsaExternalServiceException, EligibilityException {
        when(rule.shadowLimitRetriever.getShadowLimit(upstreamRequest.getHeader(), "772519", "77251902224906", rule.appGroupRetriever.callRetrieveCBSAppGroup(upstreamRequest.getHeader(), "772519", false)))
                .thenReturn("30");

        EligibilityDecision evaluate = rule.evaluate(null, ruleDataHolder);
        assertEquals("Customer does not hold an active current account having DirectDebit and shadow limit greater than 0.", evaluate.getReasonText());
        assertFalse(evaluate.isEligible());

    }

    @Test
    public void testCR060ReturnsFalseForRelatedEventNotPresent() throws SalsaInternalResourceNotAvailableException, SalsaInternalServiceException, SalsaExternalServiceException, EligibilityException {
        when(rule.shadowLimitRetriever.getShadowLimit(ruleDataHolder.getHeader(), "772519", "77251902224906", rule.appGroupRetriever.callRetrieveCBSAppGroup(upstreamRequest.getHeader(), "772519", false)))
                .thenReturn("38");

        EligibilityDecision evaluate = rule.evaluate(null, ruleDataHolder);
        assertEquals("Customer does not hold an active current account having DirectDebit and shadow limit greater than 0.", evaluate.getReasonText());
        assertFalse(evaluate.isEligible());

    }

    @Test
    public void testCR060ReturnsTrueForShadowLimitGreaterThanThresholdAndRelatedEventPresent() throws SalsaInternalResourceNotAvailableException, SalsaInternalServiceException, SalsaExternalServiceException, EligibilityException {

        upstreamRequest.getCustomerArrangements().get(0).getRelatedEvents().add("37");
        upstreamRequest.getCustomerArrangements().get(1).getRelatedEvents().add("37");
        when(rule.shadowLimitRetriever.getShadowLimit(ruleDataHolder.getHeader(), "772519", "77251902224906", rule.appGroupRetriever.callRetrieveCBSAppGroup(upstreamRequest.getHeader(), "772519", false)))
                .thenReturn("338");

        EligibilityDecision evaluate = rule.evaluate(null, ruleDataHolder);

        assertNull(evaluate.getReasonText());
        assertTrue(evaluate.isEligible());
    }
}

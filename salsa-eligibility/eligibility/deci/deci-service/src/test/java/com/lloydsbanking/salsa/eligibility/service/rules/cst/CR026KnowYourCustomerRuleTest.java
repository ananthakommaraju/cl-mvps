package com.lloydsbanking.salsa.eligibility.service.rules.cst;


import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.eligibility.TestDataHelper;
import com.lloydsbanking.salsa.eligibility.service.utility.EligibilityException;
import com.lloydsbanking.salsa.eligibility.service.downstream.KycStatusRetriever;
import com.lloydsbanking.salsa.eligibility.service.rules.common.EligibilityDecision;
import com.lloydsbanking.salsa.eligibility.service.rules.common.RuleDataHolder;
import com.lloydsbanking.salsa.eligibility.service.utility.constants.DeclineReasons;
import com.lloydsbanking.salsa.eligibility.service.utility.exceptions.SalsaExternalBusinessException;
import com.lloydsbanking.salsa.eligibility.service.utility.exceptions.SalsaInternalResourceNotAvailableException;
import com.lloydsbanking.salsa.eligibility.service.utility.exceptions.SalsaInternalServiceException;
import com.lloydsbanking.salsa.soap.ocis.f075.objects.F075Resp;
import lb_gbo_sales.messages.DetermineElegibileInstructionsRequest;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Category(UnitTest.class)
public class CR026KnowYourCustomerRuleTest {

    TestDataHelper testDataHelper;

    CR026KnowYourCustomerRule rule;

    F075Resp f075Resp;

    DetermineElegibileInstructionsRequest upstreamRequest;
    private EligibilityDecision testEligibility;

    @Before
    public void setUp() {

        rule = new CR026KnowYourCustomerRule();
        testDataHelper = new TestDataHelper();
        upstreamRequest = testDataHelper.createEligibilityRequest("abc", testDataHelper.TEST_OCIS_ID, testDataHelper.TEST_RETAIL_CHANNEL_ID, testDataHelper.TEST_CONTACT_POINT_ID);
        rule.kycStatusRetriever = mock(KycStatusRetriever.class);
        f075Resp = new F075Resp();
    }

    @Test
    public void testCR026isSuccessful() throws SalsaInternalServiceException, SalsaInternalResourceNotAvailableException, SalsaExternalBusinessException, EligibilityException {


        when(rule.kycStatusRetriever.getKycStatus(upstreamRequest.getHeader(), null, null, false)).thenReturn("F");
        RuleDataHolder ruleDataHolder = new RuleDataHolder();
        ruleDataHolder.setHeader(upstreamRequest.getHeader());

        testEligibility = rule.evaluate(ruleDataHolder, null, null, null);

        assertTrue(testEligibility.isEligible());
        assertNull(testEligibility.getReasonText());
    }

    @Test
    public void testCR026isUnsuccessful() throws SalsaInternalServiceException, SalsaInternalResourceNotAvailableException, SalsaExternalBusinessException, EligibilityException {
        when(rule.kycStatusRetriever.getKycStatus(upstreamRequest.getHeader(), null, null, false)).thenReturn("P");
        RuleDataHolder ruleDataHolder = new RuleDataHolder();
        ruleDataHolder.setHeader(upstreamRequest.getHeader());

       testEligibility = rule.evaluate(ruleDataHolder, null, null, null);

        assertFalse(testEligibility.isEligible());
        assertEquals(DeclineReasons.CR026_DECLINE_REASON,testEligibility.getReasonText());
    }

}

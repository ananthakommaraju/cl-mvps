package com.lloydsbanking.salsa.eligibility.service.rules.asa;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.eligibility.TestDataHelper;
import com.lloydsbanking.salsa.eligibility.service.downstream.AppGroupRetriever;
import com.lloydsbanking.salsa.eligibility.service.downstream.CBSIndicatorRetriever;
import com.lloydsbanking.salsa.eligibility.service.rules.common.EligibilityDecision;
import com.lloydsbanking.salsa.eligibility.service.rules.common.RuleDataHolder;
import com.lloydsbanking.salsa.eligibility.service.utility.EligibilityException;
import com.lloydsbanking.salsa.eligibility.service.utility.exceptions.SalsaInternalResourceNotAvailableException;
import com.lloydsbanking.salsa.eligibility.service.utility.exceptions.SalsaInternalServiceException;
import com.lloydsbanking.salsa.soap.cbs.e184.objects.E184Resp;
import lb_gbo_sales.businessobjects.CustomerInstruction;
import lb_gbo_sales.messages.DetermineElegibileInstructionsRequest;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Category(UnitTest.class)
public class CR056IndicatorTypeRuleTest {

    TestDataHelper testDataHelper;

    CR056IndicatorTypeRule rule;

    DetermineElegibileInstructionsRequest upstreamRequest;

    CustomerInstruction customerInstruction;

    @Before
    public void setUp() throws Exception {
        rule = new CR056IndicatorTypeRule();
        testDataHelper = new TestDataHelper();
        upstreamRequest = testDataHelper.createEligibilityRequest("P_BODA_RBB", testDataHelper.TEST_OCIS_ID, testDataHelper.TEST_RETAIL_CHANNEL_ID, testDataHelper.TEST_CONTACT_POINT_ID);
        rule.cbsIndicatorRetriever = mock(CBSIndicatorRetriever.class);
        rule.appGroupRetriever = mock(AppGroupRetriever.class);

        customerInstruction = new CustomerInstruction();

    }

    @Test
    public void testCR056IsUnSuccessful() throws SalsaInternalResourceNotAvailableException, SalsaInternalServiceException, EligibilityException {
        E184Resp e184Resp = testDataHelper.createE184Response(646);


        when(rule.appGroupRetriever.callRetrieveCBSAppGroup(upstreamRequest.getHeader(), testDataHelper.TEST_SORT_CODE, false)).thenReturn(testDataHelper.TEST_CBS_APP_GRP);
        when(rule.cbsIndicatorRetriever.getCbsIndicator(upstreamRequest.getHeader(), testDataHelper.TEST_SORT_CODE, testDataHelper.TEST_ACCOUNT_NUMBER, testDataHelper.TEST_CBS_APP_GRP))
                .thenReturn(e184Resp.getIndicator1Gp().getStandardIndicators1Gp());

        RuleDataHolder ruleDataHolder = new RuleDataHolder();

        ruleDataHolder.setRule("CR050");
        ruleDataHolder.setRuleParamValue("646");
        ruleDataHolder.setHeader(upstreamRequest.getHeader());
        ruleDataHolder.setArrangementIdentifier(upstreamRequest.getSelctdArr());

        EligibilityDecision evaluate = rule.evaluate(ruleDataHolder);
        assertNotNull(evaluate.getReasonText());
        assertFalse(evaluate.isEligible());
    }

    @Test
    public void testCR056IsSuccessful() throws SalsaInternalResourceNotAvailableException, SalsaInternalServiceException, EligibilityException {
        E184Resp e184Resp = testDataHelper.createE184Response(123);

        when(rule.appGroupRetriever.callRetrieveCBSAppGroup(upstreamRequest.getHeader(), testDataHelper.TEST_SORT_CODE, false)).thenReturn(testDataHelper.TEST_CBS_APP_GRP);
        when(rule.cbsIndicatorRetriever.getCbsIndicator(upstreamRequest.getHeader(), testDataHelper.TEST_SORT_CODE, testDataHelper.TEST_ACCOUNT_NUMBER, testDataHelper.TEST_CBS_APP_GRP))
                .thenReturn(e184Resp.getIndicator1Gp().getStandardIndicators1Gp());

        RuleDataHolder ruleDataHolder = new RuleDataHolder();
        ruleDataHolder.setRule("CR050");
        ruleDataHolder.setRuleParamValue("646");
        ruleDataHolder.setHeader(upstreamRequest.getHeader());
        ruleDataHolder.setArrangementIdentifier(upstreamRequest.getSelctdArr());

        EligibilityDecision evaluate = rule.evaluate(ruleDataHolder);
        assertNull(evaluate.getReasonText());
        assertTrue(evaluate.isEligible());
    }

}
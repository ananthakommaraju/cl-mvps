package com.lloydsbanking.salsa.eligibility.service.rules.aga;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.downstream.ref.model.RefInstructionRulesDto;
import com.lloydsbanking.salsa.eligibility.TestDataHelper;
import com.lloydsbanking.salsa.eligibility.service.utility.EligibilityException;
import com.lloydsbanking.salsa.eligibility.service.downstream.AppGroupRetriever;
import com.lloydsbanking.salsa.eligibility.service.downstream.CBSIndicatorRetriever;
import com.lloydsbanking.salsa.eligibility.service.utility.ProductArrangementFacade;
import com.lloydsbanking.salsa.eligibility.service.utility.ProductArrangementFacadeFactory;
import com.lloydsbanking.salsa.eligibility.service.rules.common.EligibilityDecision;
import com.lloydsbanking.salsa.eligibility.service.rules.common.RuleDataHolder;
import com.lloydsbanking.salsa.eligibility.service.utility.constants.DeclineReasons;
import com.lloydsbanking.salsa.eligibility.service.utility.exceptions.SalsaExternalServiceException;
import com.lloydsbanking.salsa.eligibility.service.utility.exceptions.SalsaInternalResourceNotAvailableException;
import com.lloydsbanking.salsa.eligibility.service.utility.exceptions.SalsaInternalServiceException;
import com.lloydsbanking.salsa.soap.cbs.e184.objects.E184Resp;
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
public class CR059IndicatorTypeRuleTest {

    TestDataHelper testDataHelper;

    CR059IndicatorTypeRule rule;

    RuleDataHolder ruleDataHolder;

    DetermineElegibileInstructionsRequest upstreamRequest;


    private List<ProductArrangementFacade> productArrangementFacade;

    @Before
    public void setUp() throws Exception {
        rule = new CR059IndicatorTypeRule();
        testDataHelper = new TestDataHelper();
        upstreamRequest = testDataHelper.createEligibilityRequest("P_BODA_RBB", testDataHelper.TEST_OCIS_ID, testDataHelper.TEST_RETAIL_CHANNEL_ID, testDataHelper.TEST_CONTACT_POINT_ID);
        ruleDataHolder = new RuleDataHolder();

        productArrangementFacade = ProductArrangementFacadeFactory.createProductArrangementFacade(upstreamRequest.getCustomerArrangements());
        rule.cbsIndicatorRetriever = mock(CBSIndicatorRetriever.class);
        rule.appGroupRetriever = mock(AppGroupRetriever.class);

    }

    @Test
    public void testCR059IsUnSuccessful() throws SalsaInternalResourceNotAvailableException, SalsaInternalServiceException, SalsaExternalServiceException, EligibilityException {
        RefInstructionRulesDto refInstructionRulesDto = testDataHelper.createRefInstructionRulesDto("CR059", "646");
        E184Resp e184Resp = testDataHelper.createE184Response(646);
        ruleDataHolder.setRule(refInstructionRulesDto.getRule());
        ruleDataHolder.setRuleParamValue(refInstructionRulesDto.getRuleParamValue());
        ruleDataHolder.setHeader(upstreamRequest.getHeader());

        when(rule.appGroupRetriever.callRetrieveCBSAppGroup(ruleDataHolder.getHeader(), testDataHelper.TEST_SORT_CODE, false)).thenReturn(testDataHelper.TEST_CBS_APP_GRP);
        when(rule.cbsIndicatorRetriever.getCbsIndicator(ruleDataHolder.getHeader(), testDataHelper.TEST_SORT_CODE, testDataHelper.TEST_ACCOUNT_NUMBER, testDataHelper.TEST_CBS_APP_GRP))
                .thenReturn(e184Resp.getIndicator1Gp().getStandardIndicators1Gp());
        ruleDataHolder.setProductArrangements(productArrangementFacade);

        EligibilityDecision evaluate = rule.evaluate(null, ruleDataHolder);
        assertEquals(DeclineReasons.CR059_DECLINE_REASON_1 + ruleDataHolder.getRuleParamValue() + DeclineReasons.CR059_DECLINE_REASON_2, evaluate.getReasonText());
        assertFalse(evaluate.isEligible());
    }

        @Test
        public void testCR059IsSuccessful() throws SalsaInternalResourceNotAvailableException, SalsaInternalServiceException, SalsaExternalServiceException, EligibilityException {
            RefInstructionRulesDto refInstructionRulesDto = testDataHelper.createRefInstructionRulesDto("CR059", "646");
            E184Resp e184Resp = testDataHelper.createE184Response(123);
            ruleDataHolder.setRule(refInstructionRulesDto.getRule());
            ruleDataHolder.setRuleParamValue(refInstructionRulesDto.getRuleParamValue());
            ruleDataHolder.setHeader(upstreamRequest.getHeader());


            when(rule.appGroupRetriever.callRetrieveCBSAppGroup(ruleDataHolder.getHeader(), testDataHelper.TEST_SORT_CODE, false)).thenReturn(testDataHelper.TEST_CBS_APP_GRP);
            when(rule.cbsIndicatorRetriever.getCbsIndicator(ruleDataHolder.getHeader(), testDataHelper.TEST_SORT_CODE, testDataHelper.TEST_ACCOUNT_NUMBER, testDataHelper.TEST_CBS_APP_GRP))
                    .thenReturn(e184Resp.getIndicator1Gp().getStandardIndicators1Gp());

            ruleDataHolder.setProductArrangements(productArrangementFacade);

            EligibilityDecision evaluate = rule.evaluate(null, ruleDataHolder);

            assertNull(evaluate.getReasonText());
            assertTrue(evaluate.isEligible());

        }
}
package com.lloydsbanking.salsa.eligibility.service.rules.aga.wz;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.eligibility.service.downstream.AppGroupRetriever;
import com.lloydsbanking.salsa.eligibility.service.downstream.CheckBalanceRetriever;
import com.lloydsbanking.salsa.eligibility.service.rules.common.EligibilityDecision;
import com.lloydsbanking.salsa.eligibility.service.rules.common.RuleDataHolder;
import com.lloydsbanking.salsa.eligibility.service.utility.EligibilityException;
import com.lloydsbanking.salsa.eligibility.service.utility.ProductArrangementFacadeFactory;
import com.lloydsbanking.salsa.eligibility.service.utility.exceptions.SalsaExternalServiceException;
import com.lloydsbanking.salsa.eligibility.service.utility.exceptions.SalsaInternalResourceNotAvailableException;
import com.lloydsbanking.salsa.eligibility.service.utility.exceptions.SalsaInternalServiceException;
import com.lloydsbanking.salsa.eligibility.service.utility.header.GmoToGboRequestHeaderConverter;
import com.lloydsbanking.salsa.eligibility.wz.TestDataHelper;
import com.lloydsbanking.salsa.soap.cbs.e141.objects.E141Resp;
import lib_sim_bo.businessobjects.ProductArrangement;
import lib_sim_gmo.messages.RequestHeader;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Category(UnitTest.class)
public class CR068CBSIndicatorPresentOnAllAccountsRuleTest {
    private CR068CBSIndicatorPresentOnAllAccountsRule rule;

    RuleDataHolder ruleDataHolder;

    TestDataHelper dataHelper;

    RequestHeader header;

    GmoToGboRequestHeaderConverter headerConverter;

    lb_gbo_sales.messages.RequestHeader gboHeader;

    @Before
    public void setUp() {
        rule = new CR068CBSIndicatorPresentOnAllAccountsRule();
        ruleDataHolder = new RuleDataHolder();
        dataHelper = new TestDataHelper();
        rule.appGroupRetriever = mock(AppGroupRetriever.class);
        rule.checkBalanceRetriever = mock(CheckBalanceRetriever.class);
        headerConverter = new GmoToGboRequestHeaderConverter();
        header = dataHelper.createEligibilityRequestHeader("LTB", TestDataHelper.TEST_INTERACTION, TestDataHelper.TEST_OCIS_ID, TestDataHelper.TEST_CUSTOMER_ID, TestDataHelper.TEST_CONTACT_POINT_ID);
        gboHeader = headerConverter.convert(header);
    }

    @Test
    public void testEvaluateReturnsNull() throws SalsaInternalResourceNotAvailableException, SalsaInternalServiceException, SalsaExternalServiceException, EligibilityException {
        ruleDataHolder.setRuleParamValue("664");
        ruleDataHolder.setArrangementType("CA");
        ruleDataHolder.setHeader(gboHeader);
        List<ProductArrangement> productArrangements = new ArrayList<>();
        productArrangements.add(dataHelper.createExistingDepositArrangements());
        int indicator = 665;
        ruleDataHolder.setProductArrangements(ProductArrangementFacadeFactory.createProductArrangementFacade(productArrangements));
        when(rule.appGroupRetriever.callRetrieveCBSAppGroup(gboHeader, "111619", true)).thenReturn("01");
        when(rule.checkBalanceRetriever.getCBSIndicators(gboHeader, "111619", "50001763", "01")).thenReturn(dataHelper.getProdIndicators(indicator));

        EligibilityDecision evaluate = rule.evaluate("P_CAR_FIN", ruleDataHolder);
        assertNull(evaluate.getReasonText());
        assertTrue(evaluate.isEligible());
    }

    @Test
    public void testEvaluateReturnsDeclineReason() throws SalsaInternalResourceNotAvailableException, SalsaInternalServiceException, SalsaExternalServiceException, EligibilityException {
        ruleDataHolder.setRuleParamValue("664");
        ruleDataHolder.setArrangementType("CA");
        ruleDataHolder.setHeader(gboHeader);
        List<ProductArrangement> productArrangements = new ArrayList<>();
        productArrangements.add(dataHelper.createExistingDepositArrangements());
        int indicator = 664;
        ruleDataHolder.setProductArrangements(ProductArrangementFacadeFactory.createProductArrangementFacade(productArrangements));
        when(rule.appGroupRetriever.callRetrieveCBSAppGroup(gboHeader, "111619", true)).thenReturn("01");
        when(rule.checkBalanceRetriever.getCBSIndicators(gboHeader, "111619", "50001763", "01")).thenReturn(dataHelper.getProdIndicators(indicator));

        EligibilityDecision evaluate = rule.evaluate("P_CAR_FIN", ruleDataHolder);
        assertEquals("Customer has all Joint Signatory Accounts", evaluate.getReasonText());
        assertFalse(evaluate.isEligible());
    }
}

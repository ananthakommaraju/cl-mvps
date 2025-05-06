package com.lloydsbanking.salsa.eligibility.service.rules.cst.wz;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.date.DateFactory;
import com.lloydsbanking.salsa.eligibility.service.utility.EligibilityException;
import com.lloydsbanking.salsa.eligibility.service.downstream.AppGroupRetriever;
import com.lloydsbanking.salsa.eligibility.service.downstream.CustomerDecisionDetailsRetriever;
import com.lloydsbanking.salsa.eligibility.service.rules.common.EligibilityDecision;
import com.lloydsbanking.salsa.eligibility.service.rules.common.RuleDataHolder;
import com.lloydsbanking.salsa.eligibility.service.utility.exceptions.SalsaInternalResourceNotAvailableException;
import com.lloydsbanking.salsa.eligibility.service.utility.exceptions.SalsaInternalServiceException;
import com.lloydsbanking.salsa.eligibility.service.utility.header.GmoToGboRequestHeaderConverter;
import com.lloydsbanking.salsa.eligibility.wz.TestDataHelper;
import com.lloydsbanking.salsa.soap.cbs.e591.objects.DecnSubGp;
import lb_gbo_sales.messages.RequestHeader;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import javax.xml.datatype.XMLGregorianCalendar;
import java.text.SimpleDateFormat;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Category(UnitTest.class)
public class CR067ShadowLimitCheckRuleTest {
    private CR067ShadowLimitCheckRule rule;

    private RuleDataHolder ruleDataHolder;

    lib_sim_gmo.messages.RequestHeader header;

    GmoToGboRequestHeaderConverter headerConverter;

    RequestHeader gboHeader;

    TestDataHelper dataHelper;

    private EligibilityDecision testEligibility;

    @Before
    public void setUp() {
        rule = new CR067ShadowLimitCheckRule();
        ruleDataHolder = new RuleDataHolder();
        dataHelper = new TestDataHelper();
        header = dataHelper.createEligibilityRequestHeader(TestDataHelper.TEST_RETAIL_CHANNEL_ID, TestDataHelper.TEST_INTERACTION_ID, TestDataHelper.TEST_OCIS_ID, "542107294", TestDataHelper.TEST_CONTACT_POINT_ID);
        this.headerConverter = new GmoToGboRequestHeaderConverter();
        this.gboHeader = headerConverter.convert(header);
        rule.appGroupRetriever = mock(AppGroupRetriever.class);
        rule.customerDecisionDetailsRetriever = mock(CustomerDecisionDetailsRetriever.class);
    }

    @Test
    public void testEvaluateReturnsNull() throws SalsaInternalResourceNotAvailableException, SalsaInternalServiceException, EligibilityException {
        ruleDataHolder.setHeader(gboHeader);
        ruleDataHolder.setRuleParamValue("10");
        ruleDataHolder.setCustomerDetails(dataHelper.createCustomerDetails(1992, 02, 22));
        DecnSubGp decnSubGp = dataHelper.createE591Response("12").getDecisionGp().getDecnSubGp().get(0);

        when(rule.appGroupRetriever.callRetrieveCBSAppGroup(gboHeader, "111618", true)).thenReturn("02");
        when(rule.customerDecisionDetailsRetriever.getCustomerDecisionDetails(gboHeader, "11161850000901", "02")).thenReturn(decnSubGp);

        DateFactory dateFactory = new DateFactory();
        SimpleDateFormat sdf = new SimpleDateFormat();
        sdf.applyPattern("yyyy-MM-dd");
        XMLGregorianCalendar date = dateFactory.stringToXMLGregorianCalendar("2015-10-20", sdf);

        testEligibility = rule.evaluate(ruleDataHolder, date, "111618", "542107294");
        assertTrue(testEligibility.isEligible());
        assertNull(testEligibility.getReasonText());
        assertEquals("6", testEligibility.getRiskBand());
        assertEquals("12.0", testEligibility.getShadowLimit());
    }

    @Test
    public void testEvaluateReturnsDeclineReason() throws SalsaInternalResourceNotAvailableException, SalsaInternalServiceException, EligibilityException {
        ruleDataHolder.setHeader(gboHeader);
        ruleDataHolder.setRuleParamValue("10");
        ruleDataHolder.setCustomerDetails(dataHelper.createCustomerDetails(1992, 02, 22));
        DecnSubGp decnSubGp = dataHelper.createE591Response("09").getDecisionGp().getDecnSubGp().get(0);

        when(rule.appGroupRetriever.callRetrieveCBSAppGroup(gboHeader, "111618", true)).thenReturn("02");
        when(rule.customerDecisionDetailsRetriever.getCustomerDecisionDetails(gboHeader, "11161850000901", "02")).thenReturn(decnSubGp);

        DateFactory dateFactory = new DateFactory();
        SimpleDateFormat sdf = new SimpleDateFormat();
        sdf.applyPattern("yyyy-MM-dd");
        XMLGregorianCalendar date = dateFactory.stringToXMLGregorianCalendar("2015-10-20", sdf);

        testEligibility = rule.evaluate(ruleDataHolder, date, "111618", "542107294");

        assertEquals("Customer has invalid shadowLimit", testEligibility.getReasonText());
        assertFalse(testEligibility.isEligible());
        assertEquals("6", testEligibility.getRiskBand());
        assertEquals("9.0", testEligibility.getShadowLimit());
    }

}

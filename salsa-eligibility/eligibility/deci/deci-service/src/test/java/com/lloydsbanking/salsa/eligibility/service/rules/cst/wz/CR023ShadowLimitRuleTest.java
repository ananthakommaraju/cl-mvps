package com.lloydsbanking.salsa.eligibility.service.rules.cst.wz;


import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.date.DateFactory;
import com.lloydsbanking.salsa.eligibility.service.utility.EligibilityException;
import com.lloydsbanking.salsa.eligibility.service.downstream.AppGroupRetriever;
import com.lloydsbanking.salsa.eligibility.service.downstream.ShadowLimitRetriever;
import com.lloydsbanking.salsa.eligibility.service.utility.ProductArrangementFacadeFactory;
import com.lloydsbanking.salsa.eligibility.service.rules.common.EligibilityDecision;
import com.lloydsbanking.salsa.eligibility.service.rules.common.RuleDataHolder;
import com.lloydsbanking.salsa.eligibility.service.utility.exceptions.SalsaExternalServiceException;
import com.lloydsbanking.salsa.eligibility.service.utility.exceptions.SalsaInternalResourceNotAvailableException;
import com.lloydsbanking.salsa.eligibility.service.utility.exceptions.SalsaInternalServiceException;
import com.lloydsbanking.salsa.eligibility.service.utility.header.GmoToGboRequestHeaderConverter;
import com.lloydsbanking.salsa.eligibility.wz.TestDataHelper;
import lb_gbo_sales.messages.RequestHeader;
import lib_sim_bo.businessobjects.Customer;
import lib_sim_bo.businessobjects.ExtSysProdIdentifier;
import lib_sim_bo.businessobjects.Product;
import lib_sim_bo.businessobjects.ProductArrangement;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import javax.xml.datatype.XMLGregorianCalendar;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;
import static junit.framework.TestCase.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Category(UnitTest.class)
public class CR023ShadowLimitRuleTest {
    private CR023ShadowLimitRule rule;

    RuleDataHolder ruleDataHolder;

    TestDataHelper dataHelper;

    RequestHeader gboHeader;

    GmoToGboRequestHeaderConverter headerConverter;

    lib_sim_gmo.messages.RequestHeader header;

    private EligibilityDecision testEligibility;


    @Before
    public void setUp() {
        rule = new CR023ShadowLimitRule();
        ruleDataHolder = new RuleDataHolder();
        dataHelper = new TestDataHelper();

        header = dataHelper.createEligibilityRequestHeader(TestDataHelper.TEST_RETAIL_CHANNEL_ID, TestDataHelper.TEST_INTERACTION_ID, TestDataHelper.TEST_OCIS_ID, "542107294", TestDataHelper.TEST_CONTACT_POINT_ID);
        this.headerConverter = new GmoToGboRequestHeaderConverter();
        this.gboHeader = headerConverter.convert(header);
        rule.appGroupRetriever = mock(AppGroupRetriever.class);
        rule.shadowLimitRetriever = mock(ShadowLimitRetriever.class);
    }

    @Test
    public void testCR023ShadowLimitRuleReturnsNull() throws SalsaInternalResourceNotAvailableException, SalsaInternalServiceException, SalsaExternalServiceException, EligibilityException {
        ruleDataHolder.setRuleParamValue("2");
        ruleDataHolder.setHeader(gboHeader);
        Customer customer=new Customer();
        customer.setCustomerNumber("542107283");
        ruleDataHolder.setCustomerDetails(customer);
        when(rule.shadowLimitRetriever.getShadowLimit(gboHeader, null, "542107283", "02")).thenReturn("2");
        when(rule.appGroupRetriever.callRetrieveCBSAppGroup(gboHeader, "32121", true)).thenReturn("02");
        DateFactory dateFactory = new DateFactory();
        SimpleDateFormat sdf = new SimpleDateFormat();
        sdf.applyPattern("yyyy-MM-dd");
        XMLGregorianCalendar date = dateFactory.stringToXMLGregorianCalendar("2015-10-20", sdf);

        testEligibility = rule.evaluate(ruleDataHolder, date, "32121", "122");
        assertTrue(testEligibility.isEligible());
        assertNull(testEligibility.getReasonText());
    }

    @Test
    public void testCR023ShadowLimitRuleReturnsDeclineReason() throws SalsaInternalResourceNotAvailableException, SalsaInternalServiceException, SalsaExternalServiceException, EligibilityException {
        ruleDataHolder.setRuleParamValue("2");
        ruleDataHolder.setHeader(gboHeader);
        Customer customer=new Customer();
        customer.setCustomerNumber("542107283");
        ruleDataHolder.setCustomerDetails(customer);
        when(rule.shadowLimitRetriever.getShadowLimit(gboHeader, null, "542107283", "02")).thenReturn("1");
        when(rule.appGroupRetriever.callRetrieveCBSAppGroup(gboHeader, "32121", true)).thenReturn("02");
        DateFactory dateFactory = new DateFactory();
        SimpleDateFormat sdf = new SimpleDateFormat();
        sdf.applyPattern("yyyy-MM-dd");
        XMLGregorianCalendar date = dateFactory.stringToXMLGregorianCalendar("2015-10-20", sdf);
        List<ProductArrangement> productArrangements1 = new ArrayList<>();
        ProductArrangement productArrangement = new ProductArrangement();
        productArrangement.setAssociatedProduct(new Product());
        productArrangement.getAssociatedProduct().setProductType("1");
        productArrangement.getAssociatedProduct().getExternalSystemProductIdentifier().add(new ExtSysProdIdentifier());
        productArrangement.getAssociatedProduct().getExternalSystemProductIdentifier().get(0).setSystemCode("00004");
        productArrangements1.add(productArrangement);
        ruleDataHolder.setProductArrangements(ProductArrangementFacadeFactory.createProductArrangementFacade(productArrangements1));
        testEligibility = rule.evaluate(ruleDataHolder, date, "32121", "122");
        assertEquals("Customer has current account and Shadow Limit amount is less than threshold2", testEligibility.getReasonText());
        assertFalse(testEligibility.isEligible());

    }

}


  
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

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.XMLGregorianCalendar;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Category(UnitTest.class)
public class CR022ShadowLimitRuleTest {
    private CR022ShadowLimitRule rule;

    RuleDataHolder ruleDataHolder;

    lib_sim_gmo.messages.RequestHeader header;

    GmoToGboRequestHeaderConverter headerConverter;

    private EligibilityDecision testEligibility;

    RequestHeader gboHeader;

    TestDataHelper dataHelper;

    @Before
    public void setUp() {
        rule=new CR022ShadowLimitRule();
        ruleDataHolder = new RuleDataHolder();
        dataHelper = new TestDataHelper();
        header = dataHelper.createEligibilityRequestHeader(TestDataHelper.TEST_RETAIL_CHANNEL_ID, TestDataHelper.TEST_INTERACTION_ID, TestDataHelper.TEST_OCIS_ID, "542107294", TestDataHelper.TEST_CONTACT_POINT_ID);
        this.headerConverter = new GmoToGboRequestHeaderConverter();
        this.gboHeader = headerConverter.convert(header);
        rule.appGroupRetriever = mock(AppGroupRetriever.class);
        rule.shadowLimitRetriever = mock(ShadowLimitRetriever.class);
    }

    @Test
    public void testEvaluateWhenReturnDeclineReason() throws SalsaInternalResourceNotAvailableException, SalsaInternalServiceException, DatatypeConfigurationException, SalsaExternalServiceException, EligibilityException {
        ruleDataHolder.setHeader(gboHeader);
        List<ProductArrangement> productArrangements = new ArrayList<>();
        ProductArrangement productArrangement = new ProductArrangement();
        productArrangement.setAssociatedProduct(new Product());
        productArrangement.getAssociatedProduct().setProductType("1");
        productArrangement.getAssociatedProduct().getExternalSystemProductIdentifier().add(new ExtSysProdIdentifier());
        productArrangement.getAssociatedProduct().getExternalSystemProductIdentifier().get(0).setSystemCode("00004");
        productArrangements.add(productArrangement);
        Customer customer=new Customer();
        customer.setCustomerNumber("542107283");
        ruleDataHolder.setCustomerDetails(customer);
        ruleDataHolder.setProductArrangements(ProductArrangementFacadeFactory.createProductArrangementFacade(productArrangements));
        when(rule.appGroupRetriever.callRetrieveCBSAppGroup(gboHeader, "111618", true)).thenReturn("02");
        when(rule.shadowLimitRetriever.getShadowLimit(gboHeader, null, "542107283", "02")).thenReturn("0.0");

        DateFactory dateFactory = new DateFactory();
        SimpleDateFormat sdf = new SimpleDateFormat();
        sdf.applyPattern("yyyy-MM-dd");
        XMLGregorianCalendar date = dateFactory.stringToXMLGregorianCalendar("2015-10-20", sdf);

        testEligibility = rule.evaluate(ruleDataHolder, date, "111618", "542107294");

        assertEquals("Customer has current account and Shadow Limit amount is 0", testEligibility.getReasonText());
        assertFalse(testEligibility.isEligible());
    }

    @Test
    public void testEvaluateWhenReturnNull() throws SalsaInternalResourceNotAvailableException, SalsaInternalServiceException, DatatypeConfigurationException, SalsaExternalServiceException, EligibilityException {
        ruleDataHolder.setHeader(gboHeader);
        List<ProductArrangement> productArrangements = new ArrayList<>();
        Customer customer=new Customer();
        customer.setCustomerNumber("542107283");
        ruleDataHolder.setCustomerDetails(customer);
        ruleDataHolder.setProductArrangements(ProductArrangementFacadeFactory.createProductArrangementFacade(productArrangements));
        when(rule.appGroupRetriever.callRetrieveCBSAppGroup(gboHeader, "111618", true)).thenReturn("02");
        when(rule.shadowLimitRetriever.getShadowLimit(gboHeader, null, "542107283", "02")).thenReturn("100.0");

        DateFactory dateFactory = new DateFactory();
        SimpleDateFormat sdf = new SimpleDateFormat();
        sdf.applyPattern("yyyy-MM-dd");
        XMLGregorianCalendar date = dateFactory.stringToXMLGregorianCalendar("2015-10-20", sdf);

        testEligibility = rule.evaluate(ruleDataHolder, date, "111618", "542107294");

        assertTrue(testEligibility.isEligible());
        assertNull(testEligibility.getReasonText());
    }

}

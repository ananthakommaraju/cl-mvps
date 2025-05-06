package com.lloydsbanking.salsa.eligibility.service.rules.cst.wz;


import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.eligibility.service.downstream.AppGroupRetriever;
import com.lloydsbanking.salsa.eligibility.service.downstream.CheckBalanceRetriever;
import com.lloydsbanking.salsa.eligibility.service.rules.agt.wz.CR045JointSignatureRule;
import com.lloydsbanking.salsa.eligibility.service.utility.EligibilityException;
import com.lloydsbanking.salsa.eligibility.service.utility.ProductArrangementFacadeFactory;
import com.lloydsbanking.salsa.eligibility.service.rules.common.EligibilityDecision;
import com.lloydsbanking.salsa.eligibility.service.rules.common.RuleDataHolder;
import com.lloydsbanking.salsa.eligibility.service.utility.exceptions.SalsaExternalServiceException;
import com.lloydsbanking.salsa.eligibility.service.utility.exceptions.SalsaInternalResourceNotAvailableException;
import com.lloydsbanking.salsa.eligibility.service.utility.exceptions.SalsaInternalServiceException;
import com.lloydsbanking.salsa.eligibility.service.utility.header.GmoToGboRequestHeaderConverter;
import com.lloydsbanking.salsa.eligibility.wz.TestDataHelper;
import com.lloydsbanking.salsa.soap.cbs.e141.objects.E141Resp;
import lb_gbo_sales.messages.RequestHeader;
import lib_sim_bo.businessobjects.Customer;
import lib_sim_bo.businessobjects.Organisation;
import lib_sim_bo.businessobjects.OrganisationUnit;
import lib_sim_bo.businessobjects.Product;
import lib_sim_bo.businessobjects.ProductArrangement;
import lib_sim_bo.businessobjects.ProductArrangementIndicator;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Category(UnitTest.class)
public class CR046CbsEventRuleTest {
    private RuleDataHolder ruleDataHolder;

    private CR046CbsEventRule rule;

    private EligibilityDecision testEligibility;

    private TestDataHelper dataHelper;

    private RequestHeader header;

    private GmoToGboRequestHeaderConverter headerConverter;

    @Before
    public void setUp() {
        ruleDataHolder = new RuleDataHolder();
        rule = new CR046CbsEventRule();
        rule.checkBalanceRetriever = mock(CheckBalanceRetriever.class);
        rule.appGroupRetriever = mock(AppGroupRetriever.class);
        dataHelper = new TestDataHelper();
        headerConverter = new GmoToGboRequestHeaderConverter();
        header = headerConverter.convert(dataHelper.createEligibilityRequestHeader(dataHelper.TEST_RETAIL_CHANNEL_ID, dataHelper.TEST_INTERACTION_ID, dataHelper.TEST_OCIS_ID, dataHelper.TEST_CUSTOMER_ID, dataHelper.TEST_CONTACT_POINT_ID));
    }

    @Test
    public void testEvaluateReturnsDeclineReason() throws SalsaInternalResourceNotAvailableException, SalsaInternalServiceException, SalsaExternalServiceException, EligibilityException {
        List<ProductArrangement> productArrangementList = new ArrayList();
        ProductArrangement productArrangement = new ProductArrangement();
        productArrangement.setAssociatedProduct(new Product());
        productArrangementList.add(productArrangement);
        List<Integer> productArrangementIndicatorList = new ArrayList();
        productArrangementIndicatorList.add(20);
        int indicator = 20;
        ruleDataHolder.setHeader(header);
        ruleDataHolder.setRuleParamValue("20");
        Customer customer=new Customer();
        customer.setExistingSortCode("111618");
        customer.setExistingAccountNumber("5522145");
        ruleDataHolder.setCustomerDetails(customer);
        when(rule.appGroupRetriever.callRetrieveCBSAppGroup(header, "111618", true)).thenReturn("01");
        when(rule.checkBalanceRetriever.getCBSIndicators(header, "111618", "5522145", "01")).thenReturn(dataHelper.getProdIndicators(indicator));
        ruleDataHolder.setProductArrangements(ProductArrangementFacadeFactory.createProductArrangementFacade(productArrangementList));
        testEligibility = rule.evaluate(ruleDataHolder, null, "112233", "334455");

        verify(rule.checkBalanceRetriever, times(1)).getCBSIndicators(header, "111618", "5522145", "01");
        verify(rule.appGroupRetriever, times(1)).callRetrieveCBSAppGroup(header, "111618", true);
        assertEquals("Post returned from this address", testEligibility.getReasonText());
        assertFalse(testEligibility.isEligible());
    }

    @Test
    public void testEvaluateReturnsNull() throws SalsaInternalResourceNotAvailableException, SalsaInternalServiceException, SalsaExternalServiceException, EligibilityException {
        List<ProductArrangement> productArrangementList = new ArrayList();
        ruleDataHolder.setProductArrangements(ProductArrangementFacadeFactory.createProductArrangementFacade(productArrangementList));
        ruleDataHolder.setRuleParamValue("123");
        ruleDataHolder.setCustomerDetails(new Customer());
        ruleDataHolder.setProductArrangements(ProductArrangementFacadeFactory.createProductArrangementFacade(productArrangementList));
        testEligibility = rule.evaluate(ruleDataHolder, null, "112233", "334455");

        verify(rule.checkBalanceRetriever, times(0)).getCBSIndicators(header, "111618", "5522145", "01");
        verify(rule.appGroupRetriever, times(0)).callRetrieveCBSAppGroup(header, "111618", true);
        assertNull(testEligibility.getReasonText());
        assertTrue(testEligibility.isEligible());
    }

    @Test
    public void testEvaluateReturnsTrue() throws SalsaInternalResourceNotAvailableException, SalsaExternalServiceException, EligibilityException {
        List<ProductArrangement> productArrangementList = new ArrayList();
        ProductArrangement productArrangement = new ProductArrangement();
        productArrangement.setAssociatedProduct(new Product());
        productArrangement.getAssociatedProduct().setStatusCode("002");
        productArrangement.setLifecycleStatus("Effective");
        productArrangement.setFinancialInstitution(new Organisation());
        productArrangement.getFinancialInstitution().getHasOrganisationUnits().add(new OrganisationUnit());
        productArrangement.getFinancialInstitution().getHasOrganisationUnits().get(0).setSortCode("111618");
        productArrangement.setAccountNumber("5522145");
        productArrangementList.add(productArrangement);
        ruleDataHolder.setHeader(header);
        ruleDataHolder.setRuleParamValue("123");
        ruleDataHolder.setCustomerDetails(new Customer());
        when(rule.appGroupRetriever.callRetrieveCBSAppGroup(header, "111618", true)).thenReturn("01");
        when(rule.checkBalanceRetriever.getCBSIndicators(header, "111618", "5522145", "01")).thenReturn(dataHelper.getProdIndicators(0));
        ruleDataHolder.setProductArrangements(ProductArrangementFacadeFactory.createProductArrangementFacade(productArrangementList));

        testEligibility = rule.evaluate(ruleDataHolder, null, "112233", "334455");
        verify(rule.checkBalanceRetriever, times(1)).getCBSIndicators(header, "111618", "5522145", "01");
        verify(rule.appGroupRetriever, times(1)).callRetrieveCBSAppGroup(header, "111618", true);
        assertNull(testEligibility.getReasonText());
        assertTrue(testEligibility.isEligible());
    }

}

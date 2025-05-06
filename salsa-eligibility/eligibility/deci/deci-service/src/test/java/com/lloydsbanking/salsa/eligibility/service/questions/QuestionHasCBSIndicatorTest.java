package com.lloydsbanking.salsa.eligibility.service.questions;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.eligibility.service.downstream.AppGroupRetriever;
import com.lloydsbanking.salsa.eligibility.service.downstream.CheckBalanceRetriever;
import com.lloydsbanking.salsa.eligibility.service.utility.EligibilityException;
import com.lloydsbanking.salsa.eligibility.service.utility.ProductArrangementFacade;
import com.lloydsbanking.salsa.eligibility.service.utility.exceptions.SalsaExternalServiceException;
import com.lloydsbanking.salsa.eligibility.service.utility.exceptions.SalsaInternalResourceNotAvailableException;
import com.lloydsbanking.salsa.eligibility.service.utility.exceptions.SalsaInternalServiceException;
import com.lloydsbanking.salsa.eligibility.service.utility.header.GmoToGboRequestHeaderConverter;
import com.lloydsbanking.salsa.eligibility.wz.TestDataHelper;
import com.lloydsbanking.salsa.soap.cbs.e141.objects.E141Resp;
import lb_gbo_sales.messages.RequestHeader;
import lib_sim_bo.businessobjects.Product;
import lib_sim_bo.businessobjects.ProductArrangement;
import lib_sim_bo.businessobjects.ProductArrangementIndicator;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Category(UnitTest.class)
public class QuestionHasCBSIndicatorTest {

    private ProductArrangementFacade productArrangementFacade;

    private List<ProductArrangementFacade> productArrangementFacadeList;

    private CheckBalanceRetriever checkBalanceRetriever;

    private AppGroupRetriever appGroupRetriever;

    private RequestHeader header;

    private GmoToGboRequestHeaderConverter headerConverter;

    TestDataHelper dataHelper;

    @Before
    public void before(){
        productArrangementFacadeList = new ArrayList<>();
        checkBalanceRetriever = mock(CheckBalanceRetriever.class);
        appGroupRetriever = mock(AppGroupRetriever.class);
        dataHelper = new TestDataHelper();
        headerConverter = new GmoToGboRequestHeaderConverter();
        header = headerConverter.convert(dataHelper.createEligibilityRequestHeader(dataHelper.TEST_RETAIL_CHANNEL_ID, dataHelper.TEST_INTERACTION_ID, dataHelper.TEST_OCIS_ID, dataHelper.TEST_CUSTOMER_ID, dataHelper.TEST_CONTACT_POINT_ID));
    }

    @Test
    public void testHasCBSIndicatorReturnsTrue() throws SalsaInternalResourceNotAvailableException, SalsaInternalServiceException, SalsaExternalServiceException, EligibilityException {
        ProductArrangement productArrangement = new ProductArrangement();
        Product product = new Product();
        product.setStatusCode("002");
        productArrangement.setAssociatedProduct(product);
        productArrangement.setLifecycleStatus("Effective");


        List<Integer> productArrangementIndicatorList = new ArrayList();
        productArrangementIndicatorList.add(615);
        int indicator = 615;
        productArrangementFacade = new ProductArrangementFacade(productArrangement);
        productArrangementFacadeList.add(productArrangementFacade);
        when(appGroupRetriever.callRetrieveCBSAppGroup(header, "111618", true)).thenReturn("01");
        when(checkBalanceRetriever.getCBSIndicators(header, "111618", "5565721", "01")).thenReturn(dataHelper.getProdIndicators(indicator));

        boolean ask =QuestionHasCBSIndicator.pose()
            .givenCheckBalanceRetrieverClientInstance(checkBalanceRetriever)
            .givenAnAccountNumber("5565721")
            .givenASortCode("111618")
            .givenAppGroupRetrieverClientInstance(appGroupRetriever)
            .givenRequestHeader(header)
            .givenAProductList(productArrangementFacadeList)
            .givenAValue("615").ask();
        verify(checkBalanceRetriever, times(1)).getCBSIndicators(header, "111618", "5565721", "01");
        verify(appGroupRetriever, times(1)).callRetrieveCBSAppGroup(header, "111618", true);
        assertTrue(ask);
    }

    @Test
    public void testHasCBSIndicatorReturnsFalse() throws SalsaInternalResourceNotAvailableException, SalsaInternalServiceException, SalsaExternalServiceException, EligibilityException {
        ProductArrangement productArrangement = new ProductArrangement();
        Product product = new Product();
        product.setStatusCode("002");
        productArrangement.setAssociatedProduct(product);
        productArrangement.setLifecycleStatus("Effective");


        List<Integer> productArrangementIndicatorList = new ArrayList();
        productArrangementIndicatorList.add(625);
        int indicator = 625;
        productArrangementFacade = new ProductArrangementFacade(productArrangement);
        productArrangementFacadeList.add(productArrangementFacade);
        when(appGroupRetriever.callRetrieveCBSAppGroup(header, "111618", true)).thenReturn("01");
        when(checkBalanceRetriever.getCBSIndicators(header, "111618", "5565721", "01")).thenReturn(dataHelper.getProdIndicators(indicator));

        boolean ask =QuestionHasCBSIndicator.pose()
            .givenCheckBalanceRetrieverClientInstance(checkBalanceRetriever)
            .givenAnAccountNumber("5565721")
            .givenASortCode("111618")
            .givenAppGroupRetrieverClientInstance(appGroupRetriever)
            .givenRequestHeader(header)
            .givenAProductList(productArrangementFacadeList)
            .givenAValue("615").ask();
        verify(checkBalanceRetriever, times(1)).getCBSIndicators(header, "111618", "5565721", "01");
        verify(appGroupRetriever, times(1)).callRetrieveCBSAppGroup(header, "111618", true);
        assertFalse(ask);
    }

    @Test
    public void testHasCBSIndicatorReturnsFalseWhenProductArrangementsIsEmpty() throws SalsaInternalResourceNotAvailableException, SalsaInternalServiceException, SalsaExternalServiceException, EligibilityException {
        boolean ask =QuestionHasCBSIndicator.pose()
            .givenCheckBalanceRetrieverClientInstance(checkBalanceRetriever)
            .givenAnAccountNumber("5565721")
            .givenASortCode("111618")
            .givenAppGroupRetrieverClientInstance(appGroupRetriever)
            .givenRequestHeader(header)
            .givenAProductList(productArrangementFacadeList)
            .givenAValue("615").ask();
        verify(checkBalanceRetriever, times(0)).getCBSIndicators(header, "111618", "5565721", "01");
        verify(appGroupRetriever, times(0)).callRetrieveCBSAppGroup(header, "111618", true);
        assertFalse(ask);
    }

}
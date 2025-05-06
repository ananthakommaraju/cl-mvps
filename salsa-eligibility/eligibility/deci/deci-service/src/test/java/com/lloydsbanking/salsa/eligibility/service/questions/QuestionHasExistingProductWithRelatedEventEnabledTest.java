package com.lloydsbanking.salsa.eligibility.service.questions;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.eligibility.TestDataHelper;
import com.lloydsbanking.salsa.eligibility.service.downstream.MandateAccessDetailsRetriever;
import com.lloydsbanking.salsa.eligibility.service.utility.EligibilityException;
import com.lloydsbanking.salsa.eligibility.service.utility.ProductArrangementFacade;
import com.lloydsbanking.salsa.eligibility.service.utility.exceptions.SalsaExternalBusinessException;
import com.lloydsbanking.salsa.eligibility.service.utility.exceptions.SalsaExternalServiceException;
import com.lloydsbanking.salsa.eligibility.service.utility.exceptions.SalsaInternalResourceNotAvailableException;
import com.lloydsbanking.salsa.eligibility.service.utility.exceptions.SalsaInternalServiceException;
import lb_gbo_sales.ProductArrangement;
import lb_gbo_sales.businessobjects.ProductArrangementLifecycleStatus;
import lb_gbo_sales.messages.RequestHeader;
import lib_sim_bo.businessobjects.ExtSysProdIdentifier;
import lib_sim_bo.businessobjects.Product;
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
public class QuestionHasExistingProductWithRelatedEventEnabledTest {

    private List<ProductArrangementFacade> productArrangementsList;

    private ProductArrangementFacade productArrangementFacade;

    private ProductArrangement productArrangement;

    private lib_sim_bo.businessobjects.ProductArrangement productArrangementWZ;

    private MandateAccessDetailsRetriever retriever;

    @Before
    public void before() {
        productArrangementsList = new ArrayList<>();
        productArrangement = mock(ProductArrangement.class);
        productArrangementWZ=mock(lib_sim_bo.businessobjects.ProductArrangement.class);
        productArrangementFacade = new ProductArrangementFacade(productArrangement);
        productArrangementsList.add(productArrangementFacade);
        retriever=mock(MandateAccessDetailsRetriever.class);
    }

    @Test
    public void hasExistingProductWithRelatedEventEnabledShouldReturnFalseWhenStatusIsEqualsToDormantAndRelatedEventListContainsThreshold() throws SalsaInternalResourceNotAvailableException, SalsaInternalServiceException, SalsaExternalServiceException, EligibilityException {
        when(productArrangement.getLifecycleStatus()).thenReturn(ProductArrangementLifecycleStatus.DORMANT);
        ArrayList<String> relatedEvents = new ArrayList();
        relatedEvents.add("400");
        when(productArrangement.getRelatedEvents()).thenReturn(relatedEvents);
        boolean ask = QuestionHasExistingProductWithRelatedEventEnabled.pose().givenAProductList(productArrangementsList).givenAValue("400").ask();

        assertFalse(ask);
    }

    @Test
    public void hasExistingProductWithRelatedEventEnabledShouldReturnTrueWhenStatusIsNotEqualsToDormantAndRelatedEventListContainsThreshold() throws SalsaInternalResourceNotAvailableException, SalsaInternalServiceException, SalsaExternalServiceException, EligibilityException {
        when(productArrangement.getLifecycleStatus()).thenReturn(ProductArrangementLifecycleStatus.EFFECTIVE);
        ArrayList<String> relatedEvents = new ArrayList();
        relatedEvents.add("400");
        when(productArrangement.getRelatedEvents()).thenReturn(relatedEvents);

        boolean ask = QuestionHasExistingProductWithRelatedEventEnabled.pose().givenAProductList(productArrangementsList).givenAValue("400").ask();

        assertTrue(ask);

    }

    @Test
    public void testHasExistingProductWithRelatedEventEnabledShouldReturnFalseWhenStatusIsEqualsToDormantAndRelatedEventListContainsThresholdWZ() throws SalsaInternalResourceNotAvailableException, SalsaExternalBusinessException, EligibilityException {
        ProductArrangementFacade productArrangementFacade1 = new ProductArrangementFacade(productArrangementWZ);
        productArrangementsList.clear();
        productArrangementsList.add(productArrangementFacade1);
        when(productArrangementWZ.getLifecycleStatus()).thenReturn("dormant");
        Product associatedProduct= new Product();
        associatedProduct.getExternalSystemProductIdentifier().add(new ExtSysProdIdentifier());
        associatedProduct.getExternalSystemProductIdentifier().get(0).setSystemCode("0004");
        associatedProduct.getExternalSystemProductIdentifier().get(0).setProductIdentifier("3418209");
        when(productArrangementWZ.getAssociatedProduct()).thenReturn(associatedProduct);
        RequestHeader header=new TestDataHelper().createEligibilityRequestHeader(TestDataHelper.TEST_RETAIL_CHANNEL_ID, com.lloydsbanking.salsa.eligibility.TestDataHelper.TEST_INTERACTION_ID, com.lloydsbanking.salsa.eligibility.TestDataHelper.TEST_OCIS_ID, com.lloydsbanking.salsa.eligibility.TestDataHelper.TEST_CUSTOMER_ID, com.lloydsbanking.salsa.eligibility.TestDataHelper.TEST_CONTACT_POINT_ID);
        ArrayList<String> relatedEvents = new ArrayList();
        relatedEvents.add("250");
        when(retriever.getRelatedEvents(header, "T3418209")).thenReturn(relatedEvents);
        boolean ask = QuestionHasExistingProductWithRelatedEventEnabled
            .pose()
            .givenAWzRequest(true)
            .givenAMandateAccessDetailsRetrieverInstance(retriever)
            .givenAProductList(productArrangementsList)
            .givenAValue("400")
            .givenRequestHeader(header)
            .ask();

        verify(productArrangementWZ, times(2)).getLifecycleStatus();
        verify(productArrangementWZ, times(4)).getAssociatedProduct();
        verify(retriever).getRelatedEvents(header, "T3418209");

        assertFalse(ask);
    }

    @Test
    public void testHasExistingProductWithRelatedEventEnabledShouldReturnTrueWhenStatusIsEqualsToDormantAndRelatedEventListContainsThresholdWZ() throws SalsaInternalResourceNotAvailableException, SalsaExternalBusinessException, EligibilityException {
        ProductArrangementFacade productArrangementFacade1 = new ProductArrangementFacade(productArrangementWZ);
        productArrangementsList.clear();
        productArrangementsList.add(productArrangementFacade1);
        when(productArrangementWZ.getLifecycleStatus()).thenReturn("effective");
        Product associatedProduct= new Product();
        associatedProduct.getExternalSystemProductIdentifier().add(new ExtSysProdIdentifier());
        associatedProduct.getExternalSystemProductIdentifier().get(0).setSystemCode("0004");
        associatedProduct.getExternalSystemProductIdentifier().get(0).setProductIdentifier("3418209");
        when(productArrangementWZ.getAssociatedProduct()).thenReturn(associatedProduct);
        RequestHeader header=new TestDataHelper().createEligibilityRequestHeader(TestDataHelper.TEST_RETAIL_CHANNEL_ID, com.lloydsbanking.salsa.eligibility.TestDataHelper.TEST_INTERACTION_ID, com.lloydsbanking.salsa.eligibility.TestDataHelper.TEST_OCIS_ID, com.lloydsbanking.salsa.eligibility.TestDataHelper.TEST_CUSTOMER_ID, com.lloydsbanking.salsa.eligibility.TestDataHelper.TEST_CONTACT_POINT_ID);
        ArrayList<String> relatedEvents = new ArrayList();
        relatedEvents.add("400");
        when(retriever.getRelatedEvents(header, "T3418209")).thenReturn(relatedEvents);
        boolean ask = QuestionHasExistingProductWithRelatedEventEnabled
            .pose()
            .givenAWzRequest(true)
            .givenAMandateAccessDetailsRetrieverInstance(retriever)
            .givenAProductList(productArrangementsList)
            .givenAValue("400")
            .givenRequestHeader(header)
            .ask();

        verify(productArrangementWZ, times(2)).getLifecycleStatus();
        verify(productArrangementWZ, times(4)).getAssociatedProduct();
        verify(retriever).getRelatedEvents(header, "T3418209");

        assertTrue(ask);
    }

    @Test
    public void testHasExistingProductWithRelatedEventEnabledShouldReturnTrueWhenStatusIsEqualsToDormantAndRelatedEventListContainsThresholdWZForNewCustomer() throws SalsaInternalResourceNotAvailableException, SalsaExternalBusinessException, EligibilityException {
        ProductArrangementFacade productArrangementFacade1 = new ProductArrangementFacade(productArrangementWZ);
        productArrangementsList.clear();
        productArrangementsList.add(productArrangementFacade1);
        when(productArrangementWZ.getLifecycleStatus()).thenReturn("effective");
        Product associatedProduct= new Product();
        associatedProduct.getExternalSystemProductIdentifier().add(new ExtSysProdIdentifier());
        associatedProduct.getExternalSystemProductIdentifier().get(0).setSystemCode("0001");
        associatedProduct.getExternalSystemProductIdentifier().get(0).setProductIdentifier("3418209");
        when(productArrangementWZ.getAssociatedProduct()).thenReturn(associatedProduct);
        RequestHeader header=new TestDataHelper().createEligibilityRequestHeader(TestDataHelper.TEST_RETAIL_CHANNEL_ID, com.lloydsbanking.salsa.eligibility.TestDataHelper.TEST_INTERACTION_ID, com.lloydsbanking.salsa.eligibility.TestDataHelper.TEST_OCIS_ID, com.lloydsbanking.salsa.eligibility.TestDataHelper.TEST_CUSTOMER_ID, com.lloydsbanking.salsa.eligibility.TestDataHelper.TEST_CONTACT_POINT_ID);
        ArrayList<String> relatedEvents = new ArrayList();
        relatedEvents.add("400");
        when(retriever.getRelatedEvents(header, "L3418209")).thenReturn(relatedEvents);
        boolean ask = QuestionHasExistingProductWithRelatedEventEnabled
            .pose()
            .givenAWzRequest(true)
            .givenAMandateAccessDetailsRetrieverInstance(retriever)
            .givenAProductList(productArrangementsList)
            .givenAValue("400")
            .givenRequestHeader(header)
            .ask();

        verify(productArrangementWZ, times(2)).getLifecycleStatus();
        verify(productArrangementWZ, times(4)).getAssociatedProduct();
        verify(retriever).getRelatedEvents(header, "L3418209");

        assertTrue(ask);
    }

    @Test
    public void testHasExistingProductWithRelatedEventEnabledShouldReturnTrueWhenStatusIsNullAndRelatedEventListContainsThresholdWZForNewCustomer() throws SalsaInternalResourceNotAvailableException, SalsaExternalBusinessException, EligibilityException {
        ProductArrangementFacade productArrangementFacade1 = new ProductArrangementFacade(productArrangementWZ);
        productArrangementsList.clear();
        productArrangementsList.add(productArrangementFacade1);
        when(productArrangementWZ.getLifecycleStatus()).thenReturn(null);
        Product associatedProduct= new Product();
        associatedProduct.getExternalSystemProductIdentifier().add(new ExtSysProdIdentifier());
        associatedProduct.getExternalSystemProductIdentifier().get(0).setSystemCode("0001");
        associatedProduct.getExternalSystemProductIdentifier().get(0).setProductIdentifier("3418209");
        when(productArrangementWZ.getAssociatedProduct()).thenReturn(associatedProduct);
        RequestHeader header=new TestDataHelper().createEligibilityRequestHeader(TestDataHelper.TEST_RETAIL_CHANNEL_ID, com.lloydsbanking.salsa.eligibility.TestDataHelper.TEST_INTERACTION_ID, com.lloydsbanking.salsa.eligibility.TestDataHelper.TEST_OCIS_ID, com.lloydsbanking.salsa.eligibility.TestDataHelper.TEST_CUSTOMER_ID, com.lloydsbanking.salsa.eligibility.TestDataHelper.TEST_CONTACT_POINT_ID);
        ArrayList<String> relatedEvents = new ArrayList();
        relatedEvents.add("400");
        when(retriever.getRelatedEvents(header, "L3418209")).thenReturn(relatedEvents);
        boolean ask = QuestionHasExistingProductWithRelatedEventEnabled
            .pose()
            .givenAWzRequest(true)
            .givenAMandateAccessDetailsRetrieverInstance(retriever)
            .givenAProductList(productArrangementsList)
            .givenAValue("400")
            .givenRequestHeader(header)
            .ask();

        verify(productArrangementWZ, times(1)).getLifecycleStatus();
        verify(productArrangementWZ, times(4)).getAssociatedProduct();
        verify(retriever).getRelatedEvents(header, "L3418209");

        assertTrue(ask);
    }

}
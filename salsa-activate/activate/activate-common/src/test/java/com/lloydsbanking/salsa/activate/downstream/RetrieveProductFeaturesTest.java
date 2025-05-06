package com.lloydsbanking.salsa.activate.downstream;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.activate.TestDataHelper;
import com.lloydsbanking.salsa.logging.application.ProductTraceLog;
import lib_sim_bo.businessobjects.DepositArrangement;
import lib_sim_bo.businessobjects.ExtSysProdIdentifier;
import lib_sim_bo.businessobjects.Product;
import lib_sim_gmo.messages.RequestHeader;
import lib_sim_productsalesreferencedatamanager.ia_retrieveproductconditions.*;
import lib_sim_productsalesreferencedatamanager.messages.RetrieveProductConditionsRequest;
import lib_sim_productsalesreferencedatamanager.messages.RetrieveProductConditionsResponse;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Category(UnitTest.class)
public class RetrieveProductFeaturesTest {
    RetrieveProductFeatures retrieveProductFeatures;
    Product product;
    TestDataHelper testDataHelper;
    RetrieveProductConditionsRequest retrieveProductConditionsRequest;
    DepositArrangement depositArrangement;

    @Before
    public void setUp() {
        retrieveProductFeatures = new RetrieveProductFeatures();
        retrieveProductFeatures.prdClient = mock(PrdClient.class);
        testDataHelper = new TestDataHelper();
        depositArrangement = testDataHelper.createDepositArrangement("123");
        product = new Product();
        retrieveProductConditionsRequest = testDataHelper.createRetrieveProductConditionsRequest();
        retrieveProductFeatures.productTraceLog = mock(ProductTraceLog.class);
        when(retrieveProductFeatures.productTraceLog.getProductTraceEventMessage(any(Product.class), any(String.class))).thenReturn("ProdArrngemnt");

    }

    @Test
    public void testGetProductConditions() throws RetrieveProductConditionsInternalServiceErrorMsg, RetrieveProductConditionsDataNotAvailableErrorMsg, RetrieveProductConditionsResourceNotAvailableErrorMsg, RetrieveProductConditionsExternalServiceErrorMsg, RetrieveProductConditionsExternalBusinessErrorMsg {
        when(retrieveProductFeatures.prdClient.retrieveProductConditions(any(RetrieveProductConditionsRequest.class))).thenReturn(testDataHelper.createRetrieveProductConditionsResponse());
        product = retrieveProductFeatures.getProduct(depositArrangement, null, new RequestHeader());
        Assert.assertEquals("01000", product.getProductIdentifier());
        Assert.assertEquals("ABS", product.getProductType());
    }

    @Test
    public void testGetProductConditionsWithExternalProductIdentifier() throws RetrieveProductConditionsInternalServiceErrorMsg, RetrieveProductConditionsDataNotAvailableErrorMsg, RetrieveProductConditionsResourceNotAvailableErrorMsg, RetrieveProductConditionsExternalServiceErrorMsg, RetrieveProductConditionsExternalBusinessErrorMsg {
        RetrieveProductConditionsResponse retrieveProductConditionsResponse = testDataHelper.createRetrieveProductConditionsResponse();
        retrieveProductConditionsResponse.getProduct().get(0).getExternalSystemProductIdentifier().add(new ExtSysProdIdentifier());
        retrieveProductConditionsResponse.getProduct().get(0).getExternalSystemProductIdentifier().get(0).setSystemCode("00010");
        retrieveProductConditionsResponse.getProduct().get(0).getExternalSystemProductIdentifier().get(0).setProductIdentifier("00056");
        when(retrieveProductFeatures.prdClient.retrieveProductConditions(any(RetrieveProductConditionsRequest.class))).thenReturn(retrieveProductConditionsResponse);
        product = retrieveProductFeatures.getProduct(depositArrangement, null, new RequestHeader());
        Assert.assertEquals("01000", product.getProductIdentifier());
        Assert.assertEquals("ABS", product.getProductType());
        Assert.assertEquals("00056", product.getExternalSystemProductIdentifier().get(0).getProductIdentifier());

    }

    @Test
    public void testGetProductConditionsWithNoSystemCode() throws RetrieveProductConditionsInternalServiceErrorMsg, RetrieveProductConditionsDataNotAvailableErrorMsg, RetrieveProductConditionsResourceNotAvailableErrorMsg, RetrieveProductConditionsExternalServiceErrorMsg, RetrieveProductConditionsExternalBusinessErrorMsg {
        RetrieveProductConditionsResponse retrieveProductConditionsResponse = testDataHelper.createRetrieveProductConditionsResponse();
        retrieveProductConditionsResponse.getProduct().get(0).getExternalSystemProductIdentifier().add(new ExtSysProdIdentifier());
        retrieveProductConditionsResponse.getProduct().get(0).getExternalSystemProductIdentifier().get(0).setSystemCode("00040");
        when(retrieveProductFeatures.prdClient.retrieveProductConditions(any(RetrieveProductConditionsRequest.class))).thenReturn(retrieveProductConditionsResponse);
        product = retrieveProductFeatures.getProduct(depositArrangement, null, new RequestHeader());
        Assert.assertEquals("01000", product.getProductIdentifier());
        Assert.assertEquals("ABS", product.getProductType());
    }

    @Test
    public void testCheckResponseThrowsInternalServiceErrorMsg() throws RetrieveProductConditionsInternalServiceErrorMsg, RetrieveProductConditionsDataNotAvailableErrorMsg, RetrieveProductConditionsResourceNotAvailableErrorMsg, RetrieveProductConditionsExternalServiceErrorMsg, RetrieveProductConditionsExternalBusinessErrorMsg {
        when(retrieveProductFeatures.prdClient.retrieveProductConditions(any(RetrieveProductConditionsRequest.class))).thenThrow(RetrieveProductConditionsInternalServiceErrorMsg.class);
        product = retrieveProductFeatures.getProduct(depositArrangement, null, new RequestHeader());
    }

    @Test
    public void testCheckResponseWithProductAsNull() throws RetrieveProductConditionsInternalServiceErrorMsg, RetrieveProductConditionsDataNotAvailableErrorMsg, RetrieveProductConditionsResourceNotAvailableErrorMsg, RetrieveProductConditionsExternalServiceErrorMsg, RetrieveProductConditionsExternalBusinessErrorMsg {
        RetrieveProductConditionsResponse retrieveProductConditionsResponse = new RetrieveProductConditionsResponse();
        when(retrieveProductFeatures.prdClient.retrieveProductConditions(any(RetrieveProductConditionsRequest.class))).thenReturn(retrieveProductConditionsResponse);
        product = retrieveProductFeatures.getProduct(depositArrangement, null, new RequestHeader());
        assertNull(product);
    }

    @Test
    public void testGetProductConditionsProductSystemCodeNull() throws RetrieveProductConditionsInternalServiceErrorMsg, RetrieveProductConditionsDataNotAvailableErrorMsg, RetrieveProductConditionsResourceNotAvailableErrorMsg, RetrieveProductConditionsExternalServiceErrorMsg, RetrieveProductConditionsExternalBusinessErrorMsg {
        RetrieveProductConditionsResponse retrieveProductConditionsResponse = testDataHelper.createRetrieveProductConditionsResponse();
        retrieveProductConditionsResponse.getProduct().get(0).getExternalSystemProductIdentifier().add(new ExtSysProdIdentifier());
        retrieveProductConditionsResponse.getProduct().get(0).getExternalSystemProductIdentifier().get(0).setSystemCode(null);
        retrieveProductConditionsResponse.getProduct().get(0).getExternalSystemProductIdentifier().get(0).setProductIdentifier("00056");
        when(retrieveProductFeatures.prdClient.retrieveProductConditions(any(RetrieveProductConditionsRequest.class))).thenReturn(retrieveProductConditionsResponse);
        product = retrieveProductFeatures.getProduct(depositArrangement, null, new RequestHeader());
        Assert.assertEquals("01000", product.getProductIdentifier());
        Assert.assertEquals("ABS", product.getProductType());
        Assert.assertEquals("00056", product.getExternalSystemProductIdentifier().get(0).getProductIdentifier());

    }

    @Test
    public void testGetProductConditionsProductExtSysProdIdentifierNull() throws RetrieveProductConditionsInternalServiceErrorMsg, RetrieveProductConditionsDataNotAvailableErrorMsg, RetrieveProductConditionsResourceNotAvailableErrorMsg, RetrieveProductConditionsExternalServiceErrorMsg, RetrieveProductConditionsExternalBusinessErrorMsg {
        RetrieveProductConditionsResponse retrieveProductConditionsResponse = testDataHelper.createRetrieveProductConditionsResponse();
        retrieveProductConditionsResponse.getProduct().get(0).getExternalSystemProductIdentifier().add(null);
        retrieveProductConditionsResponse.getProduct().get(0).getExternalSystemProductIdentifier().add(new ExtSysProdIdentifier());
        retrieveProductConditionsResponse.getProduct().get(0).getExternalSystemProductIdentifier().get(1).setSystemCode("00056");
        retrieveProductConditionsResponse.getProduct().get(0).getExternalSystemProductIdentifier().get(1).setProductIdentifier("00056");
        when(retrieveProductFeatures.prdClient.retrieveProductConditions(any(RetrieveProductConditionsRequest.class))).thenReturn(retrieveProductConditionsResponse);
        product = retrieveProductFeatures.getProduct(depositArrangement, null, new RequestHeader());
    }
}

package com.lloydsbanking.salsa.ppae.service.convert;


import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.ppae.TestDataHelper;
import lib_sim_bo.businessobjects.ProductArrangement;
import lib_sim_productsalesreferencedatamanager.messages.RetrieveProductConditionsRequest;
import lib_sim_salesprocessmanagement.messages.ProcessPendingArrangementEventRequest;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import javax.xml.datatype.DatatypeConfigurationException;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

@Category(UnitTest.class)
public class PrdRequestFactoryTest {
    PrdRequestFactory rpcRequestFactory;
    TestDataHelper testDataHelper;
    ProductArrangement productArrangement;
    RetrieveProductConditionsRequest retrieveProductConditionsRequest;
    ProcessPendingArrangementEventRequest upStreamRequest;

    @Before
    public void setUp() throws DatatypeConfigurationException {
        testDataHelper = new TestDataHelper();
        productArrangement = testDataHelper.createProductArrangement();
        rpcRequestFactory = new PrdRequestFactory();
        retrieveProductConditionsRequest = new RetrieveProductConditionsRequest();
        upStreamRequest = new ProcessPendingArrangementEventRequest();

    }

    @Test
    public void testConvert() {
        productArrangement.setArrangementType("CC");
        retrieveProductConditionsRequest = rpcRequestFactory.convert(productArrangement,upStreamRequest.getHeader());
        assertEquals("isAccepted", retrieveProductConditionsRequest.getProduct().getStatusCode());

    }

    @Test
    public void testConvertForArrTypeEqualToSA() {
        productArrangement.getAssociatedProduct().setProductIdentifier("10005");
        productArrangement.setArrangementType("SA");
        rpcRequestFactory.convert(productArrangement, upStreamRequest.getHeader());

    }
    @Test
    public void testConvertWhenCreditCardIsNotSetAndAssociatedProductIsNull() {
        productArrangement.setArrangementType("ABC");
        productArrangement.setAssociatedProduct(null);
        retrieveProductConditionsRequest = rpcRequestFactory.convert(productArrangement,upStreamRequest.getHeader());
        assertNotNull(retrieveProductConditionsRequest);
        assertEquals(null,retrieveProductConditionsRequest.getProduct());

    }
}

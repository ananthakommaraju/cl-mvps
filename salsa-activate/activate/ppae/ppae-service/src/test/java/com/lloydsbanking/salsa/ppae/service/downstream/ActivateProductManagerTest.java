package com.lloydsbanking.salsa.ppae.service.downstream;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.ppae.TestDataHelper;
import com.lloydsbanking.salsa.ppae.service.client.ActivateProductCCClient;
import com.lloydsbanking.salsa.ppae.service.client.ActivateProductPCAClient;
import com.lloydsbanking.salsa.ppae.service.client.ActivateProductSAClient;
import lib_sim_bo.businessobjects.ProductArrangement;
import lib_sim_gmo.messages.RequestHeader;
import lib_sim_salesprocessmanagement.ia_activateproductarrangement.*;
import lib_sim_salesprocessmanagement.messages.ActivateProductArrangementRequest;
import lib_sim_salesprocessmanagement.messages.ActivateProductArrangementResponse;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import javax.xml.datatype.DatatypeConfigurationException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Category(UnitTest.class)
public class ActivateProductManagerTest {

    ActivateProductManager activateProductManager;
    ProductArrangement productArrangement;
    TestDataHelper testDataHelper;
    RequestHeader requestHeader;

    @Before
    public void setUp() throws DatatypeConfigurationException {
        activateProductManager = new ActivateProductManager();
        activateProductManager.activateProductCCClient = mock(ActivateProductCCClient.class);
        activateProductManager.activateProductPCAClient = mock(ActivateProductPCAClient.class);
        activateProductManager.activateProductSAClient = mock(ActivateProductSAClient.class);
        testDataHelper = new TestDataHelper();
        productArrangement = testDataHelper.createProductArrangement();
        requestHeader = testDataHelper.createPpaeRequestHeader("LTB");
    }

    @Test
    public void testActivateProductForCC() throws ActivateProductArrangementExternalSystemErrorMsg, ActivateProductArrangementExternalBusinessErrorMsg, ActivateProductArrangementInternalSystemErrorMsg, ActivateProductArrangementResourceNotAvailableErrorMsg, ActivateProductArrangementDataNotAvailableErrorMsg {
        when(activateProductManager.activateProductCCClient.activateProductArrangement(createActivateRequest(productArrangement, requestHeader))).thenReturn(createResponse());
        ActivateProductArrangementResponse response = activateProductManager.activateProduct(productArrangement, requestHeader);
        assertEquals("1006", response.getProductArrangement().getApplicationStatus());
        assertEquals("CC", response.getProductArrangement().getArrangementType());
    }

    @Test
    public void testActivateProductForPCA() throws ActivateProductArrangementExternalSystemErrorMsg, ActivateProductArrangementExternalBusinessErrorMsg, ActivateProductArrangementInternalSystemErrorMsg, ActivateProductArrangementResourceNotAvailableErrorMsg, ActivateProductArrangementDataNotAvailableErrorMsg {
        productArrangement.setArrangementType("CA");
        when(activateProductManager.activateProductPCAClient.activateProductArrangement(createActivateRequest(productArrangement, requestHeader))).thenReturn(createResponse());
        ActivateProductArrangementResponse response = activateProductManager.activateProduct(productArrangement, requestHeader);
        assertEquals("1006", response.getProductArrangement().getApplicationStatus());
        assertEquals("CA", response.getProductArrangement().getArrangementType());
    }

    @Test
    public void testActivateProductForSA() throws ActivateProductArrangementExternalSystemErrorMsg, ActivateProductArrangementExternalBusinessErrorMsg, ActivateProductArrangementInternalSystemErrorMsg, ActivateProductArrangementResourceNotAvailableErrorMsg, ActivateProductArrangementDataNotAvailableErrorMsg {
        productArrangement.setArrangementType("SA");
        when(activateProductManager.activateProductSAClient.activateProductArrangement(createActivateRequest(productArrangement, requestHeader))).thenReturn(createResponse());
        ActivateProductArrangementResponse response = activateProductManager.activateProduct(productArrangement, requestHeader);
        assertEquals("1006", response.getProductArrangement().getApplicationStatus());
        assertEquals("SA", response.getProductArrangement().getArrangementType());
    }

    @Test
    public void testActivateProductForDefault() throws ActivateProductArrangementExternalSystemErrorMsg, ActivateProductArrangementExternalBusinessErrorMsg, ActivateProductArrangementInternalSystemErrorMsg, ActivateProductArrangementResourceNotAvailableErrorMsg, ActivateProductArrangementDataNotAvailableErrorMsg {
        productArrangement.setArrangementType("FA");
        ActivateProductArrangementResponse response = activateProductManager.activateProduct(productArrangement, requestHeader);
        assertNotNull(response);
    }

    private ActivateProductArrangementRequest createActivateRequest(ProductArrangement productArrangement, RequestHeader requestHeader) {
        ActivateProductArrangementRequest activateProductArrangementRequest = new ActivateProductArrangementRequest();
        activateProductArrangementRequest.setProductArrangement(productArrangement);
        activateProductArrangementRequest.setHeader(requestHeader);
        activateProductArrangementRequest.setSourceSystemIdentifier("2");
        return activateProductArrangementRequest;
    }

    private ActivateProductArrangementResponse createResponse() {
        ActivateProductArrangementResponse activateProductArrangementResponse = new ActivateProductArrangementResponse();
        activateProductArrangementResponse.setProductArrangement(productArrangement);
        return activateProductArrangementResponse;
    }


}

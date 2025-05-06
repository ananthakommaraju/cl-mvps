package com.lloydsbanking.salsa.opasaving.service.downstream;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.downstream.soa.client.determinecustomerproductcondition.DCPCClient;
import com.lloydsbanking.salsa.header.gmo.HeaderRetriever;
import com.lloydsbanking.salsa.offer.exception.ExceptionUtility;
import com.lloydsbanking.salsa.opasaving.service.TestDataHelper;
import com.lloydsbanking.xml.schema.enterprise.lcsm5.services.productcustomermatching.v2.productcustomermatching.serviceparameters.iproductcustomermatching.DetermineCustomerProductConditionsRequest;
import com.lloydstsb.schema.infrastructure.soap.ContactPoint;
import com.lloydstsb.schema.infrastructure.soap.ServiceRequest;
import lib_sim_bo.businessobjects.ProductArrangement;
import lib_sim_gmo.exception.InternalServiceErrorMsg;
import lib_sim_gmo.exception.ResourceNotAvailableErrorMsg;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.oasis_open.docs.wss._2004._01.oasis_200401_wss_wssecurity_secext_1_0.SecurityHeaderType;

import javax.xml.ws.WebServiceException;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Category(UnitTest.class)
public class DCPCServiceClientTest {

    private DCPCServiceClient dcpcServiceClient;
    private TestDataHelper testDataHelper;

    @Before
    public void setUp() {
        testDataHelper = new TestDataHelper();
        dcpcServiceClient = new DCPCServiceClient();
        dcpcServiceClient.dcpcClient = mock(DCPCClient.class);
        dcpcServiceClient.headerRetriever = new HeaderRetriever();
        dcpcServiceClient.exceptionUtility = new ExceptionUtility();
    }

    @Test
    public void testRetrieveDCPCResponseSuccessful() throws InternalServiceErrorMsg, ResourceNotAvailableErrorMsg {
        ProductArrangement productArrangement = testDataHelper.generateOfferProductArrangementSavingRequest("LTB").getProductArrangement();
        when(dcpcServiceClient.dcpcClient.determineCustomerProductCondition(any(DetermineCustomerProductConditionsRequest.class), any(ContactPoint.class), any(ServiceRequest.class), any(SecurityHeaderType.class))).thenReturn(testDataHelper.createDcpcResponse());
        assertNotNull(dcpcServiceClient.retrieveDCPCResponse(testDataHelper.createDcpcRequest(testDataHelper.rpcResponse(), productArrangement, testDataHelper.createOpaSavingRequestHeader("LTB")), testDataHelper.createOpaSavingRequestHeader("LTB")));
    }

    @Test(expected = ResourceNotAvailableErrorMsg.class)
    public void testRetrieveDCPCResponseOfferProductArrangementResourceNotAvailableErrorMsg() throws InternalServiceErrorMsg, ResourceNotAvailableErrorMsg {
        ProductArrangement productArrangement = testDataHelper.generateOfferProductArrangementSavingRequest("LTB").getProductArrangement();
        when(dcpcServiceClient.dcpcClient.determineCustomerProductCondition(any(DetermineCustomerProductConditionsRequest.class), any(ContactPoint.class), any(ServiceRequest.class), any(SecurityHeaderType.class))).thenThrow(WebServiceException.class);
        dcpcServiceClient.retrieveDCPCResponse(testDataHelper.createDcpcRequest(testDataHelper.rpcResponse(), productArrangement, testDataHelper.createOpaSavingRequestHeader("LTB")), testDataHelper.createOpaSavingRequestHeader("LTB"));
    }
}
package com.lloydsbanking.salsa.opasaving.service.downstream;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.opasaving.service.TestDataHelper;
import com.lloydsbanking.xml.schema.enterprise.lcsm5.services.productcustomermatching.v2.productcustomermatching.serviceparameters.iproductcustomermatching.DetermineCustomerProductConditionsRequest;
import lib_sim_bo.businessobjects.DepositArrangement;
import lib_sim_gmo.exception.ExternalBusinessErrorMsg;
import lib_sim_gmo.exception.ExternalServiceErrorMsg;
import lib_sim_gmo.exception.InternalServiceErrorMsg;
import lib_sim_gmo.exception.ResourceNotAvailableErrorMsg;
import lib_sim_gmo.messages.RequestHeader;
import lib_sim_productsalesreferencedatamanager.messages.RetrieveProductConditionsResponse;
import lib_sim_salesprocessmanagement.messages.OfferProductArrangementRequest;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Category(UnitTest.class)
public class DCPCSeviceTest {

    private DCPCService dcpcService;
    private TestDataHelper testDataHelper;
    private RetrieveProductConditionsResponse rpcResp;
    private OfferProductArrangementRequest request;

    @Before
    public void setUp() {
        testDataHelper = new TestDataHelper();
        dcpcService = new DCPCService();
        request = testDataHelper.generateOfferProductArrangementSavingRequest("LTB");
        rpcResp = testDataHelper.rpcResponse();
        dcpcService.dcpcServiceClient = mock(DCPCServiceClient.class);
    }

    @Test
    public void testCallDCPCServiceSuccessful() throws ExternalBusinessErrorMsg, InternalServiceErrorMsg, ResourceNotAvailableErrorMsg, ExternalServiceErrorMsg {
        when(dcpcService.dcpcServiceClient.retrieveDCPCResponse(any(DetermineCustomerProductConditionsRequest.class), any(RequestHeader.class))).thenReturn(testDataHelper.createDcpcResponse());
        dcpcService.callDCPCService(rpcResp, (DepositArrangement) request.getProductArrangement(), testDataHelper.createOpaSavingRequestHeader("LTB"));
        assertEquals("pprid", request.getProductArrangement().getAssociatedProduct().getProductPreferentialRate().get(0).getPreferentialRateIdentifier());
    }

    @Test
    public void testCallDCPCServiceResponseNull() throws ExternalBusinessErrorMsg, InternalServiceErrorMsg, ResourceNotAvailableErrorMsg, ExternalServiceErrorMsg {
        when(dcpcService.dcpcServiceClient.retrieveDCPCResponse(any(DetermineCustomerProductConditionsRequest.class), any(RequestHeader.class))).thenReturn(null);
        dcpcService.callDCPCService(rpcResp, (DepositArrangement) request.getProductArrangement(), testDataHelper.createOpaSavingRequestHeader("LTB"));
        assertTrue(request.getProductArrangement().getAssociatedProduct().getProductPreferentialRate().isEmpty());
    }

    @Test
    public void testCallDCPCServiceRPCResponseNull() throws ExternalBusinessErrorMsg, InternalServiceErrorMsg, ResourceNotAvailableErrorMsg, ExternalServiceErrorMsg {
        when(dcpcService.dcpcServiceClient.retrieveDCPCResponse(any(DetermineCustomerProductConditionsRequest.class), any(RequestHeader.class))).thenReturn(null);
        dcpcService.callDCPCService(null, (DepositArrangement) request.getProductArrangement(), testDataHelper.createOpaSavingRequestHeader("LTB"));
        assertTrue(request.getProductArrangement().getAssociatedProduct().getProductPreferentialRate().isEmpty());
    }

    @Test
    public void testCallDCPCServiceExtSysProdIdentifierListEmpty() throws ExternalBusinessErrorMsg, InternalServiceErrorMsg, ResourceNotAvailableErrorMsg, ExternalServiceErrorMsg {
        when(dcpcService.dcpcServiceClient.retrieveDCPCResponse(any(DetermineCustomerProductConditionsRequest.class), any(RequestHeader.class))).thenReturn(testDataHelper.createDcpcResponse());
        rpcResp.getProduct().get(0).getProductoptions().clear();
        dcpcService.callDCPCService(rpcResp, (DepositArrangement) request.getProductArrangement(), testDataHelper.createOpaSavingRequestHeader("LTB"));
        assertEquals("pprid", request.getProductArrangement().getAssociatedProduct().getProductPreferentialRate().get(0).getPreferentialRateIdentifier());
    }
}

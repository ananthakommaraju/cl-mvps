package com.lloydsbanking.salsa.opasaving.service.downstream;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.downstream.switches.SwitchServiceImpl;
import com.lloydsbanking.salsa.offer.OfferException;
import com.lloydsbanking.salsa.offer.apply.downstream.RpcRetriever;
import com.lloydsbanking.salsa.opasaving.service.TestDataHelper;
import lb_gbo_sales.messages.RequestHeader;
import lib_sim_bo.businessobjects.DepositArrangement;
import lib_sim_gmo.exception.*;
import lib_sim_productsalesreferencedatamanager.messages.RetrieveProductConditionsRequest;
import lib_sim_productsalesreferencedatamanager.messages.RetrieveProductConditionsResponse;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@Category(UnitTest.class)
public class RPCServiceTest {
    private RPCService rpcService;
    private TestDataHelper dataHelper;

    @Before
    public void setUp() {
        dataHelper = new TestDataHelper();
        rpcService = new RPCService();
        rpcService.dcpcService = mock(DCPCService.class);
        rpcService.rpcRetriever = mock(RpcRetriever.class);
        rpcService.switchClient = mock(SwitchServiceImpl.class);
    }

    @Test
    public void callRPCServiceTestIsNewCustomerTrue() throws OfferException, ResourceNotAvailableErrorMsg, InternalServiceErrorMsg, DataNotAvailableErrorMsg, ExternalBusinessErrorMsg, ExternalServiceErrorMsg {
        DepositArrangement depositArrangement = dataHelper.createDepositArrangement();
        lib_sim_gmo.messages.RequestHeader opaSavingsreqHeader = dataHelper.createOpaSavingRequestHeader("LTB");
        depositArrangement.getPrimaryInvolvedParty().setNewCustomerIndicator(false);
        when(rpcService.switchClient.getBrandedSwitchValue("SW_SvngCstSegPrc", "LTB")).thenReturn(true);
        RetrieveProductConditionsResponse rpcResponse = dataHelper.rpcResponse();
        when(rpcService.rpcRetriever.callRpcService(any(RetrieveProductConditionsRequest.class))).thenReturn(rpcResponse);
        rpcService.callRPCService(depositArrangement, opaSavingsreqHeader);
        verify(rpcService.dcpcService).callDCPCService(rpcResponse, depositArrangement, opaSavingsreqHeader);
    }

    @Test(expected = OfferException.class)
    public void callRPCServiceResourceNotAvailableErrorTest() throws OfferException, ResourceNotAvailableErrorMsg, InternalServiceErrorMsg, DataNotAvailableErrorMsg, ExternalBusinessErrorMsg, ExternalServiceErrorMsg {
        DepositArrangement depositArrangement = dataHelper.createDepositArrangement();
        depositArrangement.getPrimaryInvolvedParty().setNewCustomerIndicator(false);
        when(rpcService.switchClient.getBrandedSwitchValue("SW_SvngCstSegPrc", "LTB")).thenReturn(true);
        when(rpcService.rpcRetriever.callRpcService(any(RetrieveProductConditionsRequest.class))).thenThrow(ResourceNotAvailableErrorMsg.class);
        rpcService.callRPCService(depositArrangement, dataHelper.createOpaSavingRequestHeader("LTB"));
    }

    @Test(expected = OfferException.class)
    public void callRPCServiceInternalServiceErrorTest() throws OfferException, ResourceNotAvailableErrorMsg, InternalServiceErrorMsg, DataNotAvailableErrorMsg, ExternalBusinessErrorMsg, ExternalServiceErrorMsg {
        DepositArrangement depositArrangement = dataHelper.createDepositArrangement();
        depositArrangement.getPrimaryInvolvedParty().setNewCustomerIndicator(false);
        when(rpcService.switchClient.getBrandedSwitchValue("SW_SvngCstSegPrc", "LTB")).thenReturn(true);
        when(rpcService.rpcRetriever.callRpcService(any(RetrieveProductConditionsRequest.class))).thenThrow(InternalServiceErrorMsg.class);
        rpcService.callRPCService(depositArrangement, dataHelper.createOpaSavingRequestHeader("LTB"));
    }

    @Test(expected = OfferException.class)
    public void callRPCServiceDataNotAvailableErrorTest() throws OfferException, ResourceNotAvailableErrorMsg, InternalServiceErrorMsg, DataNotAvailableErrorMsg, ExternalBusinessErrorMsg, ExternalServiceErrorMsg {
        DepositArrangement depositArrangement = dataHelper.createDepositArrangement();
        depositArrangement.getPrimaryInvolvedParty().setNewCustomerIndicator(false);
        when(rpcService.switchClient.getBrandedSwitchValue("SW_SvngCstSegPrc", "LTB")).thenReturn(true);
        when(rpcService.rpcRetriever.callRpcService(any(RetrieveProductConditionsRequest.class))).thenThrow(DataNotAvailableErrorMsg.class);
        rpcService.callRPCService(depositArrangement, dataHelper.createOpaSavingRequestHeader("LTB"));
    }

    @Test
    public void callRPCServiceTestIsNewCustomerFalse() throws OfferException {
        DepositArrangement depositArrangement = dataHelper.createDepositArrangement();
        rpcService.callRPCService(depositArrangement, dataHelper.createOpaSavingRequestHeader("LTB"));
    }

}

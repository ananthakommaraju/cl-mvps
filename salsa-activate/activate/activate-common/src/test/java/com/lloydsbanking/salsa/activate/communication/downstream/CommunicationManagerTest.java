package com.lloydsbanking.salsa.activate.communication.downstream;


import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.activate.TestDataHelper;
import com.lloydsbanking.salsa.activate.communication.convert.CommunicationRequestFactory;
import com.lloydsbanking.salsa.downstream.cm.client.ScheduleCommunicationClient;
import com.lloydsbanking.salsa.downstream.cm.client.SendCommunicationClient;
import lib_sim_bo.businessobjects.DepositArrangement;
import lib_sim_communicationmanager.id_communicationeventscheduler.*;
import lib_sim_communicationmanager.id_communicationmanagerrouter.*;
import lib_sim_communicationmanager.messages.ScheduleCommunicationRequest;
import lib_sim_communicationmanager.messages.ScheduleCommunicationResponse;
import lib_sim_communicationmanager.messages.SendCommunicationRequest;
import lib_sim_communicationmanager.messages.SendCommunicationResponse;
import lib_sim_gmo.messages.RequestHeader;
import lib_sim_salesprocessmanagement.ia_activateproductarrangement.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static org.mockito.Mockito.*;

@Category(UnitTest.class)
public class CommunicationManagerTest {

    CommunicationManager communicationManager;
    TestDataHelper testDataHelper;
    RequestHeader requestHeader;
    DepositArrangement depositArrangement;

    @Before
    public void setUp() {
        communicationManager = new CommunicationManager();
        communicationManager.sendCommunicationClient = mock(SendCommunicationClient.class);
        communicationManager.communicationRequestFactory = mock(CommunicationRequestFactory.class);
        testDataHelper = new TestDataHelper();
        requestHeader = new RequestHeader();
        depositArrangement = testDataHelper.createDepositArrangement("hgr");
        communicationManager.scheduleCommunicationClient=mock(ScheduleCommunicationClient.class);
    }

    @Test
    public void testcallSendCommunicationService() throws ActivateProductArrangementInternalSystemErrorMsg, ActivateProductArrangementExternalBusinessErrorMsg, ActivateProductArrangementExternalSystemErrorMsg, ActivateProductArrangementResourceNotAvailableErrorMsg, ActivateProductArrangementDataNotAvailableErrorMsg, SendCommunicationResourceNotAvailableErrorMsg, SendCommunicationExternalServiceErrorMsg, SendCommunicationExternalBusinessErrorMsg, SendCommunicationDataNotAvailableErrorMsg, SendCommunicationInternalServiceErrorMsg {
        communicationManager.callSendCommunicationService(depositArrangement, "xyz", requestHeader, null, "EMAIL");
        when(communicationManager.sendCommunicationClient.sendCommunication(communicationManager.communicationRequestFactory.convertToSendCommunicationRequest(depositArrangement, "xyz", requestHeader, null, "EMAIL"))).thenReturn(new SendCommunicationResponse());
        verify(communicationManager.sendCommunicationClient).sendCommunication(communicationManager.communicationRequestFactory.convertToSendCommunicationRequest(depositArrangement, "xyz", requestHeader, null, "EMAIL"));
    }

    @Test
    public void testcallSendCommunicationServiceWithException() throws ActivateProductArrangementInternalSystemErrorMsg, ActivateProductArrangementExternalBusinessErrorMsg, ActivateProductArrangementExternalSystemErrorMsg, ActivateProductArrangementResourceNotAvailableErrorMsg, ActivateProductArrangementDataNotAvailableErrorMsg, SendCommunicationResourceNotAvailableErrorMsg, SendCommunicationExternalServiceErrorMsg, SendCommunicationExternalBusinessErrorMsg, SendCommunicationDataNotAvailableErrorMsg, SendCommunicationInternalServiceErrorMsg {
        when(communicationManager.sendCommunicationClient.sendCommunication(any(SendCommunicationRequest.class))).thenThrow(new SendCommunicationInternalServiceErrorMsg());
        communicationManager.callSendCommunicationService(depositArrangement, "xyz", requestHeader, null, "EMAIL");
    }

    @Test
    public void testScheduleCommunicationService() throws ScheduleCommunicationResourceNotAvailableErrorMsg, ScheduleCommunicationExternalBusinessErrorMsg, ScheduleCommunicationExternalServiceErrorMsg, ScheduleCommunicationInternalServiceErrorMsg, ScheduleCommunicationDataNotAvailableErrorMsg {
        communicationManager.callScheduleCommunicationService(depositArrangement, "xyz", requestHeader, null, "EMAIL");
        when(communicationManager.scheduleCommunicationClient.scheduleCommunication(communicationManager.communicationRequestFactory.convertToScheduleCommunicationRequest(depositArrangement, "xyz", requestHeader, null, "EMAIL"))).thenReturn(new ScheduleCommunicationResponse());
        verify(communicationManager.scheduleCommunicationClient).scheduleCommunication(communicationManager.communicationRequestFactory.convertToScheduleCommunicationRequest(depositArrangement, "xyz", requestHeader, null, "EMAIL"));
    }

    @Test
    public void testScheduleCommunicationServiceWithException() throws ScheduleCommunicationResourceNotAvailableErrorMsg, ScheduleCommunicationExternalBusinessErrorMsg, ScheduleCommunicationExternalServiceErrorMsg, ScheduleCommunicationInternalServiceErrorMsg, ScheduleCommunicationDataNotAvailableErrorMsg {
        when(communicationManager.scheduleCommunicationClient.scheduleCommunication(any(ScheduleCommunicationRequest.class))).thenThrow(new ScheduleCommunicationInternalServiceErrorMsg());
        communicationManager.callScheduleCommunicationService(depositArrangement, "xyz", requestHeader, null, "EMAIL");
    }
}

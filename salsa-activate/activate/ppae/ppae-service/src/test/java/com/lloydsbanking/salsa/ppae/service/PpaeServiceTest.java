package com.lloydsbanking.salsa.ppae.service;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.activate.communication.convert.NotificationEmailTemplates;
import com.lloydsbanking.salsa.activate.communication.downstream.CommunicationManager;
import com.lloydsbanking.salsa.activate.constants.ActivateCommonConstant;
import com.lloydsbanking.salsa.activate.downstream.LookUpValueRetriever;
import com.lloydsbanking.salsa.downstream.pam.service.RetrievePamService;
import com.lloydsbanking.salsa.logging.application.ProductArrangementTraceLog;
import com.lloydsbanking.salsa.ppae.TestDataHelper;
import com.lloydsbanking.salsa.ppae.logging.PpaeLogService;
import com.lloydsbanking.salsa.ppae.service.appstatus.*;
import com.lloydsbanking.salsa.ppae.service.process.AwaitingFulfilmentApplicationProcessor;
import com.lloydsbanking.salsa.ppae.service.process.AwaitingPostFulfilmentProcessor;
import com.lloydsbanking.salsa.ppae.service.process.AwaitingReferralLRAApplicationProcessor;
import lib_sim_bo.businessobjects.ProductArrangement;
import lib_sim_gmo.exception.DataNotAvailableErrorMsg;
import lib_sim_gmo.exception.InternalServiceErrorMsg;
import lib_sim_gmo.exception.ResourceNotAvailableErrorMsg;
import lib_sim_gmo.messages.RequestHeader;
import lib_sim_salesprocessmanagement.messages.ProcessPendingArrangementEventRequest;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import javax.xml.datatype.DatatypeConfigurationException;

import static org.mockito.Mockito.*;

@Category(UnitTest.class)
public class PpaeServiceTest {
    PpaeService ppaeService;
    RetrievePamService retrievePamService;
    ProductArrangement productArrangement;
    TestDataHelper testDataHelper = new TestDataHelper();
    ProcessPendingArrangementEventRequest request = testDataHelper.createPpaeRequest("1", "LTB");

    @Before
    public void setUp() throws DatatypeConfigurationException, DataNotAvailableErrorMsg, ResourceNotAvailableErrorMsg, InternalServiceErrorMsg {
        ppaeService = new PpaeService();
        retrievePamService = new RetrievePamService();
        ppaeService.ppaeLogService = mock(PpaeLogService.class);
        ppaeService.lookUpValueRetriever = mock(LookUpValueRetriever.class);
        ppaeService.retrievePamService = mock(RetrievePamService.class);
        ppaeService.awaitingRescoreProcessor = mock(AwaitingRescoreProcessor.class);
        productArrangement = testDataHelper.createProductArrangement();
        ppaeService.applicationStatusIdentifier = mock(ApplicationStatusIdentifier.class);
        ppaeService.communicationManager = mock(CommunicationManager.class);
        ppaeService.awaitingRescoreProcessor = mock(AwaitingRescoreProcessor.class);
        ppaeService.awaitingManualIDProcessor = mock(AwaitingManualIDProcessor.class);
        ppaeService.ccaSignedProcessor = mock(CcaSignedProcessor.class);
        ppaeService.notificationEmailTemplates = mock(NotificationEmailTemplates.class);
        ppaeService.processPendingApplications = mock(ProcessPendingApplications.class);
        ppaeService.awaitingPostFulfilmentProcessor = mock(AwaitingPostFulfilmentProcessor.class);
        ppaeService.processPendingApplications = mock(ProcessPendingApplications.class);
        ppaeService.awaitingReferralLRAApplicationProcessor = mock(AwaitingReferralLRAApplicationProcessor.class);
        ppaeService.awaitingFulfilmentApplicationProcessor = mock(AwaitingFulfilmentApplicationProcessor.class);
        ppaeService.productArrangementTraceLog=mock(ProductArrangementTraceLog.class);
        when(ppaeService.lookUpValueRetriever.retrieveContactPointId(request.getHeader().getChannelId(), request.getHeader())).thenReturn("0000777505");
        when(ppaeService.retrievePamService.retrievePendingArrangement(request.getHeader().getChannelId(), request.getApplicationId(), null)).thenReturn(productArrangement);
    }

    @Test
    public void testProcessPendingArrangementEvent() throws DataNotAvailableErrorMsg, ResourceNotAvailableErrorMsg, InternalServiceErrorMsg {
        when(ppaeService.productArrangementTraceLog.getApplicationTraceEventMessage(any(ProductArrangement.class),any(String.class))).thenReturn("ProductArrangement");
        ppaeService.processPendingArrangementEvent(request);
        verify(ppaeService.ppaeLogService).initialiseContext(request.getHeader());
        verify(ppaeService.retrievePamService).retrievePendingArrangement(request.getHeader().getChannelId(), request.getApplicationId(), null);
        verify(ppaeService.applicationStatusIdentifier).retrieveAppStatusToProceed(productArrangement.getApplicationStatus(), productArrangement.getArrangementType(), productArrangement.getReferral());

    }

    @Test
    public void testProcessPendingArrangementEventForAwaitingRescore() throws DataNotAvailableErrorMsg, ResourceNotAvailableErrorMsg, InternalServiceErrorMsg {
        when(ppaeService.productArrangementTraceLog.getApplicationTraceEventMessage(any(ProductArrangement.class),any(String.class))).thenReturn("ProductArrangement");
        when(ppaeService.applicationStatusIdentifier.retrieveAppStatusToProceed(productArrangement.getApplicationStatus(), productArrangement.getArrangementType(), productArrangement.getReferral())).thenReturn("AWAITING_RESCORE");
        ppaeService.processPendingArrangementEvent(request);
        verify(ppaeService.ppaeLogService).initialiseContext(request.getHeader());
        verify(ppaeService.retrievePamService).retrievePendingArrangement(request.getHeader().getChannelId(), request.getApplicationId(), null);
        verify(ppaeService.applicationStatusIdentifier).retrieveAppStatusToProceed(productArrangement.getApplicationStatus(), productArrangement.getArrangementType(), productArrangement.getReferral());
        verify(ppaeService.processPendingApplications).modifyAndActivatePendingApplications(any(ProductArrangement.class), any(RequestHeader.class), any(PpaeInvocationIdentifier.class));
    }

    @Test
    public void testProcessPendingArrangementEventForAwaitingManualId() throws DataNotAvailableErrorMsg, ResourceNotAvailableErrorMsg, InternalServiceErrorMsg {
        when(ppaeService.productArrangementTraceLog.getApplicationTraceEventMessage(any(ProductArrangement.class),any(String.class))).thenReturn("ProductArrangement");
        when(ppaeService.applicationStatusIdentifier.retrieveAppStatusToProceed(productArrangement.getApplicationStatus(), productArrangement.getArrangementType(), productArrangement.getReferral())).thenReturn("AWAITING_MANUAL_ID_V");
        ppaeService.processPendingArrangementEvent(request);
        verify(ppaeService.ppaeLogService).initialiseContext(request.getHeader());
        verify(ppaeService.retrievePamService).retrievePendingArrangement(request.getHeader().getChannelId(), request.getApplicationId(), null);
        verify(ppaeService.applicationStatusIdentifier).retrieveAppStatusToProceed(productArrangement.getApplicationStatus(), productArrangement.getArrangementType(), productArrangement.getReferral());
        verify(ppaeService.awaitingManualIDProcessor).processCommunications(productArrangement, request);
        verify(ppaeService.processPendingApplications).modifyAndActivatePendingApplications(any(ProductArrangement.class), any(RequestHeader.class), any(PpaeInvocationIdentifier.class));
    }

    @Test
    public void testProcessPendingArrangementEventForAbandoned() throws DataNotAvailableErrorMsg, ResourceNotAvailableErrorMsg, InternalServiceErrorMsg {
        when(ppaeService.productArrangementTraceLog.getApplicationTraceEventMessage(any(ProductArrangement.class),any(String.class))).thenReturn("ProductArrangement");
        when(ppaeService.applicationStatusIdentifier.retrieveAppStatusToProceed(productArrangement.getApplicationStatus(), productArrangement.getArrangementType(), productArrangement.getReferral())).thenReturn("ABANDONED");
        when(ppaeService.notificationEmailTemplates.getNotificationEmailForAbandonedStatus(productArrangement.getArrangementType())).thenReturn("CA_APPL_ABANDONED_MSG");
        ppaeService.processPendingArrangementEvent(request);
        verify(ppaeService.ppaeLogService).initialiseContext(request.getHeader());
        verify(ppaeService.retrievePamService).retrievePendingArrangement(request.getHeader().getChannelId(), request.getApplicationId(), null);
        verify(ppaeService.applicationStatusIdentifier).retrieveAppStatusToProceed(productArrangement.getApplicationStatus(), productArrangement.getArrangementType(), productArrangement.getReferral());
        verify(ppaeService.notificationEmailTemplates).getNotificationEmailForAbandonedStatus(productArrangement.getArrangementType());
        verify(ppaeService.processPendingApplications).modifyAndActivatePendingApplications(any(ProductArrangement.class), any(RequestHeader.class), any(PpaeInvocationIdentifier.class));
    }

    @Test
    public void testProcessPendingArrangementEventForNewCar() throws DataNotAvailableErrorMsg, ResourceNotAvailableErrorMsg, InternalServiceErrorMsg {
        when(ppaeService.productArrangementTraceLog.getApplicationTraceEventMessage(any(ProductArrangement.class),any(String.class))).thenReturn("ProductArrangement");
        when(ppaeService.applicationStatusIdentifier.retrieveAppStatusToProceed(productArrangement.getApplicationStatus(), productArrangement.getArrangementType(), productArrangement.getReferral())).thenReturn("NEW_CAR");
        ppaeService.processPendingArrangementEvent(request);
        verify(ppaeService.ppaeLogService).initialiseContext(request.getHeader());
        verify(ppaeService.retrievePamService).retrievePendingArrangement(request.getHeader().getChannelId(), request.getApplicationId(), null);
        verify(ppaeService.applicationStatusIdentifier).retrieveAppStatusToProceed(productArrangement.getApplicationStatus(), productArrangement.getArrangementType(), productArrangement.getReferral());
        verify(ppaeService.communicationManager).callSendCommunicationService(productArrangement, "CAR_FINANCE_DD_REMINDER_EMAIL", request.getHeader(), null, ActivateCommonConstant.CommunicationTypes.EMAIL);
        verify(ppaeService.processPendingApplications).modifyAndActivatePendingApplications(any(ProductArrangement.class), any(RequestHeader.class), any(PpaeInvocationIdentifier.class));
    }

    @Test
    public void testProcessPendingArrangementEventForAwaitingPostFulfilment() throws DataNotAvailableErrorMsg, ResourceNotAvailableErrorMsg, InternalServiceErrorMsg {
        when(ppaeService.productArrangementTraceLog.getApplicationTraceEventMessage(any(ProductArrangement.class),any(String.class))).thenReturn("ProductArrangement");
        when(ppaeService.applicationStatusIdentifier.retrieveAppStatusToProceed(productArrangement.getApplicationStatus(), productArrangement.getArrangementType(), productArrangement.getReferral())).thenReturn("AWAITING_POST_FULFILMENT_PROCESS");
        ppaeService.processPendingArrangementEvent(request);
        verify(ppaeService.ppaeLogService).initialiseContext(request.getHeader());
        verify(ppaeService.retrievePamService).retrievePendingArrangement(request.getHeader().getChannelId(), request.getApplicationId(), null);
        verify(ppaeService.applicationStatusIdentifier).retrieveAppStatusToProceed(productArrangement.getApplicationStatus(), productArrangement.getArrangementType(), productArrangement.getReferral());
        verify(ppaeService.awaitingPostFulfilmentProcessor).process(any(ProductArrangement.class), any(ProcessPendingArrangementEventRequest.class), any(PpaeInvocationIdentifier.class));
        verify(ppaeService.processPendingApplications).modifyAndActivatePendingApplications(any(ProductArrangement.class), any(RequestHeader.class), any(PpaeInvocationIdentifier.class));
    }

    @Test
    public void testProcessPendingArrangementEventForCcaSigned() throws DataNotAvailableErrorMsg, ResourceNotAvailableErrorMsg, InternalServiceErrorMsg {
        when(ppaeService.productArrangementTraceLog.getApplicationTraceEventMessage(any(ProductArrangement.class),any(String.class))).thenReturn("ProductArrangement");
        when(ppaeService.applicationStatusIdentifier.retrieveAppStatusToProceed(productArrangement.getApplicationStatus(), productArrangement.getArrangementType(), productArrangement.getReferral())).thenReturn("CCA_SIGNED_CCA_PENDING");
        ppaeService.processPendingArrangementEvent(request);
        verify(ppaeService.ppaeLogService).initialiseContext(request.getHeader());
        verify(ppaeService.retrievePamService).retrievePendingArrangement(request.getHeader().getChannelId(), request.getApplicationId(), null);
        verify(ppaeService.applicationStatusIdentifier).retrieveAppStatusToProceed(productArrangement.getApplicationStatus(), productArrangement.getArrangementType(), productArrangement.getReferral());
        verify(ppaeService.ccaSignedProcessor).processingPendingApplications(any(ProductArrangement.class), any(ProcessPendingArrangementEventRequest.class), any(PpaeInvocationIdentifier.class));
        verify(ppaeService.processPendingApplications).modifyAndActivatePendingApplications(any(ProductArrangement.class), any(RequestHeader.class), any(PpaeInvocationIdentifier.class));
    }

    @Test
    public void testProcessPendingArrangementEventForLRA() throws DataNotAvailableErrorMsg, ResourceNotAvailableErrorMsg, InternalServiceErrorMsg {
        when(ppaeService.productArrangementTraceLog.getApplicationTraceEventMessage(any(ProductArrangement.class),any(String.class))).thenReturn("ProductArrangement");
        when(ppaeService.applicationStatusIdentifier.retrieveAppStatusToProceed(productArrangement.getApplicationStatus(), productArrangement.getArrangementType(), productArrangement.getReferral())).thenReturn("AWAITING_REFERRAL_LRA");
        ppaeService.processPendingArrangementEvent(request);
        verify(ppaeService.ppaeLogService).initialiseContext(request.getHeader());
        verify(ppaeService.retrievePamService).retrievePendingArrangement(request.getHeader().getChannelId(), request.getApplicationId(), null);
        verify(ppaeService.applicationStatusIdentifier).retrieveAppStatusToProceed(productArrangement.getApplicationStatus(), productArrangement.getArrangementType(), productArrangement.getReferral());
        verify(ppaeService.awaitingReferralLRAApplicationProcessor).process(productArrangement, request.getHeader());
        verify(ppaeService.processPendingApplications).modifyAndActivatePendingApplications(any(ProductArrangement.class), any(RequestHeader.class), any(PpaeInvocationIdentifier.class));
    }

    @Test
    public void testProcessPendingArrangementEventForAwaitingFulfilment() throws DataNotAvailableErrorMsg, ResourceNotAvailableErrorMsg, InternalServiceErrorMsg {
        when(ppaeService.productArrangementTraceLog.getApplicationTraceEventMessage(any(ProductArrangement.class),any(String.class))).thenReturn("ProductArrangement");
        when(ppaeService.applicationStatusIdentifier.retrieveAppStatusToProceed(productArrangement.getApplicationStatus(), productArrangement.getArrangementType(), productArrangement.getReferral())).thenReturn("AWAITING_FULFILMENT");
        ppaeService.processPendingArrangementEvent(request);
        verify(ppaeService.ppaeLogService).initialiseContext(request.getHeader());
        verify(ppaeService.retrievePamService).retrievePendingArrangement(request.getHeader().getChannelId(), request.getApplicationId(), null);
        verify(ppaeService.applicationStatusIdentifier).retrieveAppStatusToProceed(productArrangement.getApplicationStatus(), productArrangement.getArrangementType(), productArrangement.getReferral());
        verify(ppaeService.awaitingFulfilmentApplicationProcessor).process(productArrangement, request.getHeader());
        verify(ppaeService.processPendingApplications).modifyAndActivatePendingApplications(any(ProductArrangement.class), any(RequestHeader.class), any(PpaeInvocationIdentifier.class));
    }

    @Test
    public void testProcessPendingArrangementEventForAwaitingReferral() throws DataNotAvailableErrorMsg, ResourceNotAvailableErrorMsg, InternalServiceErrorMsg {
        when(ppaeService.productArrangementTraceLog.getApplicationTraceEventMessage(any(ProductArrangement.class),any(String.class))).thenReturn("ProductArrangement");
        when(ppaeService.applicationStatusIdentifier.retrieveAppStatusToProceed(productArrangement.getApplicationStatus(), productArrangement.getArrangementType(), productArrangement.getReferral())).thenReturn("AWAITING_REFERRAL");
        ppaeService.processPendingArrangementEvent(request);
        verify(ppaeService.ppaeLogService).initialiseContext(request.getHeader());
        verify(ppaeService.retrievePamService).retrievePendingArrangement(request.getHeader().getChannelId(), request.getApplicationId(), null);
        verify(ppaeService.applicationStatusIdentifier).retrieveAppStatusToProceed(productArrangement.getApplicationStatus(), productArrangement.getArrangementType(), productArrangement.getReferral());
        verify(ppaeService.processPendingApplications).modifyAndActivatePendingApplications(any(ProductArrangement.class), any(RequestHeader.class), any(PpaeInvocationIdentifier.class));
    }

    @Test
    public void testProcessPendingArrangementEventForNullAppStatus() throws DataNotAvailableErrorMsg, ResourceNotAvailableErrorMsg, InternalServiceErrorMsg {
        when(ppaeService.productArrangementTraceLog.getApplicationTraceEventMessage(any(ProductArrangement.class),any(String.class))).thenReturn("ProductArrangement");
        when(ppaeService.applicationStatusIdentifier.retrieveAppStatusToProceed(productArrangement.getApplicationStatus(), productArrangement.getArrangementType(), productArrangement.getReferral())).thenReturn(null);
        ppaeService.processPendingArrangementEvent(request);
        verify(ppaeService.ppaeLogService).initialiseContext(request.getHeader());
        verify(ppaeService.retrievePamService).retrievePendingArrangement(request.getHeader().getChannelId(), request.getApplicationId(), null);
        verify(ppaeService.applicationStatusIdentifier).retrieveAppStatusToProceed(productArrangement.getApplicationStatus(), productArrangement.getArrangementType(), productArrangement.getReferral());
    }
}
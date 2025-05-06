package com.lloydsbanking.salsa.activate.administer;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.activate.TestDataHelper;
import com.lloydsbanking.salsa.activate.administer.downstream.CreateTask;
import com.lloydsbanking.salsa.activate.administer.downstream.ReferralTeamRetriever;
import com.lloydsbanking.salsa.activate.administer.downstream.UpdatedCreditDecisionRetriever;
import com.lloydsbanking.salsa.activate.communication.convert.NotificationEmailTemplates;
import com.lloydsbanking.salsa.activate.communication.downstream.CommunicationManager;
import com.lloydsbanking.salsa.activate.helper.ApplicationDetails;
import com.lloydsbanking.salsa.downstream.pam.model.ReferralTeams;
import lib_sim_bo.businessobjects.*;
import lib_sim_gmo.messages.RequestHeader;
import lib_sim_salesprocessmanagement.ia_activateproductarrangement.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.*;

@Category(UnitTest.class)
public class AdministerReferredServiceTest {
    private AdministerReferredService administerReferredService;
    private TestDataHelper testDataHelper;
    private RequestHeader requestHeader;
    private DepositArrangement depositArrangement;
    private ApplicationDetails applicationDetails;

    @Before
    public void setUp() {
        administerReferredService = new AdministerReferredService();
        testDataHelper = new TestDataHelper();
        requestHeader = testDataHelper.createApaRequestHeader();
        administerReferredService.createTask = mock(CreateTask.class);
        administerReferredService.referralTeamRetriever = mock(ReferralTeamRetriever.class);
        administerReferredService.updatedCreditDecisionRetriever = mock(UpdatedCreditDecisionRetriever.class);
        administerReferredService.administerReferredLookUpData = mock(AdministerReferredLookUpData.class);
        administerReferredService.communicationManager = mock(CommunicationManager.class);
        administerReferredService.notificationEmailTemplates = mock(NotificationEmailTemplates.class);
        depositArrangement = testDataHelper.createDepositArrangement("95412");
        depositArrangement.getPrimaryInvolvedParty().getCustomerScore().add(new CustomerScore());
        depositArrangement.setApplicationStatus("1");
        depositArrangement.getPrimaryInvolvedParty().getCustomerScore().get(0).getReferralCode().add(new ReferralCode());
        depositArrangement.getPrimaryInvolvedParty().getCustomerScore().get(0).getReferralCode().get(0).setCode("002");
        applicationDetails = testDataHelper.createApplicationDetails();
        applicationDetails.setApplicationStatus("2");
        applicationDetails.setScoreResult("2");
    }

    @Test
    public void testAdministerReferredArrangement() throws ActivateProductArrangementExternalSystemErrorMsg, ActivateProductArrangementExternalBusinessErrorMsg, ActivateProductArrangementInternalSystemErrorMsg, ActivateProductArrangementResourceNotAvailableErrorMsg, ActivateProductArrangementDataNotAvailableErrorMsg {
        when(administerReferredService.updatedCreditDecisionRetriever.retrieveUpdatedCreditDecision(depositArrangement, requestHeader, "025")).thenReturn(applicationDetails);
        when(administerReferredService.updatedCreditDecisionRetriever.retrieveUpdatedCreditDecision(depositArrangement, requestHeader, "012")).thenReturn(applicationDetails);
        ProductArrangement d_Arrangement = administerReferredService.administerReferredArrangement(depositArrangement, "1", new ExtraConditions(), requestHeader);
        assertNotNull(d_Arrangement);
    }

    @Test
    public void testAdministerReferredArrangementWithAppStatusAsAwaitingReferralProcessing() throws ActivateProductArrangementExternalSystemErrorMsg, ActivateProductArrangementExternalBusinessErrorMsg, ActivateProductArrangementInternalSystemErrorMsg, ActivateProductArrangementResourceNotAvailableErrorMsg, ActivateProductArrangementDataNotAvailableErrorMsg {
        applicationDetails.setApplicationStatus("1008");
        when(administerReferredService.updatedCreditDecisionRetriever.retrieveUpdatedCreditDecision(depositArrangement, requestHeader, "025")).thenReturn(applicationDetails);
        when(administerReferredService.updatedCreditDecisionRetriever.retrieveUpdatedCreditDecision(depositArrangement, requestHeader, "012")).thenReturn(applicationDetails);
        ProductArrangement d_Arrangement = administerReferredService.administerReferredArrangement(depositArrangement, "1", new ExtraConditions(), requestHeader);
        assertNotNull(d_Arrangement);
    }

    @Test
    public void testAdministerReferredArrangementWithStatusReferred() throws ActivateProductArrangementExternalSystemErrorMsg, ActivateProductArrangementExternalBusinessErrorMsg, ActivateProductArrangementInternalSystemErrorMsg, ActivateProductArrangementResourceNotAvailableErrorMsg, ActivateProductArrangementDataNotAvailableErrorMsg {
        depositArrangement.setApplicationStatus("3");
        applicationDetails.setScoreResult("1");
        when(administerReferredService.updatedCreditDecisionRetriever.retrieveUpdatedCreditDecision(depositArrangement, requestHeader, "025")).thenReturn(applicationDetails);
        when(administerReferredService.updatedCreditDecisionRetriever.retrieveUpdatedCreditDecision(depositArrangement, requestHeader, "012")).thenReturn(applicationDetails);
        ProductArrangement d_Arrangement = administerReferredService.administerReferredArrangement(depositArrangement, "1", new ExtraConditions(), requestHeader);
        assertNotNull(d_Arrangement);
    }

    @Test
    public void testAdministerReferredArrangementWithStatusReferredWithSourceSystemIdOffline() throws ActivateProductArrangementExternalSystemErrorMsg, ActivateProductArrangementExternalBusinessErrorMsg, ActivateProductArrangementInternalSystemErrorMsg, ActivateProductArrangementResourceNotAvailableErrorMsg, ActivateProductArrangementDataNotAvailableErrorMsg {
        depositArrangement.setApplicationStatus("3");
        applicationDetails.setScoreResult("1");
        List<ReferralTeams> referralTeamsList = new ArrayList<>();
        ReferralTeams referralTeams = new ReferralTeams();
        referralTeams.setId(1234);
        referralTeams.setTaskType("10048");
        referralTeamsList.add(referralTeams);
        when(administerReferredService.notificationEmailTemplates.getNotificationEmailForReferred(depositArrangement.getArrangementType())).thenReturn("CA_ONLINETOOFFLINE_MSG");
        when(administerReferredService.administerReferredLookUpData.retrieveLookUpValuesByGroupCodeAndLookUpText("002", requestHeader.getChannelId())).thenReturn(testDataHelper.createLookupData());
        when(administerReferredService.referralTeamRetriever.retrieveReferralTeams(testDataHelper.createLookupData(), requestHeader)).thenReturn(referralTeamsList);
        when(administerReferredService.createTask.taskCreation(any(DepositArrangement.class), any(RequestHeader.class), any(ApplicationDetails.class))).thenReturn("110054");
        when(administerReferredService.updatedCreditDecisionRetriever.retrieveUpdatedCreditDecision(depositArrangement, requestHeader, "025")).thenReturn(applicationDetails);
        when(administerReferredService.updatedCreditDecisionRetriever.retrieveUpdatedCreditDecision(depositArrangement, requestHeader, "012")).thenReturn(applicationDetails);
        ProductArrangement d_Arrangement = administerReferredService.administerReferredArrangement(depositArrangement, "4", new ExtraConditions(), requestHeader);
        assertNotNull(d_Arrangement);
        verify(administerReferredService.communicationManager).callSendCommunicationService(depositArrangement, "CA_ONLINETOOFFLINE_MSG", requestHeader, null, "Email");
    }

    @Test
    public void testAdministerReferredArrangementWithStatusReferredWithSourceSystemIdOfflineAndTaskIdNull() throws ActivateProductArrangementExternalSystemErrorMsg, ActivateProductArrangementExternalBusinessErrorMsg, ActivateProductArrangementInternalSystemErrorMsg, ActivateProductArrangementResourceNotAvailableErrorMsg, ActivateProductArrangementDataNotAvailableErrorMsg {
        depositArrangement.setApplicationStatus("3");
        applicationDetails.setScoreResult("1");
        List<ReferralTeams> referralTeamsList = new ArrayList<>();
        ReferralTeams referralTeams = new ReferralTeams();
        referralTeams.setId(1234);
        referralTeams.setTaskType("10048");
        referralTeamsList.add(referralTeams);
        when(administerReferredService.notificationEmailTemplates.getNotificationEmailForReferred(depositArrangement.getArrangementType())).thenReturn("CA_ONLINETOOFFLINE_MSG");
        when(administerReferredService.administerReferredLookUpData.retrieveLookUpValuesByGroupCodeAndLookUpText(depositArrangement.getPrimaryInvolvedParty().getCustomerScore().get(0).getReferralCode().get(0).getCode(), requestHeader.getChannelId())).thenReturn(testDataHelper.createLookupData());
        when(administerReferredService.referralTeamRetriever.retrieveReferralTeams(testDataHelper.createLookupData(), requestHeader)).thenReturn(referralTeamsList);
        when(administerReferredService.createTask.taskCreation(any(DepositArrangement.class), any(RequestHeader.class), any(ApplicationDetails.class))).thenReturn(null);
        when(administerReferredService.updatedCreditDecisionRetriever.retrieveUpdatedCreditDecision(depositArrangement, requestHeader, "025")).thenReturn(applicationDetails);
        when(administerReferredService.updatedCreditDecisionRetriever.retrieveUpdatedCreditDecision(depositArrangement, requestHeader, "012")).thenReturn(applicationDetails);
        ProductArrangement d_Arrangement = administerReferredService.administerReferredArrangement(depositArrangement, "4", new ExtraConditions(), requestHeader);
        assertNotNull(d_Arrangement);
        verify(administerReferredService.communicationManager).callSendCommunicationService(depositArrangement, "CA_ONLINETOOFFLINE_MSG", requestHeader, null, "Email");
    }

    @Test
    public void testAdministerReferredArrangementWithStatusReferredWithSourceSystemIdOfflineAndReferralListNull() throws ActivateProductArrangementExternalSystemErrorMsg, ActivateProductArrangementExternalBusinessErrorMsg, ActivateProductArrangementInternalSystemErrorMsg, ActivateProductArrangementResourceNotAvailableErrorMsg, ActivateProductArrangementDataNotAvailableErrorMsg {
        depositArrangement.setApplicationStatus("3");
        applicationDetails.setScoreResult("1");
        when(administerReferredService.notificationEmailTemplates.getNotificationEmailForReferred(depositArrangement.getArrangementType())).thenReturn("CA_ONLINETOOFFLINE_MSG");
        when(administerReferredService.referralTeamRetriever.retrieveReferralTeams(testDataHelper.createLookupData(), requestHeader)).thenReturn(new ArrayList<ReferralTeams>());
        when(administerReferredService.createTask.taskCreation(any(DepositArrangement.class), any(RequestHeader.class), any(ApplicationDetails.class))).thenReturn(null);
        when(administerReferredService.updatedCreditDecisionRetriever.retrieveUpdatedCreditDecision(depositArrangement, requestHeader, "025")).thenReturn(applicationDetails);
        when(administerReferredService.updatedCreditDecisionRetriever.retrieveUpdatedCreditDecision(depositArrangement, requestHeader, "012")).thenReturn(applicationDetails);
        ProductArrangement d_Arrangement = administerReferredService.administerReferredArrangement(depositArrangement, "4", new ExtraConditions(), requestHeader);
        assertNotNull(d_Arrangement);
        verify(administerReferredService.communicationManager).callSendCommunicationService(depositArrangement, "CA_ONLINETOOFFLINE_MSG", requestHeader, null, "Email");
    }

    @Test
    public void testAdministerReferredArrangementWithAsmFraudDecisionAccept() throws ActivateProductArrangementExternalSystemErrorMsg, ActivateProductArrangementExternalBusinessErrorMsg, ActivateProductArrangementInternalSystemErrorMsg, ActivateProductArrangementResourceNotAvailableErrorMsg, ActivateProductArrangementDataNotAvailableErrorMsg {
        depositArrangement.setApplicationStatus("3");
        applicationDetails.setApplicationStatus("1");
        applicationDetails.setScoreResult("1");
        when(administerReferredService.updatedCreditDecisionRetriever.retrieveUpdatedCreditDecision(depositArrangement, requestHeader, "025")).thenReturn(applicationDetails);
        when(administerReferredService.updatedCreditDecisionRetriever.retrieveUpdatedCreditDecision(depositArrangement, requestHeader, "012")).thenReturn(applicationDetails);
        ProductArrangement d_Arrangement = administerReferredService.administerReferredArrangement(depositArrangement, "1", new ExtraConditions(), requestHeader);
        assertNotNull(d_Arrangement);
    }

    @Test
    public void testAdministerReferredArrangementWithStatusApprovedWithCurrentAccount() throws ActivateProductArrangementExternalSystemErrorMsg, ActivateProductArrangementExternalBusinessErrorMsg, ActivateProductArrangementInternalSystemErrorMsg, ActivateProductArrangementResourceNotAvailableErrorMsg, ActivateProductArrangementDataNotAvailableErrorMsg {
        depositArrangement.setApplicationStatus("1");
        applicationDetails.setApplicationStatus("1");
        applicationDetails.setScoreResult("1");
        when(administerReferredService.administerReferredLookUpData.checkIfFamilyIDSameAsCreditDecision(any(Product.class), any(ArrayList.class))).thenReturn(true);
        when(administerReferredService.updatedCreditDecisionRetriever.retrieveUpdatedCreditDecision(depositArrangement, requestHeader, "025")).thenReturn(applicationDetails);
        when(administerReferredService.updatedCreditDecisionRetriever.retrieveUpdatedCreditDecision(depositArrangement, requestHeader, "012")).thenReturn(applicationDetails);
        ProductArrangement d_Arrangement = administerReferredService.administerReferredArrangement(depositArrangement, "1", new ExtraConditions(), requestHeader);
        assertNotNull(d_Arrangement);
        verify(administerReferredService.communicationManager, times(0)).callSendCommunicationService(depositArrangement, "CA_ONLINETOOFFLINE_MSG", requestHeader, null, "Email");
    }

    @Test
    public void testAdministerReferredArrangementWithStatusApprovedWithCreditCard() throws ActivateProductArrangementExternalSystemErrorMsg, ActivateProductArrangementExternalBusinessErrorMsg, ActivateProductArrangementInternalSystemErrorMsg, ActivateProductArrangementResourceNotAvailableErrorMsg, ActivateProductArrangementDataNotAvailableErrorMsg {
        depositArrangement.setApplicationStatus("1");
        depositArrangement.setArrangementType("CC");
        applicationDetails.setApplicationStatus("1");
        applicationDetails.setScoreResult("1");
        when(administerReferredService.administerReferredLookUpData.checkIfFamilyIDSameAsCreditDecision(any(Product.class), any(ArrayList.class))).thenReturn(true);
        when(administerReferredService.updatedCreditDecisionRetriever.retrieveUpdatedCreditDecision(depositArrangement, requestHeader, "025")).thenReturn(applicationDetails);
        when(administerReferredService.updatedCreditDecisionRetriever.retrieveUpdatedCreditDecision(depositArrangement, requestHeader, "012")).thenReturn(applicationDetails);
        ProductArrangement d_Arrangement = administerReferredService.administerReferredArrangement(depositArrangement, "1", new ExtraConditions(), requestHeader);
        assertNotNull(d_Arrangement);
        verify(administerReferredService.updatedCreditDecisionRetriever).retrieveUpdatedCreditDecision(depositArrangement, requestHeader, "025");
    }

}

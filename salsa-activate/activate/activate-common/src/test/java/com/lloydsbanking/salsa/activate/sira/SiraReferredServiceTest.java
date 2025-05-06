package com.lloydsbanking.salsa.activate.sira;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.activate.TestDataHelper;
import com.lloydsbanking.salsa.activate.administer.downstream.CreateTask;
import com.lloydsbanking.salsa.activate.communication.convert.NotificationEmailTemplates;
import com.lloydsbanking.salsa.activate.communication.downstream.CommunicationManager;
import com.lloydsbanking.salsa.activate.helper.ApplicationDetails;
import com.lloydsbanking.salsa.activate.utility.ExceptionUtilityActivate;
import com.lloydsbanking.salsa.downstream.pam.jdbc.ReferralTeamsDao;
import com.lloydsbanking.salsa.downstream.pam.model.ReferralTeams;
import lib_sim_bo.businessobjects.CustomerScore;
import lib_sim_bo.businessobjects.DepositArrangement;
import lib_sim_bo.businessobjects.ExtraConditions;
import lib_sim_bo.businessobjects.ProductArrangement;
import lib_sim_gmo.messages.RequestHeader;
import lib_sim_salesprocessmanagement.ia_activateproductarrangement.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@Category(UnitTest.class)
public class SiraReferredServiceTest {
    private SiraReferredService siraReferredService;
    private TestDataHelper testDataHelper;
    private RequestHeader requestHeader;
    private DepositArrangement depositArrangement;
    private ApplicationDetails applicationDetails;

    @Before
    public void setUp() {
        siraReferredService = new SiraReferredService();
        testDataHelper = new TestDataHelper();
        requestHeader = testDataHelper.createApaRequestHeader();
        siraReferredService.createTask = mock(CreateTask.class);
        siraReferredService.communicationManager = mock(CommunicationManager.class);
        siraReferredService.referralTeamsDao = mock(ReferralTeamsDao.class);
        siraReferredService.notificationEmailTemplates = mock(NotificationEmailTemplates.class);
        siraReferredService.exceptionUtilityActivate=mock(ExceptionUtilityActivate.class);
        depositArrangement = testDataHelper.createDepositArrangement("95412");
        depositArrangement.getPrimaryInvolvedParty().getCustomerScore().add(new CustomerScore());
        depositArrangement.setApplicationStatus("1");
        applicationDetails = testDataHelper.createApplicationDetails();
        applicationDetails.setApplicationStatus("2");
        applicationDetails.setScoreResult("2");


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
        when(siraReferredService.referralTeamsDao.findByNameIgnoreCaseOrderByPriorityAsc(any(String.class))).thenReturn(referralTeamsList);
        when(siraReferredService.notificationEmailTemplates.getNotificationEmailForReferred(depositArrangement.getArrangementType())).thenReturn("CA_ONLINETOOFFLINE_MSG");
        when(siraReferredService.createTask.taskCreation(any(DepositArrangement.class), any(RequestHeader.class), any(ApplicationDetails.class))).thenReturn("110054");
        ProductArrangement d_Arrangement = siraReferredService.siraReferredArrangement(depositArrangement, "4", new ExtraConditions(), requestHeader);
        assertNotNull(d_Arrangement);
        verify(siraReferredService.communicationManager).callSendCommunicationService(depositArrangement, "CA_ONLINETOOFFLINE_MSG", requestHeader, null, "Email");
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
        when(siraReferredService.referralTeamsDao.findByNameIgnoreCaseOrderByPriorityAsc(any(String.class))).thenReturn(referralTeamsList);
        when(siraReferredService.notificationEmailTemplates.getNotificationEmailForReferred(depositArrangement.getArrangementType())).thenReturn("CA_ONLINETOOFFLINE_MSG");
        when(siraReferredService.createTask.taskCreation(any(DepositArrangement.class), any(RequestHeader.class), any(ApplicationDetails.class))).thenReturn(null);
        ProductArrangement d_Arrangement = siraReferredService.siraReferredArrangement(depositArrangement, "4", new ExtraConditions(), requestHeader);
        assertNotNull(d_Arrangement);
        verify(siraReferredService.communicationManager).callSendCommunicationService(depositArrangement, "CA_ONLINETOOFFLINE_MSG", requestHeader, null, "Email");
    }

    @Test(expected =ActivateProductArrangementDataNotAvailableErrorMsg.class )
    public void testAdministerReferredArrangementWithStatusReferredWithSourceSystemIdOfflineAndReferralListNull() throws ActivateProductArrangementExternalSystemErrorMsg, ActivateProductArrangementExternalBusinessErrorMsg, ActivateProductArrangementInternalSystemErrorMsg, ActivateProductArrangementResourceNotAvailableErrorMsg, ActivateProductArrangementDataNotAvailableErrorMsg {
        depositArrangement.setApplicationStatus("3");
        applicationDetails.setScoreResult("1");
        when(siraReferredService.referralTeamsDao.findByNameIgnoreCaseOrderByPriorityAsc(any(String.class))).thenReturn(new ArrayList<ReferralTeams>());
        when(siraReferredService.exceptionUtilityActivate.dataNotAvailableError(null,null,"No referral team record found",requestHeader)).thenThrow(ActivateProductArrangementDataNotAvailableErrorMsg.class);
        when(siraReferredService.notificationEmailTemplates.getNotificationEmailForReferred(depositArrangement.getArrangementType())).thenReturn("CA_ONLINETOOFFLINE_MSG");
        when(siraReferredService.createTask.taskCreation(any(DepositArrangement.class), any(RequestHeader.class), any(ApplicationDetails.class))).thenReturn(null);
        ProductArrangement d_Arrangement = siraReferredService.siraReferredArrangement(depositArrangement, "4", new ExtraConditions(), requestHeader);
        assertNotNull(d_Arrangement);
        verify(siraReferredService.communicationManager).callSendCommunicationService(depositArrangement, "CA_ONLINETOOFFLINE_MSG", requestHeader, null, "Email");
    }

    @Test
    public void testAdministerReferredArrangementWithStatusReferredWithSourceSystemIdOnline() throws ActivateProductArrangementExternalSystemErrorMsg, ActivateProductArrangementExternalBusinessErrorMsg, ActivateProductArrangementInternalSystemErrorMsg, ActivateProductArrangementResourceNotAvailableErrorMsg, ActivateProductArrangementDataNotAvailableErrorMsg {
        depositArrangement.setApplicationStatus("3");
        applicationDetails.setScoreResult("1");
        List<ReferralTeams> referralTeamsList = new ArrayList<>();
        ReferralTeams referralTeams = new ReferralTeams();
        referralTeams.setId(1234);
        referralTeams.setTaskType("10048");
        referralTeamsList.add(referralTeams);
        when(siraReferredService.referralTeamsDao.findByNameIgnoreCaseOrderByPriorityAsc(any(String.class))).thenReturn(referralTeamsList);
        when(siraReferredService.notificationEmailTemplates.getNotificationEmailForReferred(depositArrangement.getArrangementType())).thenReturn("CA_ONLINETOOFFLINE_MSG");
        when(siraReferredService.createTask.taskCreation(any(DepositArrangement.class), any(RequestHeader.class), any(ApplicationDetails.class))).thenReturn(null);
        ProductArrangement d_Arrangement = siraReferredService.siraReferredArrangement(depositArrangement, "3", new ExtraConditions(), requestHeader);
        assertNotNull(d_Arrangement);
        verify(siraReferredService.communicationManager, times(0)).callSendCommunicationService(depositArrangement, "CA_ONLINETOOFFLINE_MSG", requestHeader, null, "Email");
    }


}

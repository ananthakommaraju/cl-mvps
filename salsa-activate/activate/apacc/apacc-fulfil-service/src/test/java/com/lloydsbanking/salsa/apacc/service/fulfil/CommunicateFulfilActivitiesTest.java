package com.lloydsbanking.salsa.apacc.service.fulfil;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.activate.communication.convert.NotificationEmailTemplates;
import com.lloydsbanking.salsa.activate.communication.downstream.CommunicationManager;
import com.lloydsbanking.salsa.apacc.TestDataHelper;
import lib_sim_bo.businessobjects.Customer;
import lib_sim_bo.businessobjects.FinanceServiceArrangement;
import lib_sim_bo.businessobjects.InternetBankingProfile;
import lib_sim_bo.businessobjects.InternetBankingRegistration;
import lib_sim_gmo.messages.RequestHeader;
import lib_sim_salesprocessmanagement.ia_activateproductarrangement.ActivateProductArrangementInternalSystemErrorMsg;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import javax.xml.datatype.DatatypeConfigurationException;

import static org.mockito.Mockito.*;

@Category(UnitTest.class)
public class CommunicateFulfilActivitiesTest {

    CommunicateFulfilActivities communicateFulfilActivities;

    TestDataHelper testDataHelper;

    FinanceServiceArrangement financeServiceArrangement;

    RequestHeader requestHeader;

    @Before
    public void setUp() throws DatatypeConfigurationException, ActivateProductArrangementInternalSystemErrorMsg {
        communicateFulfilActivities = new CommunicateFulfilActivities();
        communicateFulfilActivities.communicationManager = mock(CommunicationManager.class);
        communicateFulfilActivities.notificationEmailTemplates = mock(NotificationEmailTemplates.class);
        testDataHelper = new TestDataHelper();
        financeServiceArrangement = testDataHelper.createFinanceServiceArrangement();
        requestHeader = testDataHelper.createApaRequestHeader();
    }

    @Test
    public void sendIBRegistrationSuccessEmailTest() {
        Customer customer = new Customer();
        InternetBankingRegistration internetBankingRegistration = new InternetBankingRegistration();
        InternetBankingProfile profile = new InternetBankingProfile();
        profile.setUserName("ajay");
        internetBankingRegistration.setProfile(profile);
        customer.setIsRegisteredIn(internetBankingRegistration);
        financeServiceArrangement.getJointParties().add(customer);
        communicateFulfilActivities.sendIBRegistrationSuccessEmail(-4, financeServiceArrangement, requestHeader);
        when(communicateFulfilActivities.notificationEmailTemplates.getNotificationEmailForIBRegistration(-4)).thenReturn("TEMPLATE");
        verify(communicateFulfilActivities.notificationEmailTemplates).getNotificationEmailForIBRegistration(-4);
    }

    @Test
    public void sendWelcomeEmailTest() {
        communicateFulfilActivities.sendWelcomeEmail(true, financeServiceArrangement, requestHeader);
        verify(communicateFulfilActivities.communicationManager).callSendCommunicationService(financeServiceArrangement, "WELCOME_MSG", requestHeader, null, "Email");
    }

    @Test
    public void scheduleSTPSuccessSMSTest() {
        communicateFulfilActivities.scheduleSTPSuccessSMS(true, financeServiceArrangement, requestHeader, "2");
        verify(communicateFulfilActivities.communicationManager, never()).callSendCommunicationService(financeServiceArrangement, "STPCCRSUCCESS", requestHeader, "STPCC", "SMS");
    }
}

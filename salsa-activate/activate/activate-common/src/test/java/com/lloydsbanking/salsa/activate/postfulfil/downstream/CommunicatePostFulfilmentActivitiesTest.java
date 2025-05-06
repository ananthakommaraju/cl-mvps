package com.lloydsbanking.salsa.activate.postfulfil.downstream;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.activate.TestDataHelper;
import com.lloydsbanking.salsa.activate.communication.convert.NotificationEmailTemplates;
import com.lloydsbanking.salsa.activate.communication.downstream.CommunicationManager;
import com.lloydsbanking.salsa.activate.postfulfil.rules.ValidateProcessPostFulfilment;
import com.lloydstsb.ib.wsbridge.application.StB751BAppPerCCRegAuth;
import lib_sim_bo.businessobjects.*;
import lib_sim_communicationmanager.messages.ScheduleCommunicationResponse;
import lib_sim_communicationmanager.messages.SendCommunicationResponse;
import lib_sim_gmo.messages.RequestHeader;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static org.mockito.Mockito.*;

@Category(UnitTest.class)
public class CommunicatePostFulfilmentActivitiesTest {

    private CommunicatePostFulfilmentActivities communicatePostFulfilmentActivities;
    private TestDataHelper testDataHelper;
    private RequestHeader requestHeader;

    @Before
    public void setUp() {
        communicatePostFulfilmentActivities = new CommunicatePostFulfilmentActivities();
        testDataHelper = new TestDataHelper();
        requestHeader = testDataHelper.createApaRequestHeader();
        communicatePostFulfilmentActivities.communicationManager = mock(CommunicationManager.class);
        communicatePostFulfilmentActivities.notificationEmailTemplates = mock(NotificationEmailTemplates.class);
        communicatePostFulfilmentActivities.validateProcessPostFulfilment = mock(ValidateProcessPostFulfilment.class);
    }

    @Test
    public void communicateForIBRegistrationActivateWithMandateLiteForSendCommunicationTest() {
        StB751BAppPerCCRegAuth activateIBApplicationResponse = new StB751BAppPerCCRegAuth();
        activateIBApplicationResponse.setTacver(-4);
        ProductArrangement productArrangement = new ProductArrangement();
        when(communicatePostFulfilmentActivities.notificationEmailTemplates.getNotificationEmailForIBRegistration(-4)).thenReturn("IB_STP_REGISTRATION_SUCCESS_MAIL");
        when(communicatePostFulfilmentActivities.communicationManager.callSendCommunicationService(productArrangement, "IB_STP_REGISTRATION_SUCCESS_MAIL", requestHeader, null, "Email")).thenReturn(new SendCommunicationResponse());
        communicatePostFulfilmentActivities.communicateForIBRegistration(activateIBApplicationResponse, productArrangement, requestHeader);
        verify(communicatePostFulfilmentActivities.communicationManager).callSendCommunicationService(productArrangement, "IB_STP_REGISTRATION_SUCCESS_MAIL", requestHeader, null, "Email");
    }

    @Test
    public void communicateForIBRegistrationActivateWithMandateUltraliteForSendCommunicationTest() {
        StB751BAppPerCCRegAuth activateIBApplicationResponse = new StB751BAppPerCCRegAuth();
        activateIBApplicationResponse.setTacver(-5);
        ProductArrangement productArrangement = new ProductArrangement();
        when(communicatePostFulfilmentActivities.notificationEmailTemplates.getNotificationEmailForIBRegistration(-5)).thenReturn("IB_STP_REGISTRATION_SUCCESS_MAIL");
        when(communicatePostFulfilmentActivities.communicationManager.callSendCommunicationService(productArrangement, "IB_STP_REGISTRATION_SUCCESS_MAIL", requestHeader, null, "Email")).thenReturn(new SendCommunicationResponse());
        communicatePostFulfilmentActivities.communicateForIBRegistration(activateIBApplicationResponse, productArrangement, requestHeader);
        verify(communicatePostFulfilmentActivities.communicationManager).callSendCommunicationService(productArrangement, "IB_STP_REGISTRATION_SUCCESS_MAIL", requestHeader, null, "Email");
    }

    @Test
    public void communicateForIBRegistrationActivateWithNullB751ResponseTest() {
        StB751BAppPerCCRegAuth activateIBApplicationResponse = new StB751BAppPerCCRegAuth();
        ProductArrangement productArrangement = new ProductArrangement();
        when(communicatePostFulfilmentActivities.notificationEmailTemplates.getNotificationEmailForIBRegistration(-5)).thenReturn("IB_STP_REGISTRATION_SUCCESS_MAIL");
        when(communicatePostFulfilmentActivities.communicationManager.callSendCommunicationService(productArrangement, "IB_STP_REGISTRATION_SUCCESS_MAIL", requestHeader, null, "Email")).thenReturn(new SendCommunicationResponse());
        communicatePostFulfilmentActivities.communicateForIBRegistration(activateIBApplicationResponse, productArrangement, requestHeader);
        verify(communicatePostFulfilmentActivities.communicationManager, never()).callSendCommunicationService(productArrangement, "IB_STP_REGISTRATION_SUCCESS_MAIL", requestHeader, null, "Email");
    }

    @Test
    public void communicateWelcomeMessageAndFundReminderWithSavingArrangementTypeAndBenefitMessagesTest() {
        ProductArrangement productArrangement = testDataHelper.createDepositArrangement("1");
        productArrangement.setArrangementType("SA");
        TelephoneNumber telephoneNumber = new TelephoneNumber();
        telephoneNumber.setTelephoneType("7");
        telephoneNumber.setPhoneNumber("9123456789");
        productArrangement.getPrimaryInvolvedParty().getTelephoneNumber().add(telephoneNumber);
        RuleCondition ruleCondition = new RuleCondition();
        ruleCondition.setName("ALERT_MSGES");
        productArrangement.getConditions().add(ruleCondition);
        when(communicatePostFulfilmentActivities.notificationEmailTemplates.getWelcomeMessageNotificationEmailForSavingAccount(productArrangement.getConditions())).thenReturn("SA_WELCOME_MSG");
        when(communicatePostFulfilmentActivities.communicationManager.callSendCommunicationService(productArrangement, "SA_WELCOME_MSG", requestHeader, null, "Email")).thenReturn(new SendCommunicationResponse());
        when(communicatePostFulfilmentActivities.communicationManager.callScheduleCommunicationService(productArrangement, "SA_FUNDING_REM_MSG", requestHeader, "STPSAVINGS", "Email")).thenReturn(new ScheduleCommunicationResponse());
        when(communicatePostFulfilmentActivities.communicationManager.callScheduleCommunicationService(productArrangement, "STPSAVFUNDREMIN", requestHeader, "STPSAVINGS", "SMS")).thenReturn(new ScheduleCommunicationResponse());
        when(communicatePostFulfilmentActivities.communicationManager.callSendCommunicationService(productArrangement, "CA_BENEFITS_MSG", requestHeader, null, "Email")).thenReturn(new SendCommunicationResponse());
        when(communicatePostFulfilmentActivities.communicationManager.callScheduleCommunicationService(productArrangement, "STPSAVSUCCESS", requestHeader, "STPSAVINGS", "SMS")).thenReturn(new ScheduleCommunicationResponse());
        communicatePostFulfilmentActivities.communicateWelcomeMessageAndFundReminder(productArrangement, requestHeader, "LBG", "2");
        verify(communicatePostFulfilmentActivities.communicationManager).callSendCommunicationService(productArrangement, "SA_WELCOME_MSG", requestHeader, null, "Email");
        verify(communicatePostFulfilmentActivities.communicationManager).callScheduleCommunicationService(productArrangement, "SA_FUNDING_REM_MSG", requestHeader, "STPSAVINGS", "Email");
        verify(communicatePostFulfilmentActivities.communicationManager).callScheduleCommunicationService(productArrangement, "STPSAVFUNDREMIN", requestHeader, "STPSAVINGS", "SMS");
        verify(communicatePostFulfilmentActivities.communicationManager).callSendCommunicationService(productArrangement, "CA_BENEFITS_MSG", requestHeader, null, "Email");
        verify(communicatePostFulfilmentActivities.communicationManager).callScheduleCommunicationService(productArrangement, "STPSAVSUCCESS", requestHeader, "STPSAVINGS", "SMS");
    }

    @Test
    public void communicateWelcomeMessageAndFundReminderWithCurrentArrangementTypeAndDurableMediumSwitchOn() {
        ProductArrangement productArrangement = testDataHelper.createDepositArrangement("1");
        productArrangement.setArrangementType("CA");
        TelephoneNumber telephoneNumber = new TelephoneNumber();
        telephoneNumber.setTelephoneType("7");
        telephoneNumber.setPhoneNumber("9123456789");
        productArrangement.getPrimaryInvolvedParty().getTelephoneNumber().add(telephoneNumber);
        ProductOffer productOffer = new ProductOffer();
        Template template = new Template();
        template.setExternalTemplateIdentifier("EXTERNAL_TEMPLATE");
        productOffer.getTemplate().add(template);
        productArrangement.getAssociatedProduct().getProductoffer().add(productOffer);
        when(communicatePostFulfilmentActivities.validateProcessPostFulfilment.retrieveSwitchValue("LBG", "SW_EnSTPPCAWcMl")).thenReturn(true);
        when(communicatePostFulfilmentActivities.communicationManager.callSendCommunicationService(productArrangement, "EXTERNAL_TEMPLATE", requestHeader, null, "AttachmentPDF")).thenReturn(new SendCommunicationResponse());
        communicatePostFulfilmentActivities.communicateWelcomeMessageAndFundReminder(productArrangement, requestHeader, "LBG", "1");
        verify(communicatePostFulfilmentActivities.communicationManager).callSendCommunicationService(productArrangement, "EXTERNAL_TEMPLATE", requestHeader, null, "AttachmentPDF");
    }

    @Test
    public void communicateWelcomeMessageAndFundReminderWithCurrentArrangementTypeAndDurableMediumSwitchOff() {
        ProductArrangement productArrangement = testDataHelper.createDepositArrangement("1");
        productArrangement.setArrangementType("CA");
        TelephoneNumber telephoneNumber = new TelephoneNumber();
        telephoneNumber.setTelephoneType("7");
        telephoneNumber.setPhoneNumber("9123456789");
        productArrangement.getPrimaryInvolvedParty().getTelephoneNumber().add(telephoneNumber);
        when(communicatePostFulfilmentActivities.validateProcessPostFulfilment.retrieveSwitchValue("LBG", "SW_EnSTPPCAWcMl")).thenReturn(false);
        when(communicatePostFulfilmentActivities.communicationManager.callSendCommunicationService(productArrangement, "CA_WELCOME_MSG", requestHeader, null, "Email")).thenReturn(new SendCommunicationResponse());
        communicatePostFulfilmentActivities.communicateWelcomeMessageAndFundReminder(productArrangement, requestHeader, "LBG", "1");
        verify(communicatePostFulfilmentActivities.communicationManager).callSendCommunicationService(productArrangement, "CA_WELCOME_MSG", requestHeader, null, "Email");
    }


}

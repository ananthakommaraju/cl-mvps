package com.lloydsbanking.salsa.activate.communication.convert;


import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.activate.TestDataHelper;
import lib_sim_bo.businessobjects.*;
import lib_sim_communicationmanager.messages.ScheduleCommunicationRequest;
import lib_sim_communicationmanager.messages.SendCommunicationRequest;
import lib_sim_gmo.messages.RequestHeader;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Category(UnitTest.class)
public class CommunicationRequestFactoryTest {

    private TestDataHelper testDataHelper;
    private RequestHeader requestHeader;
    private CommunicationRequestFactory communicationRequestFactory;
    private List<InformationContent> informationContents;

    @Before
    public void setUp() {
        informationContents = new ArrayList<>();
        informationContents.add(new InformationContent());
        informationContents.get(0).setKey("key");
        informationContents.get(0).setValue("value");
        informationContents.get(0).setOrder(1);

        communicationRequestFactory = new CommunicationRequestFactory();
        testDataHelper = new TestDataHelper();
        requestHeader = testDataHelper.createApaRequestHeader();
        communicationRequestFactory.productRelatedTokenFactory = mock(ProductRelatedTokenFactory.class);
        communicationRequestFactory.postCodeFactory = mock(PostCodeFactory.class);
        communicationRequestFactory.informationContentFactory = mock(InformationContentFactory.class);
    }

    @Test
    public void convertTestWithCurrentAccount() {
        DepositArrangement depositArrangement = testDataHelper.createDepositArrangement("12542");
        depositArrangement.getPrimaryInvolvedParty().setIsPlayedBy(new Individual());
        depositArrangement.getPrimaryInvolvedParty().getIsPlayedBy().getIndividualName().add(new IndividualName());
        SendCommunicationRequest sendCommunicationRequest = communicationRequestFactory.convertToSendCommunicationRequest(depositArrangement, null, requestHeader, null, "Email");
        assertNotNull(sendCommunicationRequest);
        assertEquals("IBL", sendCommunicationRequest.getCommunication().getBrand());
        assertEquals("Email", sendCommunicationRequest.getCommunication().getCommunicationType());
    }

    @Test
    public void convertTestWithSavingAccount() {
        DepositArrangement depositArrangement = testDataHelper.createDepositArrangement("12542");
        depositArrangement.setArrangementType("SA");
        depositArrangement.getPrimaryInvolvedParty().setIsPlayedBy(new Individual());
        depositArrangement.getPrimaryInvolvedParty().getIsPlayedBy().getIndividualName().add(new IndividualName());
        SendCommunicationRequest sendCommunicationRequest = communicationRequestFactory.convertToSendCommunicationRequest(depositArrangement, null, requestHeader, null, "Email");
        assertNotNull(sendCommunicationRequest);
        assertEquals("IBL", sendCommunicationRequest.getCommunication().getBrand());
        assertEquals("Email", sendCommunicationRequest.getCommunication().getCommunicationType());
    }

    @Test
    public void convertTestWithFinanceAccount() {
        DepositArrangement depositArrangement = testDataHelper.createDepositArrangement("12542");
        depositArrangement.setArrangementType("FA");
        depositArrangement.getPrimaryInvolvedParty().setIsPlayedBy(new Individual());
        depositArrangement.getPrimaryInvolvedParty().getIsPlayedBy().getIndividualName().add(new IndividualName());
        SendCommunicationRequest sendCommunicationRequest = communicationRequestFactory.convertToSendCommunicationRequest(depositArrangement, null, requestHeader, null, "EMAIL");
        assertNotNull(sendCommunicationRequest);
        assertEquals("IBL", sendCommunicationRequest.getCommunication().getBrand());
        assertEquals("EMAIL", sendCommunicationRequest.getCommunication().getCommunicationType());
    }

    @Test
    public void testConvertWhenEmailTemplatePresent() {
        DepositArrangement depositArrangement = testDataHelper.createDepositArrangement("12542");
        depositArrangement.setArrangementType("SA");
        depositArrangement.getPrimaryInvolvedParty().setIsPlayedBy(new Individual());
        depositArrangement.getPrimaryInvolvedParty().getIsPlayedBy().getIndividualName().add(new IndividualName());

        when(communicationRequestFactory.productRelatedTokenFactory.getProductRelatedToken(any(DepositArrangement.class))).thenReturn(informationContents);

        SendCommunicationRequest sendCommunicationRequest = communicationRequestFactory.convertToSendCommunicationRequest(depositArrangement, "SA_FUNDING_REM_MSG", requestHeader, null, "EMAIL");
        assertNotNull(sendCommunicationRequest);
        assertEquals("IBL", sendCommunicationRequest.getCommunication().getBrand());
        assertEquals("EMAIL", sendCommunicationRequest.getCommunication().getCommunicationType());

        assertEquals("value", sendCommunicationRequest.getCommunication().getHasCommunicationContent().get(4).getValue());
    }


    @Test
    public void testConvertWhenEmailTemplateIsStpRegistrationSuccess() {
        DepositArrangement depositArrangement = testDataHelper.createDepositArrangement("12542");
        depositArrangement.setAccountNumber("XXXX123458");
        depositArrangement.setArrangementType("SA");
        depositArrangement.getPrimaryInvolvedParty().setIsPlayedBy(new Individual());
        depositArrangement.getPrimaryInvolvedParty().getIsPlayedBy().getIndividualName().add(new IndividualName());
        depositArrangement.getPrimaryInvolvedParty().setIsRegisteredIn(new InternetBankingRegistration());
        depositArrangement.getPrimaryInvolvedParty().getIsRegisteredIn().setProfile(new InternetBankingProfile());

        when(communicationRequestFactory.informationContentFactory.getInformationContent(any(String.class), any(String.class), any(Integer.class))).thenReturn(informationContents.get(0));

        SendCommunicationRequest sendCommunicationRequest = communicationRequestFactory.convertToSendCommunicationRequest(depositArrangement, "IB_STP_REGISTRATION_SUCCESS_MAIL", requestHeader, null, "Email");
        assertNotNull(sendCommunicationRequest);
        assertEquals("IBL", sendCommunicationRequest.getCommunication().getBrand());
        assertEquals("Email", sendCommunicationRequest.getCommunication().getCommunicationType());

        assertEquals("value", sendCommunicationRequest.getCommunication().getHasCommunicationContent().get(0).getValue());
    }

    @Test
    public void testConvertWhenIbRegistrationArrangementTypeIsCC() {
        DepositArrangement depositArrangement = testDataHelper.createDepositArrangement("12542");
        depositArrangement.setAccountNumber("XXXX123458");
        depositArrangement.setArrangementType("CC");
        depositArrangement.getJointParties().add(new Customer());
        depositArrangement.getPrimaryInvolvedParty().setIsPlayedBy(new Individual());
        depositArrangement.getPrimaryInvolvedParty().getIsPlayedBy().getIndividualName().add(new IndividualName());
        depositArrangement.getPrimaryInvolvedParty().setIsRegisteredIn(new InternetBankingRegistration());
        depositArrangement.getPrimaryInvolvedParty().getIsRegisteredIn().setProfile(new InternetBankingProfile());

        when(communicationRequestFactory.informationContentFactory.getInformationContent(any(String.class), any(String.class), any(Integer.class))).thenReturn(informationContents.get(0));

        SendCommunicationRequest sendCommunicationRequest = communicationRequestFactory.convertToSendCommunicationRequest(depositArrangement, "IB_STP_REGISTRATION_SUCCESS_MAIL", requestHeader, null, "Email");
        assertNotNull(sendCommunicationRequest);
        assertEquals("IBL", sendCommunicationRequest.getCommunication().getBrand());
        assertEquals("Email", sendCommunicationRequest.getCommunication().getCommunicationType());

        assertEquals("value", sendCommunicationRequest.getCommunication().getHasCommunicationContent().get(0).getValue());
    }

    @Test
    public void testConvertToScheduleCommunicationRequest() {
        DepositArrangement depositArrangement = testDataHelper.createDepositArrangement("12542");
        depositArrangement.setGuardianDetails(new Customer());
        depositArrangement.getGuardianDetails().getTelephoneNumber().add(new TelephoneNumber());
        depositArrangement.getGuardianDetails().getTelephoneNumber().get(0).setTelephoneType("7");
        depositArrangement.getGuardianDetails().getTelephoneNumber().get(0).setPhoneNumber("9867342156");
        ScheduleCommunicationRequest scheduleCommunicationRequest = communicationRequestFactory.convertToScheduleCommunicationRequest(depositArrangement, null, requestHeader, null, "SMS");
        assertNotNull(scheduleCommunicationRequest);
        assertEquals("IBL", scheduleCommunicationRequest.getCommunication().getBrand());
        assertEquals("SMS", scheduleCommunicationRequest.getCommunication().getCommunicationType());
    }

}

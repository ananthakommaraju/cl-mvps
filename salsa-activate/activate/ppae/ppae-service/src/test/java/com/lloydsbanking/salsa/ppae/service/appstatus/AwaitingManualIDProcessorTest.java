package com.lloydsbanking.salsa.ppae.service.appstatus;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.activate.communication.convert.NotificationEmailTemplates;
import com.lloydsbanking.salsa.activate.communication.downstream.CommunicationManager;
import com.lloydsbanking.salsa.activate.constants.ActivateCommonConstant;
import com.lloydsbanking.salsa.activate.constants.EmailTemplateEnum;
import com.lloydsbanking.salsa.activate.downstream.LookUpValueRetriever;
import com.lloydsbanking.salsa.constant.ArrangementType;
import com.lloydsbanking.salsa.date.DateFactory;
import com.lloydsbanking.salsa.downstream.pam.model.ReferenceDataLookUp;
import com.lloydsbanking.salsa.ppae.TestDataHelper;
import lib_sim_bo.businessobjects.ProductArrangement;
import lib_sim_bo.businessobjects.TelephoneNumber;
import lib_sim_gmo.messages.RequestHeader;
import lib_sim_salesprocessmanagement.messages.ProcessPendingArrangementEventRequest;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import javax.xml.datatype.DatatypeConfigurationException;
import java.util.ArrayList;
import java.util.Date;

import static org.mockito.Mockito.*;

@Category(UnitTest.class)
public class AwaitingManualIDProcessorTest {

    AwaitingManualIDProcessor awaitingManualIDProcessor;
    ProductArrangement productArrangement;
    TestDataHelper testDataHelper;
    ProcessPendingArrangementEventRequest request;

    @Before
    public void setUp() throws DatatypeConfigurationException {

        awaitingManualIDProcessor = new AwaitingManualIDProcessor();
        testDataHelper = new TestDataHelper();
        productArrangement = testDataHelper.createProductArrangement();
        request = testDataHelper.createPpaeRequest("1", "LTB");
        awaitingManualIDProcessor.lookUpValueRetriever = mock(LookUpValueRetriever.class);
        awaitingManualIDProcessor.communicationManager = mock(CommunicationManager.class);
        awaitingManualIDProcessor.notificationEmailTemplates = mock(NotificationEmailTemplates.class);
        awaitingManualIDProcessor.dateFactory = new DateFactory();
        Date currentDate = new Date();
        DateFactory dateFactory = new DateFactory();
        Date modifiedDate = dateFactory.addDays(currentDate, -2);
        productArrangement.setLastModifiedDate(new DateFactory().dateToXMLGregorianCalendar(modifiedDate));

    }

    @Test
    public void testLookUpValuesProcessForDifferenceGreaterThanFiveForCC() {
        ArrayList<String> groupCodeList = new ArrayList<>();
        groupCodeList.add(0, "CUSTOMER_NO_SHOW_UPD");
        ArrayList<ReferenceDataLookUp> referenceDataLookUps = new ArrayList<>();
        referenceDataLookUps.add(new ReferenceDataLookUp());
        referenceDataLookUps.get(0).setLookupText("Cust.NoShow_Upddays");
        referenceDataLookUps.get(0).setLookupValueDesc("35");
        when(awaitingManualIDProcessor.lookUpValueRetriever.getLookUpValues(groupCodeList, productArrangement.getAssociatedProduct().getBrandName())).thenReturn(referenceDataLookUps);
        awaitingManualIDProcessor.processCommunications(productArrangement, request);
        verify(awaitingManualIDProcessor.lookUpValueRetriever).getLookUpValues(groupCodeList, productArrangement.getAssociatedProduct().getBrandName());
    }

    @Test
    public void testLookUpValuesProcessForDifferenceGreaterThanFiveForCA() {
        productArrangement.setArrangementType("CA");

        ArrayList<String> groupCodeList = new ArrayList<>();
        groupCodeList.add(0, "CUSTOMER_NO_SHOW_UPD");
        ArrayList<ReferenceDataLookUp> referenceDataLookUps = new ArrayList<>();
        referenceDataLookUps.add(new ReferenceDataLookUp());
        referenceDataLookUps.get(0).setLookupText("Cust.NoShow_Upddays");
        referenceDataLookUps.get(0).setLookupValueDesc("35");
        when(awaitingManualIDProcessor.lookUpValueRetriever.getLookUpValues(groupCodeList, productArrangement.getAssociatedProduct().getBrandName())).thenReturn(referenceDataLookUps);
        awaitingManualIDProcessor.processCommunications(productArrangement, request);
        verify(awaitingManualIDProcessor.lookUpValueRetriever).getLookUpValues(groupCodeList, productArrangement.getAssociatedProduct().getBrandName());
    }

    @Test
    public void testLookUpValuesProcessForDifferenceGreaterThanFiveForCAForInvalidPhoneType() {
        productArrangement.getPrimaryInvolvedParty().getTelephoneNumber().clear();
        productArrangement.setArrangementType("CA");
        productArrangement.getPrimaryInvolvedParty().getTelephoneNumber().add(new TelephoneNumber());
        productArrangement.getPrimaryInvolvedParty().getTelephoneNumber().get(0).setPhoneNumber("98262365");
        productArrangement.getPrimaryInvolvedParty().getTelephoneNumber().get(0).setTelephoneType("8");

        ArrayList<String> groupCodeList = new ArrayList<>();
        groupCodeList.add(0, "CUSTOMER_NO_SHOW_UPD");
        ArrayList<ReferenceDataLookUp> referenceDataLookUps = new ArrayList<>();
        referenceDataLookUps.add(new ReferenceDataLookUp());
        referenceDataLookUps.get(0).setLookupText("Cust.NoShow_Upddays");
        referenceDataLookUps.get(0).setLookupValueDesc("35");
        when(awaitingManualIDProcessor.lookUpValueRetriever.getLookUpValues(groupCodeList, productArrangement.getAssociatedProduct().getBrandName())).thenReturn(referenceDataLookUps);
        awaitingManualIDProcessor.processCommunications(productArrangement, request);
        verify(awaitingManualIDProcessor.lookUpValueRetriever).getLookUpValues(groupCodeList, productArrangement.getAssociatedProduct().getBrandName());
    }

    @Test
    public void testLookUpValuesProcessForDifferenceGreaterThanFiveForCAForValidPhoneType() {
        productArrangement.getPrimaryInvolvedParty().getTelephoneNumber().clear();
        productArrangement.setArrangementType("CA");
        productArrangement.getPrimaryInvolvedParty().getTelephoneNumber().add(new TelephoneNumber());
        productArrangement.getPrimaryInvolvedParty().getTelephoneNumber().get(0).setPhoneNumber("98262365");
        productArrangement.getPrimaryInvolvedParty().getTelephoneNumber().get(0).setTelephoneType("7");
        ArrayList<String> groupCodeList = new ArrayList<>();
        groupCodeList.add(0, "CUSTOMER_NO_SHOW_UPD");
        ArrayList<ReferenceDataLookUp> referenceDataLookUps = new ArrayList<>();
        referenceDataLookUps.add(new ReferenceDataLookUp());
        referenceDataLookUps.get(0).setLookupText("Cust.NoShow_Upddays");
        referenceDataLookUps.get(0).setLookupValueDesc("35");
        when(awaitingManualIDProcessor.lookUpValueRetriever.getLookUpValues(groupCodeList, productArrangement.getAssociatedProduct().getBrandName())).thenReturn(referenceDataLookUps);
        awaitingManualIDProcessor.processCommunications(productArrangement, request);
        verify(awaitingManualIDProcessor.lookUpValueRetriever).getLookUpValues(groupCodeList, productArrangement.getAssociatedProduct().getBrandName());
    }

    @Test
    public void testLookUpValuesProcessForDifferenceGreaterThanFiveForValidPhoneNumber() {
        productArrangement.getPrimaryInvolvedParty().getTelephoneNumber().clear();
        productArrangement.setArrangementType("CA");
        productArrangement.getPrimaryInvolvedParty().getTelephoneNumber().add(new TelephoneNumber());
        productArrangement.getPrimaryInvolvedParty().getTelephoneNumber().get(0).setPhoneNumber("0826236559");
        productArrangement.getPrimaryInvolvedParty().getTelephoneNumber().get(0).setTelephoneType("6");
        ArrayList<String> groupCodeList = new ArrayList<>();
        groupCodeList.add(0, "CUSTOMER_NO_SHOW_UPD");
        ArrayList<ReferenceDataLookUp> referenceDataLookUps = new ArrayList<>();
        referenceDataLookUps.add(new ReferenceDataLookUp());
        referenceDataLookUps.get(0).setLookupText("Cust.NoShow_Upddays");
        referenceDataLookUps.get(0).setLookupValueDesc("35");
        when(awaitingManualIDProcessor.lookUpValueRetriever.getLookUpValues(groupCodeList, productArrangement.getAssociatedProduct().getBrandName())).thenReturn(referenceDataLookUps);
        awaitingManualIDProcessor.processCommunications(productArrangement, request);
        verify(awaitingManualIDProcessor.lookUpValueRetriever).getLookUpValues(groupCodeList, productArrangement.getAssociatedProduct().getBrandName());
    }

    @Test
    public void testLookUpValuesProcessForDifferenceGreaterThanFiveForNullTelephoneNo() {
        productArrangement.getPrimaryInvolvedParty().getTelephoneNumber().clear();
        productArrangement.setArrangementType("CA");
        ArrayList<String> groupCodeList = new ArrayList<>();
        groupCodeList.add(0, "CUSTOMER_NO_SHOW_UPD");
        ArrayList<ReferenceDataLookUp> referenceDataLookUps = new ArrayList<>();
        referenceDataLookUps.add(new ReferenceDataLookUp());
        referenceDataLookUps.get(0).setLookupText("Cust.NoShow_Upddays");
        referenceDataLookUps.get(0).setLookupValueDesc("35");
        when(awaitingManualIDProcessor.lookUpValueRetriever.getLookUpValues(groupCodeList, productArrangement.getAssociatedProduct().getBrandName())).thenReturn(referenceDataLookUps);
        awaitingManualIDProcessor.processCommunications(productArrangement, request);
        verify(awaitingManualIDProcessor.lookUpValueRetriever).getLookUpValues(groupCodeList, productArrangement.getAssociatedProduct().getBrandName());
    }


    @Test
    public void testLookUpValuesProcessForDifferenceGreaterThanFiveForSAForInvalidMobNo() {
        productArrangement.setArrangementType("SA");
        productArrangement.setPrimaryInvolvedParty(null);
        ArrayList<String> groupCodeList = new ArrayList<>();
        groupCodeList.add(0, "CUSTOMER_NO_SHOW_UPD");
        ArrayList<ReferenceDataLookUp> referenceDataLookUps = new ArrayList<>();
        referenceDataLookUps.add(new ReferenceDataLookUp());
        referenceDataLookUps.get(0).setLookupText("Cust.NoShow_Upddays");
        referenceDataLookUps.get(0).setLookupValueDesc("35");
        when(awaitingManualIDProcessor.lookUpValueRetriever.getLookUpValues(groupCodeList, productArrangement.getAssociatedProduct().getBrandName())).thenReturn(referenceDataLookUps);
        awaitingManualIDProcessor.processCommunications(productArrangement, request);
        verify(awaitingManualIDProcessor.lookUpValueRetriever).getLookUpValues(groupCodeList, productArrangement.getAssociatedProduct().getBrandName());
    }

    @Test
    public void testLookUpValuesProcessForDifferenceLessThanFive() {
        productArrangement.setLastModifiedDate(null);
        ArrayList<String> groupCodeList = new ArrayList<>();
        groupCodeList.add(0, "CUSTOMER_NO_SHOW_UPD");
        ArrayList<ReferenceDataLookUp> referenceDataLookUps = new ArrayList<>();
        referenceDataLookUps.add(new ReferenceDataLookUp());
        referenceDataLookUps.get(0).setLookupText("Cust.NoShow_Upddays");
        referenceDataLookUps.get(0).setLookupValueDesc("3");
        when(awaitingManualIDProcessor.lookUpValueRetriever.getLookUpValues(groupCodeList, productArrangement.getAssociatedProduct().getBrandName())).thenReturn(referenceDataLookUps);
        when(awaitingManualIDProcessor.notificationEmailTemplates.getNotificationEmailForDifferenceLessThanFive(productArrangement.getArrangementType())).thenReturn(EmailTemplateEnum.CA_EIDNV_REMINDER_MSG.getTemplate());
        awaitingManualIDProcessor.processCommunications(productArrangement, request);
        verify(awaitingManualIDProcessor.lookUpValueRetriever).getLookUpValues(groupCodeList, productArrangement.getAssociatedProduct().getBrandName());
        verify(awaitingManualIDProcessor.notificationEmailTemplates).getNotificationEmailForDifferenceLessThanFive(productArrangement.getArrangementType());
        verify(awaitingManualIDProcessor.communicationManager).callSendCommunicationService(productArrangement, EmailTemplateEnum.CA_EIDNV_REMINDER_MSG.getTemplate(), request.getHeader(), null, ActivateCommonConstant.CommunicationTypes.EMAIL);
    }

    @Test
    public void testLookUpValuesProcessForSendingSMS() throws DatatypeConfigurationException {
        productArrangement.setArrangementType(ArrangementType.SAVINGS.getValue());
        ArrayList<String> groupCodeList = new ArrayList<>();
        groupCodeList.add(0, "CUSTOMER_NO_SHOW_UPD");
        ArrayList<ReferenceDataLookUp> referenceDataLookUps = new ArrayList<>();
        referenceDataLookUps.add(new ReferenceDataLookUp());
        referenceDataLookUps.get(0).setLookupText("Cust.NoShow_Upddays");
        referenceDataLookUps.get(0).setLookupValueDesc("66");
        when(awaitingManualIDProcessor.lookUpValueRetriever.getLookUpValues(groupCodeList, productArrangement.getAssociatedProduct().getBrandName())).thenReturn(referenceDataLookUps);
        awaitingManualIDProcessor.processCommunications(productArrangement, request);
        verify(awaitingManualIDProcessor.communicationManager).callScheduleCommunicationServiceForPpae(any(ProductArrangement.class),any(String.class),any(RequestHeader.class),any(String.class),any(String.class),any(Integer.class));
        verify(awaitingManualIDProcessor.lookUpValueRetriever).getLookUpValues(groupCodeList, productArrangement.getAssociatedProduct().getBrandName());
    }

}

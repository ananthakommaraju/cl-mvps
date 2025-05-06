package com.lloydsbanking.salsa.ppae.service.appstatus;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.activate.communication.convert.NotificationEmailTemplates;
import com.lloydsbanking.salsa.activate.communication.downstream.CommunicationManager;
import com.lloydsbanking.salsa.activate.constants.ActivateCommonConstant;
import com.lloydsbanking.salsa.activate.downstream.LookUpValueRetriever;
import com.lloydsbanking.salsa.date.DateFactory;
import com.lloydsbanking.salsa.downstream.pam.model.ReferenceDataLookUp;
import com.lloydsbanking.salsa.header.gmo.HeaderRetriever;
import com.lloydsbanking.salsa.ppae.TestDataHelper;
import com.lloydsbanking.salsa.ppae.service.downstream.EstablishFinanceServiceArrangementRetriever;
import com.lloydsbanking.salsa.ppae.service.downstream.F263LoanDetailsRetriever;
import com.lloydsbanking.salsa.soap.pad.f263.objects.F263Req;
import com.lloydsbanking.salsa.soap.pad.f263.objects.F263Resp;
import lib_sim_bo.businessobjects.Customer;
import lib_sim_bo.businessobjects.FinanceServiceArrangement;
import lib_sim_bo.businessobjects.ProductArrangement;
import lib_sim_salesprocessmanagement.messages.ProcessPendingArrangementEventRequest;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.dao.DataIntegrityViolationException;

import javax.xml.datatype.DatatypeConfigurationException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import static org.mockito.Mockito.*;

@Category(UnitTest.class)
public class CcaSignedProcessorTest {

    CcaSignedProcessor ccaSignedProcessor;
    TestDataHelper testDataHelper;
    ProductArrangement productArrangement;
    ProcessPendingArrangementEventRequest request;
    F263Req f263Req;
    F263Resp f263Resp;

    @Before
    public void setUp() throws DatatypeConfigurationException {
        ccaSignedProcessor = new CcaSignedProcessor();
        testDataHelper = new TestDataHelper();
        productArrangement = testDataHelper.createProductArrangement();
        request = testDataHelper.createPpaeRequest("1", "HLX");
        ccaSignedProcessor.headerRetriever = new HeaderRetriever();
        ccaSignedProcessor.notificationEmailTemplates = mock(NotificationEmailTemplates.class);
        ccaSignedProcessor.lookUpValueRetriever = mock(LookUpValueRetriever.class);
        ccaSignedProcessor.emailDateSender = mock(EmailDateSender.class);
        ccaSignedProcessor.communicationManager = mock(CommunicationManager.class);
        ccaSignedProcessor.f263LoanDetailsRetriever = mock(F263LoanDetailsRetriever.class);
        ccaSignedProcessor.establishFinanceServiceArrangementRetriever = mock(EstablishFinanceServiceArrangementRetriever.class);
        f263Req = new F263Req();
        f263Resp = testDataHelper.createF263Resp();
    }

    @Test
    public void testProcessingPendingApplicationsForCcaSigned() {
        productArrangement.setApplicationStatus("1015");
        productArrangement.getPrimaryInvolvedParty().setIsRegistrationSelected(false);
        productArrangement.getPrimaryInvolvedParty().setEmailAddress("a.b@ts.com");
        ArrayList<String> groupCodeList = new ArrayList<>();
        ArrayList<ReferenceDataLookUp> referenceDataLookUp = new ArrayList<>();
        groupCodeList.add("CCA_PENDING_DAYS");
        when(ccaSignedProcessor.f263LoanDetailsRetriever.invokeF263(request, productArrangement)).thenReturn(f263Resp);
        when(ccaSignedProcessor.lookUpValueRetriever.getLookUpValues(groupCodeList, "HLX")).thenReturn(referenceDataLookUp);
        when(ccaSignedProcessor.emailDateSender.getEmailDate(productArrangement.getLastModifiedDate(), new ArrayList<ReferenceDataLookUp>())).thenReturn(9);
        ccaSignedProcessor.processingPendingApplications(productArrangement, request, new PpaeInvocationIdentifier());
        verify(ccaSignedProcessor.communicationManager).callSendCommunicationService(productArrangement, null, request.getHeader(), null, ActivateCommonConstant.CommunicationTypes.EMAIL);
    }

    @Test
    public void testProcessingPendingApplicationsForCcaSignedForNullEmailAddress() {
        productArrangement.setApplicationStatus("1015");
        productArrangement.getPrimaryInvolvedParty().setIsRegistrationSelected(false);
        productArrangement.getPrimaryInvolvedParty().setEmailAddress(null);
        when(ccaSignedProcessor.f263LoanDetailsRetriever.invokeF263(request, productArrangement)).thenReturn(f263Resp);
        ccaSignedProcessor.processingPendingApplications(productArrangement, request, new PpaeInvocationIdentifier());
    }

    @Test
    public void testProcessingPendingApplicationsForCcaSignedForNullF263Resp() {
        productArrangement.setApplicationStatus("1015");
        productArrangement.getPrimaryInvolvedParty().setIsRegistrationSelected(false);
        productArrangement.getPrimaryInvolvedParty().setEmailAddress(null);
        when(ccaSignedProcessor.f263LoanDetailsRetriever.invokeF263(request, productArrangement)).thenReturn(null);
        ccaSignedProcessor.processingPendingApplications(productArrangement, request, new PpaeInvocationIdentifier());
    }

    @Test
    public void testProcessingPendingApplicationsCcaSignedAndOpen() throws DatatypeConfigurationException {
        productArrangement.setApplicationStatus("1015");
        f263Resp.getApplicationDetails().setLoanApplnStatusCd(7);
        when(ccaSignedProcessor.f263LoanDetailsRetriever.invokeF263(request, productArrangement)).thenReturn(f263Resp);
        doThrow(new DatatypeConfigurationException()).when(ccaSignedProcessor.establishFinanceServiceArrangementRetriever).retrieve(request.getHeader(), f263Resp, productArrangement.getLastModifiedDate());
        ccaSignedProcessor.processingPendingApplications(productArrangement, request, new PpaeInvocationIdentifier());
    }

    @Test
    public void testProcessingPendingApplicationsCcaSigned() throws DatatypeConfigurationException {
        productArrangement.setApplicationStatus("1015");
        f263Resp.getApplicationDetails().setLoanApplnStatusCd(6);
        productArrangement.getPrimaryInvolvedParty().setIsRegistrationSelected(false);
        productArrangement.getPrimaryInvolvedParty().setEmailAddress("a@b.com");
        when(ccaSignedProcessor.f263LoanDetailsRetriever.invokeF263(request, productArrangement)).thenReturn(f263Resp);
        ArrayList<String> groupCodeList = new ArrayList<>();
        groupCodeList.add("CCA_SIGNED_DAYS");
        when(ccaSignedProcessor.lookUpValueRetriever.getLookUpValues(groupCodeList, request.getHeader().getChannelId())).thenThrow(DataIntegrityViolationException.class);
        ccaSignedProcessor.processingPendingApplications(productArrangement, request, new PpaeInvocationIdentifier());
    }

    @Test
    public void testProcessingPendingApplicationsForNullAppStatus() throws DatatypeConfigurationException {
        productArrangement.setApplicationStatus(null);
        when(ccaSignedProcessor.f263LoanDetailsRetriever.invokeF263(request, productArrangement)).thenReturn(f263Resp);
        ccaSignedProcessor.processingPendingApplications(productArrangement, request, new PpaeInvocationIdentifier());
    }

    @Test
    public void testProcessingPendingApplicationsForFinanceArrInstance() {
        FinanceServiceArrangement financeServiceArrangement = new FinanceServiceArrangement();
        financeServiceArrangement.setApplicationStatus("1016");
        financeServiceArrangement.setPrimaryInvolvedParty(new Customer());
        financeServiceArrangement.getPrimaryInvolvedParty().setIsRegistrationSelected(false);
        financeServiceArrangement.getPrimaryInvolvedParty().setEmailAddress("a@b.tcs.com");
        financeServiceArrangement.setNameAndAddressVerifiedFlag(true);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        DateFactory dateFactory = new DateFactory();
        financeServiceArrangement.setLastModifiedDate(dateFactory.stringToXMLGregorianCalendar("2015-12-23T12:21:39Z", dateFormat));
        f263Resp.getApplicationDetails().setLoanApplnStatusCd(4);
        when(ccaSignedProcessor.f263LoanDetailsRetriever.invokeF263(request, financeServiceArrangement)).thenReturn(f263Resp);
        ArrayList<String> groupCodeList = new ArrayList<>();
        ArrayList<ReferenceDataLookUp> referenceDataLookUp = new ArrayList<>();
        referenceDataLookUp.add(new ReferenceDataLookUp());
        referenceDataLookUp.get(0).setLookupValueDesc("2");
        groupCodeList.add("CCA_PENDING_DAYS");
        when(ccaSignedProcessor.lookUpValueRetriever.getLookUpValues(groupCodeList, "HLX")).thenReturn(referenceDataLookUp);
        when(ccaSignedProcessor.emailDateSender.getEmailDate(financeServiceArrangement.getLastModifiedDate(), referenceDataLookUp)).thenReturn(0);
        when(ccaSignedProcessor.notificationEmailTemplates.getNotificationEmailForCcaPending(0)).thenReturn(null);
        ccaSignedProcessor.processingPendingApplications(financeServiceArrangement, request, new PpaeInvocationIdentifier());
    }

    @Test
    public void testProcessingPendingApplicationsForFinanceArrInstanceForNullEmail() {
        FinanceServiceArrangement financeServiceArrangement = new FinanceServiceArrangement();
        financeServiceArrangement.setApplicationStatus("1016");
        financeServiceArrangement.setPrimaryInvolvedParty(new Customer());
        financeServiceArrangement.getPrimaryInvolvedParty().setIsRegistrationSelected(true);
        financeServiceArrangement.getPrimaryInvolvedParty().setEmailAddress(null);
        f263Resp.getApplicationDetails().setLoanApplnStatusCd(3);
        when(ccaSignedProcessor.f263LoanDetailsRetriever.invokeF263(request, financeServiceArrangement)).thenReturn(f263Resp);
        ccaSignedProcessor.processingPendingApplications(financeServiceArrangement, request, new PpaeInvocationIdentifier());
    }

    @Test
    public void testProcessingPendingApplicationsForFinanceArrInstanceForFalseVerifiedFlag() {
        FinanceServiceArrangement financeServiceArrangement = new FinanceServiceArrangement();
        financeServiceArrangement.setApplicationStatus("1016");
        financeServiceArrangement.setPrimaryInvolvedParty(new Customer());
        financeServiceArrangement.getPrimaryInvolvedParty().setIsRegistrationSelected(false);
        financeServiceArrangement.getPrimaryInvolvedParty().setEmailAddress("a@b.com");
        financeServiceArrangement.setNameAndAddressVerifiedFlag(false);
        f263Resp.getApplicationDetails().setLoanApplnStatusCd(3);
        when(ccaSignedProcessor.f263LoanDetailsRetriever.invokeF263(request, financeServiceArrangement)).thenReturn(f263Resp);
        ccaSignedProcessor.processingPendingApplications(financeServiceArrangement, request, new PpaeInvocationIdentifier());
    }
}

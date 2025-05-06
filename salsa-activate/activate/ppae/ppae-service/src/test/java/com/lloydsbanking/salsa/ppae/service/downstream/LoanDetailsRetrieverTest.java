package com.lloydsbanking.salsa.ppae.service.downstream;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.downstream.pad.client.q028.Q028Client;
import com.lloydsbanking.salsa.header.gmo.HeaderRetriever;
import com.lloydsbanking.salsa.ppae.TestDataHelper;
import com.lloydsbanking.salsa.soap.pad.q028.objects.*;
import com.lloydstsb.schema.infrastructure.soap.ContactPoint;
import com.lloydstsb.schema.infrastructure.soap.ResultCondition;
import com.lloydstsb.schema.infrastructure.soap.ServiceRequest;
import lib_sim_bo.businessobjects.Customer;
import lib_sim_bo.businessobjects.CustomerScore;
import lib_sim_bo.businessobjects.FinanceServiceArrangement;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.oasis_open.docs.wss._2004._01.oasis_200401_wss_wssecurity_secext_1_0.SecurityHeaderType;

import javax.xml.ws.WebServiceException;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@Category(UnitTest.class)
public class LoanDetailsRetrieverTest {

    LoanDetailsRetriever retriever;
    TestDataHelper testDataHelper;

    @Before
    public void setUp() {
        testDataHelper = new TestDataHelper();
        retriever = new LoanDetailsRetriever();
        retriever.headerRetriever = new HeaderRetriever();
        retriever.q028Client = mock(Q028Client.class);
    }

    @Test
    public void retrieveTestWithCustomerAndPadStatusCCASigned() {
        FinanceServiceArrangement financeServiceArrangement = testDataHelper.createFinanceArrangementForCC();
        Customer customer = new Customer();
        customer.getCustomerScore().add(new CustomerScore());
        customer.getCustomerScore().get(0).setScoreIdentifier("1234");
        financeServiceArrangement.setPrimaryInvolvedParty(customer);
        Q028Resp q028Resp = new Q028Resp();
        ApplicantDetails applicantDetails = new ApplicantDetails();
        applicantDetails.getParty().add(new Party());
        applicantDetails.getParty().get(0).setPartyId(985400);
        q028Resp.setApplicantDetails(applicantDetails);
        ApplicationDetails applicationDetails = new ApplicationDetails();
        applicationDetails.setLoanApplnStatusCd(6);
        applicationDetails.setLastCCAValidDt("01032016");
        q028Resp.setApplicationDetails(applicationDetails);
        when(retriever.q028Client.retrieveLoanDetails(any(Q028Req.class), any(ContactPoint.class), any(ServiceRequest.class), any(SecurityHeaderType.class))).thenReturn(q028Resp);
        Q028Resp response=retriever.retrieve(financeServiceArrangement, testDataHelper.createPpaeRequestHeader("LTB"));
        verify(retriever.q028Client).retrieveLoanDetails(any(Q028Req.class), any(ContactPoint.class), any(ServiceRequest.class), any(SecurityHeaderType.class));
        assertEquals(985400,response.getApplicantDetails().getParty().get(0).getPartyId());
        assertEquals(6,response.getApplicationDetails().getLoanApplnStatusCd());
    }

    @Test
    public void retrieveTestWithCustomer() {
        FinanceServiceArrangement financeServiceArrangement = testDataHelper.createFinanceArrangementForCC();
        Customer customer = new Customer();
        customer.getCustomerScore().add(new CustomerScore());
        customer.getCustomerScore().get(0).setScoreIdentifier("1234");
        financeServiceArrangement.setPrimaryInvolvedParty(customer);
        Q028Resp q028Resp = new Q028Resp();
        ApplicantDetails applicantDetails = new ApplicantDetails();
        applicantDetails.getParty().add(new Party());
        applicantDetails.getParty().get(0).setPartyId(985400);
        q028Resp.setApplicantDetails(applicantDetails);
        ApplicationDetails applicationDetails = new ApplicationDetails();
        applicationDetails.setLoanApplnStatusCd(8);
        applicationDetails.setLastQteValidDt("01032015");
        q028Resp.setApplicationDetails(applicationDetails);
        when(retriever.q028Client.retrieveLoanDetails(any(Q028Req.class), any(ContactPoint.class), any(ServiceRequest.class), any(SecurityHeaderType.class))).thenReturn(q028Resp);
        Q028Resp response=retriever.retrieve(financeServiceArrangement, testDataHelper.createPpaeRequestHeader("LTB"));
        verify(retriever.q028Client).retrieveLoanDetails(any(Q028Req.class), any(ContactPoint.class), any(ServiceRequest.class), any(SecurityHeaderType.class));
        assertEquals(985400,response.getApplicantDetails().getParty().get(0).getPartyId());
        assertEquals(8,response.getApplicationDetails().getLoanApplnStatusCd());
    }

    @Test
    public void retrieveTestWithCustomerAndInvalidDate() {
        FinanceServiceArrangement financeServiceArrangement = testDataHelper.createFinanceArrangementForCC();
        Customer customer = new Customer();
        customer.getCustomerScore().add(new CustomerScore());
        customer.getCustomerScore().get(0).setScoreIdentifier("1234");
        financeServiceArrangement.setPrimaryInvolvedParty(customer);
        Q028Resp q028Resp = new Q028Resp();
        ApplicantDetails applicantDetails = new ApplicantDetails();
        applicantDetails.getParty().add(new Party());
        applicantDetails.getParty().get(0).setPartyId(985400);
        q028Resp.setApplicantDetails(applicantDetails);
        ApplicationDetails applicationDetails = new ApplicationDetails();
        applicationDetails.setLoanApplnStatusCd(8);
        applicationDetails.setLastQteValidDt("0103102542015");
        q028Resp.setApplicationDetails(applicationDetails);
        when(retriever.q028Client.retrieveLoanDetails(any(Q028Req.class), any(ContactPoint.class), any(ServiceRequest.class), any(SecurityHeaderType.class))).thenReturn(q028Resp);
        Q028Resp response=retriever.retrieve(financeServiceArrangement, testDataHelper.createPpaeRequestHeader("LTB"));
        verify(retriever.q028Client).retrieveLoanDetails(any(Q028Req.class), any(ContactPoint.class), any(ServiceRequest.class), any(SecurityHeaderType.class));
        assertEquals(985400,response.getApplicantDetails().getParty().get(0).getPartyId());
        assertEquals(8,response.getApplicationDetails().getLoanApplnStatusCd());
    }

    @Test
    public void retrieveTestWithCustomerAndNoApplicationDetails() {
        FinanceServiceArrangement financeServiceArrangement = testDataHelper.createFinanceArrangementForCC();
        Customer customer = new Customer();
        customer.getCustomerScore().add(new CustomerScore());
        customer.getCustomerScore().get(0).setScoreIdentifier("1234");
        financeServiceArrangement.setPrimaryInvolvedParty(customer);
        Q028Resp q028Resp = new Q028Resp();
        ApplicantDetails applicantDetails = new ApplicantDetails();
        applicantDetails.getParty().add(new Party());
        applicantDetails.getParty().get(0).setPartyId(985400);
        q028Resp.setApplicantDetails(applicantDetails);
        when(retriever.q028Client.retrieveLoanDetails(any(Q028Req.class), any(ContactPoint.class), any(ServiceRequest.class), any(SecurityHeaderType.class))).thenReturn(q028Resp);
        Q028Resp response=retriever.retrieve(financeServiceArrangement, testDataHelper.createPpaeRequestHeader("LTB"));
        verify(retriever.q028Client).retrieveLoanDetails(any(Q028Req.class), any(ContactPoint.class), any(ServiceRequest.class), any(SecurityHeaderType.class));
        assertEquals(985400,response.getApplicantDetails().getParty().get(0).getPartyId());
    }


    @Test
    public void retrieveTestWithReasonCode() {
        FinanceServiceArrangement financeServiceArrangement = testDataHelper.createFinanceArrangementForCC();
        Customer customer = new Customer();
        customer.getCustomerScore().add(new CustomerScore());
        customer.getCustomerScore().get(0).setScoreIdentifier("1234");
        financeServiceArrangement.setPrimaryInvolvedParty(customer);
        Q028Resp q028Resp = new Q028Resp();
        q028Resp.setQ028Result(new Q028Result());
        q028Resp.getQ028Result().setResultCondition(new ResultCondition());
        q028Resp.getQ028Result().getResultCondition().setReasonCode(5);
        when(retriever.q028Client.retrieveLoanDetails(any(Q028Req.class), any(ContactPoint.class), any(ServiceRequest.class), any(SecurityHeaderType.class))).thenReturn(q028Resp);
        retriever.retrieve(financeServiceArrangement, testDataHelper.createPpaeRequestHeader("LTB"));
        verify(retriever.q028Client).retrieveLoanDetails(any(Q028Req.class), any(ContactPoint.class), any(ServiceRequest.class), any(SecurityHeaderType.class));
    }

    @Test
    public void retrieveTest() {
        when(retriever.q028Client.retrieveLoanDetails(any(Q028Req.class), any(ContactPoint.class), any(ServiceRequest.class), any(SecurityHeaderType.class))).thenReturn(new Q028Resp());
        FinanceServiceArrangement financeServiceArrangement = testDataHelper.createFinanceArrangementForCC();
        retriever.retrieve(financeServiceArrangement, testDataHelper.createPpaeRequestHeader("LTB"));
        verify(retriever.q028Client).retrieveLoanDetails(any(Q028Req.class), any(ContactPoint.class), any(ServiceRequest.class), any(SecurityHeaderType.class));
    }

    @Test
    public void retrieveTestWithException() {
        when(retriever.q028Client.retrieveLoanDetails(any(Q028Req.class), any(ContactPoint.class), any(ServiceRequest.class), any(SecurityHeaderType.class))).thenThrow(WebServiceException.class);
        FinanceServiceArrangement financeServiceArrangement = testDataHelper.createFinanceArrangementForCC();
        retriever.retrieve(financeServiceArrangement, testDataHelper.createPpaeRequestHeader("LTB"));
    }
}

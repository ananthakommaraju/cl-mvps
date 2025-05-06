package com.lloydsbanking.salsa.offer.verify;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.logging.application.CustomerTraceLog;
import com.lloydsbanking.salsa.offer.OfferException;
import com.lloydsbanking.salsa.offer.TestDataHelper;
import com.lloydsbanking.salsa.offer.exception.ExceptionUtility;
import com.lloydsbanking.salsa.offer.pam.LookupDataRetriever;
import com.lloydsbanking.salsa.offer.verify.downstream.EidvRetriever;
import lib_sim_gmo.exception.DataNotAvailableErrorMsg;
import lib_sim_gmo.exception.InternalServiceErrorMsg;
import lib_sim_gmo.exception.ResourceNotAvailableErrorMsg;
import lib_sim_bo.businessobjects.*;
import lib_sim_gmo.messages.RequestHeader;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Category(UnitTest.class)
public class VerifyInvolvedPartyRoleServiceTest {

    ExceptionUtility exceptionUtility;
    LookupDataRetriever lookupDataRetriever;
    VerifyInvolvedPartyRoleService service;
    Customer customer;
    RequestHeader requestHeader;
    ProductArrangement productArrangement;
    TestDataHelper dataHelper;

    @Before
    public void setUp() throws Exception {
        service = new VerifyInvolvedPartyRoleService();
        exceptionUtility = mock(ExceptionUtility.class);
        service.lookupDataRetriever = mock(LookupDataRetriever.class);
        service.eidvRetriever = mock(EidvRetriever.class);

        customer = new Customer();
        requestHeader = new RequestHeader();
        productArrangement = new ProductArrangement();
        productArrangement.setAssociatedProduct(new Product());
        dataHelper = new TestDataHelper();

        service.customerTraceLog = mock(CustomerTraceLog.class);
        when(service.customerTraceLog.getCustomerTraceEventMessage(any(Customer.class), any(String.class))).thenReturn("Customer");
    }

    @Test
    public void testVerifyWithTradeValue() throws DataNotAvailableErrorMsg, InternalServiceErrorMsg, ResourceNotAvailableErrorMsg, OfferException {
        requestHeader.setArrangementId("123");
        Individual individual = new Individual();
        customer.setIsPlayedBy(individual);
        customer.getIsPlayedBy().setEmploymentStatus("004");
        productArrangement.setPrimaryInvolvedParty(customer);
        productArrangement.setApplicationType("10002");
        productArrangement.setArrangementType("CA");

        service.verify(productArrangement, requestHeader);
        assertEquals("EIDV", productArrangement.getPrimaryInvolvedParty().getCustomerScore().get(0).getAssessmentType());
        assertEquals("REFER", productArrangement.getPrimaryInvolvedParty().getCustomerScore().get(0).getScoreResult());
    }

    @Test
    public void testVerifyForBFPOAddress() throws DataNotAvailableErrorMsg, InternalServiceErrorMsg, ResourceNotAvailableErrorMsg, OfferException {
        requestHeader.setArrangementId("123");
        productArrangement.setPrimaryInvolvedParty(customer);
        productArrangement.setApplicationType("102");
        List<PostalAddress> postalAddressList = new ArrayList<>();
        PostalAddress postalAddress = new PostalAddress();
        postalAddress.setStatusCode("CURRENT");
        postalAddress.setIsBFPOAddress(true);
        postalAddressList.add(postalAddress);
        customer.getPostalAddress().addAll(postalAddressList);
        customer.setNewCustomerIndicator(true);

        service.verify(productArrangement, requestHeader);
        assertEquals("EIDV", productArrangement.getPrimaryInvolvedParty().getCustomerScore().get(0).getAssessmentType());
        assertEquals("REFER", productArrangement.getPrimaryInvolvedParty().getCustomerScore().get(0).getScoreResult());
    }


    @Test
    public void testVerifyForCustomerChild() throws DataNotAvailableErrorMsg, InternalServiceErrorMsg, ResourceNotAvailableErrorMsg, OfferException {
        requestHeader.setArrangementId("123");
        customer.setApplicantType("01");
        productArrangement.setPrimaryInvolvedParty(customer);
        productArrangement.setApplicationType("102");
        service.verify(productArrangement, requestHeader);
        assertEquals("EIDV", productArrangement.getPrimaryInvolvedParty().getCustomerScore().get(0).getAssessmentType());
        assertEquals("N/A", productArrangement.getPrimaryInvolvedParty().getCustomerScore().get(0).getScoreResult());
    }

    @Test
    public void testVerifyForCustomer() throws DataNotAvailableErrorMsg, InternalServiceErrorMsg, ResourceNotAvailableErrorMsg, OfferException {
        requestHeader.setArrangementId("123");
        customer.setApplicantType("565896");
        productArrangement.setPrimaryInvolvedParty(customer);
        productArrangement.setApplicationType("102");
        when(service.eidvRetriever.getEidvScore(any(Customer.class), any(RequestHeader.class))).thenReturn(new CustomerScore());
        service.verify(productArrangement, requestHeader);
    }

    @Test
    public void testVerifyForCustomerWithInternalServiceError() throws DataNotAvailableErrorMsg, InternalServiceErrorMsg, ResourceNotAvailableErrorMsg, OfferException {
        requestHeader.setArrangementId("123");
        customer.setApplicantType("565896");
        productArrangement.setPrimaryInvolvedParty(customer);
        productArrangement.setApplicationType("102");
        when(service.eidvRetriever.getEidvScore(any(Customer.class), any(RequestHeader.class))).thenThrow(InternalServiceErrorMsg.class);
        service.verify(productArrangement, requestHeader);
    }

    @Test
    public void testVerifyForCustomerWithDataNotAvailableErrorMsg() throws DataNotAvailableErrorMsg, InternalServiceErrorMsg, ResourceNotAvailableErrorMsg, OfferException {
        requestHeader.setArrangementId("123");
        customer.setApplicantType("565896");
        productArrangement.setPrimaryInvolvedParty(customer);
        productArrangement.setApplicationType("102");
        when(service.eidvRetriever.getEidvScore(any(Customer.class), any(RequestHeader.class))).thenThrow(DataNotAvailableErrorMsg.class);
        service.verify(productArrangement, requestHeader);
    }


}

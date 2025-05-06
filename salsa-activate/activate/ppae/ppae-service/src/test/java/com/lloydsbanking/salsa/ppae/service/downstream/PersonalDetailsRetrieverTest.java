package com.lloydsbanking.salsa.ppae.service.downstream;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.downstream.ocis.client.f595.F595Client;
import com.lloydsbanking.salsa.header.gmo.HeaderRetriever;
import com.lloydsbanking.salsa.ppae.TestDataHelper;
import com.lloydsbanking.salsa.soap.ocis.f595.objects.*;
import com.lloydstsb.schema.infrastructure.soap.ContactPoint;
import com.lloydstsb.schema.infrastructure.soap.ResultCondition;
import com.lloydstsb.schema.infrastructure.soap.ServiceRequest;
import lib_sim_bo.businessobjects.Customer;
import lib_sim_bo.businessobjects.UnstructuredAddress;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.oasis_open.docs.wss._2004._01.oasis_200401_wss_wssecurity_secext_1_0.SecurityHeaderType;

import javax.xml.ws.WebServiceException;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@Category(UnitTest.class)
public class PersonalDetailsRetrieverTest {

    PersonalDetailsRetriever retriever;
    TestDataHelper testDataHelper;

    @Before
    public void setUp() {
        testDataHelper = new TestDataHelper();
        retriever = new PersonalDetailsRetriever();
        retriever.headerRetriever = new HeaderRetriever();
        retriever.f595Client = mock(F595Client.class);
    }

    @Test
    public void retrieveTest() {
        when(retriever.f595Client.retrievePersonalDetails(any(F595Req.class), any(ContactPoint.class), any(ServiceRequest.class), any(SecurityHeaderType.class))).thenReturn(new F595Resp());
        Customer customer = testDataHelper.createDepositArrangementForSA().getPrimaryInvolvedParty();
        customer.setCustomerIdentifier("123");
        retriever.retrieve(customer, testDataHelper.createPpaeRequestHeader("LTB"));
        verify(retriever.f595Client).retrievePersonalDetails(any(F595Req.class), any(ContactPoint.class), any(ServiceRequest.class), any(SecurityHeaderType.class));
    }

    @Test
    public void retrieveTestWithException() {
        when(retriever.f595Client.retrievePersonalDetails(any(F595Req.class), any(ContactPoint.class), any(ServiceRequest.class), any(SecurityHeaderType.class))).thenThrow(WebServiceException.class);
        Customer customer = testDataHelper.createDepositArrangementForSA().getPrimaryInvolvedParty();
        customer.setCustomerIdentifier("123");
        retriever.retrieve(customer, testDataHelper.createPpaeRequestHeader("LTB"));
        verify(retriever.f595Client).retrievePersonalDetails(any(F595Req.class), any(ContactPoint.class), any(ServiceRequest.class), any(SecurityHeaderType.class));
    }

    @Test
    public void retrieveTestWithSeverity() {
        F595Resp f595Resp=new F595Resp();
        f595Resp.setF595Result(new F595Result());
        f595Resp.getF595Result().setResultCondition(new ResultCondition());
        f595Resp.getF595Result().getResultCondition().setSeverityCode((byte)5);
        when(retriever.f595Client.retrievePersonalDetails(any(F595Req.class), any(ContactPoint.class), any(ServiceRequest.class), any(SecurityHeaderType.class))).thenReturn(f595Resp);
        Customer customer = testDataHelper.createDepositArrangementForSA().getPrimaryInvolvedParty();
        customer.setCustomerIdentifier("123");
        retriever.retrieve(customer, testDataHelper.createPpaeRequestHeader("LTB"));
        verify(retriever.f595Client).retrievePersonalDetails(any(F595Req.class), any(ContactPoint.class), any(ServiceRequest.class), any(SecurityHeaderType.class));
    }

    @Test
    public void retrieveTestWithPartyGroupAndPostCode() {
        F595Resp f595Resp=new F595Resp();
        f595Resp.setPartyGroup(new PartyGroup());
        f595Resp.getPartyGroup().setAddressGroup(new AddressGroup());
        f595Resp.getPartyGroup().getAddressGroup().setPostCd("0001254");
        f595Resp.getPartyGroup().setPersonalDetails(new PersonalDetails());
        f595Resp.setF595Result(new F595Result());
        f595Resp.getF595Result().setResultCondition(new ResultCondition());
        f595Resp.getF595Result().getResultCondition().setSeverityCode((byte)0);
        when(retriever.f595Client.retrievePersonalDetails(any(F595Req.class), any(ContactPoint.class), any(ServiceRequest.class), any(SecurityHeaderType.class))).thenReturn(f595Resp);
        Customer customer = testDataHelper.createDepositArrangementForSA().getPrimaryInvolvedParty();
        customer.getPostalAddress().get(0).setUnstructuredAddress(new UnstructuredAddress());
        customer.setCustomerIdentifier("123");
        retriever.retrieve(customer, testDataHelper.createPpaeRequestHeader("LTB"));
        verify(retriever.f595Client).retrievePersonalDetails(any(F595Req.class), any(ContactPoint.class), any(ServiceRequest.class), any(SecurityHeaderType.class));
    }

    @Test
    public void retrieveTestWithPartyGroup() {
        F595Resp f595Resp=new F595Resp();
        f595Resp.setPartyGroup(new PartyGroup());
        f595Resp.getPartyGroup().setAddressGroup(new AddressGroup());
        f595Resp.getPartyGroup().setPersonalDetails(new PersonalDetails());
        f595Resp.setF595Result(new F595Result());
        f595Resp.getF595Result().setResultCondition(new ResultCondition());
        f595Resp.getF595Result().getResultCondition().setSeverityCode((byte)0);
        when(retriever.f595Client.retrievePersonalDetails(any(F595Req.class), any(ContactPoint.class), any(ServiceRequest.class), any(SecurityHeaderType.class))).thenReturn(f595Resp);
        Customer customer = testDataHelper.createDepositArrangementForSA().getPrimaryInvolvedParty();
        customer.getPostalAddress().get(0).setUnstructuredAddress(new UnstructuredAddress());
        customer.setCustomerIdentifier("123");
        retriever.retrieve(customer, testDataHelper.createPpaeRequestHeader("LTB"));
        verify(retriever.f595Client).retrievePersonalDetails(any(F595Req.class), any(ContactPoint.class), any(ServiceRequest.class), any(SecurityHeaderType.class));
    }

    @Test
    public void retrieveTestWithPartyGroupAndDetails() {
        F595Resp f595Resp=new F595Resp();
        f595Resp.setPartyGroup(new PartyGroup());
        f595Resp.getPartyGroup().setAddressGroup(new AddressGroup());
        f595Resp.getPartyGroup().getAddressGroup().setPostCd("00012548");
        f595Resp.getPartyGroup().setPersonalDetails(new PersonalDetails());
        f595Resp.setF595Result(new F595Result());
        f595Resp.getF595Result().setResultCondition(new ResultCondition());
        f595Resp.getF595Result().getResultCondition().setSeverityCode((byte)0);
        when(retriever.f595Client.retrievePersonalDetails(any(F595Req.class), any(ContactPoint.class), any(ServiceRequest.class), any(SecurityHeaderType.class))).thenReturn(f595Resp);
        Customer customer = testDataHelper.createDepositArrangementForSA().getPrimaryInvolvedParty();
        customer.getIsPlayedBy().getIndividualName().clear();
        customer.getPostalAddress().clear();
        customer.setCustomerIdentifier("123");
        retriever.retrieve(customer, testDataHelper.createPpaeRequestHeader("LTB"));
        verify(retriever.f595Client).retrievePersonalDetails(any(F595Req.class), any(ContactPoint.class), any(ServiceRequest.class), any(SecurityHeaderType.class));
    }
}
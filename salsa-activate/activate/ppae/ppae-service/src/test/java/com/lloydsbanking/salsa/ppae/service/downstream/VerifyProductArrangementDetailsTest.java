package com.lloydsbanking.salsa.ppae.service.downstream;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.downstream.arrangement.client.wz.ArrangementClient;
import com.lloydsbanking.salsa.header.gmo.HeaderRetriever;
import com.lloydsbanking.salsa.ppae.TestDataHelper;
import com.lloydsbanking.salsa.ppae.service.convert.VerifyProductArrangementDetailsRequestFactory;
import com.lloydstsb.schema.enterprise.lcsm.BapiInformation;
import com.lloydstsb.schema.enterprise.lcsm.ResponseHeader;
import com.lloydstsb.schema.enterprise.lcsm_arrangementnegotiation.wz.ErrorInfo;
import com.lloydstsb.schema.enterprise.lcsm_arrangementnegotiation.wz.VerifyProductArrangementDetailsRequest;
import com.lloydstsb.schema.enterprise.lcsm_arrangementnegotiation.wz.VerifyProductArrangementDetailsResponse;
import com.lloydstsb.schema.infrastructure.soap.*;
import lib_sim_bo.businessobjects.BalanceTransfer;
import lib_sim_bo.businessobjects.Customer;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.oasis_open.docs.wss._2004._01.oasis_200401_wss_wssecurity_secext_1_0.SecurityHeaderType;

import javax.xml.bind.JAXBException;

import static org.junit.Assert.assertFalse;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@Category(UnitTest.class)
public class VerifyProductArrangementDetailsTest {

    VerifyProductArrangementDetails verifyProductArrangementDetails;
    TestDataHelper testDataHelper;

    @Before
    public void setUp() {
        testDataHelper = new TestDataHelper();
        verifyProductArrangementDetails = new VerifyProductArrangementDetails();
        verifyProductArrangementDetails.arrangementClient = mock(ArrangementClient.class);
        verifyProductArrangementDetails.verifyProductArrangementDetailsRequestFactory = mock(VerifyProductArrangementDetailsRequestFactory.class);
        verifyProductArrangementDetails.headerRetriever = mock(HeaderRetriever.class);
    }

    @Test
    public void verifyTest() throws ErrorInfo, JAXBException {
        boolean flag = verifyProductArrangementDetails.verify(new BalanceTransfer(), new Customer(), testDataHelper.createPpaeRequest().getHeader());
        verify(verifyProductArrangementDetails.arrangementClient).verifyProductArrangementDetails(any(VerifyProductArrangementDetailsRequest.class), any(ContactPoint.class), any(ServiceRequest.class), any(SecurityHeaderType.class), any(BapiInformation.class));
        assertFalse(flag);
    }

    @Test
    public void verifyTestWithNotNullResponse() throws ErrorInfo, JAXBException {
        VerifyProductArrangementDetailsResponse response = new VerifyProductArrangementDetailsResponse();
        when(verifyProductArrangementDetails.arrangementClient.verifyProductArrangementDetails(any(VerifyProductArrangementDetailsRequest.class), any(ContactPoint.class), any(ServiceRequest.class), any(SecurityHeaderType.class), any(BapiInformation.class))).thenReturn(response);
        boolean flag = verifyProductArrangementDetails.verify(new BalanceTransfer(), new Customer(), testDataHelper.createPpaeRequest().getHeader());
        verify(verifyProductArrangementDetails.arrangementClient).verifyProductArrangementDetails(any(VerifyProductArrangementDetailsRequest.class), any(ContactPoint.class), any(ServiceRequest.class), any(SecurityHeaderType.class), any(BapiInformation.class));
        assertFalse(flag);
    }

    @Test
    public void verifyTestWithReasonCode() throws ErrorInfo, JAXBException {
        VerifyProductArrangementDetailsResponse response = new VerifyProductArrangementDetailsResponse();
        response.setResponseHeader(new ResponseHeader());
        response.getResponseHeader().setResultCondition(new ResultCondition());
        response.getResponseHeader().getResultCondition().setExtraConditions(new ExtraConditions());
        response.getResponseHeader().getResultCondition().getExtraConditions().getCondition().add(new Condition());
        response.getResponseHeader().getResultCondition().getExtraConditions().getCondition().get(0).setReasonCode(12);
        when(verifyProductArrangementDetails.arrangementClient.verifyProductArrangementDetails(any(VerifyProductArrangementDetailsRequest.class), any(ContactPoint.class), any(ServiceRequest.class), any(SecurityHeaderType.class), any(BapiInformation.class))).thenReturn(response);
        boolean flag = verifyProductArrangementDetails.verify(new BalanceTransfer(), new Customer(), testDataHelper.createPpaeRequest().getHeader());
        verify(verifyProductArrangementDetails.arrangementClient).verifyProductArrangementDetails(any(VerifyProductArrangementDetailsRequest.class), any(ContactPoint.class), any(ServiceRequest.class), any(SecurityHeaderType.class), any(BapiInformation.class));
        assertFalse(flag);
    }

    @Test
    public void verifyTestWithZeroReasonCode() throws ErrorInfo, JAXBException {
        VerifyProductArrangementDetailsResponse response = new VerifyProductArrangementDetailsResponse();
        response.setResponseHeader(new ResponseHeader());
        response.getResponseHeader().setResultCondition(new ResultCondition());
        response.getResponseHeader().getResultCondition().setExtraConditions(new ExtraConditions());
        response.getResponseHeader().getResultCondition().getExtraConditions().getCondition().add(new Condition());
        response.getResponseHeader().getResultCondition().getExtraConditions().getCondition().get(0).setReasonCode(0);
        when(verifyProductArrangementDetails.arrangementClient.verifyProductArrangementDetails(any(VerifyProductArrangementDetailsRequest.class), any(ContactPoint.class), any(ServiceRequest.class), any(SecurityHeaderType.class), any(BapiInformation.class))).thenReturn(response);
        boolean flag = verifyProductArrangementDetails.verify(new BalanceTransfer(), new Customer(), testDataHelper.createPpaeRequest().getHeader());
        verify(verifyProductArrangementDetails.arrangementClient).verifyProductArrangementDetails(any(VerifyProductArrangementDetailsRequest.class), any(ContactPoint.class), any(ServiceRequest.class), any(SecurityHeaderType.class), any(BapiInformation.class));
        assertFalse(flag);
    }

    @Test
    public void verifyTestWithNullReasonCode() throws ErrorInfo, JAXBException {
        VerifyProductArrangementDetailsResponse response = new VerifyProductArrangementDetailsResponse();
        response.setResponseHeader(new ResponseHeader());
        response.getResponseHeader().setResultCondition(new ResultCondition());
        response.getResponseHeader().getResultCondition().setExtraConditions(new ExtraConditions());
        response.getResponseHeader().getResultCondition().getExtraConditions().getCondition().add(new Condition());
        when(verifyProductArrangementDetails.arrangementClient.verifyProductArrangementDetails(any(VerifyProductArrangementDetailsRequest.class), any(ContactPoint.class), any(ServiceRequest.class), any(SecurityHeaderType.class), any(BapiInformation.class))).thenReturn(response);
        boolean flag = verifyProductArrangementDetails.verify(new BalanceTransfer(), new Customer(), testDataHelper.createPpaeRequest().getHeader());
        verify(verifyProductArrangementDetails.arrangementClient).verifyProductArrangementDetails(any(VerifyProductArrangementDetailsRequest.class), any(ContactPoint.class), any(ServiceRequest.class), any(SecurityHeaderType.class), any(BapiInformation.class));
        assertFalse(flag);
    }

    @Test
    public void verifyTestWithEmptyCondition() throws ErrorInfo, JAXBException {
        VerifyProductArrangementDetailsResponse response = new VerifyProductArrangementDetailsResponse();
        response.setResponseHeader(new ResponseHeader());
        response.getResponseHeader().setResultCondition(new ResultCondition());
        response.getResponseHeader().getResultCondition().setExtraConditions(new ExtraConditions());
        when(verifyProductArrangementDetails.arrangementClient.verifyProductArrangementDetails(any(VerifyProductArrangementDetailsRequest.class), any(ContactPoint.class), any(ServiceRequest.class), any(SecurityHeaderType.class), any(BapiInformation.class))).thenReturn(response);
        boolean flag = verifyProductArrangementDetails.verify(new BalanceTransfer(), new Customer(), testDataHelper.createPpaeRequest().getHeader());
        verify(verifyProductArrangementDetails.arrangementClient).verifyProductArrangementDetails(any(VerifyProductArrangementDetailsRequest.class), any(ContactPoint.class), any(ServiceRequest.class), any(SecurityHeaderType.class), any(BapiInformation.class));
        assertFalse(flag);
    }

    @Test
    public void verifyTestWithEmptyResultCondition() throws ErrorInfo, JAXBException {
        VerifyProductArrangementDetailsResponse response = new VerifyProductArrangementDetailsResponse();
        response.setResponseHeader(new ResponseHeader());
        when(verifyProductArrangementDetails.arrangementClient.verifyProductArrangementDetails(any(VerifyProductArrangementDetailsRequest.class), any(ContactPoint.class), any(ServiceRequest.class), any(SecurityHeaderType.class), any(BapiInformation.class))).thenReturn(response);
        boolean flag = verifyProductArrangementDetails.verify(new BalanceTransfer(), new Customer(), testDataHelper.createPpaeRequest().getHeader());
        verify(verifyProductArrangementDetails.arrangementClient).verifyProductArrangementDetails(any(VerifyProductArrangementDetailsRequest.class), any(ContactPoint.class), any(ServiceRequest.class), any(SecurityHeaderType.class), any(BapiInformation.class));
        assertFalse(flag);
    }

    @Test
    public void verifyTestWithNoExtraCondition() throws ErrorInfo, JAXBException {
        VerifyProductArrangementDetailsResponse response = new VerifyProductArrangementDetailsResponse();
        response.setResponseHeader(new ResponseHeader());
        response.getResponseHeader().setResultCondition(new ResultCondition());
        when(verifyProductArrangementDetails.arrangementClient.verifyProductArrangementDetails(any(VerifyProductArrangementDetailsRequest.class), any(ContactPoint.class), any(ServiceRequest.class), any(SecurityHeaderType.class), any(BapiInformation.class))).thenReturn(response);
        boolean flag = verifyProductArrangementDetails.verify(new BalanceTransfer(), new Customer(), testDataHelper.createPpaeRequest().getHeader());
        verify(verifyProductArrangementDetails.arrangementClient).verifyProductArrangementDetails(any(VerifyProductArrangementDetailsRequest.class), any(ContactPoint.class), any(ServiceRequest.class), any(SecurityHeaderType.class), any(BapiInformation.class));
        assertFalse(flag);
    }


}

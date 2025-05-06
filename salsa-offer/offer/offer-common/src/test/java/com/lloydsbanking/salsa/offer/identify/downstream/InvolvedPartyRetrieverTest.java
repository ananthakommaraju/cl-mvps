package com.lloydsbanking.salsa.offer.identify.downstream;


import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.downstream.ocis.client.f061.F061Client;
import com.lloydsbanking.salsa.header.gmo.HeaderRetriever;
import com.lloydsbanking.salsa.offer.TestDataHelper;
import com.lloydsbanking.salsa.offer.exception.ExceptionUtility;
import com.lloydsbanking.salsa.soap.ocis.f061.objects.F061Req;
import com.lloydsbanking.salsa.soap.ocis.f061.objects.F061Resp;
import com.lloydstsb.schema.infrastructure.soap.ContactPoint;
import com.lloydstsb.schema.infrastructure.soap.ServiceRequest;
import lib_sim_gmo.exception.ExternalBusinessErrorMsg;
import lib_sim_gmo.exception.ExternalServiceErrorMsg;
import lib_sim_gmo.exception.ResourceNotAvailableErrorMsg;
import lib_sim_bo.businessobjects.DepositArrangement;
import lib_sim_gmo.messages.RequestHeader;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.oasis_open.docs.wss._2004._01.oasis_200401_wss_wssecurity_secext_1_0.SecurityHeaderType;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Category(UnitTest.class)
public class InvolvedPartyRetrieverTest {

    private InvolvedPartyRetriever retriever;
    private F061Resp f061Resp;
    DepositArrangement depositArrangement;
    RequestHeader requestHeader;

    @Before
    public void setUp() {
        retriever = new InvolvedPartyRetriever();
        retriever.f061Client = mock(F061Client.class);
        retriever.headerRetriever = mock(HeaderRetriever.class);
        retriever.exceptionUtility = new ExceptionUtility();
        depositArrangement = new TestDataHelper().createDepositArrangement();
        requestHeader = new TestDataHelper().createOpaPcaRequestHeader("LTB");

    }

    @Test
    public void testRetrieveInvolvedPartyDetailsIsSuccessful() throws ExternalServiceErrorMsg, ResourceNotAvailableErrorMsg, ExternalBusinessErrorMsg {


        f061Resp = new TestDataHelper().createF061Resp(0);
        when(retriever.f061Client.f061(any(F061Req.class), any(ContactPoint.class), any(ServiceRequest.class), any(SecurityHeaderType.class))).thenReturn(f061Resp);
        F061Resp resp = retriever.retrieveInvolvedPartyDetails(requestHeader, "123");
        assertEquals("0", resp.getF061Result().getResultCondition().getReasonCode().toString());

    }

    @Test(expected = ExternalServiceErrorMsg.class)
    public void testRetrieveInvolvedPartyDetailsThrowsExternalServiceError() throws ExternalServiceErrorMsg, ResourceNotAvailableErrorMsg, ExternalBusinessErrorMsg {
        F061Resp f061Resp = new TestDataHelper().createF061Resp(160999);
        when(retriever.f061Client.f061(any(F061Req.class), any(ContactPoint.class), any(ServiceRequest.class), any(SecurityHeaderType.class))).thenReturn(f061Resp);
        retriever.retrieveInvolvedPartyDetails(requestHeader, "123");
    }

    @Test(expected = ExternalBusinessErrorMsg.class)
    public void testRetrieveInvolvedPartyDetailsThrowsExternalBusinessError() throws ExternalServiceErrorMsg, ResourceNotAvailableErrorMsg, ExternalBusinessErrorMsg {
        F061Resp f061Resp = new TestDataHelper().createF061Resp(163004);
        when(retriever.f061Client.f061(any(F061Req.class), any(ContactPoint.class), any(ServiceRequest.class), any(SecurityHeaderType.class))).thenReturn(f061Resp);
        retriever.retrieveInvolvedPartyDetails(requestHeader, "123");
    }


    @Test(expected = ExternalBusinessErrorMsg.class)
    public void testRetrieveInvolvedPartyDetailsThrowsExternalBusinessErrorBasedOnCurrentAddress() throws ExternalServiceErrorMsg, ResourceNotAvailableErrorMsg, ExternalBusinessErrorMsg {
        F061Resp f061Resp = new TestDataHelper().createF061Resp(0);
        f061Resp.getPartyEnqData().getAddressData().setAddressStatusCd("002");
        when(retriever.f061Client.f061(any(F061Req.class), any(ContactPoint.class), any(ServiceRequest.class), any(SecurityHeaderType.class))).thenReturn(f061Resp);
        retriever.retrieveInvolvedPartyDetails(requestHeader, "123");
    }

}

package com.lloydsbanking.salsa.apapca.service.fulfil.orderaccess.downstream;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.activate.utility.ExceptionUtilityActivate;
import com.lloydsbanking.salsa.apapca.TestDataHelper;
import com.lloydsbanking.salsa.downstream.cmas.client.c818.C818Client;
import com.lloydsbanking.salsa.header.gmo.HeaderRetriever;
import com.lloydsbanking.salsa.soap.cmas.c818.objects.C818Req;
import com.lloydsbanking.salsa.soap.cmas.c818.objects.C818Resp;
import com.lloydstsb.schema.infrastructure.soap.ContactPoint;
import com.lloydstsb.schema.infrastructure.soap.ServiceRequest;
import lib_sim_gmo.messages.RequestHeader;
import lib_sim_salesprocessmanagement.ia_activateproductarrangement.ActivateProductArrangementExternalSystemErrorMsg;
import lib_sim_salesprocessmanagement.ia_activateproductarrangement.ActivateProductArrangementResourceNotAvailableErrorMsg;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.oasis_open.docs.wss._2004._01.oasis_200401_wss_wssecurity_secext_1_0.SecurityHeaderType;

import javax.xml.ws.WebServiceException;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Category(UnitTest.class)
public class FulfilCardOrderRetrieverTest {
    private FulfilCardOrderRetriever fulfilCardOrderRetriever;

    RequestHeader requestHeader;

    TestDataHelper testDataHelper;

    ContactPoint contactPoint;

    ServiceRequest serviceRequest;

    SecurityHeaderType securityHeaderType;

    @Before
    public void setUp() {
        testDataHelper = new TestDataHelper();
        fulfilCardOrderRetriever = new FulfilCardOrderRetriever();
        fulfilCardOrderRetriever.exceptionUtilityActivate = mock(ExceptionUtilityActivate.class);
        fulfilCardOrderRetriever.headerRetriever = new HeaderRetriever();
        fulfilCardOrderRetriever.c818Client = mock(C818Client.class);
        requestHeader = testDataHelper.createApaRequestHeader();
        contactPoint = fulfilCardOrderRetriever.headerRetriever.getContactPoint(requestHeader.getLloydsHeaders());
        serviceRequest = fulfilCardOrderRetriever.headerRetriever.getServiceRequest(requestHeader.getLloydsHeaders(), "http://www.lloydstsb.com/Schema/Personal/CreditCardPlatform/CMAS/C818_AddCardOrder", "C818");
        securityHeaderType = fulfilCardOrderRetriever.headerRetriever.getSecurityHeader(requestHeader.getLloydsHeaders());
    }

    @Test
    public void testFulfilCardOrderRetriever() throws ActivateProductArrangementResourceNotAvailableErrorMsg, ActivateProductArrangementExternalSystemErrorMsg {
        C818Req c818Req = testDataHelper.createC818Request(testDataHelper.createCardOrderAdd(), testDataHelper.createCardOrderAddNew(), testDataHelper.createCardOrderCBSCCA(), null, testDataHelper.createCardOrderActions());
        C818Resp c818Resp = testDataHelper.createC818Resp();
        when(fulfilCardOrderRetriever.c818Client.fulfilCardOrder(c818Req, contactPoint, serviceRequest, securityHeaderType)).thenReturn(c818Resp);

        C818Req c818Request = C818Factory.createC818Request(testDataHelper.createCardOrderAdd(), testDataHelper.createCardOrderAddNew(),
                testDataHelper.createCardOrderCBSCCA(), null, testDataHelper.createCardOrderActions());

        C818Resp c818Resp1 = fulfilCardOrderRetriever.getResponse(c818Request, requestHeader);

        assertEquals(281254276, c818Resp1.getCardOrderId());

    }

    @Test(expected = ActivateProductArrangementResourceNotAvailableErrorMsg.class)
    public void testFulfilCardOrderRetrieverWebServiceException() throws ActivateProductArrangementResourceNotAvailableErrorMsg, ActivateProductArrangementExternalSystemErrorMsg {
        C818Req c818Req = testDataHelper.createC818Request(testDataHelper.createCardOrderAdd(), testDataHelper.createCardOrderAddNew(), testDataHelper.createCardOrderCBSCCA(), null, testDataHelper.createCardOrderActions());
        when(fulfilCardOrderRetriever.c818Client.fulfilCardOrder(c818Req, contactPoint, serviceRequest, securityHeaderType)).thenThrow(new WebServiceException());
        when(fulfilCardOrderRetriever.exceptionUtilityActivate.resourceNotAvailableError(requestHeader, null)).thenThrow(ActivateProductArrangementResourceNotAvailableErrorMsg.class);

        C818Req c818Request = C818Factory.createC818Request(testDataHelper.createCardOrderAdd(),
                testDataHelper.createCardOrderAddNew(), testDataHelper.createCardOrderCBSCCA(),
                null, testDataHelper.createCardOrderActions());

        fulfilCardOrderRetriever.getResponse(c818Request, requestHeader);
    }

    @Test(expected = ActivateProductArrangementExternalSystemErrorMsg.class)
    public void testFulfilCardOrderRetrieverExternalServiceException() throws ActivateProductArrangementResourceNotAvailableErrorMsg, ActivateProductArrangementExternalSystemErrorMsg {
        C818Req c818Req = testDataHelper.createC818Request(testDataHelper.createCardOrderAdd(), testDataHelper.createCardOrderAddNew(), testDataHelper.createCardOrderCBSCCA(), null, testDataHelper.createCardOrderActions());
        C818Resp c818Resp = testDataHelper.createC818Resp();
        c818Resp.getC818Result().getResultCondition().setReasonCode(218018);
        when(fulfilCardOrderRetriever.c818Client.fulfilCardOrder(c818Req, contactPoint, serviceRequest, securityHeaderType)).thenReturn(c818Resp);
        when(fulfilCardOrderRetriever.exceptionUtilityActivate.externalServiceError(requestHeader, null, "218018")).thenThrow(ActivateProductArrangementExternalSystemErrorMsg.class);

        C818Req c818Request = C818Factory.createC818Request(testDataHelper.createCardOrderAdd(),
                testDataHelper.createCardOrderAddNew(), testDataHelper.createCardOrderCBSCCA(),
                null, testDataHelper.createCardOrderActions());

        fulfilCardOrderRetriever.getResponse(c818Request, requestHeader);
    }
}

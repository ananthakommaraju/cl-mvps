package com.lloydsbanking.salsa.apapca.service.fulfil.orderaccess.downstream;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.activate.utility.ExceptionUtilityActivate;
import com.lloydsbanking.salsa.apapca.TestDataHelper;
import com.lloydsbanking.salsa.downstream.cmas.client.c846.C846Client;
import com.lloydsbanking.salsa.header.gmo.HeaderRetriever;
import com.lloydsbanking.salsa.soap.cmas.c846.objects.C846Req;
import com.lloydsbanking.salsa.soap.cmas.c846.objects.C846Resp;
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
public class RetrieveEligibleCardsRetriverTest {
    private RetrieveEligibleCardsRetriver retrieveEligibleCardsRetriver;

    RequestHeader requestHeader;

    TestDataHelper testDataHelper;

    ContactPoint contactPoint;

    ServiceRequest serviceRequest;

    SecurityHeaderType securityHeaderType;

    @Before
    public void setUp() {
        testDataHelper = new TestDataHelper();
        retrieveEligibleCardsRetriver = new RetrieveEligibleCardsRetriver();
        retrieveEligibleCardsRetriver.exceptionUtilityActivate = mock(ExceptionUtilityActivate.class);
        retrieveEligibleCardsRetriver.c846Client = mock(C846Client.class);
        retrieveEligibleCardsRetriver.headerRetriever = new HeaderRetriever();
        requestHeader = testDataHelper.createApaRequestHeader();
        contactPoint = retrieveEligibleCardsRetriver.headerRetriever.getContactPoint(requestHeader.getLloydsHeaders());
        serviceRequest = retrieveEligibleCardsRetriver.headerRetriever.getServiceRequest(requestHeader.getLloydsHeaders(), "http://xml.lloydsbanking.com/Schema/Enterprise/ProductManufacturing/CMAS/C846_BroCardTypePlasticType", "C846");
        securityHeaderType = retrieveEligibleCardsRetriver.headerRetriever.getSecurityHeader(requestHeader.getLloydsHeaders());
    }

    @Test
    public void testRetrieveEligibleCards() throws ActivateProductArrangementResourceNotAvailableErrorMsg, ActivateProductArrangementExternalSystemErrorMsg {
        C846Req c846Req = testDataHelper.createC846Request("0071776000", "0", "R", 50, 137178748);
        C846Resp c846Resp = testDataHelper.createC846Response();
        when(retrieveEligibleCardsRetriver.c846Client.retrieveEligibleCards(c846Req, contactPoint, serviceRequest, securityHeaderType)).thenReturn(c846Resp);
        C846Resp c846Resp1 = retrieveEligibleCardsRetriver.getResponse(c846Req, requestHeader);
        assertEquals("VISA Payment Card", c846Resp1.getCardTypes().getCardType().get(0).getCardTypeNr());

    }

    @Test(expected = ActivateProductArrangementResourceNotAvailableErrorMsg.class)
    public void testRetrieveEligibleCardsWebServiceException() throws ActivateProductArrangementResourceNotAvailableErrorMsg, ActivateProductArrangementExternalSystemErrorMsg {
        C846Req c846Req = testDataHelper.createC846Request("0071776000", "0", "R", 50, 137178748);
        when(retrieveEligibleCardsRetriver.c846Client.retrieveEligibleCards(c846Req, contactPoint, serviceRequest, securityHeaderType)).thenThrow(new WebServiceException());
        when(retrieveEligibleCardsRetriver.exceptionUtilityActivate.resourceNotAvailableError(requestHeader, null)).thenThrow(ActivateProductArrangementResourceNotAvailableErrorMsg.class);
        retrieveEligibleCardsRetriver.getResponse(c846Req, requestHeader);

    }

    @Test(expected = ActivateProductArrangementExternalSystemErrorMsg.class)
    public void testRetrieveEligibleCardsExternalServiceException() throws ActivateProductArrangementResourceNotAvailableErrorMsg, ActivateProductArrangementExternalSystemErrorMsg {
        C846Req c846Req = testDataHelper.createC846Request("0071776000", "0", "R", 50, 137178748);
        C846Resp c846Resp = testDataHelper.createC846Response();
        c846Resp.getC846Result().getResultCondition().setReasonCode(218046);
        when(retrieveEligibleCardsRetriver.c846Client.retrieveEligibleCards(c846Req, contactPoint, serviceRequest, securityHeaderType)).thenReturn(c846Resp);
        when(retrieveEligibleCardsRetriver.exceptionUtilityActivate.externalServiceError(requestHeader, null, "218046")).thenThrow(ActivateProductArrangementExternalSystemErrorMsg.class);
        retrieveEligibleCardsRetriver.getResponse(c846Req, requestHeader);

    }

    @Test
    public void testRetrieveEligibleCardsForNull() throws ActivateProductArrangementResourceNotAvailableErrorMsg, ActivateProductArrangementExternalSystemErrorMsg {
        C846Req c846Req = testDataHelper.createC846Request("0071776000", "0", "R", 50, 137178748);
        C846Resp c846Resp = testDataHelper.createC846Response();
        c846Resp.setC846Result(null);
        when(retrieveEligibleCardsRetriver.c846Client.retrieveEligibleCards(c846Req, contactPoint, serviceRequest, securityHeaderType)).thenReturn(c846Resp);
        C846Resp c846Resp1 = retrieveEligibleCardsRetriver.getResponse(c846Req, requestHeader);
        assertEquals("VISA Payment Card", c846Resp1.getCardTypes().getCardType().get(0).getCardTypeNr());
    }

    @Test
    public void testRetrieveEligibleCardsWhenResultConditionNull() throws ActivateProductArrangementResourceNotAvailableErrorMsg, ActivateProductArrangementExternalSystemErrorMsg {
        C846Req c846Req = testDataHelper.createC846Request("0071776000", "0", "R", 50, 137178748);
        C846Resp c846Resp = testDataHelper.createC846Response();
        c846Resp.getC846Result().setResultCondition(null);
        when(retrieveEligibleCardsRetriver.c846Client.retrieveEligibleCards(c846Req, contactPoint, serviceRequest, securityHeaderType)).thenReturn(c846Resp);
        C846Resp c846Resp1 = retrieveEligibleCardsRetriver.getResponse(c846Req, requestHeader);
        assertEquals("VISA Payment Card", c846Resp1.getCardTypes().getCardType().get(0).getCardTypeNr());
    }

    @Test
    public void testRetrieveEligibleCardsWhenReasonCodeNull() throws ActivateProductArrangementResourceNotAvailableErrorMsg, ActivateProductArrangementExternalSystemErrorMsg {
        C846Req c846Req = testDataHelper.createC846Request("0071776000", "0", "R", 50, 137178748);
        C846Resp c846Resp = testDataHelper.createC846Response();
        c846Resp.getC846Result().getResultCondition().setReasonCode(null);
        when(retrieveEligibleCardsRetriver.c846Client.retrieveEligibleCards(c846Req, contactPoint, serviceRequest, securityHeaderType)).thenReturn(c846Resp);
        C846Resp c846Resp1 = retrieveEligibleCardsRetriver.getResponse(c846Req, requestHeader);
        assertEquals("VISA Payment Card", c846Resp1.getCardTypes().getCardType().get(0).getCardTypeNr());
    }
}

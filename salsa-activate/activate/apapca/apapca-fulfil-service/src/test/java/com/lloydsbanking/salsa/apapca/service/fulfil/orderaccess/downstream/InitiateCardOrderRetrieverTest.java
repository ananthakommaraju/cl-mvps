package com.lloydsbanking.salsa.apapca.service.fulfil.orderaccess.downstream;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.activate.utility.ExceptionUtilityActivate;
import com.lloydsbanking.salsa.apapca.TestDataHelper;
import com.lloydsbanking.salsa.downstream.cmas.client.c808.C808Client;
import com.lloydsbanking.salsa.header.gmo.HeaderRetriever;
import com.lloydsbanking.salsa.soap.cmas.c808.objects.C808Req;
import com.lloydsbanking.salsa.soap.cmas.c808.objects.C808Resp;
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
public class InitiateCardOrderRetrieverTest {
    private InitiateCardOrderRetriever initiateCardOrderRetriever;

    RequestHeader requestHeader;

    TestDataHelper testDataHelper;

    ContactPoint contactPoint;

    ServiceRequest serviceRequest;

    SecurityHeaderType securityHeaderType;

    @Before
    public void setUp() {
        testDataHelper = new TestDataHelper();
        initiateCardOrderRetriever = new InitiateCardOrderRetriever();
        initiateCardOrderRetriever.exceptionUtilityActivate = mock(ExceptionUtilityActivate.class);
        initiateCardOrderRetriever.headerRetriever = new HeaderRetriever();
        initiateCardOrderRetriever.c808Client = mock(C808Client.class);
        requestHeader = testDataHelper.createApaRequestHeader();
        contactPoint = initiateCardOrderRetriever.headerRetriever.getContactPoint(requestHeader.getLloydsHeaders());
        serviceRequest = initiateCardOrderRetriever.headerRetriever.getServiceRequest(requestHeader.getLloydsHeaders(), "http://www.lloydstsb.com/Schema/Personal/CreditCardPlatform/CMAS/C808_EnqInitNewCrdOrd", "C808");
        securityHeaderType = initiateCardOrderRetriever.headerRetriever.getSecurityHeader(requestHeader.getLloydsHeaders());

    }

    @Test
    public void testInitiateCardOrderRetriever() throws ActivateProductArrangementResourceNotAvailableErrorMsg, ActivateProductArrangementExternalSystemErrorMsg {
        C808Req c808Req = testDataHelper.createC808Request("779129", "09543160", 50);
        C808Resp c808Resp = testDataHelper.createC808Res();
        when(initiateCardOrderRetriever.c808Client.initiateCardOrder(c808Req, contactPoint, serviceRequest, securityHeaderType)).thenReturn(c808Resp);

        C808Resp c808Resp1 = initiateCardOrderRetriever.getResponse(c808Req, requestHeader);
        assertEquals("R", c808Resp1.getCardAuthoriserNew().getCardOrderCBSDecision().getCustomerDecisionTypeCd());

    }

    @Test(expected = ActivateProductArrangementResourceNotAvailableErrorMsg.class)
    public void testInitiateCardOrderRetrieverWebServiceException() throws ActivateProductArrangementResourceNotAvailableErrorMsg, ActivateProductArrangementExternalSystemErrorMsg {
        C808Req c808Req = testDataHelper.createC808Request("779129", "09543160", 50);
        when(initiateCardOrderRetriever.c808Client.initiateCardOrder(c808Req, contactPoint, serviceRequest, securityHeaderType)).thenThrow(new WebServiceException());
        when(initiateCardOrderRetriever.exceptionUtilityActivate.resourceNotAvailableError(requestHeader, null)).thenThrow(ActivateProductArrangementResourceNotAvailableErrorMsg.class);
        initiateCardOrderRetriever.getResponse(c808Req, requestHeader);

    }

    @Test(expected = ActivateProductArrangementExternalSystemErrorMsg.class)
    public void testInitiateCardOrderRetrieverExternalServiceException() throws ActivateProductArrangementResourceNotAvailableErrorMsg, ActivateProductArrangementExternalSystemErrorMsg {
        C808Req c808Req = testDataHelper.createC808Request("779129", "09543160", 50);
        C808Resp c808Resp = testDataHelper.createC808Res();
        c808Resp.getC808Result().getResultCondition().setReasonCode(218008);
        when(initiateCardOrderRetriever.c808Client.initiateCardOrder(c808Req, contactPoint, serviceRequest, securityHeaderType)).thenReturn(c808Resp);
        when(initiateCardOrderRetriever.exceptionUtilityActivate.externalServiceError(requestHeader, null, "218008")).thenThrow(ActivateProductArrangementExternalSystemErrorMsg.class);
        initiateCardOrderRetriever.getResponse(c808Req, requestHeader);

    }

    @Test
    public void testInitiateCardOrderRetrieverForNull() throws ActivateProductArrangementResourceNotAvailableErrorMsg, ActivateProductArrangementExternalSystemErrorMsg {
        C808Req c808Req = testDataHelper.createC808Request("779129", "09543160", 50);
        C808Resp c808Resp = testDataHelper.createC808Res();
        c808Resp.setC808Result(null);
        when(initiateCardOrderRetriever.c808Client.initiateCardOrder(c808Req, contactPoint, serviceRequest, securityHeaderType)).thenReturn(c808Resp);

        C808Resp c808Resp1 = initiateCardOrderRetriever.getResponse(c808Req, requestHeader);
        assertEquals("R", c808Resp1.getCardAuthoriserNew().getCardOrderCBSDecision().getCustomerDecisionTypeCd());
    }

    @Test
    public void testInitiateCardOrderRetrieverWhenResultConditionNull() throws ActivateProductArrangementResourceNotAvailableErrorMsg, ActivateProductArrangementExternalSystemErrorMsg {
        C808Req c808Req = testDataHelper.createC808Request("779129", "09543160", 50);
        C808Resp c808Resp = testDataHelper.createC808Res();
        c808Resp.getC808Result().setResultCondition(null);
        when(initiateCardOrderRetriever.c808Client.initiateCardOrder(c808Req, contactPoint, serviceRequest, securityHeaderType)).thenReturn(c808Resp);

        C808Resp c808Resp1 = initiateCardOrderRetriever.getResponse(c808Req, requestHeader);
        assertEquals("R", c808Resp1.getCardAuthoriserNew().getCardOrderCBSDecision().getCustomerDecisionTypeCd());
    }

    @Test
    public void testInitiateCardOrderRetrieverWhenReasonCodeNull() throws ActivateProductArrangementResourceNotAvailableErrorMsg, ActivateProductArrangementExternalSystemErrorMsg {
        C808Req c808Req = testDataHelper.createC808Request("779129", "09543160", 50);
        C808Resp c808Resp = testDataHelper.createC808Res();
        c808Resp.getC808Result().getResultCondition().setReasonCode(null);
        when(initiateCardOrderRetriever.c808Client.initiateCardOrder(c808Req, contactPoint, serviceRequest, securityHeaderType)).thenReturn(c808Resp);

        C808Resp c808Resp1 = initiateCardOrderRetriever.getResponse(c808Req, requestHeader);
        assertEquals("R", c808Resp1.getCardAuthoriserNew().getCardOrderCBSDecision().getCustomerDecisionTypeCd());
    }
}

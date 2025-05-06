package com.lloydsbanking.salsa.apapca.service.fulfil.orderaccess.downstream;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.activate.utility.ExceptionUtilityActivate;
import com.lloydsbanking.salsa.apapca.TestDataHelper;
import com.lloydsbanking.salsa.downstream.cmas.client.c812.C812Client;
import com.lloydsbanking.salsa.header.gmo.HeaderRetriever;
import com.lloydsbanking.salsa.soap.cmas.c812.objects.C812Req;
import com.lloydsbanking.salsa.soap.cmas.c812.objects.C812Resp;
import com.lloydstsb.schema.infrastructure.soap.ContactPoint;
import com.lloydstsb.schema.infrastructure.soap.ServiceRequest;
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
public class ValidateCardOrderRetrieverTest {
    private ValidateCardOrderRetriever validateCardOrderRetriever;

    lib_sim_gmo.messages.RequestHeader requestHeader;

    TestDataHelper testDataHelper;

    ContactPoint contactPoint;

    ServiceRequest serviceRequest;

    SecurityHeaderType securityHeaderType;

    @Before
    public void setUp() {
        testDataHelper = new TestDataHelper();
        validateCardOrderRetriever = new ValidateCardOrderRetriever();

        validateCardOrderRetriever.exceptionUtilityActivate = mock(ExceptionUtilityActivate.class);
        validateCardOrderRetriever.c812Client = mock(C812Client.class);
        validateCardOrderRetriever.headerRetriever = new HeaderRetriever();
        requestHeader = testDataHelper.createApaRequestHeader();
        contactPoint = validateCardOrderRetriever.headerRetriever.getContactPoint(requestHeader.getLloydsHeaders());
        serviceRequest = validateCardOrderRetriever.headerRetriever.getServiceRequest(requestHeader.getLloydsHeaders(), "http://www.lloydstsb.com/Schema/Personal/CreditCardPlatform/CMAS/C808_EnqInitNewCrdOrd", "C812");
        securityHeaderType = validateCardOrderRetriever.headerRetriever.getSecurityHeader(requestHeader.getLloydsHeaders());

    }

    @Test
    public void testValidateCardOrderRetriever() throws ActivateProductArrangementResourceNotAvailableErrorMsg, ActivateProductArrangementExternalSystemErrorMsg {
        C812Req c812Req = testDataHelper.createC812Request(testDataHelper.createCardOrderNew(), testDataHelper.createCardOrderCBSData(), testDataHelper.createCardOrderCBSAddress());
        C812Resp c812Resp = testDataHelper.createC812Response();
        when(validateCardOrderRetriever.c812Client.validateCardOrder(c812Req, contactPoint, serviceRequest, securityHeaderType)).thenReturn(c812Resp);
        C812Resp c812Resp1 = validateCardOrderRetriever.getResponse(c812Req, requestHeader);
        assertEquals("779129", c812Resp1.getCardOrderNewValid().getCardNewDelivery().getCustomerCollectSortCd());

    }

    @Test(expected = ActivateProductArrangementResourceNotAvailableErrorMsg.class)
    public void testValidateCardOrderRetrieverWebServiceException() throws ActivateProductArrangementResourceNotAvailableErrorMsg, ActivateProductArrangementExternalSystemErrorMsg {
        C812Req c812Req = testDataHelper.createC812Request(testDataHelper.createCardOrderNew(), testDataHelper.createCardOrderCBSData(), testDataHelper.createCardOrderCBSAddress());
        when(validateCardOrderRetriever.c812Client.validateCardOrder(c812Req, contactPoint, serviceRequest, securityHeaderType)).thenThrow(new WebServiceException());
        when(validateCardOrderRetriever.exceptionUtilityActivate.resourceNotAvailableError(requestHeader, null)).thenThrow(ActivateProductArrangementResourceNotAvailableErrorMsg.class);
        validateCardOrderRetriever.getResponse(c812Req, requestHeader);

    }

    @Test(expected = ActivateProductArrangementExternalSystemErrorMsg.class)
    public void testValidateCardOrderRetrieverExternalServiceException() throws ActivateProductArrangementResourceNotAvailableErrorMsg, ActivateProductArrangementExternalSystemErrorMsg {
        C812Req c812Req = testDataHelper.createC812Request(testDataHelper.createCardOrderNew(), testDataHelper.createCardOrderCBSData(), testDataHelper.createCardOrderCBSAddress());
        C812Resp c812Resp = testDataHelper.createC812Response();
        c812Resp.getC812Result().getResultCondition().setReasonCode(218012);
        when(validateCardOrderRetriever.c812Client.validateCardOrder(c812Req, contactPoint, serviceRequest, securityHeaderType)).thenReturn(c812Resp);
        when(validateCardOrderRetriever.exceptionUtilityActivate.externalServiceError(requestHeader, null, "218012")).thenThrow(ActivateProductArrangementExternalSystemErrorMsg.class);
        validateCardOrderRetriever.getResponse(c812Req, requestHeader);

    }

    @Test
    public void testValidateCardOrderRetrieverForNull() throws ActivateProductArrangementResourceNotAvailableErrorMsg, ActivateProductArrangementExternalSystemErrorMsg {
        C812Req c812Req = testDataHelper.createC812Request(testDataHelper.createCardOrderNew(), testDataHelper.createCardOrderCBSData(), testDataHelper.createCardOrderCBSAddress());
        C812Resp c812Resp = testDataHelper.createC812Response();
        c812Resp.setC812Result(null);
        when(validateCardOrderRetriever.c812Client.validateCardOrder(c812Req, contactPoint, serviceRequest, securityHeaderType)).thenReturn(c812Resp);
        C812Resp c812Resp1 = validateCardOrderRetriever.getResponse(c812Req, requestHeader);
        assertEquals("779129", c812Resp1.getCardOrderNewValid().getCardNewDelivery().getCustomerCollectSortCd());
    }

    @Test
    public void testValidateCardOrderRetrieverWhenResultConditionNull() throws ActivateProductArrangementResourceNotAvailableErrorMsg, ActivateProductArrangementExternalSystemErrorMsg {
        C812Req c812Req = testDataHelper.createC812Request(testDataHelper.createCardOrderNew(), testDataHelper.createCardOrderCBSData(), testDataHelper.createCardOrderCBSAddress());
        C812Resp c812Resp = testDataHelper.createC812Response();
        c812Resp.getC812Result().setResultCondition(null);
        when(validateCardOrderRetriever.c812Client.validateCardOrder(c812Req, contactPoint, serviceRequest, securityHeaderType)).thenReturn(c812Resp);
        C812Resp c812Resp1 = validateCardOrderRetriever.getResponse(c812Req, requestHeader);
        assertEquals("779129", c812Resp1.getCardOrderNewValid().getCardNewDelivery().getCustomerCollectSortCd());
    }

    @Test
    public void testValidateCardOrderRetrieverWhenReasonCodeNull() throws ActivateProductArrangementResourceNotAvailableErrorMsg, ActivateProductArrangementExternalSystemErrorMsg {
        C812Req c812Req = testDataHelper.createC812Request(testDataHelper.createCardOrderNew(), testDataHelper.createCardOrderCBSData(), testDataHelper.createCardOrderCBSAddress());
        C812Resp c812Resp = testDataHelper.createC812Response();
        c812Resp.getC812Result().getResultCondition().setReasonCode(null);
        when(validateCardOrderRetriever.c812Client.validateCardOrder(c812Req, contactPoint, serviceRequest, securityHeaderType)).thenReturn(c812Resp);
        C812Resp c812Resp1 = validateCardOrderRetriever.getResponse(c812Req, requestHeader);
        assertEquals("779129", c812Resp1.getCardOrderNewValid().getCardNewDelivery().getCustomerCollectSortCd());
    }
}

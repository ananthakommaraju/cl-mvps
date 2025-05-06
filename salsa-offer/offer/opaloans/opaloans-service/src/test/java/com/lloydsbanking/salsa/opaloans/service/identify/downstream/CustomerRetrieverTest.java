package com.lloydsbanking.salsa.opaloans.service.identify.downstream;


import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.downstream.ocis.client.c216.C216Client;
import com.lloydsbanking.salsa.header.gmo.HeaderRetriever;
import com.lloydsbanking.salsa.offer.createinvolvedparty.errorcode.RetrieveOcisErrorMap;
import com.lloydsbanking.salsa.offer.exception.ExceptionUtility;
import com.lloydsbanking.salsa.opaloans.service.TestDataHelper;
import com.lloydsbanking.salsa.soap.ocis.c216.objects.C216Req;
import com.lloydsbanking.salsa.soap.ocis.c216.objects.C216Resp;
import com.lloydsbanking.salsa.soap.ocis.c216.objects.C216Result;
import com.lloydstsb.schema.infrastructure.soap.ContactPoint;
import com.lloydstsb.schema.infrastructure.soap.ResultCondition;
import com.lloydstsb.schema.infrastructure.soap.ServiceRequest;
import lib_sim_bo.businessobjects.Customer;
import lib_sim_gmo.exception.*;
import lib_sim_gmo.messages.RequestHeader;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.oasis_open.docs.wss._2004._01.oasis_200401_wss_wssecurity_secext_1_0.SecurityHeaderType;

import javax.xml.ws.WebServiceException;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Category(UnitTest.class)
public class CustomerRetrieverTest {
    CustomerRetriever customerRetriever;
    C216Req c216Req;
    C216Resp c216Resp;
    TestDataHelper dataHelper;
    RequestHeader requestHeader;

    @Before
    public void setUp() {
        customerRetriever = new CustomerRetriever();
        customerRetriever.c216Client = mock(C216Client.class);
        customerRetriever.headerRetriever = mock(HeaderRetriever.class);
        customerRetriever.exceptionUtility = mock(ExceptionUtility.class);
        customerRetriever.ocisErrorMap = mock(RetrieveOcisErrorMap.class);
        requestHeader = new RequestHeader();
        dataHelper = new TestDataHelper();

    }

    @Test
    public void testRetrieveCustomer() throws InternalServiceErrorMsg, ResourceNotAvailableErrorMsg, DataNotAvailableErrorMsg, ExternalServiceErrorMsg, ExternalBusinessErrorMsg {
        c216Req = dataHelper.createC216Request("123", "567");
        c216Resp = dataHelper.createC216Response(456662112l);
        when(customerRetriever.c216Client.c216(any(C216Req.class), any(ContactPoint.class), any(ServiceRequest.class), any(SecurityHeaderType.class))).thenReturn(c216Resp);
        List<Customer> customerList = customerRetriever.retrieveCustomer(requestHeader, "123", "567");

        assertEquals("Ariyana", customerList.get(0).getIsPlayedBy().getIndividualName().get(0).getFirstName());
        assertEquals("Lockheart", customerList.get(0).getIsPlayedBy().getIndividualName().get(0).getLastName());
        assertEquals("Mr", customerList.get(0).getIsPlayedBy().getIndividualName().get(0).getPrefixTitle());
    }

    @Test(expected = ExternalServiceErrorMsg.class)
    public void testRetrieveCustomerThrowsError() throws InternalServiceErrorMsg, ResourceNotAvailableErrorMsg, DataNotAvailableErrorMsg, ExternalServiceErrorMsg, ExternalBusinessErrorMsg {
        c216Req = dataHelper.createC216Request("123", "567");
        c216Resp = dataHelper.createC216Response(456662112l);
        c216Resp.setC216Result(new C216Result());
        c216Resp.getC216Result().setResultCondition(new ResultCondition());
        c216Resp.getC216Result().getResultCondition().setReasonCode(160999);
        when(customerRetriever.c216Client.c216(any(C216Req.class), any(ContactPoint.class), any(ServiceRequest.class), any(SecurityHeaderType.class))).thenReturn(c216Resp);
        when(customerRetriever.exceptionUtility.externalServiceError(anyString(), anyString())).thenThrow(ExternalServiceErrorMsg.class);
        List<Customer> customerList = customerRetriever.retrieveCustomer(requestHeader, "123", "567");

    }

    @Test(expected = NullPointerException.class)
    public void testRetrieveCustomerthrowsWebServiceException() throws InternalServiceErrorMsg, ResourceNotAvailableErrorMsg, DataNotAvailableErrorMsg, ExternalServiceErrorMsg, ExternalBusinessErrorMsg {
        c216Req = dataHelper.createC216Request("123", "567");
        c216Resp = dataHelper.createC216Response(456662112l);
        when(customerRetriever.c216Client.c216(any(C216Req.class), any(ContactPoint.class), any(ServiceRequest.class), any(SecurityHeaderType.class))).thenThrow(WebServiceException.class);
        customerRetriever.retrieveCustomer(requestHeader, "123", "567");

    }

    @Test(expected = ResourceNotAvailableErrorMsg.class)
    public void testResourceNotAvailable() throws InternalServiceErrorMsg, ResourceNotAvailableErrorMsg, DataNotAvailableErrorMsg, ExternalServiceErrorMsg, ExternalBusinessErrorMsg {
        when(customerRetriever.c216Client.c216(any(C216Req.class), any(ContactPoint.class), any(ServiceRequest.class), any(SecurityHeaderType.class))).thenThrow(WebServiceException.class);
        when(customerRetriever.exceptionUtility.resourceNotAvailableError(any(String.class))).thenThrow(ResourceNotAvailableErrorMsg.class);
        customerRetriever.retrieveCustomer(requestHeader, "123", "567");

    }

    @Test(expected = ExternalServiceErrorMsg.class)
    public void testExternalServiceError() throws InternalServiceErrorMsg, ResourceNotAvailableErrorMsg, DataNotAvailableErrorMsg, ExternalServiceErrorMsg, ExternalBusinessErrorMsg {
        customerRetriever.exceptionUtility = new ExceptionUtility();
        c216Req = dataHelper.createC216Request("123", "567");
        c216Resp = dataHelper.createC216Response(456662112l);
        c216Resp.setC216Result(new C216Result());
        c216Resp.getC216Result().setResultCondition(new ResultCondition());
        c216Resp.getC216Result().getResultCondition().setReasonCode(160999);
        c216Resp.getC216Result().getResultCondition().setReasonText("Severe Error Occured");
        when(customerRetriever.c216Client.c216(any(C216Req.class), any(ContactPoint.class), any(ServiceRequest.class), any(SecurityHeaderType.class))).thenReturn(c216Resp);
        customerRetriever.retrieveCustomer(requestHeader, "123", "567");
    }

    @Test(expected = ExternalBusinessErrorMsg.class)
    public void testExternalBusinessError() throws InternalServiceErrorMsg, ResourceNotAvailableErrorMsg, DataNotAvailableErrorMsg, ExternalServiceErrorMsg, ExternalBusinessErrorMsg {
        customerRetriever.exceptionUtility = new ExceptionUtility();
        c216Req = dataHelper.createC216Request("123", "567");
        c216Resp = dataHelper.createC216Response(456662112l);
        c216Resp.setC216Result(new C216Result());
        c216Resp.getC216Result().setResultCondition(new ResultCondition());
        c216Resp.getC216Result().getResultCondition().setReasonCode(163004);
        c216Resp.getC216Result().getResultCondition().setReasonText("External Error Occured");
        when(customerRetriever.c216Client.c216(any(C216Req.class), any(ContactPoint.class), any(ServiceRequest.class), any(SecurityHeaderType.class))).thenReturn(c216Resp);
        customerRetriever.retrieveCustomer(requestHeader, "123", "567");
    }

    @Test
    public void testRetrieveCustomerSpaceBirthDate() throws InternalServiceErrorMsg, ResourceNotAvailableErrorMsg, DataNotAvailableErrorMsg, ExternalServiceErrorMsg, ExternalBusinessErrorMsg {
        c216Req = dataHelper.createC216Request("123", "567");
        c216Resp = dataHelper.createC216Response(456662112l);
        c216Resp.getPartyProdData().get(0).setBirthDt("  ");
        when(customerRetriever.c216Client.c216(any(C216Req.class), any(ContactPoint.class), any(ServiceRequest.class), any(SecurityHeaderType.class))).thenReturn(c216Resp);
        List<Customer> customerList = customerRetriever.retrieveCustomer(requestHeader, "123", "567");

        assertNull(customerList.get(0).getIsPlayedBy().getBirthDate());

    }


}

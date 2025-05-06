package com.lloydsbanking.salsa.ppae.service.downstream;


import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.downstream.pad.client.f263.F263Client;
import com.lloydsbanking.salsa.header.gmo.HeaderRetriever;
import com.lloydsbanking.salsa.ppae.TestDataHelper;
import com.lloydsbanking.salsa.ppae.service.convert.F263RequestFactory;
import com.lloydsbanking.salsa.soap.pad.f263.objects.F263Req;
import com.lloydsbanking.salsa.soap.pad.f263.objects.F263Resp;
import com.lloydsbanking.salsa.soap.pad.f263.objects.F263Result;
import com.lloydstsb.schema.infrastructure.soap.ContactPoint;
import com.lloydstsb.schema.infrastructure.soap.ResultCondition;
import com.lloydstsb.schema.infrastructure.soap.ServiceRequest;
import lib_sim_bo.businessobjects.Customer;
import lib_sim_bo.businessobjects.ProductArrangement;
import lib_sim_salesprocessmanagement.messages.ProcessPendingArrangementEventRequest;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.oasis_open.docs.wss._2004._01.oasis_200401_wss_wssecurity_secext_1_0.SecurityHeaderType;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.ws.WebServiceException;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
@Category(UnitTest.class)
public class F263LoanDetailsRetrieverTest {

    F263LoanDetailsRetriever f263LoanDetailsRetriever;
    ProductArrangement productArrangement;
    TestDataHelper testDataHelper;
    F263Req f263Req;
    F263Resp f263Resp;
    ProcessPendingArrangementEventRequest request;

    @Before
    public void setUp() throws DatatypeConfigurationException {
        f263LoanDetailsRetriever = new F263LoanDetailsRetriever();
        testDataHelper = new TestDataHelper();
        f263Req = new F263Req();
        productArrangement = testDataHelper.createProductArrangement();
        f263LoanDetailsRetriever.f263Client = mock(F263Client.class);
        f263LoanDetailsRetriever.f263RequestFactory = mock(F263RequestFactory.class);
        f263LoanDetailsRetriever.headerRetriever = new HeaderRetriever();
        f263LoanDetailsRetriever.f263RequestFactory = mock(F263RequestFactory.class);
        f263Resp=testDataHelper.createF263Resp();
        request = testDataHelper.createPpaeRequest("1", "LTB");
    }

    @Test
    public void testInvokeF263() {
        when(f263LoanDetailsRetriever.f263RequestFactory.createF263Req(any(Customer.class))).thenReturn(new F263Req());
        when(f263LoanDetailsRetriever.f263Client.enquireLoanApplication(any(F263Req.class),any(ContactPoint.class),any(ServiceRequest.class),any(SecurityHeaderType.class))).thenReturn(f263Resp);
        f263LoanDetailsRetriever.invokeF263(request, productArrangement);
        verify(f263LoanDetailsRetriever.f263RequestFactory).createF263Req(productArrangement.getPrimaryInvolvedParty());
        verify(f263LoanDetailsRetriever.f263Client).enquireLoanApplication(any(F263Req.class),any(ContactPoint.class),any(ServiceRequest.class),any(SecurityHeaderType.class));
    }

    @Test
    public void testInvokeF263ForLoanAppStatusSeven() {
        f263Resp.getApplicationDetails().setLoanApplnStatusCd(7);
        when(f263LoanDetailsRetriever.f263RequestFactory.createF263Req(any(Customer.class))).thenReturn(new F263Req());
        when(f263LoanDetailsRetriever.f263Client.enquireLoanApplication(any(F263Req.class),any(ContactPoint.class),any(ServiceRequest.class),any(SecurityHeaderType.class))).thenReturn(f263Resp);
        f263LoanDetailsRetriever.invokeF263(request, productArrangement);
        verify(f263LoanDetailsRetriever.f263RequestFactory).createF263Req(productArrangement.getPrimaryInvolvedParty());
        verify(f263LoanDetailsRetriever.f263Client).enquireLoanApplication(any(F263Req.class), any(ContactPoint.class), any(ServiceRequest.class), any(SecurityHeaderType.class));

    }

    @Test
    public void testInvokeF263ForNullccaVaildDate() {
        f263Resp.getApplicationDetails().setLastCCAValidDt(null);
        when(f263LoanDetailsRetriever.f263RequestFactory.createF263Req(any(Customer.class))).thenReturn(new F263Req());
        when(f263LoanDetailsRetriever.f263Client.enquireLoanApplication(any(F263Req.class),any(ContactPoint.class),any(ServiceRequest.class),any(SecurityHeaderType.class))).thenReturn(f263Resp);
        f263LoanDetailsRetriever.invokeF263(request, productArrangement);
        verify(f263LoanDetailsRetriever.f263RequestFactory).createF263Req(productArrangement.getPrimaryInvolvedParty());
        verify(f263LoanDetailsRetriever.f263Client).enquireLoanApplication(any(F263Req.class), any(ContactPoint.class), any(ServiceRequest.class), any(SecurityHeaderType.class));

    }

    @Test
    public void testInvokeF263ForNullQteVaildDate() {
        f263Resp.getApplicationDetails().setLastQteValidDt(null);
        when(f263LoanDetailsRetriever.f263RequestFactory.createF263Req(any(Customer.class))).thenReturn(new F263Req());
        when(f263LoanDetailsRetriever.f263Client.enquireLoanApplication(any(F263Req.class),any(ContactPoint.class),any(ServiceRequest.class),any(SecurityHeaderType.class))).thenReturn(f263Resp);
        f263LoanDetailsRetriever.invokeF263(request, productArrangement);
        verify(f263LoanDetailsRetriever.f263RequestFactory).createF263Req(productArrangement.getPrimaryInvolvedParty());
        verify(f263LoanDetailsRetriever.f263Client).enquireLoanApplication(any(F263Req.class), any(ContactPoint.class), any(ServiceRequest.class), any(SecurityHeaderType.class));

    }

    @Test
    public void testInvokeF263ForNullF263Resp() {
        when(f263LoanDetailsRetriever.f263RequestFactory.createF263Req(any(Customer.class))).thenReturn(new F263Req());
        when(f263LoanDetailsRetriever.f263Client.enquireLoanApplication(any(F263Req.class),any(ContactPoint.class),any(ServiceRequest.class),any(SecurityHeaderType.class))).thenReturn(null);
        f263LoanDetailsRetriever.invokeF263(request, productArrangement);
        verify(f263LoanDetailsRetriever.f263RequestFactory).createF263Req(productArrangement.getPrimaryInvolvedParty());
        verify(f263LoanDetailsRetriever.f263Client).enquireLoanApplication(any(F263Req.class),any(ContactPoint.class),any(ServiceRequest.class),any(SecurityHeaderType.class));

    }

    @Test
    public void testInvokeF263ForReasonCodePresent() {
        f263Resp.setF263Result(new F263Result());
        f263Resp.getF263Result().setResultCondition(new ResultCondition());
        f263Resp.getF263Result().getResultCondition().setReasonCode(12546);
        when(f263LoanDetailsRetriever.f263RequestFactory.createF263Req(any(Customer.class))).thenReturn(new F263Req());
        when(f263LoanDetailsRetriever.f263Client.enquireLoanApplication(any(F263Req.class),any(ContactPoint.class),any(ServiceRequest.class),any(SecurityHeaderType.class))).thenReturn(f263Resp);
        f263LoanDetailsRetriever.invokeF263(request, productArrangement);
        verify(f263LoanDetailsRetriever.f263RequestFactory).createF263Req(productArrangement.getPrimaryInvolvedParty());
        verify(f263LoanDetailsRetriever.f263Client).enquireLoanApplication(any(F263Req.class),any(ContactPoint.class),any(ServiceRequest.class),any(SecurityHeaderType.class));

    }

    @Test
    public void testInvokeF263ForNullF263Result() {
        f263Resp.setF263Result(null);
        when(f263LoanDetailsRetriever.f263RequestFactory.createF263Req(any(Customer.class))).thenReturn(new F263Req());
        when(f263LoanDetailsRetriever.f263Client.enquireLoanApplication(any(F263Req.class),any(ContactPoint.class),any(ServiceRequest.class),any(SecurityHeaderType.class))).thenReturn(f263Resp);
        f263LoanDetailsRetriever.invokeF263(request, productArrangement);
        verify(f263LoanDetailsRetriever.f263RequestFactory).createF263Req(productArrangement.getPrimaryInvolvedParty());
        verify(f263LoanDetailsRetriever.f263Client).enquireLoanApplication(any(F263Req.class),any(ContactPoint.class),any(ServiceRequest.class),any(SecurityHeaderType.class));

    }

    @Test
    public void testInvokeF263ForNullResultConditionPresent() {
        f263Resp.setF263Result(new F263Result());
        f263Resp.getF263Result().setResultCondition(null);
        when(f263LoanDetailsRetriever.f263RequestFactory.createF263Req(any(Customer.class))).thenReturn(new F263Req());
        when(f263LoanDetailsRetriever.f263Client.enquireLoanApplication(any(F263Req.class),any(ContactPoint.class),any(ServiceRequest.class),any(SecurityHeaderType.class))).thenReturn(f263Resp);
        f263LoanDetailsRetriever.invokeF263(request, productArrangement);
        verify(f263LoanDetailsRetriever.f263RequestFactory).createF263Req(productArrangement.getPrimaryInvolvedParty());
        verify(f263LoanDetailsRetriever.f263Client).enquireLoanApplication(any(F263Req.class),any(ContactPoint.class),any(ServiceRequest.class),any(SecurityHeaderType.class));

    }
    @Test
    public void testInvokeF263ForNullReasonCode() {
        f263Resp.setF263Result(new F263Result());
        f263Resp.getF263Result().setResultCondition(new ResultCondition());
        f263Resp.getF263Result().getResultCondition().setReasonCode(null);
        when(f263LoanDetailsRetriever.f263RequestFactory.createF263Req(any(Customer.class))).thenReturn(new F263Req());
        when(f263LoanDetailsRetriever.f263Client.enquireLoanApplication(any(F263Req.class),any(ContactPoint.class),any(ServiceRequest.class),any(SecurityHeaderType.class))).thenReturn(f263Resp);
        f263LoanDetailsRetriever.invokeF263(request, productArrangement);
        verify(f263LoanDetailsRetriever.f263RequestFactory).createF263Req(productArrangement.getPrimaryInvolvedParty());
        verify(f263LoanDetailsRetriever.f263Client).enquireLoanApplication(any(F263Req.class),any(ContactPoint.class),any(ServiceRequest.class),any(SecurityHeaderType.class));
    }

    @Test
    public void testInvokeF263ThrowsWebServiceException() {
        when(f263LoanDetailsRetriever.f263RequestFactory.createF263Req(any(Customer.class))).thenReturn(new F263Req());
        when(f263LoanDetailsRetriever.f263Client.enquireLoanApplication(any(F263Req.class),any(ContactPoint.class),any(ServiceRequest.class),any(SecurityHeaderType.class))).thenThrow(new WebServiceException());
        f263LoanDetailsRetriever.invokeF263(request, productArrangement);
        verify(f263LoanDetailsRetriever.f263RequestFactory).createF263Req(productArrangement.getPrimaryInvolvedParty());
        verify(f263LoanDetailsRetriever.f263Client).enquireLoanApplication(any(F263Req.class),any(ContactPoint.class),any(ServiceRequest.class),any(SecurityHeaderType.class));
    }
}

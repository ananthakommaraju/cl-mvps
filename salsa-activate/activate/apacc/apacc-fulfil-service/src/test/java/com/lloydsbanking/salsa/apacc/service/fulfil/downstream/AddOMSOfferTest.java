package com.lloydsbanking.salsa.apacc.service.fulfil.downstream;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.activate.helper.ApplicationDetails;
import com.lloydsbanking.salsa.activate.helper.UpdateDepositArrangementConditionAndApplicationStatusHelper;
import com.lloydsbanking.salsa.activate.utility.ExceptionUtilityActivate;
import com.lloydsbanking.salsa.apacc.TestDataHelper;
import com.lloydsbanking.salsa.apacc.service.fulfil.convert.F251RequestFactory;
import com.lloydsbanking.salsa.downstream.fdi.client.f251.F251Client;
import com.lloydsbanking.salsa.header.gmo.HeaderRetriever;
import com.lloydsbanking.salsa.logging.application.ProductArrangementTraceLog;
import com.lloydsbanking.salsa.soap.fdi.f251.objects.F251Req;
import com.lloydsbanking.salsa.soap.fdi.f251.objects.F251Resp;
import com.lloydsbanking.salsa.soap.fdi.f251.objects.F251Result;
import com.lloydstsb.schema.infrastructure.soap.ContactPoint;
import com.lloydstsb.schema.infrastructure.soap.ResultCondition;
import com.lloydstsb.schema.infrastructure.soap.ServiceRequest;
import lib_sim_bo.businessobjects.FinanceServiceArrangement;
import lib_sim_bo.businessobjects.ProductArrangement;
import lib_sim_gmo.messages.RequestHeader;
import lib_sim_gmo.messages.SOAPHeader;
import lib_sim_salesprocessmanagement.ia_activateproductarrangement.ActivateProductArrangementExternalSystemErrorMsg;
import lib_sim_salesprocessmanagement.ia_activateproductarrangement.ActivateProductArrangementResourceNotAvailableErrorMsg;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.oasis_open.docs.wss._2004._01.oasis_200401_wss_wssecurity_secext_1_0.SecurityHeaderType;

import javax.xml.ws.WebServiceException;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@Category(UnitTest.class)
public class AddOMSOfferTest {
    TestDataHelper testDataHelper;
    AddOMSOffer addOMSOffer;
    F251Req f251Req;
    RequestHeader requestHeader;
    F251Resp f251Resp;

    @Before
    public void setUp() {
        requestHeader = new RequestHeader();
        addOMSOffer = new AddOMSOffer();
        addOMSOffer.f251RequestFactory = mock(F251RequestFactory.class);
        addOMSOffer.f251Client = mock(F251Client.class);
        addOMSOffer.headerRetriever = new HeaderRetriever();
        addOMSOffer.exceptionUtilityActivate = mock(ExceptionUtilityActivate.class);
        addOMSOffer.applicationStatusHelper = new UpdateDepositArrangementConditionAndApplicationStatusHelper();
        testDataHelper = new TestDataHelper();
        f251Req = new F251Req();
        SOAPHeader soapHeader = new SOAPHeader();
        requestHeader.getLloydsHeaders().add(soapHeader);
        requestHeader.getLloydsHeaders().get(0).setName("name");
        requestHeader = testDataHelper.createApaRequestHeader();
        f251Resp = new F251Resp();
        addOMSOffer.productArrangementTraceLog = mock(ProductArrangementTraceLog.class);
        when(addOMSOffer.productArrangementTraceLog.getApplicationTraceEventMessage(any(ProductArrangement.class), any(String.class))).thenReturn("ProdArrngemnt");

    }

    @Test
    public void testAddOMSDetails() throws ActivateProductArrangementResourceNotAvailableErrorMsg, ActivateProductArrangementExternalSystemErrorMsg {
        F251Result f251Result = new F251Result();
        ResultCondition resultCondition = new ResultCondition();
        resultCondition.setReasonCode(41159);
        f251Result.setResultCondition(resultCondition);
        f251Resp.setF251Result(f251Result);
        when(addOMSOffer.f251RequestFactory.convert(any(FinanceServiceArrangement.class), any(String.class))).thenReturn(new F251Req());
        when(addOMSOffer.f251Client.addOMSOffer(any(F251Req.class), any(ContactPoint.class), any(ServiceRequest.class), any(SecurityHeaderType.class))).thenReturn(f251Resp);
        when(addOMSOffer.exceptionUtilityActivate.externalServiceError(any(RequestHeader.class), any(String.class), any(String.class))).thenThrow(ActivateProductArrangementExternalSystemErrorMsg.class);
        addOMSOffer.addOMSOffers(new FinanceServiceArrangement(), requestHeader, new ApplicationDetails());
        verify(addOMSOffer.f251Client).addOMSOffer(any(F251Req.class), any(ContactPoint.class), any(ServiceRequest.class), any(SecurityHeaderType.class));
    }

    @Test
    public void testAddOMSDetailsWithException() throws ActivateProductArrangementResourceNotAvailableErrorMsg, ActivateProductArrangementExternalSystemErrorMsg {
        when(addOMSOffer.f251RequestFactory.convert(any(FinanceServiceArrangement.class), any(String.class))).thenReturn(new F251Req());
        when(addOMSOffer.f251Client.addOMSOffer(any(F251Req.class), any(ContactPoint.class), any(ServiceRequest.class), any(SecurityHeaderType.class))).thenThrow(new WebServiceException());
        when(addOMSOffer.exceptionUtilityActivate.resourceNotAvailableError(any(RequestHeader.class), any(String.class))).thenThrow(ActivateProductArrangementResourceNotAvailableErrorMsg.class);
        addOMSOffer.addOMSOffers(new FinanceServiceArrangement(), requestHeader, new ApplicationDetails());
        verify(addOMSOffer.f251Client).addOMSOffer(any(F251Req.class), any(ContactPoint.class), any(ServiceRequest.class), any(SecurityHeaderType.class));

    }

    @Test
    public void testAddOMSDetailsWhenReasonCodeIsNull(){
        F251Result f251Result = new F251Result();
        ResultCondition resultCondition = new ResultCondition();
        f251Result.setResultCondition(resultCondition);
        f251Resp.setF251Result(f251Result);
        when(addOMSOffer.f251RequestFactory.convert(any(FinanceServiceArrangement.class), any(String.class))).thenReturn(new F251Req());
        when(addOMSOffer.f251Client.addOMSOffer(any(F251Req.class), any(ContactPoint.class), any(ServiceRequest.class), any(SecurityHeaderType.class))).thenReturn(f251Resp);
        addOMSOffer.addOMSOffers(new FinanceServiceArrangement(), requestHeader, new ApplicationDetails());
        verify(addOMSOffer.f251Client).addOMSOffer(any(F251Req.class), any(ContactPoint.class), any(ServiceRequest.class), any(SecurityHeaderType.class));
    }
}

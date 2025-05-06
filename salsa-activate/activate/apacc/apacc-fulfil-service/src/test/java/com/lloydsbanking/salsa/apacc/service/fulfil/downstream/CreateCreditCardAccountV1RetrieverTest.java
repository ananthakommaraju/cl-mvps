package com.lloydsbanking.salsa.apacc.service.fulfil.downstream;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.activate.constants.ActivateCommonConstant;
import com.lloydsbanking.salsa.activate.helper.ApplicationDetails;
import com.lloydsbanking.salsa.activate.helper.UpdateDepositArrangementConditionAndApplicationStatusHelper;
import com.lloydsbanking.salsa.apacc.TestDataHelper;
import com.lloydsbanking.salsa.apacc.service.fulfil.convert.F241V1RequestFactory;
import com.lloydsbanking.salsa.constant.ApplicationStatus;
import com.lloydsbanking.salsa.downstream.fdi.client.f241v1.F241V1Client;
import com.lloydsbanking.salsa.header.gmo.HeaderRetriever;
import com.lloydsbanking.salsa.soap.fdi.f241V1.objects.F241Req;
import com.lloydsbanking.salsa.soap.fdi.f241V1.objects.F241Resp;
import com.lloydsbanking.salsa.soap.fdi.f241V1.objects.F241Result;
import com.lloydstsb.schema.infrastructure.soap.ContactPoint;
import com.lloydstsb.schema.infrastructure.soap.ResultCondition;
import com.lloydstsb.schema.infrastructure.soap.ServiceRequest;
import lib_sim_bo.businessobjects.FinanceServiceArrangement;
import lib_sim_gmo.messages.RequestHeader;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.oasis_open.docs.wss._2004._01.oasis_200401_wss_wssecurity_secext_1_0.SecurityHeaderType;

import javax.xml.ws.WebServiceException;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@Category(UnitTest.class)
public class CreateCreditCardAccountV1RetrieverTest {
    private static final String F241_V1_SERVICE_NAME = "http://www.lloydstsb.com/Schema/Personal/CreditCardPlatform/FDI";
    private static final String F241_V1_ACTION_NAME = "F241V1.1";
    CreateCreditCardAccountV1Retriever retriever;
    TestDataHelper testDataHelper;
    RequestHeader requestHeader;
    FinanceServiceArrangement financeServiceArrangement;
    HeaderRetriever headerRetriever;

    @Before
    public void setUp() {
        retriever = new CreateCreditCardAccountV1Retriever();
        testDataHelper = new TestDataHelper();
        requestHeader = testDataHelper.createApaRequestHeader();
        financeServiceArrangement = testDataHelper.createFinanceServiceArrangement();
        retriever.f241V1Client = mock(F241V1Client.class);
        retriever.f241RequestFactory = mock(F241V1RequestFactory.class);
        retriever.headerRetriever = new HeaderRetriever();
        headerRetriever = new HeaderRetriever();
        retriever.applicationStatusHelper = new UpdateDepositArrangementConditionAndApplicationStatusHelper();
    }

    @Test
    public void testCreateCreditCardAccount() {
        F241Req f241Req = new F241Req();
        ContactPoint contactPoint = headerRetriever.getContactPoint(requestHeader);
        ServiceRequest serviceRequest = headerRetriever.getServiceRequest(requestHeader.getLloydsHeaders(), F241_V1_SERVICE_NAME, F241_V1_ACTION_NAME);
        SecurityHeaderType securityHeaderType = headerRetriever.getSecurityHeader(requestHeader);
        when(retriever.f241RequestFactory.convert(financeServiceArrangement)).thenReturn(f241Req);
        when(retriever.f241V1Client.createCardAccountV1(any(F241Req.class), any(ContactPoint.class), any(ServiceRequest.class), any(SecurityHeaderType.class))).thenReturn(new F241Resp());
        ApplicationDetails applicationDetails = new ApplicationDetails();
        retriever.createCreditCardAccount(financeServiceArrangement, requestHeader, applicationDetails);
        verify(retriever.f241V1Client).createCardAccountV1(f241Req, contactPoint, serviceRequest, securityHeaderType);
    }

    @Test
    public void testCreateCreditCardAccountWithErrorReponse() {
        F241Resp f241Resp1 = new F241Resp();
        ResultCondition resultCondition = new ResultCondition();
        resultCondition.setSeverityCode((byte) 21);
        F241Result f241Result = new F241Result();
        f241Result.setResultCondition(resultCondition);
        f241Resp1.setF241Result(f241Result);
        when(retriever.f241RequestFactory.convert(financeServiceArrangement)).thenReturn(new F241Req());
        when(retriever.f241V1Client.createCardAccountV1(any(F241Req.class), any(ContactPoint.class), any(ServiceRequest.class), any(SecurityHeaderType.class))).thenReturn(f241Resp1);
        ApplicationDetails applicationDetails = new ApplicationDetails();
        retriever.createCreditCardAccount(financeServiceArrangement, requestHeader, applicationDetails);
        assertEquals(ApplicationStatus.AWAITING_FULFILMENT.getValue(), applicationDetails.getApplicationStatus());
        assertEquals(ActivateCommonConstant.AppSubStatus.CARD_CREATION_FAILURE, applicationDetails.getApplicationSubStatus());
    }

    @Test
    public void testCreateCreditCardAccountWithException() {
        when(retriever.f241RequestFactory.convert(financeServiceArrangement)).thenReturn(new F241Req());
        when(retriever.f241V1Client.createCardAccountV1(any(F241Req.class), any(ContactPoint.class), any(ServiceRequest.class), any(SecurityHeaderType.class))).thenThrow(WebServiceException.class);
        ApplicationDetails applicationDetails = new ApplicationDetails();
        retriever.createCreditCardAccount(financeServiceArrangement, requestHeader, applicationDetails);
        assertEquals(ApplicationStatus.AWAITING_FULFILMENT.getValue(), applicationDetails.getApplicationStatus());
        assertEquals(ActivateCommonConstant.AppSubStatus.CARD_CREATION_FAILURE, applicationDetails.getApplicationSubStatus());
    }

    @Test
    public void testCreateCreditCardAccountWithSeverityCode() {
        F241Resp f241Resp1 = new F241Resp();
        ResultCondition resultCondition = new ResultCondition();
        resultCondition.setSeverityCode((byte) 2);
        F241Result f241Result = new F241Result();
        f241Result.setResultCondition(resultCondition);
        f241Resp1.setF241Result(f241Result);
        when(retriever.f241RequestFactory.convert(financeServiceArrangement)).thenReturn(new F241Req());
        when(retriever.f241V1Client.createCardAccountV1(any(F241Req.class), any(ContactPoint.class), any(ServiceRequest.class), any(SecurityHeaderType.class))).thenReturn(f241Resp1);
        ApplicationDetails applicationDetails = new ApplicationDetails();
        retriever.createCreditCardAccount(financeServiceArrangement, requestHeader, applicationDetails);
        assertEquals(ApplicationStatus.AWAITING_FULFILMENT.getValue(), applicationDetails.getApplicationStatus());
        assertEquals(ActivateCommonConstant.AppSubStatus.CARD_CREATION_FAILURE, applicationDetails.getApplicationSubStatus());
    }
}

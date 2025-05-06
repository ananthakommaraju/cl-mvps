package com.lloydsbanking.salsa.ppae.service.downstream;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.downstream.pp.client.PaymentProcessingClient;
import com.lloydsbanking.salsa.header.gmo.HeaderRetriever;
import com.lloydsbanking.salsa.ppae.TestDataHelper;
import com.lloydsbanking.salsa.ppae.service.convert.IssueInPaymentInstRequestFactory;
import com.lloydsbanking.salsa.soap.fs.ftp.IssueInpaymentInstructionRequest;
import com.lloydsbanking.salsa.soap.fs.ftp.IssueInpaymentInstructionResponse;
import com.lloydstsb.schema.enterprise.lcsm.BapiInformation;
import com.lloydstsb.schema.enterprise.lcsm.ResponseHeader;
import com.lloydstsb.schema.enterprise.lcsm_financialtransactionprocessing.ErrorInfo;
import com.lloydstsb.schema.infrastructure.soap.*;
import lib_sim_bo.businessobjects.BalanceTransfer;
import lib_sim_bo.businessobjects.FinanceServiceArrangement;
import lib_sim_bo.businessobjects.ProductArrangement;
import lib_sim_salesprocessmanagement.messages.ProcessPendingArrangementEventRequest;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.oasis_open.docs.wss._2004._01.oasis_200401_wss_wssecurity_secext_1_0.SecurityHeaderType;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.ws.WebServiceException;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@Category(UnitTest.class)
public class IssueInPaymentInstRetrieverTest {
    IssueInPaymentInstRetriever issueInPaymentInstRetriever;
    TestDataHelper testDataHelper;
    IssueInpaymentInstructionRequest issueInpaymentInstructionRequest;
    ProductArrangement productArrangement;
    IssueInpaymentInstructionResponse response;
    ProcessPendingArrangementEventRequest request;
    FinanceServiceArrangement financeServiceArrangement;


    @Before
    public void setUp() throws DatatypeConfigurationException {
        issueInPaymentInstRetriever = new IssueInPaymentInstRetriever();
        financeServiceArrangement = new FinanceServiceArrangement();
        response = new IssueInpaymentInstructionResponse();
        testDataHelper = new TestDataHelper();
        issueInpaymentInstructionRequest = new IssueInpaymentInstructionRequest();
        productArrangement = testDataHelper.createProductArrangement();
        issueInPaymentInstRetriever.headerRetriever = new HeaderRetriever();
        issueInPaymentInstRetriever.paymentProcessingClient = mock(PaymentProcessingClient.class);
        issueInPaymentInstRetriever.issueInPaymentInstRequestFactory = mock(IssueInPaymentInstRequestFactory.class);
        request = testDataHelper.createPpaeRequest("1", "LTB");
    }

    @Test
    public void testInvoke() throws ErrorInfo {
        when(issueInPaymentInstRetriever.issueInPaymentInstRequestFactory.convert(any(String.class), any(BalanceTransfer.class), any(String.class))).thenReturn(new IssueInpaymentInstructionRequest());
        when(issueInPaymentInstRetriever.paymentProcessingClient.issueInPaymentInstruction(any(IssueInpaymentInstructionRequest.class), any(ContactPoint.class), any(ServiceRequest.class), any(SecurityHeaderType.class), any(BapiInformation.class))).thenReturn(response);
        issueInPaymentInstRetriever.invoke("abc", request.getHeader(), new BalanceTransfer(), financeServiceArrangement.getCreditCardNumber());
        verify(issueInPaymentInstRetriever.issueInPaymentInstRequestFactory).convert(any(String.class), any(BalanceTransfer.class), any(String.class));
    }

    @Test
    public void testInvokethrowsException() throws ErrorInfo {
        when(issueInPaymentInstRetriever.issueInPaymentInstRequestFactory.convert(any(String.class), any(BalanceTransfer.class), any(String.class))).thenReturn(new IssueInpaymentInstructionRequest());
        when(issueInPaymentInstRetriever.paymentProcessingClient.issueInPaymentInstruction(any(IssueInpaymentInstructionRequest.class), any(ContactPoint.class), any(ServiceRequest.class), any(SecurityHeaderType.class), any(BapiInformation.class))).thenThrow(new WebServiceException());
        issueInPaymentInstRetriever.invoke("abc", request.getHeader(), new BalanceTransfer(), financeServiceArrangement.getCreditCardNumber());
        verify(issueInPaymentInstRetriever.issueInPaymentInstRequestFactory).convert(any(String.class), any(BalanceTransfer.class), any(String.class));
    }

    @Test
    public void testInvokeForInvalidResponse() throws ErrorInfo {
        response.setResponseHeader(new ResponseHeader());
        response.getResponseHeader().setResultCondition(new ResultCondition());
        response.getResponseHeader().getResultCondition().setExtraConditions(new ExtraConditions());
        Condition condition = new Condition();
        condition.setSeverityCode((byte) 4);
        response.getResponseHeader().getResultCondition().getExtraConditions().getCondition().add(condition);
        when(issueInPaymentInstRetriever.issueInPaymentInstRequestFactory.convert(any(String.class), any(BalanceTransfer.class), any(String.class))).thenReturn(new IssueInpaymentInstructionRequest());
        when(issueInPaymentInstRetriever.paymentProcessingClient.issueInPaymentInstruction(any(IssueInpaymentInstructionRequest.class), any(ContactPoint.class), any(ServiceRequest.class), any(SecurityHeaderType.class), any(BapiInformation.class))).thenReturn(response);
        issueInPaymentInstRetriever.invoke("abc", request.getHeader(), new BalanceTransfer(), financeServiceArrangement.getCreditCardNumber());
        verify(issueInPaymentInstRetriever.issueInPaymentInstRequestFactory).convert(any(String.class), any(BalanceTransfer.class), any(String.class));
    }
}

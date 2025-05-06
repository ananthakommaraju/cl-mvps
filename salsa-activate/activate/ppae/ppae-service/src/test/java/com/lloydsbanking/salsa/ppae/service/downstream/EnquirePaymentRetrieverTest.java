package com.lloydsbanking.salsa.ppae.service.downstream;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.downstream.pp.client.PaymentProcessingClient;
import com.lloydsbanking.salsa.header.gmo.HeaderRetriever;
import com.lloydsbanking.salsa.ppae.TestDataHelper;
import com.lloydsbanking.salsa.ppae.service.convert.EnquirePaymentRequestFactory;
import com.lloydsbanking.salsa.soap.fs.ftp.EnquirePaymentInstructionFacilityDetailsRequest;
import com.lloydsbanking.salsa.soap.fs.ftp.EnquirePaymentInstructionFacilityDetailsResponse;
import com.lloydstsb.schema.enterprise.lcsm.BapiInformation;
import com.lloydstsb.schema.enterprise.lcsm.ResponseHeader;
import com.lloydstsb.schema.enterprise.lcsm_arrangement.ProductArrangement;
import com.lloydstsb.schema.enterprise.lcsm_common.RuleCondition;
import com.lloydstsb.schema.enterprise.lcsm_communication.PaymentInstruction;
import com.lloydstsb.schema.enterprise.lcsm_financialtransactionprocessing.ErrorInfo;
import com.lloydstsb.schema.infrastructure.soap.ContactPoint;
import com.lloydstsb.schema.infrastructure.soap.ResultCondition;
import com.lloydstsb.schema.infrastructure.soap.ServiceRequest;
import lib_sim_bo.businessobjects.BalanceTransfer;
import lib_sim_gmo.messages.RequestHeader;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.oasis_open.docs.wss._2004._01.oasis_200401_wss_wssecurity_secext_1_0.SecurityHeaderType;

import javax.xml.ws.WebServiceException;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Category(UnitTest.class)
public class EnquirePaymentRetrieverTest {

    EnquirePaymentRetriever enquirePaymentRetriever;
    RequestHeader header;
    TestDataHelper testDataHelper;

    @Before
    public void setUp() {
        enquirePaymentRetriever = new EnquirePaymentRetriever();
        enquirePaymentRetriever.enquirePaymentRequestFactory = mock(EnquirePaymentRequestFactory.class);
        enquirePaymentRetriever.headerRetriever = new HeaderRetriever();
        enquirePaymentRetriever.paymentProcessingClient = mock(PaymentProcessingClient.class);
        testDataHelper=new TestDataHelper();
        header=testDataHelper.createPpaeRequestHeader("LTB");
    }

    @Test
    public void testRetriever() throws ErrorInfo {
        BalanceTransfer balanceTransfer=new BalanceTransfer();
        balanceTransfer.setCreditCardNumber("00001254");
        EnquirePaymentInstructionFacilityDetailsRequest request=new EnquirePaymentInstructionFacilityDetailsRequest();
        when(enquirePaymentRetriever.enquirePaymentRequestFactory.convert("00001254")).thenReturn(request);
        when(enquirePaymentRetriever.paymentProcessingClient.enquirePaymentInstructionFacilityDetails(any(EnquirePaymentInstructionFacilityDetailsRequest.class), any(ContactPoint.class), any(ServiceRequest.class), any(SecurityHeaderType.class), any(BapiInformation.class))).thenReturn(getResponse());
        enquirePaymentRetriever.retrieve(header,balanceTransfer);
    }

    @Test
    public void testRetrieverWithServiceError() throws ErrorInfo {
        BalanceTransfer balanceTransfer=new BalanceTransfer();
        balanceTransfer.setCreditCardNumber("00001254");
        EnquirePaymentInstructionFacilityDetailsRequest request=new EnquirePaymentInstructionFacilityDetailsRequest();
        EnquirePaymentInstructionFacilityDetailsResponse response=getResponse();
        response.getResponseHeader().getResultCondition().setReasonCode(1024);
        when(enquirePaymentRetriever.enquirePaymentRequestFactory.convert("00001254")).thenReturn(request);
        when(enquirePaymentRetriever.paymentProcessingClient.enquirePaymentInstructionFacilityDetails(any(EnquirePaymentInstructionFacilityDetailsRequest.class), any(ContactPoint.class), any(ServiceRequest.class), any(SecurityHeaderType.class), any(BapiInformation.class))).thenReturn(response);
        enquirePaymentRetriever.retrieve(header, balanceTransfer);
    }

    @Test
    public void testRetrieverWithException() throws ErrorInfo {
        BalanceTransfer balanceTransfer=new BalanceTransfer();
        balanceTransfer.setCreditCardNumber("00001254");
        EnquirePaymentInstructionFacilityDetailsRequest request=new EnquirePaymentInstructionFacilityDetailsRequest();
        when(enquirePaymentRetriever.enquirePaymentRequestFactory.convert("00001254")).thenReturn(request);
        when(enquirePaymentRetriever.paymentProcessingClient.enquirePaymentInstructionFacilityDetails(any(EnquirePaymentInstructionFacilityDetailsRequest.class), any(ContactPoint.class), any(ServiceRequest.class), any(SecurityHeaderType.class), any(BapiInformation.class))).thenThrow(WebServiceException.class);
        enquirePaymentRetriever.retrieve(header,balanceTransfer);
    }

    @Test
    public void testRetrieverWithNormalPayment() throws ErrorInfo {
        BalanceTransfer balanceTransfer=new BalanceTransfer();
        balanceTransfer.setCreditCardNumber("00001254");
        EnquirePaymentInstructionFacilityDetailsRequest request=new EnquirePaymentInstructionFacilityDetailsRequest();
        EnquirePaymentInstructionFacilityDetailsResponse response=getResponse();
        response.getPaymentInstruction().getSourceArrangement().getHasObjectConditions().clear();
        RuleCondition ruleCondition1 = new RuleCondition();
        ruleCondition1.setName("NON_FAST_PAYMENT");
        ruleCondition1.setResult("NP");
        response.getPaymentInstruction().getSourceArrangement().getHasObjectConditions().add(ruleCondition1);
        when(enquirePaymentRetriever.enquirePaymentRequestFactory.convert("00001254")).thenReturn(request);
        when(enquirePaymentRetriever.paymentProcessingClient.enquirePaymentInstructionFacilityDetails(any(EnquirePaymentInstructionFacilityDetailsRequest.class), any(ContactPoint.class), any(ServiceRequest.class), any(SecurityHeaderType.class), any(BapiInformation.class))).thenReturn(response);
        enquirePaymentRetriever.retrieve(header,balanceTransfer);
    }

    private EnquirePaymentInstructionFacilityDetailsResponse getResponse() {
        EnquirePaymentInstructionFacilityDetailsResponse response = new EnquirePaymentInstructionFacilityDetailsResponse();
        response.setPaymentInstruction(new PaymentInstruction());
        response.getPaymentInstruction().setSourceArrangement(new ProductArrangement());
        RuleCondition ruleCondition = new RuleCondition();
        ruleCondition.setName("FAST_PAYMENT_SYSTEM_INDICATOR");
        ruleCondition.setResult("P");
        RuleCondition ruleCondition1 = new RuleCondition();
        ruleCondition1.setName("NON_FAST_PAYMENT");
        ruleCondition1.setResult("NP");
        response.getPaymentInstruction().getSourceArrangement().getHasObjectConditions().add(ruleCondition1);
        response.getPaymentInstruction().getSourceArrangement().getHasObjectConditions().add(ruleCondition);
        response.setResponseHeader(new ResponseHeader());
        response.getResponseHeader().setResultCondition(new ResultCondition());
        response.getResponseHeader().getResultCondition().setReasonCode(0);
        return response;
    }
}


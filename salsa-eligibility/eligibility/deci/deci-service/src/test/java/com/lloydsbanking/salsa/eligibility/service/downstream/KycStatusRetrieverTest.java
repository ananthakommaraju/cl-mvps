package com.lloydsbanking.salsa.eligibility.service.downstream;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.downstream.ocis.client.f075.F075Client;
import com.lloydsbanking.salsa.eligibility.TestDataHelper;
import com.lloydsbanking.salsa.eligibility.service.utility.ExceptionUtility;
import com.lloydsbanking.salsa.eligibility.service.utility.exceptions.SalsaExternalBusinessException;
import com.lloydsbanking.salsa.eligibility.service.utility.exceptions.SalsaInternalResourceNotAvailableException;
import com.lloydsbanking.salsa.eligibility.service.utility.exceptions.SalsaInternalServiceException;
import com.lloydsbanking.salsa.header.HeaderRetriever;
import com.lloydsbanking.salsa.header.RequestToResponseHeaderConverter;
import com.lloydsbanking.salsa.logging.application.TraceLogUtility;
import com.lloydsbanking.salsa.soap.ocis.f075.objects.F075Req;
import com.lloydsbanking.salsa.soap.ocis.f075.objects.F075Resp;
import com.lloydsbanking.salsa.soap.ocis.f075.objects.F075Result;
import com.lloydstsb.schema.infrastructure.soap.ContactPoint;
import com.lloydstsb.schema.infrastructure.soap.ResultCondition;
import com.lloydstsb.schema.infrastructure.soap.ServiceRequest;
import lb_gbo_sales.messages.DetermineElegibileInstructionsRequest;
import lb_gbo_sales.messages.RequestHeader;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.oasis_open.docs.wss._2004._01.oasis_200401_wss_wssecurity_secext_1_0.SecurityHeaderType;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Category(UnitTest.class)
public class KycStatusRetrieverTest {
    private KycStatusRetriever kycStatusRetriever;

    private TestDataHelper testDataHelper;

    private com.lloydsbanking.salsa.eligibility.wz.TestDataHelper testDataHelperWZ;

    RequestHeader header;

    DetermineElegibileInstructionsRequest upstreamRequest;

    F075Resp f075Resp;

    private List<String> lookupValues;

    @Before
    public void setUp() {
        testDataHelper = new TestDataHelper();
        testDataHelperWZ = new com.lloydsbanking.salsa.eligibility.wz.TestDataHelper();
        kycStatusRetriever = new KycStatusRetriever();

        kycStatusRetriever.headerRetriever = new HeaderRetriever();
        kycStatusRetriever.exceptionUtility = new ExceptionUtility(new RequestToResponseHeaderConverter());
        kycStatusRetriever.f075Client = mock(F075Client.class);
        kycStatusRetriever.traceLogUtility = mock(TraceLogUtility.class);

        upstreamRequest = testDataHelper.createEligibilityRequest("abc", testDataHelper.TEST_OCIS_ID, testDataHelper.TEST_RETAIL_CHANNEL_ID, testDataHelper.TEST_CONTACT_POINT_ID);
        header = upstreamRequest.getHeader();
        f075Resp = new F075Resp();
        lookupValues = new ArrayList();
        lookupValues.add("lookUpValueDesc");
    }

    @Test
    public void testGetKycStatusIsSuccessful() throws SalsaInternalServiceException, SalsaInternalResourceNotAvailableException, SalsaExternalBusinessException {

        f075Resp = testDataHelper.createKYCResponse("F");
        when(kycStatusRetriever.traceLogUtility.getMiscTraceEventMessage(any(String.class), any(String.class), any(String.class))).thenReturn("");
        when(kycStatusRetriever.f075Client.knowYourCustomerStatus(any(F075Req.class), any(ContactPoint.class), any(ServiceRequest.class), any(SecurityHeaderType.class))).thenReturn(f075Resp);

        String dataCollectedStatusCode = kycStatusRetriever.getKycStatus(upstreamRequest.getHeader(), null, null, false);

        assertEquals(f075Resp.getKYCControlData().getDataCollectedStatusCd(), dataCollectedStatusCode);
        assertNotNull(f075Resp.getF075Result());
        assertNotNull(f075Resp.getF075Result().getResultCondition());
        assertNotNull(f075Resp.getF075Result().getResultCondition().getReasonCode());
        assertEquals("0", f075Resp.getF075Result().getResultCondition().getReasonCode().toString());

    }

    @Test
    public void testGetKycStatusForExternalBusinessError() throws SalsaInternalResourceNotAvailableException, SalsaInternalServiceException {
        when(kycStatusRetriever.traceLogUtility.getMiscTraceEventMessage(any(String.class), any(String.class), any(String.class))).thenReturn("");
        when(kycStatusRetriever.f075Client.knowYourCustomerStatus(any(F075Req.class), any(ContactPoint.class), any(ServiceRequest.class), any(SecurityHeaderType.class))).thenReturn(f075Resp);
        f075Resp.setF075Result(new F075Result());
        f075Resp.getF075Result().setResultCondition(new ResultCondition());
        f075Resp.getF075Result().getResultCondition().setReasonCode(163004);

        try {
            kycStatusRetriever.getKycStatus(upstreamRequest.getHeader(), null, null, false);
        }
        catch (SalsaExternalBusinessException errorMsg) {
            assertEquals("00720001", errorMsg.getReasonCode());
            assertEquals(f075Resp.getF075Result().getResultCondition().getReasonText(), errorMsg.getReasonText());
        }
    }

    @Test(expected = SalsaInternalResourceNotAvailableException.class)
    public void testGetKycStatusForResourceNotFoundException() throws SalsaInternalResourceNotAvailableException, SalsaInternalServiceException {
        when(kycStatusRetriever.f075Client.knowYourCustomerStatus(any(F075Req.class), any(ContactPoint.class), any(ServiceRequest.class), any(SecurityHeaderType.class))).thenThrow(SalsaInternalResourceNotAvailableException.class);
        when(kycStatusRetriever.traceLogUtility.getMiscTraceEventMessage(any(String.class), any(String.class), any(String.class))).thenReturn("");

        try {
            kycStatusRetriever.getKycStatus(upstreamRequest.getHeader(), null, null, false);
        }
        catch (SalsaExternalBusinessException errorMsg) {
        }
    }

    @Test
    public void testGetKycStatusForInternalServiceError() throws SalsaInternalResourceNotAvailableException, SalsaExternalBusinessException {
        when(kycStatusRetriever.traceLogUtility.getMiscTraceEventMessage(any(String.class), any(String.class), any(String.class))).thenReturn("");
        when(kycStatusRetriever.f075Client.knowYourCustomerStatus(any(F075Req.class), any(ContactPoint.class), any(ServiceRequest.class), any(SecurityHeaderType.class))).thenReturn(f075Resp);
        f075Resp.setF075Result(new F075Result());
        f075Resp.getF075Result().setResultCondition(new ResultCondition());
        f075Resp.getF075Result().getResultCondition().setReasonCode(161034);

        try {
            kycStatusRetriever.getKycStatus(upstreamRequest.getHeader(), null, null, false);
        }
        catch (SalsaInternalServiceException errorMsg) {
            assertEquals("00720002", errorMsg.getReasonCode());
            assertEquals(f075Resp.getF075Result().getResultCondition().getReasonText(), errorMsg.getReasonText().getText());
        }
    }

    @Test
    public void testGetKycStatusWZIsSuccessful() throws SalsaInternalServiceException, SalsaInternalResourceNotAvailableException, SalsaExternalBusinessException {

        f075Resp = testDataHelperWZ.createKYCResponse("F", "097", "082", "097", "082");
        when(kycStatusRetriever.f075Client.knowYourCustomerStatus(any(F075Req.class), any(ContactPoint.class), any(ServiceRequest.class), any(SecurityHeaderType.class))).thenReturn(f075Resp);
        when(kycStatusRetriever.traceLogUtility.getMiscTraceEventMessage(any(String.class), any(String.class), any(String.class))).thenReturn("");
        String result = kycStatusRetriever.getKycStatus(upstreamRequest.getHeader(), "1234", lookupValues, true);

        assertEquals("F", result);
    }

    @Test
    public void testGetKycStatusWZForExternalBusinessError() throws SalsaInternalResourceNotAvailableException, SalsaInternalServiceException {
        when(kycStatusRetriever.traceLogUtility.getMiscTraceEventMessage(any(String.class), any(String.class), any(String.class))).thenReturn("");
        when(kycStatusRetriever.f075Client.knowYourCustomerStatus(any(F075Req.class), any(ContactPoint.class), any(ServiceRequest.class), any(SecurityHeaderType.class))).thenReturn(f075Resp);
        f075Resp.setF075Result(new F075Result());
        f075Resp.getF075Result().setResultCondition(new ResultCondition());
        f075Resp.getF075Result().getResultCondition().setReasonCode(163007);

        try {
            kycStatusRetriever.getKycStatus(upstreamRequest.getHeader(), "1234", lookupValues, true);
        }
        catch (SalsaExternalBusinessException errorMsg) {
            assertEquals("813003", errorMsg.getReasonCode());
            assertEquals(f075Resp.getF075Result().getResultCondition().getReasonText(), errorMsg.getReasonText());
        }
    }

    @Test
    public void testGetKycStatusWZForInternalServiceError() throws SalsaInternalResourceNotAvailableException, SalsaExternalBusinessException {
        when(kycStatusRetriever.traceLogUtility.getMiscTraceEventMessage(any(String.class), any(String.class), any(String.class))).thenReturn("");
        when(kycStatusRetriever.f075Client.knowYourCustomerStatus(any(F075Req.class), any(ContactPoint.class), any(ServiceRequest.class), any(SecurityHeaderType.class))).thenReturn(f075Resp);
        f075Resp.setF075Result(new F075Result());
        f075Resp.getF075Result().setResultCondition(new ResultCondition());
        f075Resp.getF075Result().getResultCondition().setReasonCode(163000);

        try {
            kycStatusRetriever.getKycStatus(upstreamRequest.getHeader(), "1234", lookupValues, true);
        }
        catch (SalsaInternalServiceException errorMsg) {
            assertEquals("823005", errorMsg.getReasonCode());
            assertEquals(f075Resp.getF075Result().getResultCondition().getReasonText(), errorMsg.getReasonText().getText());
        }
    }

    @Test
    public void testGetKycStatusWZForEmptyPartyEvidenceDataAndAddressEvidenceData() throws SalsaInternalServiceException, SalsaInternalResourceNotAvailableException, SalsaExternalBusinessException {

        f075Resp = testDataHelperWZ.createKYCResponse("F", "097", "082", "097", "082");
        f075Resp.getEvidenceData().getAddrEvidence().clear();
        f075Resp.getEvidenceData().getPartyEvidence().clear();
        when(kycStatusRetriever.traceLogUtility.getMiscTraceEventMessage(any(String.class), any(String.class), any(String.class))).thenReturn("");
        when(kycStatusRetriever.f075Client.knowYourCustomerStatus(any(F075Req.class), any(ContactPoint.class), any(ServiceRequest.class), any(SecurityHeaderType.class))).thenReturn(f075Resp);

        String result = kycStatusRetriever.getKycStatus(upstreamRequest.getHeader(), "1234", lookupValues, true);

        assertEquals("N", result);
    }

}

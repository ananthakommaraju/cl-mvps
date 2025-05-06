package com.lloydsbanking.salsa.activate.downstream;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.activate.TestDataHelper;
import com.lloydsbanking.salsa.activate.converter.F061RespToF062ReqConverter;
import com.lloydsbanking.salsa.activate.converter.F062RequestFactory;
import com.lloydsbanking.salsa.activate.helper.ApplicationDetails;
import com.lloydsbanking.salsa.activate.helper.UpdateDepositArrangementConditionAndApplicationStatusHelper;
import com.lloydsbanking.salsa.downstream.ocis.client.f061.F061Client;
import com.lloydsbanking.salsa.downstream.ocis.client.f062.F062Client;
import com.lloydsbanking.salsa.header.gmo.HeaderRetriever;
import com.lloydsbanking.salsa.logging.application.CustomerTraceLog;
import com.lloydsbanking.salsa.soap.ocis.f061.objects.F061Req;
import com.lloydsbanking.salsa.soap.ocis.f061.objects.F061Resp;
import com.lloydsbanking.salsa.soap.ocis.f062.objects.F062Req;
import com.lloydsbanking.salsa.soap.ocis.f062.objects.F062Resp;
import com.lloydsbanking.salsa.soap.ocis.f062.objects.F062Result;
import com.lloydstsb.schema.infrastructure.soap.ContactPoint;
import com.lloydstsb.schema.infrastructure.soap.ResultCondition;
import com.lloydstsb.schema.infrastructure.soap.ServiceRequest;
import lib_sim_bo.businessobjects.AssessmentEvidence;
import lib_sim_bo.businessobjects.Customer;
import lib_sim_gmo.messages.RequestHeader;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.oasis_open.docs.wss._2004._01.oasis_200401_wss_wssecurity_secext_1_0.SecurityHeaderType;

import javax.xml.ws.WebServiceException;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Category(UnitTest.class)
public class CreateInvolvedPartyTest {
    private CreateInvolvedParty createInvolvedParty;
    private TestDataHelper testDataHelper;
    private RequestHeader requestHeader;


    @Before
    public void setUp() {
        createInvolvedParty = new CreateInvolvedParty();
        testDataHelper = new TestDataHelper();
        testDataHelper.headerRetriever = new HeaderRetriever();
        requestHeader = testDataHelper.createApaRequestHeader();
        createInvolvedParty.f061Client = mock(F061Client.class);
        createInvolvedParty.f061RespToF062ReqConverter = mock(F061RespToF062ReqConverter.class);
        createInvolvedParty.f062Client = mock(F062Client.class);
        createInvolvedParty.f062RequestFactory = mock(F062RequestFactory.class);
        createInvolvedParty.headerRetriever = mock(HeaderRetriever.class);
        createInvolvedParty.updateAppDetails = new UpdateDepositArrangementConditionAndApplicationStatusHelper();
        createInvolvedParty.customerTraceLog = mock(CustomerTraceLog.class);
        when(createInvolvedParty.customerTraceLog.getCustomerTraceEventMessage(any(Customer.class), any(String.class))).thenReturn("Customer");
    }

    @Test
    public void testCreate() {
        Customer customer = new Customer();
        customer.setCustomerIdentifier("123");
        F062Resp f062Resp = createF062Resp();
        f062Resp.getF062Result().getResultCondition().setReasonCode(null);
        AssessmentEvidence assessmentEvidence = new AssessmentEvidence();
        ApplicationDetails applicationDetails = new ApplicationDetails();
        when(createInvolvedParty.headerRetriever.getContactPoint(requestHeader.getLloydsHeaders())).thenReturn(testDataHelper.getContactPointId(requestHeader));
        when(createInvolvedParty.headerRetriever.getSecurityHeader(requestHeader.getLloydsHeaders())).thenReturn(testDataHelper.getSecurityHeaderTypeFromRequestHeader(requestHeader));
        when(createInvolvedParty.headerRetriever.getServiceRequest(requestHeader.getLloydsHeaders())).thenReturn(testDataHelper.getServiceRequestFromRequestHeader(requestHeader));
        when(createInvolvedParty.f061Client.f061(any(F061Req.class), any(ContactPoint.class), any(ServiceRequest.class), any(SecurityHeaderType.class))).thenReturn(createF061Resp());
        when(createInvolvedParty.f061RespToF062ReqConverter.convert(createF061Resp(), assessmentEvidence, customer)).thenReturn(createF062Req());
        when(createInvolvedParty.f062Client.updateCustomerRecord(any(F062Req.class), any(ContactPoint.class), any(ServiceRequest.class), any(SecurityHeaderType.class))).thenReturn(f062Resp);
        String custId = createInvolvedParty.create(customer, "CA", assessmentEvidence, requestHeader, applicationDetails, 0);
        assertEquals("123", custId);
    }

    @Test
    public void testCreateWithNullCustId() {
        Customer customer = new Customer();
        customer.setCustomerIdentifier(null);
        AssessmentEvidence assessmentEvidence = new AssessmentEvidence();
        ApplicationDetails applicationDetails = new ApplicationDetails();
        when(createInvolvedParty.headerRetriever.getContactPoint(requestHeader.getLloydsHeaders())).thenReturn(testDataHelper.getContactPointId(requestHeader));
        when(createInvolvedParty.headerRetriever.getSecurityHeader(requestHeader.getLloydsHeaders())).thenReturn(testDataHelper.getSecurityHeaderTypeFromRequestHeader(requestHeader));
        when(createInvolvedParty.headerRetriever.getServiceRequest(requestHeader.getLloydsHeaders())).thenReturn(testDataHelper.getServiceRequestFromRequestHeader(requestHeader));
        when(createInvolvedParty.f061Client.f061(createF061Req(), testDataHelper.getContactPointId(requestHeader), testDataHelper.getServiceRequestFromRequestHeader(requestHeader), testDataHelper.getSecurityHeaderTypeFromRequestHeader(requestHeader))).thenReturn(createF061Resp());
        when(createInvolvedParty.f061RespToF062ReqConverter.convert(createF061Resp(), assessmentEvidence, customer)).thenReturn(createF062Req());
        when(createInvolvedParty.f062Client.updateCustomerRecord(createF062Req(), testDataHelper.getContactPointId(requestHeader), testDataHelper.getServiceRequestFromRequestHeader(requestHeader), testDataHelper.getSecurityHeaderTypeFromRequestHeader(requestHeader))).thenReturn(createF062Resp());
        String custId = createInvolvedParty.create(customer, "CA", assessmentEvidence, requestHeader, applicationDetails, 0);
        assertNull(custId);
    }

    @Test
    public void testCreateWithF061ClientException() {
        Customer customer = new Customer();
        customer.setCustomerIdentifier("123");
        AssessmentEvidence assessmentEvidence = new AssessmentEvidence();
        ApplicationDetails applicationDetails = new ApplicationDetails();
        when(createInvolvedParty.headerRetriever.getContactPoint(requestHeader.getLloydsHeaders())).thenReturn(testDataHelper.getContactPointId(requestHeader));
        when(createInvolvedParty.headerRetriever.getSecurityHeader(requestHeader.getLloydsHeaders())).thenReturn(testDataHelper.getSecurityHeaderTypeFromRequestHeader(requestHeader));
        when(createInvolvedParty.headerRetriever.getServiceRequest(requestHeader.getLloydsHeaders())).thenReturn(testDataHelper.getServiceRequestFromRequestHeader(requestHeader));
        when(createInvolvedParty.f061Client.f061(createF061Req(), testDataHelper.getContactPointId(requestHeader), testDataHelper.getServiceRequestFromRequestHeader(requestHeader), testDataHelper.getSecurityHeaderTypeFromRequestHeader(requestHeader))).thenThrow(WebServiceException.class);
        createInvolvedParty.create(customer, "CA", assessmentEvidence, requestHeader, applicationDetails, 0);
    }

    @Test
    public void testCreateWithNullResponse() {
        Customer customer = new Customer();
        customer.setCustomerIdentifier("123");
        AssessmentEvidence assessmentEvidence = new AssessmentEvidence();
        ApplicationDetails applicationDetails = new ApplicationDetails();
        when(createInvolvedParty.headerRetriever.getContactPoint(requestHeader.getLloydsHeaders())).thenReturn(testDataHelper.getContactPointId(requestHeader));
        when(createInvolvedParty.headerRetriever.getSecurityHeader(requestHeader.getLloydsHeaders())).thenReturn(testDataHelper.getSecurityHeaderTypeFromRequestHeader(requestHeader));
        when(createInvolvedParty.headerRetriever.getServiceRequest(requestHeader.getLloydsHeaders())).thenReturn(testDataHelper.getServiceRequestFromRequestHeader(requestHeader));
        when(createInvolvedParty.f061Client.f061(createF061Req(), testDataHelper.getContactPointId(requestHeader), testDataHelper.getServiceRequestFromRequestHeader(requestHeader), testDataHelper.getSecurityHeaderTypeFromRequestHeader(requestHeader))).thenReturn(null);
        when(createInvolvedParty.f062Client.updateCustomerRecord(createF062Req(), testDataHelper.getContactPointId(requestHeader), testDataHelper.getServiceRequestFromRequestHeader(requestHeader), testDataHelper.getSecurityHeaderTypeFromRequestHeader(requestHeader))).thenReturn(null);
        String custId = createInvolvedParty.create(customer, "CA", assessmentEvidence, requestHeader, applicationDetails, 0);
        assertEquals("123", custId);
    }

    @Test
    public void testCreateWhenCustomerIdentifierIsNullAndF062Exception() {
        Customer customer = new Customer();
        AssessmentEvidence assessmentEvidence = new AssessmentEvidence();
        ApplicationDetails applicationDetails = new ApplicationDetails();
        when(createInvolvedParty.headerRetriever.getContactPoint(requestHeader.getLloydsHeaders())).thenReturn(testDataHelper.getContactPointId(requestHeader));
        when(createInvolvedParty.headerRetriever.getSecurityHeader(requestHeader.getLloydsHeaders())).thenReturn(testDataHelper.getSecurityHeaderTypeFromRequestHeader(requestHeader));
        when(createInvolvedParty.headerRetriever.getServiceRequest(requestHeader.getLloydsHeaders())).thenReturn(testDataHelper.getServiceRequestFromRequestHeader(requestHeader));
        when(createInvolvedParty.f062Client.updateCustomerRecord(createF062Req(), testDataHelper.getContactPointId(requestHeader), testDataHelper.getServiceRequestFromRequestHeader(requestHeader), testDataHelper.getSecurityHeaderTypeFromRequestHeader(requestHeader))).thenThrow(WebServiceException.class);
        when(createInvolvedParty.f062RequestFactory.convert(customer, "CA", assessmentEvidence)).thenReturn(createF062Req());
        createInvolvedParty.create(customer, "CA", assessmentEvidence, requestHeader, applicationDetails, null);
    }

    @Test
    public void testCreateWithErrorScenario() {
        Customer customer = new Customer();
        customer.setCustomerIdentifier("123");
        F062Resp f062Resp = createF062Resp();
        AssessmentEvidence assessmentEvidence = new AssessmentEvidence();
        ApplicationDetails applicationDetails = new ApplicationDetails();
        when(createInvolvedParty.headerRetriever.getContactPoint(requestHeader.getLloydsHeaders())).thenReturn(testDataHelper.getContactPointId(requestHeader));
        when(createInvolvedParty.headerRetriever.getSecurityHeader(requestHeader.getLloydsHeaders())).thenReturn(testDataHelper.getSecurityHeaderTypeFromRequestHeader(requestHeader));
        when(createInvolvedParty.headerRetriever.getServiceRequest(requestHeader.getLloydsHeaders())).thenReturn(testDataHelper.getServiceRequestFromRequestHeader(requestHeader));
        when(createInvolvedParty.f061Client.f061(createF061Req(), testDataHelper.getContactPointId(requestHeader), testDataHelper.getServiceRequestFromRequestHeader(requestHeader), testDataHelper.getSecurityHeaderTypeFromRequestHeader(requestHeader))).thenReturn(createF061Resp());
        when(createInvolvedParty.f061RespToF062ReqConverter.convert(createF061Resp(), assessmentEvidence, customer)).thenReturn(createF062Req());
        when(createInvolvedParty.f062Client.updateCustomerRecord(any(F062Req.class), any(ContactPoint.class), any(ServiceRequest.class), any(SecurityHeaderType.class))).thenReturn(f062Resp);
        assertNotNull(createInvolvedParty.create(customer, "CA", assessmentEvidence, requestHeader, applicationDetails, 0));
        assertNotNull(f062Resp);
        assertEquals("CHANNEL_OUTLET_TYPE_INVALID_CODE", f062Resp.getF062Result().getResultCondition().getReasonText());
    }

    private F061Req createF061Req() {
        F061Req f061Req = new F061Req();
        f061Req.setExtSysId(Short.valueOf("19"));
        f061Req.setPartyId(Long.valueOf("123"));
        return f061Req;
    }

    private F061Resp createF061Resp() {
        F061Resp f061Resp = new F061Resp();
        return f061Resp;
    }

    private F062Req createF062Req() {
        F062Req f062Req = new F062Req();
        return f062Req;
    }

    private F062Resp createF062Resp() {
        F062Resp f062Resp = new F062Resp();
        f062Resp.setF062Result(new F062Result());
        f062Resp.getF062Result().setResultCondition(new ResultCondition());
        f062Resp.getF062Result().getResultCondition().setReasonCode(163003);
        f062Resp.getF062Result().getResultCondition().setReasonText("CHANNEL_OUTLET_TYPE_INVALID_CODE");
        return f062Resp;
    }
}

package com.lloydsbanking.salsa.apapca.service.fulfil.downstream;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.activate.helper.ApplicationDetails;
import com.lloydsbanking.salsa.activate.helper.UpdateDepositArrangementConditionAndApplicationStatusHelper;
import com.lloydsbanking.salsa.apapca.TestDataHelper;
import com.lloydsbanking.salsa.apapca.service.fulfil.converter.CreateCaseRequestFactory;
import com.lloydsbanking.salsa.downstream.pega.client.PegaClient;
import com.lloydsbanking.salsa.header.gmo.HeaderRetriever;
import com.lloydsbanking.salsa.soap.pega.objects.CreateCaseRequestType;
import com.lloydsbanking.xml.schema.pega.industryaccountswitching.CreateCasePayloadResponseType;
import com.lloydsbanking.xml.schema.pega.industryaccountswitching.InitiateSwitchInType;
import com.lloydstsb.schema.casetracking.ifw.GenericResponseType;
import com.lloydstsb.schema.enterprise.ifwxml.ResponseHeader;
import com.lloydstsb.schema.enterprise.ifwxml.ResultCondition;
import com.lloydstsb.schema.infrastructure.soap.ContactPoint;
import com.lloydstsb.schema.infrastructure.soap.ServiceRequest;
import lib_sim_bo.businessobjects.Condition;
import lib_sim_bo.businessobjects.DepositArrangement;
import lib_sim_bo.businessobjects.DirectDebit;
import lib_sim_gmo.messages.RequestHeader;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.oasis_open.docs.wss._2004._01.oasis_200401_wss_wssecurity_secext_1_0.SecurityHeaderType;

import javax.xml.ws.WebServiceException;

import static org.mockito.Mockito.*;

@Category(UnitTest.class)
public class CreateCaseRetrieverTest {
    private CreateCaseRetriever createCaseRetriever;
    private TestDataHelper testDataHelper;
    private RequestHeader requestHeader;
    private ContactPoint contactPoint;
    private SecurityHeaderType securityHeaderType;
    private ServiceRequest serviceRequest;

    @Before
    public void setUp() {
        createCaseRetriever = new CreateCaseRetriever();
        testDataHelper = new TestDataHelper();
        requestHeader = testDataHelper.createApaRequestHeader();
        createCaseRetriever.pegaClient = mock(PegaClient.class);
        createCaseRetriever.headerRetriever = new HeaderRetriever();
        createCaseRetriever.createCaseRequestFactory = mock(CreateCaseRequestFactory.class);
        createCaseRetriever.updateDepositArrangementConditionAndApplicationStatusHelper = mock(UpdateDepositArrangementConditionAndApplicationStatusHelper.class);
        contactPoint = createCaseRetriever.headerRetriever.getContactPoint(requestHeader.getLloydsHeaders());
        serviceRequest = createCaseRetriever.headerRetriever.getServiceRequest((requestHeader.getLloydsHeaders()), "CreateCaseDetails", "PEGA-IAS");
        securityHeaderType = createCaseRetriever.headerRetriever.getSecurityHeader(requestHeader.getLloydsHeaders());
    }

    @Test
    public void testCreate() {
        DepositArrangement depositArrangement = testDataHelper.createDepositArrangement("10245");
        depositArrangement.setAccountSwitchingDetails(new DirectDebit());
        CreateCaseRequestType createCaseRequest = new CreateCaseRequestType();
        GenericResponseType response = new GenericResponseType();
        ResultCondition resultCondition = new ResultCondition();
        resultCondition.setSeverityCode("1023");
        response.setResponseHeader(new ResponseHeader());
        response.getResponseHeader().setResultConditions(resultCondition);
        CreateCasePayloadResponseType createCasePayloadResponseType = new CreateCasePayloadResponseType();
        createCasePayloadResponseType.setInitiateSwitchIn(new InitiateSwitchInType());
        createCasePayloadResponseType.getInitiateSwitchIn().setCaseId("112");
        response.setPayload(createCasePayloadResponseType);
        when(createCaseRetriever.createCaseRequestFactory.convert(depositArrangement, requestHeader)).thenReturn(createCaseRequest);
        when(createCaseRetriever.pegaClient.createTask(createCaseRequest, contactPoint, serviceRequest, securityHeaderType)).thenReturn(response);
        createCaseRetriever.create(depositArrangement, requestHeader, createApplicationDetailsResponse());
        verify(createCaseRetriever.pegaClient).createTask(createCaseRequest, contactPoint, serviceRequest, securityHeaderType);
    }

    @Test
    public void testCreateWithSeverityCodeAsZero() {
        DepositArrangement depositArrangement = testDataHelper.createDepositArrangement("10245");
        depositArrangement.setAccountSwitchingDetails(new DirectDebit());
        CreateCaseRequestType createCaseRequest = new CreateCaseRequestType();
        GenericResponseType response = new GenericResponseType();
        ResultCondition resultCondition = new ResultCondition();
        resultCondition.setSeverityCode("0");
        response.setResponseHeader(new ResponseHeader());
        response.getResponseHeader().setResultConditions(resultCondition);
        CreateCasePayloadResponseType createCasePayloadResponseType = new CreateCasePayloadResponseType();
        createCasePayloadResponseType.setInitiateSwitchIn(new InitiateSwitchInType());
        response.setPayload(createCasePayloadResponseType);
        when(createCaseRetriever.createCaseRequestFactory.convert(depositArrangement, requestHeader)).thenReturn(createCaseRequest);
        when(createCaseRetriever.pegaClient.createTask(createCaseRequest, contactPoint, serviceRequest, securityHeaderType)).thenReturn(response);
        createCaseRetriever.create(depositArrangement, requestHeader, new ApplicationDetails());
        verify(createCaseRetriever.pegaClient).createTask(createCaseRequest, contactPoint, serviceRequest, securityHeaderType);
    }

    @Test
    public void testCreateWithAccountSwitchingDetailsAsNull() {
        DepositArrangement depositArrangement = testDataHelper.createDepositArrangement("10245");
        CreateCaseRequestType createCaseRequest = new CreateCaseRequestType();
        GenericResponseType response = new GenericResponseType();
        ResultCondition resultCondition = new ResultCondition();
        resultCondition.setSeverityCode("1023");
        response.setResponseHeader(new ResponseHeader());
        response.getResponseHeader().setResultConditions(resultCondition);
        CreateCasePayloadResponseType createCasePayloadResponseType = new CreateCasePayloadResponseType();
        createCasePayloadResponseType.setInitiateSwitchIn(new InitiateSwitchInType());
        createCasePayloadResponseType.getInitiateSwitchIn().setCaseId("112");
        response.setPayload(createCasePayloadResponseType);
        when(createCaseRetriever.createCaseRequestFactory.convert(depositArrangement, requestHeader)).thenReturn(createCaseRequest);
        when(createCaseRetriever.pegaClient.createTask(createCaseRequest, contactPoint, serviceRequest, securityHeaderType)).thenReturn(response);
        createCaseRetriever.create(depositArrangement, requestHeader, new ApplicationDetails());
        verify(createCaseRetriever.pegaClient).createTask(createCaseRequest, contactPoint, serviceRequest, securityHeaderType);
    }

    @Test
    public void testCreateWithCaseId() {
        DepositArrangement depositArrangement = testDataHelper.createDepositArrangement("10245");
        depositArrangement.setAccountSwitchingDetails(new DirectDebit());
        CreateCaseRequestType createCaseRequest = new CreateCaseRequestType();
        GenericResponseType response = new GenericResponseType();
        ResultCondition resultCondition = new ResultCondition();
        resultCondition.setSeverityCode("0");
        response.setResponseHeader(new ResponseHeader());
        response.getResponseHeader().setResultConditions(resultCondition);
        CreateCasePayloadResponseType createCasePayloadResponseType = new CreateCasePayloadResponseType();
        createCasePayloadResponseType.setInitiateSwitchIn(new InitiateSwitchInType());
        createCasePayloadResponseType.getInitiateSwitchIn().setCaseId("1001");
        response.setPayload(createCasePayloadResponseType);
        when(createCaseRetriever.createCaseRequestFactory.convert(depositArrangement, requestHeader)).thenReturn(createCaseRequest);
        when(createCaseRetriever.pegaClient.createTask(createCaseRequest, contactPoint, serviceRequest, securityHeaderType)).thenReturn(response);
        createCaseRetriever.create(depositArrangement, requestHeader, new ApplicationDetails());
        verify(createCaseRetriever.pegaClient).createTask(createCaseRequest, contactPoint, serviceRequest, securityHeaderType);
    }

    @Test
    public void testCreateWithNullPayload() {
        DepositArrangement depositArrangement = testDataHelper.createDepositArrangement("10245");
        depositArrangement.setAccountSwitchingDetails(new DirectDebit());
        CreateCaseRequestType createCaseRequest = new CreateCaseRequestType();
        GenericResponseType response = new GenericResponseType();
        ResultCondition resultCondition = new ResultCondition();
        resultCondition.setSeverityCode("0");
        response.setResponseHeader(new ResponseHeader());
        response.getResponseHeader().setResultConditions(resultCondition);
        when(createCaseRetriever.createCaseRequestFactory.convert(depositArrangement, requestHeader)).thenReturn(createCaseRequest);
        when(createCaseRetriever.pegaClient.createTask(createCaseRequest, contactPoint, serviceRequest, securityHeaderType)).thenReturn(response);
        createCaseRetriever.create(depositArrangement, requestHeader, new ApplicationDetails());
        verify(createCaseRetriever.pegaClient).createTask(createCaseRequest, contactPoint, serviceRequest, securityHeaderType);
    }

    @Test
    public void testCreateWithException() {
        CreateCaseRequestType createCaseRequest = new CreateCaseRequestType();
        when(createCaseRetriever.createCaseRequestFactory.convert(testDataHelper.createDepositArrangement("10245"), requestHeader)).thenReturn(createCaseRequest);
        when(createCaseRetriever.pegaClient.createTask(createCaseRequest, contactPoint, serviceRequest, securityHeaderType)).thenThrow(WebServiceException.class);
        createCaseRetriever.create(testDataHelper.createDepositArrangement("10245"), requestHeader, new ApplicationDetails());
    }

    private ApplicationDetails createApplicationDetailsResponse() {
        ApplicationDetails applicationDetails = new ApplicationDetails();
        applicationDetails.getConditionList().add(getCondition("1034", "Create Case Failure"));
        applicationDetails.setApiFailureFlag(true);
        applicationDetails.setApplicationStatus(null);
        applicationDetails.setApplicationSubStatus("1034");
        return applicationDetails;
    }

    private Condition getCondition(String reasonCode, String reasonText) {
        Condition condition = new Condition();
        condition.setSeverityCode(null);
        condition.setReasonCode(reasonCode);
        condition.setReasonText(reasonText);
        return condition;
    }
}

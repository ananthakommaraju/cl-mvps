package com.lloydsbanking.salsa.activate.postfulfil.downstream;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.activate.helper.ApplicationDetails;
import com.lloydsbanking.salsa.activate.helper.UpdateDepositArrangementConditionAndApplicationStatusHelper;
import com.lloydsbanking.salsa.activate.postfulfil.convert.RecordDocumentMetaContentRequestBuilder;
import com.lloydsbanking.salsa.activate.utility.ExceptionUtilityActivate;
import com.lloydsbanking.salsa.downstream.soadms.client.documentmanager.SOADocumentManagerClient;
import com.lloydsbanking.salsa.header.gmo.HeaderRetriever;
import com.lloydsbanking.salsa.logging.application.CustomerTraceLog;
import com.lloydsbanking.salsa.soap.soa.dm.esb.ifwxml.ResponseHeader;
import com.lloydsbanking.salsa.soap.soa.dm.esb.ifwxml.ResultCondition;
import com.lloydsbanking.salsa.soap.soa.documentation.manager.RecordDocumentMetaContentRequest;
import com.lloydsbanking.salsa.soap.soa.documentation.manager.RecordDocumentMetaContentResponse;
import com.lloydsbanking.salsa.soap.soa.documentation.manager.RetrieveDocumentMetaContentRequest;
import com.lloydsbanking.salsa.soap.soa.documentation.manager.RetrieveDocumentMetaContentResponse;
import com.lloydstsb.schema.documentmanagement.ifw.ErrorInfo;
import com.lloydstsb.schema.infrastructure.soap.ContactPoint;
import com.lloydstsb.schema.infrastructure.soap.ServiceRequest;
import lib_sim_bo.businessobjects.Customer;
import lib_sim_bo.businessobjects.ProductArrangement;
import lib_sim_gmo.messages.RequestHeader;
import lib_sim_gmo.messages.SOAPHeader;
import lib_sim_salesprocessmanagement.ia_activateproductarrangement.ActivateProductArrangementExternalSystemErrorMsg;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.oasis_open.docs.wss._2004._01.oasis_200401_wss_wssecurity_secext_1_0.SecurityHeaderType;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Category(UnitTest.class)
public class RecordDocumentMetaContentTest {
    RecordDocumentMetaContent recordDocumentMetaContent;
    Customer customer;
    RequestHeader header;
    RecordDocumentMetaContentResponse recordDocumentMetaContentResponse;
    RetrieveDocumentMetaContentResponse retrieveDocumentMetaContentResponse;
    RecordDocumentMetaContentRequest recordDocumentMetaContentRequest;
    ContactPoint contactPoint;
    ServiceRequest serviceRequest;
    SecurityHeaderType securityHeader;
    ResponseHeader responseHeader;
    ActivateProductArrangementExternalSystemErrorMsg activateProductArrangementExternalSystemErrorMsg;
    ProductArrangement productArrangement;
    ApplicationDetails applicationDetails;
    UpdateDepositArrangementConditionAndApplicationStatusHelper updateDepositArrangementConditionAndApplicationStatusHelper;

    @Before
    public void setUp() {
        activateProductArrangementExternalSystemErrorMsg = new ActivateProductArrangementExternalSystemErrorMsg();
        customer = new Customer();
        applicationDetails = new ApplicationDetails();
        productArrangement = new ProductArrangement();
        productArrangement.setPrimaryInvolvedParty(customer);
        header = new RequestHeader();
        contactPoint = new ContactPoint();
        serviceRequest = new ServiceRequest();
        securityHeader = new SecurityHeaderType();
        responseHeader = new ResponseHeader();
        ResultCondition resultCondition = new ResultCondition();
        resultCondition.setSeverityCode("0");
        responseHeader.setResultConditions(resultCondition);
        recordDocumentMetaContent = new RecordDocumentMetaContent();
        recordDocumentMetaContent.updateDepositArrangementConditionAndApplicationStatusHelper = mock(UpdateDepositArrangementConditionAndApplicationStatusHelper.class);
        recordDocumentMetaContentRequest = new RecordDocumentMetaContentRequest();
        retrieveDocumentMetaContentResponse = new RetrieveDocumentMetaContentResponse();
        recordDocumentMetaContentResponse = new RecordDocumentMetaContentResponse();
        recordDocumentMetaContentResponse.setResponseHeader(responseHeader);
        recordDocumentMetaContent.headerRetriever = mock(HeaderRetriever.class);
        recordDocumentMetaContent.recordDocumentMetaContentRequestBuilder = mock(RecordDocumentMetaContentRequestBuilder.class);
        recordDocumentMetaContent.soaDocumentManagerClient = mock(SOADocumentManagerClient.class);
        recordDocumentMetaContent.exceptionUtilityActivate = mock(ExceptionUtilityActivate.class);
        recordDocumentMetaContent.customerTraceLog = mock(CustomerTraceLog.class);
        when(recordDocumentMetaContent.customerTraceLog.getIndividualTraceEventMessage(any(lib_sim_bo.businessobjects.Individual.class), any(String.class))).thenReturn("Invidual ");

    }

    @Test
    public void testRecordDocumentMetaContent() throws ErrorInfo, ActivateProductArrangementExternalSystemErrorMsg {
        when(recordDocumentMetaContent.headerRetriever.getContactPoint(anyListOf(SOAPHeader.class))).thenReturn(contactPoint);
        when(recordDocumentMetaContent.headerRetriever.getServiceRequest(anyListOf(SOAPHeader.class), anyString(), anyString())).thenReturn(serviceRequest);
        when(recordDocumentMetaContent.headerRetriever.getSecurityHeader(anyListOf(SOAPHeader.class))).thenReturn(securityHeader);
        when(recordDocumentMetaContent.recordDocumentMetaContentRequestBuilder.convert(any(Customer.class), any(RetrieveDocumentMetaContentResponse.class))).thenReturn(recordDocumentMetaContentRequest);
        when(recordDocumentMetaContent.soaDocumentManagerClient.retrieveDocumentMetaContent(any(RetrieveDocumentMetaContentRequest.class), any(ContactPoint.class), any(ServiceRequest.class), any(SecurityHeaderType.class))).thenReturn(retrieveDocumentMetaContentResponse);
        when(recordDocumentMetaContent.soaDocumentManagerClient.recordDocumentMetaContent(any(RecordDocumentMetaContentRequest.class), any(ContactPoint.class), any(ServiceRequest.class), any(SecurityHeaderType.class))).thenReturn(recordDocumentMetaContentResponse);
        recordDocumentMetaContent.recordDocMetaContent(applicationDetails, productArrangement, header);
    }

    @Test
    public void testRecordDocumentMetaContentThrowsError() throws ErrorInfo {
        ActivateProductArrangementExternalSystemErrorMsg activateProductArrangementExternalSystemErrorMsg1 = null;
        ResultCondition resultCondition = new ResultCondition();
        resultCondition.setSeverityCode("1");
        responseHeader.setResultConditions(resultCondition);
        recordDocumentMetaContentResponse.setResponseHeader(responseHeader);
        when(recordDocumentMetaContent.soaDocumentManagerClient.retrieveDocumentMetaContent(any(RetrieveDocumentMetaContentRequest.class), any(ContactPoint.class), any(ServiceRequest.class), any(SecurityHeaderType.class))).thenReturn(retrieveDocumentMetaContentResponse);
        when(recordDocumentMetaContent.soaDocumentManagerClient.recordDocumentMetaContent(any(RecordDocumentMetaContentRequest.class), any(ContactPoint.class), any(ServiceRequest.class), any(SecurityHeaderType.class))).thenReturn(recordDocumentMetaContentResponse);
        when(recordDocumentMetaContent.exceptionUtilityActivate.externalServiceError(any(RequestHeader.class), any(String.class), any(String.class))).thenReturn(activateProductArrangementExternalSystemErrorMsg);
        when(recordDocumentMetaContent.headerRetriever.getContactPoint(anyListOf(SOAPHeader.class))).thenReturn(contactPoint);
        when(recordDocumentMetaContent.headerRetriever.getServiceRequest(anyListOf(SOAPHeader.class), anyString(), anyString())).thenReturn(serviceRequest);
        when(recordDocumentMetaContent.headerRetriever.getSecurityHeader(anyListOf(SOAPHeader.class))).thenReturn(securityHeader);
        when(recordDocumentMetaContent.recordDocumentMetaContentRequestBuilder.convert(any(Customer.class), any(RetrieveDocumentMetaContentResponse.class))).thenReturn(recordDocumentMetaContentRequest);
        recordDocumentMetaContent.recordDocMetaContent(applicationDetails, productArrangement, header);
    }
}

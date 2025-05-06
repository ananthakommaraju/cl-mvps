package com.lloydsbanking.salsa.activate.postfulfil.downstream;


import com.lloydsbanking.salsa.activate.helper.ApplicationDetails;
import com.lloydsbanking.salsa.activate.helper.UpdateDepositArrangementConditionAndApplicationStatusHelper;
import com.lloydsbanking.salsa.activate.postfulfil.convert.RecordDocumentMetaContentRequestBuilder;
import com.lloydsbanking.salsa.activate.utility.ExceptionUtilityActivate;
import com.lloydsbanking.salsa.downstream.soadms.client.documentmanager.SOADocumentManagerClient;
import com.lloydsbanking.salsa.header.gmo.HeaderRetriever;
import com.lloydsbanking.salsa.logging.application.CustomerTraceLog;
import com.lloydsbanking.salsa.soap.soa.dm.esb.common.ObjectReference;
import com.lloydsbanking.salsa.soap.soa.dm.esb.ip.Individual;
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
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.oasis_open.docs.wss._2004._01.oasis_200401_wss_wssecurity_secext_1_0.SecurityHeaderType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.xml.ws.WebServiceException;

@Repository
public class RecordDocumentMetaContent {
    private static final Logger LOGGER = Logger.getLogger(RecordDocumentMetaContent.class);
    @Autowired
    SOADocumentManagerClient soaDocumentManagerClient;
    @Autowired
    RecordDocumentMetaContentRequestBuilder recordDocumentMetaContentRequestBuilder;
    @Autowired
    ExceptionUtilityActivate exceptionUtilityActivate;
    @Autowired
    HeaderRetriever headerRetriever;
    @Autowired
    UpdateDepositArrangementConditionAndApplicationStatusHelper updateDepositArrangementConditionAndApplicationStatusHelper;
    @Autowired
    CustomerTraceLog customerTraceLog;

    public static final String DATA_SOURCE_NAME = "19";
    public static final String RETRIEVE_SERVICE_NAME = "http://www.lloydstsb.com/Schema/InvolvedPartyManagement/IFW";
    public static final String RETRIEVE_SERVICE_ACTION = "RetrieveInvolvedParty";
    public static final String RECORD_SERVICE_NAME = "http://www.lloydstsb.com/Schema/DocumentManagement/IFW";
    public static final String RECORD_SERVICE_ACTION = "RecordDocumentMetaContent";

    public void recordDocMetaContent(ApplicationDetails applicationDetails, ProductArrangement productArrangement, RequestHeader header) {
        Customer customer = productArrangement.getPrimaryInvolvedParty();
        LOGGER.info(customerTraceLog.getIndividualTraceEventMessage(customer.getIsPlayedBy(), "Entering RecordDocMetaContent with Individual "));
        ContactPoint contactPoint = headerRetriever.getContactPoint(header.getLloydsHeaders());
        ServiceRequest retrieveServiceRequest = headerRetriever.getServiceRequest(header.getLloydsHeaders(), RETRIEVE_SERVICE_NAME, RETRIEVE_SERVICE_ACTION);
        ServiceRequest recordServiceRequest = headerRetriever.getServiceRequest(header.getLloydsHeaders(), RECORD_SERVICE_NAME, RECORD_SERVICE_ACTION);
        SecurityHeaderType securityHeaderType = headerRetriever.getSecurityHeader(header.getLloydsHeaders());
        try {
            RetrieveDocumentMetaContentResponse retrieveDocumentMetaContentResponse = soaDocumentManagerClient.retrieveDocumentMetaContent(customerToRetrieveDocMetContentReq(customer), contactPoint, retrieveServiceRequest, securityHeaderType);

            RecordDocumentMetaContentRequest recordDocumentMetaContentRequest = recordDocumentMetaContentRequestBuilder.convert(customer, retrieveDocumentMetaContentResponse);
            RecordDocumentMetaContentResponse recordDocumentMetaContentResponse = soaDocumentManagerClient.recordDocumentMetaContent(recordDocumentMetaContentRequest, contactPoint, recordServiceRequest, securityHeaderType);
            if (null != recordDocumentMetaContentResponse.getResponseHeader().getResultConditions() && StringUtils.isNumeric(recordDocumentMetaContentResponse.getResponseHeader().getResultConditions().getSeverityCode()) && Integer.valueOf(recordDocumentMetaContentResponse.getResponseHeader().getResultConditions().getSeverityCode()) > 0) {
                LOGGER.info("RecordDocumentMetaContent Error Detail :ErrorCode | ErrorReason: " + recordDocumentMetaContentResponse.getResponseHeader().getResultConditions().getReasonCode() + " | " + recordDocumentMetaContentResponse.getResponseHeader().getResultConditions().getReasonText());
            }
        } catch (WebServiceException | ErrorInfo ex) {
            LOGGER.info("Exception occurred while calling Document Manager Service api.", ex);
        }

        productArrangement.setApplicationSubStatus(applicationDetails.getApplicationSubStatus());
        LOGGER.info("Exiting RecordDocMetaContent");
    }

    public RetrieveDocumentMetaContentRequest customerToRetrieveDocMetContentReq(Customer customer) {
        RetrieveDocumentMetaContentRequest retrieveDocumentMetaContentRequest = new RetrieveDocumentMetaContentRequest();
        Individual individual = new Individual();
        ObjectReference objectReference = new ObjectReference();
        if (customer != null && !StringUtils.isEmpty(customer.getCustomerIdentifier())) {
            objectReference.setIdentifier(customer.getCustomerIdentifier());

        }
        individual.setObjectReference(objectReference);
        retrieveDocumentMetaContentRequest.setInvolvedParty(individual);
        com.lloydsbanking.salsa.soap.soa.dm.esb.ifwxml.RequestHeader header = new com.lloydsbanking.salsa.soap.soa.dm.esb.ifwxml.RequestHeader();
        header.setDatasourceName(DATA_SOURCE_NAME);
        retrieveDocumentMetaContentRequest.setRequestHeader(header);
        return retrieveDocumentMetaContentRequest;
    }
}

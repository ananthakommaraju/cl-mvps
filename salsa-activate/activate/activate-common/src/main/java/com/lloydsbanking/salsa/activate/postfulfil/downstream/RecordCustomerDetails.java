package com.lloydsbanking.salsa.activate.postfulfil.downstream;

import com.lloydsbanking.salsa.activate.constants.ActivateCommonConstant;
import com.lloydsbanking.salsa.activate.helper.ApplicationDetails;
import com.lloydsbanking.salsa.activate.helper.UpdateDepositArrangementConditionAndApplicationStatusHelper;
import com.lloydsbanking.salsa.activate.postfulfil.convert.RecordInvolvedPartyDetailsRequestFactory;
import com.lloydsbanking.salsa.activate.postfulfil.convert.RetrieveInvolvedPartyDetailsRequestFactory;
import com.lloydsbanking.salsa.constant.ApplicationStatus;
import com.lloydsbanking.salsa.downstream.soaipm.client.involvedpartymanager.IPMClient;
import com.lloydsbanking.salsa.header.gmo.HeaderRetriever;
import com.lloydsbanking.salsa.logging.application.CustomerTraceLog;
import com.lloydsbanking.salsa.logging.application.TraceLogUtility;
import com.lloydsbanking.salsa.soap.soa.involvedpartymanagement.RecordInvolvedPartyDetailsRequest;
import com.lloydsbanking.salsa.soap.soa.involvedpartymanagement.RecordInvolvedPartyDetailsResponse;
import com.lloydsbanking.salsa.soap.soa.involvedpartymanagement.RetrieveInvolvedPartyDetailsRequest;
import com.lloydsbanking.salsa.soap.soa.involvedpartymanagement.RetrieveInvolvedPartyDetailsResponse;
import com.lloydsbanking.salsa.soap.soa.ipm.esb.ifwxml.ResponseHeader;
import com.lloydsbanking.salsa.soap.soa.ipm.esb.ifwxml.ResultCondition;
import com.lloydstsb.schema.infrastructure.soap.ContactPoint;
import com.lloydstsb.schema.infrastructure.soap.ServiceRequest;
import com.lloydstsb.schema.involvedpartymanagement.ifw.ErrorInfo;
import lib_sim_bo.businessobjects.Customer;
import lib_sim_bo.businessobjects.ProductArrangement;
import lib_sim_gmo.messages.RequestHeader;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.cxf.common.util.StringUtils;
import org.apache.log4j.Logger;
import org.oasis_open.docs.wss._2004._01.oasis_200401_wss_wssecurity_secext_1_0.SecurityHeaderType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.xml.ws.WebServiceException;
import java.util.Map;

@Repository
public class RecordCustomerDetails {
    private static final Logger LOGGER = Logger.getLogger(RecordCustomerDetails.class);
    private static final String DB2_TIMEOUT_HAS_OCCURRED = "1031";
    private static final String SQL_FAILURE = "1001";
    private static final String UNEXPECTED_PROGRAM_EXIT = "1011";
    private static final String RECORD_WAS_PARTIALLY_UPDATED = "1081";
    private static final String DATA_MODIFIED_BY_ANOTHER_USER = "1029";
    private static final String SEVERE_ERROR_OCCURRED_AT_OCIS_HOST = "999";
    private static final String ERROR_IN_RETRIEVE_PARTY_SERVICE = "9701";
    private static final String UNEXPECTED_ERROR = "8402";
    private static final String[] RECORD_INVOLVED_PARTY_EXTERNAL_SERVICE_ERROR_CODES = {SEVERE_ERROR_OCCURRED_AT_OCIS_HOST, UNEXPECTED_ERROR, RECORD_WAS_PARTIALLY_UPDATED, UNEXPECTED_PROGRAM_EXIT, SQL_FAILURE, DATA_MODIFIED_BY_ANOTHER_USER, DB2_TIMEOUT_HAS_OCCURRED};
    private static final String SERVICE_NAME = "http://www.lloydstsb.com/Schema/InvolvedPartyManagement/IFW";
    private static final String RECORD_SERVICE_ACTION = "RecordCustomerDetails";
    private static final String RETRIEVE_SERVICE_ACTION = "RetrieveInvolvedParty";
    @Autowired
    RetrieveInvolvedPartyDetailsRequestFactory retrieveInvolvedPartyDetailsRequestFactory;
    @Autowired
    RecordInvolvedPartyDetailsRequestFactory recordInvolvedPartyDetailsRequestFactory;
    @Autowired
    IPMClient soaIpmClient;
    @Autowired
    HeaderRetriever headerRetriever;
    @Autowired
    UpdateDepositArrangementConditionAndApplicationStatusHelper updateAppDetails;
    @Autowired
    CustomerTraceLog customerTraceLog;
    @Autowired
    TraceLogUtility traceLogUtility;

    public void recordCustomerDetails(ProductArrangement productArrangement, RequestHeader requestHeader, ApplicationDetails applicationDetails, Map<String, String> countryCodeMap, boolean isCRSSwitch) {
        Customer customer;
        if (productArrangement.getGuardianDetails() != null && productArrangement.getGuardianDetails().getIsPlayedBy() != null) {
            customer = productArrangement.getGuardianDetails();
        } else {
            customer = productArrangement.getPrimaryInvolvedParty();
        }
        LOGGER.info("Entering RecordCustomerDetails " + customerTraceLog.getIndividualTraceEventMessage(customer.getIsPlayedBy(), "IndividualDetails ") + traceLogUtility.getTaxResidencyTraceEventMessage(customer.getTaxResidencyDetails(), "TaxResidencyDetails "));
        String countryOfBirth = null;
        if (customer.getIsPlayedBy().getCountryOfBirth() != null) {
            countryOfBirth = countryCodeMap.get(customer.getIsPlayedBy().getCountryOfBirth());
        }
        RetrieveInvolvedPartyDetailsRequest request = retrieveInvolvedPartyDetailsRequestFactory.convert(customer.getCustomerIdentifier());
        RetrieveInvolvedPartyDetailsResponse response;
        try {
            response = getInvolvedPartyDetails(request, requestHeader);
            if (response != null) {
                ResponseHeader responseHeader = response.getResponseHeader();
                if (checkSeverityCode(responseHeader)) {
                    updateAppDetails.setApplicationDetails(productArrangement.getRetryCount(), null, null, ApplicationStatus.AWAITING_FULFILMENT.getValue(), ActivateCommonConstant.AppSubStatus.CUSTOMER_DETAILS_UPDATE_FAILURE, applicationDetails);
                }
                if (!checkSeverityCode(responseHeader)) {
                    RecordInvolvedPartyDetailsRequest recordInvolvedPartyDetailsRequest = recordInvolvedPartyDetailsRequestFactory.convert(response.getInvolvedParty(), customer, countryOfBirth, isCRSSwitch);
                    RecordInvolvedPartyDetailsResponse recordInvolvedPartyDetailsResponse = getInvolvedPartyDetails(recordInvolvedPartyDetailsRequest, requestHeader);
                    if (recordInvolvedPartyDetailsResponse != null && checkSeverityCode(recordInvolvedPartyDetailsResponse.getResponseHeader())) {
                        validateInvolvedPartyResponse(recordInvolvedPartyDetailsResponse.getResponseHeader(), RECORD_INVOLVED_PARTY_EXTERNAL_SERVICE_ERROR_CODES, productArrangement.getRetryCount(), applicationDetails);
                    }
                }
            }

        } catch (WebServiceException | ErrorInfo e) {
            LOGGER.info("Exception while calling RecordCustomerDetails. Returning Resource not available error ", e);
            updateAppDetails.setApplicationDetails(productArrangement.getRetryCount(), null, null, ApplicationStatus.AWAITING_FULFILMENT.getValue(), ActivateCommonConstant.AppSubStatus.CUSTOMER_DETAILS_UPDATE_FAILURE, applicationDetails);
        }
        productArrangement.setApplicationSubStatus(applicationDetails.getApplicationSubStatus());
    }

    private boolean checkSeverityCode(ResponseHeader responseHeader) {
        if (responseHeader != null && responseHeader.getResultConditions() != null) {
            String severityCode = responseHeader.getResultConditions().getSeverityCode();
            return !StringUtils.isEmpty(severityCode) && "2".equalsIgnoreCase(severityCode) ? true : false;
        }
        return false;
    }


    private <I, O> O getInvolvedPartyDetails(I request, RequestHeader requestHeader) throws ErrorInfo {
        ContactPoint contactPoint = headerRetriever.getContactPoint(requestHeader.getLloydsHeaders());
        SecurityHeaderType securityHeaderType = headerRetriever.getSecurityHeader(requestHeader.getLloydsHeaders());
        if (request instanceof RetrieveInvolvedPartyDetailsRequest) {
            ServiceRequest serviceRequest = headerRetriever.getServiceRequest(requestHeader.getLloydsHeaders(), SERVICE_NAME, RETRIEVE_SERVICE_ACTION);
            return (O) soaIpmClient.retrieveInvolvedPartyDetails((RetrieveInvolvedPartyDetailsRequest) request, contactPoint, serviceRequest, securityHeaderType);
        } else if (request instanceof RecordInvolvedPartyDetailsRequest) {
            ServiceRequest serviceRequest = headerRetriever.getServiceRequest(requestHeader.getLloydsHeaders(), SERVICE_NAME, RECORD_SERVICE_ACTION);
            return (O) soaIpmClient.recordInvolvedPartyDetails((RecordInvolvedPartyDetailsRequest) request, contactPoint, serviceRequest, securityHeaderType);
        }
        return null;

    }

    private void validateInvolvedPartyResponse(ResponseHeader responseHeader, String[] errorCodes, Integer retryCount, ApplicationDetails applicationDetails) {
        boolean serviceErrorFlag = true;
        for (ResultCondition resultCondition : responseHeader.getResultConditions().getExtraConditions()) {
            if (!(checkReasonCode(resultCondition.getReasonCode(), errorCodes) && serviceErrorFlag)) {
                serviceErrorFlag = false;
            }
            responseHeader.getResultConditions().setReasonCode(resultCondition.getReasonCode());
            responseHeader.getResultConditions().setReasonText(resultCondition.getReasonText());
        }
        if (serviceErrorFlag) {
            updateAppDetails.setApplicationDetails(retryCount, null, null, ApplicationStatus.AWAITING_FULFILMENT.getValue(), ActivateCommonConstant.AppSubStatus.CUSTOMER_DETAILS_UPDATE_FAILURE, applicationDetails);
        }
    }

    private boolean checkReasonCode(String reasonCode, String[] errorCodes) {
        return ArrayUtils.contains(errorCodes, reasonCode) ? true : false;
    }
}

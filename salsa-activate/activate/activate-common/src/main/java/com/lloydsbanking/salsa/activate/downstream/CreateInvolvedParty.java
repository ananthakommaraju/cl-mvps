package com.lloydsbanking.salsa.activate.downstream;

import com.lloydsbanking.salsa.activate.constants.ActivateCommonConstant;
import com.lloydsbanking.salsa.activate.converter.F061RespToF062ReqConverter;
import com.lloydsbanking.salsa.activate.converter.F062RequestFactory;
import com.lloydsbanking.salsa.activate.helper.ApplicationDetails;
import com.lloydsbanking.salsa.activate.helper.UpdateDepositArrangementConditionAndApplicationStatusHelper;
import com.lloydsbanking.salsa.activate.utility.OcisErrorCodes;
import com.lloydsbanking.salsa.constant.ApplicationStatus;
import com.lloydsbanking.salsa.downstream.ocis.client.f061.F061Client;
import com.lloydsbanking.salsa.downstream.ocis.client.f062.F062Client;
import com.lloydsbanking.salsa.header.gmo.HeaderRetriever;
import com.lloydsbanking.salsa.logging.application.CustomerTraceLog;
import com.lloydsbanking.salsa.soap.ocis.f061.objects.F061Req;
import com.lloydsbanking.salsa.soap.ocis.f061.objects.F061Resp;
import com.lloydsbanking.salsa.soap.ocis.f062.objects.F062Req;
import com.lloydsbanking.salsa.soap.ocis.f062.objects.F062Resp;
import com.lloydstsb.schema.infrastructure.soap.ContactPoint;
import com.lloydstsb.schema.infrastructure.soap.ServiceRequest;
import lib_sim_bo.businessobjects.AssessmentEvidence;
import lib_sim_bo.businessobjects.Customer;
import lib_sim_gmo.messages.RequestHeader;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.oasis_open.docs.wss._2004._01.oasis_200401_wss_wssecurity_secext_1_0.SecurityHeaderType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.xml.ws.WebServiceException;

@Repository
public class CreateInvolvedParty {
    private static final Logger LOGGER = Logger.getLogger(CreateInvolvedParty.class);
    private static final String EXT_SYSY_ID = "19";
    private static final String F061_SERVICE_NAME = "http://www.lloydstsb.com/Schema/Personal/RetailParty/OCIS/F061_AddPtyDetailsPartner";
    private static final String F061_SERVICE_ACTION = "F061";
    private static final String F062_SERVICE_NAME = "http://www.lloydstsb.com/Schema/Personal/RetailParty/OCIS/F062_AddPtyDetailsPartner";
    private static final String F062_SERVICE_ACTION = "F062";
    @Autowired
    F061Client f061Client;
    @Autowired
    F062Client f062Client;
    @Autowired
    HeaderRetriever headerRetriever;
    @Autowired
    F062RequestFactory f062RequestFactory;
    @Autowired
    F061RespToF062ReqConverter f061RespToF062ReqConverter;
    @Autowired
    UpdateDepositArrangementConditionAndApplicationStatusHelper updateAppDetails;
    @Autowired
    CustomerTraceLog customerTraceLog;

    public String create(Customer customer, String arrangementType, AssessmentEvidence assessmentEvidence, RequestHeader requestHeader, ApplicationDetails applicationDetails, Integer retryCount) {
        LOGGER.info(customerTraceLog.getCustomerTraceEventMessage(customer, "Entering CreateInvolvedParty (OCIS F062)"));
        ContactPoint contactPoint = headerRetriever.getContactPoint(requestHeader.getLloydsHeaders());
        SecurityHeaderType securityHeaderType = headerRetriever.getSecurityHeader(requestHeader.getLloydsHeaders());
        ServiceRequest serviceRequestF061 = headerRetriever.getServiceRequest(requestHeader.getLloydsHeaders(), F061_SERVICE_NAME, F061_SERVICE_ACTION);
        ServiceRequest serviceRequestF062 = headerRetriever.getServiceRequest(requestHeader.getLloydsHeaders(), F062_SERVICE_NAME, F062_SERVICE_ACTION);

        F062Req f062Req = null;

        if (!StringUtils.isEmpty(customer.getCustomerIdentifier()) && Integer.parseInt(customer.getCustomerIdentifier()) > 0) {
            F061Req f061Req = createF061Request(customer);
            F061Resp f061Resp = invokeF061(contactPoint, securityHeaderType, serviceRequestF061, f061Req);
            if (f061Resp != null) {
                f062Req = f061RespToF062ReqConverter.convert(f061Resp, assessmentEvidence, customer);
            }
        } else {
            f062Req = f062RequestFactory.convert(customer, arrangementType, assessmentEvidence);
        }

        F062Resp f062Resp = invokeF062(contactPoint, securityHeaderType, serviceRequestF062, f062Req, applicationDetails, retryCount);
        if (f062Resp != null && !isErrorScenario(f062Resp, applicationDetails, retryCount)) {
            applicationDetails.setApplicationSubStatus(null);
        }
        String custId = getCustomerIdFromF062Resp(f062Resp, customer.getCustomerIdentifier());
        LOGGER.info("Exiting CreateInvolvedParty (OCIS F062) with custId: " + custId);
        return custId;
    }

    private String getCustomerIdFromF062Resp(F062Resp f062Resp, String custId) {
        String customerId = null;
        if (custId != null) {
            customerId = custId;
        } else if (f062Resp != null) {
            customerId = String.valueOf(f062Resp.getPartyId());
        }
        return customerId;
    }

    private boolean isErrorScenario(F062Resp f062Resp, ApplicationDetails applicationDetails, Integer retryCount) {
        boolean isError = false;
        if (f062Resp.getF062Result() != null && f062Resp.getF062Result().getResultCondition() != null
                && f062Resp.getF062Result().getResultCondition().getReasonCode() != null) {
            Integer reasonCode = f062Resp.getF062Result().getResultCondition().getReasonCode();
            String reasonText = f062Resp.getF062Result().getResultCondition().getReasonText();
            if (null != reasonCode && OcisErrorCodes.isErrorFromOcis(reasonCode)) {
                isError = true;
                updateAppDetails.setApplicationDetails(retryCount, null, null, ApplicationStatus.AWAITING_FULFILMENT.getValue(), ActivateCommonConstant.AppSubStatus.UPDATE_CUSTOMER_RECORD_FAILURE, applicationDetails);
                LOGGER.info("Error from F062. Error Code : Error Text " + reasonCode + " " + reasonText);
            }
        }
        return isError;
    }

    private F061Resp invokeF061(ContactPoint contactPoint, SecurityHeaderType securityHeaderType, ServiceRequest serviceRequest, F061Req f061Req) {
        F061Resp f061Resp = null;
        try {
            f061Resp = f061Client.f061(f061Req, contactPoint, serviceRequest, securityHeaderType);
        } catch (WebServiceException | IllegalStateException e) {
            LOGGER.info("Error while calling F061 for create Involved Party: " + e);
        }
        return f061Resp;
    }

    private F062Resp invokeF062(ContactPoint contactPoint, SecurityHeaderType securityHeaderType, ServiceRequest serviceRequest, F062Req f062Req, ApplicationDetails applicationDetails, Integer retryCount) {
        F062Resp f062Resp = null;
        try {
            f062Resp = f062Client.updateCustomerRecord(f062Req, contactPoint, serviceRequest, securityHeaderType);
        } catch (WebServiceException e) {
            updateAppDetails.setApplicationDetails(retryCount, null, null, ApplicationStatus.AWAITING_FULFILMENT.getValue(), ActivateCommonConstant.AppSubStatus.UPDATE_CUSTOMER_RECORD_FAILURE, applicationDetails);
            LOGGER.info("Error while calling F061 for create Involved Party: " + e);
        }
        return f062Resp;
    }

    private F061Req createF061Request(Customer customer) {
        F061Req f061Req = new F061Req();
        f061Req.setExtSysId(Short.valueOf(EXT_SYSY_ID));
        f061Req.setPartyId(Long.valueOf(customer.getCustomerIdentifier()));
        return f061Req;
    }

}

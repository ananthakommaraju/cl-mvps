package com.lloydsbanking.salsa.activate.administer.downstream;


import com.lloydsbanking.salsa.activate.administer.convert.F425ResponseToApplicationDetailsConverter;
import com.lloydsbanking.salsa.activate.helper.ApplicationDetails;
import com.lloydsbanking.salsa.constant.ApplicationStatus;
import com.lloydsbanking.salsa.constant.ArrangementType;
import com.lloydsbanking.salsa.downstream.asm.client.f425.F425Client;
import com.lloydsbanking.salsa.header.gmo.HeaderRetriever;
import com.lloydsbanking.salsa.soap.asm.f425.objects.F425Req;
import com.lloydsbanking.salsa.soap.asm.f425.objects.F425Resp;
import com.lloydstsb.schema.infrastructure.soap.ContactPoint;
import com.lloydstsb.schema.infrastructure.soap.ServiceRequest;
import lib_sim_bo.businessobjects.Condition;
import lib_sim_bo.businessobjects.ProductArrangement;
import lib_sim_gmo.messages.RequestHeader;
import org.apache.log4j.Logger;
import org.oasis_open.docs.wss._2004._01.oasis_200401_wss_wssecurity_secext_1_0.SecurityHeaderType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.xml.ws.WebServiceException;

@Repository
public class UpdatedCreditDecisionRetriever {

    public static final String SERVICE_NAME = "http://xml.lloydsbanking.com/Schema/Enterprise/CrossProductManufacturing/ASM/F425_EnqDcsnReqPartner";
    public static final String ACTION_F425 = "F425";
    public static final String FAILED_TO_GET_REFRESHED_DECISION_ERROR = "001";
    public static final String ORGANISATION_CODE_CONNECTED_PERSONAL_ACCOUNT = "001";
    public static final int RETRY_COUNT_ONE = 1;
    private static final Logger LOGGER = Logger.getLogger(UpdatedCreditDecisionRetriever.class);
    private static final String CREDIT_SCORE_SOURCE_SYSTEM_F424_CC = "024";
    @Autowired
    F425Client f425Client;
    @Autowired
    HeaderRetriever headerRetriever;
    @Autowired
    F425ResponseToApplicationDetailsConverter applicationDetailsConverter;

    public ApplicationDetails retrieveUpdatedCreditDecision(ProductArrangement productArrangement, RequestHeader header, String creditScoreSourceSystem) {
        ApplicationDetails applicationDetails = null;
        LOGGER.info("Entering RetrieveUpdateCreditDecision with appId | productType | sourceSystem: " + productArrangement.getArrangementId() + " | " + productArrangement.getArrangementType() + " | " + creditScoreSourceSystem);
        F425Req request = createF425Request(productArrangement.getArrangementType(), productArrangement.getArrangementId(), creditScoreSourceSystem);
        try {
            F425Resp f425Response = getF425Resp(header, request);
            if (f425Response != null) {
                if (f425Response.getF425Result() != null && f425Response.getF425Result().getResultCondition() != null &&
                        f425Response.getF425Result().getResultCondition().getSeverityCode() != 0) {
                    LOGGER.info("External Service Error while calling F425, this exception is consumed");
                    applicationDetails = setAwaitingReferralProcessingStatus();
                } else {
                    applicationDetails = applicationDetailsConverter.convert(f425Response);
                    LOGGER.info("Exiting RetrieveUpdateCreditDecision with scoreResult: " + applicationDetails.getScoreResult());
                }
            }
        } catch (WebServiceException e) {
            LOGGER.info("Resource not Available Error while calling F425, this exception is consumed:", e);
            applicationDetails = setAwaitingReferralProcessingStatus();
        }
        return applicationDetails;
    }

    private F425Resp getF425Resp(RequestHeader header, F425Req request) {
        ContactPoint contactPoint = headerRetriever.getContactPoint(header.getLloydsHeaders());
        ServiceRequest serviceRequest = headerRetriever.getServiceRequest(header.getLloydsHeaders(), SERVICE_NAME, ACTION_F425);
        SecurityHeaderType securityHeaderType = headerRetriever.getSecurityHeader(header.getLloydsHeaders());
        return f425Client.f425(request, contactPoint, serviceRequest, securityHeaderType);
    }

    private ApplicationDetails setAwaitingReferralProcessingStatus() {
        ApplicationDetails applicationDetails = new ApplicationDetails();
        applicationDetails.setApplicationStatus(ApplicationStatus.AWAITING_REFERRAL_PROCESSING.getValue());
        applicationDetails.setRetryCount(RETRY_COUNT_ONE);
        applicationDetails.getConditionList().add(getAwaitingCondition());
        applicationDetails.setApiFailureFlag(true);
        return applicationDetails;
    }

    private Condition getAwaitingCondition() {
        Condition condition = new Condition();
        condition.setReasonText("Failed To get refreshed ASM Decision");
        condition.setReasonCode(FAILED_TO_GET_REFRESHED_DECISION_ERROR);
        return condition;
    }

    private F425Req createF425Request(String arrangementType, String applicationId, String creditScore) {
        F425Req request = new F425Req();
        request.setMaxRepeatGroupQy(0);
        request.setCSOrganisationCd(ORGANISATION_CODE_CONNECTED_PERSONAL_ACCOUNT);
        if (ArrangementType.CREDITCARD.getValue().equalsIgnoreCase(arrangementType)) {
            request.setCreditScoreSourceSystemCd(CREDIT_SCORE_SOURCE_SYSTEM_F424_CC);
        } else {
            request.setCreditScoreSourceSystemCd(creditScore);
        }
        request.setCreditScoreRequestNo(applicationId);
        request.setApplicationSourceCd("004");
        return request;
    }
}

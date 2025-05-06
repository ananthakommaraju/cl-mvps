package com.lloydsbanking.salsa.offer.verify.downstream;

import com.lloydsbanking.salsa.downstream.eidv.client.x711.X711Client;
import com.lloydsbanking.salsa.header.gmo.HeaderRetriever;
import com.lloydsbanking.salsa.logging.application.CustomerTraceLog;
import com.lloydsbanking.salsa.offer.exception.ExceptionUtility;
import com.lloydsbanking.salsa.offer.verify.convert.RetrieveEIDVScoreRequestFactory;
import com.lloydsbanking.salsa.offer.verify.evaluate.EidvStatusEvaluator;
import com.lloydstsb.schema.infrastructure.soap.ContactPoint;
import com.lloydstsb.schema.infrastructure.soap.ServiceRequest;
import lib_sim_bo.businessobjects.AssessmentEvidence;
import lib_sim_bo.businessobjects.Customer;
import lib_sim_bo.businessobjects.CustomerScore;
import lib_sim_bo.businessobjects.ReferralCode;
import lib_sim_gmo.exception.DataNotAvailableErrorMsg;
import lib_sim_gmo.exception.InternalServiceErrorMsg;
import lib_sim_gmo.exception.ResourceNotAvailableErrorMsg;
import lib_sim_gmo.messages.RequestHeader;
import lloydstsb.schema.personal.customer.partyidandv.IdentifyParty;
import lloydstsb.schema.personal.customer.partyidandv.IdentifyPartyResp;
import lloydstsb.schema.personal.customer.partyidandv.serviceobjects.IdentifyPartyOutput;
import lloydstsb.schema.personal.customer.partyidandv.serviceobjects.ReferralReason;
import org.apache.log4j.Logger;
import org.oasis_open.docs.wss._2004._01.oasis_200401_wss_wssecurity_secext_1_0.SecurityHeaderType;
import org.springframework.beans.factory.annotation.Autowired;

import javax.xml.ws.WebServiceException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class EidvRetriever {
    private static final Logger LOGGER = Logger.getLogger(EidvRetriever.class);

    private static final String INTERNAL_SERVICE_ERROR_CODE = "820001";

    Map<String, X711Client> x711ClientMap;

    @Autowired
    HeaderRetriever headerRetriever;
    @Autowired
    RetrieveEIDVScoreRequestFactory retrieveEIDVScoreRequestFactory;
    @Autowired
    ExceptionUtility exceptionUtility;
    @Autowired
    EidvStatusEvaluator eidvStatusEvaluator;
    @Autowired
    CustomerTraceLog customerTraceLog;

    public CustomerScore getEidvScore(Customer customer, RequestHeader requestHeader) throws InternalServiceErrorMsg, ResourceNotAvailableErrorMsg, DataNotAvailableErrorMsg {
        LOGGER.info(customerTraceLog.getCustomerTraceEventMessage(customer, "Entering RetrieveEIDVScores (EIDV X711) "));

        CustomerScore customerScore = null;
        String contactPointId = headerRetriever.getContactPoint(requestHeader).getContactPointId();
        IdentifyParty x711Req = retrieveEIDVScoreRequestFactory.create(customer, contactPointId);
        IdentifyPartyResp identifyPartyResp = retrieveX711Response(x711Req, requestHeader);
        if (isError(identifyPartyResp)) {
            throw exceptionUtility.internalServiceError(INTERNAL_SERVICE_ERROR_CODE, identifyPartyResp.getIdentifyPartyReturn().getResultCondition().getReasonCode() + " : " + identifyPartyResp.getIdentifyPartyReturn().getResultCondition().getReasonText());
        }
        if (identifyPartyResp.getIdentifyPartyReturn() != null && identifyPartyResp.getIdentifyPartyReturn().getIdentifyPartyOutput() != null) {
            IdentifyPartyOutput partyOutput = identifyPartyResp.getIdentifyPartyReturn().getIdentifyPartyOutput();
            customerScore = populateCustomerScore(partyOutput);
            List<ReferralCode> referralCodeList = new ArrayList<>();
            if (null != partyOutput.getReferralReasons() && null != partyOutput.getReferralReasons().getReferralReason()) {
                for (ReferralReason reason : partyOutput.getReferralReasons().getReferralReason()) {
                    ReferralCode referralCode = new ReferralCode();
                    referralCode.setCode(reason.getCode());
                    referralCode.setDescription(reason.getDescription());
                    referralCodeList.add(referralCode);
                }
            }
            LOGGER.info(customerTraceLog.getCustScoreTraceEventMessage(customerScore, "Customer Score from RetrieveEIDVScores (EIDV X711): "));
            eidvStatusEvaluator.evaluateEidvStatus(requestHeader.getChannelId(), customerScore, referralCodeList);
            //EIDV Referral Switch check is disabled in WPS code hence not implemented
        }
        LOGGER.info("Exiting RetrieveEIDVScores (EIDV X711)");
        return customerScore;
    }

    private CustomerScore populateCustomerScore(IdentifyPartyOutput partyOutput) {
        CustomerScore customerScore = new CustomerScore();
        customerScore.setDecisionCode(partyOutput.getTxRc());
        customerScore.setDecisionText(partyOutput.getTxDesc());
        AssessmentEvidence evidence = new AssessmentEvidence();
        if (null != partyOutput.getEvaluationEvidence()) {
            evidence.setEvidenceIdentifier(partyOutput.getEvaluationEvidence().getStrengthToken());
            evidence.setAddressStrength(partyOutput.getEvaluationEvidence().getAddressStrength());
            evidence.setIdentityStrength(partyOutput.getEvaluationEvidence().getIdentityStrength());
            customerScore.getAssessmentEvidence().add(evidence);
        }
        return customerScore;
    }

    private boolean isError(IdentifyPartyResp response) {
        if (null != response && null != response.getIdentifyPartyReturn() && null != response.getIdentifyPartyReturn().getResultCondition() &&
                response.getIdentifyPartyReturn().getResultCondition().getSeverityCode() > 1) {
            return true;
        }
        return false;
    }

    private IdentifyPartyResp retrieveX711Response(IdentifyParty x711Req, RequestHeader requestHeader) throws ResourceNotAvailableErrorMsg {
        ContactPoint contactPoint = headerRetriever.getContactPoint(requestHeader);
        ServiceRequest serviceRequest = headerRetriever.getServiceRequest(requestHeader);
        SecurityHeaderType securityHeaderType = headerRetriever.getSecurityHeader(requestHeader);
        IdentifyPartyResp identifyPartyResp;
        try {
            LOGGER.info("Calling EIDV X711 verifyInvolvePartyRole with Channel ID" + requestHeader.getChannelId());
            identifyPartyResp = x711ClientMap.get(requestHeader.getChannelId()).retrieveIdentityAndScores(x711Req, contactPoint, serviceRequest, securityHeaderType);
        } catch (WebServiceException e) {
            LOGGER.error("Exception occurred while calling X711. Returning ResourceNotAvailableError ;", e);
            throw exceptionUtility.resourceNotAvailableError(e.getMessage());
        }
        return identifyPartyResp;
    }

    public Map<String, X711Client> getX711ClientMap() {
        return x711ClientMap;
    }

    public void setX711ClientMap(Map<String, X711Client> x711ClientMap) {
        this.x711ClientMap = x711ClientMap;
    }
}
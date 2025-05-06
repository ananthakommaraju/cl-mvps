package com.lloydsbanking.salsa.apapca.service.fulfil.downstream;

import com.lloydsbanking.salsa.activate.constants.ActivateCommonConstant;
import com.lloydsbanking.salsa.activate.helper.ApplicationDetails;
import com.lloydsbanking.salsa.activate.helper.UpdateDepositArrangementConditionAndApplicationStatusHelper;
import com.lloydsbanking.salsa.apapca.service.fulfil.converter.DepositArrangementToE226Request;
import com.lloydsbanking.salsa.brand.Channel;
import com.lloydsbanking.salsa.constant.ApplicationStatus;
import com.lloydsbanking.salsa.downstream.cbs.client.e226.E226Client;
import com.lloydsbanking.salsa.header.gmo.HeaderRetriever;
import com.lloydsbanking.salsa.soap.cbs.e226.objects.E226Req;
import com.lloydsbanking.salsa.soap.cbs.e226.objects.E226Resp;
import com.lloydstsb.schema.infrastructure.soap.CBSAppGrp;
import com.lloydstsb.schema.infrastructure.soap.ContactPoint;
import com.lloydstsb.schema.infrastructure.soap.ServiceRequest;
import lib_sim_bo.businessobjects.DepositArrangement;
import lib_sim_bo.businessobjects.RuleCondition;
import lib_sim_gmo.messages.RequestHeader;
import org.apache.log4j.Logger;
import org.oasis_open.docs.wss._2004._01.oasis_200401_wss_wssecurity_secext_1_0.SecurityHeaderType;
import org.springframework.beans.factory.annotation.Autowired;

import javax.xml.ws.WebServiceException;
import java.math.BigDecimal;
import java.util.Map;

public class CBSCustDetailsTrialRetriever {
    @Autowired
    DepositArrangementToE226Request depositArrangementToE226Request;

    @Autowired
    Map<String, E226Client> cbsE226ClientMap;

    @Autowired
    HeaderRetriever headerRetriever;

    @Autowired
    UpdateDepositArrangementConditionAndApplicationStatusHelper updateDepositArrangementConditionAndApplicationStatusHelper;

    public static final int NO_ERROR = 0;

    public static final int SUCCESS = 3332;

    private static final Logger LOGGER = Logger.getLogger(CBSCustDetailsTrialRetriever.class);

    public void addDecisionTrial(DepositArrangement depositArrangement, RequestHeader header, ApplicationDetails applicationDetails) {
        LOGGER.info("Entering addDecisionTrial (E226) with CustomerNumber: " + depositArrangement.getPrimaryInvolvedParty().getCustomerNumber());
        CBSAppGrp cbsAppGrp = new CBSAppGrp();
        cbsAppGrp.setCBSApplicationGroupNumber(depositArrangement.getFinancialInstitution().getChannel());
        ContactPoint contactPoint = headerRetriever.getContactPoint(header.getLloydsHeaders());
        ServiceRequest serviceRequest = headerRetriever.getServiceRequest(header.getLloydsHeaders(), "http://www.lloydstsb.com/Schema/Personal/CurrentACandSavings/CBS/E226_AddCBSCustDecnTrailPartner", "E226");
        SecurityHeaderType securityHeaderType = headerRetriever.getSecurityHeader(header.getLloydsHeaders());
        try {
            E226Req request = depositArrangementToE226Request.getAddInterPartyRelationshipRequest(depositArrangement.getConditions(), depositArrangement.getPrimaryInvolvedParty().getCustomerNumber(), getOverdraftAmount(depositArrangement));
            E226Resp e226Resp = e226Client(headerRetriever.getBapiInformationHeader(header.getLloydsHeaders()).getBAPIHeader().getChanid()).createDecisionTrailersInCBS(request, contactPoint, serviceRequest, securityHeaderType, cbsAppGrp);
            Integer reasonCode = e226Resp.getE226Result().getResultCondition().getReasonCode();
            if (reasonCode != null && (reasonCode != NO_ERROR && reasonCode != SUCCESS)) {
                LOGGER.info("E226 Error Detail :ErrorCode | ErrorReason: " + reasonCode + " | " + e226Resp.getE226Result().getResultCondition().getReasonText());
                updateDepositArrangementConditionAndApplicationStatusHelper.setApplicationDetails(depositArrangement.getRetryCount(), ActivateCommonConstant.ApaPcaServiceConstants.E226_FAILURE_REASON_CODE, ActivateCommonConstant.ApaPcaServiceConstants.E226_FAILURE_REASON_TEXT, ApplicationStatus.AWAITING_FULFILMENT.getValue(), ActivateCommonConstant.AppSubStatus.FAILED_TO_UPDATE_DECISION_TRAILERS, applicationDetails);
            }
        } catch (WebServiceException e) {
            LOGGER.info("Exception occurred while calling E226. Catching it and updating ApplicationStatus ", e);
            updateDepositArrangementConditionAndApplicationStatusHelper.setApplicationDetails(depositArrangement.getRetryCount(), ActivateCommonConstant.ApaPcaServiceConstants.E226_FAILURE_REASON_CODE, ActivateCommonConstant.ApaPcaServiceConstants.E226_FAILURE_REASON_TEXT, ApplicationStatus.AWAITING_FULFILMENT.getValue(), ActivateCommonConstant.AppSubStatus.FAILED_TO_UPDATE_DECISION_TRAILERS, applicationDetails);
        }
        depositArrangement.setApplicationSubStatus(applicationDetails.getApplicationSubStatus());
    }

    private E226Client e226Client(String channel) {
        String brand = Channel.getBrandForChannel(Channel.fromString(channel)).asString();
        return cbsE226ClientMap.get(brand);
    }

    public Map<String, E226Client> getCbsE226ClientMap() {
        return cbsE226ClientMap;
    }

    public void setCbsE226ClientMap(Map<String, E226Client> cbsE226ClientMap) {
        this.cbsE226ClientMap = cbsE226ClientMap;
    }

    private BigDecimal getOverdraftAmount(DepositArrangement depositArrangement) {
        BigDecimal amount = BigDecimal.ZERO;
        if (depositArrangement.isIsOverdraftRequired()) {
            amount = depositArrangement.getOverdraftDetails().getAmount().getAmount();
        } else {
            for (RuleCondition ruleCondition : depositArrangement.getConditions()) {
                if ("OFFERED_OVERDRAFT_AMOUNT".equalsIgnoreCase(ruleCondition.getName())
                        && ruleCondition.getValue() != null) {
                    amount =  ruleCondition.getValue().getAmount();
                }
            }
        }
        return amount;
    }
}

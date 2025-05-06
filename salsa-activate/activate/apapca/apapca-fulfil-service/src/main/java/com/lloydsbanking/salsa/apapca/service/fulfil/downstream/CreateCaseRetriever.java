package com.lloydsbanking.salsa.apapca.service.fulfil.downstream;

import com.lloydsbanking.salsa.activate.constants.ActivateCommonConstant;
import com.lloydsbanking.salsa.activate.helper.ApplicationDetails;
import com.lloydsbanking.salsa.activate.helper.UpdateDepositArrangementConditionAndApplicationStatusHelper;
import com.lloydsbanking.salsa.apapca.service.fulfil.converter.CreateCaseRequestFactory;
import com.lloydsbanking.salsa.downstream.pega.client.PegaClient;
import com.lloydsbanking.salsa.header.gmo.HeaderRetriever;
import com.lloydsbanking.salsa.soap.pega.objects.CreateCaseRequestType;
import com.lloydsbanking.xml.schema.pega.industryaccountswitching.CreateCasePayloadResponseType;
import com.lloydstsb.schema.casetracking.ifw.GenericResponseType;
import com.lloydstsb.schema.infrastructure.soap.ContactPoint;
import com.lloydstsb.schema.infrastructure.soap.ServiceRequest;
import lib_sim_bo.businessobjects.DepositArrangement;
import lib_sim_gmo.messages.RequestHeader;
import org.apache.cxf.common.util.StringUtils;
import org.apache.log4j.Logger;
import org.oasis_open.docs.wss._2004._01.oasis_200401_wss_wssecurity_secext_1_0.SecurityHeaderType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.xml.ws.WebServiceException;

@Repository
public class CreateCaseRetriever {
    private static final Logger LOGGER = Logger.getLogger(CreateCaseRetriever.class);
    private static final String SERVICE_NAME = "CreateCaseDetails";
    private static final String ACTION = "PEGA-IAS";
    @Autowired
    PegaClient pegaClient;

    @Autowired
    HeaderRetriever headerRetriever;

    @Autowired
    CreateCaseRequestFactory createCaseRequestFactory;

    @Autowired
    UpdateDepositArrangementConditionAndApplicationStatusHelper updateDepositArrangementConditionAndApplicationStatusHelper;

    public ApplicationDetails create(DepositArrangement depositArrangement, RequestHeader requestHeader, ApplicationDetails applicationDetails) {
        LOGGER.info("Entering Create Case with appId: " + depositArrangement.getArrangementId());
        try {
            GenericResponseType response = invokeClient(requestHeader, createCaseRequestFactory.convert(depositArrangement, requestHeader));
            boolean isErrorScenario = checkErrorScenarios(response);
            if (isErrorScenario) {
                applicationDetails.getConditionList().add(updateDepositArrangementConditionAndApplicationStatusHelper.getCondition(ActivateCommonConstant.ApaPcaServiceConstants.CREATE_CASE_FAILURE_REASON_CODE, ActivateCommonConstant.ApaPcaServiceConstants.CREATE_CASE_FAILURE_REASON_TEXT));
            } else {
                if (response.getPayload() instanceof CreateCasePayloadResponseType) {
                    CreateCasePayloadResponseType createCasePayloadResponseType = (CreateCasePayloadResponseType) response.getPayload();
                    if (depositArrangement.getAccountSwitchingDetails() != null && !StringUtils.isEmpty(createCasePayloadResponseType.getInitiateSwitchIn().getCaseId())) {
                        depositArrangement.getAccountSwitchingDetails().setCaseID(createCasePayloadResponseType.getInitiateSwitchIn().getCaseId());
                        LOGGER.info("Exiting Create Case with caseId: " + createCasePayloadResponseType.getInitiateSwitchIn().getCaseId());
                    }
                }
            }
        } catch (WebServiceException e) {
            LOGGER.info("Exception occurred while calling create case. Returning ResourceNotAvailable Error ", e);
            applicationDetails.getConditionList().add(updateDepositArrangementConditionAndApplicationStatusHelper.getCondition(ActivateCommonConstant.ApaPcaServiceConstants.CREATE_CASE_FAILURE_REASON_CODE, ActivateCommonConstant.ApaPcaServiceConstants.CREATE_CASE_FAILURE_REASON_TEXT));
        }
        return applicationDetails;
    }

    private GenericResponseType invokeClient(RequestHeader requestHeader, CreateCaseRequestType createCaseRequest) {
        ContactPoint contactPoint = headerRetriever.getContactPoint(requestHeader.getLloydsHeaders());
        SecurityHeaderType securityHeaderType = headerRetriever.getSecurityHeader(requestHeader.getLloydsHeaders());
        ServiceRequest serviceRequest = headerRetriever.getServiceRequest(requestHeader.getLloydsHeaders(), SERVICE_NAME, ACTION);
        return pegaClient.createTask(createCaseRequest, contactPoint, serviceRequest, securityHeaderType);
    }

    private boolean checkErrorScenarios(GenericResponseType resp) {
        boolean isErrorScenario = false;
        if (resp != null && resp.getResponseHeader() != null && resp.getResponseHeader().getResultConditions() != null && !("0").equals(resp.getResponseHeader().getResultConditions().getSeverityCode())) {
            isErrorScenario = true;
        }
        return isErrorScenario;

    }
}

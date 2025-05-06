package com.lloydsbanking.salsa.activate.administer.downstream;

import com.lloydsbanking.salsa.activate.administer.convert.X741RequestFactory;
import com.lloydsbanking.salsa.activate.helper.ApplicationDetails;
import com.lloydsbanking.salsa.activate.utility.ExceptionUtilityActivate;
import com.lloydsbanking.salsa.constant.ApplicationStatus;
import com.lloydsbanking.salsa.downstream.tms.client.x741.X741Client;
import com.lloydsbanking.salsa.header.gmo.HeaderRetriever;
import com.lloydsbanking.salsa.logging.application.ProductArrangementTraceLog;
import com.lloydstsb.schema.enterprise.lcsm.BapiInformation;
import com.lloydstsb.schema.infrastructure.soap.ContactPoint;
import com.lloydstsb.schema.infrastructure.soap.ServiceRequest;
import com.lloydstsb.schema.personal.serviceplatform.tms.CreateTaskResponse;
import com.lloydstsb.schema.personal.serviceplatform.tms.TaskCreation;
import com.lloydstsb.schema.personal.serviceplatform.tms.TaskCreationResponse;
import lib_sim_bo.businessobjects.Condition;
import lib_sim_bo.businessobjects.ProductArrangement;
import lib_sim_gmo.messages.RequestHeader;
import lib_sim_salesprocessmanagement.ia_activateproductarrangement.ActivateProductArrangementInternalSystemErrorMsg;
import org.apache.log4j.Logger;
import org.oasis_open.docs.wss._2004._01.oasis_200401_wss_wssecurity_secext_1_0.SecurityHeaderType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.xml.ws.WebServiceException;

@Repository
public class CreateTask {
    private static final Logger LOGGER = Logger.getLogger(CreateTask.class);

    private static final String ARRANGEMENT_TYPE_LOAN_REFERRAL_AUTOMATION = "LRA";
    private static final String TASK_CREATION_FAILED_INTERNAL_SERVICE_ERROR_CODE = "820001";
    private static final String TASK_CREATION_FAILED_INTERNAL_SERVICE_ERROR_TEXT = "Error while creating TMS task for LRA";

    private static final String TASK_CREATION_FAILED_REASON_CODE = "002";
    private static final String TASK_CREATION_FAILED_REASON_TEXT = "Failed to create application task on TMS";
    private static final String TASK_CREATION_SERVICE_REQUEST_ACTION = "X741";
    private static final String TASK_CREATION_SERVICE_REQUEST_SERVICE_NAME = "http://www.lloydstsb.com/Schema/Personal/ServicePlatform/TMS/TMSRApplicationServicesPartner";

    @Autowired
    X741RequestFactory x741RequestFactory;
    @Autowired
    X741Client x741Client;
    @Autowired
    HeaderRetriever headerRetriever;
    @Autowired
    ExceptionUtilityActivate exceptionUtilityActivate;
    @Autowired
    ProductArrangementTraceLog productArrangementTraceLog;

    public String taskCreation(ProductArrangement productArrangement, RequestHeader header, ApplicationDetails applicationDetails) throws ActivateProductArrangementInternalSystemErrorMsg {
        LOGGER.info(productArrangementTraceLog.getApplicationTraceEventMessage(productArrangement, "Entering CreateTask X741"));
        TaskCreation x741Req = x741RequestFactory.convert(productArrangement);
        TaskCreationResponse x741Resp = null;
        ContactPoint contactPoint = headerRetriever.getContactPoint(header.getLloydsHeaders());
        ServiceRequest serviceRequest = headerRetriever.getServiceRequest(header.getLloydsHeaders(), TASK_CREATION_SERVICE_REQUEST_SERVICE_NAME, TASK_CREATION_SERVICE_REQUEST_ACTION);
        SecurityHeaderType securityHeaderType = headerRetriever.getSecurityHeader(header.getLloydsHeaders());
        BapiInformation bapiInformation = headerRetriever.getBapiInformationHeader(header.getLloydsHeaders());
        try {
            x741Resp = x741Client.x741(x741Req, contactPoint, serviceRequest, securityHeaderType, bapiInformation);
            if (checkErrorScenarios(x741Resp.getCreateTaskReturn())) {
                errorAction(productArrangement.getArrangementType(), productArrangement.getRetryCount(), applicationDetails, header);
            }
        } catch (WebServiceException e) {
            LOGGER.info("Exception occurred while calling X741 for Task Creation:" + e);
            errorAction(productArrangement.getArrangementType(), productArrangement.getRetryCount(), applicationDetails, header);
        }
        return getTaskId(x741Resp);
    }

    private String getTaskId(TaskCreationResponse x741Resp) {
        String taskId = null;
        if (x741Resp != null && x741Resp.getCreateTaskReturn() != null && x741Resp.getCreateTaskReturn().getTaskRoutingInformation() != null) {
            if (x741Resp.getCreateTaskReturn().getTaskRoutingInformation().getTaskId() != null) {
                taskId = String.valueOf(x741Resp.getCreateTaskReturn().getTaskRoutingInformation().getTaskId());
                LOGGER.info("Exiting CreateTask X741 with taskId: " + taskId);
            }
        }
        return taskId;
    }

    private void errorAction(String arrangementType, Integer retryCount, ApplicationDetails applicationDetails, RequestHeader header) throws ActivateProductArrangementInternalSystemErrorMsg {
        if (ARRANGEMENT_TYPE_LOAN_REFERRAL_AUTOMATION.equalsIgnoreCase(arrangementType)) {
            throw exceptionUtilityActivate.internalServiceError(TASK_CREATION_FAILED_INTERNAL_SERVICE_ERROR_CODE, TASK_CREATION_FAILED_INTERNAL_SERVICE_ERROR_TEXT, header);
        } else {
            setApplicationDetails(applicationDetails, retryCount);
        }
    }

    private boolean checkErrorScenarios(CreateTaskResponse taskResponse) throws ActivateProductArrangementInternalSystemErrorMsg {
        boolean isErrorScenario = false;
        if (taskResponse.getResultCondition().getSeverityCode() != 0) {
            LOGGER.info("X741 Error Detail : ErrorCode | ErrorReason: " + taskResponse.getResultCondition().getSeverityCode() + " | " + taskResponse.getResultCondition().getReasonText());
            isErrorScenario = true;
        } else if (taskResponse.getTaskRoutingInformation().getTaskId() == null || taskResponse.getTaskRoutingInformation().getTaskId() == 0) {
            LOGGER.info("X741 Error Detail : TASK ID Is Zero");
            isErrorScenario = true;
        }
        return isErrorScenario;
    }

    private Condition getCondition() {
        Condition condition = new Condition();
        condition.setReasonCode(TASK_CREATION_FAILED_REASON_CODE);
        condition.setReasonText(TASK_CREATION_FAILED_REASON_TEXT);
        return condition;
    }

    private void setApplicationDetails(ApplicationDetails applicationDetails, Integer retryCount) {
        applicationDetails.getConditionList().add(getCondition());
        applicationDetails.setApplicationStatus(ApplicationStatus.AWAITING_REFERRAL_PROCESSING.getValue());
        applicationDetails.setApplicationSubStatus(ApplicationStatus.ACCEPT_PND.getValue());
        if (retryCount != null) {
            applicationDetails.setRetryCount(retryCount + 1);
        } else {
            applicationDetails.setRetryCount(1);
        }
    }
}

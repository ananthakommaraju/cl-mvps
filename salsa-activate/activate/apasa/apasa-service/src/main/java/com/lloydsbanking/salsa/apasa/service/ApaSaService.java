package com.lloydsbanking.salsa.apasa.service;

import com.lloydsbanking.salsa.activate.administer.AdministerServiceCallValidator;
import com.lloydsbanking.salsa.activate.constants.ActivateCommonConstant;
import com.lloydsbanking.salsa.activate.downstream.LookUpValueRetriever;
import com.lloydsbanking.salsa.activate.registration.RegistrationService;
import com.lloydsbanking.salsa.activate.retrieve.RetrievePendingArrangementService;
import com.lloydsbanking.salsa.activate.utility.ActivateRequestValidator;
import com.lloydsbanking.salsa.activate.utility.ExceptionUtilityActivate;
import com.lloydsbanking.salsa.activate.validator.EidvStatusProcessor;
import com.lloydsbanking.salsa.apasa.logging.ApaSaLogService;
import com.lloydsbanking.salsa.apasa.service.fulfil.FulfilPendingSavingsArrangement;
import com.lloydsbanking.salsa.constant.ApplicationStatus;
import com.lloydsbanking.salsa.downstream.pam.service.UpdatePamServiceForActivateDA;
import com.lloydsbanking.salsa.header.gmo.RequestToResponseHeaderConverter;
import com.lloydsbanking.salsa.logging.application.ProductArrangementTraceLog;
import lib_sim_bo.businessobjects.DepositArrangement;
import lib_sim_bo.businessobjects.ExtraConditions;
import lib_sim_bo.businessobjects.ProductArrangement;
import lib_sim_bo.businessobjects.ResultCondition;
import lib_sim_gmo.messages.RequestHeader;
import lib_sim_salesprocessmanagement.ia_activateproductarrangement.*;
import lib_sim_salesprocessmanagement.messages.ActivateProductArrangementRequest;
import lib_sim_salesprocessmanagement.messages.ActivateProductArrangementResponse;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class ApaSaService implements IAActivateProductArrangement {
    private static final Logger LOGGER = Logger.getLogger(ApaSaService.class);

    @Autowired
    ApaSaLogService apaSaLogService;
    @Autowired
    LookUpValueRetriever lookUpValueRetriever;
    @Autowired
    RetrievePendingArrangementService retrievePendingArrangementService;
    @Autowired
    ActivateRequestValidator activateRequestValidator;
    @Autowired
    ExceptionUtilityActivate exceptionUtilityActivate;
    @Autowired
    EidvStatusProcessor eidvStatusProcessor;
    @Autowired
    RegistrationService registrationService;
    @Autowired
    AdministerServiceCallValidator administerServiceCallValidator;
    @Autowired
    FulfilPendingSavingsArrangement fulfilPendingSavingsArrangement;
    @Autowired
    RequestToResponseHeaderConverter requestToResponseHeaderConverter;
    @Autowired
    UpdatePamServiceForActivateDA updatePamServiceForActivateDA;
    @Autowired
    ProductArrangementTraceLog productArrangementTraceLog;

    @Override
    public ActivateProductArrangementResponse activateProductArrangement(final ActivateProductArrangementRequest upStreamRequest) throws ActivateProductArrangementDataNotAvailableErrorMsg, ActivateProductArrangementResourceNotAvailableErrorMsg, ActivateProductArrangementInternalSystemErrorMsg, ActivateProductArrangementExternalSystemErrorMsg, ActivateProductArrangementExternalBusinessErrorMsg {
        apaSaLogService.initialiseContext(upStreamRequest.getHeader());
        LOGGER.info(productArrangementTraceLog.getApplicationTraceEventMessage(upStreamRequest.getProductArrangement(), "Entering ActivateProductArrangement "));
        ActivateProductArrangementResponse response = new ActivateProductArrangementResponse();
        response.setHeader(requestToResponseHeaderConverter.convert(upStreamRequest.getHeader()));

        try {
            lookUpValueRetriever.retrieveChannelId(upStreamRequest.getSourceSystemIdentifier(), upStreamRequest.getHeader(), upStreamRequest.getProductArrangement().getArrangementType());
            Map<String, String> accountPurposeMap = lookUpValueRetriever.retrieveEncryptionKeyAndAccountPurposeMap(upStreamRequest.getHeader()).get(ActivateCommonConstant.PamGroupCodes.PURPOSE_OF_ACCOUNT_CODE);
            retrievePendingArrangementService.retrievePendingArrangements(upStreamRequest);
            validateApplicationRequest(upStreamRequest.getProductArrangement().getApplicationStatus(), upStreamRequest.getSourceSystemIdentifier(), upStreamRequest.getHeader());
            callIBRegistrationAndAdministerService(upStreamRequest.getProductArrangement(), upStreamRequest.getHeader(), upStreamRequest.getSourceSystemIdentifier(), response);

            if (eidvStatusProcessor.validateApplicationAndGetFulfilmentEligibilityFlag(upStreamRequest.getProductArrangement(), upStreamRequest.getHeader(),upStreamRequest.getSourceSystemIdentifier())) {
                fulfilPendingSavingsArrangement.fulfilPendingSavingsArrangement(response, accountPurposeMap, upStreamRequest);
            } else if (!ApplicationStatus.REFERRED.getValue().equalsIgnoreCase(upStreamRequest.getProductArrangement().getApplicationStatus())) {
                String operation = getUpdatePamOperation(upStreamRequest.getSourceSystemIdentifier());
                updatePamServiceForActivateDA.update(upStreamRequest.getProductArrangement(), upStreamRequest.getSourceSystemIdentifier(), operation);
                setUpdatePamResponse(upStreamRequest, response);
            } else {
                response.setProductArrangement(new DepositArrangement());
                response.getProductArrangement().setArrangementId(upStreamRequest.getProductArrangement().getArrangementId());
            }

        } catch (ActivateProductArrangementDataNotAvailableErrorMsg errorMsg) {
            LOGGER.info("Exiting activateProductArrangement service with Data Not Available Error Message Fault");
            throw errorMsg;
        } catch (ActivateProductArrangementInternalSystemErrorMsg errorMsg) {
            LOGGER.info("Exiting activateProductArrangement service with Internal Service Error Message Fault");
            throw errorMsg;
        }

        response.getProductArrangement().setArrangementType(upStreamRequest.getProductArrangement().getArrangementType());
        LOGGER.info(productArrangementTraceLog.getApplicationTraceEventMessage(upStreamRequest.getProductArrangement(), "Exiting ActivateProductArrangement "));
        apaSaLogService.clearContext();
        return response;
    }

    private String getUpdatePamOperation(String sourceId) {
        return sourceId.equals(ActivateCommonConstant.SourceSystemIdentifier.GALAXY_ONLINE_SOURCE_SYSTEM_IDENTIFIER) ? ActivateCommonConstant.Operation.SAVINGS : ActivateCommonConstant.Operation.OFFLINE;
    }

    private void setUpdatePamResponse(ActivateProductArrangementRequest upStreamRequest, ActivateProductArrangementResponse response) {
        response.setProductArrangement(new DepositArrangement());
        response.getProductArrangement().setArrangementId(upStreamRequest.getProductArrangement().getArrangementId());
        response.getProductArrangement().setApplicationStatus(upStreamRequest.getProductArrangement().getApplicationStatus());
        response.getProductArrangement().setAccountNumber(upStreamRequest.getProductArrangement().getAccountNumber());
        response.getProductArrangement().setFinancialInstitution(upStreamRequest.getProductArrangement().getFinancialInstitution());
    }

    private void callIBRegistrationAndAdministerService(ProductArrangement productArrangement, RequestHeader header, String sourceId, ActivateProductArrangementResponse response) throws ActivateProductArrangementInternalSystemErrorMsg, ActivateProductArrangementExternalBusinessErrorMsg, ActivateProductArrangementExternalSystemErrorMsg, ActivateProductArrangementResourceNotAvailableErrorMsg, ActivateProductArrangementDataNotAvailableErrorMsg {
        String applicationType = productArrangement.getApplicationType();
        if (ActivateCommonConstant.ApplicationType.NEW.equals(applicationType) || ActivateCommonConstant.ApplicationType.JOINT.equals(applicationType)) {
            registrationService.serviceCallForIBRegistration(header, productArrangement);
            ExtraConditions extraConditions = administerServiceCallValidator.checkApplicationStatusAndCallAdministerService(productArrangement, header, sourceId);
            if (extraConditions != null) {
                ResultCondition resultCondition = new ResultCondition();
                resultCondition.setExtraConditions(extraConditions);
                response.setResultCondition(resultCondition);
            }
        } else if (!ActivateCommonConstant.ApplicationType.TRADE.equals(applicationType)) {
            LOGGER.error("The Product Eligibility Type of the Application is Invalid. Returning internal service error.");
            throw exceptionUtilityActivate.internalServiceError("820001", "The Product Eligibility Type of the Application is Invalid", header);
        }
    }

    private void validateApplicationRequest(String appStatus, String sourceSystemIdentifier, RequestHeader requestHeader) throws ActivateProductArrangementInternalSystemErrorMsg {
        boolean requestValid = activateRequestValidator.validateRequest(appStatus, sourceSystemIdentifier);
        if (!requestValid) {
            throw exceptionUtilityActivate.internalServiceError("810002", "Application State and Source System Identifier Combination is Invalid", requestHeader);
        }
    }

}
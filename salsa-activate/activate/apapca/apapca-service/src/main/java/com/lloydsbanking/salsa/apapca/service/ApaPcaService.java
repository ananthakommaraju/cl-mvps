package com.lloydsbanking.salsa.apapca.service;

import com.lloydsbanking.salsa.activate.administer.AdministerServiceCallValidator;
import com.lloydsbanking.salsa.activate.constants.ActivateCommonConstant;
import com.lloydsbanking.salsa.activate.downstream.EncryptDataRetriever;
import com.lloydsbanking.salsa.activate.downstream.LookUpValueRetriever;
import com.lloydsbanking.salsa.activate.registration.RegistrationService;
import com.lloydsbanking.salsa.activate.retrieve.RetrievePendingArrangementService;
import com.lloydsbanking.salsa.activate.utility.ActivateRequestValidator;
import com.lloydsbanking.salsa.activate.utility.ExceptionUtilityActivate;
import com.lloydsbanking.salsa.activate.validator.EidvStatusProcessor;
import com.lloydsbanking.salsa.apapca.logging.ApaPcaLogService;
import com.lloydsbanking.salsa.apapca.service.downstream.GetSortCodeByCoordinatesRetriever;
import com.lloydsbanking.salsa.apapca.service.fulfil.FulfillPendingBankAccountArrangement;
import com.lloydsbanking.salsa.apapca.service.propose.ProposedProductArrangementService;
import com.lloydsbanking.salsa.constant.ApplicationStatus;
import com.lloydsbanking.salsa.downstream.pam.service.UpdatePamServiceForActivateDA;
import com.lloydsbanking.salsa.header.gmo.HeaderRetriever;
import com.lloydsbanking.salsa.header.gmo.RequestToResponseHeaderConverter;
import com.lloydsbanking.salsa.logging.application.ProductArrangementTraceLog;
import lib_sim_bo.businessobjects.*;
import lib_sim_gmo.messages.RequestHeader;
import lib_sim_salesprocessmanagement.ia_activateproductarrangement.*;
import lib_sim_salesprocessmanagement.messages.ActivateProductArrangementRequest;
import lib_sim_salesprocessmanagement.messages.ActivateProductArrangementResponse;
import org.apache.cxf.common.util.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
public class ApaPcaService implements IAActivateProductArrangement {
    private static final Logger LOGGER = Logger.getLogger(ApaPcaService.class);
    @Autowired
    ApaPcaLogService apaPcaLogService;
    @Autowired
    HeaderRetriever headerRetriever;
    @Autowired
    ExceptionUtilityActivate exceptionUtilityActivate;
    @Autowired
    LookUpValueRetriever lookUpValueRetriever;
    @Autowired
    ActivateRequestValidator activateRequestValidator;
    @Autowired
    ProposedProductArrangementService proposedProductArrangementService;
    @Autowired
    RetrievePendingArrangementService retrievePendingArrangementService;
    @Autowired
    FulfillPendingBankAccountArrangement fulfillPendingBankAccountArrangement;
    @Autowired
    GetSortCodeByCoordinatesRetriever generateSortCodeByCoordinates;
    @Autowired
    EncryptDataRetriever encryptDataRetriever;
    @Autowired
    RegistrationService registrationService;
    @Autowired
    EidvStatusProcessor eidvStatusProcessor;
    @Autowired
    AdministerServiceCallValidator administerServiceCallValidator;
    @Autowired
    RequestToResponseHeaderConverter requestToResponseHeaderConverter;
    @Autowired
    UpdatePamServiceForActivateDA updatePamServiceForActivateDA;
    @Autowired
    ProductArrangementTraceLog productArrangementTraceLog;

    @Override
    public ActivateProductArrangementResponse activateProductArrangement(final ActivateProductArrangementRequest upStreamRequest) throws ActivateProductArrangementDataNotAvailableErrorMsg, ActivateProductArrangementInternalSystemErrorMsg, ActivateProductArrangementExternalBusinessErrorMsg, ActivateProductArrangementResourceNotAvailableErrorMsg, ActivateProductArrangementExternalSystemErrorMsg {
        apaPcaLogService.initialiseContext(upStreamRequest.getHeader());
        LOGGER.info(productArrangementTraceLog.getApplicationTraceEventMessage(upStreamRequest.getProductArrangement(), "Entering ActivateProductArrangement "));
        ActivateProductArrangementResponse response = new ActivateProductArrangementResponse();
        response.setHeader(requestToResponseHeaderConverter.convert(upStreamRequest.getHeader()));
        Boolean isPCAReEngineering;

        try {
            lookUpValueRetriever.retrieveChannelId(upStreamRequest.getSourceSystemIdentifier(), upStreamRequest.getHeader(), upStreamRequest.getProductArrangement().getArrangementType());
            Map<String, String> accountPurposeMap = lookUpValueRetriever.retrieveEncryptionKeyAndAccountPurposeMap(upStreamRequest.getHeader()).get(ActivateCommonConstant.PamGroupCodes.PURPOSE_OF_ACCOUNT_CODE);
            isPCAReEngineering = retrievePendingArrangementService.retrievePendingArrangements(upStreamRequest);
            validateApplicationRequest(upStreamRequest.getProductArrangement().getApplicationStatus(), upStreamRequest.getSourceSystemIdentifier(), upStreamRequest.getHeader());
            if (isPCAReEngineering) {
                setSortCodeAndEncryptData(upStreamRequest.getProductArrangement(), upStreamRequest.getHeader(), accountPurposeMap);
            }
            callIBRegistrationAndAdministerService(upStreamRequest.getProductArrangement(), upStreamRequest.getHeader(), upStreamRequest.getSourceSystemIdentifier(), response);
            boolean isEligibleForFulfilment = proposedProductArrangementService.callProposeProductArrangementAndSetAccountNumber(upStreamRequest, response);
            if (isEligibleForFulfilment) {
                isEligibleForFulfilment = eidvStatusProcessor.validateApplicationAndGetFulfilmentEligibilityFlag(upStreamRequest.getProductArrangement(), upStreamRequest.getHeader(),upStreamRequest.getSourceSystemIdentifier());
            }

            if (isEligibleForFulfilment) {
                fulfillPendingBankAccountArrangement.fulfillPendingBankAccountArrangement(response, upStreamRequest, accountPurposeMap);
            } else if (!ApplicationStatus.REFERRED.getValue().equalsIgnoreCase(upStreamRequest.getProductArrangement().getApplicationStatus())) {
                String operation = getUpdatePamOperation(upStreamRequest.getSourceSystemIdentifier());
                updatePamServiceForActivateDA.update(upStreamRequest.getProductArrangement(), upStreamRequest.getSourceSystemIdentifier(), operation);
                setUpdatePamResponse(upStreamRequest, response);
            } else {
                response.setProductArrangement(new DepositArrangement());
                response.getProductArrangement().setArrangementId(upStreamRequest.getProductArrangement().getArrangementId());
                response.getProductArrangement().setApplicationStatus(upStreamRequest.getProductArrangement().getApplicationStatus());
            }

        } catch (ActivateProductArrangementDataNotAvailableErrorMsg errorMsg) {
            LOGGER.info("Exiting activateProductArrangement service with Data Not Available Error Message Fault");
            throw exceptionUtilityActivate.dataNotAvailableError(null, null, "Data Not Available Error" + errorMsg, upStreamRequest.getHeader());
        } catch (ActivateProductArrangementExternalSystemErrorMsg errorMsg) {
            LOGGER.info("Exiting activateProductArrangement service with External System Error Message Fault");
            throw errorMsg;
        }

        response.getProductArrangement().setArrangementType(upStreamRequest.getProductArrangement().getArrangementType());
        LOGGER.info(productArrangementTraceLog.getApplicationTraceEventMessage(upStreamRequest.getProductArrangement(), "Exiting ActivateProductArrangement "));
        apaPcaLogService.clearContext();
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

    private void setSortCodeAndEncryptData(ProductArrangement productArrangement, RequestHeader requestHeader, Map<String, String> accountPurposeMap) {
        DepositArrangement depositArrangement = (DepositArrangement) productArrangement;
        String latitude = null;
        String longitude = null;

        if (productArrangement.getPrimaryInvolvedParty().getIsPlayedBy() != null
                && productArrangement.getPrimaryInvolvedParty().getIsPlayedBy().getCustomerLocation() != null) {
            latitude = productArrangement.getPrimaryInvolvedParty().getIsPlayedBy().getCustomerLocation().getLatitude();
            longitude = productArrangement.getPrimaryInvolvedParty().getIsPlayedBy().getCustomerLocation().getLongitude();
        }
        if (isCallH071(productArrangement)) {
            productArrangement.getFinancialInstitution().getHasOrganisationUnits().get(0).setSortCode(generateSortCodeByCoordinates.getSortCode(latitude, longitude, requestHeader));
        }
        if (depositArrangement.getAccountSwitchingDetails() != null) {
            try {
                List<String> creditCardNumbers = encryptDataRetriever.retrieveEncryptCardNumber(requestHeader, Arrays.asList(depositArrangement.getAccountSwitchingDetails().getCardNumber()), accountPurposeMap.get(ActivateCommonConstant.PamGroupCodes.ENCRYPT_KEY_GROUP_CODE));
                if (!StringUtils.isEmpty(creditCardNumbers)) {
                    depositArrangement.getAccountSwitchingDetails().setCardNumber(creditCardNumbers.get(0));
                } else {
                    depositArrangement.getAccountSwitchingDetails().setCardNumber(null);
                }
            } catch (ActivateProductArrangementResourceNotAvailableErrorMsg resourceNotAvailableErrorMsg) {
                LOGGER.info("Error while calling retrieve encrypt data and the error is consumed " + resourceNotAvailableErrorMsg);
                depositArrangement.getAccountSwitchingDetails().setCardNumber(null);
            }
            updatePamServiceForActivateDA.updateSwitchingDetailsInPam(depositArrangement.getAccountSwitchingDetails(), depositArrangement.getArrangementId());
        }
    }

    private boolean isCallH071(ProductArrangement productArrangement) {
        boolean callH071 = false;
        if (productArrangement.getFinancialInstitution() != null && productArrangement.getFinancialInstitution().getHasOrganisationUnits() != null && !productArrangement.getFinancialInstitution().getHasOrganisationUnits().isEmpty()) {
            if (StringUtils.isEmpty(productArrangement.getFinancialInstitution().getHasOrganisationUnits().get(0).getSortCode())) {
                callH071 = true;
            }
        } else {
            callH071 = true;
            productArrangement.getFinancialInstitution().getHasOrganisationUnits().add(new OrganisationUnit());
        }
        return callH071;
    }
}
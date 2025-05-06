package com.lloydsbanking.salsa.apacc.service;

import com.lloydsbanking.salsa.activate.administer.AdministerServiceCallValidator;
import com.lloydsbanking.salsa.activate.constants.ActivateCommonConstant;
import com.lloydsbanking.salsa.activate.downstream.EncryptDataRetriever;
import com.lloydsbanking.salsa.activate.downstream.LookUpValueRetriever;
import com.lloydsbanking.salsa.activate.registration.RegistrationService;
import com.lloydsbanking.salsa.activate.retrieve.RetrievePendingArrangementService;
import com.lloydsbanking.salsa.activate.utility.ActivateRequestValidator;
import com.lloydsbanking.salsa.activate.utility.ExceptionUtilityActivate;
import com.lloydsbanking.salsa.activate.validator.EidvStatusProcessor;
import com.lloydsbanking.salsa.apacc.logging.ApaCcLogService;
import com.lloydsbanking.salsa.apacc.service.fulfil.FulfilPendingCreditCardArrangement;
import com.lloydsbanking.salsa.constant.ApplicationStatus;
import com.lloydsbanking.salsa.downstream.pam.service.UpdatePamServiceForActivateDA;
import com.lloydsbanking.salsa.downstream.pam.service.UpdatePamServiceForActivateFSA;
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
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class ApaCcService implements IAActivateProductArrangement {
    private static final Logger LOGGER = Logger.getLogger(ApaCcService.class);
    @Autowired
    ApaCcLogService apaCcLogService;
    @Autowired
    LookUpValueRetriever lookUpValueRetriever;
    @Autowired
    EncryptDataRetriever encryptDataRetriever;
    @Autowired
    RetrievePendingArrangementService retrievePendingArrangementService;
    @Autowired
    ActivateRequestValidator activateRequestValidator;
    @Autowired
    ExceptionUtilityActivate exceptionUtilityActivate;
    @Autowired
    FulfilPendingCreditCardArrangement fulfilPendingCreditCardArrangement;
    @Autowired
    RegistrationService registrationService;
    @Autowired
    EidvStatusProcessor eidvStatusProcessor;
    @Autowired
    AdministerServiceCallValidator administerServiceCallValidator;
    @Autowired
    RequestToResponseHeaderConverter requestToResponseHeaderConverter;
    @Autowired
    UpdatePamServiceForActivateFSA updatePamServiceForActivateFSA;
    @Autowired
    UpdatePamServiceForActivateDA updatePamDetailsForActivateDA;
    @Autowired
    ProductArrangementTraceLog productArrangementTraceLog;

    @Override
    public ActivateProductArrangementResponse activateProductArrangement(final ActivateProductArrangementRequest upStreamRequest) throws ActivateProductArrangementDataNotAvailableErrorMsg, ActivateProductArrangementInternalSystemErrorMsg, ActivateProductArrangementExternalBusinessErrorMsg, ActivateProductArrangementResourceNotAvailableErrorMsg, ActivateProductArrangementExternalSystemErrorMsg {
        apaCcLogService.initialiseContext(upStreamRequest.getHeader());
        LOGGER.info(productArrangementTraceLog.getApplicationTraceEventMessage(upStreamRequest.getProductArrangement(), "Entering ActivateProductArrangement "));
        ActivateProductArrangementResponse response = new ActivateProductArrangementResponse();
        response.setHeader(requestToResponseHeaderConverter.convert(upStreamRequest.getHeader()));
        response.setProductArrangement(new FinanceServiceArrangement());

        try {
            lookUpValueRetriever.retrieveChannelId(upStreamRequest.getSourceSystemIdentifier(), upStreamRequest.getHeader(), upStreamRequest.getProductArrangement().getArrangementType());
            Map<String, String> encryptionKeyMap = lookUpValueRetriever.retrieveEncryptionKeyAndAccountPurposeMap(upStreamRequest.getHeader()).get(ActivateCommonConstant.PamGroupCodes.ENCRYPT_KEY_GROUP_CODE);
            retrievePendingArrangementService.retrievePendingArrangements(upStreamRequest);
            validateApplicationRequest(upStreamRequest.getProductArrangement().getApplicationStatus(), upStreamRequest.getSourceSystemIdentifier(), upStreamRequest.getHeader());
            FinanceServiceArrangement financeServiceArrangement = (FinanceServiceArrangement) upStreamRequest.getProductArrangement();
            if ((ActivateCommonConstant.SourceSystemIdentifier.GALAXY_ONLINE_SOURCE_SYSTEM_IDENTIFIER.equals(upStreamRequest.getSourceSystemIdentifier()) || ActivateCommonConstant.SourceSystemIdentifier.GALAXY_OFFLINE_SOURCE_SYSTEM_IDENTIFIER.equals(upStreamRequest.getSourceSystemIdentifier())) && !financeServiceArrangement.getBalanceTransfer().isEmpty()) {
                callEncryptData(upStreamRequest.getHeader(), getCreditCardNumbers(financeServiceArrangement.getBalanceTransfer()), encryptionKeyMap.get(ActivateCommonConstant.PamGroupCodes.ENCRYPT_KEY_GROUP_CODE), financeServiceArrangement);
            }
            callIBRegistrationAndAdministerService(upStreamRequest.getProductArrangement(), upStreamRequest.getHeader(), upStreamRequest.getSourceSystemIdentifier(), response);
            boolean isEligibleForFulfilment = eidvStatusProcessor.validateApplicationAndGetFulfilmentEligibilityFlag(upStreamRequest.getProductArrangement(), upStreamRequest.getHeader(),upStreamRequest.getSourceSystemIdentifier());

            if (isEligibleForFulfilment) {
                setAssociatedProductProductOffer(upStreamRequest, financeServiceArrangement);
                fulfilPendingCreditCardArrangement.fulfilPendingCreditCardArrangement(upStreamRequest, response, encryptionKeyMap);
            } else if (!ApplicationStatus.REFERRED.getValue().equalsIgnoreCase(upStreamRequest.getProductArrangement().getApplicationStatus())) {
                String sourceSystemIdentifier = upStreamRequest.getSourceSystemIdentifier();
                if (sourceSystemIdentifier.equalsIgnoreCase(ActivateCommonConstant.SourceSystemIdentifier.GALAXY_ONLINE_SOURCE_SYSTEM_IDENTIFIER) || sourceSystemIdentifier.equalsIgnoreCase(ActivateCommonConstant.SourceSystemIdentifier.GALAXY_OFFLINE_SOURCE_SYSTEM_IDENTIFIER)) {
                    updatePamServiceForActivateFSA.update(upStreamRequest.getProductArrangement(), upStreamRequest.getSourceSystemIdentifier());
                } else {
                    updatePamDetailsForActivateDA.update(upStreamRequest.getProductArrangement(), upStreamRequest.getSourceSystemIdentifier(), ActivateCommonConstant.Operation.OFFLINE);
                }
                response.getProductArrangement().setArrangementId(upStreamRequest.getProductArrangement().getArrangementId());
                response.getProductArrangement().setApplicationStatus(upStreamRequest.getProductArrangement().getApplicationStatus());
            } else {
                response.setProductArrangement(new FinanceServiceArrangement());
                response.getProductArrangement().setArrangementId(upStreamRequest.getProductArrangement().getArrangementId());
            }

        } catch (ActivateProductArrangementDataNotAvailableErrorMsg errorMsg) {
            LOGGER.info("Exiting activateProductArrangement service with Data Not Available Error Message Fault");
            throw errorMsg;
        }

        response.getProductArrangement().setArrangementType(upStreamRequest.getProductArrangement().getArrangementType());
        LOGGER.info(productArrangementTraceLog.getApplicationTraceEventMessage(upStreamRequest.getProductArrangement(), "Exiting ActivateProductArrangement "));
        apaCcLogService.clearContext();
        return response;
    }

    private void setAssociatedProductProductOffer(ActivateProductArrangementRequest upStreamRequest, FinanceServiceArrangement financeServiceArrangement) {
        List<Product> offeredProduct = financeServiceArrangement.getOfferedProducts();
        if (ActivateCommonConstant.SourceSystemIdentifier.GALAXY_ONLINE_SOURCE_SYSTEM_IDENTIFIER.equals(upStreamRequest.getSourceSystemIdentifier()) || ActivateCommonConstant.SourceSystemIdentifier.GALAXY_OFFLINE_SOURCE_SYSTEM_IDENTIFIER.equals(upStreamRequest.getSourceSystemIdentifier())) {
            for (Product product : offeredProduct) {
                if (!CollectionUtils.isEmpty(product.getProductoffer())) {
                    if (financeServiceArrangement.getAssociatedProduct().getProductoffer().get(0).getProdOfferIdentifier().equals(product.getProductoffer().get(0).getProdOfferIdentifier())) {
                        addProductOffer(financeServiceArrangement, product);

                        break;
                    }
                }
            }
        } else {
            if (!CollectionUtils.isEmpty(offeredProduct)) {
                for (Product product : offeredProduct) {
                    if ("isAccepted".equalsIgnoreCase(product.getStatusCode())) {
                        addProductOffer(financeServiceArrangement, product);
                        break;
                    }
                }
            }
        }
    }

    private void addProductOffer(FinanceServiceArrangement financeServiceArrangement, Product product) {
        financeServiceArrangement.getAssociatedProduct().getProductoffer().clear();
        financeServiceArrangement.getAssociatedProduct().getProductoffer().add(product.getProductoffer().get(0));
    }

    private List<String> getCreditCardNumbers(List<BalanceTransfer> balanceTransfers) {
        List<String> creditCardNumbers = new ArrayList<>();
        for (BalanceTransfer balanceTransfer : balanceTransfers) {
            if (!StringUtils.isEmpty(balanceTransfer.getCreditCardNumber())) {
                balanceTransfer.setMaskedCreditCardNumber(balanceTransfer.getCreditCardNumber());
                creditCardNumbers.add(balanceTransfer.getCreditCardNumber());
            }
        }
        return creditCardNumbers;
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

    private void callEncryptData(RequestHeader requestHeader, List<String> creditCardNumbers, String encryptionKey, FinanceServiceArrangement financeServiceArrangement) {
        try {
            List<String> encryptedCardNumbers = encryptDataRetriever.retrieveEncryptCardNumber(requestHeader, creditCardNumbers, encryptionKey);
            int index = 0;
            for (String encryptedCardNumber : encryptedCardNumbers) {
                financeServiceArrangement.getBalanceTransfer().get(index).setCreditCardNumber(encryptedCardNumber);
                index++;
            }
        } catch (ActivateProductArrangementResourceNotAvailableErrorMsg resourceNotAvailableErrorMsg) {
            LOGGER.info("Error while calling retrieve encrypt data and the error is consumed " + resourceNotAvailableErrorMsg);
            for (BalanceTransfer balanceTransfer : financeServiceArrangement.getBalanceTransfer()) {
                balanceTransfer.setCreditCardNumber(null);
            }
        }
    }
}

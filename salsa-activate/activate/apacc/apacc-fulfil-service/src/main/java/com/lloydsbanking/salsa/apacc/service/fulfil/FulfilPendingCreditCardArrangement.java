package com.lloydsbanking.salsa.apacc.service.fulfil;

import com.lloydsbanking.salsa.activate.constants.ActivateCommonConstant;
import com.lloydsbanking.salsa.activate.downstream.*;
import com.lloydsbanking.salsa.activate.helper.ApplicationDetails;
import com.lloydsbanking.salsa.apacc.service.fulfil.downstream.*;
import com.lloydsbanking.salsa.apacc.service.fulfil.gendoc.downstream.GenerateDocumentRetriever;
import com.lloydsbanking.salsa.constant.ApplicationStatus;
import com.lloydsbanking.salsa.downstream.pam.service.UpdatePamServiceForActivateFSA;
import lib_sim_bo.businessobjects.*;
import lib_sim_communicationmanager.messages.GenerateDocumentResponse;
import lib_sim_gmo.messages.RequestHeader;
import lib_sim_salesprocessmanagement.messages.ActivateProductArrangementRequest;
import lib_sim_salesprocessmanagement.messages.ActivateProductArrangementResponse;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Map;

@Component
public class FulfilPendingCreditCardArrangement {
    public static final String PRODUCT_SYSTEM_CODE = "00010";
    public static final int CARD_LENGTH_WITH_PREFIX = 19;
    public static final int PREFIX_SIZE = 3;
    private static final Logger LOGGER = Logger.getLogger(FulfilPendingCreditCardArrangement.class);
    @Autowired
    RetrieveProductFeatures retrieveProductFeatures;
    @Autowired
    LookUpValueRetriever lookUpValueRetriever;
    @Autowired
    AddNewProductForInvolvedParty addNewProductForInvolvedParty;
    @Autowired
    EncryptDataRetriever encryptDataRetriever;
    @Autowired
    AddOMSOffer addOMSOffer;
    @Autowired
    GenerateDocumentRetriever generateDocumentRetriever;
    @Autowired
    ValidateFulfilPendingCreditCardArrangement validateFulfilPendingCreditCardArrangement;
    @Autowired
    RetrieveNextBusinessDay retrieveNextBusinessDay;
    @Autowired
    CreateCreditCardAccountRetriever createCreditCardAccountRetriever;
    @Autowired
    CustomerSegmentCheckService customerSegmentCheckService;
    @Autowired
    IBRegistration ibRegistration;
    @Autowired
    CreateCardAndAddNewProductForInvolvedParty createCardAndAddNewProductForInvolvedParty;
    @Autowired
    UpdateMarketingPreferences updateMarketingPreferences;
    @Autowired
    CommunicateFulfilActivities communicateFulfilActivities;
    @Autowired
    UpdatePamServiceForActivateFSA updatePamServiceForActivateFSA;
    @Autowired
    JmsQueueSender jmsQueueSender;

    public void fulfilPendingCreditCardArrangement(ActivateProductArrangementRequest request, ActivateProductArrangementResponse response, Map<String, String> encryptionKeyMap) {
        LOGGER.info("Entering fulfillPendingCreditCardArrangement" + request.getProductArrangement().getArrangementId() + " | " + request.getProductArrangement().getAssociatedProduct().getProductName());
        ExtraConditions extraConditions = new ExtraConditions();
        boolean isStoreApplication = false;
        FinanceServiceArrangement financeServiceArrangement = (FinanceServiceArrangement) request.getProductArrangement();
        FinanceServiceArrangement financeServiceArrangementResponse = (FinanceServiceArrangement) response.getProductArrangement();
        ApplicationDetails applicationDetails = new ApplicationDetails();
        applicationDetails.setRetryCount(financeServiceArrangement.getRetryCount());
        retrieveProductConditions(financeServiceArrangement, applicationDetails, request.getHeader());
        createCardAndAddNewProductForInvolvedParty.cardCreationAndAddingNewProduct(request.getHeader(), financeServiceArrangement, applicationDetails, financeServiceArrangementResponse);
        if (isCreditCardAccountRequired(financeServiceArrangement, applicationDetails)) {
            createAccountRequestForAdditionalHolder(financeServiceArrangement, request.getHeader(), financeServiceArrangementResponse, applicationDetails);
        }
        if (validateFulfilPendingCreditCardArrangement.isFulfillNewApplication(financeServiceArrangement.getApplicationType()) && validateFulfilPendingCreditCardArrangement.isAddOMSRequired(!applicationDetails.isApiFailureFlag(), financeServiceArrangement.getApplicationSubStatus())) {
            if (addOMSOffer.addOMSOffers(financeServiceArrangement, request.getHeader(), applicationDetails)) {
                financeServiceArrangement.setApplicationSubStatus(applicationDetails.getApplicationSubStatus());
                isStoreApplication = true;
            }
        }
        byte[] image = null;
        if (validateFulfilPendingCreditCardArrangement.isFulfillNewApplication(financeServiceArrangement.getApplicationType()) && validateFulfilPendingCreditCardArrangement.isPreviousCallSuccessful(!applicationDetails.isApiFailureFlag(), financeServiceArrangement.getApplicationSubStatus(), ActivateCommonConstant.AppSubStatus.ACQUIRE_CALL_FAILURE)) {
            GenerateDocumentResponse generateDocumentResponse = generateDocumentRetriever.callGenerateDocumentService(financeServiceArrangement, request.getHeader(), applicationDetails);
            if (!applicationDetails.isApiFailureFlag()) {
                isStoreApplication = true;
                image = generateDocumentResponse.getDocumentationItem().getDocument();
            }
        }
        callCardAcquireForMQ(financeServiceArrangement, request.getHeader().getChannelId(), applicationDetails, isStoreApplication, image);

        if (validateFulfilPendingCreditCardArrangement.isIBActivationRequired(!applicationDetails.isApiFailureFlag(), isStoreApplication, financeServiceArrangement.getPrimaryInvolvedParty())) {
            ibRegistration.ibRegistrationCall(request.getHeader(), financeServiceArrangement, applicationDetails);
        }
        callCustomerSegmentCheckService(!applicationDetails.isApiFailureFlag(), request.getSourceSystemIdentifier(), financeServiceArrangement, request.getHeader(), applicationDetails);
        if (validateFulfilPendingCreditCardArrangement.isPreviousCallSuccessful(!applicationDetails.isApiFailureFlag(), financeServiceArrangement.getApplicationSubStatus(), ActivateCommonConstant.AppSubStatus.MARKETING_PREF_UPDATE_FAILURE)) {
            callUpdateMarketingPreferences(request.getHeader(), financeServiceArrangement, applicationDetails);
        }
        communicateFulfilActivities.sendWelcomeEmail(!applicationDetails.isApiFailureFlag(), financeServiceArrangement, request.getHeader());
        communicateFulfilActivities.scheduleSTPSuccessSMS(!applicationDetails.isApiFailureFlag(), financeServiceArrangement, request.getHeader(), request.getSourceSystemIdentifier());
        checkBalanceTransferAndUpdateApplicationStatus(!applicationDetails.isApiFailureFlag(), request, financeServiceArrangement, applicationDetails);
        encryptDataRetriever.callEncryptData(encryptionKeyMap.get(ActivateCommonConstant.PamGroupCodes.ENCRYPT_KEY_GROUP_CODE), financeServiceArrangement, request.getHeader(), applicationDetails, extraConditions);
        updateResponseAndRequest(response, request, applicationDetails);
        updatePamServiceForActivateFSA.update(financeServiceArrangement, request.getSourceSystemIdentifier());
        LOGGER.info("Exiting fulfillPendingCreditCardArrangement" + request.getProductArrangement().getArrangementId() + " | " + request.getProductArrangement().getAssociatedProduct().getProductName());
    }

    private void updateResponseAndRequest(ActivateProductArrangementResponse response, ActivateProductArrangementRequest upStreamRequest, ApplicationDetails applicationDetails) {
        if (response.getResultCondition() == null) {
            response.setResultCondition(new ResultCondition());
        }
        if (null == response.getResultCondition().getExtraConditions()) {
            response.getResultCondition().setExtraConditions(new ExtraConditions());
        }
        response.getResultCondition().getExtraConditions().getConditions().addAll(applicationDetails.getConditionList());
        response.getProductArrangement().setApplicationStatus(applicationDetails.getApplicationStatus());
        response.getProductArrangement().setRetryCount(applicationDetails.getRetryCount());
        response.getProductArrangement().setApplicationSubStatus(applicationDetails.getApplicationSubStatus());
        upStreamRequest.getProductArrangement().setApplicationSubStatus(applicationDetails.getApplicationSubStatus());
        upStreamRequest.getProductArrangement().setRetryCount(applicationDetails.getRetryCount());
        upStreamRequest.getProductArrangement().setApplicationStatus(applicationDetails.getApplicationStatus());
    }

    private void checkBalanceTransferAndUpdateApplicationStatus(boolean isCallSuccessful, ActivateProductArrangementRequest request, FinanceServiceArrangement financeServiceArrangement, ApplicationDetails applicationDetails) {
        if (isCallSuccessful) {
            if (!CollectionUtils.isEmpty(financeServiceArrangement.getBalanceTransfer())) {
                financeServiceArrangement.setBTFulfilmentDate(retrieveNextBusinessDay.retrieveNextBusinessDay(request.getHeader(), applicationDetails).getDateNextWorking());
            } else {
                applicationDetails.setApplicationStatus(ApplicationStatus.FULFILLED.getValue());
                applicationDetails.setApplicationSubStatus(null);
            }
        }
    }

    private void createAccountRequestForAdditionalHolder(FinanceServiceArrangement financeServiceArrangement, RequestHeader requestHeader, FinanceServiceArrangement financeServiceArrangementResponse, ApplicationDetails applicationDetails) {
        com.lloydsbanking.salsa.soap.fdi.f241.objects.F241Resp f241Resp = createCreditCardAccountRetriever.createCreditCardAccount(financeServiceArrangement, requestHeader, applicationDetails);
        if (!applicationDetails.isApiFailureFlag()) {
            String cardNoF241 = f241Resp.getCardData().get(0).getCardNo();
            String cardNumber = cardNoF241.length() == CARD_LENGTH_WITH_PREFIX ? cardNoF241.substring(PREFIX_SIZE) : cardNoF241;
            financeServiceArrangement.setAddOnCreditCardNumber(cardNumber);
            LOGGER.info("Exiting CreateCardAccount (FDI F241 -AdditionalCardHolder) with CreditCardNo " + cardNumber);
        }
    }

    private void retrieveProductConditions(FinanceServiceArrangement financeServiceArrangement, ApplicationDetails applicationDetails, RequestHeader requestHeader) {
        Product product = retrieveProductFeatures.getProduct(financeServiceArrangement, applicationDetails, requestHeader);
        if (product != null) {
            financeServiceArrangement.getAssociatedProduct().getProductoffer().clear();
            financeServiceArrangement.getAssociatedProduct().getProductoffer().addAll(product.getProductoffer());
            financeServiceArrangement.getAssociatedProduct().getExternalSystemProductIdentifier().addAll(product.getExternalSystemProductIdentifier());
            for (ExtSysProdIdentifier extSysProdIdentifier : product.getExternalSystemProductIdentifier()) {
                if (extSysProdIdentifier.getSystemCode().equals(PRODUCT_SYSTEM_CODE)) {
                    InstructionDetails instructionDetails = new InstructionDetails();
                    instructionDetails.setInstructionMnemonic(extSysProdIdentifier.getProductIdentifier());
                    financeServiceArrangement.getAssociatedProduct().setInstructionDetails(instructionDetails);
                }
            }
        }//not overriding subStatus as retrieveProductFeatures does not set subStatus even when it fails
    }

    private void callCustomerSegmentCheckService(boolean isCallSuccessful, String sourceSystemId, FinanceServiceArrangement financeServiceArrangement, RequestHeader requestHeader, ApplicationDetails applicationDetails) {
        if (isCallSuccessful) {
            customerSegmentCheckService.updateCustomerAndGuardianId(sourceSystemId, financeServiceArrangement, requestHeader, applicationDetails);
            if (financeServiceArrangement.getApplicationSubStatus() != null) {
                Condition condition = new Condition();
                condition.setReasonCode("009");
                condition.setReasonText("Failed to update customer record on OCIS");
                applicationDetails.getConditionList().add(customerSegmentCheckService.getCondition());
            }
        }
    }

    private boolean callUpdateMarketingPreferences(RequestHeader requestHeader, FinanceServiceArrangement financeServiceArrangement, ApplicationDetails applicationDetails) {
        updateMarketingPreferences.marketingPreferencesUpdate(requestHeader, financeServiceArrangement, applicationDetails);
        if (financeServiceArrangement.getApplicationSubStatus() != null) {
            Condition condition = new Condition();
            condition.setReasonCode("010");
            condition.setReasonText("Failed to update marketing preference on OCIS");
            applicationDetails.getConditionList().add(condition);
            return false;
        }
        return true;
    }

    private boolean isCreditCardAccountRequired(FinanceServiceArrangement financeServiceArrangement, ApplicationDetails applicationDetails) {
        return validateFulfilPendingCreditCardArrangement.isFulfillNewApplication(financeServiceArrangement.getApplicationType()) && validateFulfilPendingCreditCardArrangement.checkIfAddCardHolderFailureOrIsJointParty(financeServiceArrangement, applicationDetails);
    }

    private void callCardAcquireForMQ(FinanceServiceArrangement financeServiceArrangement, String channelId, ApplicationDetails applicationDetails, boolean isStoreApplication, byte[] image) {
        if (validateFulfilPendingCreditCardArrangement.isMQCallRequired(!applicationDetails.isApiFailureFlag(), isStoreApplication, financeServiceArrangement.getApplicationType())) {
            jmsQueueSender.send(financeServiceArrangement, channelId, image, applicationDetails);
            if (!applicationDetails.isApiFailureFlag() && ActivateCommonConstant.ApplicationType.TRADE.equalsIgnoreCase(financeServiceArrangement.getApplicationType())) {
                financeServiceArrangement.setApplicationStatus(ApplicationStatus.FULFILLED.getValue());
            }
        }

    }
}
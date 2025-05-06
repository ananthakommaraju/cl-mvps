package com.lloydsbanking.salsa.apapca.service.fulfil;

import com.lloydsbanking.salsa.activate.constants.ActivateCommonConstant;
import com.lloydsbanking.salsa.activate.downstream.CreateAccountRetriever;
import com.lloydsbanking.salsa.activate.downstream.LookUpValueRetriever;
import com.lloydsbanking.salsa.activate.downstream.RetrieveProductFeatures;
import com.lloydsbanking.salsa.activate.helper.ApplicationDetails;
import com.lloydsbanking.salsa.activate.helper.UpdateDepositArrangementConditionAndApplicationStatusHelper;
import com.lloydsbanking.salsa.activate.postfulfil.AsyncProcessFulfilmentActivitiesCaller;
import com.lloydsbanking.salsa.activate.sira.downstream.SiraRetriever;
import com.lloydsbanking.salsa.apapca.service.fulfil.downstream.CBSCustDetailsTrialRetriever;
import com.lloydsbanking.salsa.apapca.service.fulfil.downstream.CreateCaseRetriever;
import com.lloydsbanking.salsa.apapca.service.fulfil.downstream.CreateOverdraft;
import com.lloydsbanking.salsa.apapca.service.fulfil.downstream.OrderAccessItemRetriever;
import com.lloydsbanking.salsa.constant.ApplicationStatus;
import com.lloydsbanking.salsa.downstream.pam.service.UpdatePamServiceForActivateDA;
import com.lloydsbanking.salsa.logging.application.ProductArrangementTraceLog;
import lib_sim_bo.businessobjects.*;
import lib_sim_gmo.messages.RequestHeader;
import lib_sim_salesprocessmanagement.ia_activateproductarrangement.*;
import lib_sim_salesprocessmanagement.messages.ActivateProductArrangementRequest;
import lib_sim_salesprocessmanagement.messages.ActivateProductArrangementResponse;
import org.apache.cxf.common.util.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;

@Component
public class FulfillPendingBankAccountArrangement {
    private static final Logger LOGGER = Logger.getLogger(FulfillPendingBankAccountArrangement.class);
    private static final String SWITCH_SIRA_DETAILS = "SW_EnSIRAFrdChk";
    @Autowired
    LookUpValueRetriever lookUpValueRetriever;
    @Autowired
    RetrieveProductFeatures retrieveProductFeatures;
    @Autowired
    CreateAccountRetriever createAccountRetriever;
    @Autowired
    CBSCustDetailsTrialRetriever cbsCustDetailsTrialRetriever;
    @Autowired
    CreateOverdraft createOverdraft;
    @Autowired
    AsyncProcessFulfilmentActivitiesCaller asyncProcessFulfilmentActivitiesCaller;
    @Autowired
    CreateCaseRetriever createCaseRetriever;
    @Autowired
    ValidateFulfillPendingBankAccountArrangement validateFulfillPendingBankAccountArrangement;
    @Autowired
    UpdateDepositArrangementConditionAndApplicationStatusHelper updateDepositArrangementConditionAndApplicationStatusHelper;
    @Autowired
    OrderAccessItemRetriever orderAccessItemRetriever;
    @Autowired
    UpdatePamServiceForActivateDA updatePamServiceForActivateDA;
    @Autowired
    SiraRetriever siraRetriever;
    @Autowired
    ProductArrangementTraceLog productArrangementTraceLog;

    public void fulfillPendingBankAccountArrangement(ActivateProductArrangementResponse response, ActivateProductArrangementRequest upStreamRequest, Map<String, String> accountPurposeMap) throws ActivateProductArrangementDataNotAvailableErrorMsg, ActivateProductArrangementInternalSystemErrorMsg, ActivateProductArrangementExternalBusinessErrorMsg, ActivateProductArrangementExternalSystemErrorMsg, ActivateProductArrangementResourceNotAvailableErrorMsg {
        LOGGER.info("Entering fulfillPendingBankAccountArrangement" + upStreamRequest.getProductArrangement().getArrangementId() + " | " + upStreamRequest.getProductArrangement().getAssociatedProduct().getProductName()+" | "+upStreamRequest.getProductArrangement().getRetryCount());
        DepositArrangement depositArrangement = (DepositArrangement) upStreamRequest.getProductArrangement();
        assignDepositArrangement(response, depositArrangement);
        RequestHeader header = upStreamRequest.getHeader();
        ApplicationDetails applicationDetails = new ApplicationDetails();
        applicationDetails.setApplicationStatus(ApplicationStatus.AWAITING_FULFILMENT.getValue());
        applicationDetails.setRetryCount(depositArrangement.getRetryCount());
        boolean apiFailureFlag = false;
        Product product = retrieveProductFeatures.getProduct(depositArrangement, null, upStreamRequest.getHeader());
        if (null != product) {
            depositArrangement.getAssociatedProduct().getProductoffer().clear();
            depositArrangement.getAssociatedProduct().getProductoffer().addAll(product.getProductoffer());
        }
        if (!validateFulfillPendingBankAccountArrangement.checkApplicationSubStatus(depositArrangement.getApplicationSubStatus())) {
            if (validateFulfillPendingBankAccountArrangement.checkCondition(apiFailureFlag, depositArrangement.getApplicationSubStatus(), ActivateCommonConstant.AppSubStatus.CURRENT_ACCOUNT_CREATION_FAILURE)) {
                createAccountRetriever.createAccount(header, depositArrangement, product, accountPurposeMap, response, applicationDetails);
                apiFailureFlag = applicationDetails.isApiFailureFlag();
            }
            updateApplicationStatusAndIsApiFailureFlagForCreateCase(depositArrangement, header, apiFailureFlag, applicationDetails);

            if (validateFulfillPendingBankAccountArrangement.checkCondition(apiFailureFlag, depositArrangement.getApplicationSubStatus(), ActivateCommonConstant.AppSubStatus.FAILED_TO_UPDATE_DECISION_TRAILERS)) {
                cbsCustDetailsTrialRetriever.addDecisionTrial(depositArrangement, header, applicationDetails);
                apiFailureFlag = applicationDetails.isApiFailureFlag();
            }
            if (validateFulfillPendingBankAccountArrangement.checkCondition(apiFailureFlag, depositArrangement.getApplicationSubStatus(), ActivateCommonConstant.AppSubStatus.FAILED_TO_CREATE_CARD_ORDER)) {
                if (validateFulfillPendingBankAccountArrangement.isDebitCardRequired(depositArrangement.getConditions(), depositArrangement.getApplicationSubStatus())) {
                    apiFailureFlag = updateApplicationStatusAndIsApiFailureFlagForOrderAccesss(depositArrangement, header, applicationDetails);
                }
            }
            if (validateFulfillPendingBankAccountArrangement.checkCondition(apiFailureFlag, depositArrangement.getApplicationSubStatus(), ActivateCommonConstant.AppSubStatus.FAILED_TO_CREATE_OVERDRAFT) && depositArrangement.isIsOverdraftRequired()) {
                if (validateFulfillPendingBankAccountArrangement.isOverdraftRequired(depositArrangement)) {
                    createOverdraft.createAccountProcessOverdraft(depositArrangement, header, applicationDetails);
                    apiFailureFlag = applicationDetails.isApiFailureFlag();
                }
            }
            callSira(depositArrangement, apiFailureFlag, applicationDetails, header);
        }
        updateResponseAndRequest(response, upStreamRequest, applicationDetails.getConditionList(), depositArrangement.getApplicationSubStatus(), applicationDetails.getRetryCount(), applicationDetails.getApplicationStatus());
        LOGGER.info(productArrangementTraceLog.getApplicationTraceEventMessage(upStreamRequest.getProductArrangement(), "Exiting fulfillPendingBankAccountArrangement"));
        decideToTerminateFulfillOrCallPostFulfill(apiFailureFlag, upStreamRequest, response, product);
    }

    private void assignDepositArrangement(ActivateProductArrangementResponse response, DepositArrangement
            depositArrangement) {
        response.setProductArrangement(depositArrangement);
    }

    private void updateResponseAndRequest(ActivateProductArrangementResponse
                                                  response, ActivateProductArrangementRequest upStreamRequest, List<Condition> conditionList, String subStatus,
                                          Integer retryCount, String status) {
        if (response.getResultCondition() == null) {
            response.setResultCondition(new ResultCondition());
        }
        if (null == response.getResultCondition().getExtraConditions()) {
            response.getResultCondition().setExtraConditions(new ExtraConditions());
        }
        if (!CollectionUtils.isEmpty(response.getResultCondition().getExtraConditions().getConditions()) && !CollectionUtils.isEmpty(conditionList)) {
            response.getResultCondition().getExtraConditions().getConditions().clear();
        }
        response.getResultCondition().getExtraConditions().getConditions().addAll(conditionList);
        response.getProductArrangement().setApplicationSubStatus(subStatus);
        response.getProductArrangement().setRetryCount(retryCount);
        response.getProductArrangement().setApplicationStatus(status);

        upStreamRequest.getProductArrangement().setApplicationSubStatus(subStatus);
        upStreamRequest.getProductArrangement().setRetryCount(retryCount);
        upStreamRequest.getProductArrangement().setApplicationStatus(status);
    }

    private void decideToTerminateFulfillOrCallPostFulfill(
            boolean apiFailureFlag, ActivateProductArrangementRequest upStreamRequest, ActivateProductArrangementResponse
            response, Product product) throws
            ActivateProductArrangementExternalSystemErrorMsg, ActivateProductArrangementExternalBusinessErrorMsg, ActivateProductArrangementInternalSystemErrorMsg, ActivateProductArrangementResourceNotAvailableErrorMsg, ActivateProductArrangementDataNotAvailableErrorMsg {
        DepositArrangement depositArrangement = (DepositArrangement) upStreamRequest.getProductArrangement();
        if (!apiFailureFlag) {
            depositArrangement.setApplicationStatus(ApplicationStatus.FULFILLED.getValue());
            response.getProductArrangement().setApplicationStatus(ApplicationStatus.FULFILLED.getValue());
            LOGGER.info("Calling processPostFulfilmentActivities");
            if (null != product) {
                upStreamRequest.getProductArrangement().getAssociatedProduct().setInstructionDetails(product.getInstructionDetails());
            }
            asyncProcessFulfilmentActivitiesCaller.callAsyncMethod(upStreamRequest);
        } else {
            updatePamServiceForActivateDA.update(depositArrangement, upStreamRequest.getSourceSystemIdentifier(), ActivateCommonConstant.Operation.SAVINGS);
            response.getProductArrangement().setArrangementId(upStreamRequest.getProductArrangement().getArrangementId());
        }
    }

    public boolean updateApplicationStatusAndIsApiFailureFlagForOrderAccesss(DepositArrangement depositArrangement, RequestHeader header, ApplicationDetails applicationDetails) {
        orderAccessItemRetriever.orderAccessItem(depositArrangement, header, applicationDetails);
        return applicationDetails.isApiFailureFlag();
    }

    private void updateApplicationStatusAndIsApiFailureFlagForCreateCase(DepositArrangement
                                                                                 depositArrangement, RequestHeader header, boolean apiFailureFlag, ApplicationDetails applicationDetails) {
        if (!apiFailureFlag && validateFulfillPendingBankAccountArrangement.checkPCAReEngineering(depositArrangement)) {
            createCaseRetriever.create(depositArrangement, header, applicationDetails);
        }
    }

    private void callSira(DepositArrangement depositArrangement, boolean apiFailureFlag, ApplicationDetails applicationDetails, RequestHeader requestHeader) {
        boolean isSiraEnabled = false;
        if (null != depositArrangement.isSIRAEnabledSwitch()) {
            if (depositArrangement.isSIRAEnabledSwitch()) {
                isSiraEnabled = true;
            }
        } else {
            isSiraEnabled = validateFulfillPendingBankAccountArrangement.retrieveSwitchValueBranded(requestHeader.getChannelId(), SWITCH_SIRA_DETAILS);
        }

        if (isSiraEnabled && validateFulfillPendingBankAccountArrangement.checkCondition(apiFailureFlag, depositArrangement.getApplicationSubStatus(), null)) {
            boolean isPCAReEngineering = false;
            for (RuleCondition condition : depositArrangement.getConditions()) {
                if (("INTEND_TO_SWITCH").equalsIgnoreCase(condition.getName()) && !StringUtils.isEmpty(condition.getResult())) {
                    isPCAReEngineering = true;
                }
            }
            if (isPCAReEngineering) {
                siraRetriever.retrieveSiraDecision(depositArrangement, applicationDetails, requestHeader, false);
            }
        }
    }


}
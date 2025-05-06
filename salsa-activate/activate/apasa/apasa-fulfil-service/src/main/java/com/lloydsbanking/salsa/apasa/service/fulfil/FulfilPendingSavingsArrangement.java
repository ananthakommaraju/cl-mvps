package com.lloydsbanking.salsa.apasa.service.fulfil;

import com.lloydsbanking.salsa.activate.constants.ActivateCommonConstant;
import com.lloydsbanking.salsa.activate.downstream.AppGroupRetriever;
import com.lloydsbanking.salsa.activate.downstream.CreateAccountRetriever;
import com.lloydsbanking.salsa.activate.downstream.RetrieveProductFeatures;
import com.lloydsbanking.salsa.activate.helper.ApplicationDetails;
import com.lloydsbanking.salsa.activate.postfulfil.AsyncProcessFulfilmentActivitiesCaller;
import com.lloydsbanking.salsa.apasa.service.fulfil.convert.MapProductArrangementToDepositArrangement;
import com.lloydsbanking.salsa.apasa.service.fulfil.downstream.AmendRollOverAccount;
import com.lloydsbanking.salsa.apasa.service.fulfil.downstream.CreateStandingOrder;
import com.lloydsbanking.salsa.constant.ApplicationStatus;
import com.lloydsbanking.salsa.downstream.pam.service.UpdatePamServiceForActivateDA;
import com.lloydsbanking.salsa.header.gmo.HeaderRetriever;
import com.lloydsbanking.salsa.logging.application.ProductArrangementTraceLog;
import lib_sim_bo.businessobjects.*;
import lib_sim_gmo.messages.RequestHeader;
import lib_sim_salesprocessmanagement.ia_activateproductarrangement.*;
import lib_sim_salesprocessmanagement.messages.ActivateProductArrangementRequest;
import lib_sim_salesprocessmanagement.messages.ActivateProductArrangementResponse;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class FulfilPendingSavingsArrangement {
    public static final int SORT_START_INDEX = 4;
    public static final int SORT_END_INDEX = 10;
    private static final Logger LOGGER = Logger.getLogger(FulfilPendingSavingsArrangement.class);
    @Autowired
    HeaderRetriever headerRetriever;
    @Autowired
    RetrieveProductFeatures retrieveProductFeatures;
    @Autowired
    CreateAccountRetriever createAccountRetriever;
    @Autowired
    AmendRollOverAccount amendRollOverAccount;
    @Autowired
    CreateStandingOrder createStandingOrder;
    @Autowired
    AppGroupRetriever appGroupRetriever;
    @Autowired
    AsyncProcessFulfilmentActivitiesCaller asyncProcessFulfilmentActivitiesCaller;
    @Autowired
    UpdatePamServiceForActivateDA updatePamServiceForActivateDA;
    @Autowired
    MapProductArrangementToDepositArrangement mapProductArrangementToDepositArrangement;
    @Autowired
    ProductArrangementTraceLog productArrangementTraceLog;

    public void fulfilPendingSavingsArrangement(ActivateProductArrangementResponse response, Map<String, String> accountPurposeMap, ActivateProductArrangementRequest upStreamRequest) throws ActivateProductArrangementExternalSystemErrorMsg, ActivateProductArrangementExternalBusinessErrorMsg, ActivateProductArrangementInternalSystemErrorMsg, ActivateProductArrangementResourceNotAvailableErrorMsg, ActivateProductArrangementDataNotAvailableErrorMsg {
        LOGGER.info("Entering fulfillPendingSavingsAccountArrangement" + upStreamRequest.getProductArrangement().getArrangementId() + " | " + upStreamRequest.getProductArrangement().getAssociatedProduct().getProductName());
        DepositArrangement depositArrangement = (DepositArrangement) upStreamRequest.getProductArrangement();
        RequestHeader header = upStreamRequest.getHeader();
        boolean apiFailureFlag;
        ApplicationDetails applicationDetails = new ApplicationDetails();
        applicationDetails.setApplicationStatus(ApplicationStatus.AWAITING_FULFILMENT.getValue());
        applicationDetails.setRetryCount(depositArrangement.getRetryCount());
        Map<String, String> productOptionMap = new HashMap<>();
        applicationDetails.setApiFailureFlag(false);
        String cbsAppGroup = appGroupRetriever.callRetrieveCBSAppGroup(header, headerRetriever.getContactPoint(header).getContactPointId().substring(SORT_START_INDEX, SORT_END_INDEX));
        if (depositArrangement.getFinancialInstitution() == null) {
            depositArrangement.setFinancialInstitution(new Organisation());
        }
        depositArrangement.getFinancialInstitution().setChannel(cbsAppGroup);
        response.setProductArrangement(mapProductArrangementToDepositArrangement.createDepositArrangement(depositArrangement));
        response.getProductArrangement().setApplicationStatus(ApplicationStatus.AWAITING_FULFILMENT.getValue());
        Product product = retrieveProductConditions(productOptionMap, depositArrangement, applicationDetails, header);
        apiFailureFlag = applicationDetails.isApiFailureFlag();
        if (!apiFailureFlag && (depositArrangement.getApplicationSubStatus() == null || !checkApplicationSubStatus(depositArrangement.getApplicationSubStatus()))) {
            if (isCreateAccountRequired(depositArrangement.getApplicationSubStatus())) {
                callCreateAccountAndAmendRollOver(response, accountPurposeMap, product, depositArrangement, header, applicationDetails);
                apiFailureFlag = applicationDetails.isApiFailureFlag();
            }
            if (isStandingOrderRequiredAndInterestRemittanceDetailsCaptured(apiFailureFlag, depositArrangement.getApplicationSubStatus(), depositArrangement)) {
                if (!depositArrangement.getFinancialInstitution().getHasOrganisationUnits().isEmpty()) {
                    ExtraConditions extraConditions = createStandingOrder.e032CreateStandingOrder(cbsAppGroup, depositArrangement.getFinancialInstitution().getHasOrganisationUnits().get(0).getSortCode(), depositArrangement.getAccountNumber(), depositArrangement.getInterestRemittanceAccountDetails().getAccountNumber(), depositArrangement.getInterestRemittanceAccountDetails().getSortCode(), header, depositArrangement.getInterestRemittanceAccountDetails().getBeneficiaryName(), applicationDetails, depositArrangement.getRetryCount());
                    depositArrangement.setApplicationSubStatus(applicationDetails.getApplicationSubStatus());
                    apiFailureFlag = applicationDetails.isApiFailureFlag();
                    //Special case for Standing order failure
                    if (apiFailureFlag) {
                        if (extraConditions != null) {
                            setExtraConditionsInResponse(response, extraConditions.getConditions().get(0));
                        }
                        response.getProductArrangement().setApplicationStatus(ApplicationStatus.FULFILLED.getValue());
                    }
                }
            }
        }
        updateResponseAndRequest(response, upStreamRequest, depositArrangement.getApplicationSubStatus(), applicationDetails.getRetryCount(), applicationDetails.getApplicationStatus());
        LOGGER.info(productArrangementTraceLog.getApplicationTraceEventMessage(upStreamRequest.getProductArrangement(), "Exiting fulfillPendingSavingsAccountArrangement"));
        decideToTerminateFulfillOrCallPostFulfill(apiFailureFlag, depositArrangement, upStreamRequest, response, product);
    }

    private void updateResponseAndRequest(ActivateProductArrangementResponse response, ActivateProductArrangementRequest upStreamRequest, String subStatus, Integer retryCount, String status) {
        if (response.getResultCondition() == null) {
            response.setResultCondition(new ResultCondition());
        }
        response.getProductArrangement().setApplicationSubStatus(subStatus);
        response.getProductArrangement().setRetryCount(retryCount);
        if (!(ApplicationStatus.FULFILLED.getValue().equals(response.getProductArrangement().getApplicationStatus()))) {
            response.getProductArrangement().setApplicationStatus(status);
        }
        upStreamRequest.getProductArrangement().setApplicationSubStatus(subStatus);
        upStreamRequest.getProductArrangement().setRetryCount(retryCount);
        upStreamRequest.getProductArrangement().setApplicationStatus(status);
    }
    private boolean isInterestRemittanceDetailsCaptured(DepositArrangement depositArrangement) {
        return depositArrangement.getInterestRemittanceAccountDetails() != null && depositArrangement.getInterestRemittanceAccountDetails().getSortCode() != null && depositArrangement.getInterestRemittanceAccountDetails().getAccountNumber() != null;
    }
    private void callCreateAccountAndAmendRollOver(ActivateProductArrangementResponse response, Map<String, String> accountPurposeMap, Product product, DepositArrangement depositArrangement, RequestHeader header, ApplicationDetails applicationDetails) {
        Boolean isSecondaryAccount = depositArrangement.isIsSecondaryAccount();
        createAccountRetriever.createAccount(header, depositArrangement, product, accountPurposeMap, response, applicationDetails);
        if (applicationDetails.isApiFailureFlag()) {
            setExtraConditionsInResponse(response, applicationDetails.getConditionList().get(0));
        }
        if (response.getProductArrangement().getAccountNumber() != null && isSecondaryAccount != null && isSecondaryAccount) {
            ExtraConditions extraConditions = amendRollOverAccount.amendRollOverAccount(depositArrangement, header);
            if (extraConditions != null) {
                setExtraConditionsInResponse(response, extraConditions.getConditions().get(0));
            }
        }
    }

    private Product retrieveProductConditions(Map<String, String> productOptionMap, DepositArrangement depositArrangement, ApplicationDetails applicationDetails, RequestHeader header) {
        Product product = retrieveProductFeatures.getProduct(depositArrangement, applicationDetails, header);
        if (product != null) {
            for (ProductOptions productOptions : product.getProductoptions()) {
                productOptionMap.put(productOptions.getOptionsType(), productOptions.getOptionsValue());
            }
        }//not overriding subStatus as retrieveProductFeatures does not set subStatus even when it fails
        return product;
    }

    private void setExtraConditionsInResponse(ActivateProductArrangementResponse response, Condition condition) {
        if (response.getResultCondition() == null) {
            response.setResultCondition(new ResultCondition());
            response.getResultCondition().setExtraConditions(new ExtraConditions());
        }
        response.getResultCondition().getExtraConditions().getConditions().clear();
        response.getResultCondition().getExtraConditions().getConditions().add(condition);
    }

    private void decideToTerminateFulfillOrCallPostFulfill(boolean apiFailureFlag, DepositArrangement depositArrangement, ActivateProductArrangementRequest request, ActivateProductArrangementResponse response, Product product) throws ActivateProductArrangementExternalSystemErrorMsg, ActivateProductArrangementExternalBusinessErrorMsg, ActivateProductArrangementInternalSystemErrorMsg, ActivateProductArrangementResourceNotAvailableErrorMsg, ActivateProductArrangementDataNotAvailableErrorMsg {
        if (!apiFailureFlag) {
            depositArrangement.setApplicationStatus(ApplicationStatus.FULFILLED.getValue());
            response.getProductArrangement().setApplicationStatus(ApplicationStatus.FULFILLED.getValue());
            LOGGER.info("Calling processPostFulfilmentActivities");
            if (null != product) {
                request.getProductArrangement().getAssociatedProduct().setInstructionDetails(product.getInstructionDetails());
            }
            asyncProcessFulfilmentActivitiesCaller.callAsyncMethod(request);
        } else {
            updatePamServiceForActivateDA.update(depositArrangement, request.getSourceSystemIdentifier(), ActivateCommonConstant.Operation.SAVINGS);
            response.getProductArrangement().setArrangementId(depositArrangement.getArrangementId());
        }
    }

    private boolean checkApplicationSubStatus(String subStatus) {
        boolean isSubStatusMatch;
        switch (subStatus) {
            case ActivateCommonConstant.AppSubStatus.AWAITING_CRS_FULFILLMENT_FAILURE:
                isSubStatusMatch = true;
                break;
            case ActivateCommonConstant.AppSubStatus.MARKETING_PREF_UPDATE_FAILURE:
                isSubStatusMatch = true;
                break;
            case ActivateCommonConstant.AppSubStatus.PARTY_RELATIONSHIP_UPDATE_FAILURE:
                isSubStatusMatch = true;
                break;
            case ActivateCommonConstant.AppSubStatus.FAILED_TO_UPDATE_EMAIL_ADDRESS:
                isSubStatusMatch = true;
                break;
            case ActivateCommonConstant.AppSubStatus.FAILED_TO_UPDATE_NI_NUMBER:
                isSubStatusMatch = true;
                break;
            case ActivateCommonConstant.AppSubStatus.UPDATE_CUSTOMER_RECORD_FAILURE:
                isSubStatusMatch = true;
                break;
            case ActivateCommonConstant.AppSubStatus.CUSTOMER_DETAILS_UPDATE_FAILURE:
                isSubStatusMatch = true;
                break;
            default:
                isSubStatusMatch = false;
                break;
        }
        return isSubStatusMatch;
    }

    private boolean isCreateAccountRequired(String applicationSubStatus) {
        return applicationSubStatus == null || !ActivateCommonConstant.AppSubStatus.STANDING_ORDER_CREATION_FAILURE.equals(applicationSubStatus);
    }

    private boolean isStandingOrderRequiredAndInterestRemittanceDetailsCaptured(boolean apiFailureFlag, String applicationSubStatus, DepositArrangement depositArrangement) {
        return (!apiFailureFlag || ActivateCommonConstant.AppSubStatus.STANDING_ORDER_CREATION_FAILURE.equals(applicationSubStatus)) && isInterestRemittanceDetailsCaptured(depositArrangement);
    }
}
package com.lloydsbanking.salsa.activate.administer;

import com.lloydsbanking.salsa.activate.constants.ActivateCommonConstant;
import com.lloydsbanking.salsa.activate.sira.SiraReferredService;
import com.lloydsbanking.salsa.activate.utility.ExceptionUtilityActivate;
import com.lloydsbanking.salsa.constant.ApplicationStatus;
import com.lloydsbanking.salsa.constant.ArrangementType;
import com.lloydsbanking.salsa.downstream.switches.SwitchService;
import com.lloydsbanking.salsa.logging.application.ProductArrangementTraceLog;
import lib_sim_bo.businessobjects.*;
import lib_sim_gmo.messages.RequestHeader;
import lib_sim_salesprocessmanagement.ia_activateproductarrangement.*;
import org.apache.cxf.common.util.CollectionUtils;
import org.apache.cxf.common.util.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.xml.ws.WebServiceException;
import java.util.List;

@Component
public class AdministerServiceCallValidator {
    private static final Logger LOGGER = Logger.getLogger(AdministerServiceCallValidator.class);
    private static final String EIDV_STATUS_ACCEPT = "ACCEPT";
    private static final String OVERDRAFT_OFFERED_FLAG_CODE = "102";
    private static final String SIRA_REFER = "5001";
    private static final String SWITCH_SIRA_DETAILS = "SW_EnSIRAFrdChk";
    private static final String SIRA_AND_ASM_REFER="5003";
    @Autowired
    AdministerReferredService administerReferredService;
    @Autowired
    SiraReferredService siraReferredService;

    @Autowired
    ExceptionUtilityActivate exceptionUtilityActivate;
    @Autowired
    ProductArrangementTraceLog productArrangementTraceLog;
    @Autowired
    public SwitchService switchClient;

    public ExtraConditions checkApplicationStatusAndCallAdministerService(ProductArrangement productArrangement, RequestHeader requestHeader, String sourceSystemIdentifier) throws ActivateProductArrangementInternalSystemErrorMsg, ActivateProductArrangementExternalBusinessErrorMsg, ActivateProductArrangementExternalSystemErrorMsg, ActivateProductArrangementResourceNotAvailableErrorMsg, ActivateProductArrangementDataNotAvailableErrorMsg {
        ExtraConditions extraConditions = null;
        if (ApplicationStatus.UNSCORED.getValue().equalsIgnoreCase(productArrangement.getApplicationStatus())) {
            productArrangement.setApplicationStatus(ApplicationStatus.AWAITING_RESCORE.getValue());
            productArrangement.setRetryCount(productArrangement.getRetryCount() != null ? productArrangement.getRetryCount() + 1 : 1);
        } else if (!ApplicationStatus.APPROVED.getValue().equalsIgnoreCase(productArrangement.getApplicationStatus())) {
            if (ApplicationStatus.AWAITING_MANUAL_ID_V.getValue().equalsIgnoreCase(productArrangement.getApplicationStatus())) {
                if (!productArrangement.getPrimaryInvolvedParty().getCustomerScore().isEmpty()) {
                    productArrangement.getPrimaryInvolvedParty().getCustomerScore().get(0).setScoreResult(EIDV_STATUS_ACCEPT);
                    if (productArrangement.getGuardianDetails() != null && !StringUtils.isEmpty(productArrangement.getGuardianDetails().getCustomerIdentifier())) {
                        productArrangement.getGuardianDetails().getCustomerScore().get(0).setScoreResult(EIDV_STATUS_ACCEPT);
                    }
                } else {
                    LOGGER.error("Address and Identity Evidence not available in the request");
                    throw exceptionUtilityActivate.internalServiceError("820001", "Address and Identity Evidence not available in the request", requestHeader);
                }
            }
            extraConditions = new ExtraConditions();
            if (isSiraCallRequired(productArrangement.isSIRAEnabledSwitch(), productArrangement.getApplicationStatus(), productArrangement.getApplicationSubStatus(), requestHeader.getChannelId(), productArrangement.getConditions())) {
                ProductArrangement siraProductArrangementResponse = siraReferredService.siraReferredArrangement(productArrangement, sourceSystemIdentifier, extraConditions, requestHeader);
                setAdministerServiceResponse(productArrangement, siraProductArrangementResponse);
            } else {
                LOGGER.info(productArrangementTraceLog.getApplicationTraceEventMessage(productArrangement, "Entering AdministerReferredService"));
                ProductArrangement administerProductArrangementResponse = administerReferredService.administerReferredArrangement(productArrangement, sourceSystemIdentifier, extraConditions, requestHeader);
                LOGGER.info(productArrangementTraceLog.getApplicationTraceEventMessage(administerProductArrangementResponse, "Exiting AdministerReferredService with response"));
                setAdministerServiceResponse(productArrangement, administerProductArrangementResponse);
            }
        }
        return extraConditions;
    }

    private void setAdministerServiceResponse(ProductArrangement productArrangement, ProductArrangement administerProductArrangementResponse) {
        productArrangement.getReferral().clear();
        productArrangement.getReferral().addAll(administerProductArrangementResponse.getReferral());
        productArrangement.setApplicationStatus(administerProductArrangementResponse.getApplicationStatus());
        productArrangement.setApplicationSubStatus(administerProductArrangementResponse.getApplicationSubStatus());
        productArrangement.setRetryCount(administerProductArrangementResponse.getRetryCount());
        if (administerProductArrangementResponse.getPrimaryInvolvedParty() != null && !CollectionUtils.isEmpty(administerProductArrangementResponse.getPrimaryInvolvedParty().getCustomerScore())) {
            String scoreResult = administerProductArrangementResponse.getPrimaryInvolvedParty().getCustomerScore().get(0).getScoreResult();
            productArrangement.getPrimaryInvolvedParty().getCustomerScore().get(1).setScoreResult(scoreResult);

            if (ArrangementType.CREDITCARD.getValue().equalsIgnoreCase(productArrangement.getArrangementType()) && ActivateCommonConstant.AsmDecision.ACCEPT.equals(scoreResult)) {
                if (!CollectionUtils.isEmpty(administerProductArrangementResponse.getOfferedProducts()) && !CollectionUtils.isEmpty(administerProductArrangementResponse.getOfferedProducts().get(0).getProductoffer())) {
                    productArrangement.getOfferedProducts().get(0).getProductoffer().get(0).setOfferAmount(administerProductArrangementResponse.getOfferedProducts().get(0).getProductoffer().get(0).getOfferAmount());
                }
            } else if (ArrangementType.CURRENT_ACCOUNT.getValue().equalsIgnoreCase(productArrangement.getArrangementType()) &&
                    (ActivateCommonConstant.AsmDecision.ACCEPT.equals(scoreResult) || ActivateCommonConstant.AsmDecision.REFERRED.equals(scoreResult))) {
                setOverdraftDetails(productArrangement, administerProductArrangementResponse);
            }

        }
    }

    private void setOverdraftDetails(ProductArrangement productArrangement, ProductArrangement administerProductArrangementResponse) {
        if (!CollectionUtils.isEmpty(administerProductArrangementResponse.getOfferedProducts().get(0).getProductoptions())) {
            for (ProductOptions productOptions : administerProductArrangementResponse.getOfferedProducts().get(0).getProductoptions()) {
                if (null != productOptions.getOptionsCode() && !StringUtils.isEmpty(productOptions.getOptionsValue())) {
                    if (OVERDRAFT_OFFERED_FLAG_CODE.equals(productOptions.getOptionsCode().replaceFirst("^0+(?!$)", "")) && 0 == (int) Double.parseDouble(productOptions.getOptionsValue())) {
                        if (productArrangement instanceof DepositArrangement) {
                            ((DepositArrangement) productArrangement).setOverdraftDetails(new OverdraftDetails());
                            ((DepositArrangement) productArrangement).setIsOverdraftRequired(false);
                        }
                        break;
                    }
                }
            }
        }
    }

    private boolean isSiraCallRequired(Boolean siraEnabledSwitch, String appStatus, String appSubStatus, String channelId, List<RuleCondition> ruleConditionList) {
        boolean isSiraEnabled=false;
        if (null != siraEnabledSwitch) {
            if(siraEnabledSwitch){
                isSiraEnabled = true;
            }
        } else {
            isSiraEnabled =retrieveSwitchValueBranded(channelId, SWITCH_SIRA_DETAILS);
        }
        if (isSiraEnabled && isPCAReEngineering(ruleConditionList)) {
            return ApplicationStatus.REFERRED.getValue().equalsIgnoreCase(appStatus) && (SIRA_REFER.equalsIgnoreCase(appSubStatus)||SIRA_AND_ASM_REFER.equalsIgnoreCase(appSubStatus));
        }
        return false;
    }

    private boolean retrieveSwitchValueBranded(String channel, String switchName) {
        try {
            return switchClient.getBrandedSwitchValue(switchName, channel, false);
        } catch (WebServiceException e) {
            LOGGER.info("Error occurred while fetching Switch value for channel " + channel + e);
            return false;
        }
    }
    private boolean isPCAReEngineering(List<RuleCondition> ruleConditionList){
        for (RuleCondition condition : ruleConditionList) {
            if (("INTEND_TO_SWITCH").equalsIgnoreCase(condition.getName()) && !StringUtils.isEmpty(condition.getResult())) {
                return true;
            }
        }
        return false;
    }
}

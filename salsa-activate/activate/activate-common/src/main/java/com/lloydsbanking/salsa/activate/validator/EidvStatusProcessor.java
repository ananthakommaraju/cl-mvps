package com.lloydsbanking.salsa.activate.validator;

import com.lloydsbanking.salsa.activate.communication.convert.NotificationEmailTemplates;
import com.lloydsbanking.salsa.activate.communication.downstream.CommunicationManager;
import com.lloydsbanking.salsa.activate.constants.ActivateCommonConstant;
import com.lloydsbanking.salsa.constant.ApplicationStatus;
import com.lloydsbanking.salsa.downstream.switches.SwitchService;
import lib_sim_bo.businessobjects.CustomerScore;
import lib_sim_bo.businessobjects.ProductArrangement;
import lib_sim_gmo.messages.RequestHeader;
import lib_sim_salesprocessmanagement.ia_activateproductarrangement.*;
import org.apache.cxf.common.util.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.xml.ws.WebServiceException;
import java.util.List;

@Component
public class EidvStatusProcessor {

    @Autowired
    NotificationEmailTemplates notificationEmailTemplates;
    @Autowired
    CommunicationManager communicationManager;
    @Autowired
    public SwitchService switchClient;
    private static final String PRODUCT_MNEMONIC_KID_SAVER = "P_KRS";
    private static final String PRODUCT_MNEMONIC_YOUNG_SAVER = "P_YNG_SVR";
    private static final String EIDV_STATUS_REFER = "REFER";
    private static final String EIDV_STATUS_ACCEPT = "ACCEPT";
    private static final String REFER_IDV = "REFER IDV";
    private static final String SWITCH_SIRA_DETAILS = "SW_EnSIRAFrdChk";
    private static final int CUSTOMER_SCORE_LIST_SIZE = 3;
    private static final Logger LOGGER = Logger.getLogger(EidvStatusProcessor.class);

    public boolean validateApplicationAndGetFulfilmentEligibilityFlag(ProductArrangement productArrangement, RequestHeader header, String sourceSystemIdentifier) throws ActivateProductArrangementExternalBusinessErrorMsg, ActivateProductArrangementResourceNotAvailableErrorMsg, ActivateProductArrangementExternalSystemErrorMsg, ActivateProductArrangementDataNotAvailableErrorMsg, ActivateProductArrangementInternalSystemErrorMsg {
        boolean isEligibleForFulfilment = false;
        if (ActivateCommonConstant.ApplicationType.TRADE.equals(productArrangement.getApplicationType())) {
            isEligibleForFulfilment = true;
        }
        if (ApplicationStatus.APPROVED.getValue().equalsIgnoreCase(productArrangement.getApplicationStatus())) {
            if (!StringUtils.isEmpty(productArrangement.getRelatedApplicationId())) {
                isEligibleForFulfilment = true;
            } else {
                String eIdVStatus = getEIDVStatus(productArrangement, header.getChannelId(),sourceSystemIdentifier);
                if (validateInstructionMnemonic(productArrangement, eIdVStatus)) {
                    isEligibleForFulfilment = false;
                    productArrangement.setApplicationStatus(ApplicationStatus.AWAITING_MANUAL_ID_V.getValue());
                } else if (EIDV_STATUS_ACCEPT.equalsIgnoreCase(eIdVStatus)) {
                    isEligibleForFulfilment = true;
                } else if (EIDV_STATUS_REFER.equalsIgnoreCase(eIdVStatus)) {
                    String notificationEmail = notificationEmailTemplates.getNotificationEmailForReferredToBranch(productArrangement.getArrangementType());
                    communicationManager.callSendCommunicationService(productArrangement, notificationEmail, header, null, ActivateCommonConstant.CommunicationTypes.EMAIL);
                    productArrangement.setApplicationStatus(ApplicationStatus.AWAITING_MANUAL_ID_V.getValue());
                }
            }
        }
        return isEligibleForFulfilment;
    }

    private boolean validateInstructionMnemonic(ProductArrangement productArrangement, String eIdVStatus) {
        boolean flag = false;
        if (productArrangement.getAssociatedProduct() != null && productArrangement.getAssociatedProduct().getInstructionDetails() != null) {
            if (PRODUCT_MNEMONIC_KID_SAVER.equalsIgnoreCase(productArrangement.getAssociatedProduct().getInstructionDetails().getInstructionMnemonic()) || PRODUCT_MNEMONIC_YOUNG_SAVER.equalsIgnoreCase(productArrangement.getAssociatedProduct().getInstructionDetails().getInstructionMnemonic())) {
                if (EIDV_STATUS_ACCEPT.equals(eIdVStatus) && EIDV_STATUS_REFER.equals(productArrangement.getPrimaryInvolvedParty().getCustomerScore().get(0).getScoreResult())) {
                    flag = true;
                }
            }
        }
        return flag;
    }

    private String getEIDVStatus(ProductArrangement productArrangement, String channelId,String sourceSystemIdentifier) {
        String eIdVStatus;
        if (productArrangement.getGuardianDetails() != null && !productArrangement.getGuardianDetails().getCustomerScore().isEmpty()) {
            eIdVStatus = productArrangement.getGuardianDetails().getCustomerScore().get(0).getScoreResult();
        } else {
            eIdVStatus = productArrangement.getPrimaryInvolvedParty().getCustomerScore().get(0).getScoreResult();
        }
        if (isSiraCallRequired(productArrangement.isSIRAEnabledSwitch(), productArrangement.getPrimaryInvolvedParty().getCustomerScore(), channelId,sourceSystemIdentifier)) {
            eIdVStatus = EIDV_STATUS_REFER;
        }
        return eIdVStatus;
    }

    private boolean isSiraCallRequired(Boolean siraEnabledSwitch, List<CustomerScore> customerScoreList, String channelId,String sourceSystemIdentifier) {
        boolean isSiraEnabled = false;
        if (null != siraEnabledSwitch) {
            if (siraEnabledSwitch) {
                isSiraEnabled = true;
            }
        } else {
            isSiraEnabled = retrieveSwitchValueBranded(channelId, SWITCH_SIRA_DETAILS);
        }
        LOGGER.info("Value of SIRAEnabled is"+isSiraEnabled+customerScoreList);
        if (isSiraEnabled && !CollectionUtils.isEmpty(customerScoreList) && customerScoreList.size() >= CUSTOMER_SCORE_LIST_SIZE) {
            if(customerScoreList.get(2)!=null && customerScoreList.get(2).getCustomerDecision()!=null) {
                return REFER_IDV.equalsIgnoreCase(customerScoreList.get(2).getCustomerDecision().getResultStatus()) && !ActivateCommonConstant.SourceSystemIdentifier.GALAXY_OAP_SOURCE_SYSTEM_IDENTIFIER.equals(sourceSystemIdentifier);
            }
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
}
package com.lloydsbanking.salsa.ppae.service.appstatus;

import com.lloydsbanking.salsa.activate.administer.AdministerReferredLookUpData;
import com.lloydsbanking.salsa.activate.communication.convert.NotificationEmailTemplates;
import com.lloydsbanking.salsa.activate.communication.downstream.CommunicationManager;
import com.lloydsbanking.salsa.activate.constants.ActivateCommonConstant;
import com.lloydsbanking.salsa.activate.constants.EmailTemplateEnum;
import com.lloydsbanking.salsa.activate.downstream.LookUpValueRetriever;
import com.lloydsbanking.salsa.constant.ApplicationStatus;
import com.lloydsbanking.salsa.constant.ArrangementType;
import lib_sim_bo.businessobjects.CustomerScore;
import lib_sim_bo.businessobjects.ProductArrangement;
import lib_sim_bo.businessobjects.ReferralCode;
import lib_sim_gmo.messages.RequestHeader;
import lib_sim_salesprocessmanagement.ia_activateproductarrangement.ActivateProductArrangementDataNotAvailableErrorMsg;
import lib_sim_salesprocessmanagement.ia_activateproductarrangement.ActivateProductArrangementResourceNotAvailableErrorMsg;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CreditRatingScoreDecisionEvaluator {

    private static final Logger LOGGER = Logger.getLogger(CreditRatingScoreDecisionEvaluator.class);

    @Autowired
    ProcessAsmAcceptDecision processAsmAcceptDecision;
    @Autowired
    LookUpValueRetriever lookUpValueRetriever;
    @Autowired
    NotificationEmailTemplates notificationEmailTemplates;
    @Autowired
    CommunicationManager communicationManager;
    @Autowired
    AdministerReferredLookUpData administerReferredLookUpData;

    private static final String EXPERIAN_AVAILABLE_REFERRAL_CODE = "501";
    private static final String ASM_CREDIT_DECISION_REFER = "2";
    public static final String ASM_CREDIT_DECISION_ACCEPT = "1";
    private static final String DECLINE_SOURCE_BUREAU = "Bureau";
    private static final String DECLINE_SOURCE_DUPLICATE_APPLICATION = "Duplicate application";

    public void applyCreditRatingScore(ProductArrangement productArrangement, RequestHeader requestHeader, String productOfferIdentifier, String productIdentifier, PpaeInvocationIdentifier ppaeInvocationIdentifier) throws ActivateProductArrangementDataNotAvailableErrorMsg, ActivateProductArrangementResourceNotAvailableErrorMsg {
        CustomerScore customerScore = getCustomerScore(productArrangement);
        if (null != customerScore) {
            LOGGER.info("Asm Decision from apply service: " + customerScore.getScoreResult());
            if (ASM_CREDIT_DECISION_REFER.equalsIgnoreCase(customerScore.getScoreResult()) && checkReferralCode(customerScore.getReferralCode())) {
                productArrangement.setApplicationStatus(ApplicationStatus.AWAITING_RESCORE.getValue());
                ppaeInvocationIdentifier.setInvokeModifyProductArrangementFlag(true);
                if (productArrangement.getRetryCount() == null) {
                    productArrangement.setRetryCount(1);
                } else {
                    productArrangement.setRetryCount(productArrangement.getRetryCount() + 1);
                }
            } else if (customerScore.getScoreResult().equalsIgnoreCase(ASM_CREDIT_DECISION_REFER) || customerScore.getScoreResult().equalsIgnoreCase(ASM_CREDIT_DECISION_ACCEPT)) {
                processAsmAcceptDecision.checkProductAndStatus(productArrangement, requestHeader, productOfferIdentifier, productIdentifier, ppaeInvocationIdentifier);
            } else {
                ppaeInvocationIdentifier.setInvokeModifyProductArrangementFlag(true);
                sendCommunicationEmailForDeclineStatus(requestHeader, productArrangement);
            }
        }
    }

    private CustomerScore getCustomerScore(ProductArrangement productArrangement) {
        if (productArrangement.getPrimaryInvolvedParty().getCustomerScore().get(1) != null && productArrangement.getPrimaryInvolvedParty().getCustomerScore().get(1).getScoreResult() != null) {
            return productArrangement.getPrimaryInvolvedParty().getCustomerScore().get(1);
        }
        return null;
    }

    private boolean checkReferralCode(List<ReferralCode> referralCodeList) {
        for (ReferralCode referralCode : referralCodeList) {
            if (EXPERIAN_AVAILABLE_REFERRAL_CODE.equalsIgnoreCase(referralCode.getCode())) {
                return true;
            }
        }
        return false;
    }

    private void sendCommunicationEmailForDeclineStatus(RequestHeader requestHeader, ProductArrangement productArrangement) {
        if (!productArrangement.getPrimaryInvolvedParty().getCustomerScore().isEmpty() && !productArrangement.getPrimaryInvolvedParty().getCustomerScore().get(1).getReferralCode().isEmpty()) {
            String declineSource = administerReferredLookUpData.getDeclineSource(getReferralCode(productArrangement), requestHeader.getChannelId());
            String notificationEmail = getNotificationEmailForDeclined(declineSource, productArrangement.getArrangementType());
            communicationManager.callSendCommunicationService(productArrangement, notificationEmail, requestHeader, null, ActivateCommonConstant.CommunicationTypes.EMAIL);
        }
    }

    private String getReferralCode(ProductArrangement productArrangement) {
        String referralCode = null;
        if (!productArrangement.getPrimaryInvolvedParty().getCustomerScore().get(1).getReferralCode().isEmpty()) {
            referralCode = productArrangement.getPrimaryInvolvedParty().getCustomerScore().get(1).getReferralCode().get(0).getCode();
        }
        return referralCode;
    }

    public String getNotificationEmailForDeclined(String declineSource, String arrangementType) {
        String notificationEmail;
        if (ArrangementType.CURRENT_ACCOUNT.getValue().equalsIgnoreCase(arrangementType)) {
            notificationEmail = getNotificationEmailByDeclineSource(declineSource, EmailTemplateEnum.CA_DECLINE_BUREAU_MSG.getTemplate(), EmailTemplateEnum.CA_DECLINE_DPLICT_MSG.getTemplate(), EmailTemplateEnum.CA_DECLINE_BANK_MSG.getTemplate());
        } else {
            notificationEmail = getNotificationEmailByDeclineSource(declineSource, EmailTemplateEnum.BUREAU_DECLINE_EMAIL.getTemplate(), EmailTemplateEnum.DUPLICATE_DECLINE_EMAIL.getTemplate(), EmailTemplateEnum.BANK_DECLINE_EMAIL.getTemplate());
        }
        return notificationEmail;
    }

    private String getNotificationEmailByDeclineSource(String declineSource, String templateBureauDecline, String templateDuplicateDecline, String templateBankDecline) {
        String notificationEmail;
        if (DECLINE_SOURCE_BUREAU.equalsIgnoreCase(declineSource)) {
            notificationEmail = templateBureauDecline;
        } else if (DECLINE_SOURCE_DUPLICATE_APPLICATION.equalsIgnoreCase(declineSource)) {
            notificationEmail = templateDuplicateDecline;
        } else {
            notificationEmail = templateBankDecline;
        }
        return notificationEmail;
    }


}



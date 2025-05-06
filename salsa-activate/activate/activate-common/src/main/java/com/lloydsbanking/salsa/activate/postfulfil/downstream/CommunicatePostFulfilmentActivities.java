package com.lloydsbanking.salsa.activate.postfulfil.downstream;

import com.lloydsbanking.salsa.activate.communication.convert.NotificationEmailTemplates;
import com.lloydsbanking.salsa.activate.communication.downstream.CommunicationManager;
import com.lloydsbanking.salsa.activate.constants.ActivateCommonConstant;
import com.lloydsbanking.salsa.activate.constants.EmailTemplateEnum;
import com.lloydsbanking.salsa.activate.postfulfil.rules.ValidateProcessPostFulfilment;
import com.lloydsbanking.salsa.constant.ArrangementType;
import com.lloydstsb.ib.wsbridge.application.StB751BAppPerCCRegAuth;
import lib_sim_bo.businessobjects.Product;
import lib_sim_bo.businessobjects.ProductArrangement;
import lib_sim_bo.businessobjects.RuleCondition;
import lib_sim_bo.businessobjects.TelephoneNumber;
import lib_sim_gmo.messages.RequestHeader;
import org.apache.cxf.common.util.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CommunicatePostFulfilmentActivities {
    @Autowired
    CommunicationManager communicationManager;
    @Autowired
    ValidateProcessPostFulfilment validateProcessPostFulfilment;
    @Autowired
    NotificationEmailTemplates notificationEmailTemplates;

    private static final String SWITCH_DURABLE_MEDIUM = "SW_EnSTPPCAWcMl";
    private static final String STP_SAVING_FUND_REMIND = "STPSAVFUNDREMIN";
    private static final String STP_SAVING_SUCCESS = "STPSAVSUCCESS";
    private static final int MOBILE_NUMBER_LENGTH = 10;
    private static final String ALERT_MESSAGES = "ALERT_MSGES";

    public void communicateForIBRegistration(StB751BAppPerCCRegAuth activateIBApplicationResponse, ProductArrangement productArrangement, RequestHeader header) {
        if (activateIBApplicationResponse.getTacver() == ActivateCommonConstant.InternetBankingMandate.ULTRALITE || activateIBApplicationResponse.getTacver() == ActivateCommonConstant.InternetBankingMandate.LITE) {
            communicationManager.callSendCommunicationService(productArrangement, notificationEmailTemplates.getNotificationEmailForIBRegistration(activateIBApplicationResponse.getTacver()), header, null, ActivateCommonConstant.CommunicationTypes.EMAIL);
        }
    }

    public void communicateWelcomeMessageAndFundReminder(ProductArrangement productArrangement, RequestHeader header, String channelId, String sourceId) {
        if (ArrangementType.SAVINGS.getValue().equalsIgnoreCase(productArrangement.getArrangementType())) {
            communicateWelcomeMessageAndFundReminderForSavingAccount(productArrangement, header);
        } else {
            if (validateProcessPostFulfilment.retrieveSwitchValue(channelId, SWITCH_DURABLE_MEDIUM) && isExternalTemplateIdPresent(productArrangement.getAssociatedProduct())) {
                String externalTemplate = productArrangement.getAssociatedProduct().getProductoffer().get(0).getTemplate().get(0).getExternalTemplateIdentifier();
                communicationManager.callSendCommunicationService(productArrangement, externalTemplate, header, null, ActivateCommonConstant.CommunicationTypes.ATTACHMENT);
            } else {
                communicationManager.callSendCommunicationService(productArrangement, EmailTemplateEnum.CA_WELCOME_MSG.getTemplate(), header, null, ActivateCommonConstant.CommunicationTypes.EMAIL);
            }
        }

        if (isBenefitsDataPresent(productArrangement.getConditions())) {
            communicationManager.callSendCommunicationService(productArrangement, EmailTemplateEnum.CA_BENEFITS_MSG.getTemplate(), header, null, ActivateCommonConstant.CommunicationTypes.EMAIL);
        }
        if (!ActivateCommonConstant.SourceSystemIdentifier.GALAXY_ONLINE_SOURCE_SYSTEM_IDENTIFIER.equals(sourceId)) {
            if (isCustomerMobileNumberPresent(productArrangement)) {
                communicationManager.callScheduleCommunicationService(productArrangement, STP_SAVING_SUCCESS, header, ActivateCommonConstant.CommunicationSource.STP_SAV_SOURCE, ActivateCommonConstant.CommunicationTypes.SMS);
            }
        }
    }

    private boolean isExternalTemplateIdPresent(Product associatedProduct) {
        boolean isExternalTemplateId = false;
        if (associatedProduct != null && !CollectionUtils.isEmpty(associatedProduct.getProductoffer()) && associatedProduct.getProductoffer().get(0) != null) {
            if (!CollectionUtils.isEmpty(associatedProduct.getProductoffer().get(0).getTemplate()) && associatedProduct.getProductoffer().get(0).getTemplate().get(0) != null && associatedProduct.getProductoffer().get(0).getTemplate().get(0).getExternalTemplateIdentifier() != null) {
                isExternalTemplateId = true;
            }
        }
        return isExternalTemplateId;
    }

    private void communicateWelcomeMessageAndFundReminderForSavingAccount(ProductArrangement productArrangement, RequestHeader header) {
        communicationManager.callSendCommunicationService(productArrangement, notificationEmailTemplates.getWelcomeMessageNotificationEmailForSavingAccount(productArrangement.getConditions()), header, null, ActivateCommonConstant.CommunicationTypes.EMAIL);
        communicationManager.callScheduleCommunicationService(productArrangement, EmailTemplateEnum.SA_FUNDING_REM_EMAIL.getTemplate(), header, ActivateCommonConstant.CommunicationSource.STP_SAV_SOURCE, ActivateCommonConstant.CommunicationTypes.EMAIL);
        if (isCustomerMobileNumberPresent(productArrangement)) {
            communicationManager.callScheduleCommunicationService(productArrangement, STP_SAVING_FUND_REMIND, header, ActivateCommonConstant.CommunicationSource.STP_SAV_SOURCE, ActivateCommonConstant.CommunicationTypes.SMS);
        }
    }

    private boolean isBenefitsDataPresent(List<RuleCondition> ruleConditionList) {
        boolean isBenefitData = false;
        if (!CollectionUtils.isEmpty(ruleConditionList)) {
            for (RuleCondition ruleCondition : ruleConditionList) {
                if (ALERT_MESSAGES.equalsIgnoreCase(ruleCondition.getName())) {
                    isBenefitData = true;
                    break;
                }
            }
        }
        return isBenefitData;
    }

    private boolean isCustomerMobileNumberPresent(ProductArrangement productArrangement) {
        boolean isMobNoPresent = false;
        if (!CollectionUtils.isEmpty(productArrangement.getPrimaryInvolvedParty().getTelephoneNumber())) {
            isMobNoPresent = isMobileNumberPresent(productArrangement.getPrimaryInvolvedParty().getTelephoneNumber());
        } else if (productArrangement.getGuardianDetails() != null && !CollectionUtils.isEmpty(productArrangement.getGuardianDetails().getTelephoneNumber())) {
            isMobNoPresent = isMobileNumberPresent(productArrangement.getGuardianDetails().getTelephoneNumber());
        }
        return isMobNoPresent;
    }

    private boolean isMobileNumberPresent(List<TelephoneNumber> telephoneNumberList) {
        for (TelephoneNumber telephoneNumber : telephoneNumberList) {
            if (ActivateCommonConstant.TelephoneTypes.MOBILE.equalsIgnoreCase(telephoneNumber.getTelephoneType()) && null != telephoneNumber.getPhoneNumber() && telephoneNumber.getPhoneNumber().length() >= MOBILE_NUMBER_LENGTH) {
                return true;
            }
        }
        return false;
    }
}

package com.lloydsbanking.salsa.apacc.service.fulfil;

import com.lloydsbanking.salsa.activate.communication.convert.NotificationEmailTemplates;
import com.lloydsbanking.salsa.activate.communication.downstream.CommunicationManager;
import com.lloydsbanking.salsa.activate.constants.ActivateCommonConstant;
import com.lloydsbanking.salsa.activate.constants.EmailTemplateEnum;
import lib_sim_bo.businessobjects.Customer;
import lib_sim_bo.businessobjects.FinanceServiceArrangement;
import lib_sim_bo.businessobjects.ProductArrangement;
import lib_sim_bo.businessobjects.TelephoneNumber;
import lib_sim_gmo.messages.RequestHeader;
import org.apache.commons.lang3.StringUtils;
import org.apache.cxf.common.util.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CommunicateFulfilActivities {

    @Autowired
    CommunicationManager communicationManager;
    @Autowired
    NotificationEmailTemplates notificationEmailTemplates;
    private static final int MOBILE_NUMBER_LENGTH = 10;
    private static final String STP_CCR_SUCCESS = "STPCCRSUCCESS";
    private static final String STP_CC_SOURCE = "STPCC";

    public void sendIBRegistrationSuccessEmail(int accessLevel, FinanceServiceArrangement financeServiceArrangement, RequestHeader header) {
        Customer jointParty = financeServiceArrangement.getJointParties().get(0);
        if (jointParty.getIsRegisteredIn().getProfile() != null && !StringUtils.isEmpty(jointParty.getIsRegisteredIn().getProfile().getUserName())) {
            if (ActivateCommonConstant.InternetBankingMandate.ULTRALITE == accessLevel || accessLevel == ActivateCommonConstant.InternetBankingMandate.LITE) {
                communicationManager.callSendCommunicationService(financeServiceArrangement, notificationEmailTemplates.getNotificationEmailForIBRegistration(accessLevel), header, null, ActivateCommonConstant.CommunicationTypes.EMAIL);
            }
        }
    }

    public void sendWelcomeEmail(boolean isCallSuccessful, FinanceServiceArrangement financeServiceArrangement, RequestHeader header) {
        if (isCallSuccessful) {
            communicationManager.callSendCommunicationService(financeServiceArrangement, EmailTemplateEnum.WELCOME_EMAIL.getTemplate(), header, null, ActivateCommonConstant.CommunicationTypes.EMAIL);
        }
    }

    public void scheduleSTPSuccessSMS(boolean isCallSuccessful, FinanceServiceArrangement financeServiceArrangement, RequestHeader header, String sourceId) {
        if (isCallSuccessful && !ActivateCommonConstant.SourceSystemIdentifier.GALAXY_ONLINE_SOURCE_SYSTEM_IDENTIFIER.equals(sourceId) && isCustomerMobileNumberPresent(financeServiceArrangement)) {
            communicationManager.callScheduleCommunicationService(financeServiceArrangement, STP_CCR_SUCCESS, header, STP_CC_SOURCE, ActivateCommonConstant.CommunicationTypes.SMS);
        }
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
            if (ActivateCommonConstant.TelephoneTypes.MOBILE.equalsIgnoreCase(telephoneNumber.getTelephoneType()) && telephoneNumber.getPhoneNumber().length() >= MOBILE_NUMBER_LENGTH) {
                return true;
            }
        }
        return false;
    }
}

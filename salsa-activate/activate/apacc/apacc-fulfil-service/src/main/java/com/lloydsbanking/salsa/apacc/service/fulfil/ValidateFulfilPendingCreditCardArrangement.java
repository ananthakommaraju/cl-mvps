package com.lloydsbanking.salsa.apacc.service.fulfil;

import com.lloydsbanking.salsa.activate.constants.ActivateCommonConstant;
import com.lloydsbanking.salsa.activate.helper.ApplicationDetails;
import lib_sim_bo.businessobjects.Customer;
import lib_sim_bo.businessobjects.FinanceServiceArrangement;
import org.apache.cxf.common.util.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

@Component
public class ValidateFulfilPendingCreditCardArrangement {
    private static final Logger LOGGER = Logger.getLogger(ValidateFulfilPendingCreditCardArrangement.class);

    public boolean checkAppSubStatus(String appSubStatus, String appSubStatusToBeCompared) {
        return (appSubStatusToBeCompared.equalsIgnoreCase(appSubStatus));
    }

    public boolean isFulfillNewApplication(String applicationType) {
        return ActivateCommonConstant.ApplicationType.NEW.equals(applicationType);
    }

    public boolean isPreviousCallSuccessful(boolean isCallSuccessful, String appSubStatus, String appSubStatusToBeCompared) {
        return (isCallSuccessful && StringUtils.isEmpty(appSubStatus)) || checkAppSubStatus(appSubStatus, appSubStatusToBeCompared);
    }

    public boolean isAddOMSRequired(boolean isCallSuccessful, String appSubStatus) {
        return (isCallSuccessful && appSubStatus == null) || ActivateCommonConstant.AppSubStatus.ADD_OMS_OFFERS.equalsIgnoreCase(appSubStatus);
    }

    public boolean isIBActivationRequired(boolean apiFailureFlag, boolean isStoreApplication, Customer primaryInvolvedParty) {
        if (isCallAndStoreApplication(apiFailureFlag, isStoreApplication)) {
            return (primaryInvolvedParty != null && primaryInvolvedParty.getIsRegisteredIn() != null &&
                    !StringUtils.isEmpty(primaryInvolvedParty.getIsRegisteredIn().getRegistrationIdentifier()));
        }
        return false;
    }

    public boolean checkIfAddCardHolderFailureOrIsJointParty(FinanceServiceArrangement financeServiceArrangement, ApplicationDetails applicationDetails) {
        if (isPreviousCallSuccessful(!applicationDetails.isApiFailureFlag(), financeServiceArrangement.getApplicationSubStatus(), ActivateCommonConstant.AppSubStatus.ADD_CARD_HOLDER_FAILURE)) {
            return checkAppSubStatus(financeServiceArrangement.getApplicationSubStatus(), ActivateCommonConstant.AppSubStatus.ADD_CARD_HOLDER_FAILURE) || (null != financeServiceArrangement.isIsJointParty() && financeServiceArrangement.isIsJointParty());
        }
        return false;
    }

    public boolean isCallAndStoreApplication(boolean isCallSuccessful, boolean isStoreApplication) {
        return isCallSuccessful && isStoreApplication;
    }

    public boolean isMQCallRequired(boolean isCallSuccessful, boolean isStoreApplication, String applicationType) {
        return isCallSuccessful && (isStoreApplication || ActivateCommonConstant.ApplicationType.TRADE.equalsIgnoreCase(applicationType));
    }
}

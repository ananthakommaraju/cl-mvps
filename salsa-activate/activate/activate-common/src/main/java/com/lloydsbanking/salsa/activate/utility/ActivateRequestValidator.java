package com.lloydsbanking.salsa.activate.utility;

import com.lloydsbanking.salsa.activate.constants.ActivateCommonConstant;
import com.lloydsbanking.salsa.constant.ApplicationStatus;
import org.springframework.stereotype.Component;

@Component
public class ActivateRequestValidator {

    public boolean validateRequest(String appStatus, String sourceSystemIdentifier) {

        boolean isRequestValid = false;
        if (ActivateCommonConstant.SourceSystemIdentifier.GALAXY_OAP_SOURCE_SYSTEM_IDENTIFIER.equals(sourceSystemIdentifier)) {
            isRequestValid = validateAppStatusForOAP(appStatus);
        } else if (ActivateCommonConstant.SourceSystemIdentifier.GALAXY_DB_EVENT_SOURCE_SYSTEM_IDENTIFIER.equals(sourceSystemIdentifier)) {
            isRequestValid = validateAppStatusForDBEvent(appStatus);
        } else if (ActivateCommonConstant.SourceSystemIdentifier.GALAXY_ONLINE_SOURCE_SYSTEM_IDENTIFIER.equals(sourceSystemIdentifier)
                || ActivateCommonConstant.SourceSystemIdentifier.GALAXY_OFFLINE_SOURCE_SYSTEM_IDENTIFIER.equals(sourceSystemIdentifier)) {
            isRequestValid = validateAppStatusForGalaxyOnlineOffline(appStatus);
        }

        return isRequestValid;
    }

    private boolean validateAppStatusForOAP(String appStatus) {

        return (ApplicationStatus.AWAITING_MANUAL_ID_V.getValue().equals(appStatus));
    }

    private boolean validateAppStatusForGalaxyOnlineOffline(String appStatus) {

        return (ApplicationStatus.REFERRED.getValue().equals(appStatus)
                || ApplicationStatus.APPROVED.getValue().equals(appStatus)
                || ApplicationStatus.UNSCORED.getValue().equals(appStatus));
    }

    private boolean validateAppStatusForDBEvent(String appStatus) {

        return (ApplicationStatus.REFERRED.getValue().equals(appStatus)
                || ApplicationStatus.APPROVED.getValue().equals(appStatus)
                || ApplicationStatus.REFERRAL_PROCESSED.getValue().equals(appStatus)
                || ApplicationStatus.AWAITING_REFERRAL_PROCESSING.getValue().equals(appStatus));
    }

}

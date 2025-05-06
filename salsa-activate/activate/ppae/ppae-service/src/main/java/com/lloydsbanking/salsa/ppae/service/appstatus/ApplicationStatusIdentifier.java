package com.lloydsbanking.salsa.ppae.service.appstatus;


import com.lloydsbanking.salsa.constant.ApplicationStatus;
import com.lloydsbanking.salsa.constant.ArrangementType;
import com.lloydsbanking.salsa.ppae.service.constant.AppStatusConstant;
import lib_sim_bo.businessobjects.Referral;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ApplicationStatusIdentifier {

    private static final Logger LOGGER = Logger.getLogger(ApplicationStatusIdentifier.class);

    public String retrieveAppStatusToProceed(String appStatus, String arrType, List<Referral> referralList) {
        String appStatusToProceed = null;
        if (ApplicationStatus.AWAITING_RESCORE.getValue().equalsIgnoreCase(appStatus)) {
            appStatusToProceed = AppStatusConstant.AWAITING_RESCORE;
        } else if (isAwaitingReferral(appStatus, arrType)) {
            appStatusToProceed = AppStatusConstant.AWAITING_REFERRAL;
        } else if (ApplicationStatus.AWAITING_MANUAL_ID_V.getValue().equalsIgnoreCase(appStatus)) {
            appStatusToProceed = AppStatusConstant.AWAITING_MANUAL_ID_V;
        } else if (ApplicationStatus.ABANDONED.getValue().equalsIgnoreCase(appStatus)) {
            appStatusToProceed = AppStatusConstant.ABANDONED;
        } else if (ApplicationStatus.AWAITING_FULFILMENT.getValue().equalsIgnoreCase(appStatus)) {
            appStatusToProceed = AppStatusConstant.AWAITING_FULFILMENT;
        } else if (isAwaitingReferralLRA(appStatus, arrType, referralList)) {
            appStatusToProceed = AppStatusConstant.AWAITING_REFERRAL_LRA;
        } else if (ApplicationStatus.ACCEPT_PND.getValue().equalsIgnoreCase(appStatus)) {
            appStatusToProceed = AppStatusConstant.NEW_CAR;
        } else if (ApplicationStatus.AWAITING_POST_FULFILMENT_PROCESS.getValue().equalsIgnoreCase(appStatus)) {
            appStatusToProceed = AppStatusConstant.AWAITING_POST_FULFILMENT_PROCESS;
        } else if (isLoanCCASignedOrPending(appStatus, arrType)) {
            appStatusToProceed = AppStatusConstant.CCA_SIGNED_CCA_PENDING;
        }
        return appStatusToProceed;
    }

    public boolean isAwaitingReferralLRA(String appStatus, String arrType, List<Referral> referralList) {
        if (!referralList.isEmpty()) {
            if (isLRAApplication(arrType) && !StringUtils.isEmpty(referralList.get(0).getTmsTaskIdentifier())
                    && ApplicationStatus.REFERRAL_PROCESSED.getValue().equalsIgnoreCase(appStatus)) {
                return true;
            }
        }
        return false;
    }

    private boolean isAwaitingReferral(String appStatus, String arrType) {
        if (!isLRAApplication(arrType)) {
            if (ApplicationStatus.AWAITING_REFERRAL_PROCESSING.getValue().equalsIgnoreCase(appStatus)
                    || ApplicationStatus.REFERRAL_PROCESSED.getValue().equalsIgnoreCase(appStatus)
                    || ApplicationStatus.APPROVED.getValue().equals(appStatus)) {
                return true;
            }
        }
        return false;
    }

    private boolean isLRAApplication(String arrType) {
        if (ArrangementType.LOAN_REFERRAL_AUTOMATION.getValue().equalsIgnoreCase(arrType)) {
            return true;
        }
        return false;
    }

    private boolean isLoanCCASignedOrPending(String appStatus, String arrType) {
        if (ArrangementType.LOAN.getValue().equalsIgnoreCase(arrType) && (ApplicationStatus.CCA_SIGNED.getValue().equalsIgnoreCase(appStatus)
                || ApplicationStatus.CCA_PENDING.getValue().equalsIgnoreCase(appStatus))) {
            return true;
        }
        return false;
    }


}

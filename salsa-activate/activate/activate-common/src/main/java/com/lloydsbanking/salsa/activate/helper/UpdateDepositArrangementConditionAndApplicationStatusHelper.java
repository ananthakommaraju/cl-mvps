package com.lloydsbanking.salsa.activate.helper;

import lib_sim_bo.businessobjects.Condition;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

@Component
public class UpdateDepositArrangementConditionAndApplicationStatusHelper {
    private static final Logger LOGGER = Logger.getLogger(UpdateDepositArrangementConditionAndApplicationStatusHelper.class);

    public Condition getCondition(String reasonCode, String reasonText) {
        Condition condition = new Condition();
        condition.setSeverityCode(null);
        condition.setReasonCode(reasonCode);
        condition.setReasonText(reasonText);
        return condition;
    }


    public void setApplicationDetails(Integer retryCount, String reasonCode, String reasonText, String appStatus, String appSubStatus, ApplicationDetails applicationDetails) {
        if (null != reasonCode && null != reasonText) {
            applicationDetails.getConditionList().add(getCondition(reasonCode, reasonText));
        }
        applicationDetails.setApiFailureFlag(true);
        applicationDetails.setApplicationStatus(appStatus);
        applicationDetails.setApplicationSubStatus(appSubStatus);
        if (retryCount != null) {
            applicationDetails.setRetryCount(retryCount.intValue() + 1);
        } else {
            applicationDetails.setRetryCount(1);
        }
    }

}

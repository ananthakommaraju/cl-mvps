package com.lloydsbanking.salsa.apacc.service.fulfil;

import com.lloydsbanking.salsa.activate.helper.ApplicationDetails;
import com.lloydsbanking.salsa.activate.registration.downstream.ActivateIBApplication;
import com.lloydsbanking.salsa.logging.application.ProductArrangementTraceLog;
import com.lloydstsb.ib.wsbridge.application.StB751BAppPerCCRegAuth;
import lib_sim_bo.businessobjects.*;
import lib_sim_gmo.messages.RequestHeader;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

@Component
public class IBRegistration {
    private static final Logger LOGGER = Logger.getLogger(IBRegistration.class);
    private static final String B751_ERROR_REASON_CODE = "008";
    private static final String B751_ERROR_REASON_TEXT = "Failed to do IB Registration";

    @Autowired
    ActivateIBApplication activateIBApplication;
    @Autowired
    ProductArrangementTraceLog productArrangementTraceLog;


    public void ibRegistrationCall(RequestHeader requestHeader, FinanceServiceArrangement financeServiceArrangement, ApplicationDetails applicationDetails) {
        LOGGER.info(productArrangementTraceLog.getApplicationTraceEventMessage(financeServiceArrangement, "Entering ActivateIB Application (BAPI B751)"));
        StB751BAppPerCCRegAuth b751Resp = activateIBApplication.retrieveActivateIBApplication(financeServiceArrangement, requestHeader);
        if (b751Resp != null) {
            if (CollectionUtils.isEmpty(financeServiceArrangement.getJointParties())) {
                Customer customer = new Customer();
                financeServiceArrangement.getJointParties().add(customer);
            }
            if (financeServiceArrangement.getJointParties().get(0).getIsRegisteredIn() == null) {
                financeServiceArrangement.getJointParties().get(0).setIsRegisteredIn(new InternetBankingRegistration());
            }
            if (financeServiceArrangement.getJointParties().get(0).getIsRegisteredIn().getProfile() == null) {
                financeServiceArrangement.getJointParties().get(0).getIsRegisteredIn().setProfile(new InternetBankingProfile());
            }
            financeServiceArrangement.getJointParties().get(0).getIsRegisteredIn().getProfile().setUserName(b751Resp.getPartyidEmergingChannelUserId());
            LOGGER.info("Exiting Activate IBApplication (BAPI B751) with userID: " + b751Resp.getPartyidEmergingChannelUserId());
        } else {
            Condition condition = new Condition();
            condition.setReasonCode(B751_ERROR_REASON_CODE);
            condition.setReasonText(B751_ERROR_REASON_TEXT);
            applicationDetails.getConditionList().add(condition);
        }
    }
}


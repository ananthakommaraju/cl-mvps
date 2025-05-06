package com.lloydsbanking.salsa.apapca.service.fulfil.downstream;

import com.lloydsbanking.salsa.activate.constants.ActivateCommonConstant;
import com.lloydsbanking.salsa.activate.helper.ApplicationDetails;
import com.lloydsbanking.salsa.activate.helper.UpdateDepositArrangementConditionAndApplicationStatusHelper;
import com.lloydsbanking.salsa.apapca.service.fulfil.converter.B276RequestFactory;
import com.lloydsbanking.salsa.constant.ApplicationStatus;
import com.lloydsbanking.salsa.downstream.account.client.AccountClient;
import com.lloydsbanking.salsa.soap.fs.account.StError;
import com.lloydstsb.ib.wsbridge.account.StB276AAccProcessOverdraft;
import com.lloydstsb.ib.wsbridge.account.StB276BAccProcessOverdraft;
import lib_sim_bo.businessobjects.DepositArrangement;
import lib_sim_gmo.messages.RequestHeader;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.xml.ws.WebServiceException;

@Repository
public class CreateOverdraft {
    private static final Logger LOGGER = Logger.getLogger(CreateOverdraft.class);

    @Autowired
    B276RequestFactory b276RequestFactory;

    @Autowired
    AccountClient accountClient;

    @Autowired
    UpdateDepositArrangementConditionAndApplicationStatusHelper updateDepositArrangementConditionAndApplicationStatusHelper;

    public void createAccountProcessOverdraft(DepositArrangement depositArrangement, RequestHeader requestHeader, ApplicationDetails applicationDetails) {
        LOGGER.info("Entering CreateOverdraft with appId: " + depositArrangement.getArrangementId());
        StB276AAccProcessOverdraft b276Req = b276RequestFactory.convert(depositArrangement, requestHeader);
        StB276BAccProcessOverdraft b276Resp;
        try {
            b276Resp = accountClient.retrieveAccountProcessOverdraft(b276Req);
            if (checkErrorScenarios(b276Resp.getSterror())) {
                updateDepositArrangementConditionAndApplicationStatusHelper.setApplicationDetails(depositArrangement.getRetryCount(), ActivateCommonConstant.ApaPcaServiceConstants.OVERDRAFT_FAILED_REASON_CODE, ActivateCommonConstant.ApaPcaServiceConstants.OVERDRAFT_FAILED_REASON_TEXT, ApplicationStatus.AWAITING_FULFILMENT.getValue(), ActivateCommonConstant.AppSubStatus.FAILED_TO_CREATE_OVERDRAFT, applicationDetails);
            }
        } catch (WebServiceException e) {
            LOGGER.info("Exception while calling B276 for createAccountProcessOverdraft " + e);
            updateDepositArrangementConditionAndApplicationStatusHelper.setApplicationDetails(depositArrangement.getRetryCount(), ActivateCommonConstant.ApaPcaServiceConstants.OVERDRAFT_FAILED_REASON_CODE, ActivateCommonConstant.ApaPcaServiceConstants.OVERDRAFT_FAILED_REASON_TEXT, ApplicationStatus.AWAITING_FULFILMENT.getValue(), ActivateCommonConstant.AppSubStatus.FAILED_TO_CREATE_OVERDRAFT, applicationDetails);
        }
        depositArrangement.setApplicationSubStatus(applicationDetails.getApplicationSubStatus());
    }

    private boolean checkErrorScenarios(StError stError) {
        boolean isErrorScenario = false;
        if (stError != null && stError.getErrorno() != 0) {
            isErrorScenario = true;
            LOGGER.info("B276 Error Detail :ErrorCode | ErrorReason: " + stError.getErrorno() + " | " + stError.getErrormsg());
        }
        return isErrorScenario;
    }

}

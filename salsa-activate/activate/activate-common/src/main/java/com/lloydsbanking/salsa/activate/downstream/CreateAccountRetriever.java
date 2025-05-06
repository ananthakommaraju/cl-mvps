package com.lloydsbanking.salsa.activate.downstream;

import com.lloydsbanking.salsa.activate.constants.ActivateCommonConstant;
import com.lloydsbanking.salsa.activate.converter.B765ToDepositArrangementResponse;
import com.lloydsbanking.salsa.activate.converter.DepositArrangementToB765Request;
import com.lloydsbanking.salsa.activate.helper.ApplicationDetails;
import com.lloydsbanking.salsa.activate.helper.UpdateDepositArrangementConditionAndApplicationStatusHelper;
import com.lloydsbanking.salsa.constant.ApplicationStatus;
import com.lloydsbanking.salsa.downstream.account.client.AccountClient;
import com.lloydstsb.ib.wsbridge.account.StB765AAccCreateAccount;
import com.lloydstsb.ib.wsbridge.account.StB765BAccCreateAccount;
import lib_sim_bo.businessobjects.DepositArrangement;
import lib_sim_bo.businessobjects.Product;
import lib_sim_gmo.messages.RequestHeader;
import lib_sim_salesprocessmanagement.messages.ActivateProductArrangementResponse;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.xml.ws.WebServiceException;
import java.util.Map;

@Repository
public class CreateAccountRetriever {
    @Autowired
    DepositArrangementToB765Request depositArrangementToB765Request;

    @Autowired
    AccountClient accountClient;

    @Autowired
    B765ToDepositArrangementResponse b765ToDepositArrangementResponse;

    @Autowired
    UpdateDepositArrangementConditionAndApplicationStatusHelper updateDepositArrangementConditionAndApplicationStatusHelper;

    private static final Logger LOGGER = Logger.getLogger(CreateAccountRetriever.class);

    public static final int NO_ERROR = 0;

    public static final int FAILED_OCIS_UPDATE = 131187;
    public static final String B765_FAILURE_REASON_CODE = "003";

    public static final String B765_FAILURE_REASON_TEXT = "Current Account Creation Failure";
    public static final String SAVING_ACCOUNT = "SA";
    public static final String EXTERNAL_ERROR_CODE = "8110001";


    public void createAccount(RequestHeader header, DepositArrangement depositArrangement, Product product, Map<String, String> accountPurposeMap, ActivateProductArrangementResponse response, ApplicationDetails applicationDetails) {
        try {
            if (null!=product && product.getInstructionDetails() == null) {
                updateAppDetails(depositArrangement, null, null, ApplicationStatus.AWAITING_FULFILMENT.getValue(), ActivateCommonConstant.AppSubStatus.CURRENT_ACCOUNT_CREATION_FAILURE, EXTERNAL_ERROR_CODE, applicationDetails);
            } else {
                StB765AAccCreateAccount b765Req = depositArrangementToB765Request.getCreateAccountRequest(header, depositArrangement, product, accountPurposeMap);
                LOGGER.info("Entering CreateAccount (B765) with Prd_No | TRF | Acc_Pur_Cd | Acc_Ty | Pty_Id | Acc_No | Srt_Cd: " + b765Req.getNProdNum() + " | " + b765Req.getNTariff() + " | " + b765Req.getNAccPurposeCode() + " | " + b765Req.getNAccType() + " | " + b765Req.getPprId() + " | " + b765Req.getAccno() + " | " + b765Req.getSortcode());
                StB765BAccCreateAccount b765Response = accountClient.createAccount(b765Req);
                if (b765Response != null && b765Response.getSterror() != null) {
                    int errorNo = b765Response.getSterror().getErrorno();
                    if (errorNo == NO_ERROR || errorNo == FAILED_OCIS_UPDATE) {
                        b765ToDepositArrangementResponse.createDepositArrangementResponse(b765Response, depositArrangement, response);
                        LOGGER.info("Exiting CreateAccount (B765) with SrtCd | AcNo: " + b765Response.getStacc().getSortcode() + " | " + b765Response.getStacc().getAccno() + b765Response.getCustnum());
                    } else {
                        LOGGER.info("B765 Error Detail: ErrorCode | ErrorReason: " + errorNo + " | " + b765Response.getSterror().getErrormsg());
                        updateAppDetails(depositArrangement, String.valueOf(b765Response.getSterror().getErrorno()), b765Response.getSterror().getErrormsg(), ApplicationStatus.AWAITING_FULFILMENT.getValue(), ActivateCommonConstant.AppSubStatus.CURRENT_ACCOUNT_CREATION_FAILURE, EXTERNAL_ERROR_CODE, applicationDetails);
                    }
                }
            }
        } catch (WebServiceException e) {
            LOGGER.info("Exception occurred while calling B765. Catching it and updating ApplicationStatus ", e);
            updateAppDetails(depositArrangement, null, null, ApplicationStatus.AWAITING_FULFILMENT.getValue(), null, "", applicationDetails);
        }
        depositArrangement.setApplicationSubStatus(applicationDetails.getApplicationSubStatus());
    }

    private boolean checkArrangementType(String arrangementType) {
        return SAVING_ACCOUNT.equals(arrangementType);
    }

    private void updateAppDetails(DepositArrangement depositArrangement, String b765ErrorNo, String b765ErrorMsg, String appStatus, String appSubStatus, String reasonCode, ApplicationDetails applicationDetails) {
        if (checkArrangementType(depositArrangement.getArrangementType())) {
            updateDepositArrangementConditionAndApplicationStatusHelper.setApplicationDetails(depositArrangement.getRetryCount(), reasonCode, b765ErrorNo + ":" + b765ErrorMsg, appStatus, null, applicationDetails);
        } else {
            updateDepositArrangementConditionAndApplicationStatusHelper.setApplicationDetails(depositArrangement.getRetryCount(), B765_FAILURE_REASON_CODE, B765_FAILURE_REASON_TEXT, appStatus, appSubStatus, applicationDetails);
        }
    }
}

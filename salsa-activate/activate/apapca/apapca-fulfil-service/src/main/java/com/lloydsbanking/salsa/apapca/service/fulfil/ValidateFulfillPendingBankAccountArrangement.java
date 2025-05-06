package com.lloydsbanking.salsa.apapca.service.fulfil;


import com.lloydsbanking.salsa.activate.constants.ActivateCommonConstant;
import com.lloydsbanking.salsa.downstream.switches.SwitchService;
import lib_sim_bo.businessobjects.DepositArrangement;
import lib_sim_bo.businessobjects.RuleCondition;
import org.apache.cxf.common.util.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.xml.ws.WebServiceException;
import java.util.List;

@Component
public class ValidateFulfillPendingBankAccountArrangement {

    public static final String DEBIT_CARD_REQUIRED_FLAG = "DEBIT_CARD_REQUIRED_FLAG";
    public static final String INTEND_TO_SWITCH = "INTEND_TO_SWITCH";
    private static final Logger LOGGER = Logger.getLogger(ValidateFulfillPendingBankAccountArrangement.class);


    @Autowired
    public SwitchService switchClient;

    public boolean checkCondition(boolean flag, String applicationSubStatus, String correspondingSubStatus) {
        return (!flag && applicationSubStatus == null) || (applicationSubStatus != null && applicationSubStatus.equals(correspondingSubStatus));
    }

    public boolean checkPCAReEngineering(DepositArrangement depositArrangement) {
        if (!CollectionUtils.isEmpty(depositArrangement.getConditions())) {
            for (RuleCondition condition : depositArrangement.getConditions()) {
                if (condition.getResult() != null && (INTEND_TO_SWITCH).equalsIgnoreCase(condition.getName())) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isDebitCardRequired(List<RuleCondition> conditions,String applicationSubStatus) {
        boolean isDebitCardRequired = false;
        for (RuleCondition condition : conditions) {
            if (!StringUtils.isEmpty(condition.getName()) && condition.getName().equals(DEBIT_CARD_REQUIRED_FLAG)) {
                if (condition.getResult().equals("Y")) {
                    isDebitCardRequired = true;
                }
            }
        }
        return isDebitCardRequired || ActivateCommonConstant.AppSubStatus.FAILED_TO_CREATE_CARD_ORDER.equals(applicationSubStatus);
    }

    public boolean isOverdraftRequired(DepositArrangement depositArrangement)
    {
        return (null != depositArrangement.isIsOverdraftRequired() && depositArrangement.isIsOverdraftRequired()) || ActivateCommonConstant.AppSubStatus.FAILED_TO_CREATE_OVERDRAFT.equals(depositArrangement.getApplicationSubStatus());
    }

    public boolean checkApplicationSubStatus(String appSubStatus) {
        boolean subStatusCompareFlag = false;
        if (appSubStatus != null) {
            switch (appSubStatus) {
                case ActivateCommonConstant.AppSubStatus.UPDATE_CUSTOMER_RECORD_FAILURE:
                    subStatusCompareFlag = true;
                    break;
                case ActivateCommonConstant.AppSubStatus.FAILED_TO_UPDATE_EMAIL_ADDRESS:
                    subStatusCompareFlag = true;
                    break;
                case ActivateCommonConstant.AppSubStatus.MARKETING_PREF_UPDATE_FAILURE:
                    subStatusCompareFlag = true;
                    break;
                case ActivateCommonConstant.AppSubStatus.CUSTOMER_DETAILS_UPDATE_FAILURE:
                    subStatusCompareFlag = true;
                    break;
                case ActivateCommonConstant.AppSubStatus.AWAITING_CRS_FULFILLMENT_FAILURE:
                    subStatusCompareFlag = true;
                    break;
                case ActivateCommonConstant.AppSubStatus.SIRA_FAILURE_SUB_STATUS:
                    subStatusCompareFlag = true;
                    break;
                default:
                    subStatusCompareFlag = false;
                    break;
            }
        }
        return subStatusCompareFlag;
    }
    public boolean retrieveSwitchValueBranded(String channel, String switchName) {
        try {
            return switchClient.getBrandedSwitchValue(switchName, channel, false);
        } catch (WebServiceException e) {
            LOGGER.info("Error occurred while fetching Switch value for channel " + channel + e);
            return false;
        }
    }
}

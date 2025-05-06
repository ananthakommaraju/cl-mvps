package com.lloydsbanking.salsa.activate.postfulfil.rules;

import com.lloydsbanking.salsa.activate.constants.ActivateCommonConstant;
import com.lloydsbanking.salsa.constant.ArrangementType;
import com.lloydsbanking.salsa.downstream.switches.SwitchService;
import lib_sim_bo.businessobjects.Individual;
import lib_sim_bo.businessobjects.InternetBankingRegistration;
import lib_sim_bo.businessobjects.ProductArrangement;
import org.apache.cxf.common.util.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.xml.ws.WebServiceException;

@Component
public class ValidateProcessPostFulfilment {
    private static final Logger LOGGER = Logger.getLogger(ValidateProcessPostFulfilment.class);

    private static final String CUSTOMER_SEGMENT_FRANCHISED = "1";
    private static final String CUSTOMER_SEGMENT_NON_FRANCHISED_NON_ALIGNED = "2";

    @Autowired
    public SwitchService switchClient;

    private boolean isCustomerSegmentNonFranchised(ProductArrangement productArrangement, String sourceSystemId) {
        boolean isCustomerSegmentNonFranchised = false;
        String custSegment = productArrangement.getPrimaryInvolvedParty().getCustomerSegment();
        if (ActivateCommonConstant.SourceSystemIdentifier.GALAXY_OAP_SOURCE_SYSTEM_IDENTIFIER.equals(sourceSystemId)) {
            isCustomerSegmentNonFranchised = true;
        } else {
            if (CUSTOMER_SEGMENT_NON_FRANCHISED_NON_ALIGNED.equals(custSegment) || CUSTOMER_SEGMENT_FRANCHISED.equals(custSegment)) {
                isCustomerSegmentNonFranchised = true;
            } else if (productArrangement.getApplicationSubStatus() == null) {
                isCustomerSegmentNonFranchised = true;
            }
        }
        return isCustomerSegmentNonFranchised;
    }

    public boolean isUpdateNINumberRequired(ProductArrangement productArrangement) {
        return (StringUtils.isEmpty(productArrangement.getApplicationSubStatus()) && checkInsuranceNumber(productArrangement.getPrimaryInvolvedParty().getIsPlayedBy())) ||
                (ActivateCommonConstant.AppSubStatus.FAILED_TO_UPDATE_NI_NUMBER).equalsIgnoreCase(productArrangement.getApplicationSubStatus());
    }

    private boolean checkInsuranceNumber(Individual individual) {
        return (individual != null && !StringUtils.isEmpty(individual.getNationalInsuranceNumber()));
    }

    public boolean checkAppSubStatus(String appSubStatus, String appSubStatusToBeCompared) {
        return (appSubStatus == null || appSubStatus.equalsIgnoreCase(appSubStatusToBeCompared));
    }

    public boolean isUpdateEmailAddressRequired(ProductArrangement productArrangement, String sourceSystemID) {
        return (productArrangement.getApplicationSubStatus() == null && isCustomerSegmentNonFranchised(productArrangement, sourceSystemID)) ||
                ActivateCommonConstant.AppSubStatus.FAILED_TO_UPDATE_EMAIL_ADDRESS.equalsIgnoreCase(productArrangement.getApplicationSubStatus());
    }

    public boolean isUpdateMarketingPreferencesRequired(ProductArrangement productArrangement) {
        if (StringUtils.isEmpty(productArrangement.getApplicationSubStatus()) ||
                ActivateCommonConstant.AppSubStatus.MARKETING_PREF_UPDATE_FAILURE.equalsIgnoreCase(productArrangement.getApplicationSubStatus())) {
            if (ArrangementType.SAVINGS.getValue().equalsIgnoreCase(productArrangement.getArrangementType()) || ArrangementType.CURRENT_ACCOUNT.getValue().equalsIgnoreCase(productArrangement.getArrangementType())) {
                return true;
            }
        }
        return false;
    }

    public boolean retrieveSwitchValue(String channel, String switchName) {
        try {
            return switchClient.getGlobalSwitchValue(switchName, channel, false);
        } catch (WebServiceException e) {
            LOGGER.info("Error occurred while fetching Switch value for channel " + channel + e);
            return false;
        }
    }

    public boolean retrieveSwitchValueBranded(String channel, String switchName) {
        try {
            return switchClient.getBrandedSwitchValue(switchName, channel, false);
        } catch (WebServiceException e) {
            LOGGER.info("Error occurred while fetching Switch value for channel " + channel + e);
            return false;
        }
    }

    public boolean isActivateIBApplicationRequired(InternetBankingRegistration internetBankingRegistration) {
        return null != internetBankingRegistration && !StringUtils.isEmpty(internetBankingRegistration.getRegistrationIdentifier());
    }
}

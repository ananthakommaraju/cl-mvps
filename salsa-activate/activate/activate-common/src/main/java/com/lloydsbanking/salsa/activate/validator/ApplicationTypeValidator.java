package com.lloydsbanking.salsa.activate.validator;


import com.lloydsbanking.salsa.activate.registration.RegistrationService;
import com.lloydsbanking.salsa.activate.constants.ActivateCommonConstant;

import com.lloydsbanking.salsa.activate.utility.ExceptionUtilityActivate;
import lib_sim_bo.businessobjects.ProductArrangement;
import lib_sim_gmo.messages.RequestHeader;
import lib_sim_salesprocessmanagement.ia_activateproductarrangement.ActivateProductArrangementInternalSystemErrorMsg;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ApplicationTypeValidator {

    private static final Logger LOGGER = Logger.getLogger(ApplicationTypeValidator.class);
    @Autowired
    ExceptionUtilityActivate exceptionUtilityActivate;
    @Autowired
    RegistrationService registrationService;

    public boolean checkApplicationTypeForArrangement(ProductArrangement productArrangement, RequestHeader header) throws ActivateProductArrangementInternalSystemErrorMsg {

        if (null != productArrangement.getApplicationType() && productArrangement.getApplicationType().equals(ActivateCommonConstant.ApplicationType.TRADE)) {
            return true;
        } else if (isApplicationTypeJointOrNew(productArrangement.getApplicationType())) {
            registrationService.serviceCallForIBRegistration(header, productArrangement);
            return false;
        } else {
            LOGGER.error("The Product Eligibility type of the application is invalid. Error code returned: 820001");
            throw exceptionUtilityActivate.internalServiceError("820001", "The Product eligibility type of the application is invalid", header);
        }
    }

    private boolean isApplicationTypeJointOrNew(String applicationType) {
        boolean isValidApplicationType = false;
        if (null != applicationType) {
            isValidApplicationType = applicationType.equals(ActivateCommonConstant.ApplicationType.JOINT) ||
                                     applicationType.equals(ActivateCommonConstant.ApplicationType.NEW);
        }
        return isValidApplicationType;
    }


}

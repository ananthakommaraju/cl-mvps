package com.lloydsbanking.salsa.eligibility.service.validator;

import com.lloydsbanking.salsa.eligibility.service.utility.ExceptionUtility;
import com.lloydsbanking.salsa.eligibility.service.utility.exceptions.Description;
import lb_gbo_sales.ProductArrangement;
import lb_gbo_sales.interfaces.ia_determineelegiblecustomerinstructions.DetermineEligibleInstructionsExternalBusinessErrorMsg;
import lb_gbo_sales.interfaces.ia_determineelegiblecustomerinstructions.DetermineEligibleInstructionsInternalServiceErrorMsg;
import lb_gbo_sales.messages.DetermineElegibileInstructionsRequest;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

public class RequestValidator {
    private static final Logger LOGGER = Logger.getLogger(RequestValidator.class);

    ExceptionUtility exceptionUtility;

    @Autowired
    public RequestValidator(ExceptionUtility exceptionUtility) {
        this.exceptionUtility = exceptionUtility;

    }

    public void validateRequest(DetermineElegibileInstructionsRequest request) throws DetermineEligibleInstructionsInternalServiceErrorMsg, DetermineEligibleInstructionsExternalBusinessErrorMsg {

        if (!isValidProductArrangementAvailable(request)) {
            LOGGER.error("ProductArrangement for eligibility is null");
            throw exceptionUtility.internalServiceError(null, new Description("No product arrangements supplied"), request.getHeader());
        }
        if (!isValidAccountTypeForProductProductArrangement(request)) {
            throw exceptionUtility.externalBusinessError("1234", null, "Account Type passed is NULL", request.getHeader());
        }
    }

    private boolean isValidProductArrangementAvailable(DetermineElegibileInstructionsRequest request) throws DetermineEligibleInstructionsInternalServiceErrorMsg {
        if (null != request && null != request.getCustomerArrangements() && !request.getCustomerArrangements().isEmpty()) {
            return true;
        }
        return false;
    }

    private boolean isValidAccountTypeForProductProductArrangement(DetermineElegibileInstructionsRequest request) throws DetermineEligibleInstructionsExternalBusinessErrorMsg {
        if (null != request && null != request.getCustomerArrangements()) {
            for (ProductArrangement productArrangement : request.getCustomerArrangements()) {
                if (StringUtils.isEmpty(productArrangement.getAccountType())) {
                    LOGGER.error("Account Type of ProductArrangement for eligibility is null");
                    return false;
                }
            }
        }
        return true;
    }

}
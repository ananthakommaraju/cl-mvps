package com.lloydsbanking.salsa.eligibility.service.utility;

import com.lloydsbanking.salsa.eligibility.service.utility.exceptions.SalsaExternalBusinessException;
import com.lloydsbanking.salsa.eligibility.service.utility.exceptions.SalsaExternalServiceException;
import com.lloydsbanking.salsa.eligibility.service.utility.exceptions.SalsaInternalResourceNotAvailableException;
import com.lloydsbanking.salsa.eligibility.service.utility.exceptions.SalsaInternalServiceException;
import lb_gbo_sales.interfaces.ia_determineelegiblecustomerinstructions.DetermineEligibleInstructionsExternalBusinessErrorMsg;
import lb_gbo_sales.interfaces.ia_determineelegiblecustomerinstructions.DetermineEligibleInstructionsExternalServiceErrorMsg;
import lb_gbo_sales.interfaces.ia_determineelegiblecustomerinstructions.DetermineEligibleInstructionsInternalServiceErrorMsg;
import lb_gbo_sales.interfaces.ia_determineelegiblecustomerinstructions.DetermineEligibleInstructionsResourceNotAvailableErrorMsg;
import lb_gbo_sales.messages.RequestHeader;
import lib_sim_salesprocessmanagement.ia_determineeligiblecustomerinstruction.DetermineEligibleCustomerInstructionsExternalBusinessErrorMsg;
import lib_sim_salesprocessmanagement.ia_determineeligiblecustomerinstruction.DetermineEligibleCustomerInstructionsExternalServiceErrorMsg;
import lib_sim_salesprocessmanagement.ia_determineeligiblecustomerinstruction.DetermineEligibleCustomerInstructionsInternalServiceErrorMsg;
import lib_sim_salesprocessmanagement.ia_determineeligiblecustomerinstruction.DetermineEligibleCustomerInstructionsResourceNotAvailableErrorMsg;
import org.apache.log4j.Logger;

public class EligibilityException extends Exception {

    final Throwable wrappedException;

    private static final Logger LOGGER = Logger.getLogger(EligibilityException.class);

    public EligibilityException(Exception we) {
        this.wrappedException = we;
    }

    public void getExternalException(RequestHeader header, String candidateInstruction, String rule, ExceptionUtility exceptionUtility) throws DetermineEligibleInstructionsResourceNotAvailableErrorMsg, DetermineEligibleInstructionsInternalServiceErrorMsg, DetermineEligibleInstructionsExternalServiceErrorMsg, DetermineEligibleInstructionsExternalBusinessErrorMsg {
        if (wrappedException instanceof SalsaExternalBusinessException) {
            String message = String.format("Exception while evaluating rule %s for candidate Instruction %s.", rule, candidateInstruction);
            throw exceptionUtility.externalBusinessError(message, (SalsaExternalBusinessException)wrappedException, header);
        }
        else if (wrappedException instanceof SalsaExternalServiceException) {
            String message = String.format("Exception while evaluating rule %s for candidate Instruction %s.", rule, candidateInstruction);
            throw exceptionUtility.externalServiceError(message, (SalsaExternalServiceException)wrappedException, header);
        }
        else if (wrappedException instanceof SalsaInternalServiceException) {
            String message = String.format("Exception while evaluating rule %s for candidate Instruction %s.", rule, candidateInstruction);
            throw exceptionUtility.internalServiceError(message, (SalsaInternalServiceException)wrappedException, header);
        }
        else if (wrappedException instanceof SalsaInternalResourceNotAvailableException) {
            String message = String.format("threwSalsaInternalResourceNotAvailableException while evaluating rule %s for candidate Instruction %s.", rule, candidateInstruction);
            LOGGER.error(message, wrappedException);
            throw exceptionUtility.resourceNotAvailableError(header, message);
        }
    }

    public void getExternalException(lib_sim_gmo.messages.RequestHeader header, String candidateInstruction, String rule, com.lloydsbanking.salsa.eligibility.service.utility.wz.ExceptionUtility exceptionUtilityWZ) throws DetermineEligibleCustomerInstructionsExternalBusinessErrorMsg, DetermineEligibleCustomerInstructionsExternalServiceErrorMsg, DetermineEligibleCustomerInstructionsInternalServiceErrorMsg, DetermineEligibleCustomerInstructionsResourceNotAvailableErrorMsg {
        if (wrappedException instanceof SalsaExternalBusinessException ) {
            String message = String.format("Exception while evaluating rule %s for candidate Instruction %s.", rule, candidateInstruction);
            throw exceptionUtilityWZ.externalBusinessError(message, (SalsaExternalBusinessException)wrappedException, header);
        }
        else if (wrappedException instanceof SalsaExternalServiceException) {
            String message = String.format("Exception while evaluating rule %s for candidate Instruction %s.", rule, candidateInstruction);
            throw exceptionUtilityWZ.externalServiceError(message, (SalsaExternalServiceException)wrappedException, header);
        }
        else if (wrappedException instanceof SalsaInternalServiceException) {
            String message = String.format("Exception while evaluating rule %s for candidate Instruction %s.", rule, candidateInstruction);
            throw exceptionUtilityWZ.internalServiceError(message, (SalsaInternalServiceException)wrappedException, header);
        }
        else if (wrappedException instanceof SalsaInternalResourceNotAvailableException) {
            String message = String.format("threwSalsaInternalResourceNotAvailableException while evaluating rule %s for candidate Instruction %s.", rule, candidateInstruction);
            LOGGER.error(message, wrappedException);
            throw exceptionUtilityWZ.resourceNotAvailableError(header, message);
        }
    }
}

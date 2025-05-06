package com.lloydsbanking.salsa.offer.downstream;


import com.lloydsbanking.salsa.offer.exception.ExceptionUtility;
import lib_sim_gmo.exception.DataNotAvailableErrorMsg;
import lib_sim_gmo.exception.InternalServiceErrorMsg;
import lib_sim_gmo.exception.ResourceNotAvailableErrorMsg;
import lib_sim_salesprocessmanagement.ia_determineeligiblecustomerinstruction.DetermineEligibleCustomerInstructionsDataNotAvailableErrorMsg;
import lib_sim_salesprocessmanagement.ia_determineeligiblecustomerinstruction.DetermineEligibleCustomerInstructionsExternalBusinessErrorMsg;
import lib_sim_salesprocessmanagement.ia_determineeligiblecustomerinstruction.DetermineEligibleCustomerInstructionsInternalServiceErrorMsg;
import lib_sim_salesprocessmanagement.ia_determineeligiblecustomerinstruction.DetermineEligibleCustomerInstructionsResourceNotAvailableErrorMsg;
import lib_sim_salesprocessmanagement.messages.DetermineEligibleCustomerInstructionsRequest;
import lib_sim_salesprocessmanagement.messages.DetermineEligibleCustomerInstructionsResponse;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import javax.xml.ws.WebServiceException;

public class EligibilityRetriever {

    private static final Logger LOGGER = Logger.getLogger(EligibilityRetriever.class);

    @Autowired
    ExceptionUtility exceptionUtility;


    @Autowired
    EligibilityServiceClient eligibilityServiceClient;

    public DetermineEligibleCustomerInstructionsResponse callEligibilityService(DetermineEligibleCustomerInstructionsRequest eligibilityRequest) throws DataNotAvailableErrorMsg, InternalServiceErrorMsg, ResourceNotAvailableErrorMsg {
        DetermineEligibleCustomerInstructionsResponse eligibilityResponse;
        try {
            eligibilityResponse = eligibilityServiceClient.determineEligibility(eligibilityRequest);
        } catch (DetermineEligibleCustomerInstructionsDataNotAvailableErrorMsg errorMsg) {
            LOGGER.error("Exception occurred while calling Determine Eligible Customer Instructions service, Returning DataNotAvailableError ;" + errorMsg.getMessage(), errorMsg);
            throw exceptionUtility.dataNotAvailableError(errorMsg.getFaultInfo().getKey(), errorMsg.getFaultInfo().getField(), errorMsg.getFaultInfo().getEntity(), errorMsg.getFaultInfo().getDescription());
        } catch (DetermineEligibleCustomerInstructionsInternalServiceErrorMsg errorMsg) {
            LOGGER.error("Exception occurred while calling Determine Eligible Customer Instructions service, Returning InternalServiceError ;" + errorMsg.getMessage(), errorMsg);
            throw exceptionUtility.internalServiceError(null, errorMsg.getMessage());
        } catch (lib_sim_salesprocessmanagement.ia_determineeligiblecustomerinstruction.DetermineEligibleCustomerInstructionsExternalServiceErrorMsg | DetermineEligibleCustomerInstructionsExternalBusinessErrorMsg | DetermineEligibleCustomerInstructionsResourceNotAvailableErrorMsg | WebServiceException errorMsg) {
            LOGGER.error("Exception occurred while calling Determine Eligible Customer Instructions service, Returning ResourceNotAvailableError ;" + errorMsg.getMessage(), errorMsg);
            throw exceptionUtility.resourceNotAvailableError(errorMsg.getMessage());
        }
        return eligibilityResponse;
    }

}
package com.lloydsbanking.salsa.offer.downstream;


import com.lloydsbanking.salsa.logging.interceptor.CxfClientLoggingInitialiser;
import lib_sim_salesprocessmanagement.ia_determineeligiblecustomerinstruction.*;
import lib_sim_salesprocessmanagement.ia_determineeligiblecustomerinstruction.binding.ExpIADetermineEligibleCustomerInstructionsSOAPIADetermineEligibleCustomerInstructionsHttpService;
import lib_sim_salesprocessmanagement.messages.DetermineEligibleCustomerInstructionsRequest;
import lib_sim_salesprocessmanagement.messages.DetermineEligibleCustomerInstructionsResponse;

import javax.xml.ws.BindingProvider;
import java.net.URL;

public class EligibilityServiceClient {


    IADetermineEligibleCustomerInstructions port;
    CxfClientLoggingInitialiser loggingInitialiser;

    public EligibilityServiceClient(URL url, CxfClientLoggingInitialiser loggingInitialiser) {
        this.port = new ExpIADetermineEligibleCustomerInstructionsSOAPIADetermineEligibleCustomerInstructionsHttpService().getExpIADetermineEligibleCustomerInstructionsSOAPIADetermineEligibleCustomerInstructionsHttpPort();
        BindingProvider bp = (BindingProvider) port;
        bp.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, url.toString());
        this.loggingInitialiser = loggingInitialiser;
    }

    public DetermineEligibleCustomerInstructionsResponse determineEligibility(DetermineEligibleCustomerInstructionsRequest eligibilityRequest) throws DetermineEligibleCustomerInstructionsInternalServiceErrorMsg, DetermineEligibleCustomerInstructionsDataNotAvailableErrorMsg, DetermineEligibleCustomerInstructionsResourceNotAvailableErrorMsg, DetermineEligibleCustomerInstructionsExternalBusinessErrorMsg, DetermineEligibleCustomerInstructionsExternalServiceErrorMsg {
        loggingInitialiser.initialiseLoggingInterceptors(port);
        return port.determineEligibleCustomerInstructions(eligibilityRequest);
    }
}

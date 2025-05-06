package com.lloydsbanking.salsa.eligibility.client.wz;

import com.lloydsbanking.salsa.logging.interceptor.CxfClientLoggingInitialiser;
import lib_sim_salesprocessmanagement.ia_determineeligiblecustomerinstruction.*;
import lib_sim_salesprocessmanagement.ia_determineeligiblecustomerinstruction.binding.ExpIADetermineEligibleCustomerInstructionsSOAPIADetermineEligibleCustomerInstructionsHttpService;
import lib_sim_salesprocessmanagement.messages.DetermineEligibleCustomerInstructionsRequest;

import javax.xml.ws.BindingProvider;
import java.net.URL;

public class EligibilityClientImpl implements EligibilityClient {
    IADetermineEligibleCustomerInstructions port;

    boolean logSoapMessages;

    String soapMessagesLogFilename = "default-soapMessagesLogFilename.txt";

    CxfClientLoggingInitialiser loggingInitialiser = new CxfClientLoggingInitialiser();

    public EligibilityClientImpl(URL url, boolean logSoapMessages) {
        this.port = new ExpIADetermineEligibleCustomerInstructionsSOAPIADetermineEligibleCustomerInstructionsHttpService().getExpIADetermineEligibleCustomerInstructionsSOAPIADetermineEligibleCustomerInstructionsHttpPort();
        BindingProvider bp = (BindingProvider) port;
        bp.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, url.toString());
        this.logSoapMessages = logSoapMessages;
    }

    @Override
    public lib_sim_salesprocessmanagement.messages.DetermineEligibleCustomerInstructionsResponse determineEligibleInstructions(DetermineEligibleCustomerInstructionsRequest deciRequest) throws DetermineEligibleCustomerInstructionsInternalServiceErrorMsg, DetermineEligibleCustomerInstructionsExternalServiceErrorMsg, DetermineEligibleCustomerInstructionsDataNotAvailableErrorMsg, DetermineEligibleCustomerInstructionsResourceNotAvailableErrorMsg, DetermineEligibleCustomerInstructionsExternalBusinessErrorMsg {
        loggingInitialiser.initialiseLoggingInterceptors(logSoapMessages, "deci", soapMessagesLogFilename, port);
        return port.determineEligibleCustomerInstructions(deciRequest);
    }

    @Override
    public void setSoapMessagesLogFilename(final String soapMessagesLogFilename) {
        this.soapMessagesLogFilename = soapMessagesLogFilename;
    }

}


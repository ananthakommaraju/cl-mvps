package com.lloydsbanking.salsa.eligibility.client;

import com.lloydsbanking.salsa.logging.interceptor.CxfClientLoggingInitialiser;
import lb_gbo_sales.interfaces.ia_determineelegiblecustomerinstructions.DetermineEligibleInstructionsDataNotAvailableErrorfault1Msg;
import lb_gbo_sales.interfaces.ia_determineelegiblecustomerinstructions.DetermineEligibleInstructionsExternalBusinessErrorMsg;
import lb_gbo_sales.interfaces.ia_determineelegiblecustomerinstructions.DetermineEligibleInstructionsExternalServiceErrorMsg;
import lb_gbo_sales.interfaces.ia_determineelegiblecustomerinstructions.DetermineEligibleInstructionsInternalServiceErrorMsg;
import lb_gbo_sales.interfaces.ia_determineelegiblecustomerinstructions.DetermineEligibleInstructionsResourceNotAvailableErrorMsg;
import lb_gbo_sales.interfaces.ia_determineelegiblecustomerinstructions.IADetermineEligibleCustomerInstructions;
import lb_gbo_sales.interfaces.ia_determineelegiblecustomerinstructions.binding2.IADetermineEligibleCustomerInstructionsExport1IADetermineEligibleCustomerInstructionsHttpService;
import lb_gbo_sales.messages.DetermineElegibileInstructionsRequest;
import lb_gbo_sales.messages.DetermineElegibleInstructionsResponse;

import javax.xml.ws.BindingProvider;
import java.net.URL;

public class EligibilityClientImpl implements EligibilityClient {
    IADetermineEligibleCustomerInstructions port;

    boolean logSoapMessages;

    String soapMessagesLogFilename = "default-soapMessagesLogFilename.txt";

    CxfClientLoggingInitialiser loggingInitialiser = new CxfClientLoggingInitialiser();

    public EligibilityClientImpl(URL url, boolean logSoapMessages) {
        this.port = new IADetermineEligibleCustomerInstructionsExport1IADetermineEligibleCustomerInstructionsHttpService().getIADetermineEligibleCustomerInstructionsExport1IADetermineEligibleCustomerInstructionsHttpPort();
        BindingProvider bp = (BindingProvider) port;
        bp.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, url.toString());
        this.logSoapMessages = logSoapMessages;
    }

    @Override
    public DetermineElegibleInstructionsResponse determineEligibleInstructions(DetermineElegibileInstructionsRequest deciRequest) throws DetermineEligibleInstructionsExternalServiceErrorMsg, DetermineEligibleInstructionsResourceNotAvailableErrorMsg, DetermineEligibleInstructionsInternalServiceErrorMsg, DetermineEligibleInstructionsDataNotAvailableErrorfault1Msg, DetermineEligibleInstructionsExternalBusinessErrorMsg {
        loggingInitialiser.initialiseLoggingInterceptors(logSoapMessages, "deci", soapMessagesLogFilename, port);
        return port.determineEligibleInstructions(deciRequest);
    }

    @Override
    public void setSoapMessagesLogFilename(final String soapMessagesLogFilename) {
        this.soapMessagesLogFilename = soapMessagesLogFilename;
    }

}


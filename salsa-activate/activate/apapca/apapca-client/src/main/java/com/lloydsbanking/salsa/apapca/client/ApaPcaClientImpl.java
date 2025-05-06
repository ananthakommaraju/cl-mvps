package com.lloydsbanking.salsa.apapca.client;

import com.lloydsbanking.salsa.logging.interceptor.CxfClientLoggingInitialiser;
import lib_sim_salesprocessmanagement.ia_activateproductarrangement.*;
import lib_sim_salesprocessmanagement.ia_activateproductarrangement.binding.ExpIAActivateProductArrangementExport1IAActivateProductArrangementHttpService;
import lib_sim_salesprocessmanagement.messages.ActivateProductArrangementRequest;
import lib_sim_salesprocessmanagement.messages.ActivateProductArrangementResponse;

import javax.xml.ws.BindingProvider;
import java.net.URL;

public class ApaPcaClientImpl implements ApaPcaClient {


    IAActivateProductArrangement port;

    boolean logSoapMessages;

    String soapMessagesLogFilename = "default-soapMessagesLogFilename.txt";

    CxfClientLoggingInitialiser loggingInitialiser = new CxfClientLoggingInitialiser();

    public ApaPcaClientImpl(URL url, boolean logSoapMessages) {
        this.port = new ExpIAActivateProductArrangementExport1IAActivateProductArrangementHttpService().getExpIAActivateProductArrangementExport1IAActivateProductArrangementHttpPort();
        BindingProvider bp = (BindingProvider) port;
        bp.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, url.toString());
        this.logSoapMessages = logSoapMessages;
    }


    @Override
    public ActivateProductArrangementResponse activateProductArrangement(final ActivateProductArrangementRequest activateProductArrangementRequest) throws ActivateProductArrangementExternalSystemErrorMsg, ActivateProductArrangementExternalBusinessErrorMsg, ActivateProductArrangementInternalSystemErrorMsg, ActivateProductArrangementResourceNotAvailableErrorMsg, ActivateProductArrangementDataNotAvailableErrorMsg {
        loggingInitialiser.initialiseLoggingInterceptors(logSoapMessages, "apapca", soapMessagesLogFilename, port);
        return port.activateProductArrangement(activateProductArrangementRequest);
    }

    @Override
    public void setSoapMessagesLogFilename(final String soapMessagesLogFilename) {
        this.soapMessagesLogFilename = soapMessagesLogFilename;
    }
}

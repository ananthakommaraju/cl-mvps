package com.lloydsbanking.salsa.apaloans.client;

import com.lloydsbanking.salsa.logging.interceptor.CxfClientLoggingInitialiser;
import lib_sim_salesprocessmanagement.ia_activateproductarrangement.IAActivateProductArrangement;
import lib_sim_salesprocessmanagement.ia_activateproductarrangement.ActivateProductArrangementExternalBusinessErrorMsg;
import lib_sim_salesprocessmanagement.ia_activateproductarrangement.ActivateProductArrangementExternalSystemErrorMsg;
import lib_sim_salesprocessmanagement.ia_activateproductarrangement.ActivateProductArrangementInternalSystemErrorMsg;
import lib_sim_salesprocessmanagement.ia_activateproductarrangement.ActivateProductArrangementResourceNotAvailableErrorMsg;
import lib_sim_salesprocessmanagement.ia_activateproductarrangement.ActivateProductArrangementDataNotAvailableErrorMsg;
import lib_sim_salesprocessmanagement.ia_activateproductarrangement.binding.ExpIAActivateProductArrangementExport1IAActivateProductArrangementHttpService;
import lib_sim_salesprocessmanagement.messages.ActivateProductArrangementRequest;
import lib_sim_salesprocessmanagement.messages.ActivateProductArrangementResponse;

import javax.xml.ws.BindingProvider;
import java.net.URL;

public class ApaloansClientImpl implements ApaloansClient {


    IAActivateProductArrangement port;

    boolean logSoapMessages;

    String soapMessagesLogFilename = "default-soapMessagesLogFilename.txt";

    CxfClientLoggingInitialiser loggingInitialiser = new CxfClientLoggingInitialiser();

    public ApaloansClientImpl(URL url, boolean logSoapMessages) {
        this.port = new ExpIAActivateProductArrangementExport1IAActivateProductArrangementHttpService().getExpIAActivateProductArrangementExport1IAActivateProductArrangementHttpPort();
        BindingProvider bp = (BindingProvider) port;
        bp.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, url.toString());
        this.logSoapMessages = logSoapMessages;
    }


    @Override
    public ActivateProductArrangementResponse activateProductArrangement(final ActivateProductArrangementRequest activateProductArrangementRequest) throws ActivateProductArrangementExternalSystemErrorMsg, ActivateProductArrangementExternalBusinessErrorMsg, ActivateProductArrangementInternalSystemErrorMsg, ActivateProductArrangementResourceNotAvailableErrorMsg, ActivateProductArrangementDataNotAvailableErrorMsg {
        loggingInitialiser.initialiseLoggingInterceptors(logSoapMessages, "apaloans", soapMessagesLogFilename, port);
        return port.activateProductArrangement(activateProductArrangementRequest);
    }

    @Override
    public void setSoapMessagesLogFilename(final String soapMessagesLogFilename) {
        this.soapMessagesLogFilename = soapMessagesLogFilename;
    }
}

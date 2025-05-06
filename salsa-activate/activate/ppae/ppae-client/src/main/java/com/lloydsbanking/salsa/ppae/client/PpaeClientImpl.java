package com.lloydsbanking.salsa.ppae.client;


import com.lloydsbanking.salsa.logging.interceptor.CxfClientLoggingInitialiser;
import lib_sim_salesprocessmanagement.ia_processpendingarrangementevent.IAProcessPendingArrangementEvent;
import lib_sim_salesprocessmanagement.ia_processpendingarrangementevent.binding.IAProcessPendingArrangementEventExport1IAProcessPendingArrangementEventHttpService;
import lib_sim_salesprocessmanagement.messages.ProcessPendingArrangementEventRequest;

import javax.xml.ws.BindingProvider;
import java.net.URL;

public class PpaeClientImpl implements PpaeClient {


    boolean logSoapMessages;
    IAProcessPendingArrangementEvent port;

    String soapMessagesLogFilename = "default-soapMessagesLogFilename.txt";

    CxfClientLoggingInitialiser loggingInitialiser = new CxfClientLoggingInitialiser();

    public PpaeClientImpl(URL url, boolean logSoapMessages) {
        this.port = new IAProcessPendingArrangementEventExport1IAProcessPendingArrangementEventHttpService().getIAProcessPendingArrangementEventExport1IAProcessPendingArrangementEventHttpPort();
        BindingProvider bp = (BindingProvider) port;
        bp.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, url.toString());
        this.logSoapMessages = logSoapMessages;
    }

    @Override
    public void processPendingArrangementEvent(ProcessPendingArrangementEventRequest processPendingArrangementEventRequest) {
        loggingInitialiser.initialiseLoggingInterceptors(logSoapMessages, "ppae", soapMessagesLogFilename, port);
        port.processPendingArrangementEvent(processPendingArrangementEventRequest);
    }

    @Override
    public void setSoapMessagesLogFilename(final String soapMessagesLogFilename) {
        this.soapMessagesLogFilename = soapMessagesLogFilename;
    }
}

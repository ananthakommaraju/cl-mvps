package com.lloydsbanking.salsa.opaloans.client;

import com.lloydsbanking.salsa.logging.interceptor.CxfClientLoggingInitialiser;
import lib_sim_salesprocessmanagement.ia_offerproductarrangement.*;
import lib_sim_salesprocessmanagement.ia_offerproductarrangement.binding2.TSKSalesProcessManagementExpIAOfferProductArrangementExportSavings;
import lib_sim_salesprocessmanagement.messages.OfferProductArrangementRequest;
import lib_sim_salesprocessmanagement.messages.OfferProductArrangementResponse;

import javax.xml.ws.BindingProvider;
import java.net.URL;

public class OpaLoansClientImpl implements OpaLoansClient {
    IAOfferProductArrangement port;

    boolean logSoapMessages;

    String soapMessagesLogFilename = "default-soapMessagesLogFilename.txt";

    CxfClientLoggingInitialiser loggingInitialiser = new CxfClientLoggingInitialiser();

    public OpaLoansClientImpl(URL url, boolean logSoapMessages) {
        this.port = new TSKSalesProcessManagementExpIAOfferProductArrangementExportSavings().getExpIAOfferProductArrangementExportSavingsIAOfferProductArrangementHttpPort();
        BindingProvider bp = (BindingProvider) port;
        bp.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, url.toString());
        this.logSoapMessages = logSoapMessages;
    }

    @Override
    public OfferProductArrangementResponse offerProductArrangement(final OfferProductArrangementRequest request) throws OfferProductArrangementInternalServiceErrorMsg, OfferProductArrangementExternalBusinessErrorMsg, OfferProductArrangementDataNotAvailableErrorMsg, OfferProductArrangementResourceNotAvailableErrorMsg, OfferProductArrangementExternalServiceErrorMsg {
        loggingInitialiser.initialiseLoggingInterceptors(logSoapMessages, "opaloans", soapMessagesLogFilename, port);
        return port.offerProductArrangement(request);
    }

    @Override
    public void setSoapMessagesLogFilename(final String soapMessagesLogFilename) {
        this.soapMessagesLogFilename = soapMessagesLogFilename;
    }
}

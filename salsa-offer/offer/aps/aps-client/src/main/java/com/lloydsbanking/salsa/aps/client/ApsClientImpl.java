package com.lloydsbanking.salsa.aps.client;

import com.lloydsbanking.salsa.logging.interceptor.CxfClientLoggingInitialiser;
import lib_sim_productsalesreferencedatamanager.id_determineproducteligibilitytype.AdministerProductSelectionDataNotAvailableErrorMsg;
import lib_sim_productsalesreferencedatamanager.id_determineproducteligibilitytype.AdministerProductSelectionInternalServiceErrorMsg;
import lib_sim_productsalesreferencedatamanager.id_determineproducteligibilitytype.AdministerProductSelectionResourceNotAvailableErrorMsg;
import lib_sim_productsalesreferencedatamanager.id_determineproducteligibilitytype.IDDetermineProductEligibilityType;
import lib_sim_productsalesreferencedatamanager.id_determineproducteligibilitytype.binding.IDDetermineProductEligibilityTypeExport1IDDetermineProductEligibilityTypeHttpService;
import lib_sim_productsalesreferencedatamanager.messages.AdministerProductSelectionRequest;
import lib_sim_productsalesreferencedatamanager.messages.AdministerProductSelectionResponse;

import javax.xml.ws.BindingProvider;
import java.net.URL;

public class ApsClientImpl implements ApsClient {

    IDDetermineProductEligibilityType port;

    boolean logSoapMessages;

    String soapMessagesLogFilename = "default-soapMessagesLogFilename.txt";

    CxfClientLoggingInitialiser loggingInitialiser = new CxfClientLoggingInitialiser();

    public ApsClientImpl(URL url, boolean logSoapMessages) {
        this.port = new IDDetermineProductEligibilityTypeExport1IDDetermineProductEligibilityTypeHttpService().getIDDetermineProductEligibilityTypeExport1IDDetermineProductEligibilityTypeHttpPort();
        BindingProvider bp = (BindingProvider) port;
        bp.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, url.toString());
        this.logSoapMessages = logSoapMessages;
    }

    @Override
    public AdministerProductSelectionResponse administerProductSelection(AdministerProductSelectionRequest request) throws AdministerProductSelectionDataNotAvailableErrorMsg, AdministerProductSelectionInternalServiceErrorMsg, AdministerProductSelectionResourceNotAvailableErrorMsg {
        loggingInitialiser.initialiseLoggingInterceptors(logSoapMessages, "aps", soapMessagesLogFilename, port);
        return port.administerProductSelection(request);
    }

    @Override
    public void setSoapMessagesLogFilename(String soapMessagesLogFilename) {
        this.soapMessagesLogFilename = soapMessagesLogFilename;
    }
}

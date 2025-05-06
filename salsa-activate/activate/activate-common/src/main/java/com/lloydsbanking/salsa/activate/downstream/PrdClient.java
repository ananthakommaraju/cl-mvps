package com.lloydsbanking.salsa.activate.downstream;

import com.lloydsbanking.salsa.logging.interceptor.CxfClientLoggingInitialiser;
import lib_sim_productsalesreferencedatamanager.ia_retrieveproductconditions.IARetrieveProductConditions;
import lib_sim_productsalesreferencedatamanager.ia_retrieveproductconditions.RetrieveProductConditionsDataNotAvailableErrorMsg;
import lib_sim_productsalesreferencedatamanager.ia_retrieveproductconditions.RetrieveProductConditionsExternalBusinessErrorMsg;
import lib_sim_productsalesreferencedatamanager.ia_retrieveproductconditions.RetrieveProductConditionsExternalServiceErrorMsg;
import lib_sim_productsalesreferencedatamanager.ia_retrieveproductconditions.RetrieveProductConditionsInternalServiceErrorMsg;
import lib_sim_productsalesreferencedatamanager.ia_retrieveproductconditions.RetrieveProductConditionsResourceNotAvailableErrorMsg;
import lib_sim_productsalesreferencedatamanager.ia_retrieveproductconditions.binding.ExpIARetrieveProductConditionsSOAPIARetrieveProductConditionsHttpService;
import lib_sim_productsalesreferencedatamanager.messages.RetrieveProductConditionsRequest;
import lib_sim_productsalesreferencedatamanager.messages.RetrieveProductConditionsResponse;

import javax.xml.ws.BindingProvider;
import java.net.URL;

public class PrdClient {
    private final IARetrieveProductConditions port;

    CxfClientLoggingInitialiser loggingInitialiser;

    public PrdClient(URL url, CxfClientLoggingInitialiser loggingInitialiser) {
        this.port = new ExpIARetrieveProductConditionsSOAPIARetrieveProductConditionsHttpService().getExpIARetrieveProductConditionsExportSOAPIARetrieveProductConditionsHttpPort();
        BindingProvider bp = (BindingProvider) port;
        bp.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, url.toString());
        this.loggingInitialiser = loggingInitialiser;
    }

    public RetrieveProductConditionsResponse retrieveProductConditions(RetrieveProductConditionsRequest retrieveProductConditionsRequest) throws RetrieveProductConditionsResourceNotAvailableErrorMsg, RetrieveProductConditionsDataNotAvailableErrorMsg, RetrieveProductConditionsExternalBusinessErrorMsg, RetrieveProductConditionsExternalServiceErrorMsg, RetrieveProductConditionsInternalServiceErrorMsg {
        loggingInitialiser.initialiseLoggingInterceptors(port);
        return port.retrieveProductConditions(retrieveProductConditionsRequest);
    }

}

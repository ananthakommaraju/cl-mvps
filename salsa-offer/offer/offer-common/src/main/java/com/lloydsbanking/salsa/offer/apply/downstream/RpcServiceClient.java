package com.lloydsbanking.salsa.offer.apply.downstream;


import com.lloydsbanking.salsa.logging.interceptor.CxfClientLoggingInitialiser;
import lib_sim_productsalesreferencedatamanager.ia_retrieveproductconditions.*;
import lib_sim_productsalesreferencedatamanager.ia_retrieveproductconditions.binding.ExpIARetrieveProductConditionsSOAPIARetrieveProductConditionsHttpService;
import lib_sim_productsalesreferencedatamanager.messages.RetrieveProductConditionsRequest;
import lib_sim_productsalesreferencedatamanager.messages.RetrieveProductConditionsResponse;

import javax.xml.ws.BindingProvider;
import java.net.URL;

public class RpcServiceClient {

    IARetrieveProductConditions port;
    CxfClientLoggingInitialiser loggingInitialiser;

    public RpcServiceClient(URL url, CxfClientLoggingInitialiser loggingInitialiser) {

        this.port = new ExpIARetrieveProductConditionsSOAPIARetrieveProductConditionsHttpService().getExpIARetrieveProductConditionsExportSOAPIARetrieveProductConditionsHttpPort();
        BindingProvider bp = (BindingProvider) port;
        bp.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, url.toString());
        this.loggingInitialiser = loggingInitialiser;

    }


    public RetrieveProductConditionsResponse retrieveProductConditions(RetrieveProductConditionsRequest rpcRequest) throws RetrieveProductConditionsInternalServiceErrorMsg, RetrieveProductConditionsDataNotAvailableErrorMsg, RetrieveProductConditionsExternalBusinessErrorMsg, RetrieveProductConditionsResourceNotAvailableErrorMsg, RetrieveProductConditionsExternalServiceErrorMsg {
        loggingInitialiser.initialiseLoggingInterceptors(port);
        return port.retrieveProductConditions(rpcRequest);
    }
}

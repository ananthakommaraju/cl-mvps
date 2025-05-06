package com.lloydsbanking.salsa.ppae.service.client;


import com.lloydsbanking.salsa.logging.interceptor.CxfClientLoggingInitialiser;
import lib_sim_salesprocessmanagement.ia_activateproductarrangement.*;
import lib_sim_salesprocessmanagement.ia_activateproductarrangement.binding.ExpIAActivateProductArrangementExport1IAActivateProductArrangementHttpService;
import lib_sim_salesprocessmanagement.messages.ActivateProductArrangementRequest;
import lib_sim_salesprocessmanagement.messages.ActivateProductArrangementResponse;
import org.apache.log4j.Logger;

import javax.xml.ws.BindingProvider;
import java.net.URL;

public class ActivateProductSAClient {

    private static final Logger LOGGER = Logger.getLogger(ActivateProductSAClient.class);
    IAActivateProductArrangement port;

    CxfClientLoggingInitialiser loggingInitialiser;


    public ActivateProductSAClient(URL url, CxfClientLoggingInitialiser loggingInitialiser) {
        this.port = new ExpIAActivateProductArrangementExport1IAActivateProductArrangementHttpService().getExpIAActivateProductArrangementExport1IAActivateProductArrangementHttpPort();
        BindingProvider bp = (BindingProvider) port;
        bp.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, url.toString());
        this.loggingInitialiser = loggingInitialiser;
    }


    public ActivateProductArrangementResponse activateProductArrangement(final ActivateProductArrangementRequest activateProductArrangementRequest) throws ActivateProductArrangementExternalSystemErrorMsg, ActivateProductArrangementExternalBusinessErrorMsg, ActivateProductArrangementInternalSystemErrorMsg, ActivateProductArrangementResourceNotAvailableErrorMsg, ActivateProductArrangementDataNotAvailableErrorMsg {
        LOGGER.info("Entering activateProductArrangement for Product Type " +activateProductArrangementRequest.getProductArrangement().getArrangementType()+"and product type"+activateProductArrangementRequest.getProductArrangement().getApplicationStatus());
        loggingInitialiser.initialiseLoggingInterceptors(port);
        return port.activateProductArrangement(activateProductArrangementRequest);
    }
}

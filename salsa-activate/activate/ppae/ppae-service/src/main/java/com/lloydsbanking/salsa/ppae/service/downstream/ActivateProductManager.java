package com.lloydsbanking.salsa.ppae.service.downstream;


import com.lloydsbanking.salsa.ppae.service.client.ActivateProductPCAClient;
import com.lloydsbanking.salsa.ppae.service.client.ActivateProductCCClient;
import com.lloydsbanking.salsa.ppae.service.client.ActivateProductSAClient;
import lib_sim_bo.businessobjects.ProductArrangement;
import lib_sim_gmo.messages.RequestHeader;
import lib_sim_salesprocessmanagement.ia_activateproductarrangement.*;
import lib_sim_salesprocessmanagement.messages.ActivateProductArrangementRequest;
import lib_sim_salesprocessmanagement.messages.ActivateProductArrangementResponse;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ActivateProductManager {

    private static final Logger LOGGER = Logger.getLogger(ActivateProductManager.class);

    @Autowired
    ActivateProductPCAClient activateProductPCAClient;

    @Autowired
    ActivateProductSAClient activateProductSAClient;

    @Autowired
    ActivateProductCCClient activateProductCCClient;

    private static final String SOURCE_SYSTEM_IDENTIFIER = "2";

    private static final String CREDIT_CARD = "CC";

    private static final String CURRENT_ACCOUNT = "CA";

    private static final String SAVINGS = "SA";

    ActivateProductArrangementResponse activateProductArrangementResponse = new ActivateProductArrangementResponse();
    ActivateProductArrangementRequest activateProductArrangementRequest = new ActivateProductArrangementRequest();

    public ActivateProductArrangementResponse activateProduct(ProductArrangement productArrangement, RequestHeader requestHeader) throws ActivateProductArrangementInternalSystemErrorMsg, ActivateProductArrangementExternalBusinessErrorMsg, ActivateProductArrangementExternalSystemErrorMsg, ActivateProductArrangementResourceNotAvailableErrorMsg, ActivateProductArrangementDataNotAvailableErrorMsg {
        activateProductArrangementRequest = createActivateRequest(productArrangement, requestHeader);

        switch (productArrangement.getArrangementType()) {
            case CREDIT_CARD:
                activateProductArrangementResponse = activateProductCCClient.activateProductArrangement(activateProductArrangementRequest);
                break;
            case CURRENT_ACCOUNT:
                activateProductArrangementResponse = activateProductPCAClient.activateProductArrangement(activateProductArrangementRequest);
                break;
            case SAVINGS:
                activateProductArrangementResponse = activateProductSAClient.activateProductArrangement(activateProductArrangementRequest);
                break;
            default:
        }
        LOGGER.info("Exiting activateProduct with AppId | appStatus | productType | RetryCount: " + productArrangement.getArrangementId() + " | " + productArrangement.getApplicationStatus() + " | " + productArrangement.getArrangementType() + " | " +productArrangement.getRetryCount());
        return activateProductArrangementResponse;

    }

    public ActivateProductArrangementRequest createActivateRequest(ProductArrangement productArrangement, RequestHeader requestHeader) {
        activateProductArrangementRequest.setProductArrangement(productArrangement);
        activateProductArrangementRequest.setHeader(requestHeader);
        activateProductArrangementRequest.setSourceSystemIdentifier(SOURCE_SYSTEM_IDENTIFIER);
        return activateProductArrangementRequest;
    }
}


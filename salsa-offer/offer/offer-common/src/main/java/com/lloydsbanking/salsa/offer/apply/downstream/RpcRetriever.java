package com.lloydsbanking.salsa.offer.apply.downstream;

import com.lloydsbanking.salsa.offer.apply.convert.OfferToRpcRequestConverter;
import com.lloydsbanking.salsa.offer.exception.ExceptionUtility;
import lib_sim_gmo.exception.DataNotAvailableErrorMsg;
import lib_sim_gmo.exception.InternalServiceErrorMsg;
import lib_sim_gmo.exception.ResourceNotAvailableErrorMsg;
import lib_sim_productsalesreferencedatamanager.ia_retrieveproductconditions.*;
import lib_sim_productsalesreferencedatamanager.messages.RetrieveProductConditionsRequest;
import lib_sim_productsalesreferencedatamanager.messages.RetrieveProductConditionsResponse;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import javax.xml.ws.WebServiceException;

public class RpcRetriever {

    private static final Logger LOGGER = Logger.getLogger(RpcRetriever.class);

    @Autowired
    ExceptionUtility exceptionUtility;

    @Autowired
    OfferToRpcRequestConverter offerToRpcRequestConverter;

    @Autowired(required = false)
    RpcServiceClient rpcServiceClient;

    public RetrieveProductConditionsResponse callRpcService(RetrieveProductConditionsRequest retrieveProductConditionsRequest) throws DataNotAvailableErrorMsg, InternalServiceErrorMsg, ResourceNotAvailableErrorMsg {
        LOGGER.info("Entering RetrieveProductConditions RPC service");
        RetrieveProductConditionsResponse rpcResponse;
        try {
            LOGGER.info("Calling RetrieveProductConditions RPC");
            rpcResponse = rpcServiceClient.retrieveProductConditions(retrieveProductConditionsRequest);
        } catch (RetrieveProductConditionsDataNotAvailableErrorMsg errorMsg) {
            LOGGER.error("Exception occurred while calling Retrieve Product Conditions service. Returning DataNotAvailableError ;" + errorMsg.getFaultInfo().getDescription(), errorMsg);
            throw exceptionUtility.dataNotAvailableError(errorMsg.getFaultInfo().getKey(), errorMsg.getFaultInfo().getField(), errorMsg.getFaultInfo().getEntity(), errorMsg.getFaultInfo().getDescription());
        } catch (RetrieveProductConditionsInternalServiceErrorMsg errorMsg) {
            LOGGER.error("Exception occurred while calling Retrieve Product Conditions service. Returning InternalServiceError ;" + errorMsg.getMessage(), errorMsg);
            throw exceptionUtility.internalServiceError(null, errorMsg.getMessage());
        } catch (RetrieveProductConditionsResourceNotAvailableErrorMsg | RetrieveProductConditionsExternalBusinessErrorMsg | RetrieveProductConditionsExternalServiceErrorMsg | WebServiceException errorMsg) {
            LOGGER.error("Exception occurred while calling Retrieve Product Conditions service. Returning ResourceNotAvailableError ;" + errorMsg.getMessage(), errorMsg);
            throw exceptionUtility.resourceNotAvailableError(errorMsg.getMessage());
        }
        LOGGER.info("Exiting RetrieveProductConditions RPC service");
        return rpcResponse;
    }
}

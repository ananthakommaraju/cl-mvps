package com.lloydsbanking.salsa.offer.apply.downstream;

import com.lloydsbanking.salsa.logging.application.ProductFamilyTraceLog;
import com.lloydsbanking.salsa.logging.application.ProductTraceLog;
import com.lloydsbanking.salsa.offer.OfferException;
import com.lloydsbanking.salsa.offer.apply.utility.OfferedProductsSorter;
import lib_sim_bo.businessobjects.Product;
import lib_sim_gmo.exception.DataNotAvailableErrorMsg;
import lib_sim_gmo.exception.InternalServiceErrorMsg;
import lib_sim_gmo.exception.ResourceNotAvailableErrorMsg;
import lib_sim_productsalesreferencedatamanager.messages.RetrieveProductConditionsRequest;
import lib_sim_productsalesreferencedatamanager.messages.RetrieveProductConditionsResponse;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

public class ProductConditionsRetriever {
    private static final Logger LOGGER = Logger.getLogger(ProductConditionsRetriever.class);

    @Autowired
    RpcRetriever rpcRetriever;
    @Autowired
    OfferedProductsSorter offeredProductsSorter;
    @Autowired
    ProductFamilyTraceLog productFamilyTraceLog;
    @Autowired
    ProductTraceLog productTraceLog;


    public RetrieveProductConditionsResponse retrieveRPCResponse(RetrieveProductConditionsRequest retrieveProductConditionsRequest, Product associatedProduct) throws OfferException {
        LOGGER.info(productFamilyTraceLog.getProdFamilyListTraceEventMessage(retrieveProductConditionsRequest.getProductFamily(), "Entering RetrieveProductConditions (PRD) "));
        RetrieveProductConditionsResponse rpcResp = null;
        try {
            rpcResp = rpcRetriever.callRpcService(retrieveProductConditionsRequest);
        } catch (DataNotAvailableErrorMsg | InternalServiceErrorMsg | ResourceNotAvailableErrorMsg errorMsg) {
            throw new OfferException(errorMsg);
        }
        LOGGER.info(productTraceLog.getProdListTraceEventMessage(rpcResp.getProduct(), "Exiting RetrieveProductConditions (PRD) "));

        offeredProductsSorter.getSortedProducts(associatedProduct, rpcResp);
        return rpcResp;
    }

}

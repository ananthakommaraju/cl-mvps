package com.lloydsbanking.salsa.opasaving.service.downstream;

import com.lloydsbanking.salsa.downstream.switches.SwitchServiceImpl;
import com.lloydsbanking.salsa.offer.OfferException;
import com.lloydsbanking.salsa.offer.apply.downstream.RpcRetriever;
import lib_sim_bo.businessobjects.DepositArrangement;
import lib_sim_bo.businessobjects.Product;
import lib_sim_gmo.exception.DataNotAvailableErrorMsg;
import lib_sim_gmo.exception.InternalServiceErrorMsg;
import lib_sim_gmo.exception.ResourceNotAvailableErrorMsg;
import lib_sim_gmo.messages.RequestHeader;
import lib_sim_productsalesreferencedatamanager.messages.RetrieveProductConditionsRequest;
import lib_sim_productsalesreferencedatamanager.messages.RetrieveProductConditionsResponse;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

public class RPCService {

    private static final Logger LOGGER = Logger.getLogger(RPCService.class);

    private static final String SCSP_SWITCH = "SW_SvngCstSegPrc";

    @Autowired
    SwitchServiceImpl switchClient;

    @Autowired
    RpcRetriever rpcRetriever;

    @Autowired
    DCPCService dcpcService;

    public void callRPCService(DepositArrangement requestDepositArrangement, RequestHeader requestHeader) throws OfferException {
        boolean isNewCustomer = requestDepositArrangement.getPrimaryInvolvedParty().isNewCustomerIndicator() != null ? requestDepositArrangement.getPrimaryInvolvedParty()
                .isNewCustomerIndicator() : false;
        LOGGER.info("Retrieving SCSP switch value");
        boolean scspSwitchValue = isSCSPSwitchEnabled(requestHeader.getChannelId());
        LOGGER.info("SCSP switch value retrieved is: " + scspSwitchValue);

        if (!isNewCustomer && scspSwitchValue) {
            retrieveProductConditions(requestDepositArrangement, requestHeader);
        }
    }

    private void retrieveProductConditions(DepositArrangement requestDepositArrangement, RequestHeader requestHeader) throws OfferException {
        RetrieveProductConditionsRequest retrieveProductConditionsRequest = convertOfferToRpcRequest(requestDepositArrangement, requestHeader);
        RetrieveProductConditionsResponse rpcResp = null;
        try {
            rpcResp = rpcRetriever.callRpcService(retrieveProductConditionsRequest);
            dcpcService.callDCPCService(rpcResp, requestDepositArrangement, requestHeader);
        } catch (InternalServiceErrorMsg | ResourceNotAvailableErrorMsg | DataNotAvailableErrorMsg errorMsg) {
            throw new OfferException(errorMsg);
        }
    }

    private RetrieveProductConditionsRequest convertOfferToRpcRequest(DepositArrangement requestDepositArrangement, RequestHeader requestHeader) {
        RetrieveProductConditionsRequest retrieveProductConditionsRequest = new RetrieveProductConditionsRequest();
        retrieveProductConditionsRequest.setHeader(requestHeader);
        retrieveProductConditionsRequest.setProduct(new Product());
        retrieveProductConditionsRequest.getProduct().setProductIdentifier(requestDepositArrangement.getAssociatedProduct().getProductIdentifier());
        return retrieveProductConditionsRequest;
    }

    private boolean isSCSPSwitchEnabled(String channel) {
        return switchClient.getBrandedSwitchValue(SCSP_SWITCH, channel);
    }
}
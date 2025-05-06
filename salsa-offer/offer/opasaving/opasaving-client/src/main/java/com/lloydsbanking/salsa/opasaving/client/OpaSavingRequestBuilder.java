package com.lloydsbanking.salsa.opasaving.client;

import lib_sim_bo.businessobjects.ProductArrangement;
import lib_sim_gmo.messages.RequestHeader;
import lib_sim_salesprocessmanagement.messages.OfferProductArrangementRequest;

public class OpaSavingRequestBuilder {
    private OfferProductArrangementRequest request;

    public OpaSavingRequestBuilder() {
        request = new OfferProductArrangementRequest();
    }

    public OfferProductArrangementRequest build() {
        return request;
    }

    public OpaSavingRequestBuilder depositArrangement(final ProductArrangement depositArrangement) {
        request.setProductArrangement(depositArrangement);
        return this;
    }

    public OpaSavingRequestBuilder requestHeader(final RequestHeader requestHeader) {
        request.setHeader(requestHeader);
        return this;
    }
}

package com.lloydsbanking.salsa.opapca.client;

import lib_sim_bo.businessobjects.ProductArrangement;
import lib_sim_gmo.messages.RequestHeader;
import lib_sim_salesprocessmanagement.messages.OfferProductArrangementRequest;

public class OpaPcaRequestBuilder {
    private OfferProductArrangementRequest request;

    public OpaPcaRequestBuilder() {
        request = new OfferProductArrangementRequest();
    }

    public OfferProductArrangementRequest build() {
        return request;
    }

    public OpaPcaRequestBuilder depositArrangement(final ProductArrangement depositArrangement) {
        request.setProductArrangement(depositArrangement);
        return this;
    }

    public OpaPcaRequestBuilder requestHeader(final RequestHeader requestHeader) {
        request.setHeader(requestHeader);
        return this;
    }
}

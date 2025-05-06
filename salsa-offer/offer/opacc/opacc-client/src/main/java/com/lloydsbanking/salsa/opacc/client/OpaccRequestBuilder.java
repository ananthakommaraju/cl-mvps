package com.lloydsbanking.salsa.opacc.client;

import lib_sim_bo.businessobjects.FinanceServiceArrangement;
import lib_sim_gmo.messages.RequestHeader;
import lib_sim_salesprocessmanagement.messages.OfferProductArrangementRequest;

public class OpaccRequestBuilder {
    private OfferProductArrangementRequest request;

    public OpaccRequestBuilder() {
        request = new OfferProductArrangementRequest();
    }

    public OfferProductArrangementRequest build() {
        return request;
    }

    public OpaccRequestBuilder requestHeader(final RequestHeader requestHeader) {
        request.setHeader(requestHeader);
        return this;
    }

    public OpaccRequestBuilder financeServiceArrangement(final FinanceServiceArrangement financeServiceArrangement) {
        request.setProductArrangement(financeServiceArrangement);
        return this;
    }
}

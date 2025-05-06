package com.lloydsbanking.salsa.opaloans.client;

import lib_sim_bo.businessobjects.FinanceServiceArrangement;
import lib_sim_gmo.messages.RequestHeader;
import lib_sim_salesprocessmanagement.messages.OfferProductArrangementRequest;

public class OpaLoansRequestBuilder {
    private OfferProductArrangementRequest request;

    public OpaLoansRequestBuilder() {
        request = new OfferProductArrangementRequest();
    }

    public OfferProductArrangementRequest build() {
        return request;
    }

    public OpaLoansRequestBuilder requestHeader(final RequestHeader requestHeader) {
        request.setHeader(requestHeader);
        return this;
    }

    public OpaLoansRequestBuilder financeServiceArrangement(final FinanceServiceArrangement financeServiceArrangement) {
        request.setProductArrangement(financeServiceArrangement);
        return this;
    }
}

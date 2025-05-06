package com.lloydsbanking.salsa.eligibility.client.wz;



import lib_sim_bo.businessobjects.Customer;
import lib_sim_bo.businessobjects.ProductArrangement;
import lib_sim_gmo.messages.RequestHeader;
import lib_sim_salesprocessmanagement.messages.DetermineEligibleCustomerInstructionsRequest;

import java.util.List;

public class EligibilityRequestBuilder {
    private DetermineEligibleCustomerInstructionsRequest request;

    public EligibilityRequestBuilder() {
        request = new DetermineEligibleCustomerInstructionsRequest();
    }

    public DetermineEligibleCustomerInstructionsRequest build() {
        return request;
    }

    public EligibilityRequestBuilder header(final RequestHeader header) {
        request.setHeader(header);
        return this;
    }

    public EligibilityRequestBuilder candidateInstructions(List<String> candidateInstructions) {
        request.getCandidateInstructions().addAll(candidateInstructions);
        return this;
    }

    public EligibilityRequestBuilder existingProductArrangments(List<ProductArrangement> productArrangments) {
        request.getExistingProductArrangments().addAll(productArrangments);
        return this;
    }
    public EligibilityRequestBuilder customerDetails(Customer customerDetails) {
        request.setCustomerDetails(customerDetails);
        return this;
    }


}

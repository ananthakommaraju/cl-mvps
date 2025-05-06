package com.lloydsbanking.salsa.eligibility.client;

import lb_gbo_sales.ProductArrangement;
import lb_gbo_sales.businessobjects.ArrangementIdentifier;
import lb_gbo_sales.businessobjects.BusinessArrangement;
import lb_gbo_sales.businessobjects.Individual;
import lb_gbo_sales.messages.DetermineElegibileInstructionsRequest;
import lb_gbo_sales.messages.RequestHeader;

import java.util.List;

public class EligibilityRequestBuilder {
    private DetermineElegibileInstructionsRequest request;

    public EligibilityRequestBuilder() {
        request = new DetermineElegibileInstructionsRequest();
    }

    public DetermineElegibileInstructionsRequest build() {
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

    public EligibilityRequestBuilder customerArrangements(List<ProductArrangement> customerArrangements) {
        request.getCustomerArrangements().addAll(customerArrangements);
        return this;
    }

    public EligibilityRequestBuilder individual(Individual individual) {
        request.setIndividual(individual);
        return this;
    }

    public EligibilityRequestBuilder selectedArrangement(ArrangementIdentifier arrangementIdentifier) {
        request.setSelctdArr(arrangementIdentifier);
        return this;
    }

    public EligibilityRequestBuilder businessArrangement(List<BusinessArrangement> businessArrangements) {
        request.getBusinessArrangements().addAll(businessArrangements);
        return this;
    }

    public EligibilityRequestBuilder selctdBusnsId(String selctdBusnsId) {
        request.setSelctdBusnsId(selctdBusnsId);
        return this;
    }
}

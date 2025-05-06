package com.lloydsbanking.salsa.eligibility.service.questions;

import com.lloydsbanking.salsa.eligibility.service.downstream.AppGroupRetriever;
import com.lloydsbanking.salsa.eligibility.service.downstream.CBSIndicatorRetriever;
import com.lloydsbanking.salsa.eligibility.service.utility.ProductArrangementFacade;
import lb_gbo_sales.messages.RequestHeader;

import java.util.List;

public abstract class AbstractProductListQuestion extends ProductListQuestionFacts implements AskQuestion {

    public AbstractProductListQuestion givenAProductList(List<ProductArrangementFacade> productArrangements) {
        this.productArrangements = productArrangements;
        return this;
    }

    public AbstractProductListQuestion givenAValue(String threshold) {
        this.threshold = threshold;
        return this;
    }

    public AbstractProductListQuestion givenAnInstruction(String candidateInstruction) {
        this.candidateInstruction = candidateInstruction;
        return this;
    }

    public AbstractProductListQuestion givenAnAccountType(String accountType) {
        this.accountType = accountType;
        return this;
    }

    public AbstractProductListQuestion givenRequestHeader(RequestHeader requestHeader) {
        this.requestHeader = requestHeader;
        return this;
    }

    public AbstractProductListQuestion givenAppGroupRetrieverClientInstance(AppGroupRetriever appGroupRetriever) {
        this.appGroupRetriever = appGroupRetriever;
        return this;
    }

    public AbstractProductListQuestion givenCbsIndicatorRetriever(CBSIndicatorRetriever cbsIndicatorRetriever) {
        this.cbsIndicatorRetriever = cbsIndicatorRetriever;
        return this;
    }
}

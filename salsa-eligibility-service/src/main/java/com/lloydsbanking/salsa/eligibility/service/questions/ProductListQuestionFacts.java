package com.lloydsbanking.salsa.eligibility.service.questions;

import com.lloydsbanking.salsa.eligibility.service.downstream.AppGroupRetriever;
import com.lloydsbanking.salsa.eligibility.service.downstream.CBSIndicatorRetriever;
import com.lloydsbanking.salsa.eligibility.service.utility.ProductArrangementFacade;
import lb_gbo_sales.messages.RequestHeader;

import java.util.List;

public class ProductListQuestionFacts {
    protected List<ProductArrangementFacade> productArrangements;
    protected String threshold;
    protected String candidateInstruction;
    protected String accountType;
    protected RequestHeader requestHeader;
    protected AppGroupRetriever appGroupRetriever;
    protected CBSIndicatorRetriever cbsIndicatorRetriever;
}

package com.lloydsbanking.salsa.eligibility.service.rules.common;


import com.lloydsbanking.salsa.eligibility.service.utility.ProductArrangementFacade;
import lb_gbo_sales.businessobjects.ArrangementIdentifier;
import lb_gbo_sales.messages.RequestHeader;
import lib_sim_bo.businessobjects.Customer;
import lib_sim_bo.businessobjects.Product;

import java.util.List;


public class RuleDataHolder {
    private String rule;

    private String ruleParamValue;

    private String ruleInsMnemonic;

    private RequestHeader header;

    private Customer customerDetails;

    private ArrangementIdentifier arrangementIdentifier;

    private BusinessArrangementHandler businessArrangement;

    private Product associatedProduct;

    private String arrangementType;

    private String channel;

    private List<ProductArrangementFacade> productArrangementFacade;

    private List<String> candidateInstructions;

    public RequestHeader getHeader() {
        return header;
    }

    public String getRuleParamValue() {
        return ruleParamValue;
    }

    public String getRule() {
        if (rule == null || rule.isEmpty()) {
            throw new IllegalStateException("A rule must be set");
        }
        return rule;
    }

    public void setRule(String rule) {
        this.rule = rule;
    }

    public void setRuleParamValue(String ruleParamValue) {
        this.ruleParamValue = ruleParamValue;
    }

    public void setHeader(RequestHeader header) {
        this.header = header;
    }


    public ArrangementIdentifier getArrangementIdentifier() {
        return arrangementIdentifier;
    }

    public void setArrangementIdentifier(ArrangementIdentifier arrangementIdentifier) {
        this.arrangementIdentifier = arrangementIdentifier;
    }

    public BusinessArrangementHandler getBusinessArrangement() {
        return businessArrangement;
    }

    public void setBusinessArrangement(BusinessArrangementHandler businessArrangement) {
        this.businessArrangement = businessArrangement;
    }

    public Customer getCustomerDetails() {
        return customerDetails;
    }

    public void setCustomerDetails(Customer customerDetails) {
        this.customerDetails = customerDetails;
    }

    public void setRuleInsMnemonic(String ruleInsMnemonic) {
        this.ruleInsMnemonic = ruleInsMnemonic;
    }

    public String getRuleInsMnemonic() {
        return ruleInsMnemonic;
    }


    public void setAssociatedProduct(Product associatedProduct) {
        this.associatedProduct = associatedProduct;
    }

    public Product getAssociatedProduct() {
        return associatedProduct;
    }

    public void setArrangementType(String arrangementType) {
        this.arrangementType = arrangementType;
    }

    public String getArrangementType() {
        return arrangementType;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public void setProductArrangements(List<ProductArrangementFacade> productArrangementFacade) {
        this.productArrangementFacade = productArrangementFacade;
    }

    public List<ProductArrangementFacade> getProductArrangements() {
        return productArrangementFacade;
    }

    public List<String> getCandidateInstructions() {
        return candidateInstructions;
    }

    public void setCandidateInstructions(final List<String> candidateInstructions) {
        this.candidateInstructions = candidateInstructions;
    }
}

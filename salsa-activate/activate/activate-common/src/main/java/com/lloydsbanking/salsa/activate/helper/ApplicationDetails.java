package com.lloydsbanking.salsa.activate.helper;

import lib_sim_bo.businessobjects.*;

import java.util.ArrayList;
import java.util.List;

public class ApplicationDetails {
    private String applicationStatus;
    private String applicationSubStatus;
    private String scoreResult;
    private Integer retryCount;
    private boolean apiFailureFlag;
    private List<Condition> conditionList = new ArrayList<>();
    private List<ReferralCode> referralCodes = new ArrayList<>();
    private CurrencyAmount creditLimit;
    private List<ProductOptions> productOptions = new ArrayList<>();
    private List<ProductFamily> productFamilies = new ArrayList<>();

    public List<ProductFamily> getProductFamilies() {
        return productFamilies;
    }

    public void setProductFamilies(List<ProductFamily> productFamilies) {
        if (productFamilies != null) {
            this.productFamilies.addAll(productFamilies);
        }
    }

    public List<ProductOptions> getProductOptions() {
        return productOptions;
    }

    public void setProductOptions(List<ProductOptions> productOptions) {
        if (productOptions != null) {
            this.productOptions.addAll(productOptions);
        }
    }

    public CurrencyAmount getCreditLimit() {
        return creditLimit;
    }

    public void setCreditLimit(CurrencyAmount creditLimit) {
        this.creditLimit = creditLimit;
    }

    public List<ReferralCode> getReferralCodes() {
        return referralCodes;
    }

    public void setReferralCodes(List<ReferralCode> referralCodes) {
        if (referralCodes != null) {
            this.referralCodes.addAll(referralCodes);
        }
    }

    public List<Condition> getConditionList() {
        return conditionList;
    }

    public String getApplicationStatus() {
        return applicationStatus;
    }

    public void setApplicationStatus(String applicationStatus) {
        this.applicationStatus = applicationStatus;
    }

    public String getApplicationSubStatus() {
        return applicationSubStatus;
    }

    public void setApplicationSubStatus(String applicationSubStatus) {
        this.applicationSubStatus = applicationSubStatus;
    }

    public String getScoreResult() {
        return scoreResult;
    }

    public void setScoreResult(String scoreResult) {
        this.scoreResult = scoreResult;
    }

    public Integer getRetryCount() {
        return retryCount;
    }

    public void setRetryCount(Integer retryCount) {
        this.retryCount = retryCount;
    }

    public boolean isApiFailureFlag() {
        return apiFailureFlag;
    }

    public void setApiFailureFlag(boolean apiFailureFlag) {
        this.apiFailureFlag = apiFailureFlag;
    }
}

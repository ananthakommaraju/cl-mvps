package com.lloydsbanking.salsa.eligibility.service.rules.common;

import lib_sim_bo.businessobjects.ExtraConditions;

public class EligibilityDecision {
    private boolean eligible;

    private String reasonText;

    private KYCStatus kycStatus;

    private String shadowLimit;

    private String riskBand;

    private ExtraConditions extraConditions;

    public ExtraConditions getExtraConditions() {
        return extraConditions;
    }

    public void setExtraConditions(ExtraConditions extraConditions) {
        this.extraConditions = extraConditions;
    }

    public EligibilityDecision(ExtraConditions extraConditions) {
        this.eligible = false;
        this.extraConditions = extraConditions;
    }

    public EligibilityDecision(boolean decision, KYCStatus kycStatus) {
        this.eligible = decision;
        this.kycStatus = kycStatus;
    }

    public EligibilityDecision(boolean decision) {
        this.eligible = decision;
        this.reasonText = null;
    }

    public EligibilityDecision(boolean decision, String shadowLimit, String riskBand) {
        this.eligible = decision;
        this.shadowLimit = shadowLimit;
        this.riskBand = riskBand;
        this.reasonText = null;
    }

    public EligibilityDecision(String reasonText, String shadowLimit, String riskBand) {
        this.eligible = false;
        this.reasonText = reasonText;
        this.shadowLimit = shadowLimit;
        this.riskBand = riskBand;
    }

    public EligibilityDecision(String reasonText, KYCStatus kycStatus) {
        this.reasonText = reasonText;
        this.kycStatus = kycStatus;
    }

    public EligibilityDecision(String reasonText) {
        this.eligible = false;
        this.reasonText = reasonText;
    }

    public String getReasonText() {
        return reasonText;
    }

    public void setReasonText(String reasonText) {
        this.reasonText = reasonText;
    }

    public boolean isEligible() {
        return eligible;
    }

    public KYCStatus getKycStatus() {
        return kycStatus;
    }

    public String getShadowLimit() {
        return shadowLimit;
    }

    public void setShadowLimit(final String shadowLimit) {
        this.shadowLimit = shadowLimit;
    }

    public String getRiskBand() {
        return riskBand;
    }

}

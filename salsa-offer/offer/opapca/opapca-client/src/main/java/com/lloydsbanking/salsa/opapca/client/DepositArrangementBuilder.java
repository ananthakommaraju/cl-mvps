package com.lloydsbanking.salsa.opapca.client;

import lib_sim_bo.businessobjects.*;

import java.util.List;

public class DepositArrangementBuilder {
    DepositArrangement depositArrangement;

    public DepositArrangementBuilder() {
        this.depositArrangement = new DepositArrangement();
    }

    public DepositArrangement build() {
        return depositArrangement;
    }

    public DepositArrangementBuilder arrangementType() {
        depositArrangement.setArrangementType("CA");
        return this;
    }

    public DepositArrangementBuilder associatedProduct(Product associatedProduct) {
        depositArrangement.setAssociatedProduct(associatedProduct);
        return this;
    }

    public DepositArrangementBuilder initiatedThrough(Channel initiatedThrough) {
        depositArrangement.setInitiatedThrough(initiatedThrough);
        return this;
    }

    public DepositArrangementBuilder primaryInvolvedParty(Customer primaryInvolvedParty) {
        depositArrangement.setPrimaryInvolvedParty(primaryInvolvedParty);
        return this;
    }

    public DepositArrangementBuilder marketingPreferenceBySMS(Boolean marketingPreferenceBySMS) {
        depositArrangement.setMarketingPreferenceBySMS(marketingPreferenceBySMS);
        return this;
    }

    public DepositArrangementBuilder applicationType(String applicationType) {
        depositArrangement.setApplicationType(applicationType);
        return this;
    }

    public DepositArrangementBuilder accountPurpose(String accountPurpose) {
        depositArrangement.setAccountPurpose(accountPurpose);
        return this;
    }

    public DepositArrangementBuilder fundingSource(String fundingSource) {
        depositArrangement.setFundingSource(fundingSource);
        return this;
    }

    public DepositArrangementBuilder affiliateDetails(List<AffiliateDetails> affiliateDetails) {
        depositArrangement.getAffiliatedetails().addAll(affiliateDetails);
        return this;
    }

    public DepositArrangementBuilder conditions(List<RuleCondition> conditions) {
        depositArrangement.getConditions().addAll(conditions);
        return this;
    }

    public DepositArrangementBuilder marketingPreferenceByEmail(Boolean marketingPreferenceByEmail) {
        depositArrangement.setMarketingPreferenceByEmail(marketingPreferenceByEmail);
        return this;
    }

    public DepositArrangementBuilder marketingPreferenceByPhone(Boolean marketingPreferenceByPhone) {
        depositArrangement.setMarketingPreferenceByPhone(marketingPreferenceByPhone);
        return this;
    }

    public DepositArrangementBuilder marketingPreferenceByMail(Boolean marketingPreferenceByMail) {
        depositArrangement.setMarketingPreferenceByMail(marketingPreferenceByMail);
        return this;
    }

}

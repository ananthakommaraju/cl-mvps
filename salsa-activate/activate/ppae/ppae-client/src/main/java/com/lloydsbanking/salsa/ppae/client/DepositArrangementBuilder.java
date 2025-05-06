package com.lloydsbanking.salsa.ppae.client;

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

    public DepositArrangementBuilder setAssProductAndIniThrough(Product associatedProduct, Channel initiatedThrough) {
        depositArrangement.setAssociatedProduct(associatedProduct);
        depositArrangement.setInitiatedThrough(initiatedThrough);
        return this;
    }


    public DepositArrangementBuilder primaryInvolvedParty(Customer primaryInvolvedParty) {
        depositArrangement.setPrimaryInvolvedParty(primaryInvolvedParty);
        return this;
    }


    public DepositArrangementBuilder setMarketingPreference(Boolean marketingPreferenceByEmail, Boolean marketingPreferenceByPhone, Boolean marketingPreferenceByMail, Boolean marketingPreferenceBySMS) {
        depositArrangement.setMarketingPreferenceByEmail(marketingPreferenceByEmail);
        depositArrangement.setMarketingPreferenceByPhone(marketingPreferenceByPhone);
        depositArrangement.setMarketingPreferenceByMail(marketingPreferenceByMail);
        depositArrangement.setMarketingPreferenceBySMS(marketingPreferenceBySMS);

        return this;
    }


    public DepositArrangementBuilder applicationTypeAndAccountPurpose(String applicationType, String accountPurpose) {
        depositArrangement.setApplicationType(applicationType);
        depositArrangement.setAccountPurpose(accountPurpose);
        return this;
    }



    public DepositArrangementBuilder fundingSource(String fundingSource) {
        depositArrangement.setFundingSource(fundingSource);
        return this;
    }

    public DepositArrangementBuilder conditions(List<RuleCondition> conditions) {
        depositArrangement.getConditions().addAll(conditions);
        return this;
    }

}
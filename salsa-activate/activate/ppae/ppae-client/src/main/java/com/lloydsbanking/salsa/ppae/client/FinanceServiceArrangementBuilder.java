package com.lloydsbanking.salsa.ppae.client;


import lib_sim_bo.businessobjects.*;

import java.util.List;

public class FinanceServiceArrangementBuilder {

    FinanceServiceArrangement financeServiceArrangement;

    public FinanceServiceArrangementBuilder() {
        this.financeServiceArrangement = new FinanceServiceArrangement();
    }

    public FinanceServiceArrangement build() {
        return financeServiceArrangement;
    }

    public FinanceServiceArrangementBuilder arrangementType() {
        financeServiceArrangement.setArrangementType("CC");
        return this;
    }

    public FinanceServiceArrangementBuilder setAssProductAndIniThrough(Product associatedProduct, Channel initiatedThrough) {
        financeServiceArrangement.setAssociatedProduct(associatedProduct);
        financeServiceArrangement.setInitiatedThrough(initiatedThrough);
        return this;
    }

    public FinanceServiceArrangementBuilder setCodeTypeAndAffiliateId(String campaignCode, String applicationType, String affiliateId) {
        financeServiceArrangement.setCampaignCode(campaignCode);
        financeServiceArrangement.setApplicationType(applicationType);
        financeServiceArrangement.setAffiliateId(affiliateId);
        return this;

    }

    public FinanceServiceArrangementBuilder primaryInvolvedParty(Customer primaryInvolvedParty) {
        financeServiceArrangement.setPrimaryInvolvedParty(primaryInvolvedParty);
        return this;
    }


    public FinanceServiceArrangementBuilder affiliateDetails(List<AffiliateDetails> affiliateDetails) {
        financeServiceArrangement.getAffiliatedetails().addAll(affiliateDetails);
        return this;
    }

    public FinanceServiceArrangementBuilder setMarketingPreference(Boolean marketingPreferenceIndicator, Boolean marketingPreferenceByEmail, Boolean marketingPreferenceByPhone, Boolean marketingPreferenceByMail, Boolean marketingPreferenceBySMS) {
        financeServiceArrangement.setMarketingPrefereceIndicator(marketingPreferenceIndicator);
        financeServiceArrangement.setMarketingPreferenceByEmail(marketingPreferenceByEmail);
        financeServiceArrangement.setMarketingPreferenceByPhone(marketingPreferenceByPhone);
        financeServiceArrangement.setMarketingPreferenceByMail(marketingPreferenceByMail);
        financeServiceArrangement.setMarketingPreferenceBySMS(marketingPreferenceBySMS);

        return this;
    }


    public FinanceServiceArrangementBuilder conditions(List<RuleCondition> conditions) {
        financeServiceArrangement.getConditions().addAll(conditions);
        return this;
    }

    public FinanceServiceArrangementBuilder balanceTransferAmount(CurrencyAmount balanceTransferAmount) {
        financeServiceArrangement.setTotalBalanceTransferAmount(balanceTransferAmount);
        return this;
    }


}

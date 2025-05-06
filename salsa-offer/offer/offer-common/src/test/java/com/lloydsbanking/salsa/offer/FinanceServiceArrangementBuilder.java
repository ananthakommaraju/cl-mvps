package com.lloydsbanking.salsa.offer;


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

    public FinanceServiceArrangementBuilder associatedProduct(Product associatedProduct) {
        financeServiceArrangement.setAssociatedProduct(associatedProduct);
        return this;
    }

    public FinanceServiceArrangementBuilder initiatedThrough(Channel initiatedThrough) {
        financeServiceArrangement.setInitiatedThrough(initiatedThrough);
        return this;
    }

    public FinanceServiceArrangementBuilder campaignCode(String campaignCode) {
        financeServiceArrangement.setCampaignCode(campaignCode);
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

    public FinanceServiceArrangementBuilder marketingPreferenceIndicator(Boolean marketingPreferenceIndicator) {
        financeServiceArrangement.setMarketingPrefereceIndicator(marketingPreferenceIndicator);
        return this;
    }

    public FinanceServiceArrangementBuilder marketingPreferenceByEmail(Boolean marketingPreferenceByEmail) {
        financeServiceArrangement.setMarketingPreferenceByEmail(marketingPreferenceByEmail);
        return this;
    }

    public FinanceServiceArrangementBuilder marketingPreferenceByPhone(Boolean marketingPreferenceByPhone) {
        financeServiceArrangement.setMarketingPreferenceByPhone(marketingPreferenceByPhone);
        return this;
    }

    public FinanceServiceArrangementBuilder marketingPreferenceByMail(Boolean marketingPreferenceByMail) {
        financeServiceArrangement.setMarketingPreferenceByMail(marketingPreferenceByMail);
        return this;
    }

    public FinanceServiceArrangementBuilder marketingPreferenceBySMS(Boolean marketingPreferenceBySMS) {
        financeServiceArrangement.setMarketingPreferenceBySMS(marketingPreferenceBySMS);
        return this;
    }

    public FinanceServiceArrangementBuilder applicationType(String applicationType) {
        financeServiceArrangement.setApplicationType(applicationType);
        return this;
    }

    public FinanceServiceArrangementBuilder affiliateId(String affiliateId) {
        financeServiceArrangement.setAffiliateId(affiliateId);
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

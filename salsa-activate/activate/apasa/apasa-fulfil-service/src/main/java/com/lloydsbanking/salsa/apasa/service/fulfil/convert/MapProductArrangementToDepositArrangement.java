package com.lloydsbanking.salsa.apasa.service.fulfil.convert;

import lib_sim_bo.businessobjects.DepositArrangement;
import org.springframework.stereotype.Component;

@Component
public class MapProductArrangementToDepositArrangement {
    public DepositArrangement createDepositArrangement(DepositArrangement depositArrangement) {
        DepositArrangement depositArrangementResponse = new DepositArrangement();
        depositArrangementResponse.setAccountNumber(depositArrangement.getAccountNumber());
        depositArrangementResponse.setArrangementType(depositArrangement.getArrangementType());
        depositArrangementResponse.setArrangementId(depositArrangement.getArrangementId());
        depositArrangementResponse.setAssociatedProduct(depositArrangement.getAssociatedProduct());
        depositArrangementResponse.setPrimaryInvolvedParty(depositArrangement.getPrimaryInvolvedParty());
        depositArrangementResponse.setFinancialInstitution(depositArrangement.getFinancialInstitution());
        depositArrangementResponse.setMarketingPreferenceBySMS(depositArrangement.isMarketingPreferenceBySMS());
        depositArrangementResponse.setIsJointParty(depositArrangement.isIsJointParty());
        depositArrangementResponse.setApplicationSubStatus(depositArrangement.getApplicationSubStatus());
        depositArrangementResponse.getReferral().addAll(depositArrangement.getReferral());
        depositArrangementResponse.setApplicationType(depositArrangement.getApplicationType());
        depositArrangementResponse.setRetryCount(depositArrangement.getRetryCount());
        depositArrangementResponse.setAccountPurpose(depositArrangement.getAccountPurpose());
        depositArrangementResponse.setInitiatedThrough(depositArrangement.getInitiatedThrough());
        depositArrangementResponse.setLifecycleStatus(depositArrangement.getLifecycleStatus());
        depositArrangementResponse.setCampaignCode(depositArrangement.getCampaignCode());
        depositArrangementResponse.setInsuranceCode(depositArrangement.getInsuranceCode());
        depositArrangementResponse.getJointParties().addAll(depositArrangement.getJointParties());
        depositArrangementResponse.getAffiliatedetails().addAll(depositArrangement.getAffiliatedetails());
        depositArrangementResponse.getOfferedProducts().addAll(depositArrangement.getOfferedProducts());
        depositArrangementResponse.getArrangementHistory().addAll(depositArrangement.getArrangementHistory());
        depositArrangementResponse.setFundingSource(depositArrangement.getFundingSource());
        depositArrangementResponse.getRelatedEvents().addAll(depositArrangement.getRelatedEvents());
        depositArrangementResponse.setAffiliateId(depositArrangement.getAffiliateId());
        return depositArrangementResponse;
    }
}

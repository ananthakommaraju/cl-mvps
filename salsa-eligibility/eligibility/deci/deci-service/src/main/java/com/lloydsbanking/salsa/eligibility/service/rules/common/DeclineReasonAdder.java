package com.lloydsbanking.salsa.eligibility.service.rules.common;

import lb_gbo_sales.businessobjects.CustomerInstruction;
import lb_gbo_sales.businessobjects.DeclineReason;
import lib_sim_bo.businessobjects.ProductEligibilityDetails;
import lib_sim_bo.businessobjects.ReasonCode;

public class DeclineReasonAdder {

    public void addDeclineReason(String cmsReason, String reason, CustomerInstruction customerInstruction) {
        DeclineReason declineReason = new DeclineReason();
        declineReason.setReasonCode(cmsReason);
        declineReason.setReasonDescription(reason);
        customerInstruction.getDeclineReasons().add(declineReason);
        customerInstruction.setEligibilityIndicator(false);
    }

    public void addDeclineReason(String cmsReason, String reason, ProductEligibilityDetails eligibilityDetails) {

        ReasonCode reasonCode = new ReasonCode();
        eligibilityDetails.getDeclineReasons().add(reasonCode);
        if (null == eligibilityDetails.getDeclineReasons().get(0).getCode()) {
            eligibilityDetails.getDeclineReasons().get(0).setCode(cmsReason);
            eligibilityDetails.getDeclineReasons().get(0).setDescription(reason);
        }
        eligibilityDetails.setIsEligible(String.valueOf(false));
    }
}

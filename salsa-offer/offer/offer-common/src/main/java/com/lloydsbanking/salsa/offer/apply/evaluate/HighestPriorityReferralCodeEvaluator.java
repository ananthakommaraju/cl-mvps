package com.lloydsbanking.salsa.offer.apply.evaluate;

import com.lloydsbanking.salsa.downstream.pam.model.ReferenceDataLookUp;
import com.lloydsbanking.salsa.offer.pam.LookupDataRetriever;
import lib_sim_gmo.exception.DataNotAvailableErrorMsg;
import lib_sim_bo.businessobjects.ReferralCode;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HighestPriorityReferralCodeEvaluator {

    private static final String EXPERIAN_UNAVAILABLE_REFERRAL_CODE = "000";

    @Autowired
    LookupDataRetriever lookupDataRetriever;

    public void findHighestPriorityCode(String channel, String groupCode, List<ReferralCode> referralCode) throws DataNotAvailableErrorMsg {

        List<String> referralCodeList = new ArrayList<>();
        if (referralCode != null) {
            for (ReferralCode referralCode1 : referralCode) {
                referralCodeList.add(referralCode1.getCode());
            }
            List<ReferenceDataLookUp> lookUpList = lookupDataRetriever.getLookupListFromGroupCodeAndChannelAndLookUpText(groupCode, channel, referralCodeList);

            int highestPriority = getHighestPriority(lookUpList);
            String code = getReferralCode(lookUpList, highestPriority);
            String description = getReferralCodeDescription(referralCode, code);

            ReferralCode highestPriorityReferralCode = new ReferralCode();
            highestPriorityReferralCode.setCode(code);
            highestPriorityReferralCode.setDescription(description);

            referralCode.clear();
            referralCode.add(highestPriorityReferralCode);
        }
    }

    private String getReferralCodeDescription(List<ReferralCode> referralCode, String code) {
        String description = "";
        for (ReferralCode referralCode2 : referralCode) {
            if (referralCode2.getCode() != null && referralCode2.getCode().equals(code)) {
                description = referralCode2.getDescription();
            }
        }
        return description;
    }

    private String getReferralCode(List<ReferenceDataLookUp> lookUpList, int highestPriority) {
        String code = EXPERIAN_UNAVAILABLE_REFERRAL_CODE;
        for (ReferenceDataLookUp lookUpData : lookUpList) {
            if (lookUpData.getSequence() == highestPriority) {
                code = lookUpData.getLookupText();
            }
        }
        return code;
    }

    private int getHighestPriority(List<ReferenceDataLookUp> lookUpList) {
        List<Integer> priorityList = new ArrayList<>();
        for (ReferenceDataLookUp lookUpData : lookUpList) {
            priorityList.add(lookUpData.getSequence().intValue());
        }
        Collections.sort(priorityList);
        return priorityList.get(0);
    }

}

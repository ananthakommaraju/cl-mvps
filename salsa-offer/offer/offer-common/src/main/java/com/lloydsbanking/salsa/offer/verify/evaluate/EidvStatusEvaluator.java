package com.lloydsbanking.salsa.offer.verify.evaluate;


import com.lloydsbanking.salsa.downstream.pam.model.ReferenceDataLookUp;
import com.lloydsbanking.salsa.offer.EIDVStatus;
import com.lloydsbanking.salsa.offer.pam.LookupDataRetriever;
import lib_sim_gmo.exception.DataNotAvailableErrorMsg;
import lib_sim_bo.businessobjects.CustomerScore;
import lib_sim_bo.businessobjects.ReferralCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;


public class EidvStatusEvaluator {

    private static final String ASSESSMENT_TYPE_EIDV = "EIDV";
    private static final String EIDV_REFERRAL_CODES = "EIDV_REFERRAL_CODES";
    private static final String EIDV_DECLINE_CODES = "EIDV_DECLINE_CODES";
    private static final String EIDV_THRESHOLD_VALUE = "EIDV_THRESHOLD_VALUE";

    @Autowired
    LookupDataRetriever lookupDataRetriever;

    List<String> referralCodeReferList = new ArrayList<>();
    List<String> referralCodeDeclineList = new ArrayList<>();
    String minimumThreshold = null;
    String maximumThreshold = null;

    public void evaluateEidvStatus(String channelId, CustomerScore customerScore, List<ReferralCode> referralCodeList) throws DataNotAvailableErrorMsg {
        populateReferenceData(channelId);
        if (customerScore != null && !CollectionUtils.isEmpty(customerScore.getAssessmentEvidence())) {
            String addressStrength = customerScore.getAssessmentEvidence().get(0).getAddressStrength();
            String identityStrength = customerScore.getAssessmentEvidence().get(0).getIdentityStrength();
            customerScore.setAssessmentType(ASSESSMENT_TYPE_EIDV);
            if (null != minimumThreshold && isStrengthLowerThanLowerThresholdValue(addressStrength, identityStrength, minimumThreshold)) {
                customerScore.setScoreResult(EIDVStatus.DECLINE.getValue());
            } else if (!CollectionUtils.isEmpty(referralCodeList)) {
                updateCustomerScoreWithReferralList(customerScore, referralCodeList, referralCodeReferList, referralCodeDeclineList);
                customerScore.getReferralCode().addAll(referralCodeList);
            } else if (null != maximumThreshold && isStrengthGreaterThanUpperThresholdValue(addressStrength, identityStrength, maximumThreshold)) {
                customerScore.setScoreResult(EIDVStatus.ACCEPT.getValue());
            } else {
                customerScore.setScoreResult(EIDVStatus.REFER.getValue());
            }
        }
    }

    private void populateReferenceData(String channelId) throws DataNotAvailableErrorMsg {
        List<ReferenceDataLookUp> referenceDataLookUpList = retrieveLookUpData(channelId);
        for (ReferenceDataLookUp lookUp : referenceDataLookUpList) {
            String groupCode = lookUp.getGroupCode();
            switch (groupCode) {
                case EIDV_REFERRAL_CODES:
                    referralCodeReferList.add(lookUp.getLookupValueDesc());
                    break;
                case EIDV_DECLINE_CODES:
                    referralCodeDeclineList.add(lookUp.getLookupValueDesc());
                    break;
                case EIDV_THRESHOLD_VALUE:
                    if ("Upper Threshold".equalsIgnoreCase(lookUp.getLookupText())) {
                        maximumThreshold = lookUp.getLookupValueDesc();
                    } else {
                        minimumThreshold = lookUp.getLookupValueDesc();
                    }
                    break;
                default:
                    break;
            }
        }
    }

    private List<ReferenceDataLookUp> retrieveLookUpData(String channelId) throws DataNotAvailableErrorMsg {
        List<String> groupCodeList = new ArrayList<>();
        groupCodeList.add("EIDV_REFERRAL_CODES");
        groupCodeList.add("EIDV_THRESHOLD_VALUE");
        groupCodeList.add("EIDV_DECLINE_CODES");
        return lookupDataRetriever.getLookupListFromChannelAndGroupCodeList(channelId, groupCodeList);
    }

    private boolean isStrengthLowerThanLowerThresholdValue(
            String addressStrength, String identityStrength,
            String minThresholdValue) {
        if ((null != addressStrength && addressStrength.compareTo(minThresholdValue) < 0)
                || (null != identityStrength && identityStrength.compareTo(minThresholdValue) < 0)) {
            return true;
        }
        return false;
    }

    private boolean isStrengthGreaterThanUpperThresholdValue(
            String addressStrength, String identityStrength,
            String upperThresholdValue) {
        if ((null != addressStrength && addressStrength.compareTo(upperThresholdValue) > 0)
                || (null != identityStrength && identityStrength.compareTo(upperThresholdValue) > 0)) {
            return true;
        }
        return false;
    }

    private void updateCustomerScoreWithReferralList(CustomerScore customerScore, List<ReferralCode> referralCodeList, List<String> referralCodeReferList, List<String> referralCodeDeclineList) {
        for (ReferralCode referralCode : referralCodeList) {
            if (referralCodeDeclineList.contains(referralCode.getCode())) {
                customerScore.setScoreResult(EIDVStatus.DECLINE.getValue());
                break;
            } else if (referralCodeReferList.contains(referralCode.getCode())) {
                customerScore.setScoreResult(EIDVStatus.REFER.getValue());
            }
        }
    }
}

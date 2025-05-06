package com.lloydsbanking.salsa.activate.administer;

import com.lloydsbanking.salsa.activate.downstream.LookUpValueRetriever;
import com.lloydsbanking.salsa.downstream.pam.model.ReferenceDataLookUp;
import lib_sim_bo.businessobjects.Product;
import lib_sim_bo.businessobjects.ProductFamily;
import org.apache.cxf.common.util.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class AdministerReferredLookUpData {
    private static final Logger LOGGER = Logger.getLogger(AdministerReferredLookUpData.class);

    private static final String DECLINE_SOURCE_BANK = "Bank";
    private static final String GROUP_CODE_ASM_DECLINE = "ASM_DECLINE_CODE";
    private static final String GROUP_CODE_REFERRAL_TEAM = "REFERRAL_TEAM_GROUPS";
    private static final String REFERRAL_CODE_DEFAULT = "001";

    @Autowired
    LookUpValueRetriever lookUpValueRetriever;

    public String getDeclineSource(String referralCode, String channelId) {
        List<ReferenceDataLookUp> refLookUpList = retrieveLookUpValuesByGroupCode(channelId);
        String declineSource = DECLINE_SOURCE_BANK;
        for (ReferenceDataLookUp refLookUp : refLookUpList) {
            if (GROUP_CODE_ASM_DECLINE.equalsIgnoreCase(refLookUp.getGroupCode()) && null != refLookUp.getLookupText() && refLookUp.getLookupText().equalsIgnoreCase(referralCode)) {
                declineSource = refLookUp.getLookupValueDesc();
            }
        }
        return declineSource;
    }


    public String getReferralCode(List<ReferenceDataLookUp> refLookUpList, String taskType) {
        String referralCode = null;
        for (ReferenceDataLookUp refLookUp : refLookUpList) {
            if (null != refLookUp.getLookupValueDesc() && refLookUp.getLookupValueDesc().equalsIgnoreCase(taskType)) {
                referralCode = refLookUp.getLookupText();
                break;
            }
        }
        return referralCode;
    }

    public boolean checkIfFamilyIDSameAsCreditDecision(Product associatedProduct, List<ProductFamily> productFamilyListFromCreditDecision) {
        boolean isFamilyIdSame = false;
        if (associatedProduct != null && !associatedProduct.getAssociatedFamily().isEmpty()
                && !associatedProduct.getAssociatedFamily().get(0).getExtsysprodfamilyidentifier().isEmpty()) {
            for (ProductFamily productFamily : productFamilyListFromCreditDecision) {
                if (!CollectionUtils.isEmpty(productFamily.getExtsysprodfamilyidentifier()) && null != productFamily.getExtsysprodfamilyidentifier().get(0).getProductFamilyIdentifier()) {
                    String extSysProFamilyId = productFamily.getExtsysprodfamilyidentifier().get(0).getProductFamilyIdentifier().replaceFirst("^0+(?!$)", "");
                    if (extSysProFamilyId.equalsIgnoreCase(associatedProduct.getAssociatedFamily().get(0).getExtsysprodfamilyidentifier().get(0).getProductFamilyIdentifier())) {
                        isFamilyIdSame = true;
                        break;
                    }
                }
            }
        }
        return isFamilyIdSame;
    }

    public List<ReferenceDataLookUp> retrieveLookUpValuesByGroupCodeAndLookUpText(String referralCode, String channelId) {
        List<String> lookUpTextList = new ArrayList<>();
        List<String> groupCodeList = new ArrayList<>();
        lookUpTextList.add(referralCode);
        groupCodeList.add(GROUP_CODE_REFERRAL_TEAM);
        List<ReferenceDataLookUp> refLookUpList = new ArrayList<>();
        try {
            refLookUpList = lookUpValueRetriever.retrieveLookUpValues(lookUpTextList, channelId, groupCodeList);
        } catch (DataAccessException e) {
            LOGGER.info("Error while retrieving LookupValue " + e);
        }
        if (refLookUpList.isEmpty()) {
            refLookUpList = retrieveLookUpValuesByGroupCodeAndLookUpText001(lookUpTextList, channelId, groupCodeList);
        }
        return refLookUpList;
    }

    private List<ReferenceDataLookUp> retrieveLookUpValuesByGroupCodeAndLookUpText001(List<String> lookUpTextList, String channelId, List<String> groupCodeList) {
        List<ReferenceDataLookUp> refLookUpList = new ArrayList<>();
        lookUpTextList.add(REFERRAL_CODE_DEFAULT);
        try {
            LOGGER.info("Entering RetieveLookUpValues with LookupText " + lookUpTextList.get(0));
            refLookUpList = lookUpValueRetriever.retrieveLookUpValues(lookUpTextList, channelId, groupCodeList);
        } catch (DataAccessException e) {
            LOGGER.info("Error retrieving lookUpValue with lookUpText 001 " + e);
        }
        LOGGER.info("Exiting RetieveLookUpValues");
        return refLookUpList;
    }

    private List<ReferenceDataLookUp> retrieveLookUpValuesByGroupCode(String channelId) {
        List<ReferenceDataLookUp> refLookUpList = new ArrayList<>();
        List<String> groupCodeList = new ArrayList<>();
        groupCodeList.add(GROUP_CODE_ASM_DECLINE);
        try {
            LOGGER.info("Entering RetieveLookUpValues with groupCode " + groupCodeList.get(0));
            refLookUpList = lookUpValueRetriever.getLookUpValues(groupCodeList, channelId);
        } catch (DataAccessException e) {
            LOGGER.info("Error while retrieving lookUp values for App Status Declined case: " + e);
        }
        LOGGER.info("Exiting RetieveLookUpValues");
        return refLookUpList;
    }

}

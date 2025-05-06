package com.lloydsbanking.salsa.offer;


import com.lloydsbanking.salsa.downstream.pam.model.ReferenceDataLookUp;
import com.lloydsbanking.salsa.offer.pam.LookupDataRetriever;
import lib_sim_gmo.exception.DataNotAvailableErrorMsg;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LegalEntityMapUtility {

    static Map<String, String> legalEntityMap = new HashMap<>();

    @Autowired
    LookupDataRetriever offerLookupDataRetriever;

    public static Map<String, String> getLegalEntityMap() {
        return legalEntityMap;
    }

    public static void setLegalEntityMap(Map<String, String> legalEntityMap) {
        LegalEntityMapUtility.legalEntityMap = legalEntityMap;
    }

    public void createLegalEntityMap(String channelId) throws DataNotAvailableErrorMsg {
        List<String> groupCodeList = new ArrayList<>();
        groupCodeList.add("MAN_LEGAL_ENT_CODE");
        List<ReferenceDataLookUp> lookUpList = offerLookupDataRetriever.getLookupListFromChannelAndGroupCodeList(channelId, groupCodeList);
        if (lookUpList != null) {
            for (ReferenceDataLookUp lookUp : lookUpList) {
                legalEntityMap.put(lookUp.getLookupText(), lookUp.getLookupValueDesc());
            }
        }
    }
}

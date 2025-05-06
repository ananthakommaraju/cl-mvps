package com.lloydsbanking.salsa.activate.converter;


import com.lloydsbanking.salsa.soap.ocis.f060.objects.CommsPrefData;
import com.lloydsbanking.salsa.soap.ocis.f060.objects.F060Req;
import com.lloydsbanking.salsa.soap.ocis.f060.objects.PartyInfo;
import lib_sim_bo.businessobjects.ProductArrangement;
import org.apache.commons.lang3.time.FastDateFormat;
import org.apache.cxf.common.util.StringUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
public class F060RequestFactory {

    private static final short EXT_SYS_SALSA = 19;
    private static final String COMMS_PREFERENCE_TYPE_SMS_CD = "007";
    private static final String COMMS_PREFERENCE_TYPE_EMAIL_CD = "005";
    private static final String COMMS_PREFERENCE_TYPE_PHONE_CD = "002";
    private static final String COMMS_PREFERENCE_TYPE_MAIL_CD = "003";
    private static final String MARKETING_TYPE_PREFERRED_OPTION_CD = "001";
    private static final String MARKETING_TYPE_NOT_PREFERRED_OPTION_CD = "002";


    public F060Req convert(ProductArrangement productArrangement) {
        F060Req f060Req = new F060Req();
        f060Req.setMaxRepeatGroupQy(1);
        f060Req.setExtSysId(EXT_SYS_SALSA);
        f060Req.setPartyInfo(getPartyInfo(productArrangement));
        f060Req.getCommsPrefData().addAll(getCommsPrefDatas(productArrangement));
        return f060Req;
    }

    private List<CommsPrefData> getCommsPrefDatas(ProductArrangement productArrangement) {
        List<CommsPrefData> commsPrefDataList = new ArrayList<>();

        commsPrefDataList.add(addCommsPrefData(productArrangement.isMarketingPreferenceBySMS(), COMMS_PREFERENCE_TYPE_SMS_CD));

        commsPrefDataList.add(addCommsPrefData(productArrangement.isMarketingPreferenceByEmail(), COMMS_PREFERENCE_TYPE_EMAIL_CD));

        commsPrefDataList.add(addCommsPrefData(productArrangement.isMarketingPreferenceByPhone(), COMMS_PREFERENCE_TYPE_PHONE_CD));

        commsPrefDataList.add(addCommsPrefData(productArrangement.isMarketingPreferenceByMail(), COMMS_PREFERENCE_TYPE_MAIL_CD));

        return commsPrefDataList;
    }

    private CommsPrefData addCommsPrefData(Boolean marketingPreference, String typeCode) {
        CommsPrefData commsPrefData;
        if (marketingPreference != null && marketingPreference) {
            commsPrefData = getCommsPrefData(MARKETING_TYPE_PREFERRED_OPTION_CD, typeCode);
        } else {
            commsPrefData = getCommsPrefData(MARKETING_TYPE_NOT_PREFERRED_OPTION_CD, typeCode);
        }
        return commsPrefData;
    }

    private CommsPrefData getCommsPrefData(String optionCode, String typeCode) {
        CommsPrefData commsPrefData = new CommsPrefData();
        commsPrefData.setCommsOptCd(optionCode);
        commsPrefData.setCommsTypeCd(typeCode);
        commsPrefData.setAuditDt(getCurrentDateOrTime("ddMMyyyy"));
        commsPrefData.setAuditTm(getCurrentDateOrTime("HHmmss"));
        return commsPrefData;
    }

    private String getCurrentDateOrTime(String format) {
        return FastDateFormat.getInstance(format).format(new Date());
    }

    private PartyInfo getPartyInfo(ProductArrangement productArrangement) {
        PartyInfo partyInfo = new PartyInfo();
        if (productArrangement.getGuardianDetails() != null &&
                !StringUtils.isEmpty(productArrangement.getGuardianDetails().getCustomerIdentifier())) {
            partyInfo.setPartyId(Long.valueOf(productArrangement.getGuardianDetails().getCustomerIdentifier()));
        } else {
            partyInfo.setPartyId(Long.valueOf(productArrangement.getPrimaryInvolvedParty().getCustomerIdentifier()));
        }
        if (productArrangement.getGuardianDetails() != null &&
                !StringUtils.isEmpty(productArrangement.getGuardianDetails().getCidPersID())) {
            partyInfo.setExtPartyIdTx(productArrangement.getGuardianDetails().getCidPersID());
        } else {
            partyInfo.setExtPartyIdTx(productArrangement.getPrimaryInvolvedParty().getCidPersID());
        }
        partyInfo.setPartyExtSysId(EXT_SYS_SALSA);
        return partyInfo;
    }
}

package com.lloydsbanking.salsa.offer.createinvolvedparty.convert;


import com.lloydsbanking.salsa.soap.ocis.f062.objects.AddressAuditUpdData1Type;
import com.lloydsbanking.salsa.soap.ocis.f062.objects.EvidAuditUpdDataType;
import lib_sim_bo.businessobjects.AuditData;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


public class AuditDataFactory {
    private static final String TYPE_PARTY_EVIDENCE = "PARTY_EVIDENCE";

    private static final String TYPE_ADDRESS_EVIDENCE = "ADDRESS_EVIDENCE";

    private static final String TYPE_ADDRESS = "ADDRESS";

    private static final int AUDITTIME_LENGTH_MINIMUM = 6;

    private static final int AUDITDATE_LENGTH_MINIMUM = 8;

    public Collection<? extends AuditData> createAudtitData(AddressAuditUpdData1Type addressAuditUpdData, EvidAuditUpdDataType evidAuditUpdData) {
        List<AuditData> auditDataList = new ArrayList<>();

        if (evidAuditUpdData != null) {
            if (!CollectionUtils.isEmpty(evidAuditUpdData.getPartyEvidUpdData())) {
                AuditData partyEvidAuditData = new AuditData();
                partyEvidAuditData.setAuditType(TYPE_PARTY_EVIDENCE);
                partyEvidAuditData.setAuditDate(evidAuditUpdData.getPartyEvidUpdData().get(0).getAuditDt());
                partyEvidAuditData.setAuditTime(evidAuditUpdData.getPartyEvidUpdData().get(0).getAuditTm());
                auditDataList.add(partyEvidAuditData);
            }
            if (!CollectionUtils.isEmpty(evidAuditUpdData.getAddrEvidUpdData())) {
                AuditData addrEvidAuditData = new AuditData();
                addrEvidAuditData.setAuditType(TYPE_ADDRESS_EVIDENCE);
                addrEvidAuditData.setAuditDate(evidAuditUpdData.getAddrEvidUpdData().get(0).getAuditDt());
                addrEvidAuditData.setAuditTime(evidAuditUpdData.getAddrEvidUpdData().get(0).getAuditTm());
                auditDataList.add(addrEvidAuditData);
            }
        }
        if (addressAuditUpdData != null) {
            AuditData addressAuditData = new AuditData();
            addressAuditData.setAuditType(TYPE_ADDRESS);
            String auditTm = String.valueOf(addressAuditUpdData.getAuditTm());
            addressAuditData.setAuditTime(auditTm.length() > AUDITTIME_LENGTH_MINIMUM ? auditTm : StringUtils.leftPad(auditTm, AUDITTIME_LENGTH_MINIMUM, "0"));
            String auditDt = String.valueOf(addressAuditUpdData.getAuditDt());
            addressAuditData.setAuditDate(auditDt.length() > AUDITDATE_LENGTH_MINIMUM ? auditDt : StringUtils.leftPad(auditDt, AUDITDATE_LENGTH_MINIMUM, "0"));
            auditDataList.add(addressAuditData);
        }

        return auditDataList;
    }
}

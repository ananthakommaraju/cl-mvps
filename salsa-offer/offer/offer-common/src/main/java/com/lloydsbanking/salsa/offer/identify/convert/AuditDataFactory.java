package com.lloydsbanking.salsa.offer.identify.convert;

import com.lloydsbanking.salsa.soap.ocis.f061.objects.PartyEnqData;
import lib_sim_bo.businessobjects.AuditData;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

public class AuditDataFactory {

    public List<AuditData> getAuditData(PartyEnqData partyEnqData) {
        List<AuditData> auditDataList = new ArrayList<>();
        if (null != partyEnqData.getAddressData() && null != partyEnqData.getAddressData().getAddressAuditData()) {
            AuditData auditData = new AuditData();
            auditData.setAuditType("ADDRESS");
            auditData.setAuditTime(partyEnqData.getAddressData().getAddressAuditData().getAuditTm());
            auditData.setAuditDate(partyEnqData.getAddressData().getAddressAuditData().getAuditDt());
            auditDataList.add(auditData);
        }
        if (null != partyEnqData.getEvidenceData() && !CollectionUtils.isEmpty(partyEnqData.getEvidenceData().getPartyEvid())) {
            AuditData auditDataPartyEvidence = new AuditData();
            auditDataPartyEvidence.setAuditType("PARTY_EVIDENCE");
            auditDataPartyEvidence.setAuditTime(partyEnqData.getEvidenceData().getPartyEvid().get(0).getPartyEvidAuditData().getAuditTm());
            auditDataPartyEvidence.setAuditDate(partyEnqData.getEvidenceData().getPartyEvid().get(0).getPartyEvidAuditData().getAuditDt());
            auditDataList.add(auditDataPartyEvidence);
        }
        if (null != partyEnqData.getEvidenceData() && !CollectionUtils.isEmpty(partyEnqData.getEvidenceData().getAddrEvid())) {
            AuditData auditDataAddressEvidence = new AuditData();
            auditDataAddressEvidence.setAuditType("ADDRESS_EVIDENCE");
            auditDataAddressEvidence.setAuditTime(partyEnqData.getEvidenceData().getAddrEvid().get(0).getAddrEvidAuditData().getAuditTm());
            auditDataAddressEvidence.setAuditDate(partyEnqData.getEvidenceData().getAddrEvid().get(0).getAddrEvidAuditData().getAuditDt());
            auditDataList.add(auditDataAddressEvidence);
        }
        return auditDataList;
    }

}

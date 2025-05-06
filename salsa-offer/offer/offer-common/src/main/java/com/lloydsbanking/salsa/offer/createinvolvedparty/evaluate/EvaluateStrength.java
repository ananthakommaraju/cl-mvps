package com.lloydsbanking.salsa.offer.createinvolvedparty.evaluate;

import com.lloydsbanking.salsa.downstream.pam.model.ReferenceDataLookUp;
import com.lloydsbanking.salsa.offer.pam.LookupDataRetriever;
import com.lloydsbanking.salsa.soap.ocis.f062.objects.AddrEvidUpdDataType;
import com.lloydsbanking.salsa.soap.ocis.f062.objects.EvidenceUpdDataType;
import com.lloydsbanking.salsa.soap.ocis.f062.objects.PartyEvidUpdDataType;
import lib_sim_bo.businessobjects.CustomerScore;
import lib_sim_gmo.exception.DataNotAvailableErrorMsg;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.List;

public class EvaluateStrength {

    @Autowired
    LookupDataRetriever offerLookupDataRetriever;

    public static final List<String> GROUP_CODE_LIST = Arrays.asList("PTY_EVIDENCE_CODE", "ADD_EVIDENCE_CODE", "PTY_PURPOSE_CODE", "ADD_PURPOSE_CODE");

    public EvidenceUpdDataType fetchAddressAndPartyEvidenceAndPurposeCode(CustomerScore customerScore, String channelId) throws DataNotAvailableErrorMsg {

        List<ReferenceDataLookUp> referenceDataLookUps = offerLookupDataRetriever.getLookupListFromChannelAndGroupCodeList(channelId, GROUP_CODE_LIST);
        EvidenceUpdDataType evidenceUpdDataType = new EvidenceUpdDataType();

        if (!CollectionUtils.isEmpty(customerScore.getAssessmentEvidence()) && !customerScore.getAssessmentEvidence().isEmpty() && null != customerScore.getAssessmentEvidence()
                .get(0)
                .getEvidenceIdentifier()) {
            AddrEvidUpdDataType addrEvidUpdDataType = new AddrEvidUpdDataType();
            PartyEvidUpdDataType partyEvidUpdDataType = new PartyEvidUpdDataType();
            getEvidenceData(customerScore, referenceDataLookUps, addrEvidUpdDataType, partyEvidUpdDataType);
            evidenceUpdDataType.getAddrEvidUpdData().add(addrEvidUpdDataType);
            evidenceUpdDataType.getPartyEvidUpdData().add(partyEvidUpdDataType);
        }

        return evidenceUpdDataType;
    }

    private void getEvidenceData(CustomerScore customerScore, List<ReferenceDataLookUp> referenceDataLookUps, AddrEvidUpdDataType addrEvidUpdDataType, PartyEvidUpdDataType partyEvidUpdDataType) {
        for (ReferenceDataLookUp referenceDataLookUp : referenceDataLookUps) {
            if (null != customerScore.getAssessmentEvidence().get(0).getAddressStrength()) {
                if ("ADD_EVIDENCE_CODE".equalsIgnoreCase(referenceDataLookUp.getGroupCode())) {
                    addrEvidUpdDataType.setAddrEvidTypeCd(referenceDataLookUp.getLookupValueDesc());
                }
                if ("ADD_PURPOSE_CODE".equalsIgnoreCase(referenceDataLookUp.getGroupCode())) {
                    addrEvidUpdDataType.setAddrEvidPurposeCd(referenceDataLookUp.getLookupValueDesc());
                }
                addrEvidUpdDataType.setAddrEvidRefTx(customerScore.getAssessmentEvidence().get(0).getEvidenceIdentifier());
            }
            if (null != customerScore.getAssessmentEvidence().get(0).getIdentityStrength()) {
                if ("PTY_EVIDENCE_CODE".equalsIgnoreCase(referenceDataLookUp.getGroupCode())) {
                    partyEvidUpdDataType.setPartyEvidTypeCd(referenceDataLookUp.getLookupValueDesc());
                }
                if ("PTY_PURPOSE_CODE".equalsIgnoreCase(referenceDataLookUp.getGroupCode())) {
                    partyEvidUpdDataType.setPartyEvidPurposeCd(referenceDataLookUp.getLookupValueDesc());
                }
                partyEvidUpdDataType.setPartyEvidRefTx(customerScore.getAssessmentEvidence().get(0).getEvidenceIdentifier());
            }
        }
    }
}

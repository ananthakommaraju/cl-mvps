package com.lloydsbanking.salsa.offer.createinvolvedparty.evaluate;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.downstream.pam.model.ReferenceDataLookUp;
import com.lloydsbanking.salsa.offer.pam.LookupDataRetriever;
import com.lloydsbanking.salsa.soap.ocis.f062.objects.EvidenceUpdDataType;
import lib_sim_gmo.exception.DataNotAvailableErrorMsg;
import lib_sim_bo.businessobjects.AssessmentEvidence;
import lib_sim_bo.businessobjects.CustomerScore;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Category(UnitTest.class)
public class EvaluateStrengthTest {
    public static final List<String> GROUP_CODE_LIST = Arrays.asList("PTY_EVIDENCE_CODE", "ADD_EVIDENCE_CODE", "PTY_PURPOSE_CODE", "ADD_PURPOSE_CODE");

    EvaluateStrength evaluateStrength;

    @Before
    public void setUp() {
        evaluateStrength = new EvaluateStrength();
        evaluateStrength.offerLookupDataRetriever = mock(LookupDataRetriever.class);
    }

    @Test
    public void testGetAddressStrength() throws DataNotAvailableErrorMsg {
        List<ReferenceDataLookUp> referenceDataLookUpList = new ArrayList<>();
        ReferenceDataLookUp referenceDataLkp = new ReferenceDataLookUp("ADD_EVIDENCE_CODE", "149", "Address Evidence Type Code", new Long("23"), "Address Evidence", "LTB", new Long("1"));
        ReferenceDataLookUp referenceDataLkp1 = new ReferenceDataLookUp("PTY_EVIDENCE_CODE", "148", "Party Evidence Type Code", new Long("22"), "Party Evidence", "LTB", new Long("1"));
        ReferenceDataLookUp referenceDataLkp2 = new ReferenceDataLookUp("PTY_PURPOSE_CODE", "010", "Party Evidence Purpose Code", new Long("24"), "Party Evidence", "LTB", new Long("1"));
        ReferenceDataLookUp referenceDataLkp3 = new ReferenceDataLookUp("ADD_PURPOSE_CODE", "009", "Address Evidence Purpose Code", new Long("25"), "Address Evidence", "LTB", new Long("1"));
        referenceDataLookUpList.add(referenceDataLkp);
        referenceDataLookUpList.add(referenceDataLkp1);
        referenceDataLookUpList.add(referenceDataLkp2);
        referenceDataLookUpList.add(referenceDataLkp3);
        CustomerScore customerScore = new CustomerScore();
        AssessmentEvidence assessmentEvidence = new AssessmentEvidence();
        assessmentEvidence.setAddressStrength("10");
        assessmentEvidence.setIdentityStrength("20");
        assessmentEvidence.setEvidenceIdentifier("134sfasd2345230");
        customerScore.getAssessmentEvidence().add(0, assessmentEvidence);

        when(evaluateStrength.offerLookupDataRetriever.getLookupListFromChannelAndGroupCodeList("LTB", GROUP_CODE_LIST)).thenReturn(referenceDataLookUpList);
        EvidenceUpdDataType evidenceUpdDataType = evaluateStrength.fetchAddressAndPartyEvidenceAndPurposeCode(customerScore, "LTB");
        assertEquals("009", evidenceUpdDataType.getAddrEvidUpdData().get(0).getAddrEvidPurposeCd());
        assertEquals("149", evidenceUpdDataType.getAddrEvidUpdData().get(0).getAddrEvidTypeCd());
        assertEquals("010", evidenceUpdDataType.getPartyEvidUpdData().get(0).getPartyEvidPurposeCd());
        assertEquals("148", evidenceUpdDataType.getPartyEvidUpdData().get(0).getPartyEvidTypeCd());
        assertEquals("134sfasd2345230", evidenceUpdDataType.getAddrEvidUpdData().get(0).getAddrEvidRefTx());
        assertEquals("134sfasd2345230", evidenceUpdDataType.getPartyEvidUpdData().get(0).getPartyEvidRefTx());

    }
}

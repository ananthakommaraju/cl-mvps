package com.lloydsbanking.salsa.offer.verify.evaluate;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.downstream.pam.model.ReferenceDataLookUp;
import com.lloydsbanking.salsa.offer.pam.LookupDataRetriever;
import lib_sim_gmo.exception.DataNotAvailableErrorMsg;
import lib_sim_bo.businessobjects.AssessmentEvidence;
import lib_sim_bo.businessobjects.CustomerScore;
import lib_sim_bo.businessobjects.ReferralCode;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Category(UnitTest.class)
public class EidvStatusEvaluatorTest {

    private EidvStatusEvaluator eidvStatusEvaluator;

    List<String> groupCodeList;

    CustomerScore customerScore;

    List<ReferenceDataLookUp> referenceDataLookUpList;

    @Before
    public void setUp() {
        eidvStatusEvaluator = new EidvStatusEvaluator();
        eidvStatusEvaluator.lookupDataRetriever = mock(LookupDataRetriever.class);
        groupCodeList = new ArrayList<>();
        customerScore = new CustomerScore();

        groupCodeList.add("EIDV_REFERRAL_CODES");
        groupCodeList.add("EIDV_THRESHOLD_VALUE");
        groupCodeList.add("EIDV_DECLINE_CODES");

        referenceDataLookUpList = new ArrayList<>();
        ReferenceDataLookUp referenceDataLkp = new ReferenceDataLookUp("EIDV_ERROR_CODES", "FG824 ", "EIDV_ERROR_CODE1", new Long("22"), "EIDV_ERROR_CODE1", "LTB", new Long("1"));
        ReferenceDataLookUp referenceDataLkp1 = new ReferenceDataLookUp("EIDV_THRESHOLD_VALUE", "0", "EIDV_THRESHOLD_VALUE", new Long("23"), "Lower Threshold", "LTB", new Long("2"));
        ReferenceDataLookUp referenceDataLkp2 = new ReferenceDataLookUp("EIDV_THRESHOLD_VALUE", "390", "EIDV_THRESHOLD_VALUE", new Long("24"), "Upper Threshold", "LTB", new Long("1"));
        ReferenceDataLookUp referenceDataLkp3 = new ReferenceDataLookUp("EIDV_REFERRAL_CODES", "009", "EIDV_REFERRAL_CODES", new Long("25"), "Refer", "LTB", new Long("5"));
        ReferenceDataLookUp referenceDataLkp4 = new ReferenceDataLookUp("EIDV_REFERRAL_CODES", "005", "EIDV_REFERRAL_CODES", new Long("26"), "Refer", "LTB", new Long("4"));
        ReferenceDataLookUp referenceDataLkp5 = new ReferenceDataLookUp("EIDV_REFERRAL_CODES", "003", "EIDV_REFERRAL_CODES", new Long("27"), "Refer", "LTB", new Long("3"));
        ReferenceDataLookUp referenceDataLkp6 = new ReferenceDataLookUp("EIDV_REFERRAL_CODES", "002", "EIDV_REFERRAL_CODES", new Long("28"), "Refer", "LTB", new Long("2"));
        ReferenceDataLookUp referenceDataLkp7 = new ReferenceDataLookUp("EIDV_REFERRAL_CODES", "001", "EIDV_REFERRAL_CODES", new Long("29"), "Refer", "LTB", new Long("1"));
        referenceDataLookUpList.add(referenceDataLkp);
        referenceDataLookUpList.add(referenceDataLkp1);
        referenceDataLookUpList.add(referenceDataLkp2);
        referenceDataLookUpList.add(referenceDataLkp3);
        referenceDataLookUpList.add(referenceDataLkp4);
        referenceDataLookUpList.add(referenceDataLkp5);
        referenceDataLookUpList.add(referenceDataLkp6);
        referenceDataLookUpList.add(referenceDataLkp7);

    }

    @Test
    public void testEvaluateEidvStatusForRefer() throws DataNotAvailableErrorMsg {

        customerScore.setDecisionCode("I");
        customerScore.setDecisionText("001");
        AssessmentEvidence evidence = new AssessmentEvidence();
        evidence.setEvidenceIdentifier("3009784HL890678330000003000724309102");
        evidence.setAddressStrength("0");
        evidence.setIdentityStrength("0");
        customerScore.getAssessmentEvidence().add(evidence);

        List<ReferralCode> referralCodeList = null;

        when(eidvStatusEvaluator.lookupDataRetriever.getLookupListFromChannelAndGroupCodeList("LTB", groupCodeList)).thenReturn(referenceDataLookUpList);
        eidvStatusEvaluator.evaluateEidvStatus("LTB", customerScore, referralCodeList);
        assertEquals("Score result not matching", "REFER", customerScore.getScoreResult());
    }

    @Test
    public void testEvaluateEidvStatusForAccept() throws DataNotAvailableErrorMsg {

        customerScore.setDecisionCode("I");
        customerScore.setDecisionText("001");
        AssessmentEvidence evidence = new AssessmentEvidence();
        evidence.setEvidenceIdentifier("3009784HL890678330000003000724309102");
        evidence.setAddressStrength("400");
        evidence.setIdentityStrength("400");
        customerScore.getAssessmentEvidence().add(evidence);

        List<ReferralCode> referralCodeList = new ArrayList<>();
        ReferralCode referralCode = new ReferralCode();
        referralCode.setCode("390");
        referralCode.setDescription("Refer");

        when(eidvStatusEvaluator.lookupDataRetriever.getLookupListFromChannelAndGroupCodeList("LTB", groupCodeList)).thenReturn(referenceDataLookUpList);
        eidvStatusEvaluator.evaluateEidvStatus("LTB", customerScore, referralCodeList);
        assertEquals("Score result not matching", "ACCEPT", customerScore.getScoreResult());
    }

    @Test
    public void testEvaluateEidvStatusReferWithAcceptAndReferCode() throws DataNotAvailableErrorMsg {

        customerScore.setDecisionCode("I");
        customerScore.setDecisionText("001");
        AssessmentEvidence evidence = new AssessmentEvidence();
        evidence.setEvidenceIdentifier("3009784HL890678330000003000724309102");
        evidence.setAddressStrength("400");
        evidence.setIdentityStrength("400");
        customerScore.getAssessmentEvidence().add(evidence);


        List<ReferralCode> referralCodeList = new ArrayList<>();
        ReferralCode referralCode = new ReferralCode();
        referralCode.setCode("009");
        referralCode.setDescription("Refer");
        referralCodeList.add(referralCode);

        when(eidvStatusEvaluator.lookupDataRetriever.getLookupListFromChannelAndGroupCodeList("LTB", groupCodeList)).thenReturn(referenceDataLookUpList);
        eidvStatusEvaluator.evaluateEidvStatus("LTB", customerScore, referralCodeList);
        assertEquals("Score result not matching", "REFER", customerScore.getScoreResult());
        assertNotNull("Referral Code list expected", customerScore.getReferralCode());
        assertEquals("Score result not matching", "009", customerScore.getReferralCode().get(0).getCode());
        assertEquals("Score result not matching", "Refer", customerScore.getReferralCode().get(0).getDescription());

    }

}
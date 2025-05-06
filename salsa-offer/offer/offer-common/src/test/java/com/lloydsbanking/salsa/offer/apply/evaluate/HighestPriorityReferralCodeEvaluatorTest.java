package com.lloydsbanking.salsa.offer.apply.evaluate;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.downstream.pam.model.ReferenceDataLookUp;
import com.lloydsbanking.salsa.offer.pam.LookupDataRetriever;
import lib_sim_gmo.exception.DataNotAvailableErrorMsg;
import lib_sim_bo.businessobjects.ReferralCode;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Category(UnitTest.class)
public class HighestPriorityReferralCodeEvaluatorTest {
    private HighestPriorityReferralCodeEvaluator evaluator;


    @Before
    public void setUp() {
        evaluator = new HighestPriorityReferralCodeEvaluator();
        evaluator.lookupDataRetriever = mock(LookupDataRetriever.class);
    }

    @Test
    public void testFindHighestPriorityCode() throws DataNotAvailableErrorMsg {

        List<ReferralCode> referralCodeList = new ArrayList<>();
        ReferralCode referralCode = new ReferralCode();
        referralCode.setCode("2");
        referralCode.setDescription("desc 2");
        referralCodeList.add(referralCode);

        List<ReferenceDataLookUp> lookUpList = new ArrayList<>();
        ReferenceDataLookUp referenceDataLookUp = new ReferenceDataLookUp();
        referenceDataLookUp.setSequence((long) 1);
        referenceDataLookUp.setLookupText("text 1");
        ReferenceDataLookUp referenceDataLookUp1 = new ReferenceDataLookUp();
        referenceDataLookUp1.setSequence((long) 2);
        referenceDataLookUp1.setLookupText("text 2");
        lookUpList.add(referenceDataLookUp);
        lookUpList.add(referenceDataLookUp1);

        List<String> lookUpText = new ArrayList<>();
        lookUpText.add("2");

        when(evaluator.lookupDataRetriever.getLookupListFromGroupCodeAndChannelAndLookUpText("grpCode", "LTB", lookUpText)).thenReturn(lookUpList);

        evaluator.findHighestPriorityCode("LTB", "grpCode", referralCodeList);

        assertEquals("text 1", referralCodeList.get(0).getCode());
        assertEquals("", referralCodeList.get(0).getDescription());
    }

    @Test
    public void testFindHighestPriorityCodeWithNullCode() throws DataNotAvailableErrorMsg {

        List<ReferralCode> referralCodeList = new ArrayList<>();
        ReferralCode referralCode = new ReferralCode();
        referralCode.setCode(null);
        referralCode.setDescription("desc 2");
        referralCodeList.add(referralCode);

        List<ReferenceDataLookUp> lookUpList = new ArrayList<>();
        ReferenceDataLookUp referenceDataLookUp = new ReferenceDataLookUp();
        referenceDataLookUp.setSequence((long) 1);
        referenceDataLookUp.setLookupText("text 1");
        ReferenceDataLookUp referenceDataLookUp1 = new ReferenceDataLookUp();
        referenceDataLookUp1.setSequence((long) 2);
        referenceDataLookUp1.setLookupText("text 2");
        lookUpList.add(referenceDataLookUp);
        lookUpList.add(referenceDataLookUp1);

        List<String> lookUpText = new ArrayList<>();
        lookUpText.add(null);

        when(evaluator.lookupDataRetriever.getLookupListFromGroupCodeAndChannelAndLookUpText("grpCode", "LTB", lookUpText)).thenReturn(lookUpList);

        evaluator.findHighestPriorityCode("LTB", "grpCode", referralCodeList);

        assertEquals("text 1", referralCodeList.get(0).getCode());
        assertEquals("", referralCodeList.get(0).getDescription());
    }
}

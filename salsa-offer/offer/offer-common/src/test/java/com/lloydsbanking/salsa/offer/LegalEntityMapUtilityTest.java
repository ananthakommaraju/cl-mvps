package com.lloydsbanking.salsa.offer;


import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.downstream.pam.model.ReferenceDataLookUp;
import com.lloydsbanking.salsa.offer.pam.LookupDataRetriever;
import lib_sim_gmo.exception.DataNotAvailableErrorMsg;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Category(UnitTest.class)
public class LegalEntityMapUtilityTest {

    private LegalEntityMapUtility mapUtility;

    @Before
    public void setUp() {
        mapUtility = new LegalEntityMapUtility();
        mapUtility.offerLookupDataRetriever = mock(LookupDataRetriever.class);
        LegalEntityMapUtility.getLegalEntityMap().clear();
    }

    @Test
    public void testLegalEntityMapUtility() throws DataNotAvailableErrorMsg {
        List<String> groupCodeList = new ArrayList<>();
        groupCodeList.add("MAN_LEGAL_ENT_CODE");
        List<ReferenceDataLookUp> referenceDataLookUpList = new ArrayList<>();
        ReferenceDataLookUp lookUp = new ReferenceDataLookUp();
        lookUp.setLookupValueDesc("desc1");
        lookUp.setLookupText("text1");
        referenceDataLookUpList.add(lookUp);
        when(mapUtility.offerLookupDataRetriever.getLookupListFromChannelAndGroupCodeList("LTB", groupCodeList)).thenReturn(referenceDataLookUpList);
        mapUtility.createLegalEntityMap("LTB");
        assertEquals("desc1", LegalEntityMapUtility.getLegalEntityMap().get("text1"));
    }

    @Test
    public void testLegalEntityMapUtilityNull() throws DataNotAvailableErrorMsg {
        List<String> groupCodeList = new ArrayList<>();
        groupCodeList.add("MAN_LEGAL_ENT_CODE");
        when(mapUtility.offerLookupDataRetriever.getLookupListFromChannelAndGroupCodeList("LTB", groupCodeList)).thenReturn(null);
        mapUtility.createLegalEntityMap("LTB");
        assertEquals(0, LegalEntityMapUtility.getLegalEntityMap().size());
        assertEquals(0, LegalEntityMapUtility.getLegalEntityMap().size());
    }
}

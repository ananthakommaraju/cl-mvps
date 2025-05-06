package com.lloydsbanking.salsa.offer.pam;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.downstream.pam.jdbc.ReferenceDataLookUpDao;
import com.lloydsbanking.salsa.downstream.pam.model.ReferenceDataLookUp;
import com.lloydsbanking.salsa.header.gmo.HeaderRetriever;
import com.lloydsbanking.salsa.offer.TestDataHelper;
import com.lloydsbanking.salsa.offer.exception.ExceptionUtility;
import lib_sim_gmo.exception.DataNotAvailableErrorMsg;
import lib_sim_gmo.messages.RequestHeader;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Category(UnitTest.class)
public class LookupDataRetrieverTest {

    RequestHeader header;

    ReferenceDataLookUpDao referenceDataLookUpDao;

    LookupDataRetriever offerLookupDataRetriever;

    HeaderRetriever headerRetriever;

    TestDataHelper testDataHelper;

    @Before
    public void setUp() {
        header = new TestDataHelper().createOpaPcaRequestHeader("LTB");
        referenceDataLookUpDao = mock(ReferenceDataLookUpDao.class);
        headerRetriever = new HeaderRetriever();
        testDataHelper = new TestDataHelper();
    }

    @Test
    public void testGetChannelIdFromContactPointId() throws DataNotAvailableErrorMsg {
        offerLookupDataRetriever = new LookupDataRetriever(referenceDataLookUpDao, new ExceptionUtility());
        ReferenceDataLookUp referenceDataLkp = new ReferenceDataLookUp("ACQUIRE_COUNTRY_NAME", "Bahrain", "Cnt_Pnt_Prtflio", new Long("1323"), "BHR", "LTB", new Long("1"));
        List<ReferenceDataLookUp> referenceDataLookUpList = new ArrayList<>();
        referenceDataLookUpList.add(referenceDataLkp);
        when(referenceDataLookUpDao.findByGroupCodeAndLookupValueDesc("Cnt_Pnt_Prtflio", (headerRetriever.getContactPoint(header)).getContactPointId())).thenReturn(referenceDataLookUpList);
        String channel = offerLookupDataRetriever.getChannelIdFromContactPointId((headerRetriever.getContactPoint(header)).getContactPointId());
        assertEquals("LTB", channel);
    }

    @Test
    public void testGetLookupListFromChannelAndGroupCodeList() throws DataNotAvailableErrorMsg {
        offerLookupDataRetriever = new LookupDataRetriever(referenceDataLookUpDao, new ExceptionUtility());
        ReferenceDataLookUp referenceDataLkp = new ReferenceDataLookUp("ACQUIRE_COUNTRY_NAME", "Bahrain", "Cnt_Pnt_Prtflio", new Long("1323"), "BHR", "LTB", new Long("1"));
        List<ReferenceDataLookUp> referenceDataLookUpList = new ArrayList<>();
        referenceDataLookUpList.add(referenceDataLkp);
        when(referenceDataLookUpDao.findByChannelAndGroupCodeIn(any(String.class), any(ArrayList.class))).thenReturn(referenceDataLookUpList);
        List<ReferenceDataLookUp> referenceDataLookUpList2 = offerLookupDataRetriever.getLookupListFromChannelAndGroupCodeList("LTB", new ArrayList<String>());
        assertEquals("BHR", referenceDataLookUpList2.get(0).getLookupText());
    }

    @Test
    public void testGetThrowsErrorForEmptyLookUpList() throws DataNotAvailableErrorMsg {
        offerLookupDataRetriever = new LookupDataRetriever(referenceDataLookUpDao, new ExceptionUtility());
        List<ReferenceDataLookUp> referenceDataLookUpListExpected = new ArrayList<>();
        List<String> groupCdList = new ArrayList<>();
        groupCdList.add("0000777505");
        DataNotAvailableErrorMsg errorfaultMsg = new DataNotAvailableErrorMsg();
        when(referenceDataLookUpDao.findByChannelAndGroupCodeIn(any(String.class), any(ArrayList.class))).thenReturn(referenceDataLookUpListExpected);
        try {
            List<ReferenceDataLookUp> referenceDataLookUpListActual = offerLookupDataRetriever.getLookupListFromChannelAndGroupCodeList("LTB", groupCdList);
            fail("No DataNotAvailableError");
        } catch (DataNotAvailableErrorMsg dataNotAvailableErrorfaultMsg) {
            //expected dataNotAvailableError. 
            errorfaultMsg = dataNotAvailableErrorfaultMsg;
            assertEquals("REF_DATA_LOOKUP_VW", errorfaultMsg.getFaultInfo().getEntity());
            assertEquals("GROUP_CODE", errorfaultMsg.getFaultInfo().getField());
            assertEquals("No matching records found, error code: ", errorfaultMsg.getFaultInfo().getDescription());
            assertEquals("0000777505", errorfaultMsg.getFaultInfo().getKey());
        }


    }

    @Test
    public void testGetLookupListFromGroupCodeAndChannelAndLookUpText() throws DataNotAvailableErrorMsg {
        offerLookupDataRetriever = new LookupDataRetriever(referenceDataLookUpDao, new ExceptionUtility());
        ReferenceDataLookUp referenceDataLkp = new ReferenceDataLookUp("ACQUIRE_COUNTRY_NAME", "Bahrain", "Cnt_Pnt_Prtflio", new Long("1323"), "BHR", "LTB", new Long("1"));
        List<ReferenceDataLookUp> referenceDataLookUpList = new ArrayList<>();
        referenceDataLookUpList.add(referenceDataLkp);
        List<String> lookUpText =  new ArrayList<>();
        lookUpText.add("BHR");
        when(referenceDataLookUpDao.findByGroupCodeAndChannelAndLookupTextIn(any(String.class), any(String.class), any(List.class))).thenReturn(referenceDataLookUpList);
        List<ReferenceDataLookUp> referenceDataLookUpList2 = offerLookupDataRetriever.getLookupListFromGroupCodeAndChannelAndLookUpText("ACQUIRE_COUNTRY_NAME", "LTB", lookUpText);
        assertEquals("Bahrain", referenceDataLookUpList2.get(0).getLookupValueDesc());
    }

    @Test
    public void testGetThrowsErrorForEmptyLookUpListGetLookupListFromGroupCodeAndChannelAndLookUpText() {
        offerLookupDataRetriever = new LookupDataRetriever(referenceDataLookUpDao, new ExceptionUtility());
        List<ReferenceDataLookUp> referenceDataLookUpListExpected = new ArrayList<>();
        List<String> groupCdList = new ArrayList<>();
        groupCdList.add("0000777505");
        DataNotAvailableErrorMsg errorfaultMsg = new DataNotAvailableErrorMsg();
        when(referenceDataLookUpDao.findByGroupCodeAndChannelAndLookupTextIn(any(String.class), any(String.class), any(List.class))).thenReturn(referenceDataLookUpListExpected);
        try {
            List<String> lookUpText =  new ArrayList<>();
            List<ReferenceDataLookUp> referenceDataLookUpListActual = offerLookupDataRetriever.getLookupListFromGroupCodeAndChannelAndLookUpText("ACQUIRE_COUNTRY_NAME", "LTB", lookUpText);
            fail("No DataNotAvailableError");
        } catch (DataNotAvailableErrorMsg dataNotAvailableErrorfaultMsg) {
            //expected dataNotAvailableError.
            errorfaultMsg = dataNotAvailableErrorfaultMsg;
            assertEquals("REF_DATA_LOOKUP_VW", errorfaultMsg.getFaultInfo().getEntity());
            assertEquals("GROUP_CODE", errorfaultMsg.getFaultInfo().getField());
            assertEquals("No matching records found, error code: ", errorfaultMsg.getFaultInfo().getDescription());
            assertEquals("ACQUIRE_COUNTRY_NAME", errorfaultMsg.getFaultInfo().getKey());
        }
    }
    @Test
    public void testGetThrowsErrorForEmptyLookupListFromChannelAndGroupCodeListAndSequence() {
        offerLookupDataRetriever = new LookupDataRetriever(referenceDataLookUpDao, new ExceptionUtility());
        List<ReferenceDataLookUp> referenceDataLookUpListExpected = new ArrayList<>();
        DataNotAvailableErrorMsg errorfaultMsg = new DataNotAvailableErrorMsg();
        when(referenceDataLookUpDao.findByChannelAndGroupCodeInOrderBySequenceAsc(any(String.class), any(List.class))).thenReturn(referenceDataLookUpListExpected);
        try {
            List<String> lookUpText =  new ArrayList<>();
            lookUpText.add("SIRA");
            List<ReferenceDataLookUp> referenceDataLookUpListActual = offerLookupDataRetriever.getLookupListFromChannelAndGroupCodeListAndSequence("LTB", lookUpText);
            fail("No DataNotAvailableError");
        } catch (DataNotAvailableErrorMsg dataNotAvailableErrorfaultMsg) {
            //expected dataNotAvailableError.
            errorfaultMsg = dataNotAvailableErrorfaultMsg;
            assertEquals("REF_DATA_LOOKUP_VW", errorfaultMsg.getFaultInfo().getEntity());
            assertEquals("GROUP_CODE", errorfaultMsg.getFaultInfo().getField());
            assertEquals("No matching records found, error code: ", errorfaultMsg.getFaultInfo().getDescription());
            assertEquals("SIRA", errorfaultMsg.getFaultInfo().getKey());
        }
    }
    @Test
    public void testGetLookupListFromChannelAndGroupCodeListAndSequence() {
        offerLookupDataRetriever = new LookupDataRetriever(referenceDataLookUpDao, new ExceptionUtility());
        List<ReferenceDataLookUp> referenceDataLookUpListExpected = new ArrayList<>();
        referenceDataLookUpListExpected.add(new ReferenceDataLookUp());
        referenceDataLookUpListExpected.get(0).setLookupValueDesc("0");
        DataNotAvailableErrorMsg errorfaultMsg = new DataNotAvailableErrorMsg();
        when(referenceDataLookUpDao.findByChannelAndGroupCodeInOrderBySequenceAsc(any(String.class), any(List.class))).thenReturn(referenceDataLookUpListExpected);
        try {
            List<String> lookUpText =  new ArrayList<>();
            lookUpText.add("SIRA");
            List<ReferenceDataLookUp> referenceDataLookUpListActual = offerLookupDataRetriever.getLookupListFromChannelAndGroupCodeListAndSequence("LTB", lookUpText);
            assertEquals("0", referenceDataLookUpListActual.get(0).getLookupValueDesc());
        } catch (DataNotAvailableErrorMsg dataNotAvailableErrorfaultMsg) {
            //expected dataNotAvailableError.
            errorfaultMsg = dataNotAvailableErrorfaultMsg;
            assertEquals("REF_DATA_LOOKUP_VW", errorfaultMsg.getFaultInfo().getEntity());
            assertEquals("GROUP_CODE", errorfaultMsg.getFaultInfo().getField());
            assertEquals("No matching records found, error code: ", errorfaultMsg.getFaultInfo().getDescription());
            assertEquals("SIRA", errorfaultMsg.getFaultInfo().getKey());
        }
    }
}

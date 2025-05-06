package com.lloydsbanking.salsa.activate.downstream;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.activate.TestDataHelper;
import com.lloydsbanking.salsa.activate.constants.ActivateCommonConstant;
import com.lloydsbanking.salsa.activate.utility.ExceptionUtilityActivate;
import com.lloydsbanking.salsa.downstream.pam.jdbc.ReferenceDataLookUpDao;
import com.lloydsbanking.salsa.downstream.pam.model.ReferenceDataLookUp;
import com.lloydsbanking.salsa.header.gmo.HeaderRetriever;
import com.lloydstsb.schema.infrastructure.soap.ContactPoint;
import lib_sim_bo.businessobjects.FinanceServiceArrangement;
import lib_sim_gmo.messages.RequestHeader;
import lib_sim_salesprocessmanagement.ia_activateproductarrangement.*;
import lib_sim_salesprocessmanagement.messages.ActivateProductArrangementRequest;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@Category(UnitTest.class)
public class LookUpValueRetrieverTest {

    ReferenceDataLookUpDao referenceDataLookUpDao;

    LookUpValueRetriever lookUpValueRetriever;

    private static final List<String> groupCodeList = Arrays.asList(ActivateCommonConstant.PamGroupCodes.ENCRYPT_KEY_GROUP_CODE, ActivateCommonConstant.PamGroupCodes.PURPOSE_OF_ACCOUNT_CODE);

    RequestHeader requestHeader;

    TestDataHelper testDataHelper;

    @Before
    public void setUp() {
        testDataHelper = new TestDataHelper();
        referenceDataLookUpDao = mock(ReferenceDataLookUpDao.class);
        lookUpValueRetriever = new LookUpValueRetriever(referenceDataLookUpDao);
        requestHeader = testDataHelper.createApaRequestHeader();
        lookUpValueRetriever.headerRetriever = mock(HeaderRetriever.class);
        lookUpValueRetriever.exceptionUtilityActivate = mock(ExceptionUtilityActivate.class);
    }

    @Test
    public void getLookUpValuesTest() {
        String channelId = "LTB";
        when(referenceDataLookUpDao.findByChannelAndGroupCodeIn(channelId, groupCodeList)).thenReturn(testDataHelper.createLookupData());
        List<ReferenceDataLookUp> referenceDateLookUpList = lookUpValueRetriever.getLookUpValues(groupCodeList, channelId);
        assertEquals(referenceDateLookUpList.size(), 3);
        assertEquals(referenceDateLookUpList.get(0).getLookupValueDesc(), "WZ_ESB_V1-sscert.pem");
        assertEquals(referenceDateLookUpList.get(1).getLookupText(), "SPORI");
    }

    @Test
    public void getChannelIdTest() {
        ReferenceDataLookUp referenceDateLookUp = new ReferenceDataLookUp();
        referenceDateLookUp.setChannel("IBL");
        when(lookUpValueRetriever.referenceDataLookUpDao.findByGroupCodeAndLookupValueDesc(ActivateCommonConstant.PamGroupCodes.CONTACT_POINTID_GROUP_CD, "000777505")).thenReturn(new ArrayList<ReferenceDataLookUp>(Arrays.asList(referenceDateLookUp)));
        List<ReferenceDataLookUp> referenceDateLookUpList = lookUpValueRetriever.getChannelId("000777505", new RequestHeader());
        assertEquals("IBL", referenceDateLookUpList.get(0).getChannel());
    }

    @Test
    public void retrieveContactPointIdTest() {
        ReferenceDataLookUp referenceDateLookUp = new ReferenceDataLookUp();
        referenceDateLookUp.setLookupValueDesc("0000777505");
        ArrayList groupCodeList = new ArrayList();
        groupCodeList.add(ActivateCommonConstant.PamGroupCodes.CONTACT_POINTID_GROUP_CD);
        when(lookUpValueRetriever.referenceDataLookUpDao.findByChannelAndGroupCodeIn("LTB", groupCodeList)).thenReturn(new ArrayList<ReferenceDataLookUp>(Arrays.asList(referenceDateLookUp)));
        String contactPointId = lookUpValueRetriever.retrieveContactPointId("LTB", new RequestHeader());
        assertEquals("0000777505", contactPointId);
    }


    @Test(expected = ActivateProductArrangementDataNotAvailableErrorMsg.class)
    public void testRetrieveChannelIdErrorScenario() throws ActivateProductArrangementDataNotAvailableErrorMsg, ActivateProductArrangementInternalSystemErrorMsg, ActivateProductArrangementResourceNotAvailableErrorMsg, ActivateProductArrangementExternalBusinessErrorMsg, ActivateProductArrangementExternalSystemErrorMsg {
        ActivateProductArrangementRequest request = testDataHelper.createApaRequestForPca();
        ContactPoint contactPoint = new ContactPoint();
        contactPoint.setContactPointId("0000777505");
        when(lookUpValueRetriever.headerRetriever.getContactPoint(request.getHeader().getLloydsHeaders())).thenReturn(contactPoint);
        when(lookUpValueRetriever.referenceDataLookUpDao.findByGroupCodeAndLookupValueDesc(ActivateCommonConstant.PamGroupCodes.CONTACT_POINTID_GROUP_CD, "000777505")).thenReturn(new ArrayList<ReferenceDataLookUp>());
        when(lookUpValueRetriever.exceptionUtilityActivate.dataNotAvailableError("CONTACT_POINT_ID", "REF_DATA_LOOKUP_VW", "No matching records found, error code: ", request.getHeader())).thenReturn(new ActivateProductArrangementDataNotAvailableErrorMsg());
        lookUpValueRetriever.retrieveChannelId("1", request.getHeader(), "LRA");
    }

    @Test(expected = ActivateProductArrangementDataNotAvailableErrorMsg.class)
    public void testRetrieveLookupErrorScenario() throws ActivateProductArrangementDataNotAvailableErrorMsg, ActivateProductArrangementInternalSystemErrorMsg, ActivateProductArrangementResourceNotAvailableErrorMsg, ActivateProductArrangementExternalBusinessErrorMsg, ActivateProductArrangementExternalSystemErrorMsg {
        ActivateProductArrangementRequest request = testDataHelper.createApaRequestForPca();
        ContactPoint contactPoint = new ContactPoint();
        contactPoint.setContactPointId("0000777505");
        when(lookUpValueRetriever.headerRetriever.getContactPoint(request.getHeader().getLloydsHeaders())).thenReturn(contactPoint);
        when(referenceDataLookUpDao.findByChannelAndGroupCodeIn("LTB", groupCodeList)).thenReturn(new ArrayList<ReferenceDataLookUp>());
        request.getHeader().setChannelId("LTB");
        when(lookUpValueRetriever.exceptionUtilityActivate.dataNotAvailableError(ActivateCommonConstant.PamGroupCodes.ENCRYPT_KEY_GROUP_CODE + ActivateCommonConstant.PamGroupCodes.PURPOSE_OF_ACCOUNT_CODE, "REF_DATA_LOOKUP_VW", "No matching records found, error code:", request.getHeader())).thenReturn(new ActivateProductArrangementDataNotAvailableErrorMsg());
        lookUpValueRetriever.retrieveEncryptionKeyAndAccountPurposeMap(request.getHeader());
    }

    @Test
    public void retrieveLookUpValuesTest() {
        String channelId = "LTB";
        List<String> groupCode = new ArrayList<>();
        groupCode.add("IND");
        groupCode.add("AUS");
        List<String> lookUpText = new ArrayList<>();
        lookUpText.add("text1");
        lookUpText.add("text2");
        when(lookUpValueRetriever.referenceDataLookUpDao.findByChannelAndGroupCodeInAndLookupTextIn(channelId, groupCode, lookUpText)).thenReturn(testDataHelper.createLookupData());
        List<ReferenceDataLookUp> referenceDateLookUpList = lookUpValueRetriever.retrieveLookUpValues(lookUpText, channelId, groupCode);
        assertEquals(referenceDateLookUpList.size(), 3);
        assertEquals(referenceDateLookUpList.get(0).getLookupValueDesc(), "WZ_ESB_V1-sscert.pem");
        assertEquals(referenceDateLookUpList.get(1).getLookupText(), "SPORI");
    }

    @Test
    public void retrieveLookUpValuesWhenLookUpTextIsNullTest() {
        String channelId = "LTB";
        List<String> groupCode = new ArrayList<>();
        groupCode.add("IND");
        groupCode.add("AUS");
        List<String> lookUpText = new ArrayList<>();
        when(lookUpValueRetriever.referenceDataLookUpDao.findByChannelAndGroupCodeIn(channelId, groupCode)).thenReturn(testDataHelper.createLookupData());
        List<ReferenceDataLookUp> referenceDateLookUpList = lookUpValueRetriever.retrieveLookUpValues(lookUpText, channelId, groupCode);
        assertEquals(3, referenceDateLookUpList.size());
        assertEquals(referenceDateLookUpList.get(0).getLookupValueDesc(), "WZ_ESB_V1-sscert.pem");
        assertEquals(referenceDateLookUpList.get(1).getLookupText(), "SPORI");
    }

    @Test
    public void testRetrieveEncryptionKeyAndAccountPurposeMap() throws ActivateProductArrangementDataNotAvailableErrorMsg, ActivateProductArrangementResourceNotAvailableErrorMsg {
        List<String> GROUP_CODE_LIST = Arrays.asList(ActivateCommonConstant.PamGroupCodes.ENCRYPT_KEY_GROUP_CODE, ActivateCommonConstant.PamGroupCodes.PURPOSE_OF_ACCOUNT_CODE);
        when(lookUpValueRetriever.referenceDataLookUpDao.findByChannelAndGroupCodeIn(any(String.class), any(List.class))).thenReturn(testDataHelper.createLookupData());
        lookUpValueRetriever.retrieveEncryptionKeyAndAccountPurposeMap(requestHeader);
        verify(lookUpValueRetriever.referenceDataLookUpDao).findByChannelAndGroupCodeIn("IBL", GROUP_CODE_LIST);
    }

    @Test(expected = ActivateProductArrangementResourceNotAvailableErrorMsg.class)
    public void testRetrieveChannelForResourceNotAvailable() throws ActivateProductArrangementDataNotAvailableErrorMsg, ActivateProductArrangementResourceNotAvailableErrorMsg {
        ActivateProductArrangementRequest request = testDataHelper.createApaRequestForPca();
        ContactPoint contactPoint = new ContactPoint();
        contactPoint.setContactPointId("0000777505");
        when(lookUpValueRetriever.headerRetriever.getContactPoint(request.getHeader().getLloydsHeaders())).thenReturn(contactPoint);
        when(lookUpValueRetriever.referenceDataLookUpDao.findByGroupCodeAndLookupValueDesc(any(String.class), any(String.class))).thenThrow(DataIntegrityViolationException.class);
        when(lookUpValueRetriever.exceptionUtilityActivate.resourceNotAvailableError(any(RequestHeader.class), any(String.class))).thenThrow(ActivateProductArrangementResourceNotAvailableErrorMsg.class);
        lookUpValueRetriever.retrieveChannelId("1", request.getHeader(), "LRA");
    }

    @Test(expected = ActivateProductArrangementResourceNotAvailableErrorMsg.class)
    public void testRetrieveEncryptionForResourceNotAvailable() throws ActivateProductArrangementDataNotAvailableErrorMsg, ActivateProductArrangementResourceNotAvailableErrorMsg {
        ActivateProductArrangementRequest request = testDataHelper.createApaRequestForPca();
        ContactPoint contactPoint = new ContactPoint();
        contactPoint.setContactPointId("0000777505");
        when(lookUpValueRetriever.headerRetriever.getContactPoint(request.getHeader().getLloydsHeaders())).thenReturn(contactPoint);
        when(referenceDataLookUpDao.findByChannelAndGroupCodeIn("LTB", groupCodeList)).thenThrow(DataIntegrityViolationException.class);
        request.getHeader().setChannelId("LTB");
        when(lookUpValueRetriever.exceptionUtilityActivate.resourceNotAvailableError(any(RequestHeader.class), any(String.class))).thenThrow(ActivateProductArrangementResourceNotAvailableErrorMsg.class);
        lookUpValueRetriever.retrieveEncryptionKeyAndAccountPurposeMap(request.getHeader());
    }

    @Test(expected = ActivateProductArrangementDataNotAvailableErrorMsg.class)
    public void testRetrieveLookUpForDataNotAvailableError() throws ActivateProductArrangementDataNotAvailableErrorMsg, ActivateProductArrangementResourceNotAvailableErrorMsg {
        ActivateProductArrangementRequest request = testDataHelper.createApaRequestForPca();
        List<String> groupCodes = new ArrayList<>();
        groupCodes.add("123");
        ContactPoint contactPoint = new ContactPoint();
        contactPoint.setContactPointId("0000777505");
        when(lookUpValueRetriever.headerRetriever.getContactPoint(request.getHeader().getLloydsHeaders())).thenReturn(contactPoint);
        when(referenceDataLookUpDao.findByChannelAndGroupCodeIn("LTB", groupCodeList)).thenReturn(new ArrayList<ReferenceDataLookUp>());
        request.getHeader().setChannelId("LTB");
        when(lookUpValueRetriever.exceptionUtilityActivate.dataNotAvailableError(any(String.class), any(String.class), any(String.class), any(RequestHeader.class))).thenReturn(new ActivateProductArrangementDataNotAvailableErrorMsg());
        lookUpValueRetriever.retrieveLookUpValues(request.getHeader(), groupCodes);
    }

    @Test(expected = ActivateProductArrangementResourceNotAvailableErrorMsg.class)
    public void testRetrieveLookUpForResourceNotAvailable() throws ActivateProductArrangementDataNotAvailableErrorMsg, ActivateProductArrangementResourceNotAvailableErrorMsg {
        ActivateProductArrangementRequest request = testDataHelper.createApaRequestForPca();
        List<String> groupCodes = new ArrayList<>();
        groupCodes.add("123");
        ContactPoint contactPoint = new ContactPoint();
        contactPoint.setContactPointId("0000777505");
        when(lookUpValueRetriever.headerRetriever.getContactPoint(request.getHeader().getLloydsHeaders())).thenReturn(contactPoint);
        when(referenceDataLookUpDao.findByChannelAndGroupCodeIn(any(String.class), any(List.class))).thenThrow(DataIntegrityViolationException.class);
        request.getHeader().setChannelId("LTB");
        when(lookUpValueRetriever.exceptionUtilityActivate.resourceNotAvailableError(any(RequestHeader.class), any(String.class))).thenThrow(ActivateProductArrangementResourceNotAvailableErrorMsg.class);
        lookUpValueRetriever.retrieveLookUpValues(request.getHeader(), groupCodes);
    }

    @Test
    public void testRetrieveChannelWithDbEvent() throws ActivateProductArrangementDataNotAvailableErrorMsg, ActivateProductArrangementResourceNotAvailableErrorMsg {
        ActivateProductArrangementRequest request = testDataHelper.createApaRequestForPca();
        lookUpValueRetriever.retrieveChannelId("2", request.getHeader(), "CC");
    }

    @Test
    public void testRetrieveChannelWithDbEventANdLRA() throws ActivateProductArrangementDataNotAvailableErrorMsg, ActivateProductArrangementResourceNotAvailableErrorMsg {
        ActivateProductArrangementRequest request = testDataHelper.createApaRequestForPca();
        when(lookUpValueRetriever.headerRetriever.getContactPoint(request.getHeader().getLloydsHeaders())).thenReturn(null);
        lookUpValueRetriever.retrieveChannelId("2", request.getHeader(), "LRA");
    }

    @Test
    public void testPopulateLookUpData() {
        FinanceServiceArrangement financeServiceArrangement = testDataHelper.createFinanceServiceArrangement();
        List<ReferenceDataLookUp> referenceDataLookUpList = new ArrayList<>();
        referenceDataLookUpList.add(new ReferenceDataLookUp());
        referenceDataLookUpList.add(new ReferenceDataLookUp());
        referenceDataLookUpList.get(0).setGroupCode("ACQUIRE_COUNTRY_NAME");
        referenceDataLookUpList.get(1).setGroupCode("ACQUIRE_COUNTRY_CODE");
        referenceDataLookUpList.get(0).setLookupValueDesc("Country");
        referenceDataLookUpList.get(0).setLookupText("");
        referenceDataLookUpList.get(1).setLookupValueDesc("Country");
        referenceDataLookUpList.get(1).setLookupText("");
        String previousAddressCountry = lookUpValueRetriever.populateLookUpData(referenceDataLookUpList, financeServiceArrangement);
        assertEquals("Country", previousAddressCountry);

    }
}

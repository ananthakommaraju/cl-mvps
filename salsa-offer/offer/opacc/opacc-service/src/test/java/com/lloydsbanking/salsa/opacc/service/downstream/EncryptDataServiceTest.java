package com.lloydsbanking.salsa.opacc.service.downstream;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.downstream.pam.model.ReferenceDataLookUp;
import com.lloydsbanking.salsa.header.gmo.HeaderRetriever;
import com.lloydsbanking.salsa.offer.OfferException;
import com.lloydsbanking.salsa.offer.pam.LookupDataRetriever;
import com.lloydsbanking.salsa.opacc.service.TestDataHelper;
import com.lloydsbanking.salsa.soap.encrpyt.objects.EncryptDataRequest;
import com.lloydsbanking.salsa.soap.encrpyt.objects.EncryptDataResponse;
import lib_sim_bo.businessobjects.AccessToken;
import lib_sim_gmo.exception.DataNotAvailableErrorMsg;
import lib_sim_gmo.exception.ResourceNotAvailableErrorMsg;
import lib_sim_gmo.messages.RequestHeader;
import lib_sim_salesprocessmanagement.ia_offerproductarrangement.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Category(UnitTest.class)
public class EncryptDataServiceTest {

    private EncryptDataService encryptDataService;
    private TestDataHelper testDataHelper;

    @Before
    public void setUp() {
        encryptDataService = new EncryptDataService();
        encryptDataService.headerRetriever = new HeaderRetriever();
        encryptDataService.offerLookupDataRetriever = mock(LookupDataRetriever.class);
        encryptDataService.encryptDataRetriever = mock(EncryptDataRetriever.class);
        testDataHelper = new TestDataHelper();
    }

    @Test
    public void testRetrieveEncryptDataAccessTokenNotNull() throws OfferProductArrangementInternalServiceErrorMsg, OfferProductArrangementResourceNotAvailableErrorMsg, OfferProductArrangementDataNotAvailableErrorMsg, OfferProductArrangementExternalBusinessErrorMsg, OfferProductArrangementExternalServiceErrorMsg, DataNotAvailableErrorMsg, ResourceNotAvailableErrorMsg, OfferException {
        AccessToken accessToken = testDataHelper.generateOfferProductArrangementPCCRequest("LTB").getProductArrangement().getPrimaryInvolvedParty().getAccessToken();
        List<ReferenceDataLookUp> lookUpList = new ArrayList<>();
        ReferenceDataLookUp referenceDataLookUp = new ReferenceDataLookUp();
        referenceDataLookUp.setDescription("Any key");
        lookUpList.add(0,referenceDataLookUp);
        EncryptDataResponse encryptDataResponse = testDataHelper.createEncryptDataRequestResponse(0);
        when(encryptDataService.offerLookupDataRetriever.getChannelIdFromContactPointId(any(String.class))).thenReturn("LTB");
        when(encryptDataService.offerLookupDataRetriever.getChannelIdFromContactPointId(any(String.class))).thenReturn("LTB");
        when(encryptDataService.offerLookupDataRetriever.getLookupListFromChannelAndGroupCodeList(any(String.class), anyList())).thenReturn(lookUpList);
        when(encryptDataService.encryptDataRetriever.retrieveEncryptDataResponse(any(EncryptDataRequest.class), any(RequestHeader.class))).thenReturn(encryptDataResponse);
        encryptDataService.retrieveEncryptData(accessToken, testDataHelper.createOpaccRequestHeader("LTB"));
        assertEquals(testDataHelper.MEMORABLE_INFO, accessToken.getMemorableInfo());
    }

    @Test(expected = OfferException.class)
    public void testRetrieveEncryptDataAccessTokenThrowsError() throws OfferProductArrangementInternalServiceErrorMsg, OfferProductArrangementResourceNotAvailableErrorMsg, OfferProductArrangementDataNotAvailableErrorMsg, OfferProductArrangementExternalBusinessErrorMsg, OfferProductArrangementExternalServiceErrorMsg, DataNotAvailableErrorMsg, ResourceNotAvailableErrorMsg, OfferException {
        AccessToken accessToken = testDataHelper.generateOfferProductArrangementPCCRequest("LTB").getProductArrangement().getPrimaryInvolvedParty().getAccessToken();
        List<ReferenceDataLookUp> lookUpList = new ArrayList<>();
        ReferenceDataLookUp referenceDataLookUp = new ReferenceDataLookUp();
        referenceDataLookUp.setDescription("Any key");
        lookUpList.add(0,referenceDataLookUp);
        EncryptDataResponse encryptDataResponse = testDataHelper.createEncryptDataRequestResponse(0);
        when(encryptDataService.offerLookupDataRetriever.getChannelIdFromContactPointId(any(String.class))).thenReturn("LTB");
        when(encryptDataService.offerLookupDataRetriever.getChannelIdFromContactPointId(any(String.class))).thenReturn("LTB");
        when(encryptDataService.offerLookupDataRetriever.getLookupListFromChannelAndGroupCodeList(any(String.class), anyList())).thenReturn(lookUpList);
        when(encryptDataService.encryptDataRetriever.retrieveEncryptDataResponse(any(EncryptDataRequest.class), any(RequestHeader.class))).thenThrow(ResourceNotAvailableErrorMsg.class);
        encryptDataService.retrieveEncryptData(accessToken, testDataHelper.createOpaccRequestHeader("LTB"));
    }

    @Test(expected = OfferException.class)
    public void testDataNotAvailableError() throws OfferProductArrangementInternalServiceErrorMsg, OfferProductArrangementResourceNotAvailableErrorMsg, OfferProductArrangementDataNotAvailableErrorMsg, OfferProductArrangementExternalBusinessErrorMsg, OfferProductArrangementExternalServiceErrorMsg, DataNotAvailableErrorMsg, ResourceNotAvailableErrorMsg, OfferException {
        AccessToken accessToken = testDataHelper.generateOfferProductArrangementPCCRequest("LTB").getProductArrangement().getPrimaryInvolvedParty().getAccessToken();
        List<ReferenceDataLookUp> lookUpList = new ArrayList<>();
        ReferenceDataLookUp referenceDataLookUp = new ReferenceDataLookUp();
        referenceDataLookUp.setDescription("Any key");
        lookUpList.add(0,referenceDataLookUp);
        EncryptDataResponse encryptDataResponse = testDataHelper.createEncryptDataRequestResponse(0);
        when(encryptDataService.offerLookupDataRetriever.getChannelIdFromContactPointId(any(String.class))).thenReturn("LTB");
        when(encryptDataService.offerLookupDataRetriever.getChannelIdFromContactPointId(any(String.class))).thenReturn("LTB");
        when(encryptDataService.offerLookupDataRetriever.getLookupListFromChannelAndGroupCodeList(any(String.class), anyList())).thenThrow(DataNotAvailableErrorMsg.class);
        encryptDataService.retrieveEncryptData(accessToken, testDataHelper.createOpaccRequestHeader("LTB"));
    }



    @Test
    public void testRetrieveEncryptDataAccessTokenNull() throws OfferProductArrangementInternalServiceErrorMsg, OfferProductArrangementResourceNotAvailableErrorMsg, OfferProductArrangementDataNotAvailableErrorMsg, OfferProductArrangementExternalBusinessErrorMsg, OfferProductArrangementExternalServiceErrorMsg, OfferException {
        AccessToken accessToken = null;
        encryptDataService.retrieveEncryptData(accessToken, testDataHelper.createOpaccRequestHeader("LTB"));
        assertNull(accessToken);
    }
}

package com.lloydsbanking.salsa.eligibility.service.downstream;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.downstream.pam.jdbc.ReferenceDataLookUpDao;
import com.lloydsbanking.salsa.downstream.pam.model.ReferenceDataLookUp;
import com.lloydsbanking.salsa.downstream.pam.service.RetrievePamService;
import com.lloydsbanking.salsa.eligibility.service.utility.header.GmoToGboRequestHeaderConverter;
import com.lloydsbanking.salsa.eligibility.service.utility.wz.ExceptionUtility;
import com.lloydsbanking.salsa.eligibility.wz.TestDataHelper;
import com.lloydsbanking.salsa.header.gmo.RequestToResponseHeaderConverter;
import lib_sim_gmo.messages.RequestHeader;
import lib_sim_salesprocessmanagement.ia_determineeligiblecustomerinstruction.DetermineEligibleCustomerInstructionsDataNotAvailableErrorMsg;
import lib_sim_salesprocessmanagement.ia_determineeligiblecustomerinstruction.DetermineEligibleCustomerInstructionsResourceNotAvailableErrorMsg;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Category(UnitTest.class)
public class EligibilityPAMRetrieverTest {
    ExceptionUtility exceptionUtility;

    TestDataHelper dataHelper;

    EligibilityPAMRetriever eligibilityPAMRetriever;

    ReferenceDataLookUpDao refLookUpDao;

    RequestHeader requestHeader;
    RetrievePamService retrievePamService;

    @Before
    public void setUp() {
        exceptionUtility = new ExceptionUtility(mock(RequestToResponseHeaderConverter.class));
        refLookUpDao = mock(ReferenceDataLookUpDao.class);
        retrievePamService = mock(RetrievePamService.class);
        dataHelper = new TestDataHelper();
        eligibilityPAMRetriever = new EligibilityPAMRetriever(refLookUpDao, exceptionUtility, retrievePamService);

        requestHeader = dataHelper.createEligibilityRequestHeader(dataHelper.TEST_RETAIL_CHANNEL_ID, dataHelper.TEST_INTERACTION_ID, dataHelper.TEST_OCIS_ID, dataHelper.TEST_CUSTOMER_ID, dataHelper.TEST_CONTACT_POINT_ID);
    }

    @Test
    public void getChannelIdFromContactPointIdTest() throws DetermineEligibleCustomerInstructionsDataNotAvailableErrorMsg, DetermineEligibleCustomerInstructionsResourceNotAvailableErrorMsg {

        ReferenceDataLookUp referenceDataLookUp = dataHelper.createReferenceDataLookUp();
        List<ReferenceDataLookUp> referenceDataLookUpList = new ArrayList<>();
        referenceDataLookUpList.add(referenceDataLookUp);
        String groupCode = referenceDataLookUp.getGroupCode();
        String cnt_pnt_prtflio = "Cnt_Pnt_Prtflio";
        when(refLookUpDao.findByGroupCodeAndLookupValueDesc(cnt_pnt_prtflio, referenceDataLookUp.getLookupValueDesc())).thenReturn(referenceDataLookUpList);
        eligibilityPAMRetriever.getChannelIdFromContactPointId(groupCode, requestHeader);

        assertEquals("IBL", eligibilityPAMRetriever.getChannelIdFromContactPointId(groupCode, requestHeader));

    }

    @Test(expected = DetermineEligibleCustomerInstructionsDataNotAvailableErrorMsg.class)
    public void getChannelIdFromContactPointIdDataNotAvlTest() throws DetermineEligibleCustomerInstructionsDataNotAvailableErrorMsg, DetermineEligibleCustomerInstructionsResourceNotAvailableErrorMsg {
        try {
            eligibilityPAMRetriever.getChannelIdFromContactPointId("Cnt_Pnt_Prtflio", requestHeader);
        } catch (DetermineEligibleCustomerInstructionsDataNotAvailableErrorMsg e) {
            assertEquals("REF_DATA_LOOKUP_VW", e.getFaultInfo().getEntity());
            assertEquals("No matching records found, error code: ", e.getFaultInfo().getDescription());
            assertEquals("CONTACT_POINT_ID", e.getFaultInfo().getField());
            throw e;
        }

    }

    @Test
    public void testGetLookUpValues() {
        GmoToGboRequestHeaderConverter headerConverter = new GmoToGboRequestHeaderConverter();
        lb_gbo_sales.messages.RequestHeader gboHeader = headerConverter.convert(requestHeader);
        List<String> groupCodes = new ArrayList<>();
        ReferenceDataLookUp referenceDataLookUp = dataHelper.createReferenceDataLookUp();
        referenceDataLookUp.setLookupValueDesc("lookUpValueDesc");
        List<ReferenceDataLookUp> referenceDataLookUpList = new ArrayList<>();
        referenceDataLookUpList.add(referenceDataLookUp);
        groupCodes.add("PARTY_EVID_TYPE_CODE");
        when(refLookUpDao.findByChannelAndGroupCodeIn(requestHeader.getChannelId(), groupCodes)).thenReturn(referenceDataLookUpList);

        List<String> lookUpValues = eligibilityPAMRetriever.getLookUpValues(gboHeader.getChannelId());
        assertEquals(1, lookUpValues.size());
        assertEquals("lookUpValueDesc", lookUpValues.get(0));
    }

    @Test
    public void getNumberOfFulfilledFinanceApplicationTest() {
        when(retrievePamService.countOfApplicationExistForCustomer("542107294", "1010", 30, "106", "0001")).thenReturn(223);
        Integer result = eligibilityPAMRetriever.getNumberOfFulfilledFinanceApplication("542107294");
        assertEquals(223, result.intValue());


    }
}


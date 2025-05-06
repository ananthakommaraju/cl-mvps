package com.lloydsbanking.salsa.eligibility.service.downstream;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.downstream.account.client.AccountClient;
import com.lloydsbanking.salsa.downstream.account.convert.BapiHeaderToStHeaderConverter;
import com.lloydsbanking.salsa.eligibility.TestDataHelper;
import com.lloydsbanking.salsa.header.HeaderRetriever;
import com.lloydsbanking.salsa.soap.fs.account.StError;
import com.lloydsbanking.salsa.soap.fs.account.StHeader;
import com.lloydstsb.ib.wsbridge.account.StB766ARetrieveCBSAppGroup;
import com.lloydstsb.ib.wsbridge.account.StB766BRetrieveCBSAppGroup;
import com.lloydstsb.schema.enterprise.lcsm.BAPIHeader;
import com.lloydstsb.schema.infrastructure.soap.ContactPoint;
import com.lloydstsb.schema.infrastructure.soap.ServiceRequest;
import lb_gbo_sales.messages.DetermineElegibileInstructionsRequest;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Category(UnitTest.class)
public class AppGroupRetrieverTest {
    AppGroupRetriever appGroupRetriever;

    TestDataHelper testDataHelper;

    HeaderRetriever headerRetriever = new HeaderRetriever();

    BAPIHeader bapiHeader;

    ServiceRequest serviceRequest;

    StHeader stHeader;

    ContactPoint contactPoint;

    DetermineElegibileInstructionsRequest upstreamRequest;

    @Before
    public void setUp() {
        appGroupRetriever = new AppGroupRetriever();
        appGroupRetriever.accountClient = mock(AccountClient.class);
        appGroupRetriever.bapiHeaderToStHeaderConverter = mock(BapiHeaderToStHeaderConverter.class);
        appGroupRetriever.headerRetriever = headerRetriever;
        testDataHelper = new TestDataHelper();
        upstreamRequest = testDataHelper.createEligibilityRequest("P_TRAV_MON", TestDataHelper.TEST_OCIS_ID, null, TestDataHelper.TEST_CONTACT_POINT_ID);
        bapiHeader = headerRetriever.getBapiInformationHeader(upstreamRequest.getHeader().getLloydsHeaders()).getBAPIHeader();
        serviceRequest = headerRetriever.getServiceRequest(upstreamRequest.getHeader().getLloydsHeaders());
        contactPoint = headerRetriever.getContactPoint(upstreamRequest.getHeader().getLloydsHeaders());
        stHeader = appGroupRetriever.bapiHeaderToStHeaderConverter.convert(bapiHeader, serviceRequest, contactPoint.getContactPointId());
    }

    @Test
    public void testCallRetrieveCBSAppGroup() throws Exception {
        StB766ARetrieveCBSAppGroup stB766ARetrieveCBSAppGroup = createRequest(stHeader, upstreamRequest.getCustomerArrangements().get(0).getSortCode());

        when(appGroupRetriever.accountClient.retrieveCBSAppGroup(stB766ARetrieveCBSAppGroup)).thenReturn(new StB766BRetrieveCBSAppGroup());

        appGroupRetriever.callRetrieveCBSAppGroup(upstreamRequest.getHeader(), upstreamRequest.getCustomerArrangements().get(0).getSortCode(), false);

        verify(appGroupRetriever.accountClient).retrieveCBSAppGroup(stB766ARetrieveCBSAppGroup);
    }

    @Test
    public void testCallRetrieveCBSAppGroupForWZ() throws Exception {
        StB766ARetrieveCBSAppGroup stB766ARetrieveCBSAppGroup = createRequest(stHeader, upstreamRequest.getCustomerArrangements().get(0).getSortCode());
        StB766BRetrieveCBSAppGroup stB766BRetrieveCBSAppGroup= createResponse("01");
        when(appGroupRetriever.accountClient.retrieveCBSAppGroup(stB766ARetrieveCBSAppGroup)).thenReturn(stB766BRetrieveCBSAppGroup);

        String cbsAppGrp=appGroupRetriever.callRetrieveCBSAppGroup(upstreamRequest.getHeader(), upstreamRequest.getCustomerArrangements().get(0).getSortCode(), true);

        assertEquals("01", cbsAppGrp);
    }

    private StB766ARetrieveCBSAppGroup createRequest(StHeader stHeader, String sortCode) {
        StB766ARetrieveCBSAppGroup stB766ARetrieveCBSAppGroup = new StB766ARetrieveCBSAppGroup();
        stB766ARetrieveCBSAppGroup.setStheader(stHeader);
        stB766ARetrieveCBSAppGroup.setSortcode(sortCode);
        return stB766ARetrieveCBSAppGroup;
    }

    private StB766BRetrieveCBSAppGroup createResponse(String cbsAppGrp){
        StB766BRetrieveCBSAppGroup stB766BRetrieveCBSAppGroup= new StB766BRetrieveCBSAppGroup();
        stB766BRetrieveCBSAppGroup.setSterror(new StError());
        stB766BRetrieveCBSAppGroup.getSterror().setErrorno(0);
        stB766BRetrieveCBSAppGroup.setCbsappgroup(cbsAppGrp);
        return stB766BRetrieveCBSAppGroup;
    }
}

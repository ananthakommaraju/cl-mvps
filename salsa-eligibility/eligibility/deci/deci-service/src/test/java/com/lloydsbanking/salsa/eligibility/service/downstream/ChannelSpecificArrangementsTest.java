package com.lloydsbanking.salsa.eligibility.service.downstream;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.downstream.user.client.UserClient;
import com.lloydsbanking.salsa.downstream.user.convert.BapiHeaderUserToStHeaderConverter;
import com.lloydsbanking.salsa.eligibility.TestDataHelper;
import com.lloydsbanking.salsa.eligibility.service.utility.ExceptionUtility;
import com.lloydsbanking.salsa.eligibility.service.utility.exceptions.SalsaInternalResourceNotAvailableException;
import com.lloydsbanking.salsa.eligibility.service.utility.exceptions.SalsaInternalServiceException;
import com.lloydsbanking.salsa.header.HeaderRetriever;
import com.lloydsbanking.salsa.header.RequestToResponseHeaderConverter;
import com.lloydsbanking.salsa.soap.fs.user.StAccountListDetail;
import com.lloydstsb.ib.wsbridge.user.StB162AUserAccReadList;
import com.lloydstsb.ib.wsbridge.user.StB162BUserAccReadList;
import lb_gbo_sales.interfaces.ia_determineelegiblecustomerinstructions.DetermineEligibleInstructionsInternalServiceErrorMsg;
import lb_gbo_sales.interfaces.ia_determineelegiblecustomerinstructions.DetermineEligibleInstructionsResourceNotAvailableErrorMsg;
import lb_gbo_sales.messages.DetermineElegibileInstructionsRequest;
import lb_gbo_sales.messages.RequestHeader;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Category(UnitTest.class)
public class ChannelSpecificArrangementsTest {
    private ChannelSpecificArrangements channelSpecificArrangements;

    private TestDataHelper testDataHelper;

    RequestHeader header;

    DetermineElegibileInstructionsRequest upstreamRequest;

    HeaderRetriever headerRetriever;

    @Before
    public void setUp() {

        channelSpecificArrangements = new ChannelSpecificArrangements();
        testDataHelper = new TestDataHelper();
        channelSpecificArrangements.userClient = mock(UserClient.class);
        channelSpecificArrangements.exceptionUtility = new ExceptionUtility(new RequestToResponseHeaderConverter());
        upstreamRequest = testDataHelper.createEligibilityRequest("abc", testDataHelper.TEST_OCIS_ID, testDataHelper.TEST_RETAIL_CHANNEL_ID, testDataHelper.TEST_CONTACT_POINT_ID);
        header = upstreamRequest.getHeader();

        channelSpecificArrangements.headerRetriever = new HeaderRetriever();
        channelSpecificArrangements.bapiHeaderUserToStHeaderConverter = mock(BapiHeaderUserToStHeaderConverter.class);

    }

    @Test
    public void testGetChannelSpecificArrangements() throws DetermineEligibleInstructionsInternalServiceErrorMsg, DetermineEligibleInstructionsResourceNotAvailableErrorMsg, SalsaInternalResourceNotAvailableException, SalsaInternalServiceException {
        StB162BUserAccReadList response = testDataHelper.createB162Response("abc", "L", "C", "IBL");
        when(channelSpecificArrangements.userClient.retrieveAccessibleArrangements(any(StB162AUserAccReadList.class))).thenReturn(response);
        List<StAccountListDetail> stAccountListDetailList = channelSpecificArrangements.getChannelSpecificArrangements(header);
        assertEquals(response.getAstacclistdetail().get(0).getAccountcategory(), stAccountListDetailList.get(0).getAccountcategory());
    }

    @Test(expected = SalsaInternalServiceException.class)
    public void testGetChannelSpecificArrangementsWithError() throws SalsaInternalResourceNotAvailableException, SalsaInternalServiceException {

        StB162BUserAccReadList response = testDataHelper.createB162ResponseWithError(9043, "abc");
        when(channelSpecificArrangements.userClient.retrieveAccessibleArrangements(any(StB162AUserAccReadList.class))).thenReturn(response);
        channelSpecificArrangements.getChannelSpecificArrangements(header);


    }

    @Test(expected = SalsaInternalResourceNotAvailableException.class)
    public void testGetChannelSpecificArrangementsSalsaInternalResourceNotAvailableException() throws SalsaInternalResourceNotAvailableException, SalsaInternalServiceException {

        StB162BUserAccReadList response = testDataHelper.createB162ResponseWithError(9043, "abc");
        when(channelSpecificArrangements.userClient.retrieveAccessibleArrangements(any(StB162AUserAccReadList.class))).thenThrow(SalsaInternalResourceNotAvailableException.class);
        channelSpecificArrangements.getChannelSpecificArrangements(header);

    }

}

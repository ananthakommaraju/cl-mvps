package com.lloydsbanking.salsa.opaloans.service.downstream;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.downstream.loan.client.LoanClient;
import com.lloydsbanking.salsa.downstream.loan.convert.BapiHeaderToStHeaderConverter;
import com.lloydsbanking.salsa.header.gmo.HeaderRetriever;
import com.lloydsbanking.salsa.offer.exception.ExceptionUtility;
import com.lloydsbanking.salsa.opaloans.service.TestDataHelper;
import com.lloydstsb.ib.wsbridge.loan.StB237ALoanSavedListGet;
import com.lloydstsb.ib.wsbridge.loan.StB237BLoanSavedListGet;
import lib_sim_bo.businessobjects.CustomerScore;
import lib_sim_gmo.exception.ExternalServiceErrorMsg;
import lib_sim_gmo.exception.ResourceNotAvailableErrorMsg;
import lib_sim_gmo.messages.RequestHeader;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import javax.xml.ws.WebServiceException;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Category(UnitTest.class)
public class ProductArrangementsRetrieverTest {
    ProductArrangementsRetriever productArrangementsRetriever;

    TestDataHelper dataHelper;

    RequestHeader header;

    @Before
    public void setUp() {
        productArrangementsRetriever = new ProductArrangementsRetriever();
        dataHelper = new TestDataHelper();

        productArrangementsRetriever.bapiHeaderToStHeaderConverter = new BapiHeaderToStHeaderConverter();
        productArrangementsRetriever.headerRetriever = new HeaderRetriever();
        productArrangementsRetriever.exceptionUtility = new ExceptionUtility();
        productArrangementsRetriever.loanClient = mock(LoanClient.class);

        header = dataHelper.createOpaLoansRequestHeader("IBL");
    }

    @Test
    public void testRetrieveProductArrangementsWhenDuplicateSavedLoanExistsAndCreditScoreNoIsNotNull() throws ExternalServiceErrorMsg, ResourceNotAvailableErrorMsg {
        CustomerScore customerScore = dataHelper.createCustomerScoreList().get(0);
        StB237ALoanSavedListGet b237Request = dataHelper.createB237RequestWithGivenStHeader("456662112", "+00668250484");
        StB237BLoanSavedListGet b237Response = dataHelper.createB237Response(0, "true");

        when(productArrangementsRetriever.loanClient.loanSavedList(b237Request)).thenReturn(b237Response);

        boolean isDuplicateSavedLoanExists = productArrangementsRetriever.retrieveProductArrangements(header, "456662112", "+00668250484", customerScore);

        assertEquals(false, isDuplicateSavedLoanExists);
        assertEquals("STPL5819141218084429", customerScore.getScoreIdentifier());
    }

    @Test
    public void testRetrieveProductArrangementsWhenDuplicateSavedLoanExistsAndCreditScoreNoIsNull() throws ExternalServiceErrorMsg, ResourceNotAvailableErrorMsg {
        CustomerScore customerScore = dataHelper.createCustomerScoreList().get(0);
        StB237ALoanSavedListGet b237Request = dataHelper.createB237RequestWithGivenStHeader("456662112", "+00090001232");
        StB237BLoanSavedListGet b237Response = dataHelper.createB237Response(0, "true");
        b237Response.getAstloansavedsummary().get(0).setCreditscoreno(null);

        when(productArrangementsRetriever.loanClient.loanSavedList(b237Request)).thenReturn(b237Response);

        boolean isDuplicateSavedLoanExists = productArrangementsRetriever.retrieveProductArrangements(header, "456662112", "+00090001232", customerScore);

        assertEquals(false, isDuplicateSavedLoanExists);
        assertEquals("STPL5819141218084429", customerScore.getScoreIdentifier());
    }

    @Test
    public void testRetrieveProductArrangementsWhenSavedLoanWithDuplicateStatusNotExists() throws ExternalServiceErrorMsg, ResourceNotAvailableErrorMsg {
        CustomerScore customerScore = dataHelper.createCustomerScoreList().get(0);
        StB237ALoanSavedListGet b237Request = dataHelper.createB237RequestWithGivenStHeader("456662112", "+00090001232");
        StB237BLoanSavedListGet b237Response = dataHelper.createB237Response(0, "false");

        when(productArrangementsRetriever.loanClient.loanSavedList(b237Request)).thenReturn(b237Response);

        boolean isDuplicateSavedLoanExists = productArrangementsRetriever.retrieveProductArrangements(header, "456662112", "+00090001232", customerScore);

        assertEquals(false, isDuplicateSavedLoanExists);
        assertEquals("STPL5819141218084429", customerScore.getScoreIdentifier());
    }

    @Test(expected = ResourceNotAvailableErrorMsg.class)
    public void testRetrieveProductArrangementsForResourceNotAvailableError() throws ExternalServiceErrorMsg, ResourceNotAvailableErrorMsg {
        CustomerScore customerScore = dataHelper.createCustomerScoreList().get(0);
        StB237ALoanSavedListGet b237Request = dataHelper.createB237Request(header, "456662112", "+00090001232");

        when(productArrangementsRetriever.loanClient.loanSavedList(b237Request)).thenThrow(WebServiceException.class);

        productArrangementsRetriever.retrieveProductArrangements(header, "456662112", "+00090001232", customerScore);
    }

    @Test(expected = ExternalServiceErrorMsg.class)
    public void testRetrieveProductArrangementsForExternalServiceError() throws ExternalServiceErrorMsg, ResourceNotAvailableErrorMsg {
        CustomerScore customerScore = dataHelper.createCustomerScoreList().get(0);
        StB237ALoanSavedListGet b237Request = dataHelper.createB237RequestWithGivenStHeader("456662112", "+00090001232");
        StB237BLoanSavedListGet b237Response = dataHelper.createB237Response(130000, "false");

        when(productArrangementsRetriever.loanClient.loanSavedList(any(StB237ALoanSavedListGet.class))).thenReturn(b237Response);

        productArrangementsRetriever.retrieveProductArrangements(header, "456662112", "+00090001232", customerScore);
    }


}

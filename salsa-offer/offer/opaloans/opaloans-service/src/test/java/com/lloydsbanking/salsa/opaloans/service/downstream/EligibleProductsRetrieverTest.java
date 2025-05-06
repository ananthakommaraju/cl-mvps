package com.lloydsbanking.salsa.opaloans.service.downstream;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.downstream.loan.client.LoanClient;
import com.lloydsbanking.salsa.downstream.loan.convert.BapiHeaderToStHeaderConverter;
import com.lloydsbanking.salsa.exception.ExternalBusinessErrorMsg;
import com.lloydsbanking.salsa.header.gmo.HeaderRetriever;
import com.lloydsbanking.salsa.offer.OfferException;
import com.lloydsbanking.salsa.offer.exception.ExceptionUtility;
import com.lloydsbanking.salsa.opaloans.service.TestDataHelper;
import com.lloydsbanking.salsa.opaloans.service.convert.B231ResponseToEligibleProductsConverter;
import com.lloydstsb.ib.wsbridge.loan.StB231ALoanPartyProductsGet;
import com.lloydstsb.ib.wsbridge.loan.StB231BLoanPartyProductsGet;
import lib_sim_bo.businessobjects.FinanceServiceArrangement;
import lib_sim_gmo.messages.RequestHeader;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static org.mockito.Mockito.*;

@Category(UnitTest.class)
public class EligibleProductsRetrieverTest {
    EligibleProductsRetriever eligibleProductsRetriever;

    TestDataHelper dataHelper;

    RequestHeader header;

    @Before
    public void setUp() {
        eligibleProductsRetriever = new EligibleProductsRetriever();
        dataHelper = new TestDataHelper();

        eligibleProductsRetriever.b231ResponseToEligibleProductsConverter = mock(B231ResponseToEligibleProductsConverter.class);
        eligibleProductsRetriever.bapiHeaderToStHeaderConverter = new BapiHeaderToStHeaderConverter();
        eligibleProductsRetriever.exceptionUtility = new ExceptionUtility();
        eligibleProductsRetriever.loanClient = mock(LoanClient.class);
        eligibleProductsRetriever.headerRetriever = new HeaderRetriever();

        header = dataHelper.createOpaLoansRequestHeader("IBL");
    }

    @Test
    public void testFetchEligibleLoanProductsWithCustomerIdentifier() throws OfferException {
        FinanceServiceArrangement financeServiceArrangement = dataHelper.createFinanceServiceArrangement("1988-01-22T06:40:56.046Z", "777146", "03182268");
        financeServiceArrangement.getPrimaryInvolvedParty().setCustomerIdentifier("456662112");
        financeServiceArrangement.getPrimaryInvolvedParty().setCidPersID("+00090001232");

        StB231BLoanPartyProductsGet b231Response = dataHelper.createB231Response(0);

        when(eligibleProductsRetriever.loanClient.retrieveEligibleLoanProducts(any(StB231ALoanPartyProductsGet.class))).thenReturn(b231Response);

        eligibleProductsRetriever.fetchEligibleLoanProducts(header, financeServiceArrangement);

        verify(eligibleProductsRetriever.b231ResponseToEligibleProductsConverter).convert(b231Response, financeServiceArrangement);
    }

    @Test
    public void testFetchEligibleLoanProductsWithoutCustomerIdentifier() throws OfferException {
        FinanceServiceArrangement financeServiceArrangement = dataHelper.createFinanceServiceArrangement("1988-01-22T06:40:56.046Z", "777146", "03182268");
        financeServiceArrangement.getPrimaryInvolvedParty().setCustomerIdentifier(null);
        financeServiceArrangement.getPrimaryInvolvedParty().setCidPersID("+00668250484");

        StB231BLoanPartyProductsGet b231Response = dataHelper.createB231Response(0);

        when(eligibleProductsRetriever.loanClient.retrieveEligibleLoanProducts(any(StB231ALoanPartyProductsGet.class))).thenReturn(b231Response);

        eligibleProductsRetriever.fetchEligibleLoanProducts(header, financeServiceArrangement);

        verify(eligibleProductsRetriever.b231ResponseToEligibleProductsConverter).convert(b231Response, financeServiceArrangement);
    }

    @Test(expected = OfferException.class)
    public void testFetchEligibleLoanProductsForExternalBusinessError() throws OfferException {
        FinanceServiceArrangement financeServiceArrangement = dataHelper.createFinanceServiceArrangement("1988-01-22T06:40:56.046Z", "777146", "03182268");
        financeServiceArrangement.getPrimaryInvolvedParty().setCustomerIdentifier("807489028");
        financeServiceArrangement.getPrimaryInvolvedParty().setCidPersID("+00668250484");

        StB231ALoanPartyProductsGet b231Request = dataHelper.createB231RequestWithGivenStHeader("807489028", "+00668250484");
        StB231BLoanPartyProductsGet b231Response = dataHelper.createB231Response(130000);

        when(eligibleProductsRetriever.loanClient.retrieveEligibleLoanProducts(any(StB231ALoanPartyProductsGet.class))).thenReturn(b231Response);

        eligibleProductsRetriever.fetchEligibleLoanProducts(header, financeServiceArrangement);

        verify(eligibleProductsRetriever.b231ResponseToEligibleProductsConverter, never()).convert(b231Response, financeServiceArrangement);
    }

    @Test(expected = OfferException.class)
    public void testFetchEligibleLoanProductsForResourceNotAvailableError() throws OfferException {
        FinanceServiceArrangement financeServiceArrangement = dataHelper.createFinanceServiceArrangement("1988-01-22T06:40:56.046Z", "777146", "03182268");
        financeServiceArrangement.getPrimaryInvolvedParty().setCustomerIdentifier("807489028");
        financeServiceArrangement.getPrimaryInvolvedParty().setCidPersID("+00668250484");

        StB231ALoanPartyProductsGet b231Request = dataHelper.createB231RequestWithGivenStHeader("807489028", "+00668250484");
        StB231BLoanPartyProductsGet b231Response = dataHelper.createB231Response(130000);

        when(eligibleProductsRetriever.loanClient.retrieveEligibleLoanProducts(any(StB231ALoanPartyProductsGet.class))).thenThrow(OfferException.class);

        eligibleProductsRetriever.fetchEligibleLoanProducts(header, financeServiceArrangement);

        verify(eligibleProductsRetriever.b231ResponseToEligibleProductsConverter, never()).convert(b231Response, financeServiceArrangement);

    }
}

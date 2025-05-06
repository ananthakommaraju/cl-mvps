package com.lloydsbanking.salsa.opaloans.service.downstream;


import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.downstream.switches.SwitchService;
import com.lloydsbanking.salsa.offer.OfferException;
import com.lloydsbanking.salsa.offer.downstream.EligibilityRetriever;
import com.lloydsbanking.salsa.opaloans.ReasonCodes;
import com.lloydsbanking.salsa.opaloans.service.TestDataHelper;
import lib_sim_bo.businessobjects.Customer;
import lib_sim_bo.businessobjects.CustomerScore;
import lib_sim_bo.businessobjects.FinanceServiceArrangement;
import lib_sim_bo.businessobjects.Product;
import lib_sim_gmo.exception.DataNotAvailableErrorMsg;
import lib_sim_gmo.exception.ExternalServiceErrorMsg;
import lib_sim_gmo.exception.InternalServiceErrorMsg;
import lib_sim_gmo.exception.ResourceNotAvailableErrorMsg;
import lib_sim_gmo.messages.RequestHeader;
import lib_sim_salesprocessmanagement.messages.DetermineEligibleCustomerInstructionsRequest;
import lib_sim_salesprocessmanagement.messages.DetermineEligibleCustomerInstructionsResponse;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@Category(UnitTest.class)
public class EligibilityServiceTest {
    private EligibilityService eligibilityService;
    private TestDataHelper testDataHelper;
    private RequestHeader header;

    @Before
    public void setUp() {
        eligibilityService = new EligibilityService();
        testDataHelper = new TestDataHelper();
        eligibilityService.eligibilityRetriever = mock(EligibilityRetriever.class);
        eligibilityService.productArrangementsRetriever = mock(ProductArrangementsRetriever.class);
        eligibilityService.switchClient = mock(SwitchService.class);

        header = testDataHelper.createOpaLoansRequestHeader("IBL");
        header.setChannelId("LTB");
    }

    @Test
    public void testGetCustomerEligibilityStatusWhenOfferedProductsAreEmpty() throws OfferException, ExternalServiceErrorMsg, ResourceNotAvailableErrorMsg, DataNotAvailableErrorMsg, InternalServiceErrorMsg {
        FinanceServiceArrangement productArrangement = testDataHelper.createFinanceServiceArrangement("1988-01-22T06:40:56.046Z", "777146", "03182268");

        boolean isCustomerEligible = eligibilityService.getCustomerEligibilityStatus(header, productArrangement);

        verify(eligibilityService.switchClient, never()).getBrandedSwitchValue("SW_EnableLRA", "LTB", false);
        verify(eligibilityService.productArrangementsRetriever, never()).retrieveProductArrangements(any(RequestHeader.class), any(String.class), any(String.class), any(CustomerScore.class));
        verify(eligibilityService.eligibilityRetriever, never()).callEligibilityService(any(DetermineEligibleCustomerInstructionsRequest.class));

        assertFalse(isCustomerEligible);
        assertNotNull(productArrangement.getReasonCode());
        assertEquals(ReasonCodes.NO_ELIGIBLE_LOAN_PRODUCTS.getValue(), productArrangement.getReasonCode().getCode());
        assertEquals(ReasonCodes.NO_ELIGIBLE_LOAN_PRODUCTS.getKey(), productArrangement.getReasonCode().getDescription());
        assertTrue(productArrangement.getOfferedProducts().isEmpty());
    }

    @Test
    public void testGetCustomerEligibilityStatusWhenLRASwitchIsEnabledAndDuplicateSavedLoanExists() throws ExternalServiceErrorMsg, ResourceNotAvailableErrorMsg, OfferException, DataNotAvailableErrorMsg, InternalServiceErrorMsg {
        FinanceServiceArrangement productArrangement = testDataHelper.createFinanceServiceArrangement("1988-01-22T06:40:56.046Z", "777146", "03182268");
        productArrangement.getOfferedProducts().add(0, new Product());
        Customer customer = productArrangement.getPrimaryInvolvedParty();
        customer.setCustomerIdentifier("456662112");
        customer.setCidPersID("+00090001232");
        customer.getCustomerScore().addAll(testDataHelper.createCustomerScoreList());

        when(eligibilityService.switchClient.getBrandedSwitchValue("SW_EnableLRA", "LTB", false)).thenReturn(true);
        when(eligibilityService.productArrangementsRetriever.retrieveProductArrangements(header, customer.getCustomerIdentifier(), customer.getCidPersID(), customer.getCustomerScore().get(0))).thenReturn(true);

        boolean isCustomerEligible = eligibilityService.getCustomerEligibilityStatus(header, productArrangement);

        verify(eligibilityService.eligibilityRetriever, never()).callEligibilityService(any(DetermineEligibleCustomerInstructionsRequest.class));
        assertFalse(isCustomerEligible);
        assertNotNull(productArrangement.getReasonCode());
        assertEquals(ReasonCodes.SAVED_LOAN_ALREADY_EXISTS.getValue(), productArrangement.getReasonCode().getCode());
        assertEquals(ReasonCodes.SAVED_LOAN_ALREADY_EXISTS.getKey(), productArrangement.getReasonCode().getDescription());
        assertFalse(productArrangement.getOfferedProducts().isEmpty());
    }

    @Test
    public void testGetEligibilityStatusWhenLRASwitchIsEnabledAndDuplicateSavedLoanNotExistsAndDeciReturnsFalse() throws ExternalServiceErrorMsg, ResourceNotAvailableErrorMsg, DataNotAvailableErrorMsg, InternalServiceErrorMsg, OfferException {
        FinanceServiceArrangement productArrangement = testDataHelper.createFinanceServiceArrangement("1988-01-22T06:40:56.046Z", "777146", "03182268");
        productArrangement.getOfferedProducts().add(0, new Product());
        Customer customer = productArrangement.getPrimaryInvolvedParty();
        customer.setCustomerIdentifier("456662112");
        customer.setCidPersID("+00090001232");
        customer.getCustomerScore().addAll(testDataHelper.createCustomerScoreList());

        DetermineEligibleCustomerInstructionsResponse eligibilityResponse = testDataHelper.createEligibilityResponse("false", "CR046", "Eligibility Rule CR046");

        when(eligibilityService.switchClient.getBrandedSwitchValue("SW_EnableLRA", "LTB", false)).thenReturn(true);
        when(eligibilityService.productArrangementsRetriever.retrieveProductArrangements(header, customer.getCustomerIdentifier(), customer.getCidPersID(), customer.getCustomerScore().get(0))).thenReturn(false);
        when(eligibilityService.eligibilityRetriever.callEligibilityService(any(DetermineEligibleCustomerInstructionsRequest.class))).thenReturn(eligibilityResponse);

        boolean isCustomerEligible = eligibilityService.getCustomerEligibilityStatus(header, productArrangement);

        assertFalse(isCustomerEligible);
        assertNotNull(productArrangement.getReasonCode());
        assertEquals("CR046", productArrangement.getReasonCode().getCode());
        assertEquals("Eligibility Rule CR046", productArrangement.getReasonCode().getDescription());
        assertFalse(productArrangement.getOfferedProducts().isEmpty());
    }

    @Test
    public void testGetCustomerEligibilityStatusWhenLRASwitchIsEnabledAndDuplicateSavedLoanNotExistsAndDeciReturnsTrue() throws ExternalServiceErrorMsg, ResourceNotAvailableErrorMsg, DataNotAvailableErrorMsg, InternalServiceErrorMsg, OfferException {
        FinanceServiceArrangement productArrangement = testDataHelper.createFinanceServiceArrangement("1988-01-22T06:40:56.046Z", "777146", "03182268");
        productArrangement.getOfferedProducts().add(0, new Product());
        Customer customer = productArrangement.getPrimaryInvolvedParty();
        customer.setCustomerIdentifier("456662112");
        customer.setCidPersID("+00090001232");
        customer.getCustomerScore().addAll(testDataHelper.createCustomerScoreList());

        DetermineEligibleCustomerInstructionsResponse eligibilityResponse = testDataHelper.createEligibilityResponse("true", "0", null);

        when(eligibilityService.switchClient.getBrandedSwitchValue("SW_EnableLRA", "LTB", false)).thenReturn(true);
        when(eligibilityService.productArrangementsRetriever.retrieveProductArrangements(header, customer.getCustomerIdentifier(), customer.getCidPersID(), customer.getCustomerScore().get(0))).thenReturn(false);
        when(eligibilityService.eligibilityRetriever.callEligibilityService(any(DetermineEligibleCustomerInstructionsRequest.class))).thenReturn(eligibilityResponse);

        boolean isCustomerEligible = eligibilityService.getCustomerEligibilityStatus(header, productArrangement);

        assertTrue(isCustomerEligible);
        assertNull(productArrangement.getReasonCode());
        assertFalse(productArrangement.getOfferedProducts().isEmpty());
    }

    @Test
    public void testGetCustomerEligibilityStatusWhenLRASwitchIsNotEnabledAndDeciReturnsFalse() throws ResourceNotAvailableErrorMsg, InternalServiceErrorMsg, DataNotAvailableErrorMsg, ExternalServiceErrorMsg, OfferException {
        FinanceServiceArrangement productArrangement = testDataHelper.createFinanceServiceArrangement("1988-01-22T06:40:56.046Z", "777146", "03182268");
        productArrangement.getOfferedProducts().add(0, new Product());
        Customer customer = productArrangement.getPrimaryInvolvedParty();
        customer.setCustomerIdentifier("456662112");
        customer.setCidPersID("+00090001232");
        customer.getCustomerScore().addAll(testDataHelper.createCustomerScoreList());

        DetermineEligibleCustomerInstructionsResponse eligibilityResponse = testDataHelper.createEligibilityResponse("false", "CR046", "Eligibility Rule CR046");

        when(eligibilityService.switchClient.getBrandedSwitchValue("SW_EnableLRA", "LTB", false)).thenReturn(false);
        when(eligibilityService.eligibilityRetriever.callEligibilityService(any(DetermineEligibleCustomerInstructionsRequest.class))).thenReturn(eligibilityResponse);

        boolean isCustomerEligible = eligibilityService.getCustomerEligibilityStatus(header, productArrangement);

        verify(eligibilityService.productArrangementsRetriever, never()).retrieveProductArrangements(header, customer.getCustomerIdentifier(), customer.getCidPersID(), customer.getCustomerScore().get(0));
        assertFalse(isCustomerEligible);
        assertNotNull(productArrangement.getReasonCode());
        assertEquals("CR046", productArrangement.getReasonCode().getCode());
        assertEquals("Eligibility Rule CR046", productArrangement.getReasonCode().getDescription());
        assertFalse(productArrangement.getOfferedProducts().isEmpty());
    }
}

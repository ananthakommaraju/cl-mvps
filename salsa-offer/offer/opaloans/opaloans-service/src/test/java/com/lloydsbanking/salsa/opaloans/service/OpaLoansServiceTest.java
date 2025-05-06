package com.lloydsbanking.salsa.opaloans.service;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.downstream.pam.service.CreatePamService;
import com.lloydsbanking.salsa.downstream.pam.service.RetrievePamService;
import com.lloydsbanking.salsa.header.gmo.RequestToResponseHeaderConverter;
import com.lloydsbanking.salsa.offer.OfferException;
import com.lloydsbanking.salsa.opaloans.logging.OpaloansLogService;
import com.lloydsbanking.salsa.opaloans.service.downstream.EligibilityService;
import com.lloydsbanking.salsa.opaloans.service.downstream.EligibleProductsRetriever;
import com.lloydsbanking.salsa.opaloans.service.identify.IdentifyService;
import com.lloydsbanking.salsa.opaloans.service.utility.ExceptionHelper;
import com.lloydsbanking.salsa.opaloans.service.validate.RequestValidatorAndInitializer;
import lib_sim_bo.businessobjects.CustomerScore;
import lib_sim_bo.businessobjects.FinanceServiceArrangement;
import lib_sim_bo.businessobjects.Product;
import lib_sim_bo.businessobjects.ProductArrangement;
import lib_sim_gmo.messages.RequestHeader;
import lib_sim_gmo.messages.ResponseHeader;
import lib_sim_salesprocessmanagement.messages.OfferProductArrangementRequest;
import lib_sim_salesprocessmanagement.messages.OfferProductArrangementResponse;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@Category(UnitTest.class)
public class OpaLoansServiceTest {
    private OpaLoansService opaLoansService;
    private TestDataHelper testDataHelper;

    @Before
    public void setUp() {
        opaLoansService = new OpaLoansService();
        testDataHelper = new TestDataHelper();

        opaLoansService.opaloansLogService = mock(OpaloansLogService.class);
        opaLoansService.validatorAndInitializer = mock(RequestValidatorAndInitializer.class);
        opaLoansService.responseHeaderConverter = new RequestToResponseHeaderConverter();
        opaLoansService.exceptionHelper = mock(ExceptionHelper.class);
        opaLoansService.eligibleProductsRetriever = mock(EligibleProductsRetriever.class);
        opaLoansService.eligibilityService = mock(EligibilityService.class);
        opaLoansService.identifyService = mock(IdentifyService.class);
        opaLoansService.createPamService = mock(CreatePamService.class);
        opaLoansService.retrievePamService = mock(RetrievePamService.class);
    }

    @Test
    public void testOfferProductArrangementLoansWithoutRelatedApplicationIdAndBFPOAddressNotTrueAndCustomerExists() throws Exception {
        OfferProductArrangementRequest request = testDataHelper.generateOfferProductArrangementLoansRequest("IBL");
        request.getProductArrangement().setRelatedApplicationId(null);
        request.getProductArrangement().getPrimaryInvolvedParty().setCustomerIdentifier("456662112");
        request.getProductArrangement().getPrimaryInvolvedParty().setCustomerNumber("11024650060200");
        request.getProductArrangement().getOfferedProducts().add(new Product());
        request.getProductArrangement().getPrimaryInvolvedParty().getCustomerScore().add(new CustomerScore());

        ProductArrangement productArrangement = request.getProductArrangement();
        productArrangement.getAffiliatedetails().addAll(testDataHelper.createAffiliateDetailsList());

        when(opaLoansService.identifyService.identifyInvolvedPartyDetails(any(RequestHeader.class), any(FinanceServiceArrangement.class))).thenReturn(true);
        when(opaLoansService.eligibilityService.getCustomerEligibilityStatus(any(RequestHeader.class), any(FinanceServiceArrangement.class))).thenReturn(true);
        when(opaLoansService.retrievePamService.retrievePendingArrangement(any(String.class), any(String.class), any(List.class))).thenReturn(productArrangement);

        OfferProductArrangementResponse response = opaLoansService.offerProductArrangement(request);

        verify(opaLoansService.opaloansLogService).initialiseContext(request.getHeader());
        verify(opaLoansService.validatorAndInitializer).initialiseVariables(request, response);
        verify(opaLoansService.eligibleProductsRetriever).fetchEligibleLoanProducts(any(RequestHeader.class), any(FinanceServiceArrangement.class));
        verify(opaLoansService.createPamService).createPendingArrangement(any(FinanceServiceArrangement.class));
        verify(opaLoansService.exceptionHelper, never()).setResponseHeaderAndThrowException(any(OfferException.class), any(ResponseHeader.class));

        assertEquals(1, response.getProductArrangement().getOfferedProducts().size());
        assertNull(response.getProductArrangement().getReasonCode());
        assertEquals(request.getProductArrangement().getPrimaryInvolvedParty(), response.getProductArrangement().getPrimaryInvolvedParty());
        assertEquals("11024650060200", response.getProductArrangement().getPrimaryInvolvedParty().getCustomerNumber());
        assertEquals(request.getProductArrangement().getApplicationType(), response.getProductArrangement().getApplicationType());
        assertEquals(request.getProductArrangement().getArrangementType(), response.getProductArrangement().getArrangementType());
        assertEquals(request.getProductArrangement().getArrangementId(), response.getProductArrangement().getArrangementId());
        assertEquals(request.getProductArrangement().getAffiliatedetails(), response.getProductArrangement().getAffiliatedetails());
        assertFalse(response.getProductArrangement().getConditions().isEmpty());
        assertEquals("ELIGIBILITY", response.getProductArrangement().getConditions().get(0).getName());
        assertEquals("Y", response.getProductArrangement().getConditions().get(0).getResult());
        assertNotEquals("000", response.getProductArrangement().getPrimaryInvolvedParty().getIsPlayedBy().getGender());
        assertEquals(null, response.getProductArrangement().getPrimaryInvolvedParty().getIndividualIdentifier());
        assertEquals(request.getProductArrangement().getPrimaryInvolvedParty().getCustomerIdentifier(), response.getProductArrangement().getPrimaryInvolvedParty().getCustomerIdentifier());
    }

    @Test
    public void testOfferProductArrangementLoansWithRelatedApplicationId() throws Exception {
        OfferProductArrangementRequest request = testDataHelper.generateOfferProductArrangementLoansRequest("IBL");
        request.getProductArrangement().setRelatedApplicationId("1");

        ProductArrangement productArrangement = request.getProductArrangement();
        productArrangement.getAffiliatedetails().addAll(testDataHelper.createAffiliateDetailsList());

        when(opaLoansService.retrievePamService.retrievePendingArrangement(any(String.class), any(String.class), any(List.class))).thenReturn(productArrangement);

        OfferProductArrangementResponse response = opaLoansService.offerProductArrangement(request);

        verify(opaLoansService.opaloansLogService).initialiseContext(request.getHeader());
        verify(opaLoansService.validatorAndInitializer).initialiseVariables(request, response);
        verify(opaLoansService.identifyService, never()).identifyInvolvedPartyDetails(any(RequestHeader.class), any(FinanceServiceArrangement.class));
        verify(opaLoansService.eligibleProductsRetriever, never()).fetchEligibleLoanProducts(any(RequestHeader.class), any(FinanceServiceArrangement.class));
        verify(opaLoansService.eligibilityService, never()).getCustomerEligibilityStatus(any(RequestHeader.class), any(FinanceServiceArrangement.class));
        verify(opaLoansService.createPamService).createPendingArrangement(any(FinanceServiceArrangement.class));
        verify(opaLoansService.exceptionHelper, never()).setResponseHeaderAndThrowException(any(OfferException.class), any(ResponseHeader.class));

        assertEquals(0, response.getProductArrangement().getOfferedProducts().size());
        assertNull(response.getProductArrangement().getReasonCode());
        assertEquals(request.getProductArrangement().getApplicationType(), response.getProductArrangement().getApplicationType());
        assertEquals(request.getProductArrangement().getArrangementType(), response.getProductArrangement().getArrangementType());
        assertEquals(request.getProductArrangement().getArrangementId(), response.getProductArrangement().getArrangementId());
        assertEquals(request.getProductArrangement().getAffiliatedetails(), response.getProductArrangement().getAffiliatedetails());
        assertEquals(null, response.getProductArrangement().getPrimaryInvolvedParty().getIsPlayedBy());
        assertEquals(null, response.getProductArrangement().getPrimaryInvolvedParty().getIndividualIdentifier());
        assertEquals(request.getProductArrangement().getPrimaryInvolvedParty().getCustomerIdentifier(), response.getProductArrangement().getPrimaryInvolvedParty().getCustomerIdentifier());
    }

    @Test
    public void testOfferProductArrangementLoansWithoutRelatedApplicationIdAndBFPOAddressNotTrueAndCustomerNotExists() throws Exception {
        OfferProductArrangementRequest request = testDataHelper.generateOfferProductArrangementLoansRequest("LTB");
        request.getProductArrangement().setRelatedApplicationId(null);
        request.getProductArrangement().getPrimaryInvolvedParty().setCustomerIdentifier("456662112");
        request.getProductArrangement().getPrimaryInvolvedParty().setCustomerNumber("11024650060200");
        request.getProductArrangement().getOfferedProducts().add(new Product());
        request.getProductArrangement().getPrimaryInvolvedParty().getCustomerScore().add(new CustomerScore());

        when(opaLoansService.identifyService.identifyInvolvedPartyDetails(any(RequestHeader.class), any(FinanceServiceArrangement.class))).thenReturn(false);

        OfferProductArrangementResponse response = opaLoansService.offerProductArrangement(request);

        verify(opaLoansService.opaloansLogService).initialiseContext(request.getHeader());
        verify(opaLoansService.validatorAndInitializer).initialiseVariables(request, response);
        verify(opaLoansService.eligibleProductsRetriever, never()).fetchEligibleLoanProducts(any(RequestHeader.class), any(FinanceServiceArrangement.class));
        verify(opaLoansService.eligibilityService, never()).getCustomerEligibilityStatus(any(RequestHeader.class), any(FinanceServiceArrangement.class));
        verify(opaLoansService.identifyService).identifyInvolvedPartyDetails(any(RequestHeader.class), any(FinanceServiceArrangement.class));

        assertEquals(0, response.getProductArrangement().getOfferedProducts().size());
        assertNull(response.getProductArrangement().getReasonCode());
        assertEquals(request.getProductArrangement().getApplicationType(), response.getProductArrangement().getApplicationType());
        assertEquals(request.getProductArrangement().getArrangementType(), response.getProductArrangement().getArrangementType());
        assertNull(response.getProductArrangement().getArrangementId());
        assertTrue(response.getProductArrangement().getAffiliatedetails().isEmpty());
        assertFalse(response.getProductArrangement().getConditions().isEmpty());
        assertEquals("ELIGIBILITY", response.getProductArrangement().getConditions().get(0).getName());
        assertEquals("N", response.getProductArrangement().getConditions().get(0).getResult());
        assertEquals(null, response.getProductArrangement().getPrimaryInvolvedParty().getIndividualIdentifier());
    }
}

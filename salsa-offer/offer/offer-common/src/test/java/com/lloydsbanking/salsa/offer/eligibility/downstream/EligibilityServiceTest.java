package com.lloydsbanking.salsa.offer.eligibility.downstream;

import com.lloydsbanking.salsa.UnitTest;
import com.lloydsbanking.salsa.header.gmo.HeaderRetriever;
import com.lloydsbanking.salsa.offer.ApplicantType;
import com.lloydsbanking.salsa.offer.OfferException;
import com.lloydsbanking.salsa.offer.TestDataHelper;
import com.lloydsbanking.salsa.offer.downstream.EligibilityRetriever;
import com.lloydsbanking.salsa.offer.eligibility.convert.OfferToEligibilityRequestConverter;
import com.lloydsbanking.salsa.offer.pam.LookupDataRetriever;
import lib_sim_bo.businessobjects.*;
import lib_sim_gmo.exception.DataNotAvailableErrorMsg;
import lib_sim_gmo.exception.InternalServiceErrorMsg;
import lib_sim_gmo.exception.ResourceNotAvailableErrorMsg;
import lib_sim_salesprocessmanagement.messages.DetermineEligibleCustomerInstructionsRequest;
import lib_sim_salesprocessmanagement.messages.DetermineEligibleCustomerInstructionsResponse;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Category(UnitTest.class)
public class EligibilityServiceTest {

    private EligibilityService eligibilityService;
    private TestDataHelper dataHelper;

    @Before
    public void setUp() {
        eligibilityService = new EligibilityService();
        eligibilityService.eligibilityRetriever = mock(EligibilityRetriever.class);
        eligibilityService.headerRetriever = new HeaderRetriever();
        eligibilityService.offerLookupDataRetriever = mock(LookupDataRetriever.class);
        eligibilityService.offerToEligibilityRequestConverter = new OfferToEligibilityRequestConverter();
        dataHelper = new TestDataHelper();
    }

    @Test
    public void testDetermineEligibilityGuardian() throws DataNotAvailableErrorMsg, ResourceNotAvailableErrorMsg, InternalServiceErrorMsg, OfferException {
        List<ProductArrangement> responseExistingProductArrangement = new ArrayList<>();
        List<Product> responseExistingProducts = new ArrayList<>();
        DepositArrangement depositArrangement = dataHelper.createDepositArrangement();
        depositArrangement.getPrimaryInvolvedParty().setApplicantType(ApplicantType.GUARDIAN.getValue());
        assertTrue(eligibilityService.determineEligibility(depositArrangement, responseExistingProductArrangement, responseExistingProducts, dataHelper.createOpaccRequestHeader("LTB"), true));
    }

    @Test
    public void testDetermineEligibilityDependentIsBFPOTrue() throws DataNotAvailableErrorMsg, ResourceNotAvailableErrorMsg, InternalServiceErrorMsg, OfferException {
        List<ProductArrangement> responseExistingProductArrangement = new ArrayList<>();
        List<Product> responseExistingProducts = new ArrayList<>();
        DepositArrangement depositArrangement = dataHelper.createDepositArrangement();
        depositArrangement.getPrimaryInvolvedParty().setApplicantType(ApplicantType.DEPENDENT.getValue());
        DetermineEligibleCustomerInstructionsResponse response = new DetermineEligibleCustomerInstructionsResponse();
        when(eligibilityService.eligibilityRetriever.callEligibilityService(any(DetermineEligibleCustomerInstructionsRequest.class))).thenReturn(response);
        assertFalse(eligibilityService.determineEligibility(depositArrangement, responseExistingProductArrangement, responseExistingProducts, dataHelper.createOpaccRequestHeader("LTB"), true));
    }

    @Test
    public void testDetermineEligibilityDependentIsBFPOFalse() throws DataNotAvailableErrorMsg, ResourceNotAvailableErrorMsg, InternalServiceErrorMsg, OfferException {
        List<ProductArrangement> responseExistingProductArrangement = new ArrayList<>();
        List<Product> responseExistingProducts = new ArrayList<>();
        DepositArrangement depositArrangement = dataHelper.createDepositArrangement();
        depositArrangement.getPrimaryInvolvedParty().setApplicantType(ApplicantType.DEPENDENT.getValue());
        DetermineEligibleCustomerInstructionsResponse response = new DetermineEligibleCustomerInstructionsResponse();
        when(eligibilityService.eligibilityRetriever.callEligibilityService(any(DetermineEligibleCustomerInstructionsRequest.class))).thenReturn(response);
        assertFalse(eligibilityService.determineEligibility(depositArrangement, responseExistingProductArrangement, responseExistingProducts, dataHelper.createOpaccRequestHeader("LTB"), false));
    }

    @Test(expected = OfferException.class)
    public void testDetermineEligibilityRetrieverThrowsDataNotAvailableError() throws DataNotAvailableErrorMsg, ResourceNotAvailableErrorMsg, InternalServiceErrorMsg, OfferException {
        List<ProductArrangement> responseExistingProductArrangement = new ArrayList<>();
        List<Product> responseExistingProducts = new ArrayList<>();
        DepositArrangement depositArrangement = dataHelper.createDepositArrangement();
        depositArrangement.getPrimaryInvolvedParty().setApplicantType(ApplicantType.DEPENDENT.getValue());
        DetermineEligibleCustomerInstructionsResponse response = new DetermineEligibleCustomerInstructionsResponse();
        when(eligibilityService.eligibilityRetriever.callEligibilityService(any(DetermineEligibleCustomerInstructionsRequest.class))).thenThrow(DataNotAvailableErrorMsg.class);
        eligibilityService.determineEligibility(depositArrangement, responseExistingProductArrangement, responseExistingProducts, dataHelper.createOpaccRequestHeader("LTB"), true);
    }
    @Test(expected = OfferException.class)
    public void testDetermineEligibilityRetrieverThrowsInternalServiceError() throws DataNotAvailableErrorMsg, ResourceNotAvailableErrorMsg, InternalServiceErrorMsg, OfferException {
        List<ProductArrangement> responseExistingProductArrangement = new ArrayList<>();
        List<Product> responseExistingProducts = new ArrayList<>();
        DepositArrangement depositArrangement = dataHelper.createDepositArrangement();
        depositArrangement.getPrimaryInvolvedParty().setApplicantType(ApplicantType.DEPENDENT.getValue());
        DetermineEligibleCustomerInstructionsResponse response = new DetermineEligibleCustomerInstructionsResponse();
        when(eligibilityService.eligibilityRetriever.callEligibilityService(any(DetermineEligibleCustomerInstructionsRequest.class))).thenThrow(InternalServiceErrorMsg.class);
        eligibilityService.determineEligibility(depositArrangement, responseExistingProductArrangement, responseExistingProducts, dataHelper.createOpaccRequestHeader("LTB"), true);
    }
    @Test(expected = OfferException.class)
    public void testDetermineEligibilityRetrieverThrowsResourcenotAvailblError() throws DataNotAvailableErrorMsg, ResourceNotAvailableErrorMsg, InternalServiceErrorMsg, OfferException {
        List<ProductArrangement> responseExistingProductArrangement = new ArrayList<>();
        List<Product> responseExistingProducts = new ArrayList<>();
        DepositArrangement depositArrangement = dataHelper.createDepositArrangement();
        depositArrangement.getPrimaryInvolvedParty().setApplicantType(ApplicantType.DEPENDENT.getValue());
        DetermineEligibleCustomerInstructionsResponse response = new DetermineEligibleCustomerInstructionsResponse();
        when(eligibilityService.eligibilityRetriever.callEligibilityService(any(DetermineEligibleCustomerInstructionsRequest.class))).thenThrow(ResourceNotAvailableErrorMsg.class);
        eligibilityService.determineEligibility(depositArrangement, responseExistingProductArrangement, responseExistingProducts, dataHelper.createOpaccRequestHeader("LTB"), true);
    }

    @Test
    public void testDetermineEligibilityIsTrue() throws DataNotAvailableErrorMsg, ResourceNotAvailableErrorMsg, InternalServiceErrorMsg, OfferException {
        List<ProductArrangement> responseExistingProductArrangement = new ArrayList<>();
        List<Product> responseExistingProducts = new ArrayList<>();
        DepositArrangement depositArrangement = dataHelper.createDepositArrangement();
        depositArrangement.getPrimaryInvolvedParty().setApplicantType(ApplicantType.DEPENDENT.getValue());

        DetermineEligibleCustomerInstructionsResponse response = new DetermineEligibleCustomerInstructionsResponse();
        response.getProductEligibilityDetails().add(new ProductEligibilityDetails());
        response.getProductEligibilityDetails().get(0).getProduct().add(new Product());
        response.getProductEligibilityDetails().get(0).getProduct().get(0).setProductIdentifier("124");
        response.getProductEligibilityDetails().get(0).getProduct().get(0).setInstructionDetails(new InstructionDetails());
        response.getProductEligibilityDetails().get(0).getProduct().get(0).getInstructionDetails().setInstructionMnemonic("P_CLUB");
        response.getProductEligibilityDetails().get(0).setIsEligible("True");

        when(eligibilityService.eligibilityRetriever.callEligibilityService(any(DetermineEligibleCustomerInstructionsRequest.class))).thenReturn(response);
        assertTrue(eligibilityService.determineEligibility(depositArrangement, responseExistingProductArrangement, responseExistingProducts, dataHelper.createOpaccRequestHeader("LTB"), true));
    }

    @Test
    public void testDetermineEligibilityIsFalse() throws DataNotAvailableErrorMsg, ResourceNotAvailableErrorMsg, InternalServiceErrorMsg, OfferException {
        List<ProductArrangement> responseExistingProductArrangement = new ArrayList<>();
        List<Product> responseExistingProducts = new ArrayList<>();
        DepositArrangement depositArrangement = dataHelper.createDepositArrangement();
        depositArrangement.getPrimaryInvolvedParty().setApplicantType(ApplicantType.DEPENDENT.getValue());

        DetermineEligibleCustomerInstructionsResponse response = new DetermineEligibleCustomerInstructionsResponse();
        response.getProductEligibilityDetails().add(new ProductEligibilityDetails());
        response.getProductEligibilityDetails().get(0).getProduct().add(new Product());
        response.getProductEligibilityDetails().get(0).getProduct().get(0).setProductIdentifier("124");
        response.getProductEligibilityDetails().get(0).getProduct().get(0).setInstructionDetails(new InstructionDetails());
        response.getProductEligibilityDetails().get(0).getProduct().get(0).getInstructionDetails().setInstructionMnemonic("P_CLUB");
        response.getProductEligibilityDetails().get(0).setIsEligible("False");

        when(eligibilityService.eligibilityRetriever.callEligibilityService(any(DetermineEligibleCustomerInstructionsRequest.class))).thenReturn(response);
        assertFalse(eligibilityService.determineEligibility(depositArrangement, responseExistingProductArrangement, responseExistingProducts, dataHelper.createOpaccRequestHeader("LTB"), true));
        assertEquals("false",depositArrangement.getAssociatedProduct().getEligibilityDetails().getIsEligible());
    }

    @Test
    public void testDetermineEligibilityDependentIsBFPONotPresent() throws DataNotAvailableErrorMsg, ResourceNotAvailableErrorMsg, InternalServiceErrorMsg, OfferException {
        List<ProductArrangement> responseExistingProductArrangement = new ArrayList<>();
        List<Product> responseExistingProducts = new ArrayList<>();
        DepositArrangement depositArrangement = dataHelper.createDepositArrangement();
        depositArrangement.getPrimaryInvolvedParty().setApplicantType(ApplicantType.DEPENDENT.getValue());
        depositArrangement.getExistingProducts().add(new Product());
        depositArrangement.getExistingProducts().get(0).setBrandName("brandName");
        depositArrangement.getExistingProducts().get(0).setProductIdentifier("1");
        depositArrangement.getExistingProducts().get(0).getExternalSystemProductIdentifier().add(new ExtSysProdIdentifier());
        depositArrangement.getExistingProducts().get(0).getExternalSystemProductIdentifier().get(0).setProductIdentifier("123");
        depositArrangement.getExistingProducts().get(0).getExternalSystemProductIdentifier().get(0).setSystemCode("298");
        depositArrangement.getExistingProducts().get(0).getProductoffer().add(new ProductOffer());
        DetermineEligibleCustomerInstructionsResponse response = new DetermineEligibleCustomerInstructionsResponse();
        when(eligibilityService.eligibilityRetriever.callEligibilityService(any(DetermineEligibleCustomerInstructionsRequest.class))).thenReturn(response);
        assertFalse(eligibilityService.determineEligibility(depositArrangement, responseExistingProductArrangement, responseExistingProducts, dataHelper.createOpaccRequestHeader("LTB"), false));
    }


}

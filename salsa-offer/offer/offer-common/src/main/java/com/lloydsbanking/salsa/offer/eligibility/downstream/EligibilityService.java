package com.lloydsbanking.salsa.offer.eligibility.downstream;

import com.lloydsbanking.salsa.header.gmo.HeaderRetriever;
import com.lloydsbanking.salsa.offer.ApplicantType;
import com.lloydsbanking.salsa.offer.OfferException;
import com.lloydsbanking.salsa.offer.downstream.EligibilityRetriever;
import com.lloydsbanking.salsa.offer.eligibility.convert.OfferToEligibilityRequestConverter;
import com.lloydsbanking.salsa.offer.pam.LookupDataRetriever;
import lib_sim_bo.businessobjects.*;
import lib_sim_gmo.exception.DataNotAvailableErrorMsg;
import lib_sim_gmo.exception.InternalServiceErrorMsg;
import lib_sim_gmo.exception.ResourceNotAvailableErrorMsg;
import lib_sim_gmo.messages.RequestHeader;
import lib_sim_salesprocessmanagement.messages.DetermineEligibleCustomerInstructionsRequest;
import lib_sim_salesprocessmanagement.messages.DetermineEligibleCustomerInstructionsResponse;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class EligibilityService {

    private static final Logger LOGGER = Logger.getLogger(EligibilityService.class);
    private static final String TRUE = "true";
    private static final String FALSE = "false";

    @Autowired(required = false)
    EligibilityRetriever eligibilityRetriever;
    @Autowired
    HeaderRetriever headerRetriever;
    @Autowired
    LookupDataRetriever offerLookupDataRetriever;
    @Autowired
    OfferToEligibilityRequestConverter offerToEligibilityRequestConverter;

    public boolean determineEligibility(ProductArrangement requestProductArrangement, List<ProductArrangement> responseExistingProductArrangements, List<Product> responseExistingProducts, RequestHeader requestHeader, boolean isBFPOIndicatorPresent) throws OfferException {
        LOGGER.info("Calling determineEligibleCustomerInstructions service to determine eligibility of customer");
        if (ApplicantType.GUARDIAN.getValue().equals(requestProductArrangement.getPrimaryInvolvedParty().getApplicantType())) {
            return true;
        } else {
            String internalUserIdentifier = requestProductArrangement.getPrimaryInvolvedParty().getInternalUserIdentifier();
            DetermineEligibleCustomerInstructionsRequest eligibilityRequest = offerToEligibilityRequestConverter.convertOfferToEligibilityRequest(requestProductArrangement, requestHeader, isBFPOIndicatorPresent);
            DetermineEligibleCustomerInstructionsResponse eligibilityResponse = callEligibilityWz(eligibilityRequest);
            requestProductArrangement.getPrimaryInvolvedParty().setInternalUserIdentifier(internalUserIdentifier);
            String candidateInstructionMnemonic = requestProductArrangement.getAssociatedProduct().getInstructionDetails() != null ? requestProductArrangement.getAssociatedProduct().getInstructionDetails().getInstructionMnemonic() : null;
            return isEligible(requestProductArrangement, responseExistingProductArrangements, eligibilityRequest.getExistingProductArrangments(), eligibilityResponse.getProductEligibilityDetails(), candidateInstructionMnemonic);
        }
    }

    private DetermineEligibleCustomerInstructionsResponse callEligibilityWz(DetermineEligibleCustomerInstructionsRequest eligibilityRequest) throws OfferException {
        DetermineEligibleCustomerInstructionsResponse eligibilityResponse;
        try {
            eligibilityResponse = eligibilityRetriever.callEligibilityService(eligibilityRequest);
        } catch (DataNotAvailableErrorMsg | InternalServiceErrorMsg | ResourceNotAvailableErrorMsg errorMsg) {
            throw new OfferException(errorMsg);
        }
        return eligibilityResponse;
    }

    private boolean isEligible(ProductArrangement requestProductArrangement, List<ProductArrangement> responseExistingProductArrangements, List<ProductArrangement> existingProdArrangements, List<ProductEligibilityDetails> productEligibilityDetailsList, String candidateInstructionMnemonic) {
        boolean isEligible = false;
        for (ProductEligibilityDetails productEligibilityDetail : productEligibilityDetailsList) {
            String instructionMnemonic = !productEligibilityDetail.getProduct().isEmpty() ?
                    (productEligibilityDetail.getProduct().get(0).getInstructionDetails() != null ? productEligibilityDetail.getProduct().get(0).getInstructionDetails().getInstructionMnemonic() : null) : null;
            if (instructionMnemonic != null) {
                if (instructionMnemonic.equalsIgnoreCase(candidateInstructionMnemonic)) {
                    if (TRUE.equalsIgnoreCase(productEligibilityDetail.getIsEligible())) {
                        isEligible = true;
                        responseExistingProductArrangements.addAll(existingProdArrangements);
                    } else {
                        isEligible = false;
                        ProductEligibilityDetails eligibilityDetails = new ProductEligibilityDetails();
                        eligibilityDetails.setIsEligible(FALSE);
                        eligibilityDetails.getDeclineReasons().addAll(productEligibilityDetail.getDeclineReasons());
                        requestProductArrangement.getAssociatedProduct().setEligibilityDetails(eligibilityDetails);
                    }
                }
            }
        }

        return isEligible;
    }
}
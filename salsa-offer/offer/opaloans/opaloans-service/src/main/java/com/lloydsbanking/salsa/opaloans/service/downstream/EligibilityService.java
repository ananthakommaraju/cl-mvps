package com.lloydsbanking.salsa.opaloans.service.downstream;

import com.lloydsbanking.salsa.downstream.switches.SwitchService;
import com.lloydsbanking.salsa.offer.OfferException;
import com.lloydsbanking.salsa.offer.downstream.EligibilityRetriever;
import com.lloydsbanking.salsa.opaloans.ReasonCodes;
import lib_sim_bo.businessobjects.*;
import lib_sim_gmo.exception.DataNotAvailableErrorMsg;
import lib_sim_gmo.exception.ExternalServiceErrorMsg;
import lib_sim_gmo.exception.InternalServiceErrorMsg;
import lib_sim_gmo.exception.ResourceNotAvailableErrorMsg;
import lib_sim_gmo.messages.RequestHeader;
import lib_sim_salesprocessmanagement.messages.DetermineEligibleCustomerInstructionsRequest;
import lib_sim_salesprocessmanagement.messages.DetermineEligibleCustomerInstructionsResponse;
import org.apache.cxf.common.util.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class EligibilityService {
    private static final Logger LOGGER = Logger.getLogger(EligibilityService.class);
    private static final String SWITCH_ENABLE_LRA = "SW_EnableLRA";
    private static final String IS_CUSTOMER_ELIGIBLE = "true";

    @Autowired
    EligibilityRetriever eligibilityRetriever;
    @Autowired
    ProductArrangementsRetriever productArrangementsRetriever;
    @Autowired
    SwitchService switchClient;

    public boolean getCustomerEligibilityStatus(RequestHeader header, FinanceServiceArrangement productArrangement) throws OfferException {
        Customer primaryInvolvedParty = productArrangement.getPrimaryInvolvedParty();
        boolean isEligible = false;

        if (productArrangement.getOfferedProducts().isEmpty()) {
            setReasonCode(ReasonCodes.NO_ELIGIBLE_LOAN_PRODUCTS.getValue(), ReasonCodes.NO_ELIGIBLE_LOAN_PRODUCTS.getKey(), productArrangement);
            return isEligible;
        }

        if (isLRASwitchEnabled(header.getChannelId())) {
            boolean savedLoanStatus = isSavedLoanWithDuplicateStatus(header, primaryInvolvedParty.getCustomerScore().get(0), primaryInvolvedParty.getCustomerIdentifier(),
                    primaryInvolvedParty.getCidPersID());

            if (savedLoanStatus) {
                setReasonCode(ReasonCodes.SAVED_LOAN_ALREADY_EXISTS.getValue(), ReasonCodes.SAVED_LOAN_ALREADY_EXISTS.getKey(), productArrangement);
            } else {
                isEligible = getEligibilityStatus(header, productArrangement);
            }
        } else {
            isEligible = getEligibilityStatus(header, productArrangement);
        }
        return isEligible;
    }

    private boolean isSavedLoanWithDuplicateStatus(RequestHeader header, CustomerScore customerScore, String customerIdentifier, String cidPersID) throws OfferException {
        try {
            return productArrangementsRetriever.retrieveProductArrangements(header, customerIdentifier, cidPersID, customerScore);
        } catch (ExternalServiceErrorMsg | ResourceNotAvailableErrorMsg errorMsg) {
            throw new OfferException(errorMsg);
        }
    }

    private boolean getEligibilityStatus(RequestHeader header, FinanceServiceArrangement productArrangement) throws OfferException {
        DetermineEligibleCustomerInstructionsRequest eligibilityRequest = createEligibilityRequest(productArrangement, header);
        DetermineEligibleCustomerInstructionsResponse eligibilityResponse = null;
        try {
            eligibilityResponse = eligibilityRetriever.callEligibilityService(eligibilityRequest);
        } catch (DataNotAvailableErrorMsg | InternalServiceErrorMsg | ResourceNotAvailableErrorMsg errorMsg) {
            throw new OfferException(errorMsg);
        }
        boolean isCustomerEligible = false;

        if (null != eligibilityResponse && !CollectionUtils.isEmpty(eligibilityResponse.getProductEligibilityDetails())) {
            if (IS_CUSTOMER_ELIGIBLE.equalsIgnoreCase(eligibilityResponse.getProductEligibilityDetails().get(0).getIsEligible())) {
                LOGGER.info("Customer is Eligible");
                isCustomerEligible = true;
            } else {
                LOGGER.info("Customer is NOT Eligible");
                List<ReasonCode> declineReasons = eligibilityResponse.getProductEligibilityDetails().get(0).getDeclineReasons();
                if (!CollectionUtils.isEmpty(declineReasons)) {
                    setReasonCode(declineReasons.get(0).getCode(), declineReasons.get(0).getDescription(), productArrangement);
                }
            }
        }
        return isCustomerEligible;
    }

    private DetermineEligibleCustomerInstructionsRequest createEligibilityRequest(final FinanceServiceArrangement productArrangement, final RequestHeader header) {
        DetermineEligibleCustomerInstructionsRequest eligibilityRequest = new DetermineEligibleCustomerInstructionsRequest();
        eligibilityRequest.setHeader(header);
        eligibilityRequest.getExistingProductArrangments().add(productArrangement);
        eligibilityRequest.setCustomerDetails(productArrangement.getPrimaryInvolvedParty());
        String instructionMnemonic = productArrangement.getAssociatedProduct().getInstructionDetails() != null ?
                productArrangement.getAssociatedProduct().getInstructionDetails().getInstructionMnemonic() : null;
        eligibilityRequest.getCandidateInstructions().add(instructionMnemonic);
        eligibilityRequest.setArrangementType(productArrangement.getArrangementType());
        return eligibilityRequest;
    }

    private void setReasonCode(final String code, final String description, ProductArrangement productArrangement) {
        if (null == productArrangement.getReasonCode()) {
            productArrangement.setReasonCode(new ReasonCode());
        }
        productArrangement.getReasonCode().setCode(code);
        productArrangement.getReasonCode().setDescription(description);
    }

    private boolean isLRASwitchEnabled(final String channelId) {
        return switchClient.getBrandedSwitchValue(SWITCH_ENABLE_LRA, channelId, false);
    }
}

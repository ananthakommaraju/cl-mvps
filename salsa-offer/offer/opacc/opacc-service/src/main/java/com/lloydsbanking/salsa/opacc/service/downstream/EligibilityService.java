package com.lloydsbanking.salsa.opacc.service.downstream;

import com.lloydsbanking.salsa.downstream.switches.SwitchServiceImpl;
import com.lloydsbanking.salsa.header.gmo.HeaderRetriever;
import com.lloydsbanking.salsa.offer.LegalEntityMapUtility;
import com.lloydsbanking.salsa.offer.OfferException;
import com.lloydsbanking.salsa.offer.ProductEligibilityType;
import com.lloydsbanking.salsa.offer.downstream.EligibilityRetriever;
import com.lloydsbanking.salsa.offer.pam.LookupDataRetriever;
import com.lloydsbanking.salsa.opacc.service.evaluate.ApplicationTypeEvaluator;
import lib_sim_bo.businessobjects.*;
import lib_sim_gmo.exception.DataNotAvailableErrorMsg;
import lib_sim_gmo.exception.InternalServiceErrorMsg;
import lib_sim_gmo.exception.ResourceNotAvailableErrorMsg;
import lib_sim_gmo.messages.RequestHeader;
import lib_sim_salesprocessmanagement.messages.DetermineEligibleCustomerInstructionsRequest;
import lib_sim_salesprocessmanagement.messages.DetermineEligibleCustomerInstructionsResponse;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

public class EligibilityService {
    private static final Logger LOGGER = Logger.getLogger(EligibilityService.class);

    private static final int ACCOUNT_NUMBER_START_INDEX = 6;

    private static final int ACCOUNT_NUMBER_END_INDEX = 14;

    private static final String MULTI_CARD_SWITCH = "SW_EnblMCS";

    @Autowired
    EligibilityRetriever eligibilityRetriever;

    @Autowired
    HeaderRetriever headerRetriever;

    @Autowired
    LookupDataRetriever offerLookupDataRetriever;

    @Autowired
    SwitchServiceImpl switchClient;

    @Autowired
    ApplicationTypeEvaluator applicationTypeEvaluator;

    public String getProductEligibilityTypeCodeForAuthCustomer(ProductArrangement requestFinanceServiceArrangement) throws OfferException {
        LOGGER.info("Entering Get ProductEligibilityTypeCode For Auth Customer");
        String productEligibilityTypeCode = null;
        if (requestFinanceServiceArrangement.getAssociatedProduct() != null && requestFinanceServiceArrangement.getAssociatedProduct().getEligibilityDetails() != null &&
                requestFinanceServiceArrangement.getAssociatedProduct().getEligibilityDetails().getIsEligible() != null) {
            productEligibilityTypeCode = requestFinanceServiceArrangement.getAssociatedProduct().getEligibilityDetails().getIsEligible();
        }
        List<String> applicationTypeAndEligibilityCode;
        try {
            applicationTypeAndEligibilityCode = applicationTypeEvaluator.getApplicationType(requestFinanceServiceArrangement, false, false);
        } catch (InternalServiceErrorMsg | ResourceNotAvailableErrorMsg errorMsg) {
            throw new OfferException(errorMsg);
        }
        requestFinanceServiceArrangement.setApplicationType(applicationTypeAndEligibilityCode.get(0));
        if (applicationTypeAndEligibilityCode.get(1) != null) {
            productEligibilityTypeCode = applicationTypeAndEligibilityCode.get(1);
        }
        LOGGER.info("Exiting Get ProductEligibilityTypeCode For Auth Customer with Application Type: " + requestFinanceServiceArrangement.getApplicationType() + " and ProductEligibilityTypeCode: " + productEligibilityTypeCode);
        return productEligibilityTypeCode;
    }

    public String getProductEligibilityTypeCodeForUnAuthCustomer(ProductArrangement requestFinanceServiceArrangement, RequestHeader requestHeader) throws OfferException {
        LOGGER.info("Entering Get ProductEligibilityTypeCode For UnAuth Customer");
        String productEligibilityTypeCode = null;
        boolean isMultiCardSwitchEnabled;
        isMultiCardSwitchEnabled = isMultiCardSwitchEnabled(requestHeader.getChannelId());
        try {
            if (isMultiCardSwitchEnabled) {
                LOGGER.info("Multi Card Switch is enabled");
                DetermineEligibleCustomerInstructionsRequest eligibilityRequest = convertOfferToEligibilityRequest(requestFinanceServiceArrangement, requestHeader);
                eligibilityRequest.getExistingProductArrangments().addAll(populateProductArrangementForEligibilityService(requestFinanceServiceArrangement));
                LOGGER.info("Calling determineEligibleCustomerInstructions service to determine eligibility of customer");
                DetermineEligibleCustomerInstructionsResponse eligibilityResponse = null;
                eligibilityResponse = eligibilityRetriever.callEligibilityService(eligibilityRequest);
                if (isEligible(eligibilityResponse)) {
                    LOGGER.info("Eligibility is true");
                    List<String> applicationTypeAndEligibilityCode = applicationTypeEvaluator.getApplicationType(requestFinanceServiceArrangement, true, isMultiCardSwitchEnabled);
                    requestFinanceServiceArrangement.setApplicationType(applicationTypeAndEligibilityCode.get(0));
                    productEligibilityTypeCode = applicationTypeAndEligibilityCode.get(1);
                } else {
                    LOGGER.info("Eligibility is false");
                    requestFinanceServiceArrangement.setApplicationType(ProductEligibilityType.INELIGIBLE.getValue());
                }

            } else {
                LOGGER.info("Multi Card Switch is not enabled");
                List<String> applicationTypeAndEligibilityCode = applicationTypeEvaluator.getApplicationType(requestFinanceServiceArrangement, true, isMultiCardSwitchEnabled);
                requestFinanceServiceArrangement.setApplicationType(applicationTypeAndEligibilityCode.get(0));
                productEligibilityTypeCode = applicationTypeAndEligibilityCode.get(1);
            }
        } catch (DataNotAvailableErrorMsg | InternalServiceErrorMsg | ResourceNotAvailableErrorMsg errorMsg) {
            throw new OfferException(errorMsg);
        }
        LOGGER.info("Exiting Get ProductEligibilityTypeCode For UnAuth Customer with Application Type: " + requestFinanceServiceArrangement.getApplicationType() + " and ProductEligibilityTypeCode: " + productEligibilityTypeCode);
        return productEligibilityTypeCode;
    }

    private boolean isEligible(DetermineEligibleCustomerInstructionsResponse eligibilityResponse) {
        if (null != eligibilityResponse && !CollectionUtils.isEmpty(eligibilityResponse.getProductEligibilityDetails()) && eligibilityResponse.getProductEligibilityDetails()
                .get(0)
                .getIsEligible()
                .equals("true")) {
            return true;
        }
        return false;
    }

    private DetermineEligibleCustomerInstructionsRequest convertOfferToEligibilityRequest(ProductArrangement productArrangement, RequestHeader requestHeader) {
        DetermineEligibleCustomerInstructionsRequest eligibilityRequest = new DetermineEligibleCustomerInstructionsRequest();
        eligibilityRequest.setHeader(requestHeader);
        String instructionMnemonic = productArrangement.getAssociatedProduct().getInstructionDetails() != null ? productArrangement.getAssociatedProduct()
                .getInstructionDetails()
                .getInstructionMnemonic() : null;
        eligibilityRequest.setCustomerDetails(productArrangement.getPrimaryInvolvedParty());
        eligibilityRequest.getCandidateInstructions().add(0, instructionMnemonic);
        eligibilityRequest.setArrangementType(productArrangement.getArrangementType());
        eligibilityRequest.setAssociatedProduct(productArrangement.getAssociatedProduct());
        return eligibilityRequest;
    }

    private List<ProductArrangement> populateProductArrangementForEligibilityService(ProductArrangement requestProductArrangement) {
        List<ProductArrangement> existingProductArrangements = new ArrayList<>();
        for (Product productHolding : requestProductArrangement.getExistingProducts()) {
            ProductArrangement productArrangement = new ProductArrangement();
            Product productCopy = createProductCopy(productHolding);

            if (!CollectionUtils.isEmpty(productHolding.getProductoffer())) {
                productArrangement.setArrangementStartDate(productHolding.getProductoffer().get(0).getStartDate());
            }
            Organisation financialInstitution = new Organisation();
            OrganisationUnit organisationUnit = new OrganisationUnit();
            ExtSysProdIdentifier extSysProdId = new ExtSysProdIdentifier();
            if (!CollectionUtils.isEmpty(productHolding.getExternalSystemProductIdentifier())) {
                productCopy.setProductIdentifier(productHolding.getExternalSystemProductIdentifier().get(0).getProductIdentifier());
                extSysProdId.setSystemCode(productHolding.getExternalSystemProductIdentifier().get(0).getSystemCode());
            }
            if (productHolding.getExternalProductHeldIdentifier() != null) {
                productArrangement.setAccountNumber(productHolding.getExternalProductHeldIdentifier().substring(ACCOUNT_NUMBER_START_INDEX, ACCOUNT_NUMBER_END_INDEX));
                organisationUnit.setSortCode(productHolding.getExternalProductHeldIdentifier().substring(0, ACCOUNT_NUMBER_START_INDEX));
            }
            productCopy.getExternalSystemProductIdentifier().add(extSysProdId);
            financialInstitution.getHasOrganisationUnits().add(organisationUnit);
            productCopy.setBrandName(LegalEntityMapUtility.getLegalEntityMap().get(productHolding.getBrandName()));
            productArrangement.setAssociatedProduct(productCopy);
            productArrangement.setFinancialInstitution(financialInstitution);
            existingProductArrangements.add(productArrangement);
        }
        LOGGER.info("Existing Product Arrangements for determineEligibleCustomerInstructions: " + existingProductArrangements.size());
        return existingProductArrangements;
    }

    private Product createProductCopy(Product productHolding) {
        Product product = new Product();

        product.setProductIdentifier(productHolding.getProductIdentifier());
        product.setBrandName(productHolding.getBrandName());
        product.setIPRTypeCode(productHolding.getIPRTypeCode());
        product.setRoleCode(productHolding.getRoleCode());
        product.setStatusCode(productHolding.getStatusCode());
        product.setAmendmentEffectiveDate(productHolding.getAmendmentEffectiveDate());
        if (!CollectionUtils.isEmpty(productHolding.getExternalSystemProductIdentifier())) {
            ExtSysProdIdentifier extSysProdIdentifier = new ExtSysProdIdentifier();
            extSysProdIdentifier.setSystemCode(productHolding.getExternalSystemProductIdentifier().get(0).getSystemCode());
            extSysProdIdentifier.setProductIdentifier(productHolding.getExternalSystemProductIdentifier().get(0).getProductIdentifier());
            product.getExternalSystemProductIdentifier().add(extSysProdIdentifier);
        }
        if (!CollectionUtils.isEmpty(productHolding.getProductoffer())) {
            ProductOffer productOffer = new ProductOffer();
            productOffer.setStartDate(productHolding.getProductoffer().get(0).getStartDate());
            product.getProductoffer().add(productOffer);
        }
        product.setProductName(productHolding.getProductName());
        product.setProductType(productHolding.getProductType());
        product.setExtPartyIdTx(productHolding.getExtPartyIdTx());
        product.setExternalProductHeldIdentifier(productHolding.getExternalProductHeldIdentifier());
        return product;
    }

    private boolean isMultiCardSwitchEnabled(String channel) {
        return switchClient.getBrandedSwitchValue(MULTI_CARD_SWITCH, channel);
    }
}

package com.lloydsbanking.salsa.offer.eligibility.convert;

import com.lloydsbanking.salsa.offer.LegalEntityMapUtility;
import lib_sim_bo.businessobjects.*;
import lib_sim_gmo.messages.RequestHeader;
import lib_sim_salesprocessmanagement.messages.DetermineEligibleCustomerInstructionsRequest;
import org.apache.log4j.Logger;
import org.springframework.util.CollectionUtils;
import org.apache.cxf.common.util.StringUtils;

import java.util.ArrayList;
import java.util.List;


public class OfferToEligibilityRequestConverter {

    private static final Logger LOGGER = Logger.getLogger(OfferToEligibilityRequestConverter.class);
    private static final String EXTERNAL_SYSTEM_CODE_IB = "00010";
    private static final String INTERNAL_USER_IDENTIFIER = "1";
    private static final int ACCOUNT_NUMBER_START_INDEX = 6;
    private static final int ACCOUNT_NUMBER_END_INDEX = 14;



    public DetermineEligibleCustomerInstructionsRequest convertOfferToEligibilityRequest(ProductArrangement productArrangement, RequestHeader requestHeader, boolean isBFPOIndicatorPresent) {
        DetermineEligibleCustomerInstructionsRequest eligibilityRequest = new DetermineEligibleCustomerInstructionsRequest();
        eligibilityRequest.setHeader(requestHeader);
        String instructionMnemonic = productArrangement.getAssociatedProduct().getInstructionDetails() != null ? productArrangement.getAssociatedProduct().getInstructionDetails().getInstructionMnemonic() : null;
        eligibilityRequest.setCustomerDetails(productArrangement.getPrimaryInvolvedParty());
        eligibilityRequest.getCandidateInstructions().add(0, instructionMnemonic);
        eligibilityRequest.setArrangementType(productArrangement.getArrangementType());
        eligibilityRequest.getCustomerDetails().setInternalUserIdentifier(INTERNAL_USER_IDENTIFIER);

        if (!isBFPOIndicatorPresent) {
            eligibilityRequest.getExistingProductArrangments().addAll((populateProductArrangementForEligibilityService(productArrangement)));
        }
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
            if (!StringUtils.isEmpty(productHolding.getExternalProductHeldIdentifier())) {
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

    public DetermineEligibleCustomerInstructionsRequest convertOfferToEligibilityForOfferedProducts(ProductArrangement productArrangement, RequestHeader requestHeader,
                                                                                                    List<Product> products, boolean isBFPOIndicatorPresent) {
        DetermineEligibleCustomerInstructionsRequest eligibilityRequest = convertOfferToEligibilityRequest(productArrangement, requestHeader, isBFPOIndicatorPresent);
        eligibilityRequest.getCandidateInstructions().clear();
        eligibilityRequest.getCandidateInstructions().addAll(getInstructionMnemonicList(products, productArrangement.getAssociatedProduct().getInstructionDetails().getInstructionMnemonic()));
        eligibilityRequest.setArrangementType(productArrangement.getArrangementType());
        return eligibilityRequest;
    }

    private List<String> getInstructionMnemonicList(
            List<Product> products, String requestedProductMnemonic) {
        List<String> instructionMnemonics = new ArrayList<>();
        for (Product product : products) {
            List<ExtSysProdIdentifier> extSysProdIdentifierList = product.getExternalSystemProductIdentifier();
            for (ExtSysProdIdentifier extSysProdIdentifier : extSysProdIdentifierList) {
                if (EXTERNAL_SYSTEM_CODE_IB.equalsIgnoreCase(extSysProdIdentifier.getSystemCode()) &&
                        !requestedProductMnemonic.equalsIgnoreCase(extSysProdIdentifier.getProductIdentifier())) {
                    instructionMnemonics.add(extSysProdIdentifier.getProductIdentifier());
                }
            }
        }
        return instructionMnemonics;
    }
}

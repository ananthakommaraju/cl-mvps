package com.lloydsbanking.salsa.opaloans.service.identify;

import com.lloydsbanking.salsa.constant.CustomerSegment;
import com.lloydsbanking.salsa.downstream.switches.SwitchService;
import com.lloydsbanking.salsa.offer.LegalEntityMapUtility;
import com.lloydsbanking.salsa.offer.OfferException;
import com.lloydsbanking.salsa.offer.identify.convert.F061ToInvolvedPartyDetailsResponseConverter;
import com.lloydsbanking.salsa.offer.identify.downstream.InvolvedPartyRetriever;
import com.lloydsbanking.salsa.offer.identify.downstream.ProductHoldingRetriever;
import com.lloydsbanking.salsa.offer.identify.evaluate.KYCStatusEvaluator;
import com.lloydsbanking.salsa.offer.identify.utility.CustomerUtility;
import com.lloydsbanking.salsa.opaloans.ReasonCodes;
import com.lloydsbanking.salsa.opaloans.service.identify.evaluate.CustomerEvaluator;
import com.lloydsbanking.salsa.opaloans.service.identify.evaluate.ProductEvaluator;
import com.lloydsbanking.salsa.soap.ocis.f061.objects.F061Resp;
import lib_sim_bo.businessobjects.*;
import lib_sim_gmo.exception.ExternalBusinessErrorMsg;
import lib_sim_gmo.exception.ExternalServiceErrorMsg;
import lib_sim_gmo.exception.InternalServiceErrorMsg;
import lib_sim_gmo.exception.ResourceNotAvailableErrorMsg;
import lib_sim_gmo.messages.RequestHeader;
import org.apache.cxf.common.util.CollectionUtils;
import org.apache.cxf.common.util.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

public class IdentifyService {
    private static final Logger LOGGER = Logger.getLogger(IdentifyService.class);
    private static final String SCORE_RESULT_KYC_COMPLIANT = "1001";
    private static final String SCORE_RESULT_NON_KYC_COMPLIANT = "1000";
    private static final String ASSESSMENT_TYPE_KYC_COMPLIANCE = "KYCCompliance";
    private static final String NON_OCIS_CUSTOMER_IDENTIFIER = "0";
    private static final long EXT_SYSTEM_CODE_VISION_PLUS = 13;
    private static final String BRAND_VER = "VER";
    private static final String STP_LOAN_VERDE_SWITCH = "SW_STPLnsVrdTrns";
    private static final String APPENDED_ZEROES_FOR_EXT_PROD_HELD_ID_TX = "00000";
    @Autowired
    CustomerEvaluator customerEvaluator;
    @Autowired
    InvolvedPartyRetriever involvedPartyRetriever;
    @Autowired
    ProductHoldingRetriever productHoldingRetriever;
    @Autowired
    CustomerUtility customerUtility;
    @Autowired
    F061ToInvolvedPartyDetailsResponseConverter f061ToInvolvedPartyDetailsResponseConverter;
    @Autowired
    KYCStatusEvaluator kycStatusEvaluator;
    @Autowired
    SwitchService switchClient;
    @Autowired
    ProductEvaluator productEvaluator;

    public boolean identifyInvolvedPartyDetails(RequestHeader header, ProductArrangement productArrangement) throws OfferException {
        LOGGER.info("Entering identifyInvolvedPartyDetails");
        Customer customer = productArrangement.getPrimaryInvolvedParty();
        boolean isAuthCustomer = false;
        boolean isCustomerExist = false;
        boolean isBirthDate = true;
        List<Product> productHoldings = new ArrayList<>();
        if (!StringUtils.isEmpty(customer.getCustomerIdentifier())) {
            LOGGER.info("Offer: identify: Customer identifier not null in request " + customer.getCustomerIdentifier());
            assignCustomerSegment(customer);
            isAuthCustomer = true;
        } else {
            isBirthDate = customerEvaluator.isBirthDate(header, productArrangement);
        }
        if (!isBirthDate) {
            customer.setCustomerIdentifier(NON_OCIS_CUSTOMER_IDENTIFIER);
        } else if (!StringUtils.isEmpty(customer.getCustomerIdentifier())) {
            productHoldings = getCustomerHoldingsSegmentAndCustomerNumber(header, customer);
            updateCustomerDetailsFromOcis(header, customer, productHoldings);
        } else {
            customer.setCustomerSegment(CustomerSegment.NON_FRANCHISED.getValue());
            customer.setNewCustomerIndicator(true);
            customer.getIsPlayedBy().setIsStaffMember(false);
            isAuthCustomer = false;
        }
        customer.setIsAuthCustomer(isAuthCustomer);
        String customerNumber = assignCustomerNumber(productHoldings);
        if (!StringUtils.isEmpty(customerNumber)) {
            customer.setCustomerNumber(customerNumber);
        }
        if (!StringUtils.isEmpty(customer.getCustomerIdentifier()) && !NON_OCIS_CUSTOMER_IDENTIFIER.equalsIgnoreCase(customer.getCustomerIdentifier())) {
            productArrangement.getConditions().clear();
            productArrangement.setReasonCode(null);
            productArrangement.getExistingProducts().clear();
            productArrangement.getExistingProducts().addAll(productHoldings);
            boolean isAssociatedProductPopulated = setAssociatedProduct(productArrangement, header.getChannelId());
            if (CollectionUtils.isEmpty(productArrangement.getExistingProducts()) && !isAssociatedProductPopulated) {
                productArrangement.setReasonCode(initialiseReasonCode(ReasonCodes.ACCOUNT_INVALID_FOR_LOAN));
            }
            isCustomerExist = true;
        }
        return isCustomerExist;
    }

    private boolean setAssociatedProduct(ProductArrangement productArrangement, String brand) {
        boolean isVerdeSwitchOn = switchClient.getGlobalSwitchValue(STP_LOAN_VERDE_SWITCH, brand, false);
        String sortCode = productArrangement.getPrimaryInvolvedParty().getExistingSortCode();
        String accNo = productArrangement.getPrimaryInvolvedParty().getExistingAccountNumber();
        String capturedSortCodeAndAccNo = sortCode.concat(accNo).concat(APPENDED_ZEROES_FOR_EXT_PROD_HELD_ID_TX);
        InstructionDetails instructionDetails = productArrangement.getAssociatedProduct().getInstructionDetails();
        List<Product> brandSpecificProducts = productEvaluator.getBrandSpecificProducts(productArrangement.getExistingProducts(), brand, isVerdeSwitchOn);
        for (Product product : brandSpecificProducts) {
            if (capturedSortCodeAndAccNo.equals(product.getExternalProductHeldIdentifier())) {
                Product associatedProduct = getProductFromBrandSpecificProduct(product);
                associatedProduct.setBrandName(LegalEntityMapUtility.getLegalEntityMap().get(product.getBrandName()));
                productArrangement.setAssociatedProduct(associatedProduct);
                productArrangement.getAssociatedProduct().setInstructionDetails(instructionDetails);
                if (productEvaluator.isVerdeProduct(product, brand, isVerdeSwitchOn)) {
                    productArrangement.setReasonCode(initialiseReasonCode(ReasonCodes.VERDE_CUSTOMER));
                }
                return true;
            }
        }
        return false;
    }

    private lib_sim_bo.businessobjects.ReasonCode initialiseReasonCode(ReasonCodes reasonCode) {
        lib_sim_bo.businessobjects.ReasonCode reason = new lib_sim_bo.businessobjects.ReasonCode();
        reason.setCode(reasonCode.getValue());
        reason.setDescription(reasonCode.getKey());
        return reason;
    }

    private String assignCustomerNumber(List<Product> products) {
        if (!CollectionUtils.isEmpty(products)) {
            for (Product product : products) {
                long extSystemCode = (!StringUtils.isEmpty(product.getExternalSystemProductIdentifier().get(0).getSystemCode()) ? Long.parseLong(product.getExternalSystemProductIdentifier().get(0).getSystemCode()) : 0);
                if (EXT_SYSTEM_CODE_VISION_PLUS == extSystemCode) {
                    //TODO Only brandSpecificProducts are populated by ProductPartyDataToProductConverter. Verde Products may bot be present.
                    if (!StringUtils.isEmpty(product.getBrandName())) {
                        if (BRAND_VER.equalsIgnoreCase(LegalEntityMapUtility.getLegalEntityMap().get(product.getBrandName()))) {
                            return product.getExtPartyIdTx();
                        }
                    }
                }
            }
        }
        return "";
    }
    private void assignCustomerSegment(Customer customer) {
        if (StringUtils.isEmpty(customer.getCustomerSegment())) {
            customer.setCustomerSegment(CustomerSegment.FRANCHISED.getValue());
        }
    }

    private List<Product> getCustomerHoldingsSegmentAndCustomerNumber(RequestHeader header, Customer customer) throws OfferException {
        customer.setNewCustomerIndicator(false);
        List<Product> productHoldings;
        try {
            productHoldings = productHoldingRetriever.getProductHoldings(header, customer.getCustomerIdentifier());
        } catch (ExternalServiceErrorMsg | ResourceNotAvailableErrorMsg e) {
            throw new OfferException(e);
        }
        customer.setCustomerSegment(customerUtility.getCustomerSegment(productHoldings));
        customer.setCbsCustomerNumber(customerUtility.getCBSCustomerNumber(productHoldings));
        customer.setCustomerNumber(customerUtility.getFDICustomerID(productHoldings));
        return productHoldings;
    }

    private void updateCustomerDetailsFromOcis(RequestHeader header, Customer customer, List<Product> productHoldings) throws OfferException {
        F061Resp f061Resp;
        try {
            f061Resp = involvedPartyRetriever.retrieveInvolvedPartyDetails(header, customer.getCustomerIdentifier());
            f061ToInvolvedPartyDetailsResponseConverter.setInvolvedPartyDetailsResponseForLoans(customer, f061Resp);
        } catch (InternalServiceErrorMsg | ExternalServiceErrorMsg | ExternalBusinessErrorMsg | ResourceNotAvailableErrorMsg e) {
            throw new OfferException(e);
        }
        CustomerScore customerScore = new CustomerScore();
        customerScore.setAssessmentType(ASSESSMENT_TYPE_KYC_COMPLIANCE);
        if (kycStatusEvaluator.isKycCompliant(customer, productHoldings, f061Resp.getPartyEnqData())) {
            customerScore.setScoreResult(SCORE_RESULT_KYC_COMPLIANT);
        } else {
            customerScore.setScoreResult(SCORE_RESULT_NON_KYC_COMPLIANT);
        }
        customer.getCustomerScore().add(0, customerScore);
    }

    private Product getProductFromBrandSpecificProduct(final Product brandSpecificProduct) {
        Product product = new Product();
        product.setProductIdentifier(brandSpecificProduct.getProductIdentifier());
        product.setBrandName(brandSpecificProduct.getBrandName());
        product.setIPRTypeCode(brandSpecificProduct.getIPRTypeCode());
        product.setRoleCode(brandSpecificProduct.getRoleCode());
        product.setStatusCode(brandSpecificProduct.getStatusCode());
        product.setAmendmentEffectiveDate(brandSpecificProduct.getAmendmentEffectiveDate());
        product.getExternalSystemProductIdentifier().addAll(brandSpecificProduct.getExternalSystemProductIdentifier());
        product.getProductoffer().addAll(brandSpecificProduct.getProductoffer());
        product.setProductName(brandSpecificProduct.getProductName());
        product.setProductType(brandSpecificProduct.getProductType());
        product.setExtPartyIdTx(brandSpecificProduct.getExtPartyIdTx());
        product.setExternalProductHeldIdentifier(brandSpecificProduct.getExternalProductHeldIdentifier());
        return product;
    }
}
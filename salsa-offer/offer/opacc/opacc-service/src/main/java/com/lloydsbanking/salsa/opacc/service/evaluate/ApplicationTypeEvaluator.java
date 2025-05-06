package com.lloydsbanking.salsa.opacc.service.evaluate;


import com.lloydsbanking.salsa.downstream.prd.service.AdministerProductSelectionService;
import com.lloydsbanking.salsa.header.gmo.HeaderRetriever;
import com.lloydsbanking.salsa.logging.application.ProductTraceLog;
import com.lloydsbanking.salsa.offer.ProductEligibilityType;
import com.lloydsbanking.salsa.offer.pam.LookupDataRetriever;
import lib_sim_bo.businessobjects.Product;
import lib_sim_bo.businessobjects.ProductArrangement;
import lib_sim_gmo.exception.InternalServiceErrorMsg;
import lib_sim_gmo.exception.ResourceNotAvailableErrorMsg;
import org.apache.cxf.common.util.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

public class ApplicationTypeEvaluator {

    private static final Logger LOGGER = Logger.getLogger(ApplicationTypeEvaluator.class);

    private static final String PRODUCT_ID_CREDIT_CARD = "3";
    private static final String APP_TYPE_CD_SECONDARY = "SECONDARY";
    private static final String PRODUCT_TYPE_CREDIT_CARD = "3";
    @Autowired
    HeaderRetriever headerRetriever;
    @Autowired
    LookupDataRetriever offerLookupDataRetriever;
    @Autowired
    AdministerProductSelectionService administerProductSelectionService;
    @Autowired
    ProductTraceLog productTraceLog;

    public List<String> getApplicationType(ProductArrangement requestProductArrangement, boolean isUnAuth, boolean isMultiCardSwitchEnabled) throws InternalServiceErrorMsg, ResourceNotAvailableErrorMsg {
        String applicationType = null, productEligibilityTypeCode = null;
        List<Product> existingCCProductList = filterCreditCardProducts(requestProductArrangement.getExistingProducts());
        int numberOfCreditCardHoldsOfSameBrand = existingCCProductList.size();
        if (numberOfCreditCardHoldsOfSameBrand == 0) {
            if (!StringUtils.isEmpty(requestProductArrangement.getApplicationType())) {
                applicationType = requestProductArrangement.getApplicationType();
                productEligibilityTypeCode = ProductEligibilityType.NEW.getKey();
            } else {
                applicationType = ProductEligibilityType.NEW.getValue();
                productEligibilityTypeCode = ProductEligibilityType.NEW.getKey();
            }
        } else if (numberOfCreditCardHoldsOfSameBrand >= 1) {
            Product associatedProduct = requestProductArrangement.getAssociatedProduct();
            associatedProduct.setProductType(PRODUCT_TYPE_CREDIT_CARD);
            if (isUnAuth) {
                if (isMultiCardSwitchEnabled) {
                    LOGGER.info(productTraceLog.getProdListTraceEventMessage(existingCCProductList, "Entering Administer Product Selection with existing credit cards product list "));
                    LOGGER.info(productTraceLog.getProductTraceEventMessage(associatedProduct, "Entering Administer Product Selection with Product "));
                    productEligibilityTypeCode = administerProductSelectionService.administerProductSelection(existingCCProductList, associatedProduct, APP_TYPE_CD_SECONDARY);
                    LOGGER.info("Exiting Administer Product Selection with ProductEligibilityType: " + productEligibilityTypeCode);
                    applicationType = ProductEligibilityType.getApplicationType(productEligibilityTypeCode);
                } else {
                    applicationType = ProductEligibilityType.INELIGIBLE.getValue();
                    productEligibilityTypeCode = ProductEligibilityType.INELIGIBLE.getKey();

                }
            } else {
                LOGGER.info(productTraceLog.getProdListTraceEventMessage(existingCCProductList, "Entering Administer Product Selection with existing credit cards product list: "));
                LOGGER.info(productTraceLog.getProductTraceEventMessage(associatedProduct, "Entering Administer Product Selection with Product "));
                productEligibilityTypeCode = administerProductSelectionService.administerProductSelection(existingCCProductList, associatedProduct, APP_TYPE_CD_SECONDARY);
                LOGGER.info("Exiting Administer Product Selection with ProductEligibilityType: " + productEligibilityTypeCode);
                applicationType = ProductEligibilityType.getApplicationType(productEligibilityTypeCode);
            }
        }
        List<String> applicationTypeAndEligibilityCode = new ArrayList<>();
        applicationTypeAndEligibilityCode.add(applicationType);
        applicationTypeAndEligibilityCode.add(productEligibilityTypeCode);
        return applicationTypeAndEligibilityCode;
    }

    private List<Product> filterCreditCardProducts(List<Product> responseExistingProducts) {
        List<Product> creditCardProducts = new ArrayList<>();
        for (Product product : responseExistingProducts) {
            if (PRODUCT_ID_CREDIT_CARD.equals(product.getProductIdentifier())) {
                creditCardProducts.add(product);
            }
        }
        LOGGER.info("Number of Credit Cards customer already holds of same brand: " + creditCardProducts.size());
        return creditCardProducts;
    }
}

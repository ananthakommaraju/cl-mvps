package com.lloydsbanking.salsa.offer.apply.downstream;

import com.lloydsbanking.salsa.downstream.prd.service.AdministerProductSelectionService;
import com.lloydsbanking.salsa.logging.application.ProductTraceLog;
import lib_sim_bo.businessobjects.FinanceServiceArrangement;
import lib_sim_bo.businessobjects.Product;
import lib_sim_gmo.exception.DataNotAvailableErrorMsg;
import lib_sim_gmo.exception.InternalServiceErrorMsg;
import lib_sim_gmo.exception.ResourceNotAvailableErrorMsg;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

public class AdministerProductService {
    private static final Logger LOGGER = Logger.getLogger(AdministerProductService.class);
    private static final String PRODUCT_ELIGIBILITY_TYPE_CO_HOLD = "CO_HOLD";
    private static final String PRODUCT_ID_CREDIT_CARD = "3";
    private static final String PRODUCT_TYPE_CREDIT_CARD = "3";
    private static final String APPLICATION_STATUS_WHEN_NO_DOWNSTREAM_PRODUCT_ELIGIBLE = "INELIGIBLE";
    private static final String APPLICATION_STATUS_DECLINE = "1004";

    @Autowired
    AdministerProductSelectionService administerProductSelectionService;
    @Autowired
    ProductTraceLog productTraceLog;

    public void callAdministerProductSelectionService(FinanceServiceArrangement financeServiceArrangement, String productEligibilityTypeCode) throws InternalServiceErrorMsg, DataNotAvailableErrorMsg, ResourceNotAvailableErrorMsg {
        if (StringUtils.isEmpty(financeServiceArrangement.getRelatedApplicationId()) && !isRequestedProductIdPresentInAsmResponse(financeServiceArrangement)) {
            callAdministerProductSelectionService(productEligibilityTypeCode, financeServiceArrangement);
        }
    }

    private void callAdministerProductSelectionService(String productEligibilityTypeCode, FinanceServiceArrangement financeServiceArrangement) throws InternalServiceErrorMsg, DataNotAvailableErrorMsg, ResourceNotAvailableErrorMsg {

        if (PRODUCT_ELIGIBILITY_TYPE_CO_HOLD.equalsIgnoreCase(productEligibilityTypeCode)
                && financeServiceArrangement.getOfferedProducts() != null && !financeServiceArrangement.getOfferedProducts().isEmpty()) {
            LOGGER.info("ProductEligibilityTypeCode is CO_HOLD");
            List<Product> asmProductList = createAsmProductList(financeServiceArrangement);
            List<Product> existingProducts = filterCreditCardProducts(financeServiceArrangement);
            if (financeServiceArrangement.getOfferedProducts() != null && !financeServiceArrangement.getOfferedProducts().isEmpty()) {
                financeServiceArrangement.getOfferedProducts().clear();
            }

            for (Product product : asmProductList) {
                String productEligibilityType;
                LOGGER.info(productTraceLog.getProdListTraceEventMessage(existingProducts, "Entering Administer Product Selection with existing credit cards product list "));
                LOGGER.info(productTraceLog.getProductTraceEventMessage(product, "Entering Administer Product Selection with ASM Product "));
                productEligibilityType = administerProductSelectionService.administerProductSelection(existingProducts, product, "SECONDARY");
                LOGGER.info("Exiting Administer Product Selection with ProductEligibilityType: " + productEligibilityType);

                if (productEligibilityType != null && productEligibilityType.equalsIgnoreCase(PRODUCT_ELIGIBILITY_TYPE_CO_HOLD)) {
                    financeServiceArrangement.getOfferedProducts().add(product);
                }
            }
            setStatusAndTypeWhenOfferedProductListIsEmpty(financeServiceArrangement);
        }
    }

    private List<Product> createAsmProductList(FinanceServiceArrangement financeServiceArrangement) {
        List<Product> asmProductList = new ArrayList<>();
        for (Product product : financeServiceArrangement.getOfferedProducts()) {
            product.setProductType(PRODUCT_TYPE_CREDIT_CARD);
            asmProductList.add(product);
        }
        return asmProductList;
    }

    private void setStatusAndTypeWhenOfferedProductListIsEmpty(FinanceServiceArrangement financeServiceArrangement) {
        if (financeServiceArrangement.getOfferedProducts() != null && financeServiceArrangement.getOfferedProducts().isEmpty()) {
            financeServiceArrangement.setApplicationStatus(APPLICATION_STATUS_DECLINE);
            financeServiceArrangement.setApplicationType(APPLICATION_STATUS_WHEN_NO_DOWNSTREAM_PRODUCT_ELIGIBLE);
        }
    }


    private List<Product> filterCreditCardProducts(FinanceServiceArrangement financeServiceArrangement) {
        List<Product> creditCardProducts = new ArrayList<>();
        if (!financeServiceArrangement.getExistingProducts().isEmpty()) {
            for (Product product : financeServiceArrangement.getExistingProducts()) {
                if (PRODUCT_ID_CREDIT_CARD.equals(product.getProductIdentifier())) {
                    creditCardProducts.add(product);
                }
            }
        }
        return creditCardProducts;
    }


    private boolean isRequestedProductIdPresentInAsmResponse(FinanceServiceArrangement financeServiceArrangement) {
        if (financeServiceArrangement.getOfferedProducts() != null && !financeServiceArrangement.getOfferedProducts().isEmpty()) {
            for (Product product : financeServiceArrangement.getOfferedProducts()) {
                if (product.getProductIdentifier() != null && financeServiceArrangement.getAssociatedProduct().getProductIdentifier() != null) {
                    if (product.getProductIdentifier().equalsIgnoreCase(financeServiceArrangement.getAssociatedProduct().getProductIdentifier())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}

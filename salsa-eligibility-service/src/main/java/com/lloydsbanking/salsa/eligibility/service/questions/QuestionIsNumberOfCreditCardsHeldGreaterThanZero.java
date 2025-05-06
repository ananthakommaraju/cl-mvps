package com.lloydsbanking.salsa.eligibility.service.questions;

import com.lloydsbanking.salsa.downstream.prd.service.AdministerProductSelectionService;
import com.lloydsbanking.salsa.eligibility.service.utility.EligibilityException;
import com.lloydsbanking.salsa.eligibility.service.utility.ProductArrangementFacade;
import com.lloydsbanking.salsa.eligibility.service.utility.exceptions.ReasonText;
import com.lloydsbanking.salsa.eligibility.service.utility.exceptions.SalsaInternalResourceNotAvailableException;
import com.lloydsbanking.salsa.eligibility.service.utility.exceptions.SalsaInternalServiceException;
import com.lloydsbanking.salsa.logging.application.ProductTraceLog;
import lib_sim_bo.businessobjects.Condition;
import lib_sim_bo.businessobjects.ExtraConditions;
import lib_sim_bo.businessobjects.Product;
import lib_sim_gmo.exception.InternalServiceErrorMsg;
import lib_sim_gmo.exception.ResourceNotAvailableErrorMsg;
import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class QuestionIsNumberOfCreditCardsHeldGreaterThanZero extends AbstractProductListQuestion implements AskQuestion {
    private static final Logger LOGGER = Logger.getLogger(QuestionIsNumberOfCreditCardsHeldGreaterThanZero.class);

    private static final String PRODUCT_TYPE_CC = "3";

    protected AdministerProductSelectionService administerProductSelectionService;

    protected Product associatedProduct;

    protected ExtraConditions extraConditions;

    protected ProductTraceLog productTraceLog;

    private static final String PRODUCT_ELIGIBILITY_TYPE_INELIGIBLE = "INELIGIBLE";

    private static final String PRODUCT_ELIGIBILITY_TYPE_ELIGIBLE = "ELIGIBLE";

    public static QuestionIsNumberOfCreditCardsHeldGreaterThanZero pose() {
        return new QuestionIsNumberOfCreditCardsHeldGreaterThanZero();
    }

    @Override
    public boolean ask() throws EligibilityException {
        int cardHoldings = 0;
        List<Product> ccProductList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(productArrangements)) {
            for (ProductArrangementFacade productArrangement : productArrangements) {
                Product product = productArrangement.getAssociatedProduct();
                if (PRODUCT_TYPE_CC.equalsIgnoreCase(product.getProductType())) {
                    ccProductList.add(product);
                    cardHoldings++;
                }
                switch (cardHoldings) {

                    case 0:
                        return false;
                    case 1:
                        ccProductList.get(0).setProductType("CC");
                        associatedProduct.setProductType("CC");
                        String productEligibilityType = "";
                        try {
                            LOGGER.info(productTraceLog.getProductTraceEventMessage(ccProductList.get(0), "Calling AdministerProductSelectionService for existingProduct: ") + productTraceLog.getProductTraceEventMessage(associatedProduct, " appliedProduct: "));
                            productEligibilityType = administerProductSelectionService.administerProductSelection(ccProductList, associatedProduct, null);
                            LOGGER.info("ProductEligibilityType returned by AdministerProductSelectionService: " + productEligibilityType);
                        }
                        catch (ResourceNotAvailableErrorMsg | InternalServiceErrorMsg errorMsg) {
                            throwException(errorMsg);
                        }
                        return productEligibilityCheckMethod(productEligibilityType);
                    default:
                        return true;
                }
            }
        }
        return false;
    }

    private void throwException(Exception e) throws EligibilityException {
        if (e instanceof ResourceNotAvailableErrorMsg) {
            LOGGER.info("ResourceNotAvailable exception returned from AdministerProductSelection" + e);
            throw new EligibilityException(new SalsaInternalServiceException(e.getMessage(), null, new ReasonText(e.getMessage())));
        }
        if (e instanceof InternalServiceErrorMsg) {
            LOGGER.info("InternalService exception returned from AdministerProductSelection" + e);
            throw new EligibilityException(new SalsaInternalResourceNotAvailableException(e.getMessage()));
        }
    }

    private boolean productEligibilityCheckMethod(String productEligibilityType) {
        if (PRODUCT_ELIGIBILITY_TYPE_INELIGIBLE.equalsIgnoreCase(productEligibilityType)) {
            return true;
        }
        else if (PRODUCT_ELIGIBILITY_TYPE_ELIGIBLE.equalsIgnoreCase(productEligibilityType)) {
            return false;
        }
        else {
            //This rule is expired and its implementation is also not clear in wps so this condition is pending
            Condition condition = new Condition();
            condition.setReasonCode(productEligibilityType);
            extraConditions.getConditions().add(condition);
            return false;
        }
    }

    public QuestionIsNumberOfCreditCardsHeldGreaterThanZero givenExtraCondition(ExtraConditions extraConditions) {
        this.extraConditions = extraConditions;
        return this;
    }

    public QuestionIsNumberOfCreditCardsHeldGreaterThanZero givenAnInstanceAdministerProductSelection(AdministerProductSelectionService administerProductSelectionService) {
        this.administerProductSelectionService = administerProductSelectionService;
        return this;
    }

    public QuestionIsNumberOfCreditCardsHeldGreaterThanZero givenAssociatedProduct(Product associatedProduct) {
        this.associatedProduct = associatedProduct;
        return this;
    }

    public QuestionIsNumberOfCreditCardsHeldGreaterThanZero givenProductTraceLogInstance(ProductTraceLog productTraceLog) {
        this.productTraceLog = productTraceLog;
        return this;
    }
}
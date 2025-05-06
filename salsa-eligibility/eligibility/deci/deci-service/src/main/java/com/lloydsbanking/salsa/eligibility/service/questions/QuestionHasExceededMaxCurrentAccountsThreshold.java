package com.lloydsbanking.salsa.eligibility.service.questions;

import com.lloydsbanking.salsa.eligibility.service.utility.EligibilityException;
import com.lloydsbanking.salsa.eligibility.service.utility.ProductArrangementFacade;
import com.lloydsbanking.salsa.eligibility.service.utility.exceptions.ReasonText;
import com.lloydsbanking.salsa.eligibility.service.utility.exceptions.SalsaInternalServiceException;
import lib_sim_bo.businessobjects.ExtSysProdIdentifier;
import lib_sim_bo.businessobjects.Product;

public class QuestionHasExceededMaxCurrentAccountsThreshold extends AbstractProductListQuestion implements AskQuestion  {

    private static final String PRODUCT_TYPE_CURRENT_ACCOUNT = "1";

    private static final String EXT_SYS_ID_CURRENT_ACCOUNT = "00004";

    public static QuestionHasExceededMaxCurrentAccountsThreshold pose() {
        return new QuestionHasExceededMaxCurrentAccountsThreshold();
    }

    public boolean ask() throws EligibilityException {
        int maxNumOfProducts = Integer.valueOf(threshold).intValue();
        int matches = 0;
        for (ProductArrangementFacade productArrangement : productArrangements) {
            if (null != productArrangement && null != productArrangement.getAssociatedProduct()) {
                if (hasACurrentAccount(productArrangement.getAssociatedProduct())) {
                    matches++;
                    if (matches >= maxNumOfProducts) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    private boolean hasACurrentAccount(Product associatedProduct) throws EligibilityException {
        if (null != associatedProduct.getExternalSystemProductIdentifier()) {
            if (associatedProduct.getExternalSystemProductIdentifier().isEmpty()){
                throw new EligibilityException(new SalsaInternalServiceException("Index out of bound", "", new ReasonText("index=0, size =0")));
            }
            ExtSysProdIdentifier extSysProdIdentifier = associatedProduct.getExternalSystemProductIdentifier().get(0);
            if (null != extSysProdIdentifier) {
                if (PRODUCT_TYPE_CURRENT_ACCOUNT.equals(associatedProduct.getProductType()) && EXT_SYS_ID_CURRENT_ACCOUNT.equals(extSysProdIdentifier.getSystemCode())) {
                    return true;
                }
            }
        }
        return false;
    }

}

package com.lloydsbanking.salsa.eligibility.service.questions;

import com.lloydsbanking.salsa.eligibility.service.utility.EligibilityException;
import com.lloydsbanking.salsa.eligibility.service.utility.ProductArrangementFacade;
import com.lloydsbanking.salsa.eligibility.service.utility.exceptions.ReasonText;
import com.lloydsbanking.salsa.eligibility.service.utility.exceptions.SalsaInternalServiceException;
import lib_sim_bo.businessobjects.ExtSysProdIdentifier;
import lib_sim_bo.businessobjects.Product;

import java.util.List;

public class QuestionIsCurrentAccount extends AbstractProductListQuestion implements AskQuestion {

    private static final String PRODUCT_TYPE_CURRENT_ACCOUNT = "1";

    private static final String EXT_SYS_ID_CURRENT_ACCOUNT = "00004";

    public static QuestionIsCurrentAccount pose() {
        return new QuestionIsCurrentAccount();
    }

    public boolean ask() throws EligibilityException {
        for (ProductArrangementFacade customerArrangement : productArrangements) {
            if (null != customerArrangement) {
                Product associatedProduct = customerArrangement.getAssociatedProduct();
                if (null != associatedProduct && PRODUCT_TYPE_CURRENT_ACCOUNT.equals(associatedProduct.getProductType()) ) {
                    List<ExtSysProdIdentifier> externalSystemProductIdentifier = associatedProduct.getExternalSystemProductIdentifier();
                    if (isExternalIdCurrentAccount(externalSystemProductIdentifier)) {
                        return true;
                    }
                }

            }
        }
        return false;
    }

    private boolean isExternalIdCurrentAccount(List<ExtSysProdIdentifier> externalSystemProductIdentifier) throws EligibilityException {
        if (null != externalSystemProductIdentifier) {
            if (externalSystemProductIdentifier.isEmpty()){
                throw new EligibilityException(new SalsaInternalServiceException("Index out of bound", "", new ReasonText("index=0, size =0")));
            }
            ExtSysProdIdentifier extSysProdIdentifier = externalSystemProductIdentifier.get(0);
            if (null != extSysProdIdentifier) {
                if ( EXT_SYS_ID_CURRENT_ACCOUNT.equals(extSysProdIdentifier.getSystemCode() )) {
                    return true;
                }
            }
        }
        return false;
    }
}

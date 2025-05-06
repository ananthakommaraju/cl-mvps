package com.lloydsbanking.salsa.eligibility.service.questions;

import com.lloydsbanking.salsa.eligibility.service.downstream.MandateAccessDetailsRetriever;
import com.lloydsbanking.salsa.eligibility.service.utility.EligibilityException;
import com.lloydsbanking.salsa.eligibility.service.utility.ProductArrangementFacade;
import com.lloydsbanking.salsa.eligibility.service.utility.exceptions.SalsaExternalBusinessException;
import com.lloydsbanking.salsa.eligibility.service.utility.exceptions.SalsaInternalResourceNotAvailableException;
import lib_sim_bo.businessobjects.ExtSysProdIdentifier;
import org.apache.cxf.common.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class QuestionHasExistingProductWithRelatedEventEnabled extends AbstractProductListQuestion implements AskQuestion {
    public static QuestionHasExistingProductWithRelatedEventEnabled pose() {
        return new QuestionHasExistingProductWithRelatedEventEnabled();
    }

    private static final String DORMANT = "Dormant";

    private boolean isWzRequest = false;

    private MandateAccessDetailsRetriever mandateAccessDetailsRetriever;

    private static final int SYSTEM_CODE_EXISTING_CUSTOMER = 4;

    private static final int SYSTEM_CODE_NEW_CUSTOMER = 1;

    public boolean ask() throws EligibilityException {
        if (productArrangements != null) {
            List<String> relatedEvents=new ArrayList<>();
            if (this.isWzRequest) {
                String accType = getAccountType();
                try {
                    relatedEvents.addAll(mandateAccessDetailsRetriever.getRelatedEvents(requestHeader, accType));
                }
                catch (SalsaInternalResourceNotAvailableException | SalsaExternalBusinessException e) {
                    throw new EligibilityException(e);
                }
            }
            for (ProductArrangementFacade customerArrangement : productArrangements) {
                if (!this.isWzRequest) {
                    relatedEvents.clear();
                    relatedEvents.addAll(customerArrangement.getRelatedEvents());
                }
                String status = customerArrangement.getLifecycleStatus();
                if (relatedEvents.contains(threshold) && !DORMANT.equals(status)) {
                    return true;
                }

            }
        }
        return false;
    }

    public QuestionHasExistingProductWithRelatedEventEnabled givenAWzRequest(boolean isWzRequest) {
        this.isWzRequest = isWzRequest;
        return this;
    }

    public QuestionHasExistingProductWithRelatedEventEnabled givenAMandateAccessDetailsRetrieverInstance(MandateAccessDetailsRetriever mandateAccessDetailsRetriever) {
        this.mandateAccessDetailsRetriever = mandateAccessDetailsRetriever;
        return this;
    }

    private String getAccountType() {
        if (isExternalSystemProductIdentifierListNotEmpty()) {
            ExtSysProdIdentifier extSysProdIdentifier = productArrangements.get(0).getAssociatedProduct().getExternalSystemProductIdentifier().get(0);
            if (null != extSysProdIdentifier && null != extSysProdIdentifier.getSystemCode() && !StringUtils.isEmpty(extSysProdIdentifier.getSystemCode())) {
                int systemCode = Integer.parseInt(extSysProdIdentifier.getSystemCode());
                if (systemCode == SYSTEM_CODE_EXISTING_CUSTOMER) {
                    return "T".concat(extSysProdIdentifier.getProductIdentifier());
                }
                if (systemCode == SYSTEM_CODE_NEW_CUSTOMER) {
                    return "L".concat(extSysProdIdentifier.getProductIdentifier());
                }
            }
        }
        return "";
    }

    private boolean isExternalSystemProductIdentifierListNotEmpty() {
        return isAssociatedProductNotNull()
            && null != productArrangements.get(0).getAssociatedProduct().getExternalSystemProductIdentifier()
            && !productArrangements.get(0).getAssociatedProduct().getExternalSystemProductIdentifier().isEmpty();
    }

    private boolean isAssociatedProductNotNull() {
        return !productArrangements.isEmpty()
            && null != productArrangements.get(0)
            && null != productArrangements.get(0).getAssociatedProduct();
    }
}

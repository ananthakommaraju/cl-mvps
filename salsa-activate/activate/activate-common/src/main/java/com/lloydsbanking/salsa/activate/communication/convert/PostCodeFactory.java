package com.lloydsbanking.salsa.activate.communication.convert;

import lib_sim_bo.businessobjects.Customer;
import lib_sim_bo.businessobjects.PostalAddress;
import lib_sim_bo.businessobjects.ProductArrangement;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

@Component
public class PostCodeFactory {

    private static final String ACCOUNT_TYPE_CURRENT = "CURRENT";
    private static final String POST_CODE_PREFIX = "XXX";
    private static final int POST_CODE_START_INDEX = 3;
    private static final int POST_CODE_END_INDEX = 6;

    public String getMaskedPostcode(ProductArrangement productArrangement) {
        String maskedPostCode = null;
        Customer customer = productArrangement.getGuardianDetails() != null ? productArrangement.getGuardianDetails() : productArrangement.getPrimaryInvolvedParty();
        for (PostalAddress postalAddress : customer.getPostalAddress()) {
            if (ACCOUNT_TYPE_CURRENT.equalsIgnoreCase(postalAddress.getStatusCode())) {
                if (postalAddress.isIsPAFFormat() != null && postalAddress.isIsPAFFormat() && postalAddress.getStructuredAddress() != null) {
                    maskedPostCode = POST_CODE_PREFIX.concat(postalAddress.getStructuredAddress().getPostCodeIn());
                } else if (postalAddress.getUnstructuredAddress() != null && !StringUtils.isEmpty(postalAddress.getUnstructuredAddress().getPostCode()) && postalAddress.getUnstructuredAddress().getPostCode().length() > 5) {
                    maskedPostCode = POST_CODE_PREFIX.concat(postalAddress.getUnstructuredAddress().getPostCode().substring(POST_CODE_START_INDEX, POST_CODE_END_INDEX));
                }
            }
        }
        return maskedPostCode;
    }

    public String getPostCode(ProductArrangement productArrangement) {
        String postCode = null;
        for (PostalAddress postalAddress : productArrangement.getPrimaryInvolvedParty().getPostalAddress()) {
            if (ACCOUNT_TYPE_CURRENT.equalsIgnoreCase(postalAddress.getStatusCode())) {
                if (postalAddress.isIsPAFFormat() != null && postalAddress.isIsPAFFormat() && postalAddress.getStructuredAddress() != null) {
                    postCode = postalAddress.getStructuredAddress().getPostCodeIn();
                } else if (postalAddress.getUnstructuredAddress() != null) {
                    postCode = postalAddress.getUnstructuredAddress().getPostCode();
                }
            }
        }
        return postCode;
    }

}

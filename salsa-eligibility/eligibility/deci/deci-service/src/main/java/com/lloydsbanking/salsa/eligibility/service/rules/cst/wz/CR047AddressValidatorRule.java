package com.lloydsbanking.salsa.eligibility.service.rules.cst.wz;

import com.lloydsbanking.salsa.eligibility.service.rules.common.EligibilityDecision;
import com.lloydsbanking.salsa.eligibility.service.rules.common.RuleDataHolder;
import com.lloydsbanking.salsa.eligibility.service.rules.cst.CSTEligibilityRule;
import com.lloydsbanking.salsa.eligibility.service.utility.constants.DeclineReasons;
import lib_sim_bo.businessobjects.PostalAddress;
import lib_sim_bo.businessobjects.StructuredAddress;
import lib_sim_bo.businessobjects.UnstructuredAddress;
import org.apache.commons.lang.BooleanUtils;
import org.apache.cxf.common.util.CollectionUtils;

import javax.xml.datatype.XMLGregorianCalendar;
import java.util.List;

public class CR047AddressValidatorRule implements CSTEligibilityRule {
    private static final int EMPTY = 0;

    @Override
    public EligibilityDecision evaluate(RuleDataHolder ruleDataHolder, XMLGregorianCalendar birthDate, String sortCode, String customerId) {
        List<PostalAddress> postalAddresses = ruleDataHolder.getCustomerDetails() != null ? ruleDataHolder.getCustomerDetails().getPostalAddress() : null;
        if (!CollectionUtils.isEmpty(postalAddresses)) {
            if (!isValidPostalAddress(postalAddresses)) {
                return new EligibilityDecision(DeclineReasons.CR047_DECLINE_REASON);
            }
        }
        return new EligibilityDecision(true);
    }

    private boolean isValidPostalAddress(List<PostalAddress> postalAddresses) {
        boolean validAddress = false;
        for (PostalAddress postalAddress : postalAddresses) {
            if (BooleanUtils.isTrue(postalAddress.isIsPAFFormat())) {
                StructuredAddress structuredAddress = postalAddress.getStructuredAddress();

                if (null != structuredAddress) {
                    if (isValidStructuredAddress(structuredAddress)) {
                        validAddress = true;

                    }
                }
            }
            else {
                UnstructuredAddress unstructuredAddress = postalAddress.getUnstructuredAddress();
                if (null != unstructuredAddress) {

                    if (isTrimmedStringNotEmpty(unstructuredAddress.getPostCode()) && isValidUnstructuredAddress(unstructuredAddress)) {
                        validAddress = true;

                    }
                }
            }
        }
        return validAddress;
    }

    private boolean isValidUnstructuredAddress(UnstructuredAddress unstructuredAddress) {
        return (isTrimmedStringNotEmpty(unstructuredAddress.getAddressLine1()) && isTrimmedStringNotEmpty(unstructuredAddress.getAddressLine2()));

    }

    private boolean isValidStructuredAddress(StructuredAddress structuredAddress) {
        return (isTrimmedStringNotEmpty(structuredAddress.getPostCodeOut()) && isTrimmedStringNotEmpty(structuredAddress.getPostCodeIn()));
    }

    private boolean isTrimmedStringNotEmpty(String str) {
        return (str != null && str.trim().length() > EMPTY);
    }
}








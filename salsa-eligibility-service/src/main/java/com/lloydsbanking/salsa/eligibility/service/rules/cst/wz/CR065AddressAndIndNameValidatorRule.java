package com.lloydsbanking.salsa.eligibility.service.rules.cst.wz;

import com.lloydsbanking.salsa.eligibility.service.rules.common.EligibilityDecision;
import com.lloydsbanking.salsa.eligibility.service.rules.common.RuleDataHolder;
import com.lloydsbanking.salsa.eligibility.service.rules.cst.CSTEligibilityRule;
import com.lloydsbanking.salsa.eligibility.service.utility.constants.DeclineReasons;
import lib_sim_bo.businessobjects.Individual;
import lib_sim_bo.businessobjects.IndividualName;
import lib_sim_bo.businessobjects.PostalAddress;
import lib_sim_bo.businessobjects.UnstructuredAddress;

import javax.xml.datatype.XMLGregorianCalendar;
import java.util.List;

public class CR065AddressAndIndNameValidatorRule implements CSTEligibilityRule {
    private static final int EMPTY = 0;

    @Override
    public EligibilityDecision evaluate(RuleDataHolder ruleDataHolder, XMLGregorianCalendar birthDate, String sortCode, String customerId) {
        List<PostalAddress> postalAddresses = ruleDataHolder.getCustomerDetails() != null ? ruleDataHolder.getCustomerDetails().getPostalAddress() : null;
        Individual individual = ruleDataHolder.getCustomerDetails() != null ? ruleDataHolder.getCustomerDetails().getIsPlayedBy() : null;
        if (isValidUnstructuredPostalAddress(postalAddresses) && isValidIndividualName(individual)) {
            return new EligibilityDecision(true);
        }
        return new EligibilityDecision(DeclineReasons.CR065_DECLINE_REASON);
    }

    private boolean isValidUnstructuredPostalAddress(List<PostalAddress> postalAddresses) {
        boolean validAddress = false;
        if (postalAddresses != null) {
            for (PostalAddress postalAddress : postalAddresses) {

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

    private boolean isValidIndividualName(Individual individual) {
        List<IndividualName> individualNames = (null != individual) ? individual.getIndividualName() : null;

        if (null != individualNames) {
            for (IndividualName individualName : individualNames) {
                return (isStringNotEmpty(individualName.getFirstName()) && isStringNotEmpty(individualName.getLastName()));
            }
        }
        return false;

    }


    private boolean isTrimmedStringNotEmpty(String str) {
        return (str != null && str.trim().length() > EMPTY);
    }

    private boolean isStringNotEmpty(String str) {
        return (str != null && str.length() > EMPTY);
    }
}








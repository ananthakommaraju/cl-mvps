package com.lloydsbanking.salsa.opaloans.service.identify.evaluate;

import com.lloydsbanking.salsa.date.DateFactory;
import com.lloydsbanking.salsa.offer.OfferException;
import com.lloydsbanking.salsa.opaloans.ReasonCodes;
import com.lloydsbanking.salsa.opaloans.service.identify.downstream.CustomerRetriever;
import lib_sim_bo.businessobjects.Customer;
import lib_sim_bo.businessobjects.IndividualName;
import lib_sim_bo.businessobjects.ProductArrangement;
import lib_sim_bo.businessobjects.RuleCondition;
import lib_sim_gmo.exception.ExternalBusinessErrorMsg;
import lib_sim_gmo.exception.ExternalServiceErrorMsg;
import lib_sim_gmo.exception.ResourceNotAvailableErrorMsg;
import lib_sim_gmo.messages.RequestHeader;
import org.apache.cxf.common.util.CollectionUtils;
import org.apache.cxf.common.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.xml.datatype.XMLGregorianCalendar;
import java.util.ArrayList;
import java.util.List;

@Component
public class CustomerEvaluator {

    @Autowired
    CustomerRetriever customerRetriever;

    private static final String NON_OCIS_CUSTOMER_IDENTIFIER = "0";

    private static final String ADDITIONAL_DATA_REQUIRED_CONDITION_NAME = "ADDITIONAL_DATA_REQUIRED_INDICATOR";

    private static final String ADDITIONAL_DATA_REQUIRED_CONDITION_RESULT = "true";

    public boolean isBirthDate(RequestHeader header, ProductArrangement productArrangement) throws OfferException {
        Customer customer = productArrangement.getPrimaryInvolvedParty();
        boolean isBirthDate = true;
        List<Customer> clonedCustomerList = new ArrayList<>();
        List<Customer> customerList;
        try {
            customerList = customerRetriever.retrieveCustomer(header, customer.getExistingSortCode(), customer.getExistingAccountNumber());
        } catch (ResourceNotAvailableErrorMsg | ExternalBusinessErrorMsg | ExternalServiceErrorMsg e) {
            throw new OfferException(e);
        }
        clonedCustomerList.addAll(customerList);
        if (null != customerList) {
            isBirthDate = false;
            int birthDateMatchValue = getNumberOfBirthDateMatches(customer.getIsPlayedBy().getBirthDate(), customerList);
            if (birthDateMatchValue == 1) {
                isBirthDate = true;
                customer.setCustomerIdentifier(customerList.get(0).getCustomerIdentifier());
                customer.getIsPlayedBy().setBirthDate(customerList.get(0).getIsPlayedBy().getBirthDate());
            } else if (birthDateMatchValue > 1) {
                if (isFirstAndLastNameCaptured(customer.getIsPlayedBy().getIndividualName())) {
                    customer.setCustomerIdentifier(NON_OCIS_CUSTOMER_IDENTIFIER);
                    isBirthDate = getIsBirthDateIfFirstAndLastNamesAreEqual(clonedCustomerList, customer);
                    if (!isBirthDate) {
                        productArrangement.setReasonCode(initialiseReasonCode(ReasonCodes.NAME_NOT_MATCHED_WITH_OCIS));
                    }
                } else {
                    productArrangement.getConditions().add(0, new RuleCondition());
                    productArrangement.getConditions().get(0).setName(ADDITIONAL_DATA_REQUIRED_CONDITION_NAME);
                    productArrangement.getConditions().get(0).setResult(ADDITIONAL_DATA_REQUIRED_CONDITION_RESULT);
                }
            } else {
                productArrangement.setReasonCode(initialiseReasonCode(ReasonCodes.BIRTH_DATE_NOT_MATCHED));
            }
        }

        return isBirthDate;
    }

    private lib_sim_bo.businessobjects.ReasonCode initialiseReasonCode(ReasonCodes reasonCode) {
        lib_sim_bo.businessobjects.ReasonCode reason = new lib_sim_bo.businessobjects.ReasonCode();
        reason.setCode(reasonCode.getValue());
        reason.setDescription(reasonCode.getKey());
        return reason;
    }

    private int getNumberOfBirthDateMatches(XMLGregorianCalendar birthDateFromReq, List<Customer> customerList) {
        int birthDateMatchValue = 0;
        DateFactory dateFactory = new DateFactory();
        List<Customer> customers = new ArrayList<>();
        customers.addAll(customerList);
        for (Customer customer : customers) {
            if (null != birthDateFromReq && null != customer.getIsPlayedBy() && null != customer.getIsPlayedBy().getBirthDate()) {
                long comparisonOfDates = dateFactory.differenceInDays(
                        customer.getIsPlayedBy().getBirthDate().toGregorianCalendar().getTime(),
                        birthDateFromReq.toGregorianCalendar().getTime());
                if (0 == comparisonOfDates) {
                    birthDateMatchValue = birthDateMatchValue + 1;
                } else {
                    customerList.remove(birthDateMatchValue);
                }
            }
        }
        return birthDateMatchValue;
    }

    private boolean getIsBirthDateIfFirstAndLastNamesAreEqual(List<Customer> customerList, Customer customerFromReq) {
        boolean isBirthDate = false;

        for (Customer customer : customerList) {
            if (isFirstAndLastNameCaptured(customer.getIsPlayedBy().getIndividualName())) {
                String firstNameFromCustomer = customer.getIsPlayedBy().getIndividualName().get(0).getFirstName();
                String lastNameFromCustomer = customer.getIsPlayedBy().getIndividualName().get(0).getLastName();
                String firstNameFromRequest = customerFromReq.getIsPlayedBy().getIndividualName().get(0).getFirstName();
                String lastNameFromRequest = customerFromReq.getIsPlayedBy().getIndividualName().get(0).getLastName();

                if (firstNameFromCustomer.equalsIgnoreCase(firstNameFromRequest) && lastNameFromCustomer.equalsIgnoreCase(lastNameFromRequest)) {
                    isBirthDate = true;
                    customerFromReq.setCustomerIdentifier(customer.getCustomerIdentifier());
                    customerFromReq.getIsPlayedBy().setBirthDate(customer.getIsPlayedBy().getBirthDate());
                }
            }
        }
        return isBirthDate;
    }

    private boolean isFirstAndLastNameCaptured(List<IndividualName> individualNames) {
        return !CollectionUtils.isEmpty(individualNames)
                && null != individualNames.get(0)
                && !StringUtils.isEmpty(individualNames.get(0).getFirstName())
                && !StringUtils.isEmpty(individualNames.get(0).getLastName());
    }

}

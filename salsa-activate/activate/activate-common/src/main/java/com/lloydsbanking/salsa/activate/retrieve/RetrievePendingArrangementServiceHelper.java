package com.lloydsbanking.salsa.activate.retrieve;

import lib_sim_bo.businessobjects.Customer;
import lib_sim_bo.businessobjects.ProductArrangement;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

@Component
public class RetrievePendingArrangementServiceHelper {
    private static final Logger LOGGER = Logger.getLogger(RetrievePendingArrangementServiceHelper.class);

    public void setCommonPamDetailsForGalaxy(ProductArrangement upStreamProductArrangement, ProductArrangement productArrangement) {
        Customer customer = productArrangement.getPrimaryInvolvedParty();
        Customer upStreamCustomer = upStreamProductArrangement.getPrimaryInvolvedParty();
        customer.setIsRegistrationSelected(upStreamCustomer.isIsRegistrationSelected());
        customer.setPassword(upStreamCustomer.getPassword());
        customer.setUserType(upStreamCustomer.getUserType());
        customer.setInternalUserIdentifier(upStreamCustomer.getInternalUserIdentifier());
        if (customer.getIsPlayedBy() != null && upStreamCustomer.getIsPlayedBy() != null) {
            customer.getIsPlayedBy().setCustomerDeviceDetails(upStreamCustomer.getIsPlayedBy().getCustomerDeviceDetails());
        }
        upStreamProductArrangement.setPrimaryInvolvedParty(customer);
        upStreamProductArrangement.setApplicationStatus(productArrangement.getApplicationStatus());
        upStreamProductArrangement.setApplicationSubStatus(productArrangement.getApplicationSubStatus());
        upStreamProductArrangement.setArrangementType(productArrangement.getArrangementType());
        upStreamProductArrangement.setApplicationType(productArrangement.getApplicationType());
        upStreamProductArrangement.setRelatedApplicationId(productArrangement.getRelatedApplicationId());
        upStreamProductArrangement.getAffiliatedetails().addAll(productArrangement.getAffiliatedetails());
        upStreamProductArrangement.setMarketingPreferenceByEmail(productArrangement.isMarketingPreferenceByEmail());
        upStreamProductArrangement.setMarketingPreferenceByMail(productArrangement.isMarketingPreferenceByMail());
        upStreamProductArrangement.setMarketingPreferenceByPhone(productArrangement.isMarketingPreferenceByPhone());
        upStreamProductArrangement.setMarketingPreferenceBySMS(productArrangement.isMarketingPreferenceBySMS());
    }
}

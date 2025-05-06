package com.lloydsbanking.salsa.eligibility.service.utility;

import lb_gbo_sales.Customer;
import lb_gbo_sales.ProductArrangement;
import lib_sim_bo.businessobjects.OrganisationUnit;

import java.util.List;

public class EligibilityDataHelper {
    public String getCustomerId(List<ProductArrangement> customerArrangements) {
        for (ProductArrangement customerArrangement : customerArrangements) {
            for (Customer customer : customerArrangement.getParticipantCusomters()) {
                if (null != customer.getPartyId() && !"".equalsIgnoreCase(customer.getPartyId())) {
                    return customer.getPartyId();
                }
            }
        }
        return null;
    }

    public String getSortCode(List<ProductArrangement> customerArrangements) {
        for (ProductArrangement customerArrangement : customerArrangements) {
            if (null != customerArrangement.getSortCode() && !"".equalsIgnoreCase(customerArrangement.getSortCode().trim())) {
                return customerArrangement.getSortCode();
            }
        }
        return null;
    }

    public String getCustomerIdWZ(lib_sim_bo.businessobjects.Customer customerDetails) {

        if (null != customerDetails && null != customerDetails.getCustomerIdentifier() && !"".equalsIgnoreCase(customerDetails.getCustomerIdentifier())) {
            return customerDetails.getCustomerIdentifier();
        }

        return null;
    }

    public String getSortCodeWZ(List<lib_sim_bo.businessobjects.ProductArrangement> customerArrangements) {
        for (lib_sim_bo.businessobjects.ProductArrangement customerArrangement : customerArrangements) {
            if (null != customerArrangement.getFinancialInstitution() && null != customerArrangement.getFinancialInstitution().getHasOrganisationUnits()) {
                for (OrganisationUnit organisationUnit : customerArrangement.getFinancialInstitution().getHasOrganisationUnits()) {
                    if (null != organisationUnit && null != organisationUnit.getSortCode() && !"".equalsIgnoreCase(organisationUnit.getSortCode())) {
                        return organisationUnit.getSortCode();
                    }
                }
            }
        }
        return null;
    }
}

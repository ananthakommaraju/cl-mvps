package com.lloydsbanking.salsa.eligibility.service.rules.common;

import lb_gbo_sales.businessobjects.BusinessArrangement;

import java.util.List;

public class BusinessArrangementHandlerBZ implements BusinessArrangementHandler {

    private List<BusinessArrangement> businessArrangements;

    public BusinessArrangementHandlerBZ(List<BusinessArrangement> businessArrangements) {
        this.businessArrangements = businessArrangements;
    }

    @Override
    public String getEntityTypes(String selectedBusinessId) {
        if (businessArrangements != null && !businessArrangements.isEmpty()) {
            for (BusinessArrangement businessArrangement : businessArrangements) {
                if (null != businessArrangement.getBusinessId() && businessArrangement.getBusinessId().equals(selectedBusinessId)) {
                    return businessArrangement.getEnttyTyp();
                }
            }
        }
        return null;
    }

    @Override
    public boolean hasMBCRole(String selectedBusinessId, String rulParamValue) {

        for (BusinessArrangement businessArrangement : businessArrangements) {
            if (null != businessArrangement.getBusinessId() && selectedBusinessId.equalsIgnoreCase(businessArrangement.getBusinessId())) {
                String rolesInCtxt = businessArrangement.getRolesInCtxt();
                if (null != rolesInCtxt && rolesInCtxt.equalsIgnoreCase(rulParamValue)) {
                    return true;
                }

            }
        }
        return false;
    }

}

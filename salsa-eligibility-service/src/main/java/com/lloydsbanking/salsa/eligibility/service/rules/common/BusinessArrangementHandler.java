package com.lloydsbanking.salsa.eligibility.service.rules.common;

public interface BusinessArrangementHandler {

    String getEntityTypes(String selectedBusinessId);

    boolean hasMBCRole(String selectedBusinessId, String rulParamValue);
}

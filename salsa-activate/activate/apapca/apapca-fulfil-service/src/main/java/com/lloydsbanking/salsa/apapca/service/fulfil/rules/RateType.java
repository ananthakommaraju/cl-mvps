package com.lloydsbanking.salsa.apapca.service.fulfil.rules;

public interface RateType {
    String AMT_WAIVER_AMT = "WAIVER_AMT";
    String INT_FREE_OVERDRAFT = "INT_FREE_OVERDRAFT";
    String UNAUTH_MONTHLY_AMT_MONTHLY_FEE = "UNAUTH_MONTHLY_AMT_MONTHLY_FEE";
    String UNAUTH_EAR = "UNAUTH_EAR";
    String MARGIN_OBR_RATE = "MARGIN_OBR_RATE";
    String BASE_INT_RATE = "BASE_INT_RATE";
    String AUTH_MONTHLY = "AUTH_MONTHLY";
    String AUTH_EAR = "AUTH_EAR";
    String EXCESS_FEE_CAP = "EXCESS_FEE_CAP";
    String AMT_EXCESS_FEE_BAL_INC = "AMT_EXCESS_FEE_BAL_INC";
    String AMT_EXCESS_FEE = "AMT_EXCESS_FEE";
    String TOTAL_COST_OF_CREDIT = "TOTAL_COST_OF_CREDIT";
    String AMT_MONTHLY_FEE = "AMT_MONTHLY_FEE";
}

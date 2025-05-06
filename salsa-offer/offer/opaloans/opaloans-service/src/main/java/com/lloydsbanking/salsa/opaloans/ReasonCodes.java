package com.lloydsbanking.salsa.opaloans;

public enum ReasonCodes {
    NAME_NOT_MATCHED_WITH_OCIS("FirstName and LastName Not Matched with OCIS", "01"),
    BIRTH_DATE_NOT_MATCHED("BirthDate Not Matched", "01"),
    NO_ELIGIBLE_LOAN_PRODUCTS("No Eligible Loan Products", "02"),
    VERDE_CUSTOMER("Verde Customer", "03"),
    ACCOUNT_INVALID_FOR_LOAN("Account is invalid to apply for Loan", "04"),
    SAVED_LOAN_ALREADY_EXISTS("Already Have Saved Application", "05");

    private final String value;

    private final String key;

    ReasonCodes(String key, String value) {
        this.value = value;
        this.key = key;
    }

    public String getValue() {
        return this.value;
    }

    public String getKey() {
        return this.key;
    }

    public static String getReasonCode(String reasonCodeKey) {
        for (ReasonCodes appStatus : values()) {
            if (appStatus.getKey().equals(reasonCodeKey)) {
                return appStatus.getValue();
            }
        }
        return null;
    }
}

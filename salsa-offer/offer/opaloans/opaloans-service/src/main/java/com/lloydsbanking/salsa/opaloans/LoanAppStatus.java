package com.lloydsbanking.salsa.opaloans;

public enum LoanAppStatus {

    REFERRED("F", "LOANAPPSTATUS-Referred"),
    QUOTE_GIVEN("Q", "LOANAPPSTATUS-QuoteGiven"),
    BEING_PROCESSED("U", "LOANAPPSTATUS-BeingProcessed"),
    REFERIN_PROGRESS("T", "LOANAPPSTATUS-ReferInProgress"),
    ILLUS_GIVEN("I", "LOANAPPSTATUS-IllustrationGiven");

    private final String value;

    private final String key;

    LoanAppStatus(String key, String value) {
        this.value = value;
        this.key = key;
    }

    public String getValue() {
        return this.value;
    }

    public String getKey() {
        return this.key;
    }

    public static String getLoanAppStatus(String loanAppStatusKey) {
        for (LoanAppStatus appStatus : values()) {
            if (appStatus.getKey().equals(loanAppStatusKey)) {
                return appStatus.getValue();
            }
        }
        return null;
    }
}

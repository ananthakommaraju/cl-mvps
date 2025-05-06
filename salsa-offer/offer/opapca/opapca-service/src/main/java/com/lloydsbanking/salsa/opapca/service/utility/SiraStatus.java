package com.lloydsbanking.salsa.opapca.service.utility;

public enum SiraStatus {

    ACCEPT("ACCEPT"),
    REFER_FRAUD("REFER FRAUD"),
    REFER_IDV("REFER IDV"),
    DECLINE("DECLINE");

    private final String value;

    SiraStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }

    public static SiraStatus getSiraDecision(String resultStatus) {
        for (SiraStatus siraStatus : values()) {
            if (siraStatus.getValue().equals(resultStatus)) {
                return siraStatus;
            }
        }
        return null;
    }

}
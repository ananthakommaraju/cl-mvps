package com.lloydsbanking.salsa.opapca.service.utility;

public enum AppSubStatus {
    SIRA_REFER("5001"),
    ASM_REFER("5002"),
    SIRA_AND_ASM_REFER("5003"),
    EIDV_REFER("5004"),
    SIRA_IDV_REFER("5005"),
    SIRA_DECLINE("5006"),
    ASM_DECLINE("5007");

    private final String value;

    AppSubStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }

    public static AppSubStatus getAppSubtatus(String appSubStatus) {
        for (AppSubStatus subStatus : values()) {
            if (subStatus.getValue().equals(appSubStatus)) {
                return subStatus;
            }
        }
        return null;
    }
}

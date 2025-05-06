package com.lloydsbanking.salsa.offer;

public enum AsmDecision {

    APPROVED("1"),
    REFERRED("2"),
    DECLINED("3");


    private final String value;

    AsmDecision(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }


    public static AsmDecision getAsmDecision(String appStatus) {
        for (AsmDecision asmDecision : values()) {
            if (asmDecision.getValue().equals(appStatus)) {
                return asmDecision;
            }
        }
        return null;
    }
}

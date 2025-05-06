package com.lloydsbanking.salsa.offer;

public enum EIDVStatus {

    ACCEPT("ACCEPT"),
    REFER("REFER"),
    DECLINE("DECLINE");

    private final String value;

    EIDVStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }

}

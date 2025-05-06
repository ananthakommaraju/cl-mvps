package com.lloydsbanking.salsa.offer;

public enum ApplicantType {
    SELF("00"),
    DEPENDENT("01"),
    GUARDIAN("02");

    private final String value;

    ApplicantType(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }

    public static ApplicantType getApplicantType(String arrType) {
        for (ApplicantType applicantType : values()) {
            if (applicantType.getValue().equals(arrType)) {
                return applicantType;
            }
        }
        return null;
    }
}

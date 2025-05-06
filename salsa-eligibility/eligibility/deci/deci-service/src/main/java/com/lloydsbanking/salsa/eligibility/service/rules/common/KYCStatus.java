package com.lloydsbanking.salsa.eligibility.service.rules.common;


public class KYCStatus {
    private String status;

    public KYCStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}

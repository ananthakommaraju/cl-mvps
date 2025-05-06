package com.lloydsbanking.salsa.eligibility.service.utility.exceptions;

public class SalsaExternalServiceException extends Exception {


    private final String reasonText;

    private final String reasonCode;

    public SalsaExternalServiceException(String message, String reasonCode, String reasonText) {
        super(message);
        this.reasonCode = reasonCode;
        this.reasonText = reasonText;
    }

    public String getReasonText() {
        return reasonText;
    }

    public String getReasonCode() {
        return reasonCode;
    }
}

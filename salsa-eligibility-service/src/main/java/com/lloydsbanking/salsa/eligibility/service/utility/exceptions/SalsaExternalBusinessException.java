package com.lloydsbanking.salsa.eligibility.service.utility.exceptions;

public class SalsaExternalBusinessException extends Exception {


    private final Description description;

    private final ReasonText reasonText;

    private final String reasonCode;

    public SalsaExternalBusinessException(String message, String reasonCode, Description description) {
        super(message);
        this.reasonCode = reasonCode;
        this.description = description;
        this.reasonText = null;
    }

    public SalsaExternalBusinessException(String message, String reasonCode, ReasonText reasonText) {
        super(message);
        this.reasonCode = reasonCode;
        this.reasonText = reasonText;
        this.description = null;

    }


    public ReasonText getReasonText() {
        return reasonText;
    }

    public String getReasonCode() {
        return reasonCode;
    }

    public Description getDescription() {
        return description;
    }
}

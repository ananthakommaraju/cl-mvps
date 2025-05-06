package com.lloydsbanking.salsa.eligibility.service.utility.exceptions;

public class SalsaInternalResourceNotAvailableException extends Exception {


    public SalsaInternalResourceNotAvailableException(String message) {
        super(message);
    }

    public SalsaInternalResourceNotAvailableException(String message, Throwable cause) {
        super(message, cause);
    }

}

package com.lloydsbanking.salsa.offer;

public class OfferException extends Exception {

    final Throwable errorMsg;

    public OfferException(Exception errorMsg) {
        this.errorMsg = errorMsg;

    }

    public Throwable getErrorMsg() {
        return errorMsg;
    }


}

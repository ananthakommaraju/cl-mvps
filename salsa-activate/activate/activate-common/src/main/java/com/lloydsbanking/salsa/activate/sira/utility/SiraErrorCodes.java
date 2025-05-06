package com.lloydsbanking.salsa.activate.sira.utility;


public enum SiraErrorCodes {
    INCORRECT_CREDENTIALS_SUPPLIED("E50035"),
    OPERATION_NOT_SUPPORTED_FOR_USER("E50042"),
    DUPLICATE_ITEM("E50031"),
    SCHEMA_VALIDATION_FAILURE("E50036"),
    INVALID_KEY_SUPPLIED("E50046"),
    INVALID_PARAMETER("E50061"),
    SERVICE_HAS_REACHED_MAXIMUM_PROCESSING_LIMIT("E50053"),
    PROCESSING_NOT_COMPLETED("E50040"),
    REQUEST_EXPIRED_BEFORE_PROCESSING_ATTEMPTED("E50052"),
    SERVICE_DID_NOT_COMPLETE("E50055"),
    DATA_PROCESSING_SERVICE_UNAVAILABLE("E50037"),
    CRITICAL_SERVICE_RETURNED_ERROR("E50054"),
    UNEXPECTED_SYSTEM_ERROR("E50050"),
    SYSTEM_TEMPORARILY_UNAVAILABLE_TO_ACCEPT_REAL_TIME_REQUEST("E50051");


    private final String errorCode;

    SiraErrorCodes(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getSiraErrorCode() {
        return this.errorCode;
    }


    public static boolean isErrorForSira(String errorCode) {
        for (SiraErrorCodes siraErrorCodes : SiraErrorCodes.values()) {
            if (siraErrorCodes.errorCode.equalsIgnoreCase(errorCode)) {
                return Boolean.TRUE;
            }
        }
        return Boolean.FALSE;
    }

}

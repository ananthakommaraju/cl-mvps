package com.lloydsbanking.salsa.apapca.service.fulfil.orderaccess.downstream;

public enum C818ErrorCodes {
    RESPONSE_DATA_ERROR_IN_C818("218118"),
    UPDATE_COUNTER_MISMATCH_ERROR("219100"),
    UNEXPECTED_ERROR_CONDITION_IN_C818("218218"),
    ACCOUNT_DETAILS_NOT_FOUND("211523"),
    CBS_CUSTOMER_DETAILS_NOT_FOUND("219302"),
    CBS_ACCOUNT_CLOSED("219303"),
    CBS_SYSTEM_BUSY("219304"),
    REQUEST_DATA_ERROR_IN_C818("218018"),
    EXTERNAL_SYSTEM_ID_INVALID("218900"),
    EXTERNAL_SYSTEM_ID_UNAUTHORIZED("218901"),
    USER_ID_NOT_SUPPLIED("218980"),
    USER_ID_INVALID("218981"),
    USER_ID_EXCEEDING_EIGHT_CHARACTERS("218982"),
    USER_ID_TYPE_CODE_NOT_SUPPLIED("218983"),
    USER_ID_TYPE_CODE_INVALID("218984"),
    CHANNEL_OUTLET_ID_NOT_SUPPLIED("218985"),
    CHANNEL_OUTLET_ID_INVALID("218986"),
    CHANNEL_OUTLET_ID_EXCEEDING_EIGHT_CHARACTERS("218987"),
    CHANNEL_OUTLET_TYPE_CODE_NOT_SUPPLIED("218988"),
    CHANNEL_OUTLET_TYPE_CODE_INVALID("218989"),
    MAXIMUM_REPEAT_GROUP_COUNT_INVALID("218990"),
    CIAO_API_COULD_NOT_CALL_CMAS("218997"),
    CIAO_API_FAILURES("218998"),
    CMAS_RETURNED_ERROR_CODE_TO_CIAO_API("218999");


    private String errorCode;

    C818ErrorCodes() {
        this.errorCode = null;
    }

    C818ErrorCodes(String errorCode) {
        this.errorCode = errorCode;
    }

    public static boolean isExternalServiceErrorForC818(int errorCode) {
        for (C818ErrorCodes c818ErrorCodes : C818ErrorCodes.values()) {
            if (Integer.parseInt(c818ErrorCodes.errorCode) == errorCode) {
                return Boolean.TRUE;
            }
        }
        return Boolean.FALSE;
    }

    public String getCode() {
        return errorCode;
    }
}

package com.lloydsbanking.salsa.offer.apply.errorcode;

public enum DB2ErrorCodes {

    UNSUCCESSFUL_EXECUTION_CAUSED_BY_DEADLOCK_OR_TIMEOUT_IN_MODULE(-913),
    UNIT_OF_WORK_ROLLED_BACK_DUE_TO_DEADLOCK_OR_TIMEOUT_IN_MODULE(-911),
    UNAVAILABLE_DB2_RESOURCE_IN_MODULE(-904),
    DB2_TIMESTAMP_ERROR_IN(-818),
    A_SINGLETON_SELECT_FOUND_MORE_THAN_1_ROW_IN_MODULE(-811),
    MODULE_NOT_IN_DB2_PLAN_OR_NEW_VERSION_NOT_BOUND(-805),
    INVALID_DATE_TIME_VALUE_IN_MODULE(-181),
    INVALID_STRING_REPRESENTATION_OF_A_DATE_OR_TIME_VALUE_IN_MODULE(-180),
    USER_ID_ERROR_CONDITION_OCCURED_IN_COMMAND(69);

    private final int errorCode;

    DB2ErrorCodes(int errorCode) {
        this.errorCode = errorCode;
    }

    public int getDB2ErrorCode() {
        return this.errorCode;
    }

}

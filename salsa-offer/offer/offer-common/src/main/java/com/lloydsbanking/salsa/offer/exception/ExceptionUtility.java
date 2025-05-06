package com.lloydsbanking.salsa.offer.exception;


import lib_sim_gmo.exception.*;
import lib_sim_gmo.faults.*;

public class ExceptionUtility {

    public ExternalBusinessErrorMsg externalBusinessError(String errorCode, String errorText) {
        ExternalBusinessError externalBusinessError = new ExternalBusinessError();
        externalBusinessError.setReasonCode(errorCode);
        externalBusinessError.setReasonText(errorText);
        return (new ExternalBusinessErrorMsg(null, externalBusinessError));
    }

    public InternalServiceErrorMsg internalServiceError(String errorCode, String errorText) {
        InternalServiceError internalServiceError = new InternalServiceError();
        internalServiceError.setReasonCode(errorCode);
        internalServiceError.setReasonText(errorText);
        return (new InternalServiceErrorMsg(null, internalServiceError));
    }

    public ResourceNotAvailableErrorMsg resourceNotAvailableError(String message) {
        ResourceNotAvailableError resourcenotAvailableError = new ResourceNotAvailableError();
        resourcenotAvailableError.setDescription(message);
        return (new ResourceNotAvailableErrorMsg(null, resourcenotAvailableError));

    }

    public ExternalServiceErrorMsg externalServiceError(String errorCode, String reasonText) {
        ExternalServiceError externalServiceError = new ExternalServiceError();
        externalServiceError.setReasonText(reasonText);
        externalServiceError.setReasonCode(errorCode);
        return (new ExternalServiceErrorMsg(null, externalServiceError));
    }

    public DataNotAvailableErrorMsg dataNotAvailableError(String key, String field, String entity, String errorText) {
        DatabaseServiceError dataNotAvailableError = new DatabaseServiceError();
        dataNotAvailableError.setKey(key);
        dataNotAvailableError.setEntity(entity);
        dataNotAvailableError.setField(field);
        dataNotAvailableError.setDescription(errorText);
        return (new DataNotAvailableErrorMsg(null, dataNotAvailableError));
    }
}

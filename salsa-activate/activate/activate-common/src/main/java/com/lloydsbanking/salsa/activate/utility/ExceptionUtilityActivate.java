package com.lloydsbanking.salsa.activate.utility;

import com.lloydsbanking.salsa.header.gmo.RequestToResponseHeaderConverter;
import lib_sim_gmo.faults.DatabaseServiceError;
import lib_sim_gmo.faults.ExternalBusinessError;
import lib_sim_gmo.faults.ExternalServiceError;
import lib_sim_gmo.faults.InternalServiceError;
import lib_sim_gmo.faults.ResourceNotAvailableError;
import lib_sim_gmo.messages.RequestHeader;
import lib_sim_salesprocessmanagement.ia_activateproductarrangement.ActivateProductArrangementDataNotAvailableErrorMsg;
import lib_sim_salesprocessmanagement.ia_activateproductarrangement.ActivateProductArrangementExternalBusinessErrorMsg;
import lib_sim_salesprocessmanagement.ia_activateproductarrangement.ActivateProductArrangementExternalSystemErrorMsg;
import lib_sim_salesprocessmanagement.ia_activateproductarrangement.ActivateProductArrangementInternalSystemErrorMsg;
import lib_sim_salesprocessmanagement.ia_activateproductarrangement.ActivateProductArrangementResourceNotAvailableErrorMsg;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ExceptionUtilityActivate {
    @Autowired
    RequestToResponseHeaderConverter requestToResponseHeaderConverter;

    public ActivateProductArrangementDataNotAvailableErrorMsg dataNotAvailableError(String field, String entity, String errorText, RequestHeader header) {
        DatabaseServiceError dataNotAvailableError = new DatabaseServiceError();
        dataNotAvailableError.setEntity(entity);
        dataNotAvailableError.setField(field);
        dataNotAvailableError.setDescription(errorText);
        dataNotAvailableError.setResponseHeader(requestToResponseHeaderConverter.convert(header));
        return (new ActivateProductArrangementDataNotAvailableErrorMsg(null, dataNotAvailableError));
    }

    public ActivateProductArrangementInternalSystemErrorMsg internalServiceError(String errorCode, String errorText, RequestHeader header) {
        InternalServiceError internalServiceError = new InternalServiceError();
        if (null != errorCode) {
            internalServiceError.setReasonCode(errorCode);
        }
        if (null != errorText) {
            internalServiceError.setReasonText(errorText);
        }
        internalServiceError.setResponseHeader(requestToResponseHeaderConverter.convert(header));
        return (new ActivateProductArrangementInternalSystemErrorMsg(null, internalServiceError));
    }

    public ActivateProductArrangementResourceNotAvailableErrorMsg resourceNotAvailableError(RequestHeader header, String message) {
        ResourceNotAvailableError resourcenotAvailableError = new ResourceNotAvailableError();
        resourcenotAvailableError.setResponseHeader(requestToResponseHeaderConverter.convert(header));
        resourcenotAvailableError.setDescription(message);
        return (new ActivateProductArrangementResourceNotAvailableErrorMsg(null, resourcenotAvailableError));

    }

    public ActivateProductArrangementExternalBusinessErrorMsg externalBusinessError(RequestHeader header, String reasonText, String errorCode) {
        ExternalBusinessError externalBusinessError = new ExternalBusinessError();
        externalBusinessError.setReasonText(reasonText);
        externalBusinessError.setReasonCode(errorCode);
        externalBusinessError.setResponseHeader(requestToResponseHeaderConverter.convert(header));
        return new ActivateProductArrangementExternalBusinessErrorMsg(null, externalBusinessError);
    }

    public ActivateProductArrangementExternalSystemErrorMsg externalServiceError(RequestHeader requestHeader, String reasonText, String errorCode) {
        ExternalServiceError externalServiceError = new ExternalServiceError();
        externalServiceError.setReasonText(reasonText);
        externalServiceError.setReasonCode(errorCode);
        externalServiceError.setResponseHeader(requestToResponseHeaderConverter.convert(requestHeader));
        return new ActivateProductArrangementExternalSystemErrorMsg(null, externalServiceError);
    }

}

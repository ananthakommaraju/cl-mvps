package com.lloydsbanking.salsa.eligibility.service.utility.wz;

import com.lloydsbanking.salsa.eligibility.service.utility.exceptions.*;
import com.lloydsbanking.salsa.header.gmo.RequestToResponseHeaderConverter;
import lib_sim_gmo.faults.*;
import lib_sim_gmo.messages.RequestHeader;
import lib_sim_salesprocessmanagement.ia_determineeligiblecustomerinstruction.DetermineEligibleCustomerInstructionsDataNotAvailableErrorMsg;
import lib_sim_salesprocessmanagement.ia_determineeligiblecustomerinstruction.DetermineEligibleCustomerInstructionsExternalBusinessErrorMsg;
import lib_sim_salesprocessmanagement.ia_determineeligiblecustomerinstruction.DetermineEligibleCustomerInstructionsExternalServiceErrorMsg;
import lib_sim_salesprocessmanagement.ia_determineeligiblecustomerinstruction.DetermineEligibleCustomerInstructionsInternalServiceErrorMsg;
import lib_sim_salesprocessmanagement.ia_determineeligiblecustomerinstruction.DetermineEligibleCustomerInstructionsResourceNotAvailableErrorMsg;
import org.springframework.beans.factory.annotation.Autowired;

public class ExceptionUtility {
    RequestToResponseHeaderConverter requestToResponseHeaderConverterWZ;


    @Autowired
    public ExceptionUtility(RequestToResponseHeaderConverter requestToResponseHeaderConverterWZ) {
        this.requestToResponseHeaderConverterWZ = requestToResponseHeaderConverterWZ;

    }

    public DetermineEligibleCustomerInstructionsExternalBusinessErrorMsg externalBusinessError(String message, SalsaExternalBusinessException cause, RequestHeader header) {
        ExternalBusinessError externalBusinessError = new ExternalBusinessError();
        externalBusinessError.setReasonCode(cause.getReasonCode());
        if (null != cause.getReasonText()) {
            externalBusinessError.setReasonText(cause.getReasonText().getText());
        }
        if (null != cause.getDescription()) {
            externalBusinessError.setDescription(cause.getDescription().getText());
        }
        externalBusinessError.setResponseHeader(requestToResponseHeaderConverterWZ.convert(header));
        return (new DetermineEligibleCustomerInstructionsExternalBusinessErrorMsg(message, externalBusinessError));
    }

    public DetermineEligibleCustomerInstructionsExternalBusinessErrorMsg externalBusinessError(String errorCode, String errorText, String description, RequestHeader header) {
        ExternalBusinessError externalBusinessError = new ExternalBusinessError();
        externalBusinessError.setReasonCode(errorCode);
        externalBusinessError.setReasonText(errorText);
        externalBusinessError.setDescription(description);
        externalBusinessError.setResponseHeader(requestToResponseHeaderConverterWZ.convert(header));
        return (new DetermineEligibleCustomerInstructionsExternalBusinessErrorMsg(externalBusinessError.getDescription(), externalBusinessError));
    }

    public DetermineEligibleCustomerInstructionsInternalServiceErrorMsg internalServiceError(String message, SalsaInternalServiceException cause, RequestHeader header) {
        InternalServiceError internalServiceError = new InternalServiceError();
        if (null != cause.getReasonCode()) {
            internalServiceError.setReasonCode(cause.getReasonCode());


        }
        if (null != cause.getReasonText()) {
            internalServiceError.setReasonText(cause.getReasonText().getText());

        }

        if (null != cause.getDescription()) {
            internalServiceError.setDescription(cause.getDescription().getText());


        }

        internalServiceError.setResponseHeader(requestToResponseHeaderConverterWZ.convert(header));
        return (new DetermineEligibleCustomerInstructionsInternalServiceErrorMsg(message, internalServiceError));
    }

    public DetermineEligibleCustomerInstructionsInternalServiceErrorMsg internalServiceError(String reasonCode, Description description, RequestHeader header) {
        InternalServiceError internalServiceError = new InternalServiceError();
        internalServiceError.setReasonCode(reasonCode);
        internalServiceError.setDescription(description.getText());
        internalServiceError.setResponseHeader(requestToResponseHeaderConverterWZ.convert(header));
        return (new DetermineEligibleCustomerInstructionsInternalServiceErrorMsg(internalServiceError.getDescription(), internalServiceError));
    }

    public DetermineEligibleCustomerInstructionsInternalServiceErrorMsg internalServiceError(String reasonCode, ReasonText reasonText, RequestHeader header) {
        InternalServiceError internalServiceError = new InternalServiceError();
        internalServiceError.setReasonCode(reasonCode);
        internalServiceError.setReasonText(reasonText.getText());
        internalServiceError.setResponseHeader(requestToResponseHeaderConverterWZ.convert(header));
        return (new DetermineEligibleCustomerInstructionsInternalServiceErrorMsg(internalServiceError.getReasonText(), internalServiceError));

    }

    public DetermineEligibleCustomerInstructionsDataNotAvailableErrorMsg dataNotAvailableError(String key, String field, String entity, String errorText, RequestHeader header) {
        DatabaseServiceError dataNotAvailableError = new DatabaseServiceError();
        dataNotAvailableError.setKey(key);
        dataNotAvailableError.setEntity(entity);
        dataNotAvailableError.setField(field);
        dataNotAvailableError.setDescription(errorText);
        dataNotAvailableError.setResponseHeader(requestToResponseHeaderConverterWZ.convert(header));
        return (new DetermineEligibleCustomerInstructionsDataNotAvailableErrorMsg(null, dataNotAvailableError));
    }

    public DetermineEligibleCustomerInstructionsResourceNotAvailableErrorMsg resourceNotAvailableError(RequestHeader header, String message) {
        ResourceNotAvailableError resourcenotAvailableError = new ResourceNotAvailableError();
        resourcenotAvailableError.setResponseHeader(requestToResponseHeaderConverterWZ.convert(header));
        resourcenotAvailableError.setDescription(message);
        return (new DetermineEligibleCustomerInstructionsResourceNotAvailableErrorMsg(message, resourcenotAvailableError));

    }

    public DetermineEligibleCustomerInstructionsExternalServiceErrorMsg externalServiceError(String message, SalsaExternalServiceException cause, RequestHeader header) {
        ExternalServiceError externalServiceError = new ExternalServiceError();
        externalServiceError.setReasonCode(cause.getReasonCode());
        externalServiceError.setReasonText(cause.getReasonText());
        externalServiceError.setResponseHeader(requestToResponseHeaderConverterWZ.convert(header));
        return (new DetermineEligibleCustomerInstructionsExternalServiceErrorMsg(null, externalServiceError));
    }

    public DetermineEligibleCustomerInstructionsExternalServiceErrorMsg externalServiceError(String errorCode, String reasonText, RequestHeader header) {
        ExternalServiceError externalServiceError = new ExternalServiceError();
        externalServiceError.setReasonText(reasonText);
        externalServiceError.setReasonCode(errorCode);
        externalServiceError.setResponseHeader(requestToResponseHeaderConverterWZ.convert(header));
        return (new DetermineEligibleCustomerInstructionsExternalServiceErrorMsg(null, externalServiceError));
    }

}

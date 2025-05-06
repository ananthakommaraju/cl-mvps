package com.lloydsbanking.salsa.eligibility.service.utility;

import com.lloydsbanking.salsa.eligibility.service.utility.exceptions.Description;
import com.lloydsbanking.salsa.eligibility.service.utility.exceptions.ReasonText;
import com.lloydsbanking.salsa.eligibility.service.utility.exceptions.SalsaExternalBusinessException;
import com.lloydsbanking.salsa.eligibility.service.utility.exceptions.SalsaExternalServiceException;
import com.lloydsbanking.salsa.eligibility.service.utility.exceptions.SalsaInternalServiceException;
import com.lloydsbanking.salsa.header.RequestToResponseHeaderConverter;
import lb_gbo_sales.faults.DataNotAvailableError;
import lb_gbo_sales.faults.ExternalBusinessError;
import lb_gbo_sales.faults.ExternalServiceError;
import lb_gbo_sales.faults.InternalServiceError;
import lb_gbo_sales.faults.ResourceNotAvailableError;
import lb_gbo_sales.interfaces.ia_determineelegiblecustomerinstructions.DetermineEligibleInstructionsDataNotAvailableErrorfault1Msg;
import lb_gbo_sales.interfaces.ia_determineelegiblecustomerinstructions.DetermineEligibleInstructionsExternalBusinessErrorMsg;
import lb_gbo_sales.interfaces.ia_determineelegiblecustomerinstructions.DetermineEligibleInstructionsExternalServiceErrorMsg;
import lb_gbo_sales.interfaces.ia_determineelegiblecustomerinstructions.DetermineEligibleInstructionsInternalServiceErrorMsg;
import lb_gbo_sales.interfaces.ia_determineelegiblecustomerinstructions.DetermineEligibleInstructionsResourceNotAvailableErrorMsg;
import lb_gbo_sales.messages.RequestHeader;
import org.springframework.beans.factory.annotation.Autowired;

public class ExceptionUtility {
    RequestToResponseHeaderConverter requestToResponseHeaderConverter;

    @Autowired
    public ExceptionUtility(RequestToResponseHeaderConverter requestToResponseHeaderConverter) {
        this.requestToResponseHeaderConverter = requestToResponseHeaderConverter;

    }

    public DetermineEligibleInstructionsExternalBusinessErrorMsg externalBusinessError(String message, SalsaExternalBusinessException cause, RequestHeader header) {
        ExternalBusinessError externalBusinessError = new ExternalBusinessError();
        externalBusinessError.setReasonCode(cause.getReasonCode());
        if (null != cause.getReasonText()) {
            externalBusinessError.setReasonText(cause.getReasonText().getText());
        }
        if (null != cause.getDescription()) {
            externalBusinessError.setDescription(cause.getDescription().getText());
        }
        externalBusinessError.setResponseHeader(requestToResponseHeaderConverter.convert(header));
        return (new DetermineEligibleInstructionsExternalBusinessErrorMsg(message, externalBusinessError));
    }

    public DetermineEligibleInstructionsExternalBusinessErrorMsg externalBusinessError(String errorCode, String errorText, String description, RequestHeader header) {
        ExternalBusinessError externalBusinessError = new ExternalBusinessError();
        externalBusinessError.setReasonCode(errorCode);
        externalBusinessError.setReasonText(errorText);
        externalBusinessError.setDescription(description);
        externalBusinessError.setResponseHeader(requestToResponseHeaderConverter.convert(header));
        return (new DetermineEligibleInstructionsExternalBusinessErrorMsg(externalBusinessError.getDescription(), externalBusinessError));
    }

    public DetermineEligibleInstructionsInternalServiceErrorMsg internalServiceError(String message, SalsaInternalServiceException cause, RequestHeader header) {
        InternalServiceError internalServiceError = new InternalServiceError();
        if (null != cause.getReasonCode()) {
            internalServiceError.setReasonCode(cause.getReasonCode());
            if (null != cause.getReasonText()) {
                internalServiceError.setReasonText(cause.getReasonText().getText());
            }
            if (null != cause.getDescription()) {
                internalServiceError.setDescription(cause.getDescription().getText());
            }
        }
        internalServiceError.setResponseHeader(requestToResponseHeaderConverter.convert(header));
        return (new DetermineEligibleInstructionsInternalServiceErrorMsg(message, internalServiceError));
    }

    public DetermineEligibleInstructionsInternalServiceErrorMsg internalServiceError(String reasonCode, Description description, RequestHeader header) {
        InternalServiceError internalServiceError = new InternalServiceError();

        internalServiceError.setReasonCode(reasonCode);
        if (null != description) {
            internalServiceError.setDescription(description.getText());
        }
        internalServiceError.setResponseHeader(requestToResponseHeaderConverter.convert(header));
        return (new DetermineEligibleInstructionsInternalServiceErrorMsg(internalServiceError.getDescription(), internalServiceError));
    }

    public DetermineEligibleInstructionsInternalServiceErrorMsg internalServiceError(String reasonCode, ReasonText reasonText, RequestHeader header) {
        InternalServiceError internalServiceError = new InternalServiceError();
        internalServiceError.setReasonCode(reasonCode);
        if (null != reasonText) {
            internalServiceError.setReasonText(reasonText.getText());
        }
        internalServiceError.setResponseHeader(requestToResponseHeaderConverter.convert(header));
        return (new DetermineEligibleInstructionsInternalServiceErrorMsg(internalServiceError.getReasonText(), internalServiceError));
    }

    public DetermineEligibleInstructionsDataNotAvailableErrorfault1Msg dataNotAvailableError(String key, String field, String entity, String errorText, RequestHeader header) {
        DataNotAvailableError dataNotAvailableError = new DataNotAvailableError();
        dataNotAvailableError.setKey(key);
        dataNotAvailableError.setEntity(entity);
        dataNotAvailableError.setField(field);
        dataNotAvailableError.setDescription(errorText);
        dataNotAvailableError.setResponseHeader(requestToResponseHeaderConverter.convert(header));
        return (new DetermineEligibleInstructionsDataNotAvailableErrorfault1Msg(null, dataNotAvailableError));
    }

    public DetermineEligibleInstructionsResourceNotAvailableErrorMsg resourceNotAvailableError(RequestHeader header, String message) {
        ResourceNotAvailableError resourcenotAvailableError = new ResourceNotAvailableError();
        resourcenotAvailableError.setResponseHeader(requestToResponseHeaderConverter.convert(header));
        resourcenotAvailableError.setDescription(message);
        return (new DetermineEligibleInstructionsResourceNotAvailableErrorMsg(message, resourcenotAvailableError));

    }

    public DetermineEligibleInstructionsExternalServiceErrorMsg externalServiceError(String message, SalsaExternalServiceException cause, RequestHeader header) {
        ExternalServiceError externalServiceError = new ExternalServiceError();
        externalServiceError.setReasonCode(cause.getReasonCode());
        externalServiceError.setReasonText(cause.getReasonText());
        externalServiceError.setResponseHeader(requestToResponseHeaderConverter.convert(header));
        return (new DetermineEligibleInstructionsExternalServiceErrorMsg(null, externalServiceError));
    }

    public DetermineEligibleInstructionsExternalServiceErrorMsg externalServiceError(String errorCode, String reasonText, RequestHeader header) {
        ExternalServiceError externalServiceError = new ExternalServiceError();
        externalServiceError.setReasonText(reasonText);
        externalServiceError.setReasonCode(errorCode);
        externalServiceError.setResponseHeader(requestToResponseHeaderConverter.convert(header));
        return (new DetermineEligibleInstructionsExternalServiceErrorMsg(null, externalServiceError));
    }

}

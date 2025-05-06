package com.lloydsbanking.salsa.opasaving.service.utility;


import com.lloydsbanking.salsa.offer.OfferException;
import lib_sim_gmo.exception.*;

import lib_sim_gmo.messages.ResponseHeader;
import lib_sim_salesprocessmanagement.ia_offerproductarrangement.*;

public class ExceptionHelper {

    public void setResponseHeaderAndThrowException(OfferException exception, ResponseHeader responseHeader) throws OfferProductArrangementInternalServiceErrorMsg, OfferProductArrangementResourceNotAvailableErrorMsg, OfferProductArrangementExternalBusinessErrorMsg, OfferProductArrangementExternalServiceErrorMsg, OfferProductArrangementDataNotAvailableErrorMsg {
        if (exception.getErrorMsg() instanceof InternalServiceErrorMsg) {
            OfferProductArrangementInternalServiceErrorMsg internalServiceError = new OfferProductArrangementInternalServiceErrorMsg(null,((InternalServiceErrorMsg) exception.getErrorMsg()).getFaultInfo());
            internalServiceError.getFaultInfo().setResponseHeader(responseHeader);
            throw internalServiceError;
        } else if (exception.getErrorMsg() instanceof ResourceNotAvailableErrorMsg) {
            OfferProductArrangementResourceNotAvailableErrorMsg resourceNotAvailableError = new OfferProductArrangementResourceNotAvailableErrorMsg(null,((ResourceNotAvailableErrorMsg) exception.getErrorMsg()).getFaultInfo());
            resourceNotAvailableError.getFaultInfo().setResponseHeader(responseHeader);
            throw resourceNotAvailableError;
        } else if (exception.getErrorMsg() instanceof DataNotAvailableErrorMsg) {
            OfferProductArrangementDataNotAvailableErrorMsg dataNotAvailableError = new OfferProductArrangementDataNotAvailableErrorMsg(null,((DataNotAvailableErrorMsg) exception.getErrorMsg()).getFaultInfo());
            dataNotAvailableError.getFaultInfo().setResponseHeader(responseHeader);
            throw dataNotAvailableError;
        } else {
            setResponseHeaderAndThrowExternalError(exception, responseHeader);
        }
    }

    private void setResponseHeaderAndThrowExternalError(OfferException exception, ResponseHeader responseHeader) throws OfferProductArrangementExternalBusinessErrorMsg, OfferProductArrangementExternalServiceErrorMsg {
        if (exception.getErrorMsg() instanceof ExternalBusinessErrorMsg) {
            OfferProductArrangementExternalBusinessErrorMsg externalBusinessError = new OfferProductArrangementExternalBusinessErrorMsg(null,((ExternalBusinessErrorMsg) exception.getErrorMsg()).getFaultInfo());
            externalBusinessError.getFaultInfo().setResponseHeader(responseHeader);
            throw externalBusinessError;
        } else if (exception.getErrorMsg() instanceof ExternalServiceErrorMsg) {
            OfferProductArrangementExternalServiceErrorMsg externalServiceError = new OfferProductArrangementExternalServiceErrorMsg(null, ((ExternalServiceErrorMsg) exception.getErrorMsg()).getFaultInfo());
            externalServiceError.getFaultInfo().setResponseHeader(responseHeader);
            throw externalServiceError;
        }
    }

}

package com.lloydsbanking.salsa.aps.service.exception;


import lib_sim_gmo.exception.DataNotAvailableErrorMsg;
import lib_sim_gmo.exception.InternalServiceErrorMsg;
import lib_sim_gmo.exception.ResourceNotAvailableErrorMsg;
import lib_sim_gmo.messages.ResponseHeader;
import lib_sim_productsalesreferencedatamanager.id_determineproducteligibilitytype.AdministerProductSelectionDataNotAvailableErrorMsg;
import lib_sim_productsalesreferencedatamanager.id_determineproducteligibilitytype.AdministerProductSelectionInternalServiceErrorMsg;
import lib_sim_productsalesreferencedatamanager.id_determineproducteligibilitytype.AdministerProductSelectionResourceNotAvailableErrorMsg;

public class ExceptionHelper {

    public void setResponseHeaderAndThrowException(Exception exception, ResponseHeader responseHeader) throws AdministerProductSelectionResourceNotAvailableErrorMsg, AdministerProductSelectionDataNotAvailableErrorMsg, AdministerProductSelectionInternalServiceErrorMsg {
        if (exception instanceof ResourceNotAvailableErrorMsg) {
            AdministerProductSelectionResourceNotAvailableErrorMsg resourceNotAvailableError = new AdministerProductSelectionResourceNotAvailableErrorMsg(null, ((ResourceNotAvailableErrorMsg) exception).getFaultInfo());
            resourceNotAvailableError.getFaultInfo().setResponseHeader(responseHeader);
            throw resourceNotAvailableError;
        } else if (exception instanceof DataNotAvailableErrorMsg) {
            AdministerProductSelectionDataNotAvailableErrorMsg dataNotAvailableError = new AdministerProductSelectionDataNotAvailableErrorMsg(null, ((DataNotAvailableErrorMsg) exception).getFaultInfo());
            dataNotAvailableError.getFaultInfo().setResponseHeader(responseHeader);
            throw dataNotAvailableError;
        } else {
            AdministerProductSelectionInternalServiceErrorMsg internalServiceError = new AdministerProductSelectionInternalServiceErrorMsg(null, ((InternalServiceErrorMsg) exception).getFaultInfo());
            internalServiceError.getFaultInfo().setResponseHeader(responseHeader);
            throw internalServiceError;
        }
    }


}

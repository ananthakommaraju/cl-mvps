package com.lloydsbanking.salsa.aps.service;


import com.lloydsbanking.salsa.aps.service.exception.ExceptionHelper;
import com.lloydsbanking.salsa.downstream.prd.service.AdministerProductSelectionService;
import com.lloydsbanking.salsa.header.gmo.RequestToResponseHeaderConverter;
import lib_sim_gmo.exception.InternalServiceErrorMsg;
import lib_sim_gmo.exception.ResourceNotAvailableErrorMsg;
import lib_sim_productsalesreferencedatamanager.id_determineproducteligibilitytype.AdministerProductSelectionDataNotAvailableErrorMsg;
import lib_sim_productsalesreferencedatamanager.id_determineproducteligibilitytype.AdministerProductSelectionInternalServiceErrorMsg;
import lib_sim_productsalesreferencedatamanager.id_determineproducteligibilitytype.AdministerProductSelectionResourceNotAvailableErrorMsg;
import lib_sim_productsalesreferencedatamanager.id_determineproducteligibilitytype.IDDetermineProductEligibilityType;
import lib_sim_productsalesreferencedatamanager.messages.AdministerProductSelectionRequest;
import lib_sim_productsalesreferencedatamanager.messages.AdministerProductSelectionResponse;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class ApsService implements IDDetermineProductEligibilityType {

    private static final Logger LOGGER = Logger.getLogger(ApsService.class);
    @Autowired
    AdministerProductSelectionService administerProductSelectionService;
    @Autowired
    RequestToResponseHeaderConverter headerConverter;

    @Autowired
    ExceptionHelper exceptionHelper;

    @Override
    public AdministerProductSelectionResponse administerProductSelection(final AdministerProductSelectionRequest request) throws AdministerProductSelectionInternalServiceErrorMsg, AdministerProductSelectionResourceNotAvailableErrorMsg, AdministerProductSelectionDataNotAvailableErrorMsg {
        LOGGER.info("Entering Administer Product Selection");
        AdministerProductSelectionResponse response = new AdministerProductSelectionResponse();
        response.setHeader(headerConverter.convert(request.getHeader()));
        String productEligibilityType = null;
        try {
            productEligibilityType = administerProductSelectionService.administerProductSelection(request.getExistingProduct(), request.getAppliedProduct(), request.getApplicationTypeCode());
        } catch (InternalServiceErrorMsg internalServiceErrorMsg) {
            exceptionHelper.setResponseHeaderAndThrowException(internalServiceErrorMsg, response.getHeader());
        } catch (ResourceNotAvailableErrorMsg resourceNotAvailableErrorMsg) {
            exceptionHelper.setResponseHeaderAndThrowException(resourceNotAvailableErrorMsg, response.getHeader());
        }

        response.setProductEligibilityType(productEligibilityType);
        return response;
    }
}
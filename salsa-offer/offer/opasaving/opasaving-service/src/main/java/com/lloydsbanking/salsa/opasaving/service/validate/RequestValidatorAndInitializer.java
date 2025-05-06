package com.lloydsbanking.salsa.opasaving.service.validate;


import com.lloydsbanking.salsa.header.gmo.HeaderRetriever;
import com.lloydsbanking.salsa.header.gmo.RequestToResponseHeaderConverter;
import com.lloydsbanking.salsa.offer.LegalEntityMapUtility;
import com.lloydsbanking.salsa.offer.OfferException;
import com.lloydsbanking.salsa.offer.exception.ExceptionUtility;
import com.lloydsbanking.salsa.offer.pam.LookupDataRetriever;
import lib_sim_bo.businessobjects.PostalAddress;
import lib_sim_gmo.exception.DataNotAvailableErrorMsg;
import lib_sim_gmo.exception.InternalServiceErrorMsg;
import lib_sim_gmo.messages.RequestHeader;
import lib_sim_gmo.messages.ResponseHeader;
import lib_sim_salesprocessmanagement.messages.OfferProductArrangementRequest;
import lib_sim_salesprocessmanagement.messages.OfferProductArrangementResponse;
import org.apache.cxf.common.util.CollectionUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class RequestValidatorAndInitializer {
    private static final Logger LOGGER = Logger.getLogger(RequestValidatorAndInitializer.class);

    @Autowired
    ExceptionUtility exceptionUtility;
    @Autowired
    HeaderRetriever headerRetriever;
    @Autowired
    LookupDataRetriever offerLookupDataRetriever;
    @Autowired
    LegalEntityMapUtility legalEntityMapUtility;
    @Autowired
    RequestToResponseHeaderConverter responseHeaderConverter;

    private static final String CURRENT_ADDRESS = "CURRENT";

    private static final String UN_AUTH_USER_ID_AUTHOR = "UNAUTHSALE";

    public void validateRequest(OfferProductArrangementRequest request) throws InternalServiceErrorMsg {

        if (!isProductArrangementAvailable(request)) {
            LOGGER.error("ProductArrangement is null");
            throw exceptionUtility.internalServiceError(null, "No Product Arrangement supplied");
        }
        if (!isPrimaryInvolvedPartyPresent(request)) {
            throw exceptionUtility.internalServiceError(null, "Primary Involved Party passed is NULL");
        }
    }

    private boolean isPrimaryInvolvedPartyPresent(OfferProductArrangementRequest request) {
        if (null != request.getProductArrangement().getPrimaryInvolvedParty()) {
            return true;
        }
        return false;
    }

    private boolean isProductArrangementAvailable(OfferProductArrangementRequest request) {
        if (null != request && null != request.getProductArrangement()) {
            return true;
        }
        return false;
    }

    public void initialiseVariables(OfferProductArrangementRequest request, OfferProductArrangementResponse response) throws OfferException {
        try {
            validateRequest(request);
            postalAddressMapping(request.getProductArrangement().getPrimaryInvolvedParty().getPostalAddress());
            request.getHeader().setChannelId(offerLookupDataRetriever.getChannelIdFromContactPointId(headerRetriever.getContactPoint(request.getHeader()).getContactPointId()));
        }
        catch (DataNotAvailableErrorMsg dataNotAvailableErrorMsg) {
            throw new OfferException(dataNotAvailableErrorMsg);
        }
        catch (InternalServiceErrorMsg internalServiceErrorMsg) {
            throw new OfferException(internalServiceErrorMsg);
        }
        request.getProductArrangement().getPrimaryInvolvedParty().getCustomerScore().clear();
        request.getProductArrangement().getAssociatedProduct().setBrandName(request.getHeader().getChannelId());
        //TODO: Need to check the brand name setter
        ResponseHeader responseHeader = responseHeaderConverter.convert(request.getHeader());
        response.setHeader(responseHeader);
        try {
            legalEntityMapUtility.createLegalEntityMap(request.getHeader().getChannelId());
        }
        catch (DataNotAvailableErrorMsg dataNotAvailableErrorMsg) {
            throw new OfferException(dataNotAvailableErrorMsg);
        }
    }


    public boolean isUnAuthCustomer(RequestHeader requestHeader) {
        boolean isUnAuth = UN_AUTH_USER_ID_AUTHOR.equalsIgnoreCase(headerRetriever.getBapiInformationHeader(requestHeader).getBAPIHeader().getUseridAuthor());
        LOGGER.info("Offer: OPASaving: Is UnAuth Customer = " + isUnAuth);
        return isUnAuth;
    }

    public boolean isBfpoAddress(List<PostalAddress> postalAddressList) {
        if (postalAddressList != null) {
            for (PostalAddress postalAddress : postalAddressList) {
                if (CURRENT_ADDRESS.equalsIgnoreCase(postalAddress.getStatusCode()) && null != postalAddress.isIsBFPOAddress() && postalAddress.isIsBFPOAddress()) {
                    return true;
                }
            }
        }
        LOGGER.info("BFPO Address Indicator is not present");
        return false;
    }

    private void postalAddressMapping(List<PostalAddress> postalAddressList) {
        if (!CollectionUtils.isEmpty(postalAddressList)) {
            PostalAddress postalAddressCurrent = new PostalAddress();
            PostalAddress postalAddressPrevious = null;
            for (PostalAddress postalAddress : postalAddressList) {
                if (CURRENT_ADDRESS.equalsIgnoreCase(postalAddress.getStatusCode())) {
                    postalAddressCurrent = postalAddress;
                } else {
                    postalAddressPrevious = postalAddress;
                }
            }
            postalAddressList.clear();
            postalAddressList.add(postalAddressCurrent);
            if (postalAddressPrevious != null) {
                postalAddressList.add(postalAddressPrevious);
            }
        }
    }
}

package com.lloydsbanking.salsa.opaloans.service.validate;


import com.lloydsbanking.salsa.header.gmo.HeaderRetriever;
import com.lloydsbanking.salsa.header.gmo.RequestToResponseHeaderConverter;
import com.lloydsbanking.salsa.offer.LegalEntityMapUtility;
import com.lloydsbanking.salsa.offer.OfferException;
import com.lloydsbanking.salsa.offer.exception.ExceptionUtility;
import com.lloydsbanking.salsa.offer.pam.LookupDataRetriever;
import lib_sim_gmo.exception.DataNotAvailableErrorMsg;
import lib_sim_gmo.exception.InternalServiceErrorMsg;
import lib_sim_gmo.messages.ResponseHeader;
import lib_sim_salesprocessmanagement.messages.OfferProductArrangementRequest;
import lib_sim_salesprocessmanagement.messages.OfferProductArrangementResponse;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

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

    public void validateRequest(OfferProductArrangementRequest request) throws InternalServiceErrorMsg {

        if (!isProductArrangementAvailable(request)) {
            LOGGER.error("ProductArrangement for eligibility is null");
            throw exceptionUtility.internalServiceError(null, "No product arrangements supplied");
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
            request.getHeader().setChannelId(offerLookupDataRetriever.getChannelIdFromContactPointId(headerRetriever.getContactPoint(request.getHeader()).getContactPointId()));

            request.getProductArrangement().getAssociatedProduct().setBrandName(request.getHeader().getChannelId());
            //TODO: Need to check the brand name setter
            ResponseHeader responseHeader = responseHeaderConverter.convert(request.getHeader());
            response.setHeader(responseHeader);

            legalEntityMapUtility.createLegalEntityMap(request.getHeader().getChannelId());
        } catch (DataNotAvailableErrorMsg errorMsg) {
            throw new OfferException(errorMsg);
        }
        catch (InternalServiceErrorMsg internalServiceErrorMsg) {
            throw new OfferException(internalServiceErrorMsg);
        }
    }
}

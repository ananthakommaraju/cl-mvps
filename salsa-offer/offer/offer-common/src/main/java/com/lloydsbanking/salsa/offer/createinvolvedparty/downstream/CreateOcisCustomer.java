package com.lloydsbanking.salsa.offer.createinvolvedparty.downstream;

import com.lloydsbanking.salsa.downstream.ocis.client.f062.F062Client;
import lib_sim_gmo.exception.*;
import com.lloydsbanking.salsa.header.gmo.HeaderRetriever;
import com.lloydsbanking.salsa.offer.createinvolvedparty.convert.CreateOcisCustomerRequestFactory;
import com.lloydsbanking.salsa.offer.createinvolvedparty.errorcode.RetrieveOcisErrorMap;
import com.lloydsbanking.salsa.offer.exception.ExceptionUtility;
import com.lloydsbanking.salsa.soap.ocis.f062.objects.F062Req;
import com.lloydsbanking.salsa.soap.ocis.f062.objects.F062Resp;
import com.lloydsbanking.salsa.soap.ocis.f062.objects.F062Result;
import com.lloydstsb.schema.infrastructure.soap.ContactPoint;
import com.lloydstsb.schema.infrastructure.soap.ServiceRequest;
import lib_sim_bo.businessobjects.Customer;
import lib_sim_gmo.messages.RequestHeader;
import org.apache.log4j.Logger;
import org.oasis_open.docs.wss._2004._01.oasis_200401_wss_wssecurity_secext_1_0.SecurityHeaderType;
import org.springframework.beans.factory.annotation.Autowired;

import javax.xml.ws.WebServiceException;
import java.text.ParseException;

public class CreateOcisCustomer {
    private static final Logger LOGGER = Logger.getLogger(CreateOcisCustomer.class);

    private static final String INTERNAL_SERVICE_ERROR_CODE = "820001";

    private static final String EXTERNAL_BUSINESS_ERROR = "External Business Error";

    private static final String EXTERNAL_SERVICE_ERROR = "External Service Error";

    @Autowired
    CreateOcisCustomerRequestFactory createOcisCustomerRequestFactory;

    @Autowired(required = false)
    F062Client f062Client;

    @Autowired
    HeaderRetriever headerRetriever;

    @Autowired
    ExceptionUtility exceptionUtility;

    @Autowired
    RetrieveOcisErrorMap errorMapOcis;

    public F062Resp create(String arrangementType, Customer primaryInvolvedParty, RequestHeader header, boolean marketingPref) throws ResourceNotAvailableErrorMsg, ExternalServiceErrorMsg, ExternalBusinessErrorMsg, InternalServiceErrorMsg {
        F062Req f062Req = createF062Req(arrangementType, primaryInvolvedParty, header, marketingPref);
        F062Resp f062Resp = getF062Response(f062Req, header);
        if (isError(f062Resp)) {
            throwErrorInResponse(f062Resp.getF062Result());
        }
        return f062Resp;
    }

    private void throwErrorInResponse(F062Result f062Result) throws ExternalServiceErrorMsg, ExternalBusinessErrorMsg {
        int reasonCode = f062Result.getResultCondition().getReasonCode();
        String reasonText = f062Result.getResultCondition().getReasonText();
        if (isErrorType(EXTERNAL_SERVICE_ERROR, reasonCode, reasonText)) {
            throw exceptionUtility.externalServiceError(errorMapOcis.getOcisErrorCode(reasonCode), reasonCode + ":" + reasonText);
        } else if (isErrorType(EXTERNAL_BUSINESS_ERROR, reasonCode, reasonText)) {
            throw exceptionUtility.externalBusinessError(errorMapOcis.getOcisErrorCode(reasonCode), f062Result.getResultCondition()
                    .getReasonCode() + ":" + f062Result.getResultCondition().getReasonText());
        }
        throw exceptionUtility.externalServiceError(String.valueOf(reasonCode), f062Result.getResultCondition()
                .getReasonCode() + ":" + f062Result.getResultCondition().getReasonText());
    }

    private boolean isError(F062Resp f062Resp) {
        return (null != f062Resp.getF062Result().getResultCondition() && null != f062Resp.getF062Result().getResultCondition().getReasonCode() && 0 != (f062Resp.getF062Result().getResultCondition().getReasonCode()));

    }

    private boolean isErrorType(String error, Integer reasonCode, String reasonText) {
        if (error.equals(errorMapOcis.getOcisErrorCode(reasonCode))) {
            LOGGER.error(error + " while creating Ocis record via F062 . ErrorNo | ErrorMsg ; " + reasonCode + " | " + reasonText);
            return true;
        }
        return false;
    }

    private F062Req createF062Req(String arrangementType, Customer primaryInvolvedParty, RequestHeader header, boolean marketingPref) throws InternalServiceErrorMsg {
        try {
            return createOcisCustomerRequestFactory.convert(arrangementType, primaryInvolvedParty, marketingPref, header.getChannelId());
        } catch (ParseException e) {
            LOGGER.error("Returning InternalServiceError while creating OCIS F062 : ", e);
            throw exceptionUtility.internalServiceError(INTERNAL_SERVICE_ERROR_CODE, e.getMessage());
        }

    }

    private F062Resp getF062Response(F062Req f062Req, RequestHeader header) throws ResourceNotAvailableErrorMsg {
        ContactPoint contactPoint = headerRetriever.getContactPoint(header);
        ServiceRequest serviceRequest = headerRetriever.getServiceRequest(header);
        SecurityHeaderType securityHeaderType = headerRetriever.getSecurityHeader(header);
        try {
            return f062Client.updateCustomerRecord(f062Req, contactPoint, serviceRequest, securityHeaderType);
        } catch (WebServiceException webServiceException) {
            //throwing resourceNotAvailable for exceptions from client
            LOGGER.error("Exception occurred while calling OCIS F062. Returning ResourceNotAvailableError ;", webServiceException);
            throw exceptionUtility.resourceNotAvailableError(webServiceException.getMessage());
        }

    }

}

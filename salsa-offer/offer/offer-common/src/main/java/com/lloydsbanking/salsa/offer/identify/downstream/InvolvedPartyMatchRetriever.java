package com.lloydsbanking.salsa.offer.identify.downstream;

import com.lloydsbanking.salsa.downstream.ocis.client.f447.F447Client;
import lib_sim_gmo.exception.ExternalBusinessErrorMsg;
import lib_sim_gmo.exception.InternalServiceErrorMsg;
import lib_sim_gmo.exception.ResourceNotAvailableErrorMsg;
import com.lloydsbanking.salsa.header.gmo.HeaderRetriever;
import com.lloydsbanking.salsa.offer.exception.ExceptionUtility;
import com.lloydsbanking.salsa.offer.identify.convert.EnquirePartyIdRequestFactory;
import com.lloydsbanking.salsa.soap.ocis.f447.objects.F447Req;
import com.lloydsbanking.salsa.soap.ocis.f447.objects.F447Resp;
import com.lloydsbanking.salsa.soap.ocis.f447.objects.F447Result;
import com.lloydstsb.schema.infrastructure.soap.ContactPoint;
import com.lloydstsb.schema.infrastructure.soap.ServiceRequest;
import lib_sim_bo.businessobjects.Individual;
import lib_sim_bo.businessobjects.PostalAddress;
import lib_sim_gmo.messages.RequestHeader;
import org.apache.log4j.Logger;
import org.oasis_open.docs.wss._2004._01.oasis_200401_wss_wssecurity_secext_1_0.SecurityHeaderType;
import org.springframework.beans.factory.annotation.Autowired;

import javax.xml.ws.WebServiceException;
import java.util.Arrays;
import java.util.List;


public class InvolvedPartyMatchRetriever {

    @Autowired
    EnquirePartyIdRequestFactory f447RequestFactory;

    @Autowired
    ExceptionUtility exceptionUtility;

    @Autowired(required = false)
    F447Client f447Client;

    @Autowired
    HeaderRetriever headerRetriever;

    private static final Logger LOGGER = Logger.getLogger(InvolvedPartyMatchRetriever.class);

    private static final String EXTERNAL_BUSINESS_ERROR_CODE = "813005";

    private static final List<Integer> PROPER_RESPONSE_ERROR_CODES = Arrays.asList(0, 163136, 163135, 163137, 163140, 163141, 163142);


    public F447Resp getInvolvedPartyMatch(List<PostalAddress> postalAddresses, Individual isPlayedBy, RequestHeader requestHeader) throws ResourceNotAvailableErrorMsg, ExternalBusinessErrorMsg, InternalServiceErrorMsg {
        F447Req matchInvolvedPartyRequest = createMatchInvolvedPartyRequest(postalAddresses, isPlayedBy);
        F447Resp response = retrieveF447Response(matchInvolvedPartyRequest, requestHeader);

        if (isExternalBusinessError(response.getF447Result())) {
            throwErrorInResponse(response.getF447Result());
        }
        return response;
    }

    private void throwErrorInResponse(F447Result f447Result) throws ExternalBusinessErrorMsg {
        throw exceptionUtility.externalBusinessError(EXTERNAL_BUSINESS_ERROR_CODE, f447Result.getResultCondition().getReasonCode() + ":" + f447Result.getResultCondition()
                .getReasonText());
    }

    private boolean isExternalBusinessError(F447Result f447Result) {
        if (null != f447Result && null != f447Result.getResultCondition() && null != f447Result.getResultCondition().getReasonCode() && !PROPER_RESPONSE_ERROR_CODES.contains(f447Result.getResultCondition().getReasonCode())) {
            return true;
        }
        return false;
    }


    private F447Resp retrieveF447Response(F447Req matchInvolvedPartyRequest, RequestHeader header) throws ResourceNotAvailableErrorMsg {
        ContactPoint contactPoint = headerRetriever.getContactPoint(header);
        ServiceRequest serviceRequest = headerRetriever.getServiceRequest(header);
        SecurityHeaderType securityHeaderType = headerRetriever.getSecurityHeader(header);
        F447Resp response;
        try {
            response = f447Client.enquirePartyId(matchInvolvedPartyRequest, contactPoint, serviceRequest, securityHeaderType);
        } catch (WebServiceException e) {
            //catching exceptions while calling client to throw ResourceNotAvailableError only in that case
            LOGGER.error("Exception occurred while calling OCIS F447. Returning ResourceNotAvailableError ;", e);
            throw exceptionUtility.resourceNotAvailableError(e.getMessage());
        }
        return response;
    }

    private F447Req createMatchInvolvedPartyRequest(List<PostalAddress> postalAddresses, Individual isPlayedBy) throws InternalServiceErrorMsg {
        return f447RequestFactory.convert(postalAddresses, isPlayedBy, exceptionUtility);
    }
}
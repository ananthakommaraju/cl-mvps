package com.lloydsbanking.salsa.opacc.service.downstream;


import com.lloydsbanking.salsa.downstream.dp.client.encrypt.EncryptClient;
import com.lloydsbanking.salsa.header.gmo.HeaderRetriever;
import com.lloydsbanking.salsa.offer.exception.ExceptionUtility;
import com.lloydsbanking.salsa.soap.encrpyt.objects.EncryptDataRequest;
import com.lloydsbanking.salsa.soap.encrpyt.objects.EncryptDataResponse;
import com.lloydstsb.schema.infrastructure.soap.ContactPoint;
import com.lloydstsb.schema.infrastructure.soap.ServiceRequest;
import lib_sim_gmo.exception.ResourceNotAvailableErrorMsg;
import lib_sim_gmo.messages.RequestHeader;
import org.apache.log4j.Logger;
import org.oasis_open.docs.wss._2004._01.oasis_200401_wss_wssecurity_secext_1_0.SecurityHeaderType;
import org.springframework.beans.factory.annotation.Autowired;

import javax.xml.ws.WebServiceException;

public class EncryptDataRetriever {


    @Autowired
    ExceptionUtility exceptionUtility;

    @Autowired
    HeaderRetriever headerRetriever;

    @Autowired
    EncryptClient encryptClient;

    private static final Logger LOGGER = Logger.getLogger(EncryptDataRetriever.class);

    public EncryptDataResponse retrieveEncryptDataResponse(EncryptDataRequest encryptDataRequest, RequestHeader header) throws ResourceNotAvailableErrorMsg {
        ContactPoint contactPoint = headerRetriever.getContactPoint(header);
        ServiceRequest serviceRequest = headerRetriever.getServiceRequest(header);
        SecurityHeaderType securityHeaderType = headerRetriever.getSecurityHeader(header);
        EncryptDataResponse encryptDataResponse;
        try {
            LOGGER.info("Calling DP Encrypt Data Service");
            encryptDataResponse = encryptClient.retrieveEncryptData(encryptDataRequest, contactPoint, serviceRequest, securityHeaderType);
        } catch (WebServiceException e) {
            //catching exceptions while calling client to throw ResourceNotAvailableError only in that case
            LOGGER.error("Exception occurred while calling DP Encrypt Data Service. Returning ResourceNotAvailableError; ", e);
            throw exceptionUtility.resourceNotAvailableError(e.getMessage());
        }
        return encryptDataResponse;
    }
}

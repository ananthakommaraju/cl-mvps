package com.lloydsbanking.salsa.activate.postfulfil.downstream;

import com.lloydsbanking.salsa.activate.postfulfil.convert.CreateServiceArrangementRequestFactory;
import com.lloydsbanking.salsa.downstream.soa.servicearrangement.client.CSAClient;
import com.lloydsbanking.salsa.header.gmo.HeaderRetriever;
import com.lloydsbanking.salsa.soap.soa.servicearrangement.CreateServiceArrangementRequest;
import com.lloydstsb.schema.infrastructure.soap.ContactPoint;
import com.lloydstsb.schema.infrastructure.soap.ServiceRequest;
import lib_sim_bo.businessobjects.ProductArrangement;
import lib_sim_gmo.messages.RequestHeader;
import org.apache.log4j.Logger;
import org.oasis_open.docs.wss._2004._01.oasis_200401_wss_wssecurity_secext_1_0.SecurityHeaderType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class ActivateBenefitArrangement {
    private static final Logger LOGGER = Logger.getLogger(ActivateBenefitArrangement.class);
    private static final String ACTIVATE_BENEFIT_SERVICE_REQUEST_ACTION = "ActivateBenefitArrangement";
    private static final String ACTIVATE_BENEFIT_SERVICE_REQUEST_SERVICE_NAME = "http://www.lloydstsb.com/Schema/Enterprise/LCSM_ArrangementNegotiation/activateBenefitArrangement";
    @Autowired
    CreateServiceArrangementRequestFactory requestFactory;
    @Autowired
    CSAClient csaClient;
    @Autowired
    HeaderRetriever headerRetriever;

    public void activateArrangement(ProductArrangement productArrangement, RequestHeader requestHeader, String benefitLookupDesc) {
        LOGGER.info("Entering ActivateBenefitArrangement");
        CreateServiceArrangementRequest createServiceArrangementRequest = requestFactory.convert(productArrangement, benefitLookupDesc);
        try {
            activate(requestHeader, createServiceArrangementRequest);
        } catch (IllegalStateException e) {
            LOGGER.info("Exception occurred while calling createServiceArrangement. Returning ResourceNotAvailable Error ", e);
        }
    }

    private void activate(RequestHeader requestHeader, CreateServiceArrangementRequest createServiceArrangementRequest) {
        ContactPoint contactPoint = headerRetriever.getContactPoint(requestHeader.getLloydsHeaders());
        SecurityHeaderType securityHeaderType = headerRetriever.getSecurityHeader(requestHeader.getLloydsHeaders());
        ServiceRequest serviceRequest = headerRetriever.getServiceRequest(requestHeader.getLloydsHeaders(), ACTIVATE_BENEFIT_SERVICE_REQUEST_SERVICE_NAME, ACTIVATE_BENEFIT_SERVICE_REQUEST_ACTION);
        csaClient.createServiceArrangement(createServiceArrangementRequest, contactPoint, serviceRequest, securityHeaderType);
    }
}

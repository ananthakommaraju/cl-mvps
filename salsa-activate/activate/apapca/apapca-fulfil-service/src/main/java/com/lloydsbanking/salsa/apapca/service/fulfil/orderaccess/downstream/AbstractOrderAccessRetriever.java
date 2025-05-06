package com.lloydsbanking.salsa.apapca.service.fulfil.orderaccess.downstream;

import com.lloydsbanking.salsa.activate.utility.ExceptionUtilityActivate;
import com.lloydsbanking.salsa.header.gmo.HeaderRetriever;
import com.lloydstsb.schema.infrastructure.soap.ContactPoint;
import com.lloydstsb.schema.infrastructure.soap.ServiceRequest;
import lib_sim_gmo.messages.RequestHeader;
import lib_sim_salesprocessmanagement.ia_activateproductarrangement.ActivateProductArrangementExternalSystemErrorMsg;
import lib_sim_salesprocessmanagement.ia_activateproductarrangement.ActivateProductArrangementResourceNotAvailableErrorMsg;
import org.apache.log4j.Logger;
import org.oasis_open.docs.wss._2004._01.oasis_200401_wss_wssecurity_secext_1_0.SecurityHeaderType;
import org.springframework.beans.factory.annotation.Autowired;

import javax.xml.ws.WebServiceException;

public abstract class AbstractOrderAccessRetriever {

    private static final Logger LOGGER = Logger.getLogger(AbstractOrderAccessRetriever.class);

    @Autowired
    HeaderRetriever headerRetriever;

    @Autowired
    ExceptionUtilityActivate exceptionUtilityActivate;

    public <I, O> O getResponse(I request, RequestHeader header)
            throws ActivateProductArrangementResourceNotAvailableErrorMsg, ActivateProductArrangementExternalSystemErrorMsg {
        O response;
        try {
            ContactPoint contactPoint = headerRetriever.getContactPoint(header.getLloydsHeaders());
            ServiceRequest serviceRequest = headerRetriever.getServiceRequest(header.getLloydsHeaders(),
                    getServiceName(), getApiName());
            SecurityHeaderType securityHeaderType = headerRetriever.getSecurityHeader(header.getLloydsHeaders());
            response = callClient(request, contactPoint, serviceRequest, securityHeaderType);
            checkReasonCode(response, header);
        } catch (WebServiceException e) {
            LOGGER.error("Exception occurred while calling " + getApiName() + ". Returning ResourceNotAvailable Error ", e);
            throw exceptionUtilityActivate.resourceNotAvailableError(header, e.getMessage());
        }
        return response;
    }

    protected abstract <O> void checkReasonCode(O response, RequestHeader header) throws ActivateProductArrangementExternalSystemErrorMsg;

    protected abstract String getServiceName();

    protected abstract String getApiName();

    protected abstract <I, O> O callClient(I request, ContactPoint contactPoint, ServiceRequest serviceRequest,
                                           SecurityHeaderType securityHeaderType);

    protected abstract <O> Integer getReasonCode(O response);
}

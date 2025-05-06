package com.lloydsbanking.salsa.opasaving.service.downstream;

import com.lloydsbanking.salsa.downstream.soa.client.determinecustomerproductcondition.DCPCClient;
import com.lloydsbanking.salsa.header.gmo.HeaderRetriever;
import com.lloydsbanking.salsa.offer.exception.ExceptionUtility;
import com.lloydsbanking.xml.schema.enterprise.lcsm5.services.productcustomermatching.v2.productcustomermatching.serviceparameters.iproductcustomermatching.DetermineCustomerProductConditionsRequest;
import com.lloydsbanking.xml.schema.enterprise.lcsm5.services.productcustomermatching.v2.productcustomermatching.serviceparameters.iproductcustomermatching.DetermineCustomerProductConditionsResponse;
import com.lloydstsb.schema.infrastructure.soap.ContactPoint;
import com.lloydstsb.schema.infrastructure.soap.ServiceRequest;
import lib_sim_gmo.exception.ResourceNotAvailableErrorMsg;
import lib_sim_gmo.messages.RequestHeader;
import org.apache.log4j.Logger;
import org.oasis_open.docs.wss._2004._01.oasis_200401_wss_wssecurity_secext_1_0.SecurityHeaderType;
import org.springframework.beans.factory.annotation.Autowired;

import javax.xml.ws.WebServiceException;

public class DCPCServiceClient {

    private static final Logger LOGGER = Logger.getLogger(DCPCServiceClient.class);
    private static final String DCPC_SERVICE_NAME = "determineCustomerProductConditions";

    @Autowired
    ExceptionUtility exceptionUtility;
    @Autowired
    HeaderRetriever headerRetriever;
    @Autowired
    DCPCClient dcpcClient;

    public DetermineCustomerProductConditionsResponse retrieveDCPCResponse(DetermineCustomerProductConditionsRequest dcpcRequest, RequestHeader header) throws ResourceNotAvailableErrorMsg {
        ContactPoint contactPoint = headerRetriever.getContactPoint(header);
        ServiceRequest serviceRequest = headerRetriever.getServiceRequest(header);
        String serviceName = serviceRequest.getServiceName();
        String action = serviceRequest.getAction();
        serviceRequest.setServiceName(DCPC_SERVICE_NAME);
        serviceRequest.setAction(DCPC_SERVICE_NAME);
        SecurityHeaderType securityHeaderType = headerRetriever.getSecurityHeader(header);
        DetermineCustomerProductConditionsResponse dcpcResponse;
        try {
            LOGGER.info("Calling DetermineCustomerProductCondition service");
            dcpcResponse = dcpcClient.determineCustomerProductCondition(dcpcRequest, contactPoint, serviceRequest, securityHeaderType);
        } catch (WebServiceException e) {
            //catching exceptions while calling client to throw ResourceNotAvailableError only in that case
            LOGGER.error("Exception occurred while calling Determine Customer Product Condition. Returning ResourceNotAvailableError ;", e);
            serviceRequest.setServiceName(serviceName);
            serviceRequest.setAction(action);
            throw exceptionUtility.resourceNotAvailableError(e.getMessage());
        }
        serviceRequest.setServiceName(serviceName);
        serviceRequest.setAction(action);
        return dcpcResponse;
    }
}



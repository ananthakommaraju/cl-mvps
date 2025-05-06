package com.lloydsbanking.salsa.apapca.service.fulfil.orderaccess.downstream;

import com.lloydsbanking.salsa.downstream.cmas.client.c846.C846Client;
import com.lloydsbanking.salsa.soap.cmas.c846.objects.C846Req;
import com.lloydsbanking.salsa.soap.cmas.c846.objects.C846Resp;
import com.lloydsbanking.salsa.soap.cmas.c846.objects.C846Result;
import com.lloydstsb.schema.infrastructure.soap.ContactPoint;
import com.lloydstsb.schema.infrastructure.soap.ServiceRequest;
import lib_sim_gmo.messages.RequestHeader;
import lib_sim_salesprocessmanagement.ia_activateproductarrangement.ActivateProductArrangementExternalSystemErrorMsg;
import org.apache.log4j.Logger;
import org.oasis_open.docs.wss._2004._01.oasis_200401_wss_wssecurity_secext_1_0.SecurityHeaderType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class RetrieveEligibleCardsRetriver extends AbstractOrderAccessRetriever {
    private static final Logger LOGGER = Logger.getLogger(InitiateCardOrderRetriever.class);

    @Autowired
    C846Client c846Client;

    @Override
    @SuppressWarnings(value = "unchecked")
    protected <I, O> O callClient(I request, ContactPoint contactPoint, ServiceRequest serviceRequest, SecurityHeaderType securityHeaderType) {
        return (O) c846Client.retrieveEligibleCards((C846Req) request, contactPoint, serviceRequest, securityHeaderType);
    }

    @Override
    protected <O> Integer getReasonCode(O response) {
        C846Result c846Result = ((C846Resp) response).getC846Result();
        if (c846Result != null && c846Result.getResultCondition() != null && c846Result.getResultCondition().getReasonCode() != null) {
            return c846Result.getResultCondition().getReasonCode();
        }
        return null;
    }

    @Override
    protected String getApiName() {
        return "C846";
    }

    @Override
    protected String getServiceName() {
        return "http://xml.lloydsbanking.com/Schema/Enterprise/ProductManufacturing/CMAS/C846_BroCardTypePlasticType";
    }

    @Override
    protected <O> void checkReasonCode(O response, RequestHeader header) throws ActivateProductArrangementExternalSystemErrorMsg {
        Integer reasonCode = getReasonCode(response);
        if (null != reasonCode) {
            if (C846ErrorCodes.isExternalServiceErrorForC846(reasonCode)) {
                LOGGER.error("Exception occurred while calling " + getApiName() + ". Returning ExternalService error " + reasonCode, null);
                throw exceptionUtilityActivate.externalServiceError(header, null, String.valueOf(reasonCode));
            }
        }
    }
}

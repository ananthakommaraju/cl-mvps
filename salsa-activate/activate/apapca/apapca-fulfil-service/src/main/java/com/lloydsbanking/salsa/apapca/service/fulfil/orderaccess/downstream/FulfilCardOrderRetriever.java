package com.lloydsbanking.salsa.apapca.service.fulfil.orderaccess.downstream;

import com.lloydsbanking.salsa.downstream.cmas.client.c818.C818Client;
import com.lloydsbanking.salsa.soap.cmas.c818.objects.C818Req;
import com.lloydsbanking.salsa.soap.cmas.c818.objects.C818Resp;
import com.lloydsbanking.salsa.soap.cmas.c818.objects.C818Result;
import com.lloydstsb.schema.infrastructure.soap.ContactPoint;
import com.lloydstsb.schema.infrastructure.soap.ServiceRequest;
import lib_sim_gmo.messages.RequestHeader;
import lib_sim_salesprocessmanagement.ia_activateproductarrangement.ActivateProductArrangementExternalSystemErrorMsg;
import org.apache.log4j.Logger;
import org.oasis_open.docs.wss._2004._01.oasis_200401_wss_wssecurity_secext_1_0.SecurityHeaderType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class FulfilCardOrderRetriever extends AbstractOrderAccessRetriever {
    private static final Logger LOGGER = Logger.getLogger(AbstractOrderAccessRetriever.class);

    @Autowired
    C818Client c818Client;

    @Override
    @SuppressWarnings(value = "unchecked")
    protected <I, O> O callClient(I request, ContactPoint contactPoint, ServiceRequest serviceRequest, SecurityHeaderType securityHeaderType) {
        return (O) c818Client.fulfilCardOrder((C818Req) request, contactPoint, serviceRequest, securityHeaderType);
    }

    @Override
    protected <O> Integer getReasonCode(O response) {
        C818Result c818Result = ((C818Resp) response).getC818Result();
        if (c818Result != null && c818Result.getResultCondition() != null && c818Result.getResultCondition().getReasonCode() != null) {
            return c818Result.getResultCondition().getReasonCode();
        }
        return null;
    }

    @Override
    protected String getApiName() {
        return "C818";
    }

    @Override
    protected String getServiceName() {
        return "http://www.lloydstsb.com/Schema/Personal/CreditCardPlatform/CMAS/C818_AddCardOrder";
    }

    @Override
    protected <O> void checkReasonCode(O response, RequestHeader header) throws ActivateProductArrangementExternalSystemErrorMsg {
        Integer reasonCode = getReasonCode(response);
        if (null != reasonCode) {
            if (C818ErrorCodes.isExternalServiceErrorForC818(reasonCode)) {
                LOGGER.error("Exception occurred while calling " + getApiName() + ". Returning ExternalService error " + reasonCode, null);
                throw exceptionUtilityActivate.externalServiceError(header, null, String.valueOf(reasonCode));
            }
        }
    }
}

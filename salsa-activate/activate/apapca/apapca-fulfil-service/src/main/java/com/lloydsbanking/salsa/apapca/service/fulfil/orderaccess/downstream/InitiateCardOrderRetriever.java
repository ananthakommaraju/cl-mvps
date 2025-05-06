package com.lloydsbanking.salsa.apapca.service.fulfil.orderaccess.downstream;

import com.lloydsbanking.salsa.downstream.cmas.client.c808.C808Client;
import com.lloydsbanking.salsa.soap.cmas.c808.objects.C808Req;
import com.lloydsbanking.salsa.soap.cmas.c808.objects.C808Resp;
import com.lloydsbanking.salsa.soap.cmas.c808.objects.C808Result;
import com.lloydstsb.schema.infrastructure.soap.ContactPoint;
import com.lloydstsb.schema.infrastructure.soap.ServiceRequest;
import lib_sim_gmo.messages.RequestHeader;
import lib_sim_salesprocessmanagement.ia_activateproductarrangement.ActivateProductArrangementExternalSystemErrorMsg;
import org.apache.log4j.Logger;
import org.oasis_open.docs.wss._2004._01.oasis_200401_wss_wssecurity_secext_1_0.SecurityHeaderType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class InitiateCardOrderRetriever extends AbstractOrderAccessRetriever {
    private static final Logger LOGGER = Logger.getLogger(InitiateCardOrderRetriever.class);

    @Autowired
    C808Client c808Client;

    @Override
    @SuppressWarnings(value = "unchecked")
    protected <I, O> O callClient(I request, ContactPoint contactPoint, ServiceRequest serviceRequest, SecurityHeaderType securityHeaderType) {
        return (O) c808Client.initiateCardOrder((C808Req) request, contactPoint, serviceRequest, securityHeaderType);
    }

    @Override
    protected <O> Integer getReasonCode(O response) {
        C808Result c808Result = ((C808Resp) response).getC808Result();
        if (c808Result != null && c808Result.getResultCondition() != null && c808Result.getResultCondition().getReasonCode() != null) {
            return c808Result.getResultCondition().getReasonCode();
        }
        return null;
    }

    @Override
    protected String getApiName() {
        return "C808";
    }

    @Override
    protected String getServiceName() {
        return "http://www.lloydstsb.com/Schema/Personal/CreditCardPlatform/CMAS/C808_EnqInitNewCrdOrd";
    }

    @Override
    protected <O> void checkReasonCode(O response, RequestHeader header) throws ActivateProductArrangementExternalSystemErrorMsg {
        Integer reasonCode = getReasonCode(response);
        if (null != reasonCode) {
            if (C808ErrorCodes.isExternalErrorForC808(reasonCode)) {
                LOGGER.error("Exception occurred while calling " + getApiName() + ". Returning ExternalService error " + reasonCode, null);
                throw exceptionUtilityActivate.externalServiceError(header, null, String.valueOf(reasonCode));
            }
        }
    }

}

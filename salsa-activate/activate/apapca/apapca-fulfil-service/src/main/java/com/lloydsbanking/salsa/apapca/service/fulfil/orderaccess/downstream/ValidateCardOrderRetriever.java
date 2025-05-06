package com.lloydsbanking.salsa.apapca.service.fulfil.orderaccess.downstream;

import com.lloydsbanking.salsa.downstream.cmas.client.c812.C812Client;
import com.lloydsbanking.salsa.soap.cmas.c812.objects.C812Req;
import com.lloydsbanking.salsa.soap.cmas.c812.objects.C812Resp;
import com.lloydsbanking.salsa.soap.cmas.c812.objects.C812Result;
import com.lloydstsb.schema.infrastructure.soap.ContactPoint;
import com.lloydstsb.schema.infrastructure.soap.ServiceRequest;
import lib_sim_gmo.messages.RequestHeader;
import lib_sim_salesprocessmanagement.ia_activateproductarrangement.ActivateProductArrangementExternalSystemErrorMsg;
import org.apache.log4j.Logger;
import org.oasis_open.docs.wss._2004._01.oasis_200401_wss_wssecurity_secext_1_0.SecurityHeaderType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class ValidateCardOrderRetriever extends AbstractOrderAccessRetriever {

    private static final Logger LOGGER = Logger.getLogger(ValidateCardOrderRetriever.class);

    @Autowired
    C812Client c812Client;

    @Override
    @SuppressWarnings(value="unchecked")
    protected <I,O> O callClient(I request, ContactPoint contactPoint, ServiceRequest serviceRequest, SecurityHeaderType securityHeaderType) {
        return (O) c812Client.validateCardOrder((C812Req) request, contactPoint, serviceRequest, securityHeaderType);
    }

    @Override
    protected <O> Integer getReasonCode(O response) {
        C812Result c812Result = ((C812Resp) response).getC812Result();
        if (c812Result != null && c812Result.getResultCondition() != null && c812Result.getResultCondition().getReasonCode() != null) {
            return c812Result.getResultCondition().getReasonCode();
        }
        return null;
    }

    @Override
    protected String getApiName() {
        return "C812";
    }


    @Override
    protected String getServiceName() {
        return "http://www.lloydstsb.com/Schema/Personal/CreditCardPlatform/CMAS/C808_EnqInitNewCrdOrd";
    }

    @Override
    protected <O> void checkReasonCode(O response, RequestHeader header) throws ActivateProductArrangementExternalSystemErrorMsg {
        Integer reasonCode = getReasonCode(response);
        if (null != reasonCode) {
            if (C812ErrorCodes.isExternalServiceErrorForC812(reasonCode)) {
                LOGGER.error("Exception occurred while calling " + getApiName() + ". Returning ExternalService error " + reasonCode, null);
                throw exceptionUtilityActivate.externalServiceError(header, null, String.valueOf(reasonCode));
            }
        }
    }
}

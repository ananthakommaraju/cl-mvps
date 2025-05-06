package com.lloydsbanking.salsa.activate.postfulfil.downstream;

import com.lloydsbanking.salsa.activate.constants.ActivateCommonConstant;
import com.lloydsbanking.salsa.activate.helper.ApplicationDetails;
import com.lloydsbanking.salsa.activate.helper.UpdateDepositArrangementConditionAndApplicationStatusHelper;
import com.lloydsbanking.salsa.activate.postfulfil.convert.C658RequestFactory;
import com.lloydsbanking.salsa.activate.postfulfil.rules.C658ErrorSet;
import com.lloydsbanking.salsa.constant.ApplicationStatus;
import com.lloydsbanking.salsa.downstream.ocis.client.c658.C658Client;
import com.lloydsbanking.salsa.header.gmo.HeaderRetriever;
import com.lloydsbanking.salsa.soap.ocis.c658.objects.C658Req;
import com.lloydsbanking.salsa.soap.ocis.c658.objects.C658Resp;
import com.lloydsbanking.salsa.soap.ocis.c658.objects.C658Result;
import com.lloydstsb.schema.infrastructure.soap.ContactPoint;
import com.lloydstsb.schema.infrastructure.soap.ServiceRequest;
import lib_sim_bo.businessobjects.ProductArrangement;
import lib_sim_gmo.messages.RequestHeader;
import org.apache.log4j.Logger;
import org.oasis_open.docs.wss._2004._01.oasis_200401_wss_wssecurity_secext_1_0.SecurityHeaderType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.xml.ws.WebServiceException;

@Repository
public class UpdateEmailAddress {
    private static final Logger LOGGER = Logger.getLogger(UpdateEmailAddress.class);

    @Autowired
    C658RequestFactory c658RequestFactory;

    @Autowired
    C658Client c658Client;

    @Autowired
    HeaderRetriever headerRetriever;

    @Autowired
    C658ErrorSet c658ErrorSet;

    @Autowired
    UpdateDepositArrangementConditionAndApplicationStatusHelper updateAppDetails;

    private static final String SERVICE_NAME = "http://www.lloydstsb.com/Schema/Personal/RetailParty/OCIS/C658_ChaPtyTelecomPartner";

    private static final String SERVICE_ACTION = "C658";

    public void updateEmail(ProductArrangement productArrangement, RequestHeader requestHeader, ApplicationDetails applicationDetails) {
        LOGGER.info("Entering UpdateEmailAddress OCIS(C658)");
        C658Req c658Req = c658RequestFactory.convert(productArrangement);
        C658Resp c658Resp;
        try {
            c658Resp = getC658Resp(c658Req, requestHeader);
            C658Result c658Result = c658Resp.getC658Result();
            boolean isErrorScenario = checkErrorScenarios(c658Result);
            if (isErrorScenario) {
                updateAppDetails.setApplicationDetails(productArrangement.getRetryCount(), null,null,ApplicationStatus.AWAITING_FULFILMENT.getValue(), ActivateCommonConstant.AppSubStatus.FAILED_TO_UPDATE_EMAIL_ADDRESS, applicationDetails);
            }
        } catch (WebServiceException e) {
            LOGGER.info("Exception while calling C658 for updateEmailAddress " + e);
            updateAppDetails.setApplicationDetails(productArrangement.getRetryCount(),null,null, ApplicationStatus.AWAITING_FULFILMENT.getValue(), ActivateCommonConstant.AppSubStatus.FAILED_TO_UPDATE_EMAIL_ADDRESS, applicationDetails);
        }
        productArrangement.setApplicationSubStatus(applicationDetails.getApplicationSubStatus());
        LOGGER.info("Exiting UpdateEmailAddress OCIS(C658)");
    }

    private C658Resp getC658Resp(C658Req c658Req, RequestHeader requestHeader) {
        ContactPoint contactPoint = headerRetriever.getContactPoint(requestHeader.getLloydsHeaders());
        SecurityHeaderType securityHeaderType = headerRetriever.getSecurityHeader(requestHeader.getLloydsHeaders());
        ServiceRequest serviceRequest = headerRetriever.getServiceRequest(requestHeader.getLloydsHeaders(),SERVICE_NAME,SERVICE_ACTION);
        return c658Client.c658(c658Req, contactPoint, serviceRequest, securityHeaderType);
    }

    private boolean checkErrorScenarios(C658Result c658Result) {
        boolean isErrorScenario = false;
        if (c658Result != null && c658Result.getResultCondition() != null && c658Result.getResultCondition().getReasonCode() != null) {
            int reasonCode = c658Result.getResultCondition().getReasonCode().intValue();
            String reasonText = c658Result.getResultCondition().getReasonText();
            if (c658ErrorSet.isExternalServiceError(reasonCode)) {
                isErrorScenario = true;
                LOGGER.info("C658 :External Service Error. ErrorCode | ErrorReason: " + reasonCode + " | " + reasonText);
            }
        }
        return isErrorScenario;
    }

}


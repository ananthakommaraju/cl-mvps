package com.lloydsbanking.salsa.apacc.service.fulfil.downstream;


import com.lloydsbanking.salsa.activate.constants.ActivateCommonConstant;
import com.lloydsbanking.salsa.activate.helper.ApplicationDetails;
import com.lloydsbanking.salsa.activate.helper.UpdateDepositArrangementConditionAndApplicationStatusHelper;
import com.lloydsbanking.salsa.apacc.service.fulfil.convert.F241V1RequestFactory;
import com.lloydsbanking.salsa.constant.ApplicationStatus;
import com.lloydsbanking.salsa.downstream.fdi.client.f241v1.F241V1Client;
import com.lloydsbanking.salsa.header.gmo.HeaderRetriever;
import com.lloydsbanking.salsa.soap.fdi.f241V1.objects.F241Req;
import com.lloydsbanking.salsa.soap.fdi.f241V1.objects.F241Resp;
import com.lloydstsb.schema.infrastructure.soap.ContactPoint;
import com.lloydstsb.schema.infrastructure.soap.ServiceRequest;
import lib_sim_bo.businessobjects.FinanceServiceArrangement;
import lib_sim_gmo.messages.RequestHeader;
import org.apache.log4j.Logger;
import org.oasis_open.docs.wss._2004._01.oasis_200401_wss_wssecurity_secext_1_0.SecurityHeaderType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.xml.ws.WebServiceException;

@Repository
public class CreateCreditCardAccountV1Retriever {
    private static final Logger LOGGER = Logger.getLogger(CreateCreditCardAccountRetriever.class);
    private static final String F241_V1_SERVICE_NAME = "http://www.lloydstsb.com/Schema/Personal/CreditCardPlatform/FDI";
    private static final String F241_V1_ACTION_NAME = "F241V1.1";
    private static final String F241_V1_FAILURE_REASON_CODE = "003";
    private static final String F241_V1_FAILURE_REASON_TEXT = "Failed to create card account on FDI V1";

    @Autowired
    F241V1RequestFactory f241RequestFactory;
    @Autowired
    F241V1Client f241V1Client;
    @Autowired
    HeaderRetriever headerRetriever;
    @Autowired
    UpdateDepositArrangementConditionAndApplicationStatusHelper applicationStatusHelper;

    public F241Resp createCreditCardAccount(FinanceServiceArrangement financeServiceArrangement, RequestHeader requestHeader, ApplicationDetails applicationDetails) {
        F241Resp f241Resp = null;
        F241Req f241Req = f241RequestFactory.convert(financeServiceArrangement);
        try {
            f241Resp = getF241Resp(requestHeader, f241Req);
            setApplicationDetailsForError(financeServiceArrangement.getRetryCount(), f241Resp, applicationDetails);
        } catch (WebServiceException e) {
            LOGGER.info("Exception while calling F241V1 for CreateCreditCardAccountRetriever " + e);
            applicationStatusHelper.setApplicationDetails(financeServiceArrangement.getRetryCount(), F241_V1_FAILURE_REASON_CODE, F241_V1_FAILURE_REASON_TEXT, ApplicationStatus.AWAITING_FULFILMENT.getValue(), ActivateCommonConstant.AppSubStatus.CARD_CREATION_FAILURE, applicationDetails);
        }
        financeServiceArrangement.setApplicationSubStatus(applicationDetails.getApplicationSubStatus());
        return f241Resp;
    }

    private F241Resp getF241Resp(RequestHeader requestHeader, F241Req f241Req) {
        ContactPoint contactPoint = headerRetriever.getContactPoint(requestHeader.getLloydsHeaders());
        SecurityHeaderType securityHeaderType = headerRetriever.getSecurityHeader(requestHeader.getLloydsHeaders());
        ServiceRequest serviceRequest = headerRetriever.getServiceRequest(requestHeader.getLloydsHeaders(), F241_V1_SERVICE_NAME, F241_V1_ACTION_NAME);
        return f241V1Client.createCardAccountV1(f241Req, contactPoint, serviceRequest, securityHeaderType);
    }

    private void setApplicationDetailsForError(Integer retryCount, F241Resp f241Resp, ApplicationDetails applicationDetails) {
        if (f241Resp.getF241Result() != null && f241Resp.getF241Result().getResultCondition() != null) {
            if (f241Resp.getF241Result().getResultCondition().getSeverityCode() > 1) {
                LOGGER.info("Exception occurred while calling F241V1, Severity Code:" + f241Resp.getF241Result().getResultCondition().getSeverityCode());
                applicationStatusHelper.setApplicationDetails(retryCount, F241_V1_FAILURE_REASON_CODE, F241_V1_FAILURE_REASON_TEXT, ApplicationStatus.AWAITING_FULFILMENT.getValue(), ActivateCommonConstant.AppSubStatus.CARD_CREATION_FAILURE, applicationDetails);
            }
        }
    }

}

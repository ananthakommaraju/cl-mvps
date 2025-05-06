package com.lloydsbanking.salsa.apacc.service.fulfil.downstream;

import com.lloydsbanking.salsa.activate.constants.ActivateCommonConstant;
import com.lloydsbanking.salsa.activate.helper.ApplicationDetails;
import com.lloydsbanking.salsa.activate.helper.UpdateDepositArrangementConditionAndApplicationStatusHelper;
import com.lloydsbanking.salsa.apacc.service.fulfil.convert.F241RequestFactory;
import com.lloydsbanking.salsa.apacc.service.fulfil.rules.F241ErrorCodes;
import com.lloydsbanking.salsa.constant.ApplicationStatus;
import com.lloydsbanking.salsa.downstream.fdi.client.f241.F241Client;
import com.lloydsbanking.salsa.header.gmo.HeaderRetriever;
import com.lloydsbanking.salsa.soap.fdi.f241.objects.F241Req;
import com.lloydsbanking.salsa.soap.fdi.f241.objects.F241Resp;
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
public class CreateCreditCardAccountRetriever {

    private static final Logger LOGGER = Logger.getLogger(CreateCreditCardAccountRetriever.class);
    private static final String F241_SERVICE_NAME = "http://www.lloydstsb.com/Schema/Personal/CreditCardPlatform/FDI";
    private static final String F241_ACTION_NAME = "F241";
    private static String F241_V1_REASON_CODE = "005";
    private static String F241_V1_REASON_TEXT = "Failed to add Additional Card Holder to the card account";

    @Autowired
    F241RequestFactory f241RequestFactory;
    @Autowired
    F241Client f241Client;
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
            LOGGER.info("Exception while calling F241 for CreateCreditCardAccountRetriever " + e);
            applicationStatusHelper.setApplicationDetails(financeServiceArrangement.getRetryCount(), F241_V1_REASON_CODE, F241_V1_REASON_TEXT, ApplicationStatus.AWAITING_FULFILMENT.getValue(), ActivateCommonConstant.AppSubStatus.ADD_CARD_HOLDER_FAILURE, applicationDetails);
        }
        financeServiceArrangement.setApplicationSubStatus(applicationDetails.getApplicationSubStatus());
        return f241Resp;
    }

    private F241Resp getF241Resp(RequestHeader requestHeader, F241Req f241Req) {
        ContactPoint contactPoint = headerRetriever.getContactPoint(requestHeader.getLloydsHeaders());
        SecurityHeaderType securityHeaderType = headerRetriever.getSecurityHeader(requestHeader.getLloydsHeaders());
        ServiceRequest serviceRequest = headerRetriever.getServiceRequest(requestHeader.getLloydsHeaders(), F241_SERVICE_NAME, F241_ACTION_NAME);
        return f241Client.createCardAccount(f241Req, contactPoint, serviceRequest, securityHeaderType);
    }

    private void setApplicationDetailsForError(Integer retryCount, F241Resp f241Resp, ApplicationDetails applicationDetails) {
        if (f241Resp.getF241Result() != null && f241Resp.getF241Result().getResultCondition() != null) {
            if (f241Resp.getF241Result().getResultCondition().getSeverityCode() > 1) {
                LOGGER.info("Exception occurred while calling F241, Severity Code:" + f241Resp.getF241Result().getResultCondition().getSeverityCode());
                applicationStatusHelper.setApplicationDetails(retryCount, F241_V1_REASON_CODE, F241_V1_REASON_TEXT, ApplicationStatus.AWAITING_FULFILMENT.getValue(), ActivateCommonConstant.AppSubStatus.ADD_CARD_HOLDER_FAILURE, applicationDetails);
            } else if (f241Resp.getF241Result().getResultCondition().getReasonCode() != null) {
                if (F241ErrorCodes.isErrorForF241(f241Resp.getF241Result().getResultCondition().getReasonCode())) {
                    LOGGER.info("Exception occurred while calling F241, Reason Code:" + f241Resp.getF241Result().getResultCondition().getReasonCode());
                    applicationStatusHelper.setApplicationDetails(retryCount, F241_V1_REASON_CODE, F241_V1_REASON_TEXT, ApplicationStatus.AWAITING_FULFILMENT.getValue(), ActivateCommonConstant.AppSubStatus.ADD_CARD_HOLDER_FAILURE, applicationDetails);
                }

            }
        }
    }
}

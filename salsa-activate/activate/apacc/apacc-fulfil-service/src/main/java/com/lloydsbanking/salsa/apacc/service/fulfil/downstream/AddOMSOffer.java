package com.lloydsbanking.salsa.apacc.service.fulfil.downstream;


import com.lloydsbanking.salsa.activate.constants.ActivateCommonConstant;
import com.lloydsbanking.salsa.activate.helper.ApplicationDetails;
import com.lloydsbanking.salsa.activate.helper.UpdateDepositArrangementConditionAndApplicationStatusHelper;
import com.lloydsbanking.salsa.activate.utility.ExceptionUtilityActivate;
import com.lloydsbanking.salsa.apacc.service.fulfil.convert.F251RequestFactory;
import com.lloydsbanking.salsa.apacc.service.fulfil.rules.F251ErrorCodes;
import com.lloydsbanking.salsa.constant.ApplicationStatus;
import com.lloydsbanking.salsa.downstream.fdi.client.f251.F251Client;
import com.lloydsbanking.salsa.header.gmo.HeaderRetriever;
import com.lloydsbanking.salsa.logging.application.ProductArrangementTraceLog;
import com.lloydsbanking.salsa.soap.fdi.f251.objects.F251Req;
import com.lloydsbanking.salsa.soap.fdi.f251.objects.F251Resp;
import com.lloydstsb.schema.infrastructure.soap.ContactPoint;
import com.lloydstsb.schema.infrastructure.soap.ServiceRequest;
import lib_sim_bo.businessobjects.FinanceServiceArrangement;
import lib_sim_gmo.messages.RequestHeader;
import lib_sim_salesprocessmanagement.ia_activateproductarrangement.ActivateProductArrangementExternalSystemErrorMsg;
import lib_sim_salesprocessmanagement.ia_activateproductarrangement.ActivateProductArrangementResourceNotAvailableErrorMsg;
import org.apache.log4j.Logger;
import org.oasis_open.docs.wss._2004._01.oasis_200401_wss_wssecurity_secext_1_0.SecurityHeaderType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.xml.ws.WebServiceException;

@Repository
public class AddOMSOffer {
    private static final Logger LOGGER = Logger.getLogger(AddOMSOffer.class);
    private static final String F251_SERVICE_NAME = "http://www.lloydstsb.com/Schema/Personal/RetailParty/VisionPlus/F251_AddOMSOfferDriverPartner";
    private static final String F251_ACTION_NAME = "F251";

    private static final String OMS_OFFERS_FAILED_REASON_CODE = "004";

    private static final String OMS_OFFERS_FAILED_REASON_TEXT = "Failed to add OMS Offers to product on OCIS";

    @Autowired
    F251RequestFactory f251RequestFactory;
    @Autowired
    HeaderRetriever headerRetriever;
    @Autowired
    F251Client f251Client;
    @Autowired
    UpdateDepositArrangementConditionAndApplicationStatusHelper applicationStatusHelper;
    @Autowired
    ExceptionUtilityActivate exceptionUtilityActivate;
    @Autowired
    ProductArrangementTraceLog productArrangementTraceLog;

    public boolean addOMSOffers(FinanceServiceArrangement financeServiceArrangement, RequestHeader requestHeader, ApplicationDetails applicationDetails) {
        try {
            return addOMSDetailsCall(financeServiceArrangement, requestHeader);
        } catch (ActivateProductArrangementResourceNotAvailableErrorMsg resourceNotAvailableErrorMsg) {
            LOGGER.info("ResourceNotAvailableError while adding OMS Offers. This exception will be consumed " + resourceNotAvailableErrorMsg);
            return true;
        } catch (ActivateProductArrangementExternalSystemErrorMsg externalSystemErrorMsg) {
            LOGGER.info("External System Error while adding OMS Offers. This exception will be consumed " + externalSystemErrorMsg);
            applicationStatusHelper.setApplicationDetails(financeServiceArrangement.getRetryCount(), OMS_OFFERS_FAILED_REASON_CODE, OMS_OFFERS_FAILED_REASON_TEXT, ApplicationStatus.AWAITING_FULFILMENT.getValue(), ActivateCommonConstant.AppSubStatus.ADD_OMS_OFFERS, applicationDetails);
            financeServiceArrangement.setApplicationSubStatus(applicationDetails.getApplicationSubStatus());
            return false;
        }
    }

    private boolean addOMSDetailsCall(FinanceServiceArrangement financeServiceArrangement, RequestHeader requestHeader) throws ActivateProductArrangementResourceNotAvailableErrorMsg, ActivateProductArrangementExternalSystemErrorMsg {
        LOGGER.info(productArrangementTraceLog.getApplicationTraceEventMessage(financeServiceArrangement, "Entering AddOMSOffers(FDI F251)"));
        ContactPoint contactPoint = headerRetriever.getContactPoint(requestHeader.getLloydsHeaders());
        F251Req f251Req = f251RequestFactory.convert(financeServiceArrangement, contactPoint.getContactPointId());
        try {
            F251Resp f251Resp = getF251Resp(requestHeader, f251Req, contactPoint);
            getApplicationDetailsForError(f251Resp, requestHeader);
        } catch (WebServiceException e) {
            LOGGER.error("Exception occurred(ResourceNotAvailableError) while calling F251 ", e);
            throw exceptionUtilityActivate.resourceNotAvailableError(requestHeader, "Exception occurred(ResourceNotAvailableError) while calling F251");
        }
        return true;
    }

    private void getApplicationDetailsForError(F251Resp f251Resp, RequestHeader requestHeader) throws ActivateProductArrangementExternalSystemErrorMsg {
        if (f251Resp != null && f251Resp.getF251Result() != null && f251Resp.getF251Result().getResultCondition() != null) {
            if (f251Resp.getF251Result().getResultCondition().getReasonCode() != null && F251ErrorCodes.isErrorForF251(f251Resp.getF251Result().getResultCondition().getReasonCode())) {
                LOGGER.error("Exception occurred while calling F251, Reason Code:" + f251Resp.getF251Result().getResultCondition().getReasonCode());
                throw exceptionUtilityActivate.externalServiceError(requestHeader, f251Resp.getF251Result().getResultCondition().getReasonText(), String.valueOf(f251Resp.getF251Result().getResultCondition().getReasonCode()));
            }
        }
    }

    private F251Resp getF251Resp(RequestHeader requestHeader, F251Req f251Req, ContactPoint contactPoint) {
        F251Resp f251Resp;
        SecurityHeaderType securityHeaderType = headerRetriever.getSecurityHeader(requestHeader.getLloydsHeaders());
        ServiceRequest serviceRequest = headerRetriever.getServiceRequest(requestHeader.getLloydsHeaders(), F251_SERVICE_NAME, F251_ACTION_NAME);
        f251Resp = f251Client.addOMSOffer(f251Req, contactPoint, serviceRequest, securityHeaderType);
        return f251Resp;
    }

}

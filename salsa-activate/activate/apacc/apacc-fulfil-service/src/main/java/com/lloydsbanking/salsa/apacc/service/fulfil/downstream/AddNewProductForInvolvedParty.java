package com.lloydsbanking.salsa.apacc.service.fulfil.downstream;

import com.lloydsbanking.salsa.activate.helper.ApplicationDetails;
import com.lloydsbanking.salsa.activate.utility.ExceptionUtilityActivate;
import com.lloydsbanking.salsa.apacc.service.fulfil.convert.F259RequestFactory;
import com.lloydsbanking.salsa.downstream.ocis.client.f259.F259Client;
import com.lloydsbanking.salsa.header.gmo.HeaderRetriever;
import com.lloydsbanking.salsa.logging.application.ProductArrangementTraceLog;
import com.lloydsbanking.salsa.soap.ocis.f259.objects.F259Req;
import com.lloydsbanking.salsa.soap.ocis.f259.objects.F259Resp;
import com.lloydstsb.schema.infrastructure.soap.ContactPoint;
import com.lloydstsb.schema.infrastructure.soap.ResultCondition;
import com.lloydstsb.schema.infrastructure.soap.ServiceRequest;
import lib_sim_bo.businessobjects.Condition;
import lib_sim_bo.businessobjects.FinanceServiceArrangement;
import lib_sim_gmo.messages.RequestHeader;
import org.apache.log4j.Logger;
import org.oasis_open.docs.wss._2004._01.oasis_200401_wss_wssecurity_secext_1_0.SecurityHeaderType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.xml.ws.WebServiceException;

@Repository
public class AddNewProductForInvolvedParty {
    private static final Logger LOGGER = Logger.getLogger(AddNewProductForInvolvedParty.class);
    private static final String F259_SERVICE_NAME = "http://www.lloydstsb.com/Schema/Personal/RetailParty/OCIS/F259_AddProductNewPartner";
    private static final String F259_ACTION_NAME = "F259";
    private static final String TASK_CREATION_FAILED_REASON_CODE = "004";
    private static final String TASK_CREATION_FAILED_REASON_TEXT = "Failed to add card product on OCIS";

    @Autowired
    F259RequestFactory f259RequestFactory;
    @Autowired
    HeaderRetriever headerRetriever;
    @Autowired
    F259Client f259Client;
    @Autowired
    ExceptionUtilityActivate exceptionUtilityActivate;
    @Autowired
    ProductArrangementTraceLog productArrangementTraceLog;

    public void addNewProduct(RequestHeader requestHeader, FinanceServiceArrangement financeServiceArrangement, ApplicationDetails applicationDetails) {
        LOGGER.info(productArrangementTraceLog.getApplicationTraceEventMessage(financeServiceArrangement, "Entering AddNewProduct (OCIS F259)"));
        F259Req f259Req = f259RequestFactory.convert(financeServiceArrangement, requestHeader.getChannelId());
        F259Resp f259Resp = invokeF259(f259Req, requestHeader, applicationDetails);
        isErrorScenario(f259Resp, applicationDetails);
        financeServiceArrangement.setApplicationSubStatus(applicationDetails.getApplicationSubStatus());
        LOGGER.info("Exiting AddNewProductResponse (OCIS F259) with prodId: " + ((f259Resp != null && f259Resp.getAuditData() != null) ? f259Resp.getAuditData().getProdHeldId() : null));
    }

    private F259Resp invokeF259(F259Req f259Req, RequestHeader requestHeader, ApplicationDetails applicationDetails) {
        F259Resp f259Resp = null;
        ContactPoint contactPoint = headerRetriever.getContactPoint(requestHeader.getLloydsHeaders());
        SecurityHeaderType securityHeaderType = headerRetriever.getSecurityHeader(requestHeader.getLloydsHeaders());
        ServiceRequest serviceRequest = headerRetriever.getServiceRequest(requestHeader.getLloydsHeaders(), F259_SERVICE_NAME, F259_ACTION_NAME);
        try {
            f259Resp = f259Client.addNewCustomer(f259Req, contactPoint, serviceRequest, securityHeaderType);
        } catch (WebServiceException e) {
            LOGGER.info("Error while calling F259 for add new product: " + e);
            setApplicationDetails(applicationDetails);
        }
        return f259Resp;
    }

    private void isErrorScenario(F259Resp f259Resp, ApplicationDetails applicationDetails) {
        if (f259Resp != null && f259Resp.getF259Result() != null && f259Resp.getF259Result().getResultCondition() != null) {
            ResultCondition resultCondition = f259Resp.getF259Result().getResultCondition();
            if (resultCondition.getSeverityCode() != 0 || (resultCondition.getReasonCode() != null && resultCondition.getReasonCode() != 0)) {
                LOGGER.info("F259 responded with non zero severity code. Returning ExternalServiceError. ReasonCode | ReasonText " + resultCondition.getReasonCode() + " | " + resultCondition.getReasonText());
                setApplicationDetails(applicationDetails);
            }
        }
    }

    private void setApplicationDetails(ApplicationDetails applicationDetails) {
        Condition condition = new Condition();
        condition.setReasonCode(TASK_CREATION_FAILED_REASON_CODE);
        condition.setReasonText(TASK_CREATION_FAILED_REASON_TEXT);
        applicationDetails.getConditionList().add(condition);
    }
}
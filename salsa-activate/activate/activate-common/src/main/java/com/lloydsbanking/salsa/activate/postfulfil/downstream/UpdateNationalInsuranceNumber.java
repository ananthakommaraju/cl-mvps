package com.lloydsbanking.salsa.activate.postfulfil.downstream;

import com.lloydsbanking.salsa.activate.constants.ActivateCommonConstant;
import com.lloydsbanking.salsa.activate.helper.ApplicationDetails;
import com.lloydsbanking.salsa.activate.helper.UpdateDepositArrangementConditionAndApplicationStatusHelper;
import com.lloydsbanking.salsa.activate.postfulfil.convert.C234RequestFactory;
import com.lloydsbanking.salsa.constant.ApplicationStatus;
import com.lloydsbanking.salsa.downstream.ocis.client.c234.C234Client;
import com.lloydsbanking.salsa.header.gmo.HeaderRetriever;
import com.lloydsbanking.salsa.soap.ocis.c234.objects.C234Req;
import com.lloydsbanking.salsa.soap.ocis.c234.objects.C234Resp;
import com.lloydsbanking.salsa.soap.ocis.c234.objects.C234Result;
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
public class UpdateNationalInsuranceNumber {
    private static final Logger LOGGER = Logger.getLogger(UpdateNationalInsuranceNumber.class);

    private static final String C234_SERVICE_REQUEST_ACTION = "C234_ChaPersPty";
    private static final String C234_SERVICE_REQUEST_SERVICE_NAME = "http://www.lloydstsb.com/Schema/Personal/RetailParty/OCIS";

    @Autowired
    C234RequestFactory c234RequestFactory;

    @Autowired
    C234Client c234Client;

    @Autowired
    HeaderRetriever headerRetriever;

    @Autowired
    UpdateDepositArrangementConditionAndApplicationStatusHelper updateAppDetails;

    public void updateNationalInsNumber(ProductArrangement productArrangement, RequestHeader requestHeader, ApplicationDetails applicationDetails) {
        C234Req c234Req = c234RequestFactory.convert(productArrangement.getPrimaryInvolvedParty());
        LOGGER.info("Entering UpdateNationalInsuranceNumber (OCIS C234) NI Num | PartyId: " + c234Req.getDetailedPartyInfo().getNationalInsNo() + " | " + c234Req.getDetailedPartyInfo().getPartyId());
        C234Resp c234Resp;
        try {
            c234Resp = invokeC234(c234Req, requestHeader);
            if (checkErrorScenarios(c234Resp.getC234Result())) {
                updateAppDetails.setApplicationDetails(productArrangement.getRetryCount(), null, null, ApplicationStatus.AWAITING_FULFILMENT.getValue(), ActivateCommonConstant.AppSubStatus.FAILED_TO_UPDATE_NI_NUMBER, applicationDetails);
            }
        } catch (WebServiceException e) {
            LOGGER.info("Exception while calling C234 for updateNationalInsuranceNumber " + e);
            updateAppDetails.setApplicationDetails(productArrangement.getRetryCount(), null, null, ApplicationStatus.AWAITING_FULFILMENT.getValue(), ActivateCommonConstant.AppSubStatus.FAILED_TO_UPDATE_NI_NUMBER, applicationDetails);
        }
        productArrangement.setApplicationSubStatus(applicationDetails.getApplicationSubStatus());
    }

    private C234Resp invokeC234(C234Req c234Req, RequestHeader requestHeader) {
        ContactPoint contactPoint = headerRetriever.getContactPoint(requestHeader.getLloydsHeaders());
        SecurityHeaderType securityHeaderType = headerRetriever.getSecurityHeader(requestHeader.getLloydsHeaders());
        ServiceRequest serviceRequest = headerRetriever.getServiceRequest(requestHeader.getLloydsHeaders(), C234_SERVICE_REQUEST_SERVICE_NAME, C234_SERVICE_REQUEST_ACTION);
        return c234Client.c234(c234Req, contactPoint, serviceRequest, securityHeaderType);
    }

    private boolean checkErrorScenarios(C234Result c234Result) {
        boolean isErrorScenario = false;
        if (null != c234Result && null != c234Result.getResultCondition()) {
            if ((null != c234Result.getResultCondition().getReasonCode() && c234Result.getResultCondition().getReasonCode() != 0) || c234Result.getResultCondition().getSeverityCode() != (byte) 0) {
                isErrorScenario = true;
                LOGGER.info("C234 Error Details. ErrorCode | ErrorReason: " + c234Result.getResultCondition().getReasonCode() + " |" + c234Result.getResultCondition().getReasonText() + " | " + c234Result.getResultCondition().getSeverityCode());
            }
        }
        return isErrorScenario;
    }

}

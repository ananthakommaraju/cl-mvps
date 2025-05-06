package com.lloydsbanking.salsa.ppae.service.downstream;


import com.lloydsbanking.salsa.downstream.pad.client.f263.F263Client;
import com.lloydsbanking.salsa.header.gmo.HeaderRetriever;
import com.lloydsbanking.salsa.ppae.service.constant.PPAEServiceConstant;
import com.lloydsbanking.salsa.ppae.service.convert.F263RequestFactory;
import com.lloydsbanking.salsa.soap.pad.f263.objects.F263Resp;
import com.lloydstsb.schema.infrastructure.soap.ContactPoint;
import com.lloydstsb.schema.infrastructure.soap.ServiceRequest;
import lib_sim_bo.businessobjects.ProductArrangement;
import lib_sim_salesprocessmanagement.messages.ProcessPendingArrangementEventRequest;
import org.apache.log4j.Logger;
import org.oasis_open.docs.wss._2004._01.oasis_200401_wss_wssecurity_secext_1_0.SecurityHeaderType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.xml.ws.WebServiceException;

@Component
public class F263LoanDetailsRetriever {

    @Autowired
    F263Client f263Client;

    @Autowired
    HeaderRetriever headerRetriever;

    @Autowired
    F263RequestFactory f263RequestFactory;

    private static final int END_POSITION_FOR_DAY_START_FOR_MONTH = 2;
    private static final int END_POSITION_FOR_MONTH_START_FOR_YEAR = 4;
    private static final int END_POSITION_FOR_YEAR = 8;
    private static final int START_POSITION_FOR_DAY = 0;
    private static final String F263_SERVICE_NAME = "http://www.lloydstsb.com/Schema/Personal/LendingServicePlatform/CLP";
    private static final String F263_ACTION_NAME = "F263";
    private static final String ID_UNAUTH = "UNAUTHSALE";

    private static final Logger LOGGER = Logger.getLogger(F263LoanDetailsRetriever.class);

    public F263Resp invokeF263(ProcessPendingArrangementEventRequest upStreamRequest, ProductArrangement productArrangement) {
        ContactPoint contactPoint = headerRetriever.getContactPoint(upStreamRequest.getHeader().getLloydsHeaders());
        SecurityHeaderType securityHeaderType = headerRetriever.getSecurityHeader(upStreamRequest.getHeader().getLloydsHeaders());
        ServiceRequest serviceRequest = headerRetriever.getServiceRequest(upStreamRequest.getHeader().getLloydsHeaders(), F263_SERVICE_NAME, F263_ACTION_NAME);
        securityHeaderType.getUsernameToken().setUsername(ID_UNAUTH);
        F263Resp f263Resp = null;
        try {
            LOGGER.info("Entering retrieveLoanDetails F263 Client");
            f263Resp = f263Client.enquireLoanApplication(f263RequestFactory.createF263Req(productArrangement.getPrimaryInvolvedParty()), contactPoint, serviceRequest, securityHeaderType);
        } catch (WebServiceException e) {
            LOGGER.info("Error while invoking F263 - Retrieve loan Details : " + e);
        }
        if (f263Resp != null && isValidF263Resp(f263Resp)) {
            setCustomerIdAndCommunicationOption(f263Resp, productArrangement, upStreamRequest);
        }
        LOGGER.info("Exiting retrieveLoanDetails F263");
        return f263Resp;
    }

    private boolean isValidF263Resp(F263Resp f263Resp) {
        boolean isValidF263Resp = true;
        if (f263Resp.getF263Result() != null && f263Resp.getF263Result().getResultCondition() != null && f263Resp.getF263Result().getResultCondition().getReasonCode() != null) {
            LOGGER.info("External Business Error while invoking F263 - Retrieve loan Details : Error Code " + f263Resp.getF263Result().getResultCondition().getReasonCode());
            isValidF263Resp = false;
        }
        return isValidF263Resp;
    }

    private void setCustomerIdAndCommunicationOption(F263Resp f263Resp, ProductArrangement productArrangement, ProcessPendingArrangementEventRequest upStreamRequest) {

        String date = null;
        if (f263Resp.getApplicationDetails() != null) {

            if (f263Resp.getApplicationDetails().getLoanApplnStatusCd().equals(Integer.valueOf(PPAEServiceConstant.PAD_STATUS_CCA_SIGNED)) && f263Resp.getApplicationDetails().getLastCCAValidDt() != null) {
                date = getLoanAppStatus(f263Resp.getApplicationDetails().getLastCCAValidDt());
            } else if (f263Resp.getApplicationDetails().getLastQteValidDt() != null) {
                date = getLoanAppStatus(f263Resp.getApplicationDetails().getLastQteValidDt());
            }
        }
        productArrangement.setCommunicationOption(date);

    }

    private String getLoanAppStatus(String lastValidDate) {
        String day = lastValidDate.substring(START_POSITION_FOR_DAY, END_POSITION_FOR_DAY_START_FOR_MONTH);
        String month = lastValidDate.substring(END_POSITION_FOR_DAY_START_FOR_MONTH, END_POSITION_FOR_MONTH_START_FOR_YEAR);
        String year = lastValidDate.substring(END_POSITION_FOR_MONTH_START_FOR_YEAR, END_POSITION_FOR_YEAR);
        return day.concat("/").concat(month).concat("/").concat(year);
    }
}
